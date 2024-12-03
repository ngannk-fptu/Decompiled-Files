/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.query;

import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.logging.ILogger;
import com.hazelcast.map.impl.LocalMapStatsProvider;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.query.PartitionScanExecutor;
import com.hazelcast.map.impl.query.Query;
import com.hazelcast.map.impl.query.QueryResultSizeLimiter;
import com.hazelcast.map.impl.query.Result;
import com.hazelcast.map.impl.query.ResultProcessor;
import com.hazelcast.map.impl.query.ResultProcessorRegistry;
import com.hazelcast.map.impl.query.ResultSegment;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.QueryableEntriesSegment;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.query.impl.predicates.QueryOptimizer;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.OperationService;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class QueryRunner {
    protected final MapServiceContext mapServiceContext;
    protected final NodeEngine nodeEngine;
    protected final ILogger logger;
    protected final QueryResultSizeLimiter queryResultSizeLimiter;
    protected final InternalSerializationService serializationService;
    protected final QueryOptimizer queryOptimizer;
    protected final OperationService operationService;
    protected final ClusterService clusterService;
    protected final LocalMapStatsProvider localMapStatsProvider;
    protected final PartitionScanExecutor partitionScanExecutor;
    protected final ResultProcessorRegistry resultProcessorRegistry;

    public QueryRunner(MapServiceContext mapServiceContext, QueryOptimizer optimizer, PartitionScanExecutor partitionScanExecutor, ResultProcessorRegistry resultProcessorRegistry) {
        this.mapServiceContext = mapServiceContext;
        this.nodeEngine = mapServiceContext.getNodeEngine();
        this.serializationService = (InternalSerializationService)this.nodeEngine.getSerializationService();
        this.logger = this.nodeEngine.getLogger(this.getClass());
        this.queryResultSizeLimiter = new QueryResultSizeLimiter(mapServiceContext, this.logger);
        this.queryOptimizer = optimizer;
        this.operationService = this.nodeEngine.getOperationService();
        this.clusterService = this.nodeEngine.getClusterService();
        this.localMapStatsProvider = mapServiceContext.getLocalMapStatsProvider();
        this.partitionScanExecutor = partitionScanExecutor;
        this.resultProcessorRegistry = resultProcessorRegistry;
    }

    public ResultSegment runPartitionScanQueryOnPartitionChunk(Query query, int partitionId, int tableIndex, int fetchSize) {
        MapContainer mapContainer = this.mapServiceContext.getMapContainer(query.getMapName());
        Predicate predicate = this.queryOptimizer.optimize(query.getPredicate(), mapContainer.getIndexes(partitionId));
        QueryableEntriesSegment entries = this.partitionScanExecutor.execute(query.getMapName(), predicate, partitionId, tableIndex, fetchSize);
        ResultProcessor processor = this.resultProcessorRegistry.get(query.getResultType());
        Object result = processor.populateResult(query, Long.MAX_VALUE, entries.getEntries(), Collections.singletonList(partitionId));
        return new ResultSegment((Result)result, entries.getNextTableIndexToReadFrom());
    }

    public Result runIndexOrPartitionScanQueryOnOwnedPartitions(Query query) {
        Result result;
        Predicate predicate;
        Collection<QueryableEntry> entries;
        int migrationStamp = this.getMigrationStamp();
        Collection<Integer> initialPartitions = this.mapServiceContext.getOwnedPartitions();
        MapContainer mapContainer = this.mapServiceContext.getMapContainer(query.getMapName());
        Indexes indexes = mapContainer.getIndexes();
        if (indexes == null) {
            indexes = mapContainer.getIndexes(initialPartitions.iterator().next());
        }
        if ((entries = this.runUsingGlobalIndexSafely(predicate = this.queryOptimizer.optimize(query.getPredicate(), indexes), mapContainer, migrationStamp, initialPartitions.size())) == null) {
            result = this.runUsingPartitionScanSafely(query, predicate, initialPartitions, migrationStamp);
            if (result == null) {
                result = this.populateEmptyResult(query, initialPartitions);
            }
        } else {
            result = this.populateNonEmptyResult(query, entries, initialPartitions);
        }
        return result;
    }

    public Result runIndexQueryOnOwnedPartitions(Query query) {
        Predicate predicate;
        Collection<QueryableEntry> entries;
        int migrationStamp = this.getMigrationStamp();
        Collection<Integer> initialPartitions = this.mapServiceContext.getOwnedPartitions();
        MapContainer mapContainer = this.mapServiceContext.getMapContainer(query.getMapName());
        Indexes indexes = mapContainer.getIndexes();
        if (indexes == null) {
            indexes = mapContainer.getIndexes(initialPartitions.iterator().next());
        }
        Result result = (entries = this.runUsingGlobalIndexSafely(predicate = this.queryOptimizer.optimize(query.getPredicate(), indexes), mapContainer, migrationStamp, initialPartitions.size())) == null ? this.populateEmptyResult(query, initialPartitions) : this.populateNonEmptyResult(query, entries, initialPartitions);
        return result;
    }

    public Result runPartitionIndexOrPartitionScanQueryOnGivenOwnedPartition(Query query, int partitionId) {
        Result result;
        MapContainer mapContainer = this.mapServiceContext.getMapContainer(query.getMapName());
        List<Integer> partitions = Collections.singletonList(partitionId);
        Predicate predicate = this.queryOptimizer.optimize(query.getPredicate(), mapContainer.getIndexes(partitionId));
        Set<QueryableEntry> entries = null;
        Indexes indexes = mapContainer.getIndexes(partitionId);
        if (indexes != null && !indexes.isGlobal()) {
            entries = indexes.query(predicate, partitions.size());
        }
        if (entries == null) {
            result = this.createResult(query, partitions);
            this.partitionScanExecutor.execute(query.getMapName(), predicate, partitions, result);
            result.completeConstruction(partitions);
        } else {
            result = this.populateNonEmptyResult(query, entries, partitions);
        }
        return result;
    }

    Result runPartitionScanQueryOnGivenOwnedPartition(Query query, int partitionId) {
        MapContainer mapContainer = this.mapServiceContext.getMapContainer(query.getMapName());
        Predicate predicate = this.queryOptimizer.optimize(query.getPredicate(), mapContainer.getIndexes(partitionId));
        List<Integer> partitions = Collections.singletonList(partitionId);
        Result result = this.createResult(query, partitions);
        this.partitionScanExecutor.execute(query.getMapName(), predicate, partitions, result);
        result.completeConstruction(partitions);
        return result;
    }

    private Result createResult(Query query, Collection<Integer> partitions) {
        return query.createResult(this.serializationService, this.queryResultSizeLimiter.getNodeResultLimit(partitions.size()));
    }

    protected Result populateEmptyResult(Query query, Collection<Integer> initialPartitions) {
        return this.resultProcessorRegistry.get(query.getResultType()).populateResult(query, this.queryResultSizeLimiter.getNodeResultLimit(initialPartitions.size()));
    }

    protected Result populateNonEmptyResult(Query query, Collection<QueryableEntry> entries, Collection<Integer> initialPartitions) {
        ResultProcessor processor = this.resultProcessorRegistry.get(query.getResultType());
        return processor.populateResult(query, this.queryResultSizeLimiter.getNodeResultLimit(initialPartitions.size()), entries, initialPartitions);
    }

    protected Collection<QueryableEntry> runUsingGlobalIndexSafely(Predicate predicate, MapContainer mapContainer, int migrationStamp, int ownedPartitionCount) {
        if (!this.validateMigrationStamp(migrationStamp)) {
            return null;
        }
        Indexes indexes = mapContainer.getIndexes();
        if (indexes == null) {
            return null;
        }
        if (!indexes.isGlobal()) {
            return null;
        }
        Set<QueryableEntry> entries = indexes.query(predicate, ownedPartitionCount);
        if (entries == null) {
            return null;
        }
        if (this.validateMigrationStamp(migrationStamp)) {
            return entries;
        }
        return null;
    }

    protected Result runUsingPartitionScanSafely(Query query, Predicate predicate, Collection<Integer> partitions, int migrationStamp) {
        if (!this.validateMigrationStamp(migrationStamp)) {
            return null;
        }
        Result result = this.createResult(query, partitions);
        this.partitionScanExecutor.execute(query.getMapName(), predicate, partitions, result);
        if (this.validateMigrationStamp(migrationStamp)) {
            result.completeConstruction(partitions);
            return result;
        }
        return null;
    }

    private int getMigrationStamp() {
        return this.mapServiceContext.getService().getMigrationStamp();
    }

    private boolean validateMigrationStamp(int migrationStamp) {
        return this.mapServiceContext.getService().validateMigrationStamp(migrationStamp);
    }
}

