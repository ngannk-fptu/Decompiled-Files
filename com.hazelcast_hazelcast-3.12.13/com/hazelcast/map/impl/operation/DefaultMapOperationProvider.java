/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.core.EntryView;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.impl.MapEntries;
import com.hazelcast.map.impl.operation.ClearOperation;
import com.hazelcast.map.impl.operation.ClearOperationFactory;
import com.hazelcast.map.impl.operation.ContainsKeyOperation;
import com.hazelcast.map.impl.operation.ContainsValueOperationFactory;
import com.hazelcast.map.impl.operation.DeleteOperation;
import com.hazelcast.map.impl.operation.EntryOperation;
import com.hazelcast.map.impl.operation.EvictAllOperation;
import com.hazelcast.map.impl.operation.EvictAllOperationFactory;
import com.hazelcast.map.impl.operation.EvictOperation;
import com.hazelcast.map.impl.operation.GetEntryViewOperation;
import com.hazelcast.map.impl.operation.GetOperation;
import com.hazelcast.map.impl.operation.LegacyMergeOperation;
import com.hazelcast.map.impl.operation.LoadAllOperation;
import com.hazelcast.map.impl.operation.LoadMapOperation;
import com.hazelcast.map.impl.operation.MapFetchEntriesOperation;
import com.hazelcast.map.impl.operation.MapFetchKeysOperation;
import com.hazelcast.map.impl.operation.MapFetchWithQueryOperation;
import com.hazelcast.map.impl.operation.MapFlushOperation;
import com.hazelcast.map.impl.operation.MapFlushOperationFactory;
import com.hazelcast.map.impl.operation.MapGetAllOperationFactory;
import com.hazelcast.map.impl.operation.MapLoadAllOperationFactory;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.operation.MapOperationProvider;
import com.hazelcast.map.impl.operation.MergeOperation;
import com.hazelcast.map.impl.operation.MergeOperationFactory;
import com.hazelcast.map.impl.operation.MultipleEntryOperationFactory;
import com.hazelcast.map.impl.operation.PartitionWideEntryOperationFactory;
import com.hazelcast.map.impl.operation.PartitionWideEntryWithPredicateOperationFactory;
import com.hazelcast.map.impl.operation.PutAllOperation;
import com.hazelcast.map.impl.operation.PutAllPartitionAwareOperationFactory;
import com.hazelcast.map.impl.operation.PutFromLoadAllOperation;
import com.hazelcast.map.impl.operation.PutIfAbsentOperation;
import com.hazelcast.map.impl.operation.PutOperation;
import com.hazelcast.map.impl.operation.PutTransientOperation;
import com.hazelcast.map.impl.operation.RemoveIfSameOperation;
import com.hazelcast.map.impl.operation.RemoveOperation;
import com.hazelcast.map.impl.operation.ReplaceIfSameOperation;
import com.hazelcast.map.impl.operation.ReplaceOperation;
import com.hazelcast.map.impl.operation.SetOperation;
import com.hazelcast.map.impl.operation.SetTtlOperation;
import com.hazelcast.map.impl.operation.SizeOperationFactory;
import com.hazelcast.map.impl.operation.TryPutOperation;
import com.hazelcast.map.impl.operation.TryRemoveOperation;
import com.hazelcast.map.impl.query.Query;
import com.hazelcast.map.impl.query.QueryOperation;
import com.hazelcast.map.impl.query.QueryPartitionOperation;
import com.hazelcast.map.impl.tx.TxnDeleteOperation;
import com.hazelcast.map.impl.tx.TxnLockAndGetOperation;
import com.hazelcast.map.impl.tx.TxnSetOperation;
import com.hazelcast.map.merge.MapMergePolicy;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;
import com.hazelcast.spi.OperationFactory;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class DefaultMapOperationProvider
implements MapOperationProvider {
    @Override
    public OperationFactory createMapSizeOperationFactory(String name) {
        return new SizeOperationFactory(name);
    }

    @Override
    public MapOperation createPutOperation(String name, Data key, Data value, long ttl, long maxIdle) {
        return new PutOperation(name, key, value, ttl, maxIdle);
    }

    @Override
    public MapOperation createTryPutOperation(String name, Data dataKey, Data value, long timeout) {
        return new TryPutOperation(name, dataKey, value, timeout);
    }

    @Override
    public MapOperation createSetOperation(String name, Data dataKey, Data value, long ttl, long maxIdle) {
        return new SetOperation(name, dataKey, value, ttl, maxIdle);
    }

    @Override
    public MapOperation createPutIfAbsentOperation(String name, Data key, Data value, long ttl, long maxIdle) {
        return new PutIfAbsentOperation(name, key, value, ttl, maxIdle);
    }

    @Override
    public MapOperation createPutTransientOperation(String name, Data key, Data value, long ttl, long maxIdle) {
        return new PutTransientOperation(name, key, value, ttl, maxIdle);
    }

    @Override
    public MapOperation createRemoveOperation(String name, Data key, boolean disableWanReplicationEvent) {
        return new RemoveOperation(name, key, disableWanReplicationEvent);
    }

    @Override
    public MapOperation createSetTtlOperation(String name, Data key, long ttl) {
        return new SetTtlOperation(name, key, ttl);
    }

    @Override
    public MapOperation createTryRemoveOperation(String name, Data dataKey, long timeout) {
        return new TryRemoveOperation(name, dataKey, timeout);
    }

    @Override
    public MapOperation createReplaceOperation(String name, Data dataKey, Data value) {
        return new ReplaceOperation(name, dataKey, value);
    }

    @Override
    public MapOperation createRemoveIfSameOperation(String name, Data dataKey, Data value) {
        return new RemoveIfSameOperation(name, dataKey, value);
    }

    @Override
    public MapOperation createReplaceIfSameOperation(String name, Data dataKey, Data expect, Data update) {
        return new ReplaceIfSameOperation(name, dataKey, expect, update);
    }

    @Override
    public MapOperation createDeleteOperation(String name, Data key, boolean disableWanReplicationEvent) {
        return new DeleteOperation(name, key, disableWanReplicationEvent);
    }

    @Override
    public MapOperation createClearOperation(String name) {
        return new ClearOperation(name);
    }

    @Override
    public MapOperation createEntryOperation(String name, Data dataKey, EntryProcessor entryProcessor) {
        return new EntryOperation(name, dataKey, entryProcessor);
    }

    @Override
    public MapOperation createEvictOperation(String name, Data dataKey, boolean asyncBackup) {
        return new EvictOperation(name, dataKey, asyncBackup);
    }

    @Override
    public MapOperation createEvictAllOperation(String name) {
        return new EvictAllOperation(name);
    }

    @Override
    public MapOperation createContainsKeyOperation(String name, Data dataKey) {
        return new ContainsKeyOperation(name, dataKey);
    }

    @Override
    public OperationFactory createContainsValueOperationFactory(String name, Data testValue) {
        return new ContainsValueOperationFactory(name, testValue);
    }

    @Override
    public OperationFactory createGetAllOperationFactory(String name, List<Data> keys) {
        return new MapGetAllOperationFactory(name, keys);
    }

    @Override
    public OperationFactory createEvictAllOperationFactory(String name) {
        return new EvictAllOperationFactory(name);
    }

    @Override
    public OperationFactory createClearOperationFactory(String name) {
        return new ClearOperationFactory(name);
    }

    @Override
    public OperationFactory createMapFlushOperationFactory(String name) {
        return new MapFlushOperationFactory(name);
    }

    @Override
    public OperationFactory createLoadAllOperationFactory(String name, List<Data> keys, boolean replaceExistingValues) {
        return new MapLoadAllOperationFactory(name, keys, replaceExistingValues);
    }

    @Override
    public MapOperation createGetEntryViewOperation(String name, Data dataKey) {
        return new GetEntryViewOperation(name, dataKey);
    }

    @Override
    public OperationFactory createPartitionWideEntryOperationFactory(String name, EntryProcessor entryProcessor) {
        return new PartitionWideEntryOperationFactory(name, entryProcessor);
    }

    @Override
    public MapOperation createTxnDeleteOperation(String name, Data dataKey, long version) {
        return new TxnDeleteOperation(name, dataKey, version);
    }

    @Override
    public MapOperation createTxnLockAndGetOperation(String name, Data dataKey, long timeout, long ttl, String ownerUuid, boolean shouldLoad, boolean blockReads) {
        return new TxnLockAndGetOperation(name, dataKey, timeout, ttl, ownerUuid, shouldLoad, blockReads);
    }

    @Override
    public MapOperation createTxnSetOperation(String name, Data dataKey, Data value, long version, long ttl) {
        return new TxnSetOperation(name, dataKey, value, version, ttl);
    }

    @Override
    public MapOperation createLegacyMergeOperation(String name, EntryView<Data, Data> mergingEntry, MapMergePolicy policy, boolean disableWanReplicationEvent) {
        return new LegacyMergeOperation(name, mergingEntry, policy, disableWanReplicationEvent);
    }

    @Override
    public MapOperation createMergeOperation(String name, SplitBrainMergeTypes.MapMergeTypes mergingValue, SplitBrainMergePolicy<Data, SplitBrainMergeTypes.MapMergeTypes> mergePolicy, boolean disableWanReplicationEvent) {
        return new MergeOperation(name, Collections.singletonList(mergingValue), mergePolicy, disableWanReplicationEvent);
    }

    @Override
    public MapOperation createMapFlushOperation(String name) {
        return new MapFlushOperation(name);
    }

    @Override
    public MapOperation createLoadMapOperation(String name, boolean replaceExistingValues) {
        return new LoadMapOperation(name, replaceExistingValues);
    }

    @Override
    public OperationFactory createPartitionWideEntryWithPredicateOperationFactory(String name, EntryProcessor entryProcessor, Predicate predicate) {
        return new PartitionWideEntryWithPredicateOperationFactory(name, entryProcessor, predicate);
    }

    @Override
    public OperationFactory createMultipleEntryOperationFactory(String name, Set<Data> keys, EntryProcessor entryProcessor) {
        return new MultipleEntryOperationFactory(name, keys, entryProcessor);
    }

    @Override
    public MapOperation createGetOperation(String name, Data dataKey) {
        return new GetOperation(name, dataKey);
    }

    @Override
    public MapOperation createQueryOperation(Query query) {
        return new QueryOperation(query);
    }

    @Override
    public MapOperation createQueryPartitionOperation(Query query) {
        return new QueryPartitionOperation(query);
    }

    @Override
    public MapOperation createLoadAllOperation(String name, List<Data> keys, boolean replaceExistingValues) {
        return new LoadAllOperation(name, keys, replaceExistingValues);
    }

    @Override
    public MapOperation createPutAllOperation(String name, MapEntries mapEntries) {
        return new PutAllOperation(name, mapEntries);
    }

    @Override
    public OperationFactory createPutAllOperationFactory(String name, int[] partitions, MapEntries[] mapEntries) {
        return new PutAllPartitionAwareOperationFactory(name, partitions, mapEntries);
    }

    @Override
    public OperationFactory createMergeOperationFactory(String name, int[] partitions, List<SplitBrainMergeTypes.MapMergeTypes>[] mergingEntries, SplitBrainMergePolicy<Data, SplitBrainMergeTypes.MapMergeTypes> mergePolicy) {
        return new MergeOperationFactory(name, partitions, mergingEntries, mergePolicy);
    }

    @Override
    public MapOperation createPutFromLoadAllOperation(String name, List<Data> keyValueSequence) {
        return new PutFromLoadAllOperation(name, keyValueSequence);
    }

    @Override
    public MapOperation createFetchKeysOperation(String name, int lastTableIndex, int fetchSize) {
        return new MapFetchKeysOperation(name, lastTableIndex, fetchSize);
    }

    @Override
    public MapOperation createFetchEntriesOperation(String name, int lastTableIndex, int fetchSize) {
        return new MapFetchEntriesOperation(name, lastTableIndex, fetchSize);
    }

    @Override
    public MapOperation createFetchWithQueryOperation(String name, int lastTableIndex, int fetchSize, Query query) {
        return new MapFetchWithQueryOperation(name, lastTableIndex, fetchSize, query);
    }
}

