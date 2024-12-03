/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.operation;

import com.hazelcast.instance.MemberImpl;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.internal.partition.MigrationCycleOperation;
import com.hazelcast.internal.partition.impl.InternalPartitionServiceImpl;
import com.hazelcast.internal.partition.operation.AbstractPartitionOperation;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;

public class ShutdownRequestOperation
extends AbstractPartitionOperation
implements MigrationCycleOperation {
    @Override
    public void run() {
        InternalPartitionServiceImpl partitionService = (InternalPartitionServiceImpl)this.getService();
        ILogger logger = this.getLogger();
        Address caller = this.getCallerAddress();
        if (partitionService.isLocalMemberMaster()) {
            ClusterService clusterService = this.getNodeEngine().getClusterService();
            MemberImpl member = clusterService.getMember(caller);
            if (member != null) {
                if (logger.isFinestEnabled()) {
                    logger.finest("Received shutdown request from " + caller);
                }
                partitionService.onShutdownRequest(member);
            } else {
                logger.warning("Ignoring shutdown request from " + caller + " because it is not a member");
            }
        } else {
            logger.warning("Received shutdown request from " + caller + " but this node is not master.");
        }
    }

    @Override
    public boolean returnsResponse() {
        return false;
    }

    @Override
    public String getServiceName() {
        return "hz:core:partitionService";
    }

    @Override
    public int getId() {
        return 15;
    }
}

