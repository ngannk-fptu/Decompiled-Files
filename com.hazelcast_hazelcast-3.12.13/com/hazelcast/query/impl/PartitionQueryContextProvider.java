/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.QueryContext;
import com.hazelcast.query.impl.QueryContextProvider;

public class PartitionQueryContextProvider
implements QueryContextProvider {
    private final QueryContext queryContext;

    public PartitionQueryContextProvider(Indexes indexes) {
        this.queryContext = new QueryContext(indexes, 1);
    }

    @Override
    public QueryContext obtainContextFor(Indexes indexes, int ownedPartitionCount) {
        assert (indexes == this.queryContext.indexes);
        assert (this.queryContext.ownedPartitionCount == 1 && ownedPartitionCount == 1);
        return this.queryContext;
    }
}

