/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.recordstore;

import com.hazelcast.logging.ILogger;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.mapstore.MapDataStore;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.map.impl.operation.RemoveFromLoadAllOperation;
import com.hazelcast.map.impl.recordstore.RecordStore;
import com.hazelcast.map.impl.recordstore.RecordStoreLoader;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.ExecutionService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationAccessor;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.util.ExceptionUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

class BasicRecordStoreLoader
implements RecordStoreLoader {
    private final ILogger logger;
    private final String name;
    private final MapServiceContext mapServiceContext;
    private final MapDataStore mapDataStore;
    private final int partitionId;

    BasicRecordStoreLoader(RecordStore recordStore) {
        MapContainer mapContainer = recordStore.getMapContainer();
        this.name = mapContainer.getName();
        this.mapServiceContext = mapContainer.getMapServiceContext();
        this.partitionId = recordStore.getPartitionId();
        this.mapDataStore = recordStore.getMapDataStore();
        this.logger = this.mapServiceContext.getNodeEngine().getLogger(this.getClass());
    }

    @Override
    public Future<?> loadValues(List<Data> keys, boolean replaceExistingValues) {
        GivenKeysLoaderTask task = new GivenKeysLoaderTask(keys, replaceExistingValues);
        return this.executeTask("hz:map-load", task);
    }

    private Future<?> executeTask(String executorName, Callable task) {
        return this.getExecutionService().submit(executorName, task);
    }

    private ExecutionService getExecutionService() {
        NodeEngine nodeEngine = this.mapServiceContext.getNodeEngine();
        return nodeEngine.getExecutionService();
    }

    private void loadValuesInternal(List<Data> keys, boolean replaceExistingValues) throws Exception {
        if (!replaceExistingValues) {
            Future removeKeysFuture = this.removeExistingKeys(keys);
            removeKeysFuture.get();
        }
        this.removeUnloadableKeys(keys);
        if (keys.isEmpty()) {
            return;
        }
        List<Future> futures = this.doBatchLoad(keys);
        for (Future future : futures) {
            future.get();
        }
    }

    private Future removeExistingKeys(List<Data> keys) {
        OperationService operationService = this.mapServiceContext.getNodeEngine().getOperationService();
        RemoveFromLoadAllOperation operation = new RemoveFromLoadAllOperation(this.name, keys);
        return operationService.invokeOnPartition("hz:impl:mapService", operation, this.partitionId);
    }

    private List<Future> doBatchLoad(List<Data> keys) {
        Queue<List<Data>> batchChunks = this.createBatchChunks(keys);
        int size = batchChunks.size();
        ArrayList<Future> futures = new ArrayList<Future>(size);
        while (!batchChunks.isEmpty()) {
            List<Data> chunk = batchChunks.poll();
            List<Data> keyValueSequence = this.loadAndGet(chunk);
            if (keyValueSequence.isEmpty()) continue;
            futures.add(this.sendOperation(keyValueSequence));
        }
        return futures;
    }

    private Queue<List<Data>> createBatchChunks(List<Data> keys) {
        List<Data> tmpKeys;
        LinkedList<List<Data>> chunks = new LinkedList<List<Data>>();
        int loadBatchSize = this.getLoadBatchSize();
        int page = 0;
        while ((tmpKeys = this.getBatchChunk(keys, loadBatchSize, page++)) != null) {
            chunks.add(tmpKeys);
        }
        return chunks;
    }

    private List<Data> loadAndGet(List<Data> keys) {
        try {
            Map entries = this.mapDataStore.loadAll(keys);
            return this.getKeyValueSequence(entries);
        }
        catch (Throwable t) {
            this.logger.warning("Could not load keys from map store", t);
            throw ExceptionUtil.rethrow(t);
        }
    }

    private List<Data> getKeyValueSequence(Map<?, ?> entries) {
        if (entries == null || entries.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<Data> keyValueSequence = new ArrayList<Data>(entries.size() * 2);
        for (Map.Entry<?, ?> entry : entries.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            Data dataKey = this.mapServiceContext.toData(key);
            Data dataValue = this.mapServiceContext.toData(value);
            keyValueSequence.add(dataKey);
            keyValueSequence.add(dataValue);
        }
        return keyValueSequence;
    }

    private List<Data> getBatchChunk(List<Data> list, int pageSize, int pageNumber) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        int start = pageNumber * pageSize;
        int end = Math.min(start + pageSize, list.size());
        if (start >= end) {
            return null;
        }
        return list.subList(start, end);
    }

    private Future<?> sendOperation(List<Data> keyValueSequence) {
        OperationService operationService = this.mapServiceContext.getNodeEngine().getOperationService();
        Operation operation = this.createOperation(keyValueSequence);
        return operationService.invokeOnPartition("hz:impl:mapService", operation, this.partitionId);
    }

    private Operation createOperation(List<Data> keyValueSequence) {
        NodeEngine nodeEngine = this.mapServiceContext.getNodeEngine();
        MapOperationProvider operationProvider = this.mapServiceContext.getMapOperationProvider(this.name);
        MapOperation operation = operationProvider.createPutFromLoadAllOperation(this.name, keyValueSequence);
        operation.setNodeEngine(nodeEngine);
        operation.setPartitionId(this.partitionId);
        OperationAccessor.setCallerAddress(operation, nodeEngine.getThisAddress());
        operation.setCallerUuid(nodeEngine.getLocalMember().getUuid());
        operation.setServiceName("hz:impl:mapService");
        return operation;
    }

    private void removeUnloadableKeys(Collection<Data> keys) {
        if (keys == null || keys.isEmpty()) {
            return;
        }
        Iterator<Data> iterator = keys.iterator();
        while (iterator.hasNext()) {
            Data key = iterator.next();
            if (this.mapDataStore.loadable(key)) continue;
            iterator.remove();
        }
    }

    private int getLoadBatchSize() {
        return this.mapServiceContext.getNodeEngine().getProperties().getInteger(GroupProperty.MAP_LOAD_CHUNK_SIZE);
    }

    private final class GivenKeysLoaderTask
    implements Callable<Object> {
        private final List<Data> keys;
        private final boolean replaceExistingValues;

        private GivenKeysLoaderTask(List<Data> keys, boolean replaceExistingValues) {
            this.keys = keys;
            this.replaceExistingValues = replaceExistingValues;
        }

        @Override
        public Object call() throws Exception {
            BasicRecordStoreLoader.this.loadValuesInternal(this.keys, this.replaceExistingValues);
            return null;
        }
    }
}

