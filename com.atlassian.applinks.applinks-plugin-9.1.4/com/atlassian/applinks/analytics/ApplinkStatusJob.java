/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.JobRunnerRequest
 *  com.atlassian.scheduler.JobRunnerResponse
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.Schedule
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.applinks.analytics;

import com.atlassian.applinks.analytics.ApplinkStatusPublisher;
import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.Schedule;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApplinkStatusJob
implements JobRunner,
LifecycleAware {
    private static final Logger log = LoggerFactory.getLogger(ApplinkStatusJob.class);
    private static final long SCHEDULE_INTERVAL = TimeUnit.DAYS.toMillis(1L);
    private static final JobRunnerKey JOB_RUNNER_KEY = JobRunnerKey.of((String)ApplinkStatusJob.class.getName());
    private static final JobId JOB_ID = JobId.of((String)"applink-status-analytics-job");
    @Nonnull
    private final ApplinkStatusPublisher applinkStatusPublisher;
    @Nonnull
    private final SchedulerService scheduler;

    @Autowired
    public ApplinkStatusJob(@Nonnull SchedulerService scheduler, @Nonnull ApplinkStatusPublisher applinkStatusPublisher) {
        this.scheduler = Objects.requireNonNull(scheduler);
        this.applinkStatusPublisher = Objects.requireNonNull(applinkStatusPublisher);
    }

    @Nullable
    public JobRunnerResponse runJob(@Nonnull JobRunnerRequest request) {
        try {
            this.applinkStatusPublisher.publishApplinkStatus();
            return JobRunnerResponse.success();
        }
        catch (Exception e) {
            return JobRunnerResponse.failed((Throwable)e);
        }
    }

    private Schedule getSchedule() {
        return Schedule.forInterval((long)SCHEDULE_INTERVAL, null);
    }

    public void onStart() {
        this.scheduler.registerJobRunner(JOB_RUNNER_KEY, (JobRunner)this);
        try {
            this.scheduler.scheduleJob(JOB_ID, JobConfig.forJobRunnerKey((JobRunnerKey)JOB_RUNNER_KEY).withSchedule(this.getSchedule()));
        }
        catch (SchedulerServiceException e) {
            log.error("Unable to schedule analytics job", (Throwable)e);
        }
    }

    public void onStop() {
        this.scheduler.unregisterJobRunner(JOB_RUNNER_KEY);
    }
}

