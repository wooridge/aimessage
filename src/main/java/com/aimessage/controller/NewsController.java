package com.aimessage.controller;

import com.aimessage.dto.DailyReportDTO;
import com.aimessage.dto.NewsDTO;
import com.aimessage.entity.SyncLog;
import com.aimessage.service.NewsService;
import com.aimessage.service.SyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class NewsController {

    private final NewsService newsService;
    private final SyncService syncService;

    public NewsController(NewsService newsService, SyncService syncService) {
        this.newsService = newsService;
        this.syncService = syncService;
    }

    @GetMapping("/")
    public String index(Model model) {
        DailyReportDTO report = newsService.getDailyReport();
        model.addAttribute("report", report);
        return "index";
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

    @PostMapping("/api/sync")
    @ResponseBody
    public ResponseEntity<SyncLog> triggerSync() {
        return ResponseEntity.ok(syncService.syncNews());
    }
}
