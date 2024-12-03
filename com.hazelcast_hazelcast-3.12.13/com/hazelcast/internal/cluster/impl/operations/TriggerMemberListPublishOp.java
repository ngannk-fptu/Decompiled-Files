/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.cluster.impl.operations;

import com.hazelcast.internal.cluster.impl.ClusterServiceImpl;
import com.hazelcast.internal.cluster.impl.operations.AbstractClusterOperation;

public class TriggerMemberListPublishOp
extends AbstractClusterOperation {
    @Override
    public void run() throws Exception {
        ClusterServiceImpl clusterService = (ClusterServiceImpl)this.getService();
        clusterService.getMembershipManager().sendMemberListToMember(this.getCallerAddress());
    }

    @Override
    public int getId() {
        return 26;
    }
}

