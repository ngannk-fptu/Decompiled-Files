/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.confluence.plugins.analytics;

import com.atlassian.analytics.api.annotations.EventName;

public class CreateAnalyticsEvent {
    private String blueprintKey;

    public CreateAnalyticsEvent(String blueprintKey) {
        this.blueprintKey = blueprintKey;
    }

    @EventName
    public String getEventName() {
        return "confluence-spaces.playbook." + this.blueprintKey + ".created";
    }
}

