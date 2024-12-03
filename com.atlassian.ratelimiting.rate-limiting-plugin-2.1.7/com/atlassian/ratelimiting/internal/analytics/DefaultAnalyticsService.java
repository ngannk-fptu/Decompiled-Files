/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventPublisher
 */
package com.atlassian.ratelimiting.internal.analytics;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.ratelimiting.analytics.AnalyticsService;
import com.atlassian.ratelimiting.internal.analytics.event.AnalyticsRejectedRequestCountEvent;
import java.util.concurrent.atomic.AtomicLong;

public class DefaultAnalyticsService
implements AnalyticsService {
    private final AtomicLong rejectCount = new AtomicLong();
    private final EventPublisher eventPublisher;

    public DefaultAnalyticsService(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void incrementRejectCount() {
        this.rejectCount.incrementAndGet();
    }

    @Override
    public void publishBatchEvents() {
        this.publishRejectedRequestCountEvent();
    }

    private void publishRejectedRequestCountEvent() {
        long count = this.rejectCount.getAndSet(0L);
        this.eventPublisher.publish((Object)new AnalyticsRejectedRequestCountEvent(count));
    }
}

