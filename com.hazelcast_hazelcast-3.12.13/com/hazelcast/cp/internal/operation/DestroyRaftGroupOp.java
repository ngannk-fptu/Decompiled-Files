/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.operation;

import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.IndeterminateOperationStateAware;
import com.hazelcast.cp.internal.RaftServiceDataSerializerHook;
import com.hazelcast.cp.internal.operation.RaftReplicateOp;
import com.hazelcast.cp.internal.raft.command.DestroyRaftGroupCmd;
import com.hazelcast.cp.internal.raft.impl.RaftNode;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

public class DestroyRaftGroupOp
extends RaftReplicateOp
implements IndeterminateOperationStateAware,
IdentifiedDataSerializable {
    public DestroyRaftGroupOp() {
    }

    public DestroyRaftGroupOp(CPGroupId groupId) {
        super(groupId);
    }

    @Override
    protected ICompletableFuture replicate(RaftNode raftNode) {
        return raftNode.replicate(new DestroyRaftGroupCmd());
    }

    @Override
    public boolean isRetryableOnIndeterminateOperationState() {
        return false;
    }

    @Override
    public int getFactoryId() {
        return RaftServiceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 28;
    }
}

