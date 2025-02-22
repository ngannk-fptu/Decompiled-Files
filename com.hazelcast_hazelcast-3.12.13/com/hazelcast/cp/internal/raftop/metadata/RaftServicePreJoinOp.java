/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raftop.metadata;

import com.hazelcast.cp.internal.MetadataRaftGroupManager;
import com.hazelcast.cp.internal.RaftGroupId;
import com.hazelcast.cp.internal.RaftService;
import com.hazelcast.cp.internal.RaftServiceDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.nio.serialization.impl.Versioned;
import com.hazelcast.spi.Operation;
import java.io.IOException;

public class RaftServicePreJoinOp
extends Operation
implements IdentifiedDataSerializable,
Versioned {
    private boolean discoveryCompleted;
    private RaftGroupId metadataGroupId;

    public RaftServicePreJoinOp() {
    }

    public RaftServicePreJoinOp(boolean discoveryCompleted, RaftGroupId metadataGroupId) {
        this.discoveryCompleted = discoveryCompleted;
        this.metadataGroupId = metadataGroupId;
    }

    @Override
    public void run() {
        RaftService service = (RaftService)this.getService();
        MetadataRaftGroupManager metadataGroupManager = service.getMetadataGroupManager();
        metadataGroupManager.handleMetadataGroupId(this.metadataGroupId);
        if (this.discoveryCompleted) {
            metadataGroupManager.disableDiscovery();
        }
    }

    @Override
    public boolean returnsResponse() {
        return false;
    }

    @Override
    public String getServiceName() {
        return "hz:core:raft";
    }

    @Override
    public int getFactoryId() {
        return RaftServiceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 39;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeBoolean(this.discoveryCompleted);
        out.writeObject(this.metadataGroupId);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.discoveryCompleted = in.readBoolean();
        this.metadataGroupId = (RaftGroupId)in.readObject();
    }

    @Override
    protected void toString(StringBuilder sb) {
        sb.append(", discoveryCompleted=").append(this.discoveryCompleted).append(", metadataGroupId=").append(this.metadataGroupId);
    }
}

