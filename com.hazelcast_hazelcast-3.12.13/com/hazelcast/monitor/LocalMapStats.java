/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor;

import com.hazelcast.monitor.LocalIndexStats;
import com.hazelcast.monitor.LocalInstanceStats;
import com.hazelcast.monitor.NearCacheStats;
import java.util.Map;

public interface LocalMapStats
extends LocalInstanceStats {
    public long getOwnedEntryCount();

    public long getBackupEntryCount();

    public int getBackupCount();

    public long getOwnedEntryMemoryCost();

    public long getBackupEntryMemoryCost();

    @Override
    public long getCreationTime();

    public long getLastAccessTime();

    public long getLastUpdateTime();

    public long getHits();

    public long getLockedEntryCount();

    public long getDirtyEntryCount();

    public long getPutOperationCount();

    public long getGetOperationCount();

    public long getRemoveOperationCount();

    public long getTotalPutLatency();

    public long getTotalGetLatency();

    public long getTotalRemoveLatency();

    public long getMaxPutLatency();

    public long getMaxGetLatency();

    public long getMaxRemoveLatency();

    public long getEventOperationCount();

    public long getOtherOperationCount();

    public long total();

    public long getHeapCost();

    public long getMerkleTreesCost();

    public NearCacheStats getNearCacheStats();

    public long getQueryCount();

    public long getIndexedQueryCount();

    public Map<String, LocalIndexStats> getIndexStats();
}

