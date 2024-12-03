/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.operation.integration;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.RaftService;
import com.hazelcast.cp.internal.operation.integration.AsyncRaftOp;
import com.hazelcast.cp.internal.raft.impl.dto.VoteRequest;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;

public class VoteRequestOp
extends AsyncRaftOp {
    private VoteRequest voteRequest;

    public VoteRequestOp() {
    }

    public VoteRequestOp(CPGroupId groupId, VoteRequest voteRequest) {
        super(groupId);
        this.voteRequest = voteRequest;
    }

    @Override
    public void run() {
        RaftService service = (RaftService)this.getService();
        service.handleVoteRequest(this.groupId, this.voteRequest, this.target);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.voteRequest);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.voteRequest = (VoteRequest)in.readObject();
    }

    @Override
    public int getId() {
        return 5;
    }
}

