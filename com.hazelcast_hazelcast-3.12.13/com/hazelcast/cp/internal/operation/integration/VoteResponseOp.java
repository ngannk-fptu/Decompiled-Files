/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.operation.integration;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.RaftService;
import com.hazelcast.cp.internal.operation.integration.AsyncRaftOp;
import com.hazelcast.cp.internal.raft.impl.dto.VoteResponse;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;

public class VoteResponseOp
extends AsyncRaftOp {
    private VoteResponse voteResponse;

    public VoteResponseOp() {
    }

    public VoteResponseOp(CPGroupId groupId, VoteResponse voteResponse) {
        super(groupId);
        this.voteResponse = voteResponse;
    }

    @Override
    public void run() {
        RaftService service = (RaftService)this.getService();
        service.handleVoteResponse(this.groupId, this.voteResponse, this.target);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.voteResponse);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.voteResponse = (VoteResponse)in.readObject();
    }

    @Override
    public int getId() {
        return 6;
    }
}

