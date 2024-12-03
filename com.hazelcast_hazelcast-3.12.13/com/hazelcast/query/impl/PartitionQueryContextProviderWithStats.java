/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.PartitionQueryContextWithStats;
import com.hazelcast.query.impl.QueryContext;
import com.hazelcast.query.impl.QueryContextProvider;

public class PartitionQueryContextProviderWithStats
implements QueryContextProvider {
    private final PartitionQueryContextWithStats queryContext;

    public PartitionQueryContextProviderWithStats(Indexes indexes) {
        this.queryContext = new PartitionQueryContextWithStats(indexes);
    }

    @Override
    public QueryContext obtainContextFor(Indexes indexes, int ownedPartitionCount) {
        assert (this.queryContext.ownedPartitionCount == 1 && ownedPartitionCount == 1);
        this.queryContext.attachTo(indexes, ownedPartitionCount);
        return this.queryContext;
    }
}

