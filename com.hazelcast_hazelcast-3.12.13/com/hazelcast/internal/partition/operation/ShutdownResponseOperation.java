/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.partition.operation;

import com.hazelcast.internal.partition.MigrationCycleOperation;
import com.hazelcast.internal.partition.impl.InternalPartitionServiceImpl;
import com.hazelcast.internal.partition.operation.AbstractPartitionOperation;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.NodeEngine;

public class ShutdownResponseOperation
extends AbstractPartitionOperation
implements MigrationCycleOperation {
    @Override
    public void run() {
        InternalPartitionServiceImpl partitionService = (InternalPartitionServiceImpl)this.getService();
        ILogger logger = this.getLogger();
        Address caller = this.getCallerAddress();
        NodeEngine nodeEngine = this.getNodeEngine();
        if (nodeEngine.isRunning()) {
            logger.severe("Received a shutdown response from " + caller + ", but this node is not shutting down!");
            return;
        }
        if (partitionService.isMemberMaster(caller)) {
            if (logger.isFinestEnabled()) {
                logger.finest("Received shutdown response from " + caller);
            }
            partitionService.onShutdownResponse();
        } else {
            logger.warning("Received shutdown response from " + caller + " but it's not the known master");
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
        return 16;
    }
}

