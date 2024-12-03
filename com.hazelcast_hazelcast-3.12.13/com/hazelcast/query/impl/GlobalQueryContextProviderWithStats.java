/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.query.impl.GlobalQueryContextWithStats;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.QueryContext;
import com.hazelcast.query.impl.QueryContextProvider;

public class GlobalQueryContextProviderWithStats
implements QueryContextProvider {
    private static final ThreadLocal<GlobalQueryContextWithStats> QUERY_CONTEXT = new ThreadLocal<GlobalQueryContextWithStats>(){

        @Override
        protected GlobalQueryContextWithStats initialValue() {
            return new GlobalQueryContextWithStats();
        }
    };

    @Override
    public QueryContext obtainContextFor(Indexes indexes, int ownedPartitionCount) {
        GlobalQueryContextWithStats queryContext = QUERY_CONTEXT.get();
        queryContext.attachTo(indexes, ownedPartitionCount);
        return queryContext;
    }
}

