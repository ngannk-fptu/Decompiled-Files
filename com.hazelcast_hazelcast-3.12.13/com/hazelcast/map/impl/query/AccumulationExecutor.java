/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.query;

import com.hazelcast.aggregation.Aggregator;
import com.hazelcast.map.impl.query.AggregationResult;
import com.hazelcast.query.impl.QueryableEntry;
import java.util.Collection;

public interface AccumulationExecutor {
    public AggregationResult execute(Aggregator var1, Collection<QueryableEntry> var2, Collection<Integer> var3);
}

