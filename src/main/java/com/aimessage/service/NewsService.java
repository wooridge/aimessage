package com.aimessage.service;

import com.aimessage.dto.DailyReportDTO;
import com.aimessage.dto.NewsDTO;
import com.aimessage.entity.News;
import com.aimessage.entity.Category;
import com.aimessage.repository.NewsRepository;
import com.aimessage.repository.CategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NewsService {

    private final NewsRepository newsRepository;
    private final CategoryRepository categoryRepository;

    public NewsService(NewsRepository newsRepository, CategoryRepository categoryRepository) {
        this.newsRepository = newsRepository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public DailyReportDTO getDailyReport() {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();

        List<News> todayNews = newsRepository.findTodayNews(startOfDay);

        if (todayNews.isEmpty()) {
            todayNews = newsRepository.findAll();
        }

        List<NewsDTO> highlights = todayNews.stream()
                .filter(n -> n.getImportance() != null && n.getImportance() >= 8)
                .limit(10)
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Map<String, List<NewsDTO>> categorizedNews = new HashMap<>();
        for (NewsDTO news : todayNews.stream().map(this::convertToDTO).collect(Collectors.toList())) {
            String categoryName = news.getCategoryDisplayName();
            if (categoryName == null || categoryName.isEmpty()) {
                categoryName = "其他";
            }
            categorizedNews.computeIfAbsent(categoryName, k -> new ArrayList<>()).add(news);
        }

        DailyReportDTO report = new DailyReportDTO();
        report.setDate(today);
        report.setTitle("AI Daily · " + today);
        report.setHighlights(highlights);
        report.setCategorizedNews(categorizedNews);
        report.setTotalNewsCount(todayNews.size());
        report.setLastSyncTime(getLastSyncTime());
        return report;
    }

    @Transactional(readOnly = true)
    public List<NewsDTO> getNewsByCategory(String categoryName) {
        Category category = categoryRepository.findByName(categoryName)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        return newsRepository.findByCategoryIdOrderByImportanceDesc(category.getId())
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public News saveNews(News news) {
        if (!newsRepository.existsByUrl(news.getUrl())) {
            return newsRepository.save(news);
        }
        return null;
    }

    private NewsDTO convertToDTO(News news) {
        NewsDTO dto = new NewsDTO();
        dto.setId(news.getId());
        dto.setTitle(news.getTitle());
        dto.setContent(news.getContent());
        dto.setSource(news.getSource());
        dto.setUrl(news.getUrl());
        dto.setCategoryName(news.getCategory() != null ? news.getCategory().getName() : "");
        dto.setCategoryDisplayName(news.getCategory() != null ? news.getCategory().getDisplayName() : "");
        dto.setImportance(news.getImportance());
        dto.setPublishDate(news.getPublishDate());
        dto.setSyncDate(news.getSyncDate());
        return dto;
    }

    private String getLastSyncTime() {
        List<News> recentNews = newsRepository.findAll();
        if (!recentNews.isEmpty()) {
            News latest = recentNews.get(recentNews.size() - 1);
            if (latest.getSyncDate() != null) {
                return latest.getSyncDate().toString();
            }
        }
        return "未同步";
    }
}
