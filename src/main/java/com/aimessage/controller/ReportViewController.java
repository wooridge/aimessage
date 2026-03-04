package com.aimessage.controller;

import com.aimessage.dto.DailyReportDTO;
import com.aimessage.entity.NewsSource;
import com.aimessage.service.DailyReportService;
import com.aimessage.repository.NewsSourceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class ReportViewController {

    private static final Logger log = LoggerFactory.getLogger(ReportViewController.class);

    private final DailyReportService dailyReportService;
    private final NewsSourceRepository newsSourceRepository;

    public ReportViewController(DailyReportService dailyReportService,
                                 NewsSourceRepository newsSourceRepository) {
        this.dailyReportService = dailyReportService;
        this.newsSourceRepository = newsSourceRepository;
    }

    /**
     * 日报页面
     */
    @GetMapping("/report")
    public String reportPage(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                             Model model) {
        log.info("Accessing report page for date: {}", date);
        
        DailyReportDTO report;
        if (date != null) {
            report = dailyReportService.generateReportByDate(date);
            if (report == null) {
                // 如果指定日期没有数据，返回今日报告
                report = dailyReportService.generateDailyReport();
            }
        } else {
            report = dailyReportService.generateDailyReport();
        }
        
        model.addAttribute("report", report);
        return "index";
    }

    /**
     * 信源管理页面
     */
    @GetMapping("/sources")
    public String sourcesPage(Model model) {
        log.info("Accessing sources page");
        
        List<NewsSource> sources = newsSourceRepository.findAll();
        model.addAttribute("sources", sources);
        
        return "sources";
    }

}
