/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raftop.metadata;

import com.hazelcast.cp.internal.CPMemberInfo;
import com.hazelcast.cp.internal.IndeterminateOperationStateAware;
import com.hazelcast.cp.internal.MetadataRaftGroupManager;
import com.hazelcast.cp.internal.RaftServiceDataSerializerHook;
import com.hazelcast.cp.internal.raft.impl.util.PostponedResponse;
import com.hazelcast.cp.internal.raftop.metadata.MetadataRaftGroupOp;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InitMetadataRaftGroupOp
extends MetadataRaftGroupOp
implements IndeterminateOperationStateAware,
IdentifiedDataSerializable {
    private CPMemberInfo callerCPMember;
    private List<CPMemberInfo> discoveredCPMembers;
    private long groupIdSeed;

    public InitMetadataRaftGroupOp() {
    }

    public InitMetadataRaftGroupOp(CPMemberInfo callerCPMember, List<CPMemberInfo> discoveredCPMembers, long groupIdSeed) {
        this.callerCPMember = callerCPMember;
        this.discoveredCPMembers = discoveredCPMembers;
        this.groupIdSeed = groupIdSeed;
    }

    @Override
    public Object run(MetadataRaftGroupManager metadataGroupManager, long commitIndex) {
        if (metadataGroupManager.initMetadataGroup(commitIndex, this.callerCPMember, this.discoveredCPMembers, this.groupIdSeed)) {
            return null;
        }
        return PostponedResponse.INSTANCE;
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
        return 34;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.callerCPMember);
        out.writeInt(this.discoveredCPMembers.size());
        for (CPMemberInfo member : this.discoveredCPMembers) {
            out.writeObject(member);
        }
        out.writeLong(this.groupIdSeed);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.callerCPMember = (CPMemberInfo)in.readObject();
        int len = in.readInt();
        this.discoveredCPMembers = new ArrayList<CPMemberInfo>(len);
        for (int i = 0; i < len; ++i) {
            CPMemberInfo member = (CPMemberInfo)in.readObject();
            this.discoveredCPMembers.add(member);
        }
        this.groupIdSeed = in.readLong();
    }

    @Override
    protected void toString(StringBuilder sb) {
        sb.append(", callerCPMember=").append(this.callerCPMember).append(", discoveredCPMembers=").append(this.discoveredCPMembers).append(", groupIdSeed=").append(this.groupIdSeed);
    }
}

