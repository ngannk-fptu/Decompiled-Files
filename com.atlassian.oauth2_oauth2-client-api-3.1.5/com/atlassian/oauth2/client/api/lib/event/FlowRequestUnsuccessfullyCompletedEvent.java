/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.oauth2.client.api.lib.event;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.oauth2.client.api.lib.event.FlowRequestCompletedEvent;
import java.util.Objects;

@EventName(value="plugins.oauth2.client.flow.request.completed.unsuccessfully")
public class FlowRequestUnsuccessfullyCompletedEvent
extends FlowRequestCompletedEvent {
    private final String errorMessage;

    public FlowRequestUnsuccessfullyCompletedEvent(String flowRequestId, String clientId, String providerType, String errorMessage) {
        super(flowRequestId, clientId, providerType);
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        FlowRequestUnsuccessfullyCompletedEvent that = (FlowRequestUnsuccessfullyCompletedEvent)o;
        return Objects.equals(this.getFlowRequestId(), that.getFlowRequestId()) && Objects.equals(this.getClientId(), that.getClientId()) && Objects.equals(this.getProviderType(), that.getProviderType()) && Objects.equals(this.getErrorMessage(), that.getErrorMessage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getFlowRequestId(), this.getClientId(), this.getProviderType(), this.getErrorMessage());
    }
}

