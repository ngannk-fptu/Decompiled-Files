/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.client.logger;

import com.atlassian.analytics.event.ProcessedEvent;

public interface AnalyticsLogger {
    public void logEvent(ProcessedEvent var1);

    public void logCleanupDeletion(String var1);

    public void reset();
}

