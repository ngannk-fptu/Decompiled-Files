/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.client.report;

import com.atlassian.analytics.client.TimeKeeper;

public class TimeoutChecker {
    private static final int DEFAULT_TIMEOUT_MILLIS = 3600000;
    private final TimeKeeper timeKeeper;
    private long timeoutMillis;
    private long lastActionTime;

    public TimeoutChecker(TimeKeeper timeKeeper) {
        this.timeKeeper = timeKeeper;
        this.timeoutMillis = 3600000L;
        this.lastActionTime = 0L;
    }

    public void setTimeoutMillis(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
    }

    public void actionHasOccurred() {
        this.lastActionTime = this.timeKeeper.currentTimeMillis();
    }

    public boolean isTimeoutExceeded() {
        return this.timeKeeper.currentTimeMillis() - this.lastActionTime > this.timeoutMillis;
    }
}

