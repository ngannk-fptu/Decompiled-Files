/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.quorum;

import com.hazelcast.quorum.Quorum;
import com.hazelcast.quorum.QuorumException;
import com.hazelcast.quorum.QuorumType;

public interface QuorumService {
    public Quorum getQuorum(String var1) throws IllegalArgumentException;

    public void ensureQuorumPresent(String var1, QuorumType var2) throws QuorumException;
}

