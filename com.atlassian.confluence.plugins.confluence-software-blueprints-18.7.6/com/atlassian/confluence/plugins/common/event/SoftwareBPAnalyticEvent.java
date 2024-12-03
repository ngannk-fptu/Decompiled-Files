/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.confluence.plugins.common.event;

import com.atlassian.analytics.api.annotations.EventName;

public class SoftwareBPAnalyticEvent {
    private String eventName;

    public SoftwareBPAnalyticEvent(String analyticName) {
        this.eventName = analyticName;
    }

    @EventName
    public String getEventName() {
        return this.eventName;
    }
}

