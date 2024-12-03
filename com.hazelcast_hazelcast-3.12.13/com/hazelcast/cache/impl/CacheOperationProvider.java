/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.expiry.ExpiryPolicy
 *  javax.cache.processor.EntryProcessor
 */
package com.hazelcast.cache.impl;

import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationFactory;
import com.hazelcast.spi.merge.SplitBrainMergePolicy;
import com.hazelcast.spi.merge.SplitBrainMergeTypes;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.processor.EntryProcessor;

public interface CacheOperationProvider {
    public Operation createPutOperation(Data var1, Data var2, ExpiryPolicy var3, boolean var4, int var5);

    public Operation createPutAllOperation(List<Map.Entry<Data, Data>> var1, ExpiryPolicy var2, int var3);

    public Operation createGetOperation(Data var1, ExpiryPolicy var2);

    public Operation createContainsKeyOperation(Data var1);

    public Operation createPutIfAbsentOperation(Data var1, Data var2, ExpiryPolicy var3, int var4);

    public Operation createRemoveOperation(Data var1, Data var2, int var3);

    public Operation createGetAndRemoveOperation(Data var1, int var2);

    public Operation createReplaceOperation(Data var1, Data var2, Data var3, ExpiryPolicy var4, int var5);

    public Operation createGetAndReplaceOperation(Data var1, Data var2, ExpiryPolicy var3, int var4);

    public Operation createEntryProcessorOperation(Data var1, Integer var2, EntryProcessor var3, Object ... var4);

    public Operation createKeyIteratorOperation(int var1, int var2);

    public Operation createEntryIteratorOperation(int var1, int var2);

    public Operation createMergeOperation(String var1, List<SplitBrainMergeTypes.CacheMergeTypes> var2, SplitBrainMergePolicy<Data, SplitBrainMergeTypes.CacheMergeTypes> var3);

    public OperationFactory createMergeOperationFactory(String var1, int[] var2, List<SplitBrainMergeTypes.CacheMergeTypes>[] var3, SplitBrainMergePolicy<Data, SplitBrainMergeTypes.CacheMergeTypes> var4);

    public Operation createSetExpiryPolicyOperation(List<Data> var1, Data var2);

    public OperationFactory createGetAllOperationFactory(Set<Data> var1, ExpiryPolicy var2);

    public OperationFactory createLoadAllOperationFactory(Set<Data> var1, boolean var2);

    public OperationFactory createClearOperationFactory();

    public OperationFactory createRemoveAllOperationFactory(Set<Data> var1, Integer var2);

    public OperationFactory createSizeOperationFactory();
}

