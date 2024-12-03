/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cp.internal.raftop.snapshot;

import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.RaftOp;
import com.hazelcast.cp.internal.RaftServiceDataSerializerHook;
import com.hazelcast.cp.internal.raft.SnapshotAwareService;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import java.io.IOException;

public class RestoreSnapshotOp
extends RaftOp
implements IdentifiedDataSerializable {
    private String serviceName;
    private Object snapshot;

    public RestoreSnapshotOp() {
    }

    public RestoreSnapshotOp(String serviceName, Object snapshot) {
        this.serviceName = serviceName;
        this.snapshot = snapshot;
    }

    public Object getSnapshot() {
        return this.snapshot;
    }

    @Override
    public Object run(CPGroupId groupId, long commitIndex) {
        SnapshotAwareService service = (SnapshotAwareService)this.getService();
        service.restoreSnapshot(groupId, commitIndex, this.snapshot);
        return null;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.serviceName);
        out.writeObject(this.snapshot);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.serviceName = in.readUTF();
        this.snapshot = in.readObject();
    }

    @Override
    public int getFactoryId() {
        return RaftServiceDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 29;
    }

    @Override
    public String getServiceName() {
        return this.serviceName;
    }

    @Override
    protected void toString(StringBuilder sb) {
        sb.append(", snapshot=").append(this.snapshot);
    }
}

