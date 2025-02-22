/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.operation.integration;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.RaftService;
import com.hazelcast.cp.internal.operation.integration.AsyncRaftOp;
import com.hazelcast.cp.internal.raft.impl.dto.PreVoteResponse;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;

public class PreVoteResponseOp
extends AsyncRaftOp {
    private PreVoteResponse voteResponse;

    public PreVoteResponseOp() {
    }

    public PreVoteResponseOp(CPGroupId groupId, PreVoteResponse voteResponse) {
        super(groupId);
        this.voteResponse = voteResponse;
    }

    @Override
    public void run() {
        RaftService service = (RaftService)this.getService();
        service.handlePreVoteResponse(this.groupId, this.voteResponse, this.target);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.voteResponse);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.voteResponse = (PreVoteResponse)in.readObject();
    }

    @Override
    public int getId() {
        return 4;
    }
}

