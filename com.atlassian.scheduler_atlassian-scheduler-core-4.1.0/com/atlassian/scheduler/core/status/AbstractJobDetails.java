/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.RunMode
 *  com.atlassian.scheduler.config.Schedule
 *  com.atlassian.scheduler.status.JobDetails
 *  com.atlassian.scheduler.util.Safe
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.scheduler.core.status;

import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import com.atlassian.scheduler.status.JobDetails;
import com.atlassian.scheduler.util.Safe;
import java.util.Date;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractJobDetails
implements JobDetails {
    protected final JobId jobId;
    protected final JobRunnerKey jobRunnerKey;
    protected final RunMode runMode;
    protected final Schedule schedule;
    private final Date nextRunTime;
    private final byte[] rawParameters;

    protected AbstractJobDetails(JobId jobId, JobRunnerKey jobRunnerKey, RunMode runMode, Schedule schedule, @Nullable Date nextRunTime, @Nullable byte[] rawParameters) {
        this.jobId = Objects.requireNonNull(jobId, "jobId");
        this.jobRunnerKey = Objects.requireNonNull(jobRunnerKey, "jobRunnerKey");
        this.runMode = Objects.requireNonNull(runMode, "runMode");
        this.schedule = Objects.requireNonNull(schedule, "schedule");
        this.nextRunTime = Safe.copy((Date)nextRunTime);
        this.rawParameters = Safe.copy((byte[])rawParameters);
    }

    @Nonnull
    public final JobId getJobId() {
        return this.jobId;
    }

    @Nonnull
    public final JobRunnerKey getJobRunnerKey() {
        return this.jobRunnerKey;
    }

    @Nonnull
    public final RunMode getRunMode() {
        return this.runMode;
    }

    @Nonnull
    public Schedule getSchedule() {
        return this.schedule;
    }

    @Nullable
    public Date getNextRunTime() {
        return Safe.copy((Date)this.nextRunTime);
    }

    @Nullable
    public final byte[] getRawParameters() {
        return Safe.copy((byte[])this.rawParameters);
    }

    public final String toString() {
        StringBuilder sb = new StringBuilder(128).append(this.getClass().getSimpleName()).append("[jobId=").append(this.jobId).append(",jobRunnerKey=").append(this.jobRunnerKey).append(",runMode=").append(this.runMode).append(",schedule=").append(this.schedule).append(",nextRunTime=").append(this.nextRunTime).append(",rawParameters=(");
        if (this.rawParameters == null) {
            sb.append("null)");
        } else {
            sb.append(this.rawParameters.length).append(" bytes)");
        }
        this.appendToStringDetails(sb);
        return sb.append(']').toString();
    }

    protected abstract void appendToStringDetails(StringBuilder var1);
}

