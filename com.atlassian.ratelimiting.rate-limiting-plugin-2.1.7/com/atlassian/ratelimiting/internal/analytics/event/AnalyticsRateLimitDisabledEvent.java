/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.ratelimiting.internal.analytics.event;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.ratelimiting.internal.analytics.event.RateLimitingAnalyticsEvent;

public class AnalyticsRateLimitDisabledEvent
implements RateLimitingAnalyticsEvent {
    @Override
    @EventName
    public String getAnalyticsEventName() {
        return "rate_limit.disabled";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AnalyticsRateLimitDisabledEvent)) {
            return false;
        }
        AnalyticsRateLimitDisabledEvent other = (AnalyticsRateLimitDisabledEvent)o;
        return other.canEqual(this);
    }

    protected boolean canEqual(Object other) {
        return other instanceof AnalyticsRateLimitDisabledEvent;
    }

    public int hashCode() {
        boolean result = true;
        return 1;
    }
}

