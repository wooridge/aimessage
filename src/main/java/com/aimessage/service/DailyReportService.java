package com.aimessage.service;

import com.aimessage.dto.DailyReportDTO;
import com.aimessage.dto.NewsDTO;
import com.aimessage.entity.Category;
import com.aimessage.entity.News;
import com.aimessage.repository.CategoryRepository;
import com.aimessage.repository.NewsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DailyReportService {

    private static final Logger log = LoggerFactory.getLogger(DailyReportService.class);

    private final NewsRepository newsRepository;
    private final CategoryRepository categoryRepository;
    private final TranslationService translationService;

    public DailyReportService(NewsRepository newsRepository, CategoryRepository categoryRepository, TranslationService translationService) {
        this.newsRepository = newsRepository;
        this.categoryRepository = categoryRepository;
        this.translationService = translationService;
    }
    
    @Transactional(readOnly = true)
    public DailyReportDTO generateDailyReport() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();
        
        log.info("Generating daily report for {}", today);
        
        // 获取今日新闻
        List<News> todayNews = newsRepository.findBySyncDateBetween(startOfDay, endOfDay);
        
        // 如果没有今日新闻，获取最近的新闻
        if (todayNews.isEmpty()) {
            todayNews = newsRepository.findTop50ByOrderBySyncDateDesc();
            log.info("No news for today, using recent {} news items", todayNews.size());
        }
        
        // 生成Highlights（重要性 >= 8的新闻）
        List<NewsDTO> highlights = todayNews.stream()
                .filter(n -> n.getImportance() != null && n.getImportance() >= 8)
                .sorted((a, b) -> b.getImportance().compareTo(a.getImportance()))
                .limit(10)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        // 按分类组织新闻
        Map<String, List<NewsDTO>> categorizedNews = new LinkedHashMap<>();
        
        // 获取所有分类并按排序顺序
        List<Category> categories = categoryRepository.findAllByOrderBySortOrderAsc();
        
        for (Category category : categories) {
            String categoryName = category.getDisplayName();
            List<NewsDTO> categoryNews = todayNews.stream()
                    .filter(n -> n.getCategory() != null && 
                            n.getCategory().getId().equals(category.getId()))
                    .sorted((a, b) -> b.getImportance().compareTo(a.getImportance()))
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            if (!categoryNews.isEmpty()) {
                categorizedNews.put(categoryName, categoryNews);
            }
        }
        
        // 添加"其他"分类
        List<NewsDTO> otherNews = todayNews.stream()
                .filter(n -> n.getCategory() == null)
                .sorted((a, b) -> b.getImportance().compareTo(a.getImportance()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        if (!otherNews.isEmpty()) {
            categorizedNews.put("其他", otherNews);
        }
        
        // 生成统计信息
        Map<String, Object> stats = generateStats(todayNews);
        
        DailyReportDTO report = new DailyReportDTO();
        report.setDate(today);
        report.setTitle("AI Daily · " + today);
        report.setHighlights(highlights);
        report.setCategorizedNews(categorizedNews);
        report.setTotalNewsCount(todayNews.size());
        report.setLastSyncTime(getLastSyncTime());
        report.setStats(stats);
        
        log.info("Generated report with {} highlights and {} categories", 
                highlights.size(), categorizedNews.size());
        
        return report;
    }
    
    @Transactional(readOnly = true)
    public DailyReportDTO generateReportByDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();
        
        List<News> dayNews = newsRepository.findBySyncDateBetween(startOfDay, endOfDay);
        
        if (dayNews.isEmpty()) {
            return null;
        }
        
        // 类似上面的逻辑生成报告
        List<NewsDTO> highlights = dayNews.stream()
                .filter(n -> n.getImportance() != null && n.getImportance() >= 8)
                .sorted((a, b) -> b.getImportance().compareTo(a.getImportance()))
                .limit(10)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        Map<String, List<NewsDTO>> categorizedNews = new LinkedHashMap<>();
        List<Category> categories = categoryRepository.findAllByOrderBySortOrderAsc();
        
        for (Category category : categories) {
            String categoryName = category.getDisplayName();
            List<NewsDTO> categoryNews = dayNews.stream()
                    .filter(n -> n.getCategory() != null && 
                            n.getCategory().getId().equals(category.getId()))
                    .sorted((a, b) -> b.getImportance().compareTo(a.getImportance()))
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            if (!categoryNews.isEmpty()) {
                categorizedNews.put(categoryName, categoryNews);
            }
        }
        
        DailyReportDTO report = new DailyReportDTO();
        report.setDate(date);
        report.setTitle("AI Daily · " + date);
        report.setHighlights(highlights);
        report.setCategorizedNews(categorizedNews);
        report.setTotalNewsCount(dayNews.size());
        report.setLastSyncTime(getLastSyncTime());
        
        return report;
    }
    
    private Map<String, Object> generateStats(List<News> newsList) {
        Map<String, Object> stats = new HashMap<>();
        
        // 信源统计
        Map<String, Long> sourceCount = newsList.stream()
                .collect(Collectors.groupingBy(News::getSource, Collectors.counting()));
        stats.put("sourceDistribution", sourceCount);
        
        // 分类统计
        Map<String, Long> categoryCount = newsList.stream()
                .filter(n -> n.getCategory() != null)
                .collect(Collectors.groupingBy(
                        n -> n.getCategory().getDisplayName(), 
                        Collectors.counting()));
        stats.put("categoryDistribution", categoryCount);
        
        // 重要性分布
        long highImportance = newsList.stream()
                .filter(n -> n.getImportance() != null && n.getImportance() >= 8)
                .count();
        long mediumImportance = newsList.stream()
                .filter(n -> n.getImportance() != null && n.getImportance() >= 5 && n.getImportance() < 8)
                .count();
        long lowImportance = newsList.stream()
                .filter(n -> n.getImportance() == null || n.getImportance() < 5)
                .count();
        
        stats.put("importanceDistribution", Map.of(
                "high", highImportance,
                "medium", mediumImportance,
                "low", lowImportance
        ));
        
        return stats;
    }
    
    private NewsDTO convertToDTO(News news) {
        NewsDTO dto = new NewsDTO();
        dto.setId(news.getId());

        // 翻译标题（如果是英文）
        String title = news.getTitle();
        if (title != null && !translationService.isChinese(title)) {
            title = translationService.translateToChinese(title);
        }
        dto.setTitle(title);

        // 翻译内容/摘要
        String content = news.getContent();
        if (content != null && !content.isEmpty() && !translationService.isChinese(content)) {
            content = translationService.translateToChinese(content);
        } else if (content == null || content.isEmpty()) {
            content = generateSummary(title);
        }
        dto.setContent(content);

        dto.setSource(news.getSource());
        dto.setUrl(news.getUrl());
        dto.setCategoryName(news.getCategory() != null ? news.getCategory().getName() : "");
        dto.setCategoryDisplayName(news.getCategory() != null ? news.getCategory().getDisplayName() : "其他");
        dto.setImportance(news.getImportance());
        dto.setPublishDate(news.getPublishDate());
        dto.setSyncDate(news.getSyncDate());

        return dto;
    }
    
    private String generateSummary(String title) {
        // 简单的摘要生成，实际可以使用AI生成
        if (title.length() > 100) {
            return title.substring(0, 100) + "...";
        }
        return title;
    }
    
    private String getLastSyncTime() {
        List<News> recentNews = newsRepository.findTop1ByOrderBySyncDateDesc();
        if (!recentNews.isEmpty() && recentNews.get(0).getSyncDate() != null) {
            return recentNews.get(0).getSyncDate().toString();
        }
        return "未同步";
    }
}
