package com.aimessage.service;

import com.aimessage.entity.News;
import com.aimessage.entity.NewsSource;
import com.aimessage.entity.Category;
import com.aimessage.entity.SyncLog;
import com.aimessage.repository.NewsRepository;
import com.aimessage.repository.NewsSourceRepository;
import com.aimessage.repository.CategoryRepository;
import com.aimessage.repository.SyncLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MultiSourceSyncService {
    
    private static final Logger log = LoggerFactory.getLogger(MultiSourceSyncService.class);
    
    private final NewsRepository newsRepository;
    private final NewsSourceRepository newsSourceRepository;
    private final CategoryRepository categoryRepository;
    private final SyncLogRepository syncLogRepository;
    private final RssFeedService rssFeedService;
    private final GitHubSyncService gitHubSyncService;
    
    public MultiSourceSyncService(NewsRepository newsRepository, 
                                   NewsSourceRepository newsSourceRepository,
                                   CategoryRepository categoryRepository,
                                   SyncLogRepository syncLogRepository,
                                   RssFeedService rssFeedService,
                                   GitHubSyncService gitHubSyncService) {
        this.newsRepository = newsRepository;
        this.newsSourceRepository = newsSourceRepository;
        this.categoryRepository = categoryRepository;
        this.syncLogRepository = syncLogRepository;
        this.rssFeedService = rssFeedService;
        this.gitHubSyncService = gitHubSyncService;
    }
    
    @Transactional
    public SyncLog syncAllSources() {
        log.info("Starting multi-source news sync at {}", LocalDateTime.now());
        
        SyncLog syncLog = new SyncLog();
        syncLog.setSyncTime(LocalDateTime.now());
        syncLog.setStatus("RUNNING");
        syncLog.setNewsCount(0);
        syncLog = syncLogRepository.save(syncLog);
        
        int totalSaved = 0;
        int sourceCount = 0;
        
        try {
            // 获取所有激活的信源
            List<NewsSource> sources = newsSourceRepository.findByIsActiveTrue();
            List<Category> categories = categoryRepository.findAll();
            
            if (categories.isEmpty()) {
                initializeDefaultCategories();
                categories = categoryRepository.findAll();
            }
            
            for (NewsSource source : sources) {
                try {
                    int saved = syncFromSource(source, categories);
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
            
            // 同步GitHub热门项目
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
                    totalSaved, sourceCount));
            
        } catch (Exception e) {
            log.error("Error during multi-source sync", e);
            syncLog.setStatus("FAILED");
            syncLog.setMessage(e.getMessage());
        }
        
        return syncLogRepository.save(syncLog);
    }
    
    private int syncFromSource(NewsSource source, List<Category> categories) {
        switch (source.getSourceType()) {
            case RSS:
                return syncRssSource(source, categories);
            case GITHUB:
                // GitHub单独处理
                return 0;
            default:
                log.warn("Unsupported source type: {}", source.getSourceType());
                return 0;
        }
    }
    
    private int syncRssSource(NewsSource source, List<Category> categories) {
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
