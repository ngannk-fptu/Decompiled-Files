/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.logging.ILogger;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.PartitionContainer;
import com.hazelcast.map.impl.recordstore.RecordStore;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.AbstractLocalOperation;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.exception.PartitionMigratingException;
import com.hazelcast.spi.impl.MutatingOperation;
import com.hazelcast.util.Clock;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;

public class MapClearExpiredOperation
extends AbstractLocalOperation
implements PartitionAwareOperation,
MutatingOperation {
    private int expirationPercentage;

    public MapClearExpiredOperation(int expirationPercentage) {
        this.expirationPercentage = expirationPercentage;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:mapService";
    }

    @Override
    public void run() throws Exception {
        if (this.getNodeEngine().getLocalMember().isLiteMember()) {
            return;
        }
        MapService mapService = (MapService)this.getService();
        MapServiceContext mapServiceContext = mapService.getMapServiceContext();
        PartitionContainer partitionContainer = mapServiceContext.getPartitionContainer(this.getPartitionId());
        ConcurrentMap<String, RecordStore> recordStores = partitionContainer.getMaps();
        boolean backup = !this.isOwner();
        for (RecordStore recordStore : recordStores.values()) {
            if (recordStore.size() <= 0 || !recordStore.isExpirable()) continue;
            recordStore.evictExpiredEntries(this.expirationPercentage, backup);
            recordStore.disposeDeferredBlocks();
        }
    }

    private boolean isOwner() {
        NodeEngine nodeEngine = this.getNodeEngine();
        Address owner = nodeEngine.getPartitionService().getPartitionOwner(this.getPartitionId());
        return nodeEngine.getThisAddress().equals(owner);
    }

    @Override
    public void onExecutionFailure(Throwable e) {
        try {
            super.onExecutionFailure(e);
        }
        finally {
            this.prepareForNextCleanup();
        }
    }

    @Override
    public void logError(Throwable e) {
        if (e instanceof PartitionMigratingException) {
            ILogger logger = this.getLogger();
            if (logger.isLoggable(Level.FINEST)) {
                logger.log(Level.FINEST, e.toString());
            }
        } else {
            super.logError(e);
        }
    }

    @Override
    public void afterRun() throws Exception {
        this.prepareForNextCleanup();
    }

    protected void prepareForNextCleanup() {
        MapService mapService = (MapService)this.getService();
        MapServiceContext mapServiceContext = mapService.getMapServiceContext();
        PartitionContainer partitionContainer = mapServiceContext.getPartitionContainer(this.getPartitionId());
        partitionContainer.setHasRunningCleanup(false);
        partitionContainer.setLastCleanupTime(Clock.currentTimeMillis());
    }

    @Override
    public boolean returnsResponse() {
        return false;
    }

    @Override
    protected void toString(StringBuilder sb) {
        super.toString(sb);
        sb.append(", expirationPercentage=").append(this.expirationPercentage);
    }
}

