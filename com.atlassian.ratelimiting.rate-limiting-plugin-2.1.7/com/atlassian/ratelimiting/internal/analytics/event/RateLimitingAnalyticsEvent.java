/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ratelimiting.internal.analytics.event;

public interface RateLimitingAnalyticsEvent {
    public static final String EVENT_NAME_PREFIX = "rate_limit";

    public String getAnalyticsEventName();
}

