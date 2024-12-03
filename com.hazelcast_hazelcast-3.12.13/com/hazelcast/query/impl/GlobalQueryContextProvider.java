/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.QueryContext;
import com.hazelcast.query.impl.QueryContextProvider;

public class GlobalQueryContextProvider
implements QueryContextProvider {
    private static final ThreadLocal<QueryContext> QUERY_CONTEXT = new ThreadLocal<QueryContext>(){

        @Override
        protected QueryContext initialValue() {
            return new QueryContext();
        }
    };

    @Override
    public QueryContext obtainContextFor(Indexes indexes, int ownedPartitionCount) {
        QueryContext queryContext = QUERY_CONTEXT.get();
        queryContext.attachTo(indexes, ownedPartitionCount);
        return queryContext;
    }
}

