/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.operation.integration;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.RaftService;
import com.hazelcast.cp.internal.operation.integration.AsyncRaftOp;
import com.hazelcast.cp.internal.raft.impl.dto.InstallSnapshot;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;

public class InstallSnapshotOp
extends AsyncRaftOp {
    private InstallSnapshot installSnapshot;

    public InstallSnapshotOp() {
    }

    public InstallSnapshotOp(CPGroupId groupId, InstallSnapshot installSnapshot) {
        super(groupId);
        this.installSnapshot = installSnapshot;
    }

    @Override
    public void run() {
        RaftService service = (RaftService)this.getService();
        service.handleSnapshot(this.groupId, this.installSnapshot, this.target);
    }

    @Override
    public int getId() {
        return 11;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.installSnapshot);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.installSnapshot = (InstallSnapshot)in.readObject();
    }
}

