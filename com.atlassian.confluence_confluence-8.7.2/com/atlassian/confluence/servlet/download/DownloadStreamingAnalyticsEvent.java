/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.confluence.servlet.download;

import com.atlassian.analytics.api.annotations.EventName;
import java.time.Duration;

public class DownloadStreamingAnalyticsEvent {
    private final String eventName;
    private final long byteCount;
    private final Duration elapsedTime;

    DownloadStreamingAnalyticsEvent(String eventName, long byteCount, Duration elapsedTime) {
        this.eventName = eventName;
        this.byteCount = byteCount;
        this.elapsedTime = elapsedTime;
    }

    @EventName
    public String getEventName() {
        return this.eventName;
    }

    public long getByteCount() {
        return this.byteCount;
    }

    public long getElapsedTimeNanos() {
        return this.elapsedTime.toNanos();
    }
}

