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
import java.io.IOException;

public class HeartbeatComplaintOp
extends AbstractClusterOperation {
    private MembersViewMetadata receiverMembersViewMetadata;
    private MembersViewMetadata senderMembersViewMetadata;

    public HeartbeatComplaintOp() {
    }

    public HeartbeatComplaintOp(MembersViewMetadata receiverMembersViewMetadata, MembersViewMetadata senderMembersViewMetadata) {
        this.receiverMembersViewMetadata = receiverMembersViewMetadata;
        this.senderMembersViewMetadata = senderMembersViewMetadata;
    }

    @Override
    public void run() throws Exception {
        ClusterServiceImpl service = (ClusterServiceImpl)this.getService();
        ClusterHeartbeatManager heartbeatManager = service.getClusterHeartbeatManager();
        heartbeatManager.handleHeartbeatComplaint(this.receiverMembersViewMetadata, this.senderMembersViewMetadata);
    }

    @Override
    public int getId() {
        return 41;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.receiverMembersViewMetadata);
        out.writeObject(this.senderMembersViewMetadata);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.receiverMembersViewMetadata = (MembersViewMetadata)in.readObject();
        this.senderMembersViewMetadata = (MembersViewMetadata)in.readObject();
    }
}

