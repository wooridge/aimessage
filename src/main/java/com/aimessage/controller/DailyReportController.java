package com.aimessage.controller;

import com.aimessage.dto.DailyReportDTO;
import com.aimessage.entity.NewsSource;
import com.aimessage.entity.SyncLog;
import com.aimessage.service.DailyReportService;
import com.aimessage.service.MultiSourceSyncService;
import com.aimessage.repository.NewsSourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/report")
@CrossOrigin(origins = "*")
public class DailyReportController {

    private static final Logger log = LoggerFactory.getLogger(DailyReportController.class);

    private final DailyReportService dailyReportService;
    private final MultiSourceSyncService multiSourceSyncService;
    private final NewsSourceRepository newsSourceRepository;

    public DailyReportController(DailyReportService dailyReportService,
                                  MultiSourceSyncService multiSourceSyncService,
                                  NewsSourceRepository newsSourceRepository) {
        this.dailyReportService = dailyReportService;
        this.multiSourceSyncService = multiSourceSyncService;
        this.newsSourceRepository = newsSourceRepository;
    }

    /**
     * 获取今日日报
     */
    @GetMapping("/today")
    public ResponseEntity<DailyReportDTO> getTodayReport() {
        log.info("Getting today's report");
        DailyReportDTO report = dailyReportService.generateDailyReport();
        return ResponseEntity.ok(report);
    }

    /**
     * 获取指定日期的日报
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<DailyReportDTO> getReportByDate(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("Getting report for date: {}", date);
        DailyReportDTO report = dailyReportService.generateReportByDate(date);
        if (report == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(report);
    }

    /**
     * 手动触发同步
     */
    @PostMapping("/sync")
    public ResponseEntity<Map<String, Object>> triggerSync() {
        log.info("Manual sync triggered");
        SyncLog syncLog = multiSourceSyncService.syncAllSources();
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", syncLog.getStatus());
        response.put("count", syncLog.getNewsCount());
        response.put("message", syncLog.getMessage());
        response.put("time", syncLog.getSyncTime());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取所有信源
     */
    @GetMapping("/sources")
    public ResponseEntity<List<NewsSource>> getAllSources() {
        log.info("Getting all news sources");
        List<NewsSource> sources = newsSourceRepository.findAll();
        return ResponseEntity.ok(sources);
    }

    /**
     * 获取系统状态
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSystemStatus() {
        log.info("Getting system status");
        
        List<NewsSource> sources = newsSourceRepository.findAll();
        long activeSources = sources.stream().filter(NewsSource::getIsActive).count();
        
        Map<String, Object> status = new HashMap<>();
        status.put("totalSources", sources.size());
        status.put("activeSources", activeSources);
        status.put("sources", sources.stream().map(s -> Map.of(
            "name", s.getName(),
            "type", s.getSourceType(),
            "active", s.getIsActive(),
            "lastSync", s.getLastSyncTime(),
            "syncCount", s.getSyncCount()
        )).toList());
        
        return ResponseEntity.ok(status);
    }
}
