/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raftop.metadata;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.IndeterminateOperationStateAware;
import com.hazelcast.cp.internal.MetadataRaftGroupManager;
import com.hazelcast.cp.internal.RaftServiceDataSerializerHook;
import com.hazelcast.cp.internal.raftop.metadata.MetadataRaftGroupOp;
import com.hazelcast.cp.internal.util.Tuple2;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CompleteRaftGroupMembershipChangesOp
extends MetadataRaftGroupOp
implements IndeterminateOperationStateAware,
IdentifiedDataSerializable {
    private Map<CPGroupId, Tuple2<Long, Long>> changedGroups;

    public CompleteRaftGroupMembershipChangesOp() {
    }

    public CompleteRaftGroupMembershipChangesOp(Map<CPGroupId, Tuple2<Long, Long>> changedGroups) {
        this.changedGroups = changedGroups;
    }

    @Override
    public Object run(MetadataRaftGroupManager metadataGroupManager, long commitIndex) {
        return metadataGroupManager.completeRaftGroupMembershipChanges(commitIndex, this.changedGroups);
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
        return 17;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.changedGroups.size());
        for (Map.Entry<CPGroupId, Tuple2<Long, Long>> e : this.changedGroups.entrySet()) {
            out.writeObject(e.getKey());
            Tuple2<Long, Long> value = e.getValue();
            out.writeLong((Long)value.element1);
            out.writeLong((Long)value.element2);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        int count = in.readInt();
        this.changedGroups = new HashMap<CPGroupId, Tuple2<Long, Long>>(count);
        for (int i = 0; i < count; ++i) {
            CPGroupId groupId = (CPGroupId)in.readObject();
            long currMembersCommitIndex = in.readLong();
            long newMembersCommitIndex = in.readLong();
            this.changedGroups.put(groupId, Tuple2.of(currMembersCommitIndex, newMembersCommitIndex));
        }
    }

    @Override
    protected void toString(StringBuilder sb) {
        sb.append(", changedGroups=").append(this.changedGroups);
    }
}

