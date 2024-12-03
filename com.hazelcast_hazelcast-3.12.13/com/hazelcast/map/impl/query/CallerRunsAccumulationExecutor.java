/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.query;

import com.hazelcast.aggregation.Aggregator;
import com.hazelcast.map.impl.query.AccumulationExecutor;
import com.hazelcast.map.impl.query.AggregationResult;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.spi.serialization.SerializationService;
import java.util.Collection;

public class CallerRunsAccumulationExecutor
implements AccumulationExecutor {
    private SerializationService serializationService;

    public CallerRunsAccumulationExecutor(SerializationService serializationService) {
        this.serializationService = serializationService;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public AggregationResult execute(Aggregator aggregator, Collection<QueryableEntry> entries, Collection<Integer> partitionIds) {
        Aggregator resultAggregator = (Aggregator)this.serializationService.toObject(this.serializationService.toData(aggregator));
        try {
            for (QueryableEntry entry : entries) {
                resultAggregator.accumulate(entry);
            }
        }
        finally {
            resultAggregator.onAccumulationFinished();
        }
        AggregationResult result = new AggregationResult(resultAggregator, this.serializationService);
        result.setPartitionIds(partitionIds);
        return result;
    }
}

