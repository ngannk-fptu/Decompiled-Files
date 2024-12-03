/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raftop.metadata;

import com.hazelcast.cp.internal.CPMemberInfo;
import com.hazelcast.cp.internal.IndeterminateOperationStateAware;
import com.hazelcast.cp.internal.MetadataRaftGroupManager;
import com.hazelcast.cp.internal.RaftServiceDataSerializerHook;
import com.hazelcast.cp.internal.raftop.metadata.MetadataRaftGroupOp;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class CreateRaftGroupOp
extends MetadataRaftGroupOp
implements IndeterminateOperationStateAware,
IdentifiedDataSerializable {
    private String groupName;
    private Collection<CPMemberInfo> members;

    public CreateRaftGroupOp() {
    }

    public CreateRaftGroupOp(String groupName, Collection<CPMemberInfo> members) {
        this.groupName = groupName;
        this.members = members;
    }

    @Override
    public Object run(MetadataRaftGroupManager metadataGroupManager, long commitIndex) {
        return metadataGroupManager.createRaftGroup(this.groupName, this.members, commitIndex);
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
        return 13;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.groupName);
        out.writeInt(this.members.size());
        for (CPMemberInfo member : this.members) {
            out.writeObject(member);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.groupName = in.readUTF();
        int len = in.readInt();
        this.members = new ArrayList<CPMemberInfo>(len);
        for (int i = 0; i < len; ++i) {
            CPMemberInfo member = (CPMemberInfo)in.readObject();
            this.members.add(member);
        }
    }

    @Override
    protected void toString(StringBuilder sb) {
        sb.append(", groupName=").append(this.groupName).append(", members=").append(this.members);
    }
}

