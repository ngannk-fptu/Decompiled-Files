/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.plugins.mobile.analytic;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.event.api.AsynchronousPreferred;

@AsynchronousPreferred
public class MobileSimpleAnalyticEvent {
    private static final String EVENT_NAME_PREFIX = "confluence.mobile.native.server";
    private final String eventName;

    public MobileSimpleAnalyticEvent(String eventName) {
        this.eventName = eventName;
    }

    @EventName
    public String getEventName() {
        return "confluence.mobile.native.server." + this.eventName;
    }
}

