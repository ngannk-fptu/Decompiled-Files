/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
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
package com.atlassian.ratelimiting.internal.configuration;

import com.atlassian.ratelimiting.configuration.SystemPropertiesService;
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
import java.time.Instant;
import java.util.Date;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationLoggerJob
implements ScheduledJobSource {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationLoggerJob.class);
    private static final JobId JOB_ID = JobId.of((String)ConfigurationLoggerJob.class.getSimpleName());
    private static final JobRunnerKey JOB_RUNNER_KEY = JobRunnerKey.of((String)ConfigurationLoggerJob.class.getName());
    private final SystemPropertiesService systemPropertiesService;

    public ConfigurationLoggerJob(SystemPropertiesService systemPropertiesService) {
        this.systemPropertiesService = systemPropertiesService;
    }

    @Override
    public void schedule(@Nonnull SchedulerService schedulerService) throws SchedulerServiceException {
        schedulerService.registerJobRunner(this.getJobRunnerKey(), (JobRunner)new ConfigurationLoggerJobRunner());
        long intervalInMillis = Duration.ofHours(12L).toMillis();
        Date firstRunTime = Date.from(Instant.now());
        schedulerService.scheduleJob(this.getJobId(), JobConfig.forJobRunnerKey((JobRunnerKey)this.getJobRunnerKey()).withRunMode(RunMode.RUN_LOCALLY).withSchedule(Schedule.forInterval((long)intervalInMillis, (Date)firstRunTime)));
    }

    @Override
    public void unschedule(@Nonnull SchedulerService schedulerService) {
        schedulerService.unregisterJobRunner(this.getJobRunnerKey());
        schedulerService.unscheduleJob(this.getJobId());
    }

    @Override
    public JobId getJobId() {
        return JOB_ID;
    }

    public JobRunnerKey getJobRunnerKey() {
        return JOB_RUNNER_KEY;
    }

    private void logConfiguration() {
        logger.info("Periodic rate limiting configuration log. System rate limiting settings: [{}]", (Object)this.systemPropertiesService.getSystemSettings());
    }

    private class ConfigurationLoggerJobRunner
    implements JobRunner {
        private ConfigurationLoggerJobRunner() {
        }

        @Nullable
        public JobRunnerResponse runJob(@Nullable JobRunnerRequest request) {
            ConfigurationLoggerJob.this.logConfiguration();
            return JobRunnerResponse.success();
        }
    }
}

