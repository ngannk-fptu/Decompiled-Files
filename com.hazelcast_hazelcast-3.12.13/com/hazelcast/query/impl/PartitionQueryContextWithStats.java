/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.monitor.impl.PerIndexStats;
import com.hazelcast.query.impl.Index;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.InternalIndex;
import com.hazelcast.query.impl.QueryContext;
import java.util.HashSet;

public class PartitionQueryContextWithStats
extends QueryContext {
    private final HashSet<PerIndexStats> trackedStats = new HashSet(8);

    public PartitionQueryContextWithStats(Indexes indexes) {
        super(indexes, 1);
    }

    @Override
    void attachTo(Indexes indexes, int ownedPartitionCount) {
        assert (indexes == this.indexes);
        assert (ownedPartitionCount == 1 && this.ownedPartitionCount == 1);
        for (PerIndexStats stats : this.trackedStats) {
            stats.resetPerQueryStats();
        }
        this.trackedStats.clear();
    }

    @Override
    void applyPerQueryStats() {
        for (PerIndexStats stats : this.trackedStats) {
            stats.incrementQueryCount();
        }
    }

    @Override
    public Index matchIndex(String pattern, QueryContext.IndexMatchHint matchHint) {
        InternalIndex index = this.indexes.matchIndex(pattern, matchHint, this.ownedPartitionCount);
        if (index == null) {
            return null;
        }
        this.trackedStats.add(index.getPerIndexStats());
        return index;
    }
}

