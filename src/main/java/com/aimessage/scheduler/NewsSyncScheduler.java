package com.aimessage.scheduler;

import com.aimessage.service.MultiSourceSyncService;
import com.aimessage.service.DailyReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NewsSyncScheduler {

    private static final Logger log = LoggerFactory.getLogger(NewsSyncScheduler.class);

    private final MultiSourceSyncService multiSourceSyncService;
    private final DailyReportService dailyReportService;

    public NewsSyncScheduler(MultiSourceSyncService multiSourceSyncService, 
                              DailyReportService dailyReportService) {
        this.multiSourceSyncService = multiSourceSyncService;
        this.dailyReportService = dailyReportService;
    }

    /**
     * 每天早上8点执行完整同步
     */
    @Scheduled(cron = "0 0 8 * * ?")
    public void scheduledMorningSync() {
        log.info("Starting scheduled morning sync at 8:00 AM");
        try {
            multiSourceSyncService.syncAllSources();
            // 同步完成后生成日报
            dailyReportService.generateDailyReport();
            log.info("Morning sync completed successfully");
        } catch (Exception e) {
            log.error("Error during morning sync", e);
        }
    }

    /**
     * 每6小时执行一次增量同步
     */
    @Scheduled(cron = "0 0 */6 * * ?")
    public void frequentSync() {
        log.info("Starting frequent sync every 6 hours");
        try {
            multiSourceSyncService.syncAllSources();
            log.info("Frequent sync completed successfully");
        } catch (Exception e) {
            log.error("Error during frequent sync", e);
        }
    }

    /**
     * 每天凌晨2点生成日报
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void generateDailyReport() {
        log.info("Generating daily report at 2:00 AM");
        try {
            dailyReportService.generateDailyReport();
            log.info("Daily report generated successfully");
        } catch (Exception e) {
            log.error("Error generating daily report", e);
        }
    }
}
