/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.operation;

import com.hazelcast.core.Endpoint;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.exception.CPGroupDestroyedException;
import com.hazelcast.cp.exception.NotLeaderException;
import com.hazelcast.cp.internal.RaftService;
import com.hazelcast.cp.internal.RaftSystemOperation;
import com.hazelcast.cp.internal.raft.impl.RaftNode;
import com.hazelcast.cp.internal.raft.impl.RaftNodeStatus;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;
import java.io.IOException;

public abstract class RaftReplicateOp
extends Operation
implements IdentifiedDataSerializable,
RaftSystemOperation,
ExecutionCallback {
    private CPGroupId groupId;

    RaftReplicateOp() {
    }

    RaftReplicateOp(CPGroupId groupId) {
        this.groupId = groupId;
    }

    @Override
    public final void run() {
        RaftService service = (RaftService)this.getService();
        RaftNode raftNode = service.getOrInitRaftNode(this.groupId);
        if (raftNode == null) {
            if (service.isRaftGroupDestroyed(this.groupId)) {
                this.sendResponse(new CPGroupDestroyedException(this.groupId));
            } else {
                this.sendResponse(new NotLeaderException(this.groupId, (Endpoint)service.getLocalCPMember(), null));
            }
            return;
        }
        if (raftNode.getStatus() == RaftNodeStatus.STEPPED_DOWN) {
            service.stepDownRaftNode(this.groupId);
            this.sendResponse(new NotLeaderException(this.groupId, (Endpoint)service.getLocalCPMember(), null));
            return;
        }
        this.replicate(raftNode).andThen(this);
    }

    protected abstract ICompletableFuture replicate(RaftNode var1);

    public void onResponse(Object response) {
        this.sendResponse(response);
    }

    @Override
    public void onFailure(Throwable t) {
        this.sendResponse(t);
    }

    @Override
    public final boolean returnsResponse() {
        return false;
    }

    @Override
    public final boolean validatesTarget() {
        return false;
    }

    @Override
    public final String getServiceName() {
        return "hz:core:raft";
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.groupId);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.groupId = (CPGroupId)in.readObject();
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", groupId=").append(this.groupId);
    }
}

