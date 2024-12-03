/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.plugins.authentication.impl.analytics.events;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.plugins.authentication.impl.analytics.events.AbstractCompatibilityModeStatusEvent;
import com.atlassian.plugins.authentication.impl.web.saml.TrackingCompatibilityModeResponseHandler;

public class ConditionlessResponseProcessedStatusEvent
extends AbstractCompatibilityModeStatusEvent {
    private final TrackingCompatibilityModeResponseHandler.CompatibilityModeResponseData compatibilityModeResponseData;

    public ConditionlessResponseProcessedStatusEvent(String nodeId, TrackingCompatibilityModeResponseHandler.CompatibilityModeResponseData compatibilityModeResponseData) {
        super(nodeId);
        this.compatibilityModeResponseData = compatibilityModeResponseData;
    }

    @Override
    @EventName
    public String getEventName() {
        String bucket = this.resolveBucket(this.compatibilityModeResponseData.getAmountOfConditionlessResponses());
        return "plugins.authentication.status.saml.compat.conditionless." + bucket;
    }
}

