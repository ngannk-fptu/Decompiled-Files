/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.webhooks.internal.jmx;

public interface WebhooksMXBean {
    public long getDispatchCount();

    public long getDispatchErrorCount();

    public long getDispatchFailureCount();

    public long getDispatchInFlightCount();

    public long getDispatchLastRejectedTimestamp();

    public long getDispatchRejectedCount();

    public long getDispatchSuccessCount();

    public long getPublishCount();
}

