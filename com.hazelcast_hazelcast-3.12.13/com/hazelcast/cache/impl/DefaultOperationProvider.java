/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.expiry.ExpiryPolicy
 *  javax.cache.processor.EntryProcessor
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.impl.CacheOperationProvider;
import com.hazelcast.cache.impl.operation.CacheClearOperationFactory;
import com.hazelcast.cache.impl.operation.CacheContainsKeyOperation;
import com.hazelcast.cache.impl.operation.CacheEntryIteratorOperation;
import com.hazelcast.cache.impl.operation.CacheEntryProcessorOperation;
import com.hazelcast.cache.impl.operation.CacheGetAllOperationFactory;
import com.hazelcast.cache.impl.operation.CacheGetAndRemoveOperation;
import com.hazelcast.cache.impl.operation.CacheGetAndReplaceOperation;
import com.hazelcast.cache.impl.operation.CacheGetOperation;
import com.hazelcast.cache.impl.operation.CacheKeyIteratorOperation;
import com.hazelcast.cache.impl.operation.CacheLoadAllOperationFactory;
import com.hazelcast.cache.impl.operation.CacheMergeOperation;
import com.hazelcast.cache.impl.operation.CacheMergeOperationFactory;
import com.hazelcast.cache.impl.operation.CachePutAllOperation;
import com.hazelcast.cache.impl.operation.CachePutIfAbsentOperation;
import com.hazelcast.cache.impl.operation.CachePutOperation;
import com.hazelcast.cache.impl.operation.CacheRemoveAllOperationFactory;
import com.hazelcast.cache.impl.operation.CacheRemoveOperation;
import com.hazelcast.cache.impl.operation.CacheReplaceOperation;
import com.hazelcast.cache.impl.operation.CacheSetExpiryPolicyOperation;
import com.hazelcast.cache.impl.operation.CacheSizeOperationFactory;
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

public class DefaultOperationProvider
implements CacheOperationProvider {
    protected final String nameWithPrefix;

    public DefaultOperationProvider(String nameWithPrefix) {
        this.nameWithPrefix = nameWithPrefix;
    }

    @Override
    public Operation createPutOperation(Data key, Data value, ExpiryPolicy policy, boolean get, int completionId) {
        return new CachePutOperation(this.nameWithPrefix, key, value, policy, get, completionId);
    }

    @Override
    public Operation createPutAllOperation(List<Map.Entry<Data, Data>> entries, ExpiryPolicy policy, int completionId) {
        return new CachePutAllOperation(this.nameWithPrefix, entries, policy, completionId);
    }

    @Override
    public Operation createGetOperation(Data key, ExpiryPolicy policy) {
        return new CacheGetOperation(this.nameWithPrefix, key, policy);
    }

    @Override
    public Operation createContainsKeyOperation(Data key) {
        return new CacheContainsKeyOperation(this.nameWithPrefix, key);
    }

    @Override
    public Operation createPutIfAbsentOperation(Data key, Data value, ExpiryPolicy policy, int completionId) {
        return new CachePutIfAbsentOperation(this.nameWithPrefix, key, value, policy, completionId);
    }

    @Override
    public Operation createRemoveOperation(Data key, Data oldValue, int completionId) {
        return new CacheRemoveOperation(this.nameWithPrefix, key, oldValue, completionId);
    }

    @Override
    public Operation createGetAndRemoveOperation(Data key, int completionId) {
        return new CacheGetAndRemoveOperation(this.nameWithPrefix, key, completionId);
    }

    @Override
    public Operation createReplaceOperation(Data key, Data oldValue, Data newValue, ExpiryPolicy policy, int completionId) {
        return new CacheReplaceOperation(this.nameWithPrefix, key, oldValue, newValue, policy, completionId);
    }

    @Override
    public Operation createGetAndReplaceOperation(Data key, Data value, ExpiryPolicy policy, int completionId) {
        return new CacheGetAndReplaceOperation(this.nameWithPrefix, key, value, policy, completionId);
    }

    @Override
    public Operation createEntryProcessorOperation(Data key, Integer completionId, EntryProcessor entryProcessor, Object ... arguments) {
        return new CacheEntryProcessorOperation(this.nameWithPrefix, key, completionId, entryProcessor, arguments);
    }

    @Override
    public Operation createKeyIteratorOperation(int lastTableIndex, int fetchSize) {
        return new CacheKeyIteratorOperation(this.nameWithPrefix, lastTableIndex, fetchSize);
    }

    @Override
    public Operation createEntryIteratorOperation(int lastTableIndex, int fetchSize) {
        return new CacheEntryIteratorOperation(this.nameWithPrefix, lastTableIndex, fetchSize);
    }

    @Override
    public Operation createMergeOperation(String name, List<SplitBrainMergeTypes.CacheMergeTypes> mergingEntries, SplitBrainMergePolicy<Data, SplitBrainMergeTypes.CacheMergeTypes> policy) {
        return new CacheMergeOperation(name, mergingEntries, policy);
    }

    @Override
    public OperationFactory createMergeOperationFactory(String name, int[] partitions, List<SplitBrainMergeTypes.CacheMergeTypes>[] mergingEntries, SplitBrainMergePolicy<Data, SplitBrainMergeTypes.CacheMergeTypes> policy) {
        return new CacheMergeOperationFactory(name, partitions, mergingEntries, policy);
    }

    @Override
    public Operation createSetExpiryPolicyOperation(List<Data> keys, Data expiryPolicy) {
        return new CacheSetExpiryPolicyOperation(this.nameWithPrefix, keys, expiryPolicy);
    }

    @Override
    public OperationFactory createGetAllOperationFactory(Set<Data> keySet, ExpiryPolicy policy) {
        return new CacheGetAllOperationFactory(this.nameWithPrefix, keySet, policy);
    }

    @Override
    public OperationFactory createLoadAllOperationFactory(Set<Data> keySet, boolean replaceExistingValues) {
        return new CacheLoadAllOperationFactory(this.nameWithPrefix, keySet, replaceExistingValues);
    }

    @Override
    public OperationFactory createClearOperationFactory() {
        return new CacheClearOperationFactory(this.nameWithPrefix);
    }

    @Override
    public OperationFactory createRemoveAllOperationFactory(Set<Data> keySet, Integer completionId) {
        return new CacheRemoveAllOperationFactory(this.nameWithPrefix, keySet, completionId);
    }

    @Override
    public OperationFactory createSizeOperationFactory() {
        return new CacheSizeOperationFactory(this.nameWithPrefix);
    }
}

