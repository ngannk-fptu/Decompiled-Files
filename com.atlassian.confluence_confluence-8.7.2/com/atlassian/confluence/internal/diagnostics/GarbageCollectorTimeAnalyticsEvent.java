/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.internal.diagnostics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.event.api.AsynchronousPreferred;

@AsynchronousPreferred
@EventName(value="confluence.diagnostics.garbage-collector-time")
public class GarbageCollectorTimeAnalyticsEvent {
    private final long durationInMillis;
    private final long windowInMillis;
    private final long thresholdInPercent;

    public GarbageCollectorTimeAnalyticsEvent(long durationInMillis, long windowInMillis, long thresholdInPercent) {
        this.durationInMillis = durationInMillis;
        this.windowInMillis = windowInMillis;
        this.thresholdInPercent = thresholdInPercent;
    }

    public long getDurationInMillis() {
        return this.durationInMillis;
    }

    public long getWindowInMillis() {
        return this.windowInMillis;
    }

    public long getThresholdInPercent() {
        return this.thresholdInPercent;
    }
}

