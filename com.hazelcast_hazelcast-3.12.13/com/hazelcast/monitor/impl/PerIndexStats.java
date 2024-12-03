/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor.impl;

import com.hazelcast.internal.memory.MemoryAllocator;
import com.hazelcast.monitor.impl.IndexOperationStats;
import com.hazelcast.query.impl.Index;

public interface PerIndexStats {
    public static final PerIndexStats EMPTY = new PerIndexStats(){

        @Override
        public long makeTimestamp() {
            return 0L;
        }

        @Override
        public long getCreationTime() {
            return 0L;
        }

        @Override
        public long getQueryCount() {
            return 0L;
        }

        @Override
        public void incrementQueryCount() {
        }

        @Override
        public long getHitCount() {
            return 0L;
        }

        @Override
        public long getTotalHitLatency() {
            return 0L;
        }

        @Override
        public double getTotalNormalizedHitCardinality() {
            return 0.0;
        }

        @Override
        public long getInsertCount() {
            return 0L;
        }

        @Override
        public long getTotalInsertLatency() {
            return 0L;
        }

        @Override
        public long getUpdateCount() {
            return 0L;
        }

        @Override
        public long getTotalUpdateLatency() {
            return 0L;
        }

        @Override
        public long getRemoveCount() {
            return 0L;
        }

        @Override
        public long getTotalRemoveLatency() {
            return 0L;
        }

        @Override
        public long getMemoryCost() {
            return 0L;
        }

        @Override
        public void onInsert(long timestamp, IndexOperationStats operationStats, Index.OperationSource operationSource) {
        }

        @Override
        public void onUpdate(long timestamp, IndexOperationStats operationStats, Index.OperationSource operationSource) {
        }

        @Override
        public void onRemove(long timestamp, IndexOperationStats operationStats, Index.OperationSource operationSource) {
        }

        @Override
        public void onClear() {
        }

        @Override
        public void onIndexHit(long timestamp, long hitCardinality) {
        }

        @Override
        public void resetPerQueryStats() {
        }

        @Override
        public MemoryAllocator wrapMemoryAllocator(MemoryAllocator memoryAllocator) {
            return memoryAllocator;
        }

        @Override
        public IndexOperationStats createOperationStats() {
            return IndexOperationStats.EMPTY;
        }
    };

    public long makeTimestamp();

    public long getCreationTime();

    public long getQueryCount();

    public void incrementQueryCount();

    public long getHitCount();

    public long getTotalHitLatency();

    public double getTotalNormalizedHitCardinality();

    public long getInsertCount();

    public long getTotalInsertLatency();

    public long getUpdateCount();

    public long getTotalUpdateLatency();

    public long getRemoveCount();

    public long getTotalRemoveLatency();

    public long getMemoryCost();

    public void onInsert(long var1, IndexOperationStats var3, Index.OperationSource var4);

    public void onUpdate(long var1, IndexOperationStats var3, Index.OperationSource var4);

    public void onRemove(long var1, IndexOperationStats var3, Index.OperationSource var4);

    public void onClear();

    public void onIndexHit(long var1, long var3);

    public void resetPerQueryStats();

    public MemoryAllocator wrapMemoryAllocator(MemoryAllocator var1);

    public IndexOperationStats createOperationStats();
}

