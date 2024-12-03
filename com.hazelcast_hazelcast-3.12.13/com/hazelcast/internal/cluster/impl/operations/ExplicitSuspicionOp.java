/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl.operations;

import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.cluster.impl.MembersViewMetadata;
import com.hazelcast.internal.cluster.impl.operations.AbstractClusterOperation;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;

public class ExplicitSuspicionOp
extends AbstractClusterOperation {
    private MembersViewMetadata membersViewMetadata;

    public ExplicitSuspicionOp() {
    }

    public ExplicitSuspicionOp(MembersViewMetadata membersViewMetadata) {
        this.membersViewMetadata = membersViewMetadata;
    }

    @Override
    public void run() throws Exception {
        Address suspectedAddress = this.getCallerAddress();
        this.getLogger().info("Received suspicion request from: " + suspectedAddress);
        ClusterServiceImpl clusterService = (ClusterServiceImpl)this.getService();
        clusterService.handleExplicitSuspicion(this.membersViewMetadata, suspectedAddress);
    }

    @Override
    public int getId() {
        return 37;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.membersViewMetadata);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.membersViewMetadata = (MembersViewMetadata)in.readObject();
    }
}

