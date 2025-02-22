/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raftop.metadata;

import com.hazelcast.cp.internal.CPMemberInfo;
import com.hazelcast.cp.internal.RaftGroupId;
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

public class PublishActiveCPMembersOp
extends Operation
implements IdentifiedDataSerializable,
RaftSystemOperation {
    private RaftGroupId metadataGroupId;
    private long membersCommitIndex;
    private Collection<CPMemberInfo> members;

    public PublishActiveCPMembersOp() {
    }

    public PublishActiveCPMembersOp(RaftGroupId metadataGroupId, long membersCommitIndex, Collection<CPMemberInfo> members) {
        this.metadataGroupId = metadataGroupId;
        this.membersCommitIndex = membersCommitIndex;
        this.members = members;
    }

    @Override
    public void run() {
        RaftService service = (RaftService)this.getService();
        service.handleActiveCPMembers(this.metadataGroupId, this.membersCommitIndex, this.members);
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
        return 32;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.metadataGroupId);
        out.writeLong(this.membersCommitIndex);
        out.writeInt(this.members.size());
        for (CPMemberInfo member : this.members) {
            out.writeObject(member);
        }
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.metadataGroupId = (RaftGroupId)in.readObject();
        this.membersCommitIndex = in.readLong();
        int len = in.readInt();
        this.members = new ArrayList<CPMemberInfo>(len);
        for (int i = 0; i < len; ++i) {
            CPMemberInfo member = (CPMemberInfo)in.readObject();
            this.members.add(member);
        }
    }

    @Override
    protected void toString(StringBuilder sb) {
        sb.append(", metadataGroupId=").append(this.metadataGroupId).append(", membersCommitIndex").append(this.membersCommitIndex).append(", members=").append(this.members);
    }
}

