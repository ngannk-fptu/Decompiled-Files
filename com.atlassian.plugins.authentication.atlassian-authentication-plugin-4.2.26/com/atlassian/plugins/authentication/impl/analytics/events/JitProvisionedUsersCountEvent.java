/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.plugins.authentication.impl.analytics.events;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.plugins.authentication.impl.analytics.events.AnalyticsEvent;

public class JitProvisionedUsersCountEvent
implements AnalyticsEvent {
    private final long count;
    private final String nodeId;

    public JitProvisionedUsersCountEvent(String nodeId, long count) {
        this.count = count;
        this.nodeId = nodeId;
    }

    @Override
    @EventName
    public String getEventName() {
        return "plugins.authentication.jit.provisionedusers";
    }

    public long getCount() {
        return this.count;
    }

    public String getNodeId() {
        return this.nodeId;
    }
}

