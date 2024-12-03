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
package com.atlassian.ratelimiting.internal.service;

import com.atlassian.ratelimiting.configuration.SystemPropertiesService;
import com.atlassian.ratelimiting.node.RateLimitService;
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

public class RateLimitReaperJob
implements ScheduledJobSource {
    private static final Logger logger = LoggerFactory.getLogger(RateLimitReaperJob.class);
    private static final JobId JOB_ID = JobId.of((String)RateLimitReaperJob.class.getSimpleName());
    private static final JobRunnerKey JOB_RUNNER_KEY = JobRunnerKey.of((String)RateLimitReaperJob.class.getName());
    private final RateLimitService rateLimitService;
    private final SystemPropertiesService systemPropertiesService;

    public RateLimitReaperJob(RateLimitService rateLimitService, SystemPropertiesService systemPropertiesService) {
        this.rateLimitService = rateLimitService;
        this.systemPropertiesService = systemPropertiesService;
    }

    @Override
    public void schedule(@Nonnull SchedulerService schedulerService) throws SchedulerServiceException {
        RateLimitReaperJobRunner jobRunner = new RateLimitReaperJobRunner();
        schedulerService.registerJobRunner(JOB_RUNNER_KEY, (JobRunner)jobRunner);
        Duration jobDuration = this.systemPropertiesService.getSystemSettings().getJobControlSettings().getBucketCleanupJobFrequencyDuration();
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

    private class RateLimitReaperJobRunner
    implements JobRunner {
        private RateLimitReaperJobRunner() {
        }

        @Nullable
        public JobRunnerResponse runJob(@Nonnull JobRunnerRequest request) {
            logger.debug("Running scheduled RateLimitReaperJob");
            RateLimitReaperJob.this.rateLimitService.reap();
            return JobRunnerResponse.success();
        }
    }
}

