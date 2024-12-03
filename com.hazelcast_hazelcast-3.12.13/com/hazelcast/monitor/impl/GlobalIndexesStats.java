/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor.impl;

import com.hazelcast.monitor.impl.GlobalPerIndexStats;
import com.hazelcast.monitor.impl.IndexesStats;
import com.hazelcast.monitor.impl.PerIndexStats;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public class GlobalIndexesStats
implements IndexesStats {
    private static final AtomicLongFieldUpdater<GlobalIndexesStats> QUERY_COUNT = AtomicLongFieldUpdater.newUpdater(GlobalIndexesStats.class, "queryCount");
    private static final AtomicLongFieldUpdater<GlobalIndexesStats> INDEXED_QUERY_COUNT = AtomicLongFieldUpdater.newUpdater(GlobalIndexesStats.class, "indexedQueryCount");
    private volatile long queryCount;
    private volatile long indexedQueryCount;

    @Override
    public long getQueryCount() {
        return this.queryCount;
    }

    @Override
    public void incrementQueryCount() {
        QUERY_COUNT.incrementAndGet(this);
    }

    @Override
    public long getIndexedQueryCount() {
        return this.indexedQueryCount;
    }

    @Override
    public void incrementIndexedQueryCount() {
        INDEXED_QUERY_COUNT.incrementAndGet(this);
    }

    @Override
    public PerIndexStats createPerIndexStats(boolean ordered, boolean usesCachedQueryableEntries) {
        return new GlobalPerIndexStats(ordered, usesCachedQueryableEntries);
    }
}

