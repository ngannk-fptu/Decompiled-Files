/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal;

import com.hazelcast.cp.internal.raft.impl.RaftNode;

public interface RaftNodeAware {
    public void setRaftNode(RaftNode var1);
}

