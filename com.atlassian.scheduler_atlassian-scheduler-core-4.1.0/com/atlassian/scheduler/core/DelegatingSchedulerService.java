/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.JobRunner
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.Schedule
 *  com.atlassian.scheduler.status.JobDetails
 *  javax.annotation.CheckForNull
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.scheduler.core;

import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.Schedule;
import com.atlassian.scheduler.status.JobDetails;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DelegatingSchedulerService
implements SchedulerService {
    private final SchedulerService delegate;

    public DelegatingSchedulerService(SchedulerService delegate) {
        this.delegate = Objects.requireNonNull(delegate, "delegate");
    }

    public void registerJobRunner(JobRunnerKey jobRunnerKey, JobRunner jobRunner) {
        this.delegate.registerJobRunner(jobRunnerKey, jobRunner);
    }

    public void unregisterJobRunner(JobRunnerKey jobRunnerKey) {
        this.delegate.unregisterJobRunner(jobRunnerKey);
    }

    @Nonnull
    public Set<JobRunnerKey> getRegisteredJobRunnerKeys() {
        return this.delegate.getRegisteredJobRunnerKeys();
    }

    @Nonnull
    public Set<JobRunnerKey> getJobRunnerKeysForAllScheduledJobs() {
        return this.delegate.getJobRunnerKeysForAllScheduledJobs();
    }

    public void scheduleJob(JobId jobId, JobConfig jobConfig) throws SchedulerServiceException {
        this.delegate.scheduleJob(jobId, jobConfig);
    }

    @Nonnull
    public JobId scheduleJobWithGeneratedId(JobConfig jobConfig) throws SchedulerServiceException {
        return this.delegate.scheduleJobWithGeneratedId(jobConfig);
    }

    public void unscheduleJob(JobId jobId) {
        this.delegate.unscheduleJob(jobId);
    }

    @Nullable
    public Date calculateNextRunTime(Schedule schedule) throws SchedulerServiceException {
        return this.delegate.calculateNextRunTime(schedule);
    }

    @CheckForNull
    public JobDetails getJobDetails(JobId jobId) {
        return this.delegate.getJobDetails(jobId);
    }

    @Nonnull
    public List<JobDetails> getJobsByJobRunnerKey(JobRunnerKey jobRunnerKey) {
        return this.delegate.getJobsByJobRunnerKey(jobRunnerKey);
    }

    @Nonnull
    public List<JobDetails> getJobsByJobRunnerKeys(List<JobRunnerKey> jobRunnerKeys) {
        return this.delegate.getJobsByJobRunnerKeys(jobRunnerKeys);
    }
}

