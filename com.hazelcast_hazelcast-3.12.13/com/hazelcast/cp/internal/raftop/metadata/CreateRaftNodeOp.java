/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raftop.metadata;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.CPMemberInfo;
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

public class CreateRaftNodeOp
extends Operation
implements IdentifiedDataSerializable,
RaftSystemOperation {
    private CPGroupId groupId;
    private Collection<CPMemberInfo> initialMembers;

    public CreateRaftNodeOp() {
    }

    public CreateRaftNodeOp(CPGroupId groupId, Collection<CPMemberInfo> initialMembers) {
        this.groupId = groupId;
        this.initialMembers = initialMembers;
    }

    @Override
    public void run() {
        RaftService service = (RaftService)this.getService();
        service.createRaftNode(this.groupId, this.initialMembers);
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
        return 27;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.groupId);
        out.writeInt(this.initialMembers.size());
        for (CPMemberInfo member : this.initialMembers) {
            out.writeObject(member);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.groupId = (CPGroupId)in.readObject();
        int count = in.readInt();
        this.initialMembers = new ArrayList<CPMemberInfo>(count);
        for (int i = 0; i < count; ++i) {
            CPMemberInfo member = (CPMemberInfo)in.readObject();
            this.initialMembers.add(member);
        }
    }

    @Override
    protected void toString(StringBuilder sb) {
        sb.append(", groupId=").append(this.groupId).append(", initialMembers=").append(this.initialMembers);
    }
}

