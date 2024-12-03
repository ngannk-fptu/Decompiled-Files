/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  javax.annotation.CheckForNull
 *  javax.annotation.Nonnull
 *  javax.annotation.concurrent.Immutable
 */
package com.atlassian.scheduler.status;

import com.atlassian.annotations.PublicApi;
import com.atlassian.scheduler.config.JobId;
import com.atlassian.scheduler.config.JobRunnerKey;
import com.atlassian.scheduler.config.RunMode;
import com.atlassian.scheduler.config.Schedule;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
@PublicApi
public interface JobDetails {
    @Nonnull
    public JobId getJobId();

    @Nonnull
    public JobRunnerKey getJobRunnerKey();

    @Nonnull
    public RunMode getRunMode();

    @Nonnull
    public Schedule getSchedule();

    @CheckForNull
    public Date getNextRunTime();

    @Nonnull
    public Map<String, Serializable> getParameters();

    public boolean isRunnable();
}

