/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl.operations;

import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.cluster.impl.JoinRequest;
import com.hazelcast.internal.cluster.impl.operations.AbstractClusterOperation;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import java.io.IOException;

public class JoinRequestOp
extends AbstractClusterOperation {
    private JoinRequest request;

    public JoinRequestOp() {
    }

    public JoinRequestOp(JoinRequest request) {
        this.request = request;
    }

    @Override
    public void run() {
        ClusterServiceImpl cm = (ClusterServiceImpl)this.getService();
        cm.getClusterJoinManager().handleJoinRequest(this.request, this.getConnection());
    }

    public JoinRequest getRequest() {
        return this.request;
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        this.request = new JoinRequest();
        this.request.readData(in);
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        this.request.writeData(out);
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", message=").append(this.request);
    }

    @Override
    public int getId() {
        return 14;
    }
}

