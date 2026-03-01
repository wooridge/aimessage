package com.aimessage.service;

import com.aimessage.entity.GitHubProject;
import com.aimessage.repository.GitHubProjectRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class GitHubSyncService {

    private static final Logger log = LoggerFactory.getLogger(GitHubSyncService.class);

    private final GitHubProjectRepository gitHubProjectRepository;
    private final WebClient webClient;
    private final ObjectMapper objectMapper;
    private final ProjectDescriptionService descriptionService;

    public GitHubSyncService(GitHubProjectRepository gitHubProjectRepository, 
                            WebClient webClient,
                            ProjectDescriptionService descriptionService) {
        this.gitHubProjectRepository = gitHubProjectRepository;
        this.webClient = webClient;
        this.objectMapper = new ObjectMapper();
        this.descriptionService = descriptionService;
    }

    @Transactional
    public int syncTrendingRepositories() {
        log.info("Starting GitHub trending sync at {}", LocalDateTime.now());
        int totalSaved = 0;

        try {
            // 同步不同语言的热门项目
            String[] languages = {"", "python", "java", "javascript", "typescript", "go", "rust"};
            
            for (String language : languages) {
                try {
                    List<GitHubProject> projects = fetchTrendingRepositories(language);
                    for (GitHubProject project : projects) {
                        if (!gitHubProjectRepository.existsByUrl(project.getUrl())) {
                            gitHubProjectRepository.save(project);
                            totalSaved++;
                            log.info("Saved GitHub project: {} ({} stars)", project.getName(), project.getStars());
                        }
                    }
                    // 添加延迟避免触发GitHub API限制
                    Thread.sleep(1000);
                } catch (Exception e) {
                    log.error("Error syncing language {}: {}", language, e.getMessage());
                }
            }
            
            log.info("GitHub sync completed. Total saved: {}", totalSaved);
        } catch (Exception e) {
            log.error("Error during GitHub sync", e);
        }

        return totalSaved;
    }

    private List<GitHubProject> fetchTrendingRepositories(String language) {
        List<GitHubProject> projects = new ArrayList<>();
        
        try {
            // 使用GitHub Search API获取热门项目
            String query = language.isEmpty() ? "stars:>1000" : "language:" + language + " stars:>1000";
            String sort = "stars";
            String order = "desc";
            
            String response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("api.github.com")
                            .path("/search/repositories")
                            .queryParam("q", query)
                            .queryParam("sort", sort)
                            .queryParam("order", order)
                            .queryParam("per_page", 10)
                            .build())
                    .header("Accept", "application/vnd.github.v3+json")
                    .header("User-Agent", "AIMessage-App")
                    .retrieve()
                    .onStatus(
                            status -> status.isError(),
                            clientResponse -> clientResponse.bodyToMono(String.class)
                                    .flatMap(errorBody -> Mono.error(new RuntimeException("GitHub API Error: " + errorBody)))
                    )
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(30))
                    .block();

            if (response != null) {
                projects = parseGitHubResponse(response, language);
            }
        } catch (Exception e) {
            log.error("Error fetching GitHub repositories for language {}: {}", language, e.getMessage());
        }
        
        return projects;
    }

    private List<GitHubProject> parseGitHubResponse(String json, String language) {
        List<GitHubProject> projects = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode items = root.get("items");

            if (items != null && items.isArray()) {
                for (JsonNode item : items) {
                    try {
                        GitHubProject project = new GitHubProject();
                        project.setName(item.get("name").asText());
                        project.setOwner(item.get("owner").get("login").asText());
                        
                        JsonNode descNode = item.get("description");
                        String description = descNode != null && !descNode.isNull() ? descNode.asText() : "";
                        project.setDescription(description);
                        
                        // 生成中文描述
                        String descriptionZh = descriptionService.generateChineseDescription(
                            project.getName(), description, project.getLanguage());
                        project.setDescriptionZh(descriptionZh);
                        
                        project.setUrl(item.get("html_url").asText());
                        project.setStars(item.get("stargazers_count").asInt());
                        project.setForks(item.get("forks_count").asInt());
                        
                        JsonNode langNode = item.get("language");
                        project.setLanguage(langNode != null && !langNode.isNull() ? langNode.asText() : language);
                        
                        project.setTrendingDate(now);
                        project.setSyncDate(now);

                        projects.add(project);
                    } catch (Exception e) {
                        log.warn("Error parsing GitHub project: {}", e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error parsing GitHub response: {}", e.getMessage());
        }

        return projects;
    }

    @Transactional(readOnly = true)
    public List<GitHubProject> getTrendingProjects() {
        return gitHubProjectRepository.findTop20ByOrderBySyncDateDesc();
    }

    @Transactional(readOnly = true)
    public List<GitHubProject> getProjectsByLanguage(String language) {
        return gitHubProjectRepository.findByLanguageOrderByStarsDesc(language);
    }
}
