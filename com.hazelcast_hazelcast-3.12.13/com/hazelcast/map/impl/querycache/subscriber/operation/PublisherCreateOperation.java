/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber.operation;

import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.query.Query;
import com.hazelcast.map.impl.query.QueryEngine;
import com.hazelcast.map.impl.query.QueryResult;
import com.hazelcast.map.impl.query.QueryResultRow;
import com.hazelcast.map.impl.query.Target;
import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfo;
import com.hazelcast.map.impl.querycache.accumulator.AccumulatorInfoSupplier;
import com.hazelcast.map.impl.querycache.event.QueryCacheEventData;
import com.hazelcast.map.impl.querycache.publisher.MapListenerRegistry;
import com.hazelcast.map.impl.querycache.publisher.MapPublisherRegistry;
import com.hazelcast.map.impl.querycache.publisher.PartitionAccumulatorRegistry;
import com.hazelcast.map.impl.querycache.publisher.PublisherContext;
import com.hazelcast.map.impl.querycache.publisher.PublisherRegistry;
import com.hazelcast.map.impl.querycache.publisher.QueryCacheListenerRegistry;
import com.hazelcast.map.impl.querycache.subscriber.operation.ReadAndResetAccumulatorOperation;
import com.hazelcast.map.impl.querycache.utils.QueryCacheUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.OperationService;
import com.hazelcast.util.FutureUtil;
import com.hazelcast.util.IterationType;
import com.hazelcast.util.collection.Int2ObjectHashMap;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class PublisherCreateOperation
extends MapOperation {
    private static final long ACCUMULATOR_READ_OPERATION_TIMEOUT_MINUTES = 5L;
    private AccumulatorInfo info;
    private transient QueryResult queryResult;

    public PublisherCreateOperation() {
    }

    public PublisherCreateOperation(AccumulatorInfo info) {
        super(info.getMapName());
        this.info = info;
    }

    @Override
    public void run() throws Exception {
        boolean populate = this.info.isPopulate();
        if (populate) {
            this.info.setPublishable(false);
        }
        this.init();
        this.queryResult = populate ? this.createSnapshot() : null;
    }

    @Override
    protected void writeInternal(ObjectDataOutput out) throws IOException {
        super.writeInternal(out);
        out.writeObject(this.info);
    }

    @Override
    protected void readInternal(ObjectDataInput in) throws IOException {
        super.readInternal(in);
        this.info = (AccumulatorInfo)in.readObject();
    }

    @Override
    public Object getResponse() {
        return this.queryResult;
    }

    private void init() {
        this.registerAccumulatorInfo();
        this.registerPublisherAccumulator();
        this.registerLocalIMapListener();
    }

    private void registerLocalIMapListener() {
        String mapName = this.info.getMapName();
        String cacheId = this.info.getCacheId();
        PublisherContext publisherContext = this.getPublisherContext();
        MapListenerRegistry registry = publisherContext.getMapListenerRegistry();
        QueryCacheListenerRegistry listenerRegistry = registry.getOrCreate(mapName);
        listenerRegistry.getOrCreate(cacheId);
    }

    private void registerAccumulatorInfo() {
        String mapName = this.info.getMapName();
        String cacheId = this.info.getCacheId();
        PublisherContext publisherContext = this.getPublisherContext();
        AccumulatorInfoSupplier infoSupplier = publisherContext.getAccumulatorInfoSupplier();
        infoSupplier.putIfAbsent(mapName, cacheId, this.info);
    }

    private void registerPublisherAccumulator() {
        String mapName = this.info.getMapName();
        String cacheId = this.info.getCacheId();
        PublisherContext publisherContext = this.getPublisherContext();
        MapPublisherRegistry mapPublisherRegistry = publisherContext.getMapPublisherRegistry();
        PublisherRegistry publisherRegistry = mapPublisherRegistry.getOrCreate(mapName);
        publisherRegistry.remove(cacheId);
        PartitionAccumulatorRegistry partitionAccumulatorRegistry = publisherRegistry.getOrCreate(cacheId);
        partitionAccumulatorRegistry.setUuid(this.getCallerUuid());
    }

    private PublisherContext getPublisherContext() {
        QueryCacheContext queryCacheContext = this.getContext();
        return queryCacheContext.getPublisherContext();
    }

    private QueryCacheContext getContext() {
        return this.mapServiceContext.getQueryCacheContext();
    }

    private QueryResult createSnapshot() throws Exception {
        QueryResult queryResult = this.runInitialQuery();
        this.replayEventsOverResultSet(queryResult);
        return queryResult;
    }

    private QueryResult runInitialQuery() {
        QueryEngine queryEngine = this.mapServiceContext.getQueryEngine(this.name);
        IterationType iterationType = this.info.isIncludeValue() ? IterationType.ENTRY : IterationType.KEY;
        Query query = Query.of().mapName(this.name).predicate(this.info.getPredicate()).iterationType(iterationType).build();
        return (QueryResult)queryEngine.execute(query, Target.LOCAL_NODE);
    }

    private void replayEventsOverResultSet(QueryResult queryResult) throws Exception {
        Map<Integer, Future<Object>> future = this.readAccumulators();
        for (Map.Entry<Integer, Future<Object>> entry : future.entrySet()) {
            int partitionId = entry.getKey();
            Object eventsInOneAcc = entry.getValue().get();
            if (eventsInOneAcc == null) continue;
            eventsInOneAcc = this.mapServiceContext.toObject(eventsInOneAcc);
            List eventDataList = (List)eventsInOneAcc;
            for (QueryCacheEventData eventData : eventDataList) {
                if (eventData.getDataKey() == null) {
                    this.removePartitionResults(queryResult, partitionId);
                    continue;
                }
                this.add(queryResult, this.newQueryResultRow(eventData));
            }
        }
    }

    private void removePartitionResults(QueryResult queryResult, int partitionId) {
        List<QueryResultRow> rows = queryResult.getRows();
        Iterator<QueryResultRow> iterator = rows.iterator();
        while (iterator.hasNext()) {
            QueryResultRow resultRow = iterator.next();
            if (this.getPartitionId(resultRow) != partitionId) continue;
            iterator.remove();
        }
    }

    private int getPartitionId(QueryResultRow resultRow) {
        return this.getNodeEngine().getPartitionService().getPartitionId(resultRow.getKey());
    }

    private Map<Integer, Future<Object>> readAccumulators() {
        String mapName = this.info.getMapName();
        String cacheId = this.info.getCacheId();
        Collection<Integer> partitionIds = this.getPartitionIdsOfAccumulators();
        if (partitionIds.isEmpty()) {
            return Collections.emptyMap();
        }
        Int2ObjectHashMap<Future<Object>> futuresByPartitionId = new Int2ObjectHashMap<Future<Object>>(partitionIds.size());
        for (Integer partitionId : partitionIds) {
            futuresByPartitionId.put(partitionId, this.readAndResetAccumulator(mapName, cacheId, partitionId));
        }
        PublisherCreateOperation.waitResult(futuresByPartitionId.values());
        return futuresByPartitionId;
    }

    private Future<Object> readAndResetAccumulator(String mapName, String cacheId, Integer partitionId) {
        ReadAndResetAccumulatorOperation operation = new ReadAndResetAccumulatorOperation(mapName, cacheId);
        OperationService operationService = this.getNodeEngine().getOperationService();
        return operationService.invokeOnPartition("hz:impl:mapService", operation, partitionId);
    }

    private void add(QueryResult result, QueryResultRow row) {
        result.addRow(row);
    }

    private QueryResultRow newQueryResultRow(QueryCacheEventData eventData) {
        Data dataKey = eventData.getDataKey();
        Data dataNewValue = eventData.getDataNewValue();
        return new QueryResultRow(dataKey, dataNewValue);
    }

    private Collection<Integer> getPartitionIdsOfAccumulators() {
        String mapName = this.info.getMapName();
        String cacheId = this.info.getCacheId();
        QueryCacheContext context = this.getContext();
        return QueryCacheUtil.getAccumulators(context, mapName, cacheId).keySet();
    }

    private static Collection<Object> waitResult(Collection<Future<Object>> lsFutures) {
        return FutureUtil.returnWithDeadline(lsFutures, 5L, TimeUnit.MINUTES, FutureUtil.RETHROW_EVERYTHING);
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 126;
    }
}

