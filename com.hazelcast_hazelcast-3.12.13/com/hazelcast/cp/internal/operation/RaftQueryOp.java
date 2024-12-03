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
import com.hazelcast.cp.internal.IndeterminateOperationStateAware;
import com.hazelcast.cp.internal.RaftNodeAware;
import com.hazelcast.cp.internal.RaftOp;
import com.hazelcast.cp.internal.RaftService;
import com.hazelcast.cp.internal.RaftServiceDataSerializerHook;
import com.hazelcast.cp.internal.RaftSystemOperation;
import com.hazelcast.cp.internal.raft.QueryPolicy;
import com.hazelcast.cp.internal.raft.impl.RaftNode;
import com.hazelcast.cp.internal.raft.impl.RaftNodeStatus;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;
import java.io.IOException;

public class RaftQueryOp
extends Operation
implements IndeterminateOperationStateAware,
RaftSystemOperation,
ExecutionCallback,
IdentifiedDataSerializable {
    private CPGroupId groupId;
    private QueryPolicy queryPolicy;
    private Object op;

    public RaftQueryOp() {
    }

    public RaftQueryOp(CPGroupId groupId, RaftOp raftOp, QueryPolicy queryPolicy) {
        this.groupId = groupId;
        this.op = raftOp;
        this.queryPolicy = queryPolicy;
    }

    @Override
    public final void run() {
        RaftService service = (RaftService)this.getService();
        RaftNode raftNode = service.getRaftNode(this.groupId);
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
        if (this.op instanceof RaftNodeAware) {
            ((RaftNodeAware)this.op).setRaftNode(raftNode);
        }
        ICompletableFuture future = raftNode.query(this.op, this.queryPolicy);
        future.andThen(this);
    }

    @Override
    public boolean isRetryableOnIndeterminateOperationState() {
        return true;
    }

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
    public int getFactoryId() {
        return RaftServiceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 20;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.groupId);
        out.writeObject(this.op);
        out.writeUTF(this.queryPolicy.toString());
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.groupId = (CPGroupId)in.readObject();
        this.op = in.readObject();
        this.queryPolicy = QueryPolicy.valueOf(in.readUTF());
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", op=").append(this.op).append(", groupId=").append(this.groupId).append(", policy=").append((Object)this.queryPolicy);
    }
}

