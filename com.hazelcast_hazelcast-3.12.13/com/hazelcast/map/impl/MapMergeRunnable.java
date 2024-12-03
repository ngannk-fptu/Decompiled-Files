/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MergePolicyConfig;
import com.hazelcast.core.EntryView;
import com.hazelcast.map.impl.EntryViews;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.MapSplitBrainHandlerService;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.map.impl.record.Record;
import com.hazelcast.map.impl.recordstore.RecordStore;
import com.hazelcast.map.merge.MapMergePolicy;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationFactory;
import com.hazelcast.spi.impl.merge.AbstractMergeRunnable;
import com.hazelcast.spi.impl.merge.MergingValueFactory;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.Clock;
import com.hazelcast.util.function.BiConsumer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

class MapMergeRunnable
extends AbstractMergeRunnable<Data, Data, RecordStore, SplitBrainMergeTypes.MapMergeTypes> {
    private final MapServiceContext mapServiceContext;

    MapMergeRunnable(Collection<RecordStore> mergingStores, MapSplitBrainHandlerService splitBrainHandlerService, MapServiceContext mapServiceContext) {
        super("hz:impl:mapService", mergingStores, splitBrainHandlerService, mapServiceContext.getNodeEngine());
        this.mapServiceContext = mapServiceContext;
    }

    @Override
    protected void mergeStore(RecordStore store, BiConsumer<Integer, SplitBrainMergeTypes.MapMergeTypes> consumer) {
        long now = Clock.currentTimeMillis();
        int partitionId = store.getPartitionId();
        Iterator<Record> iterator = store.iterator(now, false);
        while (iterator.hasNext()) {
            Record record = iterator.next();
            Data dataKey = this.toHeapData(record.getKey());
            Data dataValue = this.toHeapData(record.getValue());
            consumer.accept(partitionId, MergingValueFactory.createMergingEntry((SerializationService)this.getSerializationService(), dataKey, dataValue, record));
        }
    }

    @Override
    protected void mergeStoreLegacy(RecordStore store, BiConsumer<Integer, Operation> consumer) {
        long now = Clock.currentTimeMillis();
        int partitionId = store.getPartitionId();
        String name = store.getName();
        MapOperationProvider operationProvider = this.mapServiceContext.getMapOperationProvider(name);
        MapMergePolicy mergePolicy = (MapMergePolicy)this.getMergePolicy(name);
        Iterator<Record> iterator = store.iterator(now, false);
        while (iterator.hasNext()) {
            Record record = iterator.next();
            Data key = record.getKey();
            Data value = this.toData(record.getValue());
            EntryView<Data, Data> entryView = EntryViews.createSimpleEntryView(key, value, record);
            MapOperation operation = operationProvider.createLegacyMergeOperation(name, entryView, mergePolicy, false);
            consumer.accept(partitionId, operation);
        }
    }

    @Override
    protected int getBatchSize(String dataStructureName) {
        MapConfig mapConfig = this.getMapConfig(dataStructureName);
        MergePolicyConfig mergePolicyConfig = mapConfig.getMergePolicyConfig();
        return mergePolicyConfig.getBatchSize();
    }

    @Override
    protected InMemoryFormat getInMemoryFormat(String dataStructureName) {
        MapConfig mapConfig = this.getMapConfig(dataStructureName);
        return mapConfig.getInMemoryFormat();
    }

    @Override
    protected Object getMergePolicy(String dataStructureName) {
        return this.mapServiceContext.getMergePolicy(dataStructureName);
    }

    @Override
    protected String getDataStructureName(RecordStore recordStore) {
        return recordStore.getName();
    }

    @Override
    protected int getPartitionId(RecordStore store) {
        return store.getPartitionId();
    }

    @Override
    protected OperationFactory createMergeOperationFactory(String dataStructureName, SplitBrainMergePolicy<Data, SplitBrainMergeTypes.MapMergeTypes> mergePolicy, int[] partitions, List<SplitBrainMergeTypes.MapMergeTypes>[] entries) {
        MapOperationProvider operationProvider = this.mapServiceContext.getMapOperationProvider(dataStructureName);
        return operationProvider.createMergeOperationFactory(dataStructureName, partitions, entries, mergePolicy);
    }

    private MapConfig getMapConfig(String dataStructureName) {
        MapContainer mapContainer = this.mapServiceContext.getMapContainer(dataStructureName);
        return mapContainer.getMapConfig();
    }
}

