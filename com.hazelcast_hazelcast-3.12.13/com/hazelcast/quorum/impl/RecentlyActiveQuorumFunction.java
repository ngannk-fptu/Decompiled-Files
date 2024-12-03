/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.quorum.impl;

import com.hazelcast.core.Member;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import com.hazelcast.quorum.HeartbeatAware;
import com.hazelcast.quorum.QuorumFunction;
import com.hazelcast.quorum.impl.AbstractPingAwareQuorumFunction;
import com.hazelcast.util.Clock;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RecentlyActiveQuorumFunction
extends AbstractPingAwareQuorumFunction
implements HeartbeatAware,
QuorumFunction,
MembershipListener {
    private final int quorumSize;
    private final int heartbeatToleranceMillis;
    private final ConcurrentMap<Member, Long> latestHeartbeatPerMember = new ConcurrentHashMap<Member, Long>();

    public RecentlyActiveQuorumFunction(int quorumSize, int heartbeatToleranceMillis) {
        this.quorumSize = quorumSize;
        this.heartbeatToleranceMillis = heartbeatToleranceMillis;
    }

    @Override
    public boolean apply(Collection<Member> members) {
        if (members.size() < this.quorumSize) {
            return false;
        }
        int count = 0;
        long now = Clock.currentTimeMillis();
        for (Member member : members) {
            if (!this.isAlivePerIcmp(member)) continue;
            if (member.localMember()) {
                ++count;
                continue;
            }
            Long latestTimestamp = (Long)this.latestHeartbeatPerMember.get(member);
            if (latestTimestamp == null || now - latestTimestamp >= (long)this.heartbeatToleranceMillis) continue;
            ++count;
        }
        return count >= this.quorumSize;
    }

    @Override
    public void onHeartbeat(Member member, long timestamp) {
        this.latestHeartbeatPerMember.put(member, timestamp);
    }

    @Override
    public void memberRemoved(MembershipEvent membershipEvent) {
        super.memberRemoved(membershipEvent);
        this.latestHeartbeatPerMember.remove(membershipEvent.getMember());
    }

    public int getHeartbeatToleranceMillis() {
        return this.heartbeatToleranceMillis;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RecentlyActiveQuorumFunction)) {
            return false;
        }
        RecentlyActiveQuorumFunction that = (RecentlyActiveQuorumFunction)o;
        if (this.quorumSize != that.quorumSize) {
            return false;
        }
        return this.heartbeatToleranceMillis == that.heartbeatToleranceMillis;
    }

    public int hashCode() {
        int result = this.quorumSize;
        result = 31 * result + this.heartbeatToleranceMillis;
        return result;
    }
}

