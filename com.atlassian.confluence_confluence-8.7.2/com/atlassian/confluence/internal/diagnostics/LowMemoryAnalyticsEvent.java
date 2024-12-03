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
@EventName(value="confluence.diagnostics.low-memory")
public class LowMemoryAnalyticsEvent {
    private final Long freeMemoryInMb;
    private final Long totalMemoryInMb;
    private final Long minMemoryThresholdInMb;

    public LowMemoryAnalyticsEvent(Long freeMemoryInMegabytes, Long totalMemoryInMegabytes, Long minMemoryThresholdInMegabytes) {
        this.freeMemoryInMb = freeMemoryInMegabytes;
        this.totalMemoryInMb = totalMemoryInMegabytes;
        this.minMemoryThresholdInMb = minMemoryThresholdInMegabytes;
    }

    public Long getFreeMemoryInMb() {
        return this.freeMemoryInMb;
    }

    public Long getTotalMemoryInMb() {
        return this.totalMemoryInMb;
    }

    public Long getMinMemoryThresholdInMb() {
        return this.minMemoryThresholdInMb;
    }
}

