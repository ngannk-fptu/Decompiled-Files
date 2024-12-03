/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.sal.api.user.UserKey
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
import com.atlassian.ratelimiting.dao.UserRateLimitCounter;
import com.atlassian.ratelimiting.dao.UserRateLimitCounterDao;
import com.atlassian.ratelimiting.internal.history.HistoryInterval;
import com.atlassian.ratelimiting.internal.history.HistoryIntervalManager;
import com.atlassian.ratelimiting.scheduling.ScheduledJobSource;
import com.atlassian.sal.api.user.UserKey;
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
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HistoryFlushJob
implements ScheduledJobSource {
    private static final Logger logger = LoggerFactory.getLogger(HistoryFlushJob.class);
    private static final JobId JOB_ID = JobId.of((String)HistoryFlushJob.class.getSimpleName());
    private static final JobRunnerKey JOB_RUNNER_KEY = JobRunnerKey.of((String)HistoryFlushJob.class.getName());
    private final SystemPropertiesService systemPropertiesService;
    private final UserRateLimitCounterDao userRateLimitCounterDao;
    private final HistoryIntervalManager historyIntervalManager;

    public HistoryFlushJob(UserRateLimitCounterDao userRateLimitCounterDao, HistoryIntervalManager historyIntervalManager, SystemPropertiesService systemPropertiesService) {
        this.userRateLimitCounterDao = userRateLimitCounterDao;
        this.historyIntervalManager = historyIntervalManager;
        this.systemPropertiesService = systemPropertiesService;
    }

    @Override
    public void schedule(@Nonnull SchedulerService schedulerService) throws SchedulerServiceException {
        HistoryFlushJobRunner jobRunner = new HistoryFlushJobRunner();
        schedulerService.registerJobRunner(JOB_RUNNER_KEY, (JobRunner)jobRunner);
        JobConfig jobConfig = JobConfig.forJobRunnerKey((JobRunnerKey)JOB_RUNNER_KEY).withRunMode(RunMode.RUN_LOCALLY).withSchedule(this.createFlushSchedule());
        schedulerService.scheduleJob(JOB_ID, jobConfig);
    }

    @Override
    public void unschedule(@Nonnull SchedulerService schedulerService) {
        schedulerService.unscheduleJob(JOB_ID);
        schedulerService.unregisterJobRunner(JOB_RUNNER_KEY);
    }

    private Schedule createFlushSchedule() {
        Duration historyFlushJobDuration = this.systemPropertiesService.getSystemSettings().getJobControlSettings().getBucketCollectionJobFrequencyDuration();
        long interval = historyFlushJobDuration.toMillis();
        Date firstRunTime = Date.from(ZonedDateTime.now().plus(historyFlushJobDuration).toInstant());
        return Schedule.forInterval((long)interval, (Date)firstRunTime);
    }

    @Override
    public JobId getJobId() {
        return JOB_ID;
    }

    @VisibleForTesting
    void collectRejectHistory() {
        logger.debug("Collecting counters...");
        HistoryInterval.CompletedHistoryInterval interval = this.historyIntervalManager.collect();
        interval.getCounters().entrySet().stream().map(e -> this.makeCounter(interval.getStart(), (UserKey)e.getKey(), (Long)e.getValue())).forEach(this.userRateLimitCounterDao::create);
        logger.debug("Successfully collected [{}] counters", (Object)interval.getCounters().size());
    }

    private UserRateLimitCounter makeCounter(LocalDateTime start, UserKey userId, long rejectCount) {
        return UserRateLimitCounter.builder().user(userId).intervalStart(start).rejectCount(rejectCount).build();
    }

    private class HistoryFlushJobRunner
    implements JobRunner {
        private HistoryFlushJobRunner() {
        }

        @Nullable
        public JobRunnerResponse runJob(@Nonnull JobRunnerRequest request) {
            try {
                HistoryFlushJob.this.collectRejectHistory();
            }
            catch (RuntimeException e) {
                return JobRunnerResponse.failed((Throwable)e);
            }
            return JobRunnerResponse.success();
        }
    }
}

