/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.nearcache.impl.invalidation;

import com.hazelcast.internal.nearcache.NearCacheRecord;
import com.hazelcast.internal.nearcache.impl.invalidation.MetaDataContainer;
import com.hazelcast.internal.nearcache.impl.invalidation.MinimalPartitionService;
import com.hazelcast.internal.nearcache.impl.invalidation.RepairingHandler;
import com.hazelcast.internal.nearcache.impl.invalidation.StaleReadDetector;

public class StaleReadDetectorImpl
implements StaleReadDetector {
    private final RepairingHandler repairingHandler;
    private final MinimalPartitionService partitionService;

    StaleReadDetectorImpl(RepairingHandler repairingHandler, MinimalPartitionService partitionService) {
        this.repairingHandler = repairingHandler;
        this.partitionService = partitionService;
    }

    @Override
    public boolean isStaleRead(Object key, NearCacheRecord record) {
        MetaDataContainer latestMetaData = this.repairingHandler.getMetaDataContainer(record.getPartitionId());
        return !record.hasSameUuid(latestMetaData.getUuid()) || record.getInvalidationSequence() < latestMetaData.getStaleSequence();
    }

    @Override
    public int getPartitionId(Object key) {
        return this.partitionService.getPartitionId(key);
    }

    @Override
    public MetaDataContainer getMetaDataContainer(int partitionId) {
        return this.repairingHandler.getMetaDataContainer(partitionId);
    }

    public String toString() {
        return "Default StaleReadDetectorImpl";
    }
}

