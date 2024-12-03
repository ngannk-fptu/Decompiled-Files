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
 */
package com.atlassian.ratelimiting.internal.analytics;

import com.atlassian.ratelimiting.analytics.AnalyticsService;
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
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AnalyticsBatchEventsPublisherJob
implements ScheduledJobSource {
    private static final JobId JOB_ID = JobId.of((String)AnalyticsBatchEventsPublisherJob.class.getSimpleName());
    private static final JobRunnerKey JOB_RUNNER_KEY = JobRunnerKey.of((String)AnalyticsBatchEventsPublisherJob.class.getName());
    private static final String EVERY_DAY_AT_MIDNIGHT_CRON_EXPRESSION = "0 0 0 * * ?";
    private final AnalyticsService analyticsService;

    public AnalyticsBatchEventsPublisherJob(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @Override
    public void schedule(@Nonnull SchedulerService schedulerService) throws SchedulerServiceException {
        schedulerService.registerJobRunner(JOB_RUNNER_KEY, (JobRunner)new AnalyticsPublisherJobRunner());
        JobConfig jobConfig = JobConfig.forJobRunnerKey((JobRunnerKey)JOB_RUNNER_KEY).withRunMode(RunMode.RUN_LOCALLY).withSchedule(Schedule.forCronExpression((String)EVERY_DAY_AT_MIDNIGHT_CRON_EXPRESSION));
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

    private class AnalyticsPublisherJobRunner
    implements JobRunner {
        private AnalyticsPublisherJobRunner() {
        }

        @Nullable
        public JobRunnerResponse runJob(@Nonnull JobRunnerRequest request) {
            AnalyticsBatchEventsPublisherJob.this.analyticsService.publishBatchEvents();
            return JobRunnerResponse.success();
        }
    }
}

