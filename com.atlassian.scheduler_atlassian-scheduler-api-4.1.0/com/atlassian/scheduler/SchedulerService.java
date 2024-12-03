/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  javax.annotation.CheckForNull
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.scheduler;

import com.atlassian.annotations.PublicApi;
import com.atlassian.scheduler.JobRunner;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.Schedule;
import com.atlassian.scheduler.status.JobDetails;
import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@PublicApi
public interface SchedulerService {
    public void registerJobRunner(JobRunnerKey var1, JobRunner var2);

    public void unregisterJobRunner(JobRunnerKey var1);

    @Nonnull
    public Set<JobRunnerKey> getRegisteredJobRunnerKeys();

    @Nonnull
    public Set<JobRunnerKey> getJobRunnerKeysForAllScheduledJobs();

    public void scheduleJob(JobId var1, JobConfig var2) throws SchedulerServiceException;

    @Nonnull
    public JobId scheduleJobWithGeneratedId(JobConfig var1) throws SchedulerServiceException;

    public void unscheduleJob(JobId var1);

    @Nullable
    public Date calculateNextRunTime(Schedule var1) throws SchedulerServiceException;

    @CheckForNull
    public JobDetails getJobDetails(JobId var1);

    @Nonnull
    public List<JobDetails> getJobsByJobRunnerKey(JobRunnerKey var1);

    @Nonnull
    public List<JobDetails> getJobsByJobRunnerKeys(List<JobRunnerKey> var1);
}

