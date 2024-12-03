/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  javax.annotation.Nonnull
 */
package com.atlassian.scheduler;

import com.atlassian.annotations.PublicApi;
import com.atlassian.scheduler.config.JobConfig;
import com.atlassian.scheduler.config.JobId;
import java.util.Date;
import javax.annotation.Nonnull;

@PublicApi
public interface JobRunnerRequest {
    @Nonnull
    public Date getStartTime();

    @Nonnull
    public JobId getJobId();

    @Nonnull
    public JobConfig getJobConfig();

    public boolean isCancellationRequested();
}

