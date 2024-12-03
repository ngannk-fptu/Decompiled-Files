/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raftop.metadata;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.RaftService;
import com.hazelcast.cp.internal.RaftServiceDataSerializerHook;
import com.hazelcast.cp.internal.RaftSystemOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class DestroyRaftNodesOp
extends Operation
implements IdentifiedDataSerializable,
RaftSystemOperation {
    private Collection<CPGroupId> groupIds;

    public DestroyRaftNodesOp() {
    }

    public DestroyRaftNodesOp(Collection<CPGroupId> groupIds) {
        this.groupIds = groupIds;
    }

    @Override
    public void run() {
        RaftService service = (RaftService)this.getService();
        for (CPGroupId groupId : this.groupIds) {
            service.destroyRaftNode(groupId);
        }
    }

    @Override
    public String getServiceName() {
        return "hz:core:raft";
    }

    @Override
    public boolean returnsResponse() {
        return false;
    }

    @Override
    public int getFactoryId() {
        return RaftServiceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 21;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeInt(this.groupIds.size());
        for (CPGroupId groupId : this.groupIds) {
            out.writeObject(groupId);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        int count = in.readInt();
        this.groupIds = new ArrayList<CPGroupId>();
        for (int i = 0; i < count; ++i) {
            CPGroupId groupId = (CPGroupId)in.readObject();
            this.groupIds.add(groupId);
        }
    }

    @Override
    protected void toString(StringBuilder sb) {
        sb.append(", groupIds=").append(this.groupIds);
    }
}

