/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.operation;

import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.CallerAware;
import com.hazelcast.cp.internal.IndeterminateOperationStateAware;
import com.hazelcast.cp.internal.RaftOp;
import com.hazelcast.cp.internal.RaftServiceDataSerializerHook;
import com.hazelcast.cp.internal.operation.RaftReplicateOp;
import com.hazelcast.cp.internal.raft.impl.RaftNode;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;

public class DefaultRaftReplicateOp
extends RaftReplicateOp
implements IndeterminateOperationStateAware {
    private RaftOp op;

    public DefaultRaftReplicateOp() {
    }

    public DefaultRaftReplicateOp(CPGroupId groupId, RaftOp op) {
        super(groupId);
        this.op = op;
    }

    @Override
    protected ICompletableFuture replicate(RaftNode raftNode) {
        if (this.op instanceof CallerAware) {
            ((CallerAware)((Object)this.op)).setCaller(this.getCallerAddress(), this.getCallId());
        }
        return raftNode.replicate(this.op);
    }

    @Override
    public boolean isRetryableOnIndeterminateOperationState() {
        if (this.op instanceof IndeterminateOperationStateAware) {
            return ((IndeterminateOperationStateAware)((Object)this.op)).isRetryableOnIndeterminateOperationState();
        }
        return false;
    }

    @Override
    public int getFactoryId() {
        return RaftServiceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 12;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.op);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.op = (RaftOp)in.readObject();
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", op=").append(this.op);
    }
}

