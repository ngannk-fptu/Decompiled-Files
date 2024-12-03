/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.query;

import com.hazelcast.map.impl.query.PartitionScanExecutor;
import com.hazelcast.map.impl.query.PartitionScanRunner;
import com.hazelcast.map.impl.query.Result;
import com.hazelcast.query.PagingPredicate;
import com.hazelcast.query.PagingPredicateAccessor;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.QueryableEntriesSegment;
import com.hazelcast.util.FutureUtil;
import com.hazelcast.util.executor.ManagedExecutorService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ParallelPartitionScanExecutor
implements PartitionScanExecutor {
    private final PartitionScanRunner partitionScanRunner;
    private final ManagedExecutorService executor;
    private final int timeoutInMillis;

    public ParallelPartitionScanExecutor(PartitionScanRunner partitionScanRunner, ManagedExecutorService executor, int timeoutInMillis) {
        this.partitionScanRunner = partitionScanRunner;
        this.executor = executor;
        this.timeoutInMillis = timeoutInMillis;
    }

    @Override
    public void execute(String mapName, Predicate predicate, Collection<Integer> partitions, Result result) {
        this.runUsingPartitionScanWithoutPaging(mapName, predicate, partitions, result);
        if (predicate instanceof PagingPredicate) {
            Map.Entry<Integer, Map.Entry> nearestAnchorEntry = PagingPredicateAccessor.getNearestAnchorEntry((PagingPredicate)predicate);
            result.orderAndLimit((PagingPredicate)predicate, nearestAnchorEntry);
        }
    }

    @Override
    public QueryableEntriesSegment execute(String mapName, Predicate predicate, int partitionId, int tableIndex, int fetchSize) {
        return this.partitionScanRunner.run(mapName, predicate, partitionId, tableIndex, fetchSize);
    }

    protected void runUsingPartitionScanWithoutPaging(String name, Predicate predicate, Collection<Integer> partitions, Result result) {
        ArrayList<Future<Result>> futures = new ArrayList<Future<Result>>(partitions.size());
        for (Integer partitionId : partitions) {
            Future<Result> future = this.runPartitionScanForPartition(name, predicate, partitionId, (Result)result.createSubResult());
            futures.add(future);
        }
        Collection<Result> subResults = ParallelPartitionScanExecutor.waitForResult(futures, this.timeoutInMillis);
        for (Result subResult : subResults) {
            result.combine(subResult);
        }
    }

    protected Future<Result> runPartitionScanForPartition(String name, Predicate predicate, int partitionId, Result result) {
        QueryPartitionCallable task = new QueryPartitionCallable(name, predicate, partitionId, result);
        return this.executor.submit(task);
    }

    private static Collection<Result> waitForResult(List<Future<Result>> lsFutures, int timeoutInMillis) {
        return FutureUtil.returnWithDeadline(lsFutures, timeoutInMillis, TimeUnit.MILLISECONDS, FutureUtil.RETHROW_EVERYTHING);
    }

    private final class QueryPartitionCallable
    implements Callable<Result> {
        protected final int partition;
        protected final String name;
        protected final Predicate predicate;
        protected final Result result;

        private QueryPartitionCallable(String name, Predicate predicate, int partitionId, Result result) {
            this.name = name;
            this.predicate = predicate;
            this.partition = partitionId;
            this.result = result;
        }

        @Override
        public Result call() {
            ParallelPartitionScanExecutor.this.partitionScanRunner.run(this.name, this.predicate, this.partition, this.result);
            this.result.setPartitionIds(Collections.singletonList(this.partition));
            return this.result;
        }
    }
}

