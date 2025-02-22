/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.quorum.impl;

import com.hazelcast.core.Member;
import com.hazelcast.quorum.QuorumFunction;
import java.util.Collection;

class MemberCountQuorumFunction
implements QuorumFunction {
    private final int quorumSize;

    public MemberCountQuorumFunction(int quorumSize) {
        this.quorumSize = quorumSize;
    }

    @Override
    public boolean apply(Collection<Member> members) {
        return members.size() >= this.quorumSize;
    }
}

