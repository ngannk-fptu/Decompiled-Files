/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.ratelimiting.internal.settings;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.ratelimiting.configuration.SystemPropertiesService;
import com.atlassian.ratelimiting.dao.SystemRateLimitingSettingsProvider;
import com.atlassian.ratelimiting.dao.UserRateLimitingSettingsProvider;
import com.atlassian.ratelimiting.events.RateLimitingSettingsReloadedEvent;
import com.atlassian.ratelimiting.properties.RateLimitingProperties;
import com.atlassian.ratelimiting.scheduling.ScheduledJobSource;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Date;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SettingsReloaderJob
implements ScheduledJobSource {
    private static final Logger logger = LoggerFactory.getLogger(SettingsReloaderJob.class);
    private static final JobId JOB_ID = JobId.of((String)SettingsReloaderJob.class.getSimpleName());
    private static final JobRunnerKey JOB_RUNNER_KEY = JobRunnerKey.of((String)SettingsReloaderJob.class.getName());
    private final SystemPropertiesService systemPropertiesService;
    private final SystemRateLimitingSettingsProvider cachingSystemRateLimitingSettingsProvider;
    private final UserRateLimitingSettingsProvider cacheableUserRateLimitingSettingsDao;
    private final RateLimitingProperties rateLimitingProperties;
    private final EventPublisher eventPublisher;

    public SettingsReloaderJob(SystemPropertiesService systemPropertiesService, SystemRateLimitingSettingsProvider systemRateLimitingSettingsProvider, UserRateLimitingSettingsProvider userRateLimitingSettingsProvider, RateLimitingProperties rateLimitingProperties, EventPublisher eventPublisher) {
        this.systemPropertiesService = systemPropertiesService;
        this.cachingSystemRateLimitingSettingsProvider = systemRateLimitingSettingsProvider;
        this.cacheableUserRateLimitingSettingsDao = userRateLimitingSettingsProvider;
        this.rateLimitingProperties = rateLimitingProperties;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void schedule(@Nonnull SchedulerService schedulerService) throws SchedulerServiceException {
        SettingsReloaderJobRunner jobRunner = new SettingsReloaderJobRunner();
        schedulerService.registerJobRunner(JOB_RUNNER_KEY, (JobRunner)jobRunner);
        Duration jobDuration = this.systemPropertiesService.getSystemSettings().getJobControlSettings().getSettingsReloadJobFrequencyDuration();
        JobConfig jobConfig = JobConfig.forJobRunnerKey((JobRunnerKey)JOB_RUNNER_KEY).withRunMode(RunMode.RUN_LOCALLY).withSchedule(Schedule.forInterval((long)jobDuration.toMillis(), (Date)Date.from(ZonedDateTime.now().plus(jobDuration).toInstant())));
        schedulerService.scheduleJob(JOB_ID, jobConfig);
    }

    @Override
    public void unschedule(@Nonnull SchedulerService schedulerService) {
        schedulerService.unscheduleJob(JOB_ID);
        schedulerService.unregisterJobRunner(JOB_RUNNER_KEY);
    }

    @Override
    public JobId getJobId() {
        return JOB_ID;
    }

    public void tryReload() {
        if (this.cachingSystemRateLimitingSettingsProvider.tryReloadCache() | this.cacheableUserRateLimitingSettingsDao.tryReloadCache()) {
            logger.debug("Settings change detected, broadcasting reload event...");
            this.eventPublisher.publish((Object)new RateLimitingSettingsReloadedEvent());
        }
        this.rateLimitingProperties.reloadCache();
    }

    private class SettingsReloaderJobRunner
    implements JobRunner {
        private SettingsReloaderJobRunner() {
        }

        @Nullable
        public JobRunnerResponse runJob(@Nonnull JobRunnerRequest request) {
            try {
                SettingsReloaderJob.this.tryReload();
            }
            catch (Exception e) {
                logger.error("Error while refreshing rate limiting settings: ", (Throwable)e);
                return JobRunnerResponse.failed((Throwable)e);
            }
            return JobRunnerResponse.success();
        }
    }
}

