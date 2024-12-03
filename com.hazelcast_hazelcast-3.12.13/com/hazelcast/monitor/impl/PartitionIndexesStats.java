/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor.impl;

import com.hazelcast.monitor.impl.IndexesStats;
import com.hazelcast.monitor.impl.PartitionPerIndexStats;
import com.hazelcast.monitor.impl.PerIndexStats;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public class PartitionIndexesStats
implements IndexesStats {
    private static final AtomicLongFieldUpdater<PartitionIndexesStats> QUERY_COUNT = AtomicLongFieldUpdater.newUpdater(PartitionIndexesStats.class, "queryCount");
    private static final AtomicLongFieldUpdater<PartitionIndexesStats> INDEXED_QUERY_COUNT = AtomicLongFieldUpdater.newUpdater(PartitionIndexesStats.class, "indexedQueryCount");
    private volatile long queryCount;
    private volatile long indexedQueryCount;

    @Override
    public long getQueryCount() {
        return this.queryCount;
    }

    @Override
    public void incrementQueryCount() {
        QUERY_COUNT.lazySet(this, this.queryCount + 1L);
    }

    @Override
    public long getIndexedQueryCount() {
        return this.indexedQueryCount;
    }

    @Override
    public void incrementIndexedQueryCount() {
        INDEXED_QUERY_COUNT.lazySet(this, this.indexedQueryCount + 1L);
    }

    @Override
    public PerIndexStats createPerIndexStats(boolean ordered, boolean queryableEntriesAreCached) {
        return new PartitionPerIndexStats();
    }
}

