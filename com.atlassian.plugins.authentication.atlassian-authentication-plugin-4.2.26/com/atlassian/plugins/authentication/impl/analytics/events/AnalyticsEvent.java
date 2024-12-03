/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.authentication.impl.analytics.events;

public interface AnalyticsEvent {
    public static final String AUTH_PLUGIN_ANALYTICS_EVENT_PREFIX = "plugins.authentication.";

    public String getEventName();

    default public boolean shouldPublish() {
        return true;
    }
}

