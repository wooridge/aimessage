package com.aimessage.service;

import com.aimessage.entity.News;
import com.aimessage.entity.Category;
import com.aimessage.entity.NewsSource;
import com.aimessage.entity.SyncLog;
import com.aimessage.repository.NewsRepository;
import com.aimessage.repository.NewsSourceRepository;
import com.aimessage.repository.CategoryRepository;
import com.aimessage.repository.SyncLogRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SyncService {

    private static final Logger log = LoggerFactory.getLogger(SyncService.class);

    private final NewsRepository newsRepository;
    private final NewsSourceRepository newsSourceRepository;
    private final CategoryRepository categoryRepository;
    private final SyncLogRepository syncLogRepository;
    private final GitHubSyncService gitHubSyncService;
    private final RssFeedService rssFeedService;
    private final WebClient webClient;

    public SyncService(NewsRepository newsRepository, 
                       NewsSourceRepository newsSourceRepository,
                       CategoryRepository categoryRepository,
                       SyncLogRepository syncLogRepository, 
                       GitHubSyncService gitHubSyncService,
                       RssFeedService rssFeedService,
                       WebClient webClient) {
        this.newsRepository = newsRepository;
        this.newsSourceRepository = newsSourceRepository;
        this.categoryRepository = categoryRepository;
        this.syncLogRepository = syncLogRepository;
        this.gitHubSyncService = gitHubSyncService;
        this.rssFeedService = rssFeedService;
        this.webClient = webClient;
    }

    @Transactional
    public SyncLog syncNews() {
        log.info("Starting multi-source news sync at {}", LocalDateTime.now());
        SyncLog syncLog = new SyncLog();
        syncLog.setSyncTime(LocalDateTime.now());
        syncLog.setStatus("RUNNING");
        syncLog.setNewsCount(0);
        syncLog = syncLogRepository.save(syncLog);

        int totalSaved = 0;
        int sourceCount = 0;

        try {
            // 初始化分类
            List<Category> categories = categoryRepository.findAll();
            if (categories.isEmpty()) {
                initializeDefaultCategories();
                categories = categoryRepository.findAll();
            }

            // 1. 从各个 RSS 信源抓取
            List<NewsSource> rssSources = newsSourceRepository.findByIsActiveTrue();
            for (NewsSource source : rssSources) {
                try {
                    int saved = syncFromRssSource(source, categories);
                    totalSaved += saved;
                    sourceCount++;
                    
                    // 更新信源统计
                    source.setLastSyncTime(LocalDateTime.now());
                    source.setSyncCount(source.getSyncCount() + saved);
                    newsSourceRepository.save(source);
                    
                    log.info("Synced {} news from {}", saved, source.getName());
                } catch (Exception e) {
                    log.error("Error syncing from {}: {}", source.getName(), e.getMessage());
                }
            }

            // 2. 从 InsightScope API 抓取（作为补充）
            try {
                int insightScopeSaved = syncFromInsightScope();
                totalSaved += insightScopeSaved;
                log.info("Synced {} news from InsightScope API", insightScopeSaved);
            } catch (Exception e) {
                log.error("Error syncing from InsightScope: {}", e.getMessage());
            }

            // 3. 同步 GitHub 热门项目
            try {
                int gitHubSaved = gitHubSyncService.syncTrendingRepositories();
                totalSaved += gitHubSaved;
                log.info("Synced {} GitHub projects", gitHubSaved);
            } catch (Exception e) {
                log.error("Error syncing GitHub: {}", e.getMessage());
            }

            syncLog.setStatus("SUCCESS");
            syncLog.setNewsCount(totalSaved);
            syncLog.setMessage(String.format("Successfully synced %d news from %d sources", 
                    totalSaved, sourceCount + 2)); // +2 for InsightScope and GitHub
        } catch (Exception e) {
            log.error("Error during news sync", e);
            syncLog.setStatus("FAILED");
            syncLog.setMessage(e.getMessage());
        }

        return syncLogRepository.save(syncLog);
    }

    private int syncFromRssSource(NewsSource source, List<Category> categories) {
        List<News> newsList = rssFeedService.fetchFromSource(source, categories);
        int savedCount = 0;

        for (News news : newsList) {
            if (!newsRepository.existsByUrl(news.getUrl())) {
                newsRepository.save(news);
                savedCount++;
            }
        }

        return savedCount;
    }

    private int syncFromInsightScope() {
        int savedCount = 0;
        try {
            log.info("Fetching data from InsightScope API...");

            String response = webClient.get()
                    .uri("https://api.insightscope.org/report")
                    .retrieve()
                    .onStatus(
                            status -> status.isError(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> Mono.error(new RuntimeException("API Error: " + errorBody)))
                    )
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(60))
                    .block();

            if (response != null && !response.isEmpty()) {
                log.info("Received response from API, length: {}", response.length());
                savedCount = parseAndSaveNews(response);
            } else {
                log.warn("Empty response from API");
            }
        } catch (WebClientResponseException e) {
            log.error("HTTP Error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Error fetching from InsightScope: {}", e.getMessage(), e);
        }
        return savedCount;
    }

    private int parseAndSaveNews(String html) {
        int count = 0;
        LocalDateTime now = LocalDateTime.now();

        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            log.warn("No categories found, skipping sync");
            return 0;
        }

        Category defaultCategory = categories.get(0);

        // 使用Jsoup解析HTML
        Document doc = Jsoup.parse(html);

        // 查找所有新闻项
        Elements newsItems = doc.select(".news-item, article, .item");

        if (newsItems.isEmpty()) {
            // 尝试其他选择器
            newsItems = doc.select("h4, .title");
        }

        log.info("Found {} news items in HTML", newsItems.size());

        String currentCategory = "model";

        // 遍历所有元素查找新闻
        for (Element element : doc.getAllElements()) {
            // 检测分类标题
            if (element.tagName().equals("h3") || element.hasClass("category")) {
                String catText = element.text();
                currentCategory = detectCategory(catText);
                log.debug("Detected category: {} from: {}", currentCategory, catText);
                continue;
            }

            // 检测新闻标题 (h4标签)
            if (element.tagName().equals("h4")) {
                String title = element.text().trim();
                if (title.isEmpty()) continue;

                // 查找重要性评分
                int importance = 7;
                Element parent = element.parent();
                if (parent != null) {
                    Elements importanceElems = parent.select(".importance, .score, .rating");
                    if (!importanceElems.isEmpty()) {
                        String impText = importanceElems.first().text();
                        try {
                            importance = Integer.parseInt(impText.split("/")[0].trim());
                        } catch (Exception e) {
                            importance = 7;
                        }
                    }
                }

                // 查找来源和链接
                String source = "InsightScope";
                String url = "";

                Element linkElem = element.selectFirst("a");
                if (linkElem != null) {
                    url = linkElem.attr("href");
                }

                // 如果没有找到链接，生成一个
                if (url.isEmpty()) {
                    url = generateUrl(title, now.toLocalDate().toString());
                }

                // 保存新闻
                if (!newsRepository.existsByUrl(url)) {
                    Category category = categoryRepository.findByName(currentCategory)
                            .orElse(defaultCategory);

                    News news = new News();
                    news.setTitle(title);
                    news.setContent("");
                    news.setSource(source);
                    news.setUrl(url);
                    news.setCategory(category);
                    news.setImportance(importance);
                    news.setSyncDate(now);

                    newsRepository.save(news);
                    count++;
                    log.info("Saved news [{}]: {}", currentCategory, title);
                }
            }
        }

        log.info("Parsed and saved {} news items from InsightScope", count);
        return count;
    }

    private String generateUrl(String title, String dateStr) {
        // 生成基于标题和日期的唯一URL
        String slug = title.replaceAll("[^\\w\\u4e00-\\u9fa5]", "-").substring(0, Math.min(title.length(), 50));
        return "https://insightscope.org/news/" + dateStr + "/" + System.currentTimeMillis();
    }

    private String detectCategory(String header) {
        String lower = header.toLowerCase();
        if (lower.contains("融资") || lower.contains("商业")) return "funding";
        if (lower.contains("安全") || lower.contains("政策") || lower.contains("监管")) return "policy";
        if (lower.contains("模型") || lower.contains("产品") || lower.contains("更新")) return "model";
        if (lower.contains("agent") || lower.contains("智能体")) return "agent";
        if (lower.contains("中国") || lower.contains("国产")) return "china";
        if (lower.contains("芯片") || lower.contains("算力")) return "chip";
        if (lower.contains("机器人") || lower.contains("具身")) return "robot";
        if (lower.contains("学术") || lower.contains("研究")) return "research";
        return "model";
    }

    private void initializeDefaultCategories() {
        log.info("Initializing default categories");

        List<Category> categories = List.of(
            createCategory("model", "模型发布", "🚀", 1, true),
            createCategory("research", "学术论文", "📄", 2, true),
            createCategory("agent", "AI Agent", "🤖", 3, true),
            createCategory("funding", "融资动态", "💰", 4, false),
            createCategory("product", "产品工具", "🛠️", 5, false),
            createCategory("policy", "安全治理", "🛡️", 6, false),
            createCategory("chip", "芯片硬件", "🔧", 7, false),
            createCategory("china", "国产AI", "🇨🇳", 8, false)
        );

        categoryRepository.saveAll(categories);
    }

    private Category createCategory(String name, String displayName, String icon, 
                                     int sortOrder, boolean isImportant) {
        Category category = new Category();
        category.setName(name);
        category.setDisplayName(displayName);
        category.setIcon(icon);
        category.setSortOrder(sortOrder);
        category.setIsImportant(isImportant);
        return category;
    }
}
