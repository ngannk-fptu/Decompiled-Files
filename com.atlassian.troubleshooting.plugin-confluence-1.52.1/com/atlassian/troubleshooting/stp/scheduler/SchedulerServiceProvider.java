/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.SchedulerService
 *  javax.annotation.Nonnull
 */
package com.atlassian.troubleshooting.stp.scheduler;

import com.atlassian.scheduler.SchedulerService;
import javax.annotation.Nonnull;

public interface SchedulerServiceProvider {
    @Nonnull
    public SchedulerService getSchedulerService();
}

