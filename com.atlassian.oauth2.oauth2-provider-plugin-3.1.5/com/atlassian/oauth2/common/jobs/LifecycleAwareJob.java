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
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.oauth2.common.jobs;

import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.JobRunnerRequest;
import com.atlassian.scheduler.JobRunnerResponse;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class LifecycleAwareJob
implements LifecycleAware,
JobRunner {
    private static final Logger log = LoggerFactory.getLogger(LifecycleAwareJob.class);
    private final SchedulerService schedulerService;

    protected LifecycleAwareJob(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    public void onStart() {
        try {
            this.schedulerService.registerJobRunner(this.getJobRunnerKey(), (JobRunner)this);
            this.schedulerService.scheduleJob(this.getJobId(), this.getJobConfig());
            log.debug("Registered job with JOB_ID " + this.getJobId());
        }
        catch (SchedulerServiceException e) {
            throw new RuntimeException("Failed to start job [" + this.getJobRunnerKey() + "].");
        }
    }

    public void onStop() {
        this.schedulerService.unregisterJobRunner(this.getJobRunnerKey());
        this.schedulerService.unscheduleJob(this.getJobId());
        log.debug("Unregistered job with JOB_ID " + this.getJobId());
    }

    @Nullable
    public JobRunnerResponse runJob(JobRunnerRequest request) {
        return this.job();
    }

    protected abstract JobRunnerResponse job();

    protected abstract JobId getJobId();

    protected abstract JobRunnerKey getJobRunnerKey();

    protected abstract JobConfig getJobConfig();
}

