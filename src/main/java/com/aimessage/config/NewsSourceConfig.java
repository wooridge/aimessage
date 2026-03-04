package com.aimessage.config;

import com.aimessage.entity.NewsSource;
import com.aimessage.repository.NewsSourceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class NewsSourceConfig {
    
    @Bean
    CommandLineRunner initNewsSources(NewsSourceRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                List<NewsSource> sources = List.of(
                    // ArXiv RSS Feeds
                    createSource("ArXiv AI", "http://export.arxiv.org/rss/cs.AI", NewsSource.SourceType.RSS, 10),
                    createSource("ArXiv CL", "http://export.arxiv.org/rss/cs.CL", NewsSource.SourceType.RSS, 10),
                    createSource("ArXiv LG", "http://export.arxiv.org/rss/cs.LG", NewsSource.SourceType.RSS, 10),
                    
                    // Tech Media RSS
                    createSource("TechCrunch AI", "https://techcrunch.com/category/artificial-intelligence/feed/", NewsSource.SourceType.RSS, 9),
                    createSource("The Verge AI", "https://www.theverge.com/ai-artificial-intelligence/rss/index.xml", NewsSource.SourceType.RSS, 9),
                    
                    // AI Company Blogs
                    createSource("OpenAI Blog", "https://openai.com/blog/rss.xml", NewsSource.SourceType.RSS, 10),
                    createSource("Google AI Blog", "https://blog.google/technology/ai/rss/", NewsSource.SourceType.RSS, 10),
                    createSource("DeepMind Blog", "https://deepmind.google/blog/rss/", NewsSource.SourceType.RSS, 10),
                    createSource("Anthropic Blog", "https://www.anthropic.com/blog/rss.xml", NewsSource.SourceType.RSS, 9),
                    
                    // Chinese Tech Media
                    createSource("量子位", "https://www.qbitai.com/feed", NewsSource.SourceType.RSS, 8),
                    createSource("InfoQ AI", "https://www.infoq.cn/feed/ai", NewsSource.SourceType.RSS, 8),
                    
                    // GitHub Trending (via API)
                    createSource("GitHub Trending", "https://api.github.com/search/repositories", NewsSource.SourceType.GITHUB, 7),
                    
                    // Hacker News
                    createSource("Hacker News", "https://news.ycombinator.com/rss", NewsSource.SourceType.RSS, 8)
                );
                
                repository.saveAll(sources);
                System.out.println("Initialized " + sources.size() + " news sources");
            }
        };
    }
    
    private NewsSource createSource(String name, String url, NewsSource.SourceType type, int priority) {
        NewsSource source = new NewsSource();
        source.setName(name);
        source.setUrl(url);
        source.setSourceType(type);
        source.setPriority(priority);
        source.setIsActive(true);
        return source;
    }
}
