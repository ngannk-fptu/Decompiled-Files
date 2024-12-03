/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.streams.internal.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.annotations.Internal;

@EventName(value="atlassian.streams.stats")
@Internal
public class StreamStatsEvent {
    private final long requestId;
    private final long processingTimeMillis;
    private final boolean localProvider;
    private final boolean requestSuccessful;
    private final String product;
    private final long maxResults;
    private final long timeout;

    public StreamStatsEvent(long requestId, long processingTimeMillis, boolean localProvider, boolean requestSuccessful, String product, long maxResults, long timeout) {
        this.requestId = requestId;
        this.processingTimeMillis = processingTimeMillis;
        this.localProvider = localProvider;
        this.requestSuccessful = requestSuccessful;
        this.product = product;
        this.maxResults = maxResults;
        this.timeout = timeout;
    }

    public long getRequestId() {
        return this.requestId;
    }

    public long getProcessingTimeMillis() {
        return this.processingTimeMillis;
    }

    public boolean isLocalProvider() {
        return this.localProvider;
    }

    public boolean isRequestSuccessful() {
        return this.requestSuccessful;
    }

    public String getProduct() {
        return this.product;
    }

    public long getMaxResults() {
        return this.maxResults;
    }

    public long getTimeout() {
        return this.timeout;
    }
}

