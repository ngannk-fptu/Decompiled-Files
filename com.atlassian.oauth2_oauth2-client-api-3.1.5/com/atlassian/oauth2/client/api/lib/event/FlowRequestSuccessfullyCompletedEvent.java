/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.oauth2.client.api.lib.event;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.oauth2.client.api.lib.event.FlowRequestCompletedEvent;

@EventName(value="plugins.oauth2.client.flow.request.completed.successfully")
public class FlowRequestSuccessfullyCompletedEvent
extends FlowRequestCompletedEvent {
    public FlowRequestSuccessfullyCompletedEvent(String flowRequestId, String clientId, String providerType) {
        super(flowRequestId, clientId, providerType);
    }
}

