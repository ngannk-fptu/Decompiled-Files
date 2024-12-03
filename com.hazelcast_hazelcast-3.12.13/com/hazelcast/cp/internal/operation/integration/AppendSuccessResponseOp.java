/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.operation.integration;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.RaftService;
import com.hazelcast.cp.internal.operation.integration.AsyncRaftOp;
import com.hazelcast.cp.internal.raft.impl.dto.AppendSuccessResponse;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;

public class AppendSuccessResponseOp
extends AsyncRaftOp {
    private AppendSuccessResponse appendResponse;

    public AppendSuccessResponseOp() {
    }

    public AppendSuccessResponseOp(CPGroupId groupId, AppendSuccessResponse appendResponse) {
        super(groupId);
        this.appendResponse = appendResponse;
    }

    @Override
    public void run() {
        RaftService service = (RaftService)this.getService();
        service.handleAppendResponse(this.groupId, this.appendResponse, this.target);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.appendResponse);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.appendResponse = (AppendSuccessResponse)in.readObject();
    }

    @Override
    public int getId() {
        return 8;
    }
}

