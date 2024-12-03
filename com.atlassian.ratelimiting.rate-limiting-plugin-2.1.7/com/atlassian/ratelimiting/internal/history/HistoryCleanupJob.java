/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
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
package com.atlassian.ratelimiting.internal.history;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.ratelimiting.configuration.SystemPropertiesService;
import com.atlassian.ratelimiting.dao.UserRateLimitCounterDao;
import com.atlassian.ratelimiting.dmz.SystemJobControlSettings;
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
import java.time.ZonedDateTime;
import java.util.Date;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HistoryCleanupJob
implements ScheduledJobSource {
    private static final Logger logger = LoggerFactory.getLogger(HistoryCleanupJob.class);
    private static final JobId JOB_ID = JobId.of((String)HistoryCleanupJob.class.getSimpleName());
    private static final JobRunnerKey JOB_RUNNER_KEY = JobRunnerKey.of((String)HistoryCleanupJob.class.getName());
    private final UserRateLimitCounterDao userRateLimitCounterDao;
    private final SystemPropertiesService systemPropertiesService;

    public HistoryCleanupJob(UserRateLimitCounterDao userRateLimitCounterDao, SystemPropertiesService systemPropertiesService) {
        this.userRateLimitCounterDao = userRateLimitCounterDao;
        this.systemPropertiesService = systemPropertiesService;
    }

    @Override
    public void schedule(@Nonnull SchedulerService schedulerService) throws SchedulerServiceException {
        HistoryCleanupJobRunner jobRunner = new HistoryCleanupJobRunner();
        schedulerService.registerJobRunner(JOB_RUNNER_KEY, (JobRunner)jobRunner);
        JobConfig jobConfig = JobConfig.forJobRunnerKey((JobRunnerKey)JOB_RUNNER_KEY).withRunMode(RunMode.RUN_ONCE_PER_CLUSTER).withSchedule(this.createCleanupSchedule());
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

    private Schedule createCleanupSchedule() {
        SystemJobControlSettings jobControlSettings = this.systemPropertiesService.getSystemSettings().getJobControlSettings();
        long interval = jobControlSettings.getReportingDbRetentionPeriodDuration().toMillis();
        Date firstRunTime = Date.from(ZonedDateTime.now().plus(jobControlSettings.getReportingDbArchivingJobFrequencyDuration()).toInstant());
        return Schedule.forInterval((long)interval, (Date)firstRunTime);
    }

    @VisibleForTesting
    void cleanupHistory() {
        logger.debug("Cleanup old rate limit history...");
        SystemJobControlSettings jobControlSettings = this.systemPropertiesService.getSystemSettings().getJobControlSettings();
        long deletedCounters = this.userRateLimitCounterDao.deleteOlderThan(jobControlSettings.getReportingDbRetentionPeriodDuration());
        logger.debug("Successfully deleted [{}] counters", (Object)deletedCounters);
    }

    @VisibleForTesting
    class HistoryCleanupJobRunner
    implements JobRunner {
        HistoryCleanupJobRunner() {
        }

        @Nullable
        public JobRunnerResponse runJob(@Nonnull JobRunnerRequest request) {
            HistoryCleanupJob.this.cleanupHistory();
            return JobRunnerResponse.success();
        }
    }
}

