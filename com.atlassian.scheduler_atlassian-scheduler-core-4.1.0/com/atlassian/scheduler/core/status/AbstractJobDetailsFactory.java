/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  com.atlassian.scheduler.status.JobDetails
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.scheduler.core.status;

import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import com.atlassian.scheduler.core.AbstractSchedulerService;
import com.atlassian.scheduler.core.status.LazyJobDetails;
import com.atlassian.scheduler.status.JobDetails;
import java.util.Date;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractJobDetailsFactory<T> {
    private final AbstractSchedulerService schedulerService;

    protected AbstractJobDetailsFactory(AbstractSchedulerService schedulerService) {
        this.schedulerService = Objects.requireNonNull(schedulerService, "schedulerService");
    }

    public JobDetails buildJobDetails(JobId jobId, T jobData, RunMode runMode) {
        Objects.requireNonNull(jobId, "jobId");
        Objects.requireNonNull(jobData, "jobData");
        Objects.requireNonNull(runMode, "runMode");
        JobRunnerKey jobRunnerKey = Objects.requireNonNull(this.getJobRunnerKey(jobData), "jobRunnerKey");
        Schedule schedule = Objects.requireNonNull(this.getSchedule(jobData), "schedule");
        Date nextRunTime = this.getNextRunTime(jobData);
        byte[] parameters = this.getSerializedParameters(jobData);
        return new LazyJobDetails(this.schedulerService, jobId, jobRunnerKey, runMode, schedule, nextRunTime, parameters);
    }

    @Nonnull
    protected abstract JobRunnerKey getJobRunnerKey(T var1);

    @Nonnull
    protected abstract Schedule getSchedule(T var1);

    @Nullable
    protected abstract Date getNextRunTime(T var1);

    @Nullable
    protected abstract byte[] getSerializedParameters(T var1);
}

