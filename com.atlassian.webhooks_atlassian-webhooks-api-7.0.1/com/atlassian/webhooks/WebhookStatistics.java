/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.webhooks;

public interface WebhookStatistics {
    public long getDispatchedCount();

    public long getPublishedCount();

    public long getDispatchErrorCount();

    public long getDispatchFailureCount();

    public long getDispatchRejectedCount();

    public long getDispatchSuccessCount();
}

