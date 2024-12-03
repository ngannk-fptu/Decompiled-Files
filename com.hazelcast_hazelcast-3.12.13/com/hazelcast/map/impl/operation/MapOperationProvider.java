/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.operation;

import com.hazelcast.core.EntryView;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.impl.MapEntries;
import com.hazelcast.map.impl.operation.MapOperation;
import com.hazelcast.map.impl.query.Query;
import com.hazelcast.map.merge.MapMergePolicy;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;
import com.hazelcast.spi.OperationFactory;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import java.util.List;
import java.util.Set;

public interface MapOperationProvider {
    public MapOperation createPutOperation(String var1, Data var2, Data var3, long var4, long var6);

    public MapOperation createTryPutOperation(String var1, Data var2, Data var3, long var4);

    public MapOperation createSetOperation(String var1, Data var2, Data var3, long var4, long var6);

    public MapOperation createPutIfAbsentOperation(String var1, Data var2, Data var3, long var4, long var6);

    public MapOperation createPutTransientOperation(String var1, Data var2, Data var3, long var4, long var6);

    public MapOperation createSetTtlOperation(String var1, Data var2, long var3);

    public MapOperation createTryRemoveOperation(String var1, Data var2, long var3);

    public MapOperation createReplaceOperation(String var1, Data var2, Data var3);

    public MapOperation createRemoveIfSameOperation(String var1, Data var2, Data var3);

    public MapOperation createReplaceIfSameOperation(String var1, Data var2, Data var3, Data var4);

    public MapOperation createRemoveOperation(String var1, Data var2, boolean var3);

    public MapOperation createDeleteOperation(String var1, Data var2, boolean var3);

    public MapOperation createClearOperation(String var1);

    public MapOperation createEntryOperation(String var1, Data var2, EntryProcessor var3);

    public MapOperation createEvictOperation(String var1, Data var2, boolean var3);

    public MapOperation createEvictAllOperation(String var1);

    public MapOperation createContainsKeyOperation(String var1, Data var2);

    public MapOperation createGetEntryViewOperation(String var1, Data var2);

    public MapOperation createGetOperation(String var1, Data var2);

    public MapOperation createQueryOperation(Query var1);

    public MapOperation createQueryPartitionOperation(Query var1);

    public MapOperation createLoadAllOperation(String var1, List<Data> var2, boolean var3);

    public MapOperation createPutAllOperation(String var1, MapEntries var2);

    public MapOperation createPutFromLoadAllOperation(String var1, List<Data> var2);

    public MapOperation createTxnDeleteOperation(String var1, Data var2, long var3);

    public MapOperation createTxnLockAndGetOperation(String var1, Data var2, long var3, long var5, String var7, boolean var8, boolean var9);

    public MapOperation createTxnSetOperation(String var1, Data var2, Data var3, long var4, long var6);

    public MapOperation createLegacyMergeOperation(String var1, EntryView<Data, Data> var2, MapMergePolicy var3, boolean var4);

    public MapOperation createMergeOperation(String var1, SplitBrainMergeTypes.MapMergeTypes var2, SplitBrainMergePolicy<Data, SplitBrainMergeTypes.MapMergeTypes> var3, boolean var4);

    public MapOperation createMapFlushOperation(String var1);

    public MapOperation createLoadMapOperation(String var1, boolean var2);

    public MapOperation createFetchKeysOperation(String var1, int var2, int var3);

    public MapOperation createFetchEntriesOperation(String var1, int var2, int var3);

    public MapOperation createFetchWithQueryOperation(String var1, int var2, int var3, Query var4);

    public OperationFactory createPartitionWideEntryOperationFactory(String var1, EntryProcessor var2);

    public OperationFactory createPartitionWideEntryWithPredicateOperationFactory(String var1, EntryProcessor var2, Predicate var3);

    public OperationFactory createMultipleEntryOperationFactory(String var1, Set<Data> var2, EntryProcessor var3);

    public OperationFactory createContainsValueOperationFactory(String var1, Data var2);

    public OperationFactory createEvictAllOperationFactory(String var1);

    public OperationFactory createClearOperationFactory(String var1);

    public OperationFactory createMapFlushOperationFactory(String var1);

    public OperationFactory createLoadAllOperationFactory(String var1, List<Data> var2, boolean var3);

    public OperationFactory createGetAllOperationFactory(String var1, List<Data> var2);

    public OperationFactory createMapSizeOperationFactory(String var1);

    public OperationFactory createPutAllOperationFactory(String var1, int[] var2, MapEntries[] var3);

    public OperationFactory createMergeOperationFactory(String var1, int[] var2, List<SplitBrainMergeTypes.MapMergeTypes>[] var3, SplitBrainMergePolicy<Data, SplitBrainMergeTypes.MapMergeTypes> var4);
}

