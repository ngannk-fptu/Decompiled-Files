/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.plugins.authentication.impl.basicauth.analytics.events;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.plugins.authentication.impl.analytics.events.AnalyticsEvent;
import com.atlassian.plugins.authentication.impl.basicauth.BasicAuthConfig;

public class BasicAuthStatusEvent
implements AnalyticsEvent {
    private final BasicAuthConfig basicAuthConfig;

    public BasicAuthStatusEvent(BasicAuthConfig basicAuthConfig) {
        this.basicAuthConfig = basicAuthConfig;
    }

    @Override
    @EventName
    public String getEventName() {
        return "plugins.authentication.status.basic.auth" + (this.basicAuthConfig.isBlockRequests() ? ".disabled" : ".enabled");
    }
}

