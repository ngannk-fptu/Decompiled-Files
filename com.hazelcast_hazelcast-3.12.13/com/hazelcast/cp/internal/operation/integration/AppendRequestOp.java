/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.operation.integration;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.RaftService;
import com.hazelcast.cp.internal.operation.integration.AsyncRaftOp;
import com.hazelcast.cp.internal.raft.impl.dto.AppendRequest;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;

public class AppendRequestOp
extends AsyncRaftOp {
    private AppendRequest appendRequest;

    public AppendRequestOp() {
    }

    public AppendRequestOp(CPGroupId groupId, AppendRequest appendRequest) {
        super(groupId);
        this.appendRequest = appendRequest;
    }

    @Override
    public void run() {
        RaftService service = (RaftService)this.getService();
        service.handleAppendEntries(this.groupId, this.appendRequest, this.target);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.appendRequest);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.appendRequest = (AppendRequest)in.readObject();
    }

    @Override
    public int getId() {
        return 7;
    }
}

