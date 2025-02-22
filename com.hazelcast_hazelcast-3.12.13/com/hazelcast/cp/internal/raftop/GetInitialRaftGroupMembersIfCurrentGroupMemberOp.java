/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raftop;

import com.hazelcast.core.Endpoint;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.IndeterminateOperationStateAware;
import com.hazelcast.cp.internal.RaftNodeAware;
import com.hazelcast.cp.internal.RaftOp;
import com.hazelcast.cp.internal.RaftServiceDataSerializerHook;
import com.hazelcast.cp.internal.raft.impl.RaftNode;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.Preconditions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class GetInitialRaftGroupMembersIfCurrentGroupMemberOp
extends RaftOp
implements RaftNodeAware,
IndeterminateOperationStateAware,
IdentifiedDataSerializable {
    private Endpoint cpMember;
    private RaftNode raftNode;

    public GetInitialRaftGroupMembersIfCurrentGroupMemberOp() {
    }

    public GetInitialRaftGroupMembersIfCurrentGroupMemberOp(Endpoint cpMember) {
        this.cpMember = cpMember;
    }

    @Override
    public void setRaftNode(RaftNode raftNode) {
        this.raftNode = raftNode;
    }

    @Override
    public Object run(CPGroupId groupId, long commitIndex) {
        Preconditions.checkState(this.raftNode != null, "RaftNode is not injected in " + groupId);
        Collection<Endpoint> members = this.raftNode.getAppliedMembers();
        Preconditions.checkState(members.contains(this.cpMember), this.cpMember + " is not in the current committed member list: " + members + " of " + groupId);
        return new ArrayList<Endpoint>(this.raftNode.getInitialMembers());
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
        return 36;
    }

    @Override
    protected String getServiceName() {
        return "hz:core:raft";
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.cpMember);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.cpMember = (Endpoint)in.readObject();
    }
}

