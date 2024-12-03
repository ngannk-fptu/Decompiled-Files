/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl.operations;

import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.cluster.impl.operations.AbstractClusterOperation;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;

public class MasterResponseOp
extends AbstractClusterOperation {
    protected Address masterAddress;

    public MasterResponseOp() {
    }

    public MasterResponseOp(Address originAddress) {
        this.masterAddress = originAddress;
    }

    @Override
    public void run() {
        ClusterServiceImpl clusterService = (ClusterServiceImpl)this.getService();
        clusterService.getClusterJoinManager().handleMasterResponse(this.masterAddress, this.getCallerAddress());
    }

    public Address getMasterAddress() {
        return this.masterAddress;
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.masterAddress = new Address();
        this.masterAddress.readData(in);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        this.masterAddress.writeData(out);
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", master=").append(this.masterAddress);
    }

    @Override
    public int getId() {
        return 24;
    }
}

