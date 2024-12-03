/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.ratelimiting.history;

import com.atlassian.sal.api.user.UserKey;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class UserRateLimitingReport {
    private final UserKey user;
    private final LocalDateTime lastRejectedTime;
    private final long rejectCount;
    private final boolean hasExemption;

    public UserRateLimitingReport(UserKey user, LocalDateTime lastRejectedTime, long rejectCount, boolean hasExemption) {
        this.user = user;
        this.lastRejectedTime = lastRejectedTime.truncatedTo(ChronoUnit.MILLIS);
        this.rejectCount = rejectCount;
        this.hasExemption = hasExemption;
    }

    public UserKey getUser() {
        return this.user;
    }

    public LocalDateTime getLastRejectedTime() {
        return this.lastRejectedTime;
    }

    public long getRejectCount() {
        return this.rejectCount;
    }

    public boolean isHasExemption() {
        return this.hasExemption;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UserRateLimitingReport)) {
            return false;
        }
        UserRateLimitingReport other = (UserRateLimitingReport)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getRejectCount() != other.getRejectCount()) {
            return false;
        }
        if (this.isHasExemption() != other.isHasExemption()) {
            return false;
        }
        UserKey this$user = this.getUser();
        UserKey other$user = other.getUser();
        if (this$user == null ? other$user != null : !this$user.equals(other$user)) {
            return false;
        }
        LocalDateTime this$lastRejectedTime = this.getLastRejectedTime();
        LocalDateTime other$lastRejectedTime = other.getLastRejectedTime();
        return !(this$lastRejectedTime == null ? other$lastRejectedTime != null : !((Object)this$lastRejectedTime).equals(other$lastRejectedTime));
    }

    protected boolean canEqual(Object other) {
        return other instanceof UserRateLimitingReport;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $rejectCount = this.getRejectCount();
        result = result * 59 + (int)($rejectCount >>> 32 ^ $rejectCount);
        result = result * 59 + (this.isHasExemption() ? 79 : 97);
        UserKey $user = this.getUser();
        result = result * 59 + ($user == null ? 43 : $user.hashCode());
        LocalDateTime $lastRejectedTime = this.getLastRejectedTime();
        result = result * 59 + ($lastRejectedTime == null ? 43 : ((Object)$lastRejectedTime).hashCode());
        return result;
    }

    public String toString() {
        return "UserRateLimitingReport(user=" + this.getUser() + ", lastRejectedTime=" + this.getLastRejectedTime() + ", rejectCount=" + this.getRejectCount() + ", hasExemption=" + this.isHasExemption() + ")";
    }
}

