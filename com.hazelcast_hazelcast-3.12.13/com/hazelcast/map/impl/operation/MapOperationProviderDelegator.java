/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.core.EntryView;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.impl.MapEntries;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.map.impl.query.Query;
import com.hazelcast.map.merge.MapMergePolicy;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;
import com.hazelcast.spi.OperationFactory;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import java.util.List;
import java.util.Set;

abstract class MapOperationProviderDelegator
implements MapOperationProvider {
    MapOperationProviderDelegator() {
    }

    abstract MapOperationProvider getDelegate();

    @Override
    public MapOperation createPutOperation(String name, Data key, Data value, long ttl, long maxIdle) {
        return this.getDelegate().createPutOperation(name, key, value, ttl, maxIdle);
    }

    @Override
    public MapOperation createTryPutOperation(String name, Data dataKey, Data value, long timeout) {
        return this.getDelegate().createTryPutOperation(name, dataKey, value, timeout);
    }

    @Override
    public MapOperation createSetOperation(String name, Data dataKey, Data value, long ttl, long maxIdle) {
        return this.getDelegate().createSetOperation(name, dataKey, value, ttl, maxIdle);
    }

    @Override
    public MapOperation createPutIfAbsentOperation(String name, Data key, Data value, long ttl, long maxIdle) {
        return this.getDelegate().createPutIfAbsentOperation(name, key, value, ttl, maxIdle);
    }

    @Override
    public MapOperation createPutTransientOperation(String name, Data key, Data value, long ttl, long maxIdle) {
        return this.getDelegate().createPutTransientOperation(name, key, value, ttl, maxIdle);
    }

    @Override
    public MapOperation createRemoveOperation(String name, Data key, boolean disableWanReplicationEvent) {
        return this.getDelegate().createRemoveOperation(name, key, disableWanReplicationEvent);
    }

    @Override
    public MapOperation createTryRemoveOperation(String name, Data dataKey, long timeout) {
        return this.getDelegate().createTryRemoveOperation(name, dataKey, timeout);
    }

    @Override
    public MapOperation createReplaceOperation(String name, Data dataKey, Data value) {
        return this.getDelegate().createReplaceOperation(name, dataKey, value);
    }

    @Override
    public MapOperation createRemoveIfSameOperation(String name, Data dataKey, Data value) {
        return this.getDelegate().createRemoveIfSameOperation(name, dataKey, value);
    }

    @Override
    public MapOperation createReplaceIfSameOperation(String name, Data dataKey, Data expect, Data update) {
        return this.getDelegate().createReplaceIfSameOperation(name, dataKey, expect, update);
    }

    @Override
    public MapOperation createDeleteOperation(String name, Data key, boolean disableWanReplicationEvent) {
        return this.getDelegate().createDeleteOperation(name, key, disableWanReplicationEvent);
    }

    @Override
    public MapOperation createClearOperation(String name) {
        return this.getDelegate().createClearOperation(name);
    }

    @Override
    public MapOperation createEntryOperation(String name, Data dataKey, EntryProcessor entryProcessor) {
        return this.getDelegate().createEntryOperation(name, dataKey, entryProcessor);
    }

    @Override
    public MapOperation createEvictOperation(String name, Data dataKey, boolean asyncBackup) {
        return this.getDelegate().createEvictOperation(name, dataKey, asyncBackup);
    }

    @Override
    public MapOperation createEvictAllOperation(String name) {
        return this.getDelegate().createEvictAllOperation(name);
    }

    @Override
    public MapOperation createContainsKeyOperation(String name, Data dataKey) {
        return this.getDelegate().createContainsKeyOperation(name, dataKey);
    }

    @Override
    public MapOperation createGetEntryViewOperation(String name, Data dataKey) {
        return this.getDelegate().createGetEntryViewOperation(name, dataKey);
    }

    @Override
    public MapOperation createGetOperation(String name, Data dataKey) {
        return this.getDelegate().createGetOperation(name, dataKey);
    }

    @Override
    public MapOperation createLoadAllOperation(String name, List<Data> keys, boolean replaceExistingValues) {
        return this.getDelegate().createLoadAllOperation(name, keys, replaceExistingValues);
    }

    @Override
    public MapOperation createPutAllOperation(String name, MapEntries mapEntries) {
        return this.getDelegate().createPutAllOperation(name, mapEntries);
    }

    @Override
    public OperationFactory createPutAllOperationFactory(String name, int[] partitions, MapEntries[] mapEntries) {
        return this.getDelegate().createPutAllOperationFactory(name, partitions, mapEntries);
    }

    @Override
    public OperationFactory createMergeOperationFactory(String name, int[] partitions, List<SplitBrainMergeTypes.MapMergeTypes>[] mergingEntries, SplitBrainMergePolicy<Data, SplitBrainMergeTypes.MapMergeTypes> mergePolicy) {
        return this.getDelegate().createMergeOperationFactory(name, partitions, mergingEntries, mergePolicy);
    }

    @Override
    public MapOperation createPutFromLoadAllOperation(String name, List<Data> keyValueSequence) {
        return this.getDelegate().createPutFromLoadAllOperation(name, keyValueSequence);
    }

    @Override
    public MapOperation createTxnDeleteOperation(String name, Data dataKey, long version) {
        return this.getDelegate().createTxnDeleteOperation(name, dataKey, version);
    }

    @Override
    public MapOperation createTxnLockAndGetOperation(String name, Data dataKey, long timeout, long ttl, String ownerUuid, boolean shouldLoad, boolean blockReads) {
        return this.getDelegate().createTxnLockAndGetOperation(name, dataKey, timeout, ttl, ownerUuid, shouldLoad, blockReads);
    }

    @Override
    public MapOperation createTxnSetOperation(String name, Data dataKey, Data value, long version, long ttl) {
        return this.getDelegate().createTxnSetOperation(name, dataKey, value, version, ttl);
    }

    @Override
    public MapOperation createSetTtlOperation(String name, Data key, long ttl) {
        return this.getDelegate().createSetTtlOperation(name, key, ttl);
    }

    @Override
    public MapOperation createLegacyMergeOperation(String name, EntryView<Data, Data> mergingEntry, MapMergePolicy policy, boolean disableWanReplicationEvent) {
        return this.getDelegate().createLegacyMergeOperation(name, mergingEntry, policy, disableWanReplicationEvent);
    }

    @Override
    public MapOperation createMergeOperation(String name, SplitBrainMergeTypes.MapMergeTypes mergingValue, SplitBrainMergePolicy<Data, SplitBrainMergeTypes.MapMergeTypes> mergePolicy, boolean disableWanReplicationEvent) {
        return this.getDelegate().createMergeOperation(name, mergingValue, mergePolicy, disableWanReplicationEvent);
    }

    @Override
    public OperationFactory createPartitionWideEntryOperationFactory(String name, EntryProcessor entryProcessor) {
        return this.getDelegate().createPartitionWideEntryOperationFactory(name, entryProcessor);
    }

    @Override
    public OperationFactory createPartitionWideEntryWithPredicateOperationFactory(String name, EntryProcessor entryProcessor, Predicate predicate) {
        return this.getDelegate().createPartitionWideEntryWithPredicateOperationFactory(name, entryProcessor, predicate);
    }

    @Override
    public OperationFactory createMultipleEntryOperationFactory(String name, Set<Data> keys, EntryProcessor entryProcessor) {
        return this.getDelegate().createMultipleEntryOperationFactory(name, keys, entryProcessor);
    }

    @Override
    public OperationFactory createContainsValueOperationFactory(String name, Data testValue) {
        return this.getDelegate().createContainsValueOperationFactory(name, testValue);
    }

    @Override
    public OperationFactory createEvictAllOperationFactory(String name) {
        return this.getDelegate().createEvictAllOperationFactory(name);
    }

    @Override
    public OperationFactory createClearOperationFactory(String name) {
        return this.getDelegate().createClearOperationFactory(name);
    }

    @Override
    public OperationFactory createMapFlushOperationFactory(String name) {
        return this.getDelegate().createMapFlushOperationFactory(name);
    }

    @Override
    public OperationFactory createLoadAllOperationFactory(String name, List<Data> keys, boolean replaceExistingValues) {
        return this.getDelegate().createLoadAllOperationFactory(name, keys, replaceExistingValues);
    }

    @Override
    public OperationFactory createGetAllOperationFactory(String name, List<Data> keys) {
        return this.getDelegate().createGetAllOperationFactory(name, keys);
    }

    @Override
    public OperationFactory createMapSizeOperationFactory(String name) {
        return this.getDelegate().createMapSizeOperationFactory(name);
    }

    @Override
    public MapOperation createMapFlushOperation(String name) {
        return this.getDelegate().createMapFlushOperation(name);
    }

    @Override
    public MapOperation createLoadMapOperation(String name, boolean replaceExistingValues) {
        return this.getDelegate().createLoadMapOperation(name, replaceExistingValues);
    }

    @Override
    public MapOperation createFetchKeysOperation(String name, int lastTableIndex, int fetchSize) {
        return this.getDelegate().createFetchKeysOperation(name, lastTableIndex, fetchSize);
    }

    @Override
    public MapOperation createFetchEntriesOperation(String name, int lastTableIndex, int fetchSize) {
        return this.getDelegate().createFetchEntriesOperation(name, lastTableIndex, fetchSize);
    }

    @Override
    public MapOperation createQueryOperation(Query query) {
        return this.getDelegate().createQueryOperation(query);
    }

    @Override
    public MapOperation createQueryPartitionOperation(Query query) {
        return this.getDelegate().createQueryPartitionOperation(query);
    }

    @Override
    public MapOperation createFetchWithQueryOperation(String name, int lastTableIndex, int fetchSize, Query query) {
        return this.getDelegate().createFetchWithQueryOperation(name, lastTableIndex, fetchSize, query);
    }
}

