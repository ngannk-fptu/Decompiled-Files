/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.SchedulerRuntimeException
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.scheduler.core.status;

import com.atlassian.scheduler.SchedulerRuntimeException;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import com.atlassian.scheduler.core.JobRunnerNotRegisteredException;
import com.atlassian.scheduler.core.status.AbstractJobDetails;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class UnusableJobDetails
extends AbstractJobDetails {
    private final Throwable cause;

    public UnusableJobDetails(JobId jobId, JobRunnerKey jobRunnerKey, RunMode runMode, Schedule schedule, @Nullable Date nextRunTime, byte[] parameters, @Nullable Throwable cause) {
        super(jobId, jobRunnerKey, runMode, schedule, nextRunTime, parameters);
        this.cause = cause != null ? cause : new JobRunnerNotRegisteredException(jobRunnerKey);
    }

    @Nonnull
    public Map<String, Serializable> getParameters() {
        throw new SchedulerRuntimeException("The parameters cannot be accessed: " + this.cause, this.cause);
    }

    public boolean isRunnable() {
        return false;
    }

    @Override
    protected void appendToStringDetails(StringBuilder sb) {
        sb.append(",cause=").append(this.cause);
    }
}

