package com.aimessage.service;

import com.aimessage.entity.News;
import com.aimessage.entity.NewsSource;
import com.aimessage.entity.Category;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class RssFeedService {
    
    private static final Logger log = LoggerFactory.getLogger(RssFeedService.class);
    
    private final WebClient webClient;
    private final NewsClassifierService classifierService;
    
    public RssFeedService(WebClient webClient, NewsClassifierService classifierService) {
        this.webClient = webClient;
        this.classifierService = classifierService;
    }
    
    public List<News> fetchFromSource(NewsSource source, List<Category> categories) {
        List<News> newsList = new ArrayList<>();
        
        try {
            log.info("Fetching RSS from: {}", source.getName());
            
            String xmlContent = webClient.get()
                    .uri(source.getUrl())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            
            if (xmlContent == null || xmlContent.isEmpty()) {
                log.warn("Empty response from: {}", source.getName());
                return newsList;
            }
            
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(
                    new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8))));
            
            for (SyndEntry entry : feed.getEntries()) {
                News news = parseEntry(entry, source, categories);
                if (news != null) {
                    newsList.add(news);
                }
            }
            
            log.info("Fetched {} news from {}", newsList.size(), source.getName());
            
        } catch (Exception e) {
            log.error("Error fetching RSS from {}: {}", source.getName(), e.getMessage());
        }
        
        return newsList;
    }
    
    private News parseEntry(SyndEntry entry, NewsSource source, List<Category> categories) {
        try {
            String title = entry.getTitle();
            if (title == null || title.isEmpty()) {
                return null;
            }
            
            // 清理HTML标签
            title = Jsoup.parse(title).text();
            
            String content = "";
            if (entry.getDescription() != null) {
                content = Jsoup.parse(entry.getDescription().getValue()).text();
            }
            
            String url = entry.getLink();
            if (url == null || url.isEmpty()) {
                return null;
            }
            
            LocalDateTime publishDate = entry.getPublishedDate() != null 
                    ? entry.getPublishedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                    : LocalDateTime.now();
            
            // 分类和评分
            Category category = classifierService.classify(title, content, categories);
            int importance = classifierService.scoreImportance(title, content, source.getName());
            
            News news = new News();
            news.setTitle(title);
            news.setContent(content.length() > 500 ? content.substring(0, 500) + "..." : content);
            news.setSource(source.getName());
            news.setUrl(url);
            news.setCategory(category);
            news.setImportance(importance);
            news.setPublishDate(publishDate);
            news.setSyncDate(LocalDateTime.now());
            
            return news;
            
        } catch (Exception e) {
            log.error("Error parsing entry: {}", e.getMessage());
            return null;
        }
    }
}
