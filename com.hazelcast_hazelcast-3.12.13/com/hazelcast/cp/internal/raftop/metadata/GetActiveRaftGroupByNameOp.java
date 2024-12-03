/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raftop.metadata;

import com.hazelcast.cp.internal.IndeterminateOperationStateAware;
import com.hazelcast.cp.internal.MetadataRaftGroupManager;
import com.hazelcast.cp.internal.RaftServiceDataSerializerHook;
import com.hazelcast.cp.internal.raftop.metadata.MetadataRaftGroupOp;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public class GetActiveRaftGroupByNameOp
extends MetadataRaftGroupOp
implements IndeterminateOperationStateAware,
IdentifiedDataSerializable {
    private String groupName;

    public GetActiveRaftGroupByNameOp() {
    }

    public GetActiveRaftGroupByNameOp(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public Object run(MetadataRaftGroupManager metadataGroupManager, long commitIndex) throws Exception {
        metadataGroupManager.checkMetadataGroupInitSuccessful();
        return metadataGroupManager.getActiveGroup(this.groupName);
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
        return 26;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.groupName);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.groupName = in.readUTF();
    }

    @Override
    protected void toString(StringBuilder sb) {
        sb.append(", groupName=").append(this.groupName);
    }
}

