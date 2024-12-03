/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ratelimiting.cluster;

public class RateLimitClusterEvent {
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RateLimitClusterEvent)) {
            return false;
        }
        RateLimitClusterEvent other = (RateLimitClusterEvent)o;
        return other.canEqual(this);
    }

    protected boolean canEqual(Object other) {
        return other instanceof RateLimitClusterEvent;
    }

    public int hashCode() {
        boolean result = true;
        return 1;
    }
}

