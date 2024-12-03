/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.scheduler.SchedulerServiceException
 *  com.atlassian.scheduler.config.JobId
 *  javax.annotation.Nonnull
 */
package com.atlassian.ratelimiting.scheduling;

import com.atlassian.scheduler.SchedulerService;
import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.config.JobId;
import javax.annotation.Nonnull;

public interface ScheduledJobSource {
    public void schedule(@Nonnull SchedulerService var1) throws SchedulerServiceException;

    public void unschedule(@Nonnull SchedulerService var1) throws SchedulerServiceException;

    public JobId getJobId();
}

