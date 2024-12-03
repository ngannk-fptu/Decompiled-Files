/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raftop.metadata;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.IndeterminateOperationStateAware;
import com.hazelcast.cp.internal.MetadataRaftGroupManager;
import com.hazelcast.cp.internal.RaftServiceDataSerializerHook;
import com.hazelcast.cp.internal.raftop.metadata.MetadataRaftGroupOp;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public class GetRaftGroupOp
extends MetadataRaftGroupOp
implements IndeterminateOperationStateAware,
IdentifiedDataSerializable {
    private CPGroupId targetGroupId;

    public GetRaftGroupOp() {
    }

    public GetRaftGroupOp(CPGroupId targetGroupId) {
        this.targetGroupId = targetGroupId;
    }

    @Override
    public Object run(MetadataRaftGroupManager metadataGroupManager, long commitIndex) {
        metadataGroupManager.checkMetadataGroupInitSuccessful();
        return metadataGroupManager.getGroup(this.targetGroupId);
    }

    @Override
    public boolean isRetryableOnIndeterminateOperationState() {
        return true;
    }

    @Override
    public int getFactoryId() {
        return RaftServiceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 25;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.targetGroupId);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.targetGroupId = (CPGroupId)in.readObject();
    }

    @Override
    protected void toString(StringBuilder sb) {
        sb.append(", targetGroupId=").append(this.targetGroupId);
    }
}

