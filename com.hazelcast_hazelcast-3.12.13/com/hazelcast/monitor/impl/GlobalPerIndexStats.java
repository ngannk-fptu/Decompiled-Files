/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor.impl;

import com.hazelcast.internal.memory.MemoryAllocator;
import com.hazelcast.monitor.impl.GlobalIndexOperationStats;
import com.hazelcast.monitor.impl.IndexOperationStats;
import com.hazelcast.monitor.impl.PerIndexStats;
import com.hazelcast.query.impl.Index;
import com.hazelcast.query.impl.IndexHeapMemoryCostUtil;
import com.hazelcast.util.Clock;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public class GlobalPerIndexStats
implements PerIndexStats {
    private static final long PRECISION_SCALE = 7L;
    private static final long PRECISION = 128L;
    private static final AtomicLongFieldUpdater<GlobalPerIndexStats> ENTRY_COUNT = AtomicLongFieldUpdater.newUpdater(GlobalPerIndexStats.class, "entryCount");
    private static final AtomicLongFieldUpdater<GlobalPerIndexStats> QUERY_COUNT = AtomicLongFieldUpdater.newUpdater(GlobalPerIndexStats.class, "queryCount");
    private static final AtomicLongFieldUpdater<GlobalPerIndexStats> HIT_COUNT = AtomicLongFieldUpdater.newUpdater(GlobalPerIndexStats.class, "hitCount");
    private static final AtomicLongFieldUpdater<GlobalPerIndexStats> TOTAL_HIT_LATENCY = AtomicLongFieldUpdater.newUpdater(GlobalPerIndexStats.class, "totalHitLatency");
    private static final AtomicLongFieldUpdater<GlobalPerIndexStats> TOTAL_NORMALIZED_HIT_CARDINALITY = AtomicLongFieldUpdater.newUpdater(GlobalPerIndexStats.class, "totalNormalizedHitCardinality");
    private static final AtomicLongFieldUpdater<GlobalPerIndexStats> INSERT_COUNT = AtomicLongFieldUpdater.newUpdater(GlobalPerIndexStats.class, "insertCount");
    private static final AtomicLongFieldUpdater<GlobalPerIndexStats> TOTAL_INSERT_LATENCY = AtomicLongFieldUpdater.newUpdater(GlobalPerIndexStats.class, "totalInsertLatency");
    private static final AtomicLongFieldUpdater<GlobalPerIndexStats> UPDATE_COUNT = AtomicLongFieldUpdater.newUpdater(GlobalPerIndexStats.class, "updateCount");
    private static final AtomicLongFieldUpdater<GlobalPerIndexStats> TOTAL_UPDATE_LATENCY = AtomicLongFieldUpdater.newUpdater(GlobalPerIndexStats.class, "totalUpdateLatency");
    private static final AtomicLongFieldUpdater<GlobalPerIndexStats> REMOVE_COUNT = AtomicLongFieldUpdater.newUpdater(GlobalPerIndexStats.class, "removeCount");
    private static final AtomicLongFieldUpdater<GlobalPerIndexStats> TOTAL_REMOVE_LATENCY = AtomicLongFieldUpdater.newUpdater(GlobalPerIndexStats.class, "totalRemoveLatency");
    private static final AtomicLongFieldUpdater<GlobalPerIndexStats> VALUES_MEMORY_COST = AtomicLongFieldUpdater.newUpdater(GlobalPerIndexStats.class, "valuesMemoryCost");
    private final boolean ordered;
    private final boolean usesCachedQueryableEntries;
    private final long creationTime;
    private volatile long entryCount;
    private volatile long queryCount;
    private volatile long hitCount;
    private volatile long totalHitLatency;
    private volatile long totalNormalizedHitCardinality;
    private volatile long insertCount;
    private volatile long totalInsertLatency;
    private volatile long updateCount;
    private volatile long totalUpdateLatency;
    private volatile long removeCount;
    private volatile long totalRemoveLatency;
    private volatile long valuesMemoryCost;

    public GlobalPerIndexStats(boolean ordered, boolean usesCachedQueryableEntries) {
        this.ordered = ordered;
        this.usesCachedQueryableEntries = usesCachedQueryableEntries;
        this.creationTime = Clock.currentTimeMillis();
    }

    @Override
    public long makeTimestamp() {
        return System.nanoTime();
    }

    @Override
    public long getCreationTime() {
        return this.creationTime;
    }

    @Override
    public long getQueryCount() {
        return this.queryCount;
    }

    @Override
    public void incrementQueryCount() {
        QUERY_COUNT.incrementAndGet(this);
    }

    @Override
    public long getHitCount() {
        return this.hitCount;
    }

    @Override
    public long getTotalHitLatency() {
        return this.totalHitLatency;
    }

    @Override
    public double getTotalNormalizedHitCardinality() {
        return (double)this.totalNormalizedHitCardinality / 128.0;
    }

    @Override
    public long getInsertCount() {
        return this.insertCount;
    }

    @Override
    public long getTotalInsertLatency() {
        return this.totalInsertLatency;
    }

    @Override
    public long getUpdateCount() {
        return this.updateCount;
    }

    @Override
    public long getTotalUpdateLatency() {
        return this.totalUpdateLatency;
    }

    @Override
    public long getRemoveCount() {
        return this.removeCount;
    }

    @Override
    public long getTotalRemoveLatency() {
        return this.totalRemoveLatency;
    }

    @Override
    public long getMemoryCost() {
        return IndexHeapMemoryCostUtil.estimateMapCost(this.entryCount, this.ordered, this.usesCachedQueryableEntries) + this.valuesMemoryCost;
    }

    @Override
    public void onInsert(long timestamp, IndexOperationStats operationStats, Index.OperationSource operationSource) {
        if (operationStats.getEntryCountDelta() == 0L) {
            return;
        }
        if (operationSource == Index.OperationSource.USER) {
            TOTAL_INSERT_LATENCY.addAndGet(this, System.nanoTime() - timestamp);
            INSERT_COUNT.incrementAndGet(this);
        }
        ENTRY_COUNT.incrementAndGet(this);
        VALUES_MEMORY_COST.addAndGet(this, operationStats.getMemoryCostDelta());
    }

    @Override
    public void onUpdate(long timestamp, IndexOperationStats operationStats, Index.OperationSource operationSource) {
        if (operationSource == Index.OperationSource.USER) {
            TOTAL_UPDATE_LATENCY.addAndGet(this, System.nanoTime() - timestamp);
            UPDATE_COUNT.incrementAndGet(this);
        }
        VALUES_MEMORY_COST.addAndGet(this, operationStats.getMemoryCostDelta());
    }

    @Override
    public void onRemove(long timestamp, IndexOperationStats operationStats, Index.OperationSource operationSource) {
        if (operationStats.getEntryCountDelta() == 0L) {
            return;
        }
        if (operationSource == Index.OperationSource.USER) {
            TOTAL_REMOVE_LATENCY.addAndGet(this, System.nanoTime() - timestamp);
            REMOVE_COUNT.incrementAndGet(this);
        }
        ENTRY_COUNT.decrementAndGet(this);
        VALUES_MEMORY_COST.addAndGet(this, operationStats.getMemoryCostDelta());
    }

    @Override
    public void onClear() {
        this.entryCount = 0L;
        this.valuesMemoryCost = 0L;
    }

    @Override
    public void onIndexHit(long timestamp, long hitCardinality) {
        long localEntryCount = this.entryCount;
        if (localEntryCount == 0L) {
            return;
        }
        TOTAL_HIT_LATENCY.addAndGet(this, System.nanoTime() - timestamp);
        HIT_COUNT.incrementAndGet(this);
        long adjustedHitCardinality = Math.min(hitCardinality, localEntryCount);
        long scaledHitCardinality = adjustedHitCardinality << 7;
        long normalizedHitCardinality = scaledHitCardinality / localEntryCount;
        TOTAL_NORMALIZED_HIT_CARDINALITY.addAndGet(this, normalizedHitCardinality);
    }

    @Override
    public void resetPerQueryStats() {
    }

    @Override
    public MemoryAllocator wrapMemoryAllocator(MemoryAllocator memoryAllocator) {
        throw new UnsupportedOperationException("global indexes are not supposed to use native memory allocators");
    }

    @Override
    public IndexOperationStats createOperationStats() {
        return new GlobalIndexOperationStats();
    }
}

