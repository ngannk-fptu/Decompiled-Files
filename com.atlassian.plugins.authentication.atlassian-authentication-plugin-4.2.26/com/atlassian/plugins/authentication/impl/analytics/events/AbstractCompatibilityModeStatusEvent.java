/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.authentication.impl.analytics.events;

import com.atlassian.plugins.authentication.impl.analytics.events.AnalyticsEvent;

public abstract class AbstractCompatibilityModeStatusEvent
implements AnalyticsEvent {
    private final String nodeId;

    public AbstractCompatibilityModeStatusEvent(String nodeId) {
        this.nodeId = nodeId;
    }

    protected String resolveBucket(long amount) {
        String bucket = amount <= 0L ? "none" : (amount <= 10L ? "one_to_ten" : (amount <= 100L ? "eleven_to_hundred" : "more_than_hundred"));
        return bucket;
    }

    public String getNodeId() {
        return this.nodeId;
    }
}

