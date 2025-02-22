/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raft.impl.state;

import com.hazelcast.core.Endpoint;
import com.hazelcast.cp.internal.raft.impl.state.FollowerState;
import com.hazelcast.util.Clock;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class LeaderState {
    private final Map<Endpoint, FollowerState> followerStates = new HashMap<Endpoint, FollowerState>();

    LeaderState(Collection<Endpoint> remoteMembers, long lastLogIndex) {
        for (Endpoint follower : remoteMembers) {
            this.followerStates.put(follower, new FollowerState(0L, lastLogIndex + 1L));
        }
    }

    public void add(Endpoint follower, long lastLogIndex) {
        assert (!this.followerStates.containsKey(follower)) : "Already known follower " + follower;
        this.followerStates.put(follower, new FollowerState(0L, lastLogIndex + 1L));
    }

    public void remove(Endpoint follower) {
        FollowerState removed = this.followerStates.remove(follower);
        assert (removed != null) : "Unknown follower " + follower;
    }

    public long[] matchIndices() {
        long[] indices = new long[this.followerStates.size() + 1];
        int ix = 0;
        for (FollowerState state : this.followerStates.values()) {
            indices[ix++] = state.matchIndex();
        }
        return indices;
    }

    public FollowerState getFollowerState(Endpoint follower) {
        FollowerState followerState = this.followerStates.get(follower);
        assert (followerState != null) : "Unknown follower " + follower;
        return followerState;
    }

    public Map<Endpoint, FollowerState> getFollowerStates() {
        return this.followerStates;
    }

    public long majorityAppendRequestAckTimestamp(int majority) {
        long[] ackTimes = new long[this.followerStates.size() + 1];
        int i = 0;
        ackTimes[i] = Clock.currentTimeMillis();
        for (FollowerState followerState : this.followerStates.values()) {
            ackTimes[++i] = followerState.appendRequestAckTimestamp();
        }
        Arrays.sort(ackTimes);
        return ackTimes[ackTimes.length - majority];
    }
}

