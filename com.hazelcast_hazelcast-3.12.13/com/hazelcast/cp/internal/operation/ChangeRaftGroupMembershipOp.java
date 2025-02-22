/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.operation;

import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.CPMemberInfo;
import com.hazelcast.cp.internal.IndeterminateOperationStateAware;
import com.hazelcast.cp.internal.RaftServiceDataSerializerHook;
import com.hazelcast.cp.internal.operation.RaftReplicateOp;
import com.hazelcast.cp.internal.raft.MembershipChangeMode;
import com.hazelcast.cp.internal.raft.impl.RaftNode;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public class ChangeRaftGroupMembershipOp
extends RaftReplicateOp
implements IndeterminateOperationStateAware,
IdentifiedDataSerializable {
    private static final int NAN_MEMBERS_COMMIT_INDEX = -1;
    private long membersCommitIndex;
    private CPMemberInfo member;
    private MembershipChangeMode membershipChangeMode;

    public ChangeRaftGroupMembershipOp() {
    }

    public ChangeRaftGroupMembershipOp(CPGroupId groupId, long membersCommitIndex, CPMemberInfo member, MembershipChangeMode membershipChangeMode) {
        super(groupId);
        this.membersCommitIndex = membersCommitIndex;
        this.member = member;
        this.membershipChangeMode = membershipChangeMode;
    }

    @Override
    protected ICompletableFuture replicate(RaftNode raftNode) {
        if (this.membersCommitIndex == -1L) {
            return raftNode.replicateMembershipChange(this.member, this.membershipChangeMode);
        }
        return raftNode.replicateMembershipChange(this.member, this.membershipChangeMode, this.membersCommitIndex);
    }

    @Override
    public boolean isRetryableOnIndeterminateOperationState() {
        return false;
    }

    @Override
    public int getFactoryId() {
        return RaftServiceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 18;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeLong(this.membersCommitIndex);
        out.writeObject(this.member);
        out.writeUTF(this.membershipChangeMode.toString());
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.membersCommitIndex = in.readLong();
        this.member = (CPMemberInfo)in.readObject();
        this.membershipChangeMode = MembershipChangeMode.valueOf(in.readUTF());
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", membersCommitIndex=").append(this.membersCommitIndex).append(", member=").append(this.member).append(", membershipChangeMode=").append((Object)this.membershipChangeMode);
    }
}

