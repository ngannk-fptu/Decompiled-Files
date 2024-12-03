/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEvent
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.internal.webhooks.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.plugins.analytics.jobs.api.PeriodicEvent;
import com.atlassian.event.api.AsynchronousPreferred;

@AsynchronousPreferred
@EventName(value="confluence.webhook.daily-triggers-summary")
public class WebhooksDailySummaryPeriodicEvent
implements PeriodicEvent {
    private final int errorCount;
    private final int failureCount;
    private final int successCount;

    public WebhooksDailySummaryPeriodicEvent(int errorCount, int failureCount, int successCount) {
        this.errorCount = errorCount;
        this.failureCount = failureCount;
        this.successCount = successCount;
    }

    public int getErrorCount() {
        return this.errorCount;
    }

    public int getFailureCount() {
        return this.failureCount;
    }

    public int getSuccessCount() {
        return this.successCount;
    }
}

