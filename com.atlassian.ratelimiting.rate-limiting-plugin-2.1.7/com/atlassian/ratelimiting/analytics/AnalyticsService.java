/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ratelimiting.analytics;

public interface AnalyticsService {
    public void incrementRejectCount();

    public void publishBatchEvents();
}

