/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.core.EntryView;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.impl.MapEntries;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.map.impl.operation.MapOperationProviderDelegator;
import com.hazelcast.map.impl.query.Query;
import com.hazelcast.map.merge.MapMergePolicy;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;
import com.hazelcast.spi.OperationFactory;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import java.util.List;
import java.util.Set;

public class WANAwareOperationProvider
extends MapOperationProviderDelegator {
    private final MapServiceContext mapServiceContext;
    private final MapOperationProvider operationProviderDelegate;

    public WANAwareOperationProvider(MapServiceContext mapServiceContext, MapOperationProvider operationProviderDelegate) {
        this.mapServiceContext = mapServiceContext;
        this.operationProviderDelegate = operationProviderDelegate;
    }

    @Override
    MapOperationProvider getDelegate() {
        return this.operationProviderDelegate;
    }

    @Override
    public MapOperation createPutOperation(String name, Data key, Data value, long ttl, long maxIdle) {
        this.checkWanReplicationQueues(name);
        return this.getDelegate().createPutOperation(name, key, value, ttl, maxIdle);
    }

    @Override
    public MapOperation createTryPutOperation(String name, Data dataKey, Data value, long timeout) {
        this.checkWanReplicationQueues(name);
        return this.getDelegate().createTryPutOperation(name, dataKey, value, timeout);
    }

    @Override
    public MapOperation createSetOperation(String name, Data dataKey, Data value, long ttl, long maxIdle) {
        this.checkWanReplicationQueues(name);
        return this.getDelegate().createSetOperation(name, dataKey, value, ttl, maxIdle);
    }

    @Override
    public MapOperation createPutIfAbsentOperation(String name, Data key, Data value, long ttl, long maxIdle) {
        this.checkWanReplicationQueues(name);
        return this.getDelegate().createPutIfAbsentOperation(name, key, value, ttl, maxIdle);
    }

    @Override
    public MapOperation createPutTransientOperation(String name, Data key, Data value, long ttl, long maxIdle) {
        this.checkWanReplicationQueues(name);
        return this.getDelegate().createPutTransientOperation(name, key, value, ttl, maxIdle);
    }

    @Override
    public MapOperation createRemoveOperation(String name, Data key, boolean disableWanReplicationEvent) {
        this.checkWanReplicationQueues(name);
        return this.getDelegate().createRemoveOperation(name, key, disableWanReplicationEvent);
    }

    @Override
    public MapOperation createSetTtlOperation(String name, Data key, long ttl) {
        this.checkWanReplicationQueues(name);
        return this.getDelegate().createSetTtlOperation(name, key, ttl);
    }

    @Override
    public MapOperation createTryRemoveOperation(String name, Data dataKey, long timeout) {
        this.checkWanReplicationQueues(name);
        return this.getDelegate().createTryRemoveOperation(name, dataKey, timeout);
    }

    @Override
    public MapOperation createReplaceOperation(String name, Data dataKey, Data value) {
        this.checkWanReplicationQueues(name);
        return this.getDelegate().createReplaceOperation(name, dataKey, value);
    }

    @Override
    public MapOperation createRemoveIfSameOperation(String name, Data dataKey, Data value) {
        this.checkWanReplicationQueues(name);
        return this.getDelegate().createRemoveIfSameOperation(name, dataKey, value);
    }

    @Override
    public MapOperation createReplaceIfSameOperation(String name, Data dataKey, Data expect, Data update) {
        this.checkWanReplicationQueues(name);
        return this.getDelegate().createReplaceIfSameOperation(name, dataKey, expect, update);
    }

    @Override
    public MapOperation createDeleteOperation(String name, Data key, boolean disableWanReplicationEvent) {
        this.checkWanReplicationQueues(name);
        return this.getDelegate().createDeleteOperation(name, key, disableWanReplicationEvent);
    }

    @Override
    public MapOperation createEntryOperation(String name, Data dataKey, EntryProcessor entryProcessor) {
        this.checkWanReplicationQueues(name);
        return this.getDelegate().createEntryOperation(name, dataKey, entryProcessor);
    }

    @Override
    public MapOperation createQueryOperation(Query query) {
        this.checkWanReplicationQueues(query.getMapName());
        return this.getDelegate().createQueryOperation(query);
    }

    @Override
    public MapOperation createQueryPartitionOperation(Query query) {
        this.checkWanReplicationQueues(query.getMapName());
        return this.getDelegate().createQueryPartitionOperation(query);
    }

    @Override
    public MapOperation createPutAllOperation(String name, MapEntries mapEntries) {
        this.checkWanReplicationQueues(name);
        return this.getDelegate().createPutAllOperation(name, mapEntries);
    }

    @Override
    public OperationFactory createPutAllOperationFactory(String name, int[] partitions, MapEntries[] mapEntries) {
        this.checkWanReplicationQueues(name);
        return this.getDelegate().createPutAllOperationFactory(name, partitions, mapEntries);
    }

    @Override
    public MapOperation createPutFromLoadAllOperation(String name, List<Data> keyValueSequence) {
        this.checkWanReplicationQueues(name);
        return this.getDelegate().createPutFromLoadAllOperation(name, keyValueSequence);
    }

    @Override
    public MapOperation createTxnDeleteOperation(String name, Data dataKey, long version) {
        this.checkWanReplicationQueues(name);
        return this.getDelegate().createTxnDeleteOperation(name, dataKey, version);
    }

    @Override
    public MapOperation createTxnSetOperation(String name, Data dataKey, Data value, long version, long ttl) {
        this.checkWanReplicationQueues(name);
        return this.getDelegate().createTxnSetOperation(name, dataKey, value, version, ttl);
    }

    @Override
    public MapOperation createLegacyMergeOperation(String name, EntryView<Data, Data> mergingEntry, MapMergePolicy policy, boolean disableWanReplicationEvent) {
        this.checkWanReplicationQueues(name);
        return this.getDelegate().createLegacyMergeOperation(name, mergingEntry, policy, disableWanReplicationEvent);
    }

    @Override
    public MapOperation createMergeOperation(String name, SplitBrainMergeTypes.MapMergeTypes mergingValue, SplitBrainMergePolicy<Data, SplitBrainMergeTypes.MapMergeTypes> mergePolicy, boolean disableWanReplicationEvent) {
        this.checkWanReplicationQueues(name);
        return this.getDelegate().createMergeOperation(name, mergingValue, mergePolicy, disableWanReplicationEvent);
    }

    @Override
    public OperationFactory createPartitionWideEntryOperationFactory(String name, EntryProcessor entryProcessor) {
        this.checkWanReplicationQueues(name);
        return this.getDelegate().createPartitionWideEntryOperationFactory(name, entryProcessor);
    }

    @Override
    public OperationFactory createPartitionWideEntryWithPredicateOperationFactory(String name, EntryProcessor entryProcessor, Predicate predicate) {
        this.checkWanReplicationQueues(name);
        return this.getDelegate().createPartitionWideEntryWithPredicateOperationFactory(name, entryProcessor, predicate);
    }

    @Override
    public OperationFactory createMultipleEntryOperationFactory(String name, Set<Data> keys, EntryProcessor entryProcessor) {
        this.checkWanReplicationQueues(name);
        return this.getDelegate().createMultipleEntryOperationFactory(name, keys, entryProcessor);
    }

    private void checkWanReplicationQueues(String name) {
        this.mapServiceContext.getMapContainer(name).checkWanReplicationQueues();
    }
}

