/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.query;

import com.hazelcast.map.impl.query.PartitionScanExecutor;
import com.hazelcast.map.impl.query.PartitionScanRunner;
import com.hazelcast.map.impl.query.Result;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.QueryableEntriesSegment;
import com.hazelcast.spi.exception.RetryableHazelcastException;
import java.util.Collection;

public class CallerRunsPartitionScanExecutor
implements PartitionScanExecutor {
    private final PartitionScanRunner partitionScanRunner;

    public CallerRunsPartitionScanExecutor(PartitionScanRunner partitionScanRunner) {
        this.partitionScanRunner = partitionScanRunner;
    }

    @Override
    public void execute(String mapName, Predicate predicate, Collection<Integer> partitions, Result result) {
        RetryableHazelcastException storedException = null;
        for (Integer partitionId : partitions) {
            try {
                this.partitionScanRunner.run(mapName, predicate, partitionId, result);
            }
            catch (RetryableHazelcastException e) {
                if (storedException != null) continue;
                storedException = e;
            }
        }
        if (storedException != null) {
            throw storedException;
        }
    }

    @Override
    public QueryableEntriesSegment execute(String mapName, Predicate predicate, int partitionId, int tableIndex, int fetchSize) {
        return this.partitionScanRunner.run(mapName, predicate, partitionId, tableIndex, fetchSize);
    }
}

