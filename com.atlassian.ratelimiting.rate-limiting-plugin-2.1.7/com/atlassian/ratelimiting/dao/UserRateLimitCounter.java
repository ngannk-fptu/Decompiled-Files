/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.ratelimiting.dao;

import com.atlassian.sal.api.user.UserKey;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class UserRateLimitCounter {
    private final long id;
    private final String nodeId;
    private final LocalDateTime intervalStart;
    private final long rejectCount;
    private final UserKey user;

    public UserRateLimitCounterBuilder copy() {
        return UserRateLimitCounter.builder().id(this.id).nodeId(this.nodeId).intervalStart(this.intervalStart).rejectCount(this.rejectCount).user(this.user);
    }

    UserRateLimitCounter(long id, String nodeId, LocalDateTime intervalStart, long rejectCount, UserKey user) {
        this.id = id;
        this.nodeId = nodeId;
        this.intervalStart = intervalStart;
        this.rejectCount = rejectCount;
        this.user = user;
    }

    public static UserRateLimitCounterBuilder builder() {
        return new UserRateLimitCounterBuilder();
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof UserRateLimitCounter)) {
            return false;
        }
        UserRateLimitCounter other = (UserRateLimitCounter)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getId() != other.getId()) {
            return false;
        }
        if (this.getRejectCount() != other.getRejectCount()) {
            return false;
        }
        String this$nodeId = this.getNodeId();
        String other$nodeId = other.getNodeId();
        if (this$nodeId == null ? other$nodeId != null : !this$nodeId.equals(other$nodeId)) {
            return false;
        }
        LocalDateTime this$intervalStart = this.getIntervalStart();
        LocalDateTime other$intervalStart = other.getIntervalStart();
        if (this$intervalStart == null ? other$intervalStart != null : !((Object)this$intervalStart).equals(other$intervalStart)) {
            return false;
        }
        UserKey this$user = this.getUser();
        UserKey other$user = other.getUser();
        return !(this$user == null ? other$user != null : !this$user.equals(other$user));
    }

    protected boolean canEqual(Object other) {
        return other instanceof UserRateLimitCounter;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $id = this.getId();
        result = result * 59 + (int)($id >>> 32 ^ $id);
        long $rejectCount = this.getRejectCount();
        result = result * 59 + (int)($rejectCount >>> 32 ^ $rejectCount);
        String $nodeId = this.getNodeId();
        result = result * 59 + ($nodeId == null ? 43 : $nodeId.hashCode());
        LocalDateTime $intervalStart = this.getIntervalStart();
        result = result * 59 + ($intervalStart == null ? 43 : ((Object)$intervalStart).hashCode());
        UserKey $user = this.getUser();
        result = result * 59 + ($user == null ? 43 : $user.hashCode());
        return result;
    }

    public long getId() {
        return this.id;
    }

    public String getNodeId() {
        return this.nodeId;
    }

    public LocalDateTime getIntervalStart() {
        return this.intervalStart;
    }

    public long getRejectCount() {
        return this.rejectCount;
    }

    public UserKey getUser() {
        return this.user;
    }

    public String toString() {
        return "UserRateLimitCounter(id=" + this.getId() + ", nodeId=" + this.getNodeId() + ", intervalStart=" + this.getIntervalStart() + ", rejectCount=" + this.getRejectCount() + ", user=" + this.getUser() + ")";
    }

    public static class UserRateLimitCounterBuilder {
        private long id;
        private String nodeId;
        private LocalDateTime intervalStart;
        private long rejectCount;
        private UserKey user;

        public UserRateLimitCounterBuilder intervalStart(LocalDateTime intervalStart) {
            this.intervalStart = intervalStart.truncatedTo(ChronoUnit.MILLIS);
            return this;
        }

        UserRateLimitCounterBuilder() {
        }

        public UserRateLimitCounterBuilder id(long id) {
            this.id = id;
            return this;
        }

        public UserRateLimitCounterBuilder nodeId(String nodeId) {
            this.nodeId = nodeId;
            return this;
        }

        public UserRateLimitCounterBuilder rejectCount(long rejectCount) {
            this.rejectCount = rejectCount;
            return this;
        }

        public UserRateLimitCounterBuilder user(UserKey user) {
            this.user = user;
            return this;
        }

        public UserRateLimitCounter build() {
            return new UserRateLimitCounter(this.id, this.nodeId, this.intervalStart, this.rejectCount, this.user);
        }

        public String toString() {
            return "UserRateLimitCounter.UserRateLimitCounterBuilder(id=" + this.id + ", nodeId=" + this.nodeId + ", intervalStart=" + this.intervalStart + ", rejectCount=" + this.rejectCount + ", user=" + this.user + ")";
        }
    }
}

