/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.SchedulerServiceException
 *  javax.annotation.Nonnull
 */
package com.atlassian.scheduler.core;

import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.core.LifecycleAwareSchedulerService;
import com.atlassian.scheduler.core.RunningJob;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;

public interface SchedulerServiceController {
    public void start() throws SchedulerServiceException;

    public void standby() throws SchedulerServiceException;

    public void shutdown();

    @Nonnull
    public Collection<RunningJob> getLocallyRunningJobs();

    public boolean waitUntilIdle(long var1, TimeUnit var3) throws InterruptedException;

    @Nonnull
    public LifecycleAwareSchedulerService.State getState();
}

