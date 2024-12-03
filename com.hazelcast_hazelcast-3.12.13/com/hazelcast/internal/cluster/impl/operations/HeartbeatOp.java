/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl.operations;

import com.hazelcast.internal.cluster.impl.ClusterHeartbeatManager;
import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.cluster.impl.MembersViewMetadata;
import com.hazelcast.internal.cluster.impl.operations.AbstractClusterOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.impl.Versioned;
import java.io.IOException;

public final class HeartbeatOp
extends AbstractClusterOperation
implements Versioned {
    private MembersViewMetadata senderMembersViewMetadata;
    private String targetUuid;
    private long timestamp;

    public HeartbeatOp() {
    }

    public HeartbeatOp(MembersViewMetadata senderMembersViewMetadata, String targetUuid, long timestamp) {
        this.senderMembersViewMetadata = senderMembersViewMetadata;
        this.targetUuid = targetUuid;
        this.timestamp = timestamp;
    }

    @Override
    public void run() {
        ClusterServiceImpl service = (ClusterServiceImpl)this.getService();
        ClusterHeartbeatManager heartbeatManager = service.getClusterHeartbeatManager();
        heartbeatManager.handleHeartbeat(this.senderMembersViewMetadata, this.targetUuid, this.timestamp);
    }

    @Override
    public int getId() {
        return 3;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.senderMembersViewMetadata);
        out.writeUTF(this.targetUuid);
        out.writeLong(this.timestamp);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.senderMembersViewMetadata = (MembersViewMetadata)in.readObject();
        this.targetUuid = in.readUTF();
        this.timestamp = in.readLong();
    }
}

