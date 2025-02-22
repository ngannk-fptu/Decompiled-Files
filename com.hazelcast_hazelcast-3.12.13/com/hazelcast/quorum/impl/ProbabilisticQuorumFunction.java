/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.quorum.impl;

import com.hazelcast.core.Member;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.core.MembershipListener;
import com.hazelcast.internal.cluster.fd.PhiAccrualClusterFailureDetector;
import com.hazelcast.quorum.HeartbeatAware;
import com.hazelcast.quorum.QuorumFunction;
import com.hazelcast.quorum.impl.AbstractPingAwareQuorumFunction;
import com.hazelcast.util.Clock;
import java.util.Collection;

public class ProbabilisticQuorumFunction
extends AbstractPingAwareQuorumFunction
implements HeartbeatAware,
QuorumFunction,
MembershipListener {
    private final double suspicionThreshold;
    private final int quorumSize;
    private final int maxSampleSize;
    private final long minStdDeviationMillis;
    private final long acceptableHeartbeatPauseMillis;
    private final long heartbeatIntervalMillis;
    private final PhiAccrualClusterFailureDetector failureDetector;

    public ProbabilisticQuorumFunction(int quorumSize, long heartbeatIntervalMillis, long acceptableHeartbeatPauseMillis, int maxSampleSize, long minStdDeviationMillis, double suspicionThreshold) {
        this.heartbeatIntervalMillis = heartbeatIntervalMillis;
        this.acceptableHeartbeatPauseMillis = acceptableHeartbeatPauseMillis;
        this.maxSampleSize = maxSampleSize;
        this.minStdDeviationMillis = minStdDeviationMillis;
        this.suspicionThreshold = suspicionThreshold;
        this.quorumSize = quorumSize;
        this.failureDetector = new PhiAccrualClusterFailureDetector(acceptableHeartbeatPauseMillis, heartbeatIntervalMillis, suspicionThreshold, maxSampleSize, minStdDeviationMillis);
    }

    @Override
    public boolean apply(Collection<Member> members) {
        if (members.size() < this.quorumSize) {
            return false;
        }
        int count = 0;
        long timestamp = Clock.currentTimeMillis();
        for (Member member : members) {
            if (!this.isAlivePerIcmp(member) || !member.localMember() && !this.failureDetector.isAlive(member, timestamp)) continue;
            ++count;
        }
        return count >= this.quorumSize;
    }

    @Override
    public void memberRemoved(MembershipEvent membershipEvent) {
        super.memberRemoved(membershipEvent);
        this.failureDetector.remove(membershipEvent.getMember());
    }

    @Override
    public void onHeartbeat(Member member, long timestamp) {
        this.failureDetector.heartbeat(member, timestamp);
    }

    public double getSuspicionThreshold() {
        return this.suspicionThreshold;
    }

    public int getMaxSampleSize() {
        return this.maxSampleSize;
    }

    public long getMinStdDeviationMillis() {
        return this.minStdDeviationMillis;
    }

    public long getAcceptableHeartbeatPauseMillis() {
        return this.acceptableHeartbeatPauseMillis;
    }

    public long getHeartbeatIntervalMillis() {
        return this.heartbeatIntervalMillis;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ProbabilisticQuorumFunction that = (ProbabilisticQuorumFunction)o;
        if (Double.compare(that.suspicionThreshold, this.suspicionThreshold) != 0) {
            return false;
        }
        if (this.quorumSize != that.quorumSize) {
            return false;
        }
        if (this.maxSampleSize != that.maxSampleSize) {
            return false;
        }
        if (this.minStdDeviationMillis != that.minStdDeviationMillis) {
            return false;
        }
        if (this.acceptableHeartbeatPauseMillis != that.acceptableHeartbeatPauseMillis) {
            return false;
        }
        return this.heartbeatIntervalMillis == that.heartbeatIntervalMillis;
    }

    public int hashCode() {
        long temp = Double.doubleToLongBits(this.suspicionThreshold);
        int result = (int)(temp ^ temp >>> 32);
        result = 31 * result + this.quorumSize;
        result = 31 * result + this.maxSampleSize;
        result = 31 * result + (int)(this.minStdDeviationMillis ^ this.minStdDeviationMillis >>> 32);
        result = 31 * result + (int)(this.acceptableHeartbeatPauseMillis ^ this.acceptableHeartbeatPauseMillis >>> 32);
        result = 31 * result + (int)(this.heartbeatIntervalMillis ^ this.heartbeatIntervalMillis >>> 32);
        return result;
    }
}

