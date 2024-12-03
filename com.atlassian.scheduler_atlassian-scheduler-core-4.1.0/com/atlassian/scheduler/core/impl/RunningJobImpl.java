/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.config.JobConfig
 *  com.atlassian.scheduler.config.JobId
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.ThreadSafe
 */
package com.atlassian.scheduler.core.impl;

import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.core.RunningJob;
import java.util.Date;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public final class RunningJobImpl
implements RunningJob {
    private final long startTime;
    private final JobId jobId;
    private final JobConfig jobConfig;
    private volatile boolean cancelled;

    public RunningJobImpl(Date startTime, JobId jobId, JobConfig jobConfig) {
        this.startTime = Objects.requireNonNull(startTime, "startTime").getTime();
        this.jobId = Objects.requireNonNull(jobId, "jobId");
        this.jobConfig = Objects.requireNonNull(jobConfig, "jobConfig");
    }

    @Nonnull
    public Date getStartTime() {
        return new Date(this.startTime);
    }

    @Nonnull
    public JobId getJobId() {
        return this.jobId;
    }

    @Nonnull
    public JobConfig getJobConfig() {
        return this.jobConfig;
    }

    public boolean isCancellationRequested() {
        return this.cancelled;
    }

    @Override
    public void cancel() {
        this.cancelled = true;
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RunningJobImpl other = (RunningJobImpl)o;
        return this.startTime == other.startTime && this.jobId.equals((Object)other.jobId) && this.jobConfig.equals((Object)other.jobConfig);
    }

    public int hashCode() {
        return Objects.hash(this.startTime, this.jobId, this.jobConfig);
    }

    public String toString() {
        return "RunningJobImpl[startTime=" + this.startTime + ",jobId=" + this.jobId + ",jobConfig=" + this.jobConfig + ",cancelled=" + this.cancelled + ']';
    }
}

