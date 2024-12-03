/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.ratelimiting.cluster;

import com.atlassian.ratelimiting.cluster.RateLimitClusterEvent;
import com.atlassian.sal.api.user.UserKey;

public class RateLimitClusterUserEvent
extends RateLimitClusterEvent {
    private final UserKey userId;

    public UserKey getUserId() {
        return this.userId;
    }

    public RateLimitClusterUserEvent(UserKey userId) {
        this.userId = userId;
    }

    public String toString() {
        return "RateLimitClusterUserEvent(userId=" + this.getUserId() + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RateLimitClusterUserEvent)) {
            return false;
        }
        RateLimitClusterUserEvent other = (RateLimitClusterUserEvent)o;
        if (!other.canEqual(this)) {
            return false;
        }
        UserKey this$userId = this.getUserId();
        UserKey other$userId = other.getUserId();
        return !(this$userId == null ? other$userId != null : !this$userId.equals(other$userId));
    }

    @Override
    protected boolean canEqual(Object other) {
        return other instanceof RateLimitClusterUserEvent;
    }

    @Override
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        UserKey $userId = this.getUserId();
        result = result * 59 + ($userId == null ? 43 : $userId.hashCode());
        return result;
    }
}

