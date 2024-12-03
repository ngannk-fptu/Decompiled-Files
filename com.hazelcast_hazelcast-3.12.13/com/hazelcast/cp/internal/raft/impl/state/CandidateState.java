/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raft.impl.state;

import com.hazelcast.core.Endpoint;
import java.util.HashSet;
import java.util.Set;

public class CandidateState {
    private final int majority;
    private final Set<Endpoint> voters = new HashSet<Endpoint>();

    CandidateState(int majority) {
        this.majority = majority;
    }

    public boolean grantVote(Endpoint address) {
        return this.voters.add(address);
    }

    public int majority() {
        return this.majority;
    }

    public int voteCount() {
        return this.voters.size();
    }

    public boolean isMajorityGranted() {
        return this.voteCount() >= this.majority();
    }
}

