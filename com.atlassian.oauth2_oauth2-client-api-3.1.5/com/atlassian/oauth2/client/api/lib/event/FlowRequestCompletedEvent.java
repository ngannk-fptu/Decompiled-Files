/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.oauth2.client.api.lib.event;

import com.atlassian.analytics.api.annotations.EventName;
import java.util.Objects;

@EventName(value="plugins.oauth2.client.flow.request.completed")
public class FlowRequestCompletedEvent {
    private final String flowRequestId;
    private final String clientId;
    private final String providerType;

    @Deprecated
    public FlowRequestCompletedEvent(String flowRequestId) {
        this.flowRequestId = flowRequestId;
        this.clientId = null;
        this.providerType = null;
    }

    public FlowRequestCompletedEvent(String flowRequestId, String clientId, String providerType) {
        this.flowRequestId = flowRequestId;
        this.clientId = clientId;
        this.providerType = providerType;
    }

    public String getFlowRequestId() {
        return this.flowRequestId;
    }

    public String getClientId() {
        return this.clientId;
    }

    public String getProviderType() {
        return this.providerType;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FlowRequestCompletedEvent that = (FlowRequestCompletedEvent)o;
        return Objects.equals(this.flowRequestId, that.flowRequestId) && Objects.equals(this.clientId, that.clientId) && Objects.equals(this.providerType, that.providerType);
    }

    public int hashCode() {
        return Objects.hash(this.flowRequestId, this.clientId, this.providerType);
    }
}

