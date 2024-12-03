/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.config.JobId
 *  com.atlassian.scheduler.config.JobRunnerKey
 *  com.atlassian.scheduler.config.Schedule
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.scheduler.caesium.spi;

import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.Schedule;
import java.util.Date;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ClusteredJob {
    @Nonnull
    public JobId getJobId();

    @Nonnull
    public JobRunnerKey getJobRunnerKey();

    @Nonnull
    public Schedule getSchedule();

    @Nullable
    public Date getNextRunTime();

    public long getVersion();

    @Nullable
    public byte[] getRawParameters();
}

