/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.fd;

import com.hazelcast.core.Member;
import com.hazelcast.internal.cluster.fd.ClusterFailureDetector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DeadlineClusterFailureDetector
implements ClusterFailureDetector {
    private final long maxNoHeartbeatMillis;
    private final ConcurrentMap<Member, Long> heartbeatTimes = new ConcurrentHashMap<Member, Long>();

    public DeadlineClusterFailureDetector(long maxNoHeartbeatMillis) {
        this.maxNoHeartbeatMillis = maxNoHeartbeatMillis;
    }

    @Override
    public void heartbeat(Member member, long timestamp) {
        this.heartbeatTimes.put(member, timestamp);
    }

    @Override
    public boolean isAlive(Member member, long timestamp) {
        long hb = this.lastHeartbeat(member);
        return hb + this.maxNoHeartbeatMillis > timestamp;
    }

    @Override
    public long lastHeartbeat(Member member) {
        Long hb = (Long)this.heartbeatTimes.get(member);
        return hb != null ? hb : 0L;
    }

    @Override
    public double suspicionLevel(Member member, long timestamp) {
        return this.isAlive(member, timestamp) ? 0.0 : 1.0;
    }

    @Override
    public void remove(Member member) {
        this.heartbeatTimes.remove(member);
    }

    @Override
    public void reset() {
        this.heartbeatTimes.clear();
    }
}

