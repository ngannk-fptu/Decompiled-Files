/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ratelimiting.internal.analytics.event;

import com.atlassian.ratelimiting.internal.analytics.event.RateLimitingAnalyticsEvent;

public class AnalyticsRejectedRequestCountEvent
implements RateLimitingAnalyticsEvent {
    private final long amount;

    @Override
    public String getAnalyticsEventName() {
        return "rate_limit.requests.rejected";
    }

    public long getAmount() {
        return this.amount;
    }

    public AnalyticsRejectedRequestCountEvent(long amount) {
        this.amount = amount;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof AnalyticsRejectedRequestCountEvent)) {
            return false;
        }
        AnalyticsRejectedRequestCountEvent other = (AnalyticsRejectedRequestCountEvent)o;
        if (!other.canEqual(this)) {
            return false;
        }
        return this.getAmount() == other.getAmount();
    }

    protected boolean canEqual(Object other) {
        return other instanceof AnalyticsRejectedRequestCountEvent;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $amount = this.getAmount();
        result = result * 59 + (int)($amount >>> 32 ^ $amount);
        return result;
    }

    public String toString() {
        return "AnalyticsRejectedRequestCountEvent(amount=" + this.getAmount() + ")";
    }
}

