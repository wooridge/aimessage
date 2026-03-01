package com.aimessage.controller;

import com.aimessage.dto.DailyReportDTO;
import com.aimessage.dto.GitHubProjectDTO;
import com.aimessage.dto.NewsDTO;
import com.aimessage.entity.GitHubProject;
import com.aimessage.entity.SyncLog;
import com.aimessage.service.GitHubSyncService;
import com.aimessage.service.NewsService;
import com.aimessage.service.SyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class NewsController {

    private final NewsService newsService;
    private final SyncService syncService;
    private final GitHubSyncService gitHubSyncService;

    public NewsController(NewsService newsService, SyncService syncService, GitHubSyncService gitHubSyncService) {
        this.newsService = newsService;
        this.syncService = syncService;
        this.gitHubSyncService = gitHubSyncService;
    }

    @GetMapping("/")
    public String index(Model model) {
        DailyReportDTO report = newsService.getDailyReport();
        model.addAttribute("report", report);
        return "index";
    }

    @GetMapping("/github")
    public String githubProjects(Model model) {
        List<GitHubProjectDTO> projects = gitHubSyncService.getTrendingProjects()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        model.addAttribute("projects", projects);
        return "github";
    }

    @GetMapping("/api/report")
    @ResponseBody
    public ResponseEntity<DailyReportDTO> getDailyReport() {
        return ResponseEntity.ok(newsService.getDailyReport());
    }

    @GetMapping("/api/news/category/{category}")
    @ResponseBody
    public ResponseEntity<List<NewsDTO>> getNewsByCategory(@PathVariable String category) {
        return ResponseEntity.ok(newsService.getNewsByCategory(category));
    }

    @GetMapping("/api/github/projects")
    @ResponseBody
    public ResponseEntity<List<GitHubProjectDTO>> getGitHubProjects() {
        List<GitHubProjectDTO> projects = gitHubSyncService.getTrendingProjects()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(projects);
    }

    @GetMapping("/api/github/projects/{language}")
    @ResponseBody
    public ResponseEntity<List<GitHubProjectDTO>> getGitHubProjectsByLanguage(@PathVariable String language) {
        List<GitHubProjectDTO> projects = gitHubSyncService.getProjectsByLanguage(language)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(projects);
    }

    @PostMapping("/api/sync")
    @ResponseBody
    public ResponseEntity<SyncLog> triggerSync() {
        return ResponseEntity.ok(syncService.syncNews());
    }

    @PostMapping("/api/sync/github")
    @ResponseBody
    public ResponseEntity<String> triggerGitHubSync() {
        int count = gitHubSyncService.syncTrendingRepositories();
        return ResponseEntity.ok("Synced " + count + " GitHub projects");
    }

    @PostMapping("/api/sync/github/generate-descriptions")
    @ResponseBody
    public ResponseEntity<String> generateChineseDescriptions() {
        int count = gitHubSyncService.generateChineseDescriptionsForExistingProjects();
        return ResponseEntity.ok("Generated Chinese descriptions for " + count + " projects");
    }

    private GitHubProjectDTO convertToDTO(GitHubProject project) {
        GitHubProjectDTO dto = new GitHubProjectDTO();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        dto.setDescriptionZh(project.getDescriptionZh());
        dto.setUrl(project.getUrl());
        dto.setStars(project.getStars());
        dto.setForks(project.getForks());
        dto.setLanguage(project.getLanguage());
        dto.setOwner(project.getOwner());
        dto.setTrendingDate(project.getTrendingDate());
        dto.setSyncDate(project.getSyncDate());
        return dto;
    }
}
