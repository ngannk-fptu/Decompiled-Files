/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.schedule.managers;

import com.atlassian.confluence.schedule.managers.DefaultSchedulerRunDetailsManager;

public enum SchedulerRunDetailsPurgeMode {
    ALL(DefaultSchedulerRunDetailsManager.ALL_JOBS_TTL_MILLIS),
    UNSUCCESSFUL(DefaultSchedulerRunDetailsManager.UNSUCCESSFUL_JOBS_TTL_MILLIS);

    private long timeToLiveThreshold;

    private SchedulerRunDetailsPurgeMode(long timeToLiveThreshold) {
        this.timeToLiveThreshold = timeToLiveThreshold;
    }

    public long getTimeToLiveThreshold() {
        return this.timeToLiveThreshold;
    }
}

