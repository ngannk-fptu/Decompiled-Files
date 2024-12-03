/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joda.time.Duration
 */
package com.atlassian.upm.schedule;

import com.atlassian.upm.schedule.UpmScheduledJob;
import org.joda.time.Duration;

public interface UpmScheduler {
    public void registerJob(UpmScheduledJob var1);

    public void unregisterJob(UpmScheduledJob var1);

    public void triggerJob(Class<? extends UpmScheduledJob> var1, RunMode var2);

    public void triggerRunnable(Runnable var1, Duration var2, String var3);

    public void waitForTriggeredJobs();

    public static enum RunMode {
        SCHEDULED,
        TRIGGERED_BY_USER,
        TRIGGERED_BY_UPM_ENABLEMENT,
        TRIGGERED_INTERNALLY;

    }
}

