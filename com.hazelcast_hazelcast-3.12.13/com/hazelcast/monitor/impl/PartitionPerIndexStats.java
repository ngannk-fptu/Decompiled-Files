/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor.impl;

import com.hazelcast.internal.memory.MemoryAllocator;
import com.hazelcast.monitor.impl.IndexOperationStats;
import com.hazelcast.monitor.impl.PartitionIndexOperationStats;
import com.hazelcast.monitor.impl.PerIndexStats;
import com.hazelcast.query.impl.Index;
import com.hazelcast.util.Clock;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public class PartitionPerIndexStats
implements PerIndexStats {
    private static final AtomicLongFieldUpdater<PartitionPerIndexStats> ENTRY_COUNT = AtomicLongFieldUpdater.newUpdater(PartitionPerIndexStats.class, "entryCount");
    private static final AtomicLongFieldUpdater<PartitionPerIndexStats> QUERY_COUNT = AtomicLongFieldUpdater.newUpdater(PartitionPerIndexStats.class, "queryCount");
    private static final AtomicLongFieldUpdater<PartitionPerIndexStats> HIT_COUNT = AtomicLongFieldUpdater.newUpdater(PartitionPerIndexStats.class, "hitCount");
    private static final AtomicLongFieldUpdater<PartitionPerIndexStats> TOTAL_HIT_LATENCY = AtomicLongFieldUpdater.newUpdater(PartitionPerIndexStats.class, "totalHitLatency");
    private static final AtomicLongFieldUpdater<PartitionPerIndexStats> TOTAL_NORMALIZED_HIT_CARDINALITY = AtomicLongFieldUpdater.newUpdater(PartitionPerIndexStats.class, "totalNormalizedHitCardinality");
    private static final AtomicLongFieldUpdater<PartitionPerIndexStats> INSERT_COUNT = AtomicLongFieldUpdater.newUpdater(PartitionPerIndexStats.class, "insertCount");
    private static final AtomicLongFieldUpdater<PartitionPerIndexStats> TOTAL_INSERT_LATENCY = AtomicLongFieldUpdater.newUpdater(PartitionPerIndexStats.class, "totalInsertLatency");
    private static final AtomicLongFieldUpdater<PartitionPerIndexStats> UPDATE_COUNT = AtomicLongFieldUpdater.newUpdater(PartitionPerIndexStats.class, "updateCount");
    private static final AtomicLongFieldUpdater<PartitionPerIndexStats> TOTAL_UPDATE_LATENCY = AtomicLongFieldUpdater.newUpdater(PartitionPerIndexStats.class, "totalUpdateLatency");
    private static final AtomicLongFieldUpdater<PartitionPerIndexStats> REMOVE_COUNT = AtomicLongFieldUpdater.newUpdater(PartitionPerIndexStats.class, "removeCount");
    private static final AtomicLongFieldUpdater<PartitionPerIndexStats> TOTAL_REMOVE_LATENCY = AtomicLongFieldUpdater.newUpdater(PartitionPerIndexStats.class, "totalRemoveLatency");
    private static final AtomicLongFieldUpdater<PartitionPerIndexStats> MEMORY_COST = AtomicLongFieldUpdater.newUpdater(PartitionPerIndexStats.class, "memoryCost");
    private final PartitionIndexOperationStats operationStats = new PartitionIndexOperationStats();
    private final long creationTime;
    private volatile long entryCount;
    private volatile long queryCount;
    private volatile long hitCount;
    private volatile long totalHitLatency;
    private volatile long totalNormalizedHitCardinality = Double.doubleToRawLongBits(0.0);
    private volatile long insertCount;
    private volatile long totalInsertLatency;
    private volatile long updateCount;
    private volatile long totalUpdateLatency;
    private volatile long removeCount;
    private volatile long totalRemoveLatency;
    private volatile long memoryCost;
    private boolean hasQueries;

    public PartitionPerIndexStats() {
        this.creationTime = Clock.currentTimeMillis();
    }

    private void updateMemoryCost(long delta) {
        MEMORY_COST.lazySet(this, this.memoryCost + delta);
    }

    private void resetMemoryCost() {
        MEMORY_COST.lazySet(this, 0L);
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
        if (this.hasQueries) {
            QUERY_COUNT.lazySet(this, this.queryCount + 1L);
        }
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
        return Double.longBitsToDouble(this.totalNormalizedHitCardinality);
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
        return this.memoryCost;
    }

    @Override
    public void onInsert(long timestamp, IndexOperationStats operationStats, Index.OperationSource operationSource) {
        if (operationStats.getEntryCountDelta() == 0L) {
            return;
        }
        if (operationSource == Index.OperationSource.USER) {
            TOTAL_INSERT_LATENCY.lazySet(this, this.totalInsertLatency + (System.nanoTime() - timestamp));
            INSERT_COUNT.lazySet(this, this.insertCount + 1L);
        }
        ENTRY_COUNT.lazySet(this, this.entryCount + 1L);
    }

    @Override
    public void onUpdate(long timestamp, IndexOperationStats operationStats, Index.OperationSource operationSource) {
        if (operationSource == Index.OperationSource.USER) {
            TOTAL_UPDATE_LATENCY.lazySet(this, this.totalUpdateLatency + (System.nanoTime() - timestamp));
            UPDATE_COUNT.lazySet(this, this.updateCount + 1L);
        }
        ENTRY_COUNT.lazySet(this, this.entryCount + operationStats.getEntryCountDelta());
    }

    @Override
    public void onRemove(long timestamp, IndexOperationStats operationStats, Index.OperationSource operationSource) {
        if (operationStats.getEntryCountDelta() == 0L) {
            return;
        }
        if (operationSource == Index.OperationSource.USER) {
            TOTAL_REMOVE_LATENCY.lazySet(this, this.totalRemoveLatency + (System.nanoTime() - timestamp));
            REMOVE_COUNT.lazySet(this, this.removeCount + 1L);
        }
        ENTRY_COUNT.lazySet(this, this.entryCount - 1L);
    }

    @Override
    public void onClear() {
        ENTRY_COUNT.lazySet(this, 0L);
    }

    @Override
    public void onIndexHit(long timestamp, long hitCardinality) {
        this.hasQueries = true;
        long localEntryCount = this.entryCount;
        if (localEntryCount == 0L) {
            return;
        }
        TOTAL_HIT_LATENCY.lazySet(this, this.totalHitLatency + (System.nanoTime() - timestamp));
        HIT_COUNT.lazySet(this, this.hitCount + 1L);
        long adjustedHitCardinality = Math.min(hitCardinality, localEntryCount);
        double normalizedHitCardinality = (double)adjustedHitCardinality / (double)localEntryCount;
        double decodedTotalNormalizedHitCardinality = Double.longBitsToDouble(this.totalNormalizedHitCardinality);
        double newTotalNormalizedHitCardinality = decodedTotalNormalizedHitCardinality + normalizedHitCardinality;
        long newEncodedTotalNormalizedHitCardinality = Double.doubleToRawLongBits(newTotalNormalizedHitCardinality);
        TOTAL_NORMALIZED_HIT_CARDINALITY.lazySet(this, newEncodedTotalNormalizedHitCardinality);
    }

    @Override
    public void resetPerQueryStats() {
        this.hasQueries = false;
    }

    @Override
    public MemoryAllocator wrapMemoryAllocator(MemoryAllocator memoryAllocator) {
        return new MemoryAllocatorWithStats(memoryAllocator);
    }

    @Override
    public IndexOperationStats createOperationStats() {
        this.operationStats.reset();
        return this.operationStats;
    }

    private class MemoryAllocatorWithStats
    implements MemoryAllocator {
        private final MemoryAllocator delegate;

        public MemoryAllocatorWithStats(MemoryAllocator delegate) {
            this.delegate = delegate;
        }

        @Override
        public long allocate(long size) {
            long result = this.delegate.allocate(size);
            PartitionPerIndexStats.this.updateMemoryCost(size);
            return result;
        }

        @Override
        public long reallocate(long address, long currentSize, long newSize) {
            long result = this.delegate.reallocate(address, currentSize, newSize);
            PartitionPerIndexStats.this.updateMemoryCost(newSize - currentSize);
            return result;
        }

        @Override
        public void free(long address, long size) {
            this.delegate.free(address, size);
            PartitionPerIndexStats.this.updateMemoryCost(-size);
        }

        @Override
        public void dispose() {
            this.delegate.dispose();
            PartitionPerIndexStats.this.resetMemoryCost();
        }
    }
}

