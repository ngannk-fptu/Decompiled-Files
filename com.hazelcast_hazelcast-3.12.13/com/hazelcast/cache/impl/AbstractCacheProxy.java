/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.CacheException
 *  javax.cache.expiry.ExpiryPolicy
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.impl.AbstractInternalCacheProxy;
import com.hazelcast.cache.impl.CacheProxyUtil;
import com.hazelcast.cache.impl.ICacheService;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.internal.cluster.Versions;
import com.hazelcast.map.impl.MapEntries;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationFactory;
import com.hazelcast.spi.OperationService;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.FutureUtil;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.SetUtil;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import javax.cache.CacheException;
import javax.cache.expiry.ExpiryPolicy;

abstract class AbstractCacheProxy<K, V>
extends AbstractInternalCacheProxy<K, V> {
    AbstractCacheProxy(CacheConfig<K, V> cacheConfig, NodeEngine nodeEngine, ICacheService cacheService) {
        super(cacheConfig, nodeEngine, cacheService);
    }

    @Override
    public InternalCompletableFuture<V> getAsync(K key) {
        return this.getAsync((Object)key, (ExpiryPolicy)null);
    }

    @Override
    public InternalCompletableFuture<V> getAsync(K key, ExpiryPolicy expiryPolicy) {
        this.ensureOpen();
        CacheProxyUtil.validateNotNull(key);
        Object keyData = this.serializationService.toData(key);
        Operation op = this.operationProvider.createGetOperation((Data)keyData, expiryPolicy);
        return this.invoke(op, (Data)keyData, false);
    }

    public InternalCompletableFuture<Void> putAsync(K key, V value) {
        return this.putAsync((Object)key, (Object)value, (ExpiryPolicy)null);
    }

    public InternalCompletableFuture<Void> putAsync(K key, V value, ExpiryPolicy expiryPolicy) {
        return this.putAsyncInternal(key, value, expiryPolicy, false, false);
    }

    public InternalCompletableFuture<Boolean> putIfAbsentAsync(K key, V value) {
        return this.putIfAbsentAsyncInternal(key, value, null, false);
    }

    public InternalCompletableFuture<Boolean> putIfAbsentAsync(K key, V value, ExpiryPolicy expiryPolicy) {
        return this.putIfAbsentAsyncInternal(key, value, expiryPolicy, false);
    }

    @Override
    public ICompletableFuture<V> getAndPutAsync(K key, V value) {
        return this.getAndPutAsync(key, value, null);
    }

    @Override
    public ICompletableFuture<V> getAndPutAsync(K key, V value, ExpiryPolicy expiryPolicy) {
        return this.putAsyncInternal(key, value, expiryPolicy, true, false);
    }

    public InternalCompletableFuture<Boolean> removeAsync(K key) {
        return this.removeAsyncInternal(key, null, false, false, false);
    }

    public InternalCompletableFuture<Boolean> removeAsync(K key, V oldValue) {
        return this.removeAsyncInternal(key, oldValue, true, false, false);
    }

    @Override
    public ICompletableFuture<V> getAndRemoveAsync(K key) {
        return this.removeAsyncInternal(key, null, false, true, false);
    }

    @Override
    public ICompletableFuture<Boolean> replaceAsync(K key, V value) {
        return this.replaceAsyncInternal(key, null, value, null, false, false, false);
    }

    @Override
    public ICompletableFuture<Boolean> replaceAsync(K key, V value, ExpiryPolicy expiryPolicy) {
        return this.replaceAsyncInternal(key, null, value, expiryPolicy, false, false, false);
    }

    @Override
    public ICompletableFuture<Boolean> replaceAsync(K key, V oldValue, V newValue) {
        return this.replaceAsyncInternal(key, oldValue, newValue, null, true, false, false);
    }

    @Override
    public ICompletableFuture<Boolean> replaceAsync(K key, V oldValue, V newValue, ExpiryPolicy expiryPolicy) {
        return this.replaceAsyncInternal(key, oldValue, newValue, expiryPolicy, true, false, false);
    }

    @Override
    public ICompletableFuture<V> getAndReplaceAsync(K key, V value) {
        return this.replaceAsyncInternal(key, null, value, null, false, true, false);
    }

    @Override
    public ICompletableFuture<V> getAndReplaceAsync(K key, V value, ExpiryPolicy expiryPolicy) {
        return this.replaceAsyncInternal(key, null, value, expiryPolicy, false, true, false);
    }

    @Override
    public V get(K key, ExpiryPolicy expiryPolicy) {
        try {
            ICompletableFuture future = this.getAsync((Object)key, expiryPolicy);
            return future.get();
        }
        catch (Throwable e) {
            throw ExceptionUtil.rethrowAllowedTypeFirst(e, CacheException.class);
        }
    }

    @Override
    public Map<K, V> getAll(Set<? extends K> keys, ExpiryPolicy expiryPolicy) {
        this.ensureOpen();
        CacheProxyUtil.validateNotNull(keys);
        if (keys.isEmpty()) {
            return Collections.emptyMap();
        }
        int keyCount = keys.size();
        Set<Data> ks = SetUtil.createHashSet(keyCount);
        for (K key : keys) {
            CacheProxyUtil.validateNotNull(key);
            Object dataKey = this.serializationService.toData(key);
            ks.add((Data)dataKey);
        }
        Map result = MapUtil.createHashMap(keyCount);
        Set<Integer> partitions = this.getPartitionsForKeys(ks);
        try {
            OperationFactory factory = this.operationProvider.createGetAllOperationFactory(ks, expiryPolicy);
            OperationService operationService = this.getNodeEngine().getOperationService();
            Map responses = operationService.invokeOnPartitions(this.getServiceName(), factory, partitions);
            for (Object response : responses.values()) {
                MapEntries mapEntries = (MapEntries)this.serializationService.toObject(response);
                mapEntries.putAllToMap(this.serializationService, result);
            }
        }
        catch (Throwable e) {
            throw ExceptionUtil.rethrowAllowedTypeFirst(e, CacheException.class);
        }
        return result;
    }

    @Override
    public void put(K key, V value, ExpiryPolicy expiryPolicy) {
        try {
            InternalCompletableFuture future = this.putAsyncInternal(key, value, expiryPolicy, false, true);
            future.get();
        }
        catch (Throwable e) {
            throw ExceptionUtil.rethrowAllowedTypeFirst(e, CacheException.class);
        }
    }

    @Override
    public V getAndPut(K key, V value, ExpiryPolicy expiryPolicy) {
        try {
            InternalCompletableFuture future = this.putAsyncInternal(key, value, expiryPolicy, true, true);
            return future.get();
        }
        catch (Throwable e) {
            throw ExceptionUtil.rethrowAllowedTypeFirst(e, CacheException.class);
        }
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map, ExpiryPolicy expiryPolicy) {
        this.ensureOpen();
        CacheProxyUtil.validateNotNull(map);
        try {
            int partitionCount = this.partitionService.getPartitionCount();
            List<Map.Entry<Data, Data>>[] entriesPerPartition = this.groupDataToPartitions(map, partitionCount);
            this.putToAllPartitionsAndWaitForCompletion(entriesPerPartition, expiryPolicy);
        }
        catch (Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
    }

    @Override
    public boolean setExpiryPolicy(K key, ExpiryPolicy expiryPolicy) {
        if (this.isClusterVersionLessThan(Versions.V3_11)) {
            throw new UnsupportedOperationException("setExpiryPolicy operation is availablewhen cluster version is 3.11 or higher");
        }
        try {
            this.ensureOpen();
            CacheProxyUtil.validateNotNull(key);
            CacheProxyUtil.validateNotNull(expiryPolicy);
            Object keyData = this.serializationService.toData(key);
            Object expiryPolicyData = this.serializationService.toData(expiryPolicy);
            List<Data> list = Collections.singletonList(keyData);
            Operation operation = this.operationProvider.createSetExpiryPolicyOperation(list, (Data)expiryPolicyData);
            InternalCompletableFuture future = this.invoke(operation, (Data)keyData, true);
            return (Boolean)future.get();
        }
        catch (Throwable e) {
            throw ExceptionUtil.rethrowAllowedTypeFirst(e, CacheException.class);
        }
    }

    @Override
    public void setExpiryPolicy(Set<? extends K> keys, ExpiryPolicy expiryPolicy) {
        if (this.isClusterVersionLessThan(Versions.V3_11)) {
            throw new UnsupportedOperationException("setExpiryPolicy operation is availablewhen cluster version is 3.11 or higher");
        }
        this.ensureOpen();
        CacheProxyUtil.validateNotNull(keys);
        CacheProxyUtil.validateNotNull(expiryPolicy);
        try {
            int partitionCount = this.partitionService.getPartitionCount();
            List<Data>[] keysPerPartition = this.groupDataToPartitions(keys, partitionCount);
            this.setTTLAllPartitionsAndWaitForCompletion(keysPerPartition, (Data)this.serializationService.toData(expiryPolicy));
        }
        catch (Exception e) {
            ExceptionUtil.rethrow(e);
        }
    }

    private List<Data>[] groupDataToPartitions(Collection<? extends K> keys, int partitionCount) {
        ArrayList[] keysPerPartition = new ArrayList[partitionCount];
        for (K key : keys) {
            CacheProxyUtil.validateNotNull(key);
            Object dataKey = this.serializationService.toData(key);
            int partitionId = this.partitionService.getPartitionId((Data)dataKey);
            ArrayList partition = keysPerPartition[partitionId];
            if (partition == null) {
                keysPerPartition[partitionId] = partition = new ArrayList();
            }
            partition.add(dataKey);
        }
        return keysPerPartition;
    }

    private List<Map.Entry<Data, Data>>[] groupDataToPartitions(Map<? extends K, ? extends V> map, int partitionCount) {
        List[] entriesPerPartition = new List[partitionCount];
        for (Map.Entry<K, V> entry : map.entrySet()) {
            K key = entry.getKey();
            V value = entry.getValue();
            CacheProxyUtil.validateNotNull(key, value);
            Object keyData = this.serializationService.toData(key);
            Object valueData = this.serializationService.toData(value);
            int partitionId = this.partitionService.getPartitionId((Data)keyData);
            ArrayList entries = entriesPerPartition[partitionId];
            if (entries == null) {
                entriesPerPartition[partitionId] = entries = new ArrayList();
            }
            entries.add(new AbstractMap.SimpleImmutableEntry(keyData, valueData));
        }
        return entriesPerPartition;
    }

    private void putToAllPartitionsAndWaitForCompletion(List<Map.Entry<Data, Data>>[] entriesPerPartition, ExpiryPolicy expiryPolicy) throws Exception {
        ArrayList futures = new ArrayList(entriesPerPartition.length);
        for (int partitionId = 0; partitionId < entriesPerPartition.length; ++partitionId) {
            List<Map.Entry<Data, Data>> entries = entriesPerPartition[partitionId];
            if (entries == null) continue;
            Operation operation = this.operationProvider.createPutAllOperation(entries, expiryPolicy, partitionId);
            InternalCompletableFuture future = this.invoke(operation, partitionId, true);
            futures.add(future);
        }
        Throwable error = null;
        for (Future future : futures) {
            try {
                future.get();
            }
            catch (Throwable t) {
                this.logger.finest("Error occurred while putting entries as batch!", t);
                if (error != null) continue;
                error = t;
            }
        }
        if (error != null) {
            throw ExceptionUtil.rethrow(error);
        }
    }

    private void setTTLAllPartitionsAndWaitForCompletion(List<Data>[] keysPerPartition, Data expiryPolicy) {
        ArrayList futures = new ArrayList(keysPerPartition.length);
        for (int partitionId = 0; partitionId < keysPerPartition.length; ++partitionId) {
            List<Data> keys = keysPerPartition[partitionId];
            if (keys == null) continue;
            Operation operation = this.operationProvider.createSetExpiryPolicyOperation(keys, expiryPolicy);
            futures.add(this.invoke(operation, partitionId, true));
        }
        List<Throwable> throwables = FutureUtil.waitUntilAllResponded(futures);
        if (throwables.size() > 0) {
            throw ExceptionUtil.rethrow(throwables.get(0));
        }
    }

    @Override
    public boolean putIfAbsent(K key, V value, ExpiryPolicy expiryPolicy) {
        try {
            InternalCompletableFuture<Boolean> future = this.putIfAbsentAsyncInternal(key, value, expiryPolicy, true);
            return (Boolean)future.get();
        }
        catch (Throwable e) {
            throw ExceptionUtil.rethrowAllowedTypeFirst(e, CacheException.class);
        }
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue, ExpiryPolicy expiryPolicy) {
        try {
            InternalCompletableFuture future = this.replaceAsyncInternal(key, oldValue, newValue, expiryPolicy, true, false, true);
            return (Boolean)future.get();
        }
        catch (Throwable e) {
            throw ExceptionUtil.rethrowAllowedTypeFirst(e, CacheException.class);
        }
    }

    @Override
    public boolean replace(K key, V value, ExpiryPolicy expiryPolicy) {
        try {
            InternalCompletableFuture future = this.replaceAsyncInternal(key, null, value, expiryPolicy, false, false, true);
            return (Boolean)future.get();
        }
        catch (Throwable e) {
            throw ExceptionUtil.rethrowAllowedTypeFirst(e, CacheException.class);
        }
    }

    @Override
    public V getAndReplace(K key, V value, ExpiryPolicy expiryPolicy) {
        try {
            InternalCompletableFuture future = this.replaceAsyncInternal(key, null, value, expiryPolicy, false, true, true);
            return future.get();
        }
        catch (Throwable e) {
            throw ExceptionUtil.rethrowAllowedTypeFirst(e, CacheException.class);
        }
    }

    @Override
    public int size() {
        this.ensureOpen();
        try {
            OperationFactory operationFactory = this.operationProvider.createSizeOperationFactory();
            Map<Integer, Object> results = this.getNodeEngine().getOperationService().invokeOnAllPartitions(this.getServiceName(), operationFactory);
            long total = 0L;
            for (Object result : results.values()) {
                total += (long)((Integer)this.getNodeEngine().toObject(result)).intValue();
            }
            return MapUtil.toIntSize(total);
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrowAllowedTypeFirst(t, CacheException.class);
        }
    }

    private Set<Integer> getPartitionsForKeys(Set<Data> keys) {
        IPartitionService partitionService = this.getNodeEngine().getPartitionService();
        int partitions = partitionService.getPartitionCount();
        int capacity = Math.min(partitions, keys.size());
        Set<Integer> partitionIds = SetUtil.createHashSet(capacity);
        Iterator<Data> iterator = keys.iterator();
        while (iterator.hasNext() && partitionIds.size() < partitions) {
            Data key = iterator.next();
            partitionIds.add(partitionService.getPartitionId(key));
        }
        return partitionIds;
    }
}

