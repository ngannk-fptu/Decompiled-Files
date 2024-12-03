/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.oauth2.client.api.lib.event;

import com.atlassian.analytics.api.annotations.EventName;
import java.util.Objects;

@EventName(value="plugins.oauth2.client.flow.request.started")
public class FlowRequestStartedEvent {
    private final String flowRequestId;

    public FlowRequestStartedEvent(String flowRequestId) {
        this.flowRequestId = flowRequestId;
    }

    public String getFlowRequestId() {
        return this.flowRequestId;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FlowRequestStartedEvent that = (FlowRequestStartedEvent)o;
        return Objects.equals(this.flowRequestId, that.flowRequestId);
    }

    public int hashCode() {
        return Objects.hash(this.flowRequestId);
    }
}

