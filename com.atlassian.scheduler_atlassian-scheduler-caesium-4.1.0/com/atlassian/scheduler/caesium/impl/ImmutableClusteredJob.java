/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.Schedule
 *  com.atlassian.scheduler.util.Safe
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.Immutable
 */
package com.atlassian.scheduler.caesium.impl;

import com.atlassian.scheduler.caesium.spi.ClusteredJob;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.Schedule;
import com.atlassian.scheduler.util.Safe;
import java.util.Date;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public class ImmutableClusteredJob
implements ClusteredJob {
    private static final char[] HEX = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private final JobId jobId;
    private final JobRunnerKey jobRunnerKey;
    private final Schedule schedule;
    private final Date nextRunTime;
    private final long version;
    private final byte[] rawParameters;

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(ClusteredJob prototype) {
        return new Builder().jobId(prototype.getJobId()).jobRunnerKey(prototype.getJobRunnerKey()).schedule(prototype.getSchedule()).nextRunTime(prototype.getNextRunTime()).version(prototype.getVersion()).parameters(prototype.getRawParameters());
    }

    ImmutableClusteredJob(Builder builder) {
        this.jobId = Objects.requireNonNull(builder.jobId, "jobId");
        this.jobRunnerKey = Objects.requireNonNull(builder.jobRunnerKey, "jobRunnerKey");
        this.schedule = Objects.requireNonNull(builder.schedule, "schedule");
        this.nextRunTime = Safe.copy((Date)builder.nextRunTime);
        this.version = builder.version;
        this.rawParameters = Safe.copy((byte[])builder.rawParameters);
    }

    public Builder copy() {
        return ImmutableClusteredJob.builder(this);
    }

    @Override
    @Nonnull
    public JobId getJobId() {
        return this.jobId;
    }

    @Override
    @Nonnull
    public JobRunnerKey getJobRunnerKey() {
        return this.jobRunnerKey;
    }

    @Override
    @Nonnull
    public Schedule getSchedule() {
        return this.schedule;
    }

    @Override
    @Nullable
    public Date getNextRunTime() {
        return Safe.copy((Date)this.nextRunTime);
    }

    @Override
    public long getVersion() {
        return this.version;
    }

    @Override
    @Nullable
    public byte[] getRawParameters() {
        return Safe.copy((byte[])this.rawParameters);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(256).append("ImmutableClusteredJob[jobId=").append(this.jobId).append(",jobRunnerKey=").append(this.jobRunnerKey).append(",schedule=").append(this.schedule).append(",nextRunTime=").append(this.nextRunTime).append(",version=").append(this.version).append(",rawParameters=");
        if (this.rawParameters == null) {
            sb.append("(null)");
        } else {
            sb.append('[');
            ImmutableClusteredJob.appendBytes(sb, this.rawParameters);
            sb.append(']');
        }
        return sb.append(']').toString();
    }

    private static void appendBytes(StringBuilder sb, byte[] rawParameters) {
        int stop = Math.min(rawParameters.length, 64);
        for (int i = 0; i < stop; ++i) {
            byte x = rawParameters[i];
            sb.append(HEX[(x & 0xF0) >> 4]);
            sb.append(HEX[x & 0xF]);
        }
        if (stop < rawParameters.length) {
            sb.append("...");
        }
    }

    public static class Builder {
        JobId jobId;
        JobRunnerKey jobRunnerKey;
        Schedule schedule = Schedule.runOnce(null);
        Date nextRunTime;
        long version = 1L;
        byte[] rawParameters;

        public Builder jobId(JobId jobId) {
            this.jobId = Objects.requireNonNull(jobId, "jobId");
            return this;
        }

        public Builder jobRunnerKey(JobRunnerKey jobRunnerKey) {
            this.jobRunnerKey = Objects.requireNonNull(jobRunnerKey, "jobRunnerKey");
            return this;
        }

        public Builder schedule(Schedule schedule) {
            this.schedule = Objects.requireNonNull(schedule, "schedule");
            return this;
        }

        public Builder nextRunTime(@Nullable Date nextRunTime) {
            this.nextRunTime = nextRunTime;
            return this;
        }

        public Builder version(long version) {
            this.version = version;
            return this;
        }

        public Builder parameters(@Nullable byte[] rawParameters) {
            this.rawParameters = rawParameters;
            return this;
        }

        public ImmutableClusteredJob build() {
            return new ImmutableClusteredJob(this);
        }
    }
}

