/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.operation;

import com.hazelcast.cache.impl.CachePartitionSegment;
import com.hazelcast.cache.impl.CacheService;
import com.hazelcast.cache.impl.ICacheRecordStore;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.spi.AbstractLocalOperation;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.PartitionAwareOperation;
import com.hazelcast.spi.exception.PartitionMigratingException;
import com.hazelcast.spi.impl.MutatingOperation;
import com.hazelcast.util.Clock;
import java.util.Iterator;
import java.util.logging.Level;

public class CacheClearExpiredOperation
extends AbstractLocalOperation
implements PartitionAwareOperation,
MutatingOperation {
    private int expirationPercentage;

    public CacheClearExpiredOperation(int expirationPercentage) {
        this.expirationPercentage = expirationPercentage;
    }

    @Override
    public String getServiceName() {
        return "hz:impl:cacheService";
    }

    @Override
    public void run() throws Exception {
        if (this.getNodeEngine().getLocalMember().isLiteMember()) {
            return;
        }
        if (!this.isOwner()) {
            return;
        }
        CacheService service = (CacheService)this.getService();
        CachePartitionSegment segment = service.getSegment(this.getPartitionId());
        Iterator<ICacheRecordStore> iterator = segment.recordStoreIterator();
        while (iterator.hasNext()) {
            ICacheRecordStore store = iterator.next();
            if (store.size() <= 0) continue;
            store.evictExpiredEntries(this.expirationPercentage);
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
        CacheService service = (CacheService)this.getService();
        CachePartitionSegment segment = service.getSegment(this.getPartitionId());
        segment.setRunningCleanupOperation(false);
        segment.setLastCleanupTime(Clock.currentTimeMillis());
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

