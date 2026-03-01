package com.aimessage.scheduler;

import com.aimessage.service.SyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NewsSyncScheduler {

    private static final Logger log = LoggerFactory.getLogger(NewsSyncScheduler.class);

    private final SyncService syncService;

    public NewsSyncScheduler(SyncService syncService) {
        this.syncService = syncService;
    }

    @Scheduled(cron = "0 0 8 * * ?")
    public void scheduledSync() {
        log.info("Starting scheduled news sync at 8:00 AM");
        syncService.syncNews();
    }

    @Scheduled(cron = "0 0 */6 * * ?")
    public void frequentSync() {
        log.info("Starting frequent news sync every 6 hours");
        syncService.syncNews();
    }
}
