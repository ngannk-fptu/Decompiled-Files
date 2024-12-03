/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.ratelimiting.internal.analytics.event;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.ratelimiting.internal.analytics.event.RateLimitingAnalyticsEvent;

public class AnalyticsRateLimitEnabledEvent
implements RateLimitingAnalyticsEvent {
    @Override
    @EventName
    public String getAnalyticsEventName() {
        return "rate_limit.enabled";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AnalyticsRateLimitEnabledEvent)) {
            return false;
        }
        AnalyticsRateLimitEnabledEvent other = (AnalyticsRateLimitEnabledEvent)o;
        return other.canEqual(this);
    }

    protected boolean canEqual(Object other) {
        return other instanceof AnalyticsRateLimitEnabledEvent;
    }

    public int hashCode() {
        boolean result = true;
        return 1;
    }
}

