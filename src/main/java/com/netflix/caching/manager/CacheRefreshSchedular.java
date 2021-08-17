package com.netflix.caching.manager;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Log4j2
public class CacheRefreshSchedular {

    /**
     * Default cache refresh interval in seconds(every 5 minutes).
     */
    private final int CACHE_REFRESH_TIME_INTERVAL_SECONDS = 300;

    private final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(10);
    @Autowired private ServiceCacheManager cacheManager;

    /**
     * This method schedules periodic tasks to update the internal cache from Origin Server.
     */
    @PostConstruct
    public void refreshCache() {
        cacheManager.setMembersCache();
        log.info("Scheduling Repos cache refresh background thread");
        executorService.scheduleWithFixedDelay(() -> {
            cacheManager.setRepositoryCache();
        }, 0, CACHE_REFRESH_TIME_INTERVAL_SECONDS, TimeUnit.SECONDS);

        log.info("Scheduling Members cache refresh background thread");
        executorService.scheduleWithFixedDelay(() -> {
            cacheManager.setMembersCache();
        }, 0, CACHE_REFRESH_TIME_INTERVAL_SECONDS, TimeUnit.SECONDS);

        log.info("Scheduling Netflix root cache refresh background thread");
        executorService.scheduleWithFixedDelay(() -> {
            cacheManager.setOrganizationDetailsCache();
        }, 0, CACHE_REFRESH_TIME_INTERVAL_SECONDS, TimeUnit.SECONDS);

        log.info("Scheduling Root path cache refresh background thread");
        executorService.scheduleWithFixedDelay(() -> {
            cacheManager.setServiceIndexPageDetails();
        }, 0, CACHE_REFRESH_TIME_INTERVAL_SECONDS, TimeUnit.SECONDS);
    }
}

