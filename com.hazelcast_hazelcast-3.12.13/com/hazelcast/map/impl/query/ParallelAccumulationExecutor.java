/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.query;

import com.hazelcast.aggregation.Aggregator;
import com.hazelcast.map.impl.query.AccumulationExecutor;
import com.hazelcast.map.impl.query.AggregationResult;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.query.impl.predicates.PredicateUtils;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.FutureUtil;
import com.hazelcast.util.executor.ManagedExecutorService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class ParallelAccumulationExecutor
implements AccumulationExecutor {
    private static final int THREAD_SPLIT_COUNT = 8;
    private final ManagedExecutorService executor;
    private final SerializationService serializationService;
    private final int callTimeoutInMillis;

    public ParallelAccumulationExecutor(ManagedExecutorService executor, SerializationService serializationService, int callTimeoutInMillis) {
        this.executor = executor;
        this.serializationService = serializationService;
        this.callTimeoutInMillis = callTimeoutInMillis;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public AggregationResult execute(Aggregator aggregator, Collection<QueryableEntry> entries, Collection<Integer> partitionIds) {
        Collection<Aggregator> chunkAggregators = this.accumulateParallel(aggregator, entries);
        Aggregator resultAggregator = this.clone(aggregator);
        try {
            for (Aggregator chunkAggregator : chunkAggregators) {
                resultAggregator.combine(chunkAggregator);
            }
        }
        finally {
            resultAggregator.onCombinationFinished();
        }
        AggregationResult result = new AggregationResult(resultAggregator, this.serializationService);
        result.setPartitionIds(partitionIds);
        return result;
    }

    protected Collection<Aggregator> accumulateParallel(Aggregator aggregator, Collection<QueryableEntry> entries) {
        ArrayList futures = new ArrayList();
        Collection<QueryableEntry>[] chunks = this.split(entries, 8);
        if (chunks == null) {
            AccumulatePartitionCallable task = new AccumulatePartitionCallable(this.clone(aggregator), entries);
            futures.add(this.executor.submit(task));
        } else {
            for (Collection<QueryableEntry> chunk : chunks) {
                AccumulatePartitionCallable task = new AccumulatePartitionCallable(this.clone(aggregator), chunk);
                futures.add(this.executor.submit(task));
            }
        }
        return FutureUtil.returnWithDeadline(futures, this.callTimeoutInMillis, TimeUnit.MILLISECONDS, FutureUtil.RETHROW_EVERYTHING);
    }

    private Collection<QueryableEntry>[] split(Collection<QueryableEntry> entries, int chunkCount) {
        int estimatedSize = PredicateUtils.estimatedSizeOf(entries);
        if (estimatedSize < chunkCount * 2) {
            return null;
        }
        int counter = 0;
        Collection[] entriesSplit = new Collection[chunkCount];
        int entriesPerChunk = estimatedSize / chunkCount;
        for (int i = 0; i < chunkCount; ++i) {
            entriesSplit[i] = new ArrayList(entriesPerChunk);
        }
        for (QueryableEntry entry : entries) {
            entriesSplit[counter++ % 8].add(entry);
        }
        return entriesSplit;
    }

    private Aggregator clone(Aggregator aggregator) {
        return (Aggregator)this.serializationService.toObject(this.serializationService.toData(aggregator));
    }

    private static final class AccumulatePartitionCallable
    implements Callable<Aggregator> {
        private final Aggregator aggregator;
        private final Collection<QueryableEntry> entries;

        private AccumulatePartitionCallable(Aggregator aggregator, Collection<QueryableEntry> entries) {
            this.aggregator = aggregator;
            this.entries = entries;
        }

        @Override
        public Aggregator call() throws Exception {
            try {
                for (QueryableEntry entry : this.entries) {
                    this.aggregator.accumulate(entry);
                }
            }
            finally {
                this.aggregator.onAccumulationFinished();
            }
            return this.aggregator;
        }
    }
}

