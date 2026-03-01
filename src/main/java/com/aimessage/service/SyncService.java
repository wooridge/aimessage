package com.aimessage.service;

import com.aimessage.entity.News;
import com.aimessage.entity.Category;
import com.aimessage.entity.SyncLog;
import com.aimessage.repository.NewsRepository;
import com.aimessage.repository.CategoryRepository;
import com.aimessage.repository.SyncLogRepository;
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
    private final CategoryRepository categoryRepository;
    private final SyncLogRepository syncLogRepository;
    private final WebClient webClient;

    public SyncService(NewsRepository newsRepository, CategoryRepository categoryRepository,
                       SyncLogRepository syncLogRepository, WebClient webClient) {
        this.newsRepository = newsRepository;
        this.categoryRepository = categoryRepository;
        this.syncLogRepository = syncLogRepository;
        this.webClient = webClient;
    }

    @Transactional
    public SyncLog syncNews() {
        log.info("Starting news sync at {}", LocalDateTime.now());
        SyncLog syncLog = new SyncLog();
        syncLog.setSyncTime(LocalDateTime.now());
        syncLog.setStatus("RUNNING");
        syncLog.setNewsCount(0);
        syncLog = syncLogRepository.save(syncLog);

        int totalSaved = 0;

        try {
            totalSaved += syncFromInsightScope();
            syncLog.setStatus("SUCCESS");
            syncLog.setNewsCount(totalSaved);
            syncLog.setMessage("Successfully synced " + totalSaved + " news items");
        } catch (Exception e) {
            log.error("Error during news sync", e);
            syncLog.setStatus("FAILED");
            syncLog.setMessage(e.getMessage());
        }

        return syncLogRepository.save(syncLog);
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

    private int parseAndSaveNews(String content) {
        int count = 0;
        LocalDateTime now = LocalDateTime.now();

        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty()) {
            log.warn("No categories found, skipping sync");
            return 0;
        }
        
        Category defaultCategory = categories.get(0);

        String[] lines = content.split("\n");
        String currentCategory = "model";

        for (String line : lines) {
            line = line.trim();

            if (line.startsWith("### ")) {
                currentCategory = detectCategory(line);
            }

            if (line.startsWith("#### [") && line.contains("](")) {
                try {
                    String title = extractTitle(line);
                    String url = extractUrl(line);

                    if (title != null && url != null && !newsRepository.existsByUrl(url)) {
                        Category category = categoryRepository.findByName(currentCategory)
                                .orElse(defaultCategory);

                        News news = new News();
                        news.setTitle(title);
                        news.setContent("");
                        news.setSource("InsightScope");
                        news.setUrl(url);
                        news.setCategory(category);
                        news.setImportance(7);
                        news.setSyncDate(now);

                        newsRepository.save(news);
                        count++;
                        log.debug("Saved news: {}", title);
                    }
                } catch (Exception e) {
                    log.warn("Error parsing line: {}", line, e);
                }
            }
        }

        log.info("Parsed and saved {} news items", count);
        return count;
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

    private String extractTitle(String line) {
        int start = line.indexOf("[") + 1;
        int end = line.indexOf("]");
        if (start > 0 && end > start) {
            return line.substring(start, end);
        }
        return null;
    }

    private String extractUrl(String line) {
        int start = line.indexOf("](") + 2;
        int end = line.indexOf(")", start);
        if (start > 1 && end > start) {
            return line.substring(start, end);
        }
        return null;
    }
}
