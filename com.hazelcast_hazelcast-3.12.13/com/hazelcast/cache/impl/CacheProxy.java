/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.Cache$Entry
 *  javax.cache.CacheException
 *  javax.cache.configuration.CacheEntryListenerConfiguration
 *  javax.cache.configuration.Configuration
 *  javax.cache.expiry.ExpiryPolicy
 *  javax.cache.integration.CompletionListener
 *  javax.cache.processor.EntryProcessor
 *  javax.cache.processor.EntryProcessorException
 *  javax.cache.processor.EntryProcessorResult
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.impl.AbstractCacheProxy;
import com.hazelcast.cache.impl.AbstractCacheProxyBase;
import com.hazelcast.cache.impl.CacheEntryProcessorResult;
import com.hazelcast.cache.impl.CacheEventListenerAdaptor;
import com.hazelcast.cache.impl.CachePartitionIterator;
import com.hazelcast.cache.impl.CacheProxyUtil;
import com.hazelcast.cache.impl.ClusterWideIterator;
import com.hazelcast.cache.impl.ICacheService;
import com.hazelcast.cache.impl.event.CachePartitionLostEventFilter;
import com.hazelcast.cache.impl.event.CachePartitionLostListener;
import com.hazelcast.cache.impl.event.InternalCachePartitionLostListenerAdapter;
import com.hazelcast.cache.impl.journal.CacheEventJournalReadOperation;
import com.hazelcast.cache.impl.journal.CacheEventJournalSubscribeOperation;
import com.hazelcast.cache.impl.operation.CacheListenerRegistrationOperation;
import com.hazelcast.cache.journal.EventJournalCacheEvent;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.core.Member;
import com.hazelcast.internal.journal.EventJournalInitialSubscriberState;
import com.hazelcast.internal.journal.EventJournalReader;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.ringbuffer.ReadResultSet;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationService;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.SetUtil;
import com.hazelcast.util.function.Function;
import com.hazelcast.util.function.Predicate;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Configuration;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.integration.CompletionListener;
import javax.cache.processor.EntryProcessor;
import javax.cache.processor.EntryProcessorException;
import javax.cache.processor.EntryProcessorResult;

public class CacheProxy<K, V>
extends AbstractCacheProxy<K, V>
implements EventJournalReader<EventJournalCacheEvent<K, V>> {
    CacheProxy(CacheConfig<K, V> cacheConfig, NodeEngine nodeEngine, ICacheService cacheService) {
        super(cacheConfig, nodeEngine, cacheService);
    }

    public V get(K key) {
        return (V)this.get((Object)key, (ExpiryPolicy)null);
    }

    public Map<K, V> getAll(Set<? extends K> keys) {
        return this.getAll((Set)keys, (ExpiryPolicy)null);
    }

    public boolean containsKey(K key) {
        this.ensureOpen();
        CacheProxyUtil.validateNotNull(key);
        Object dataKey = this.serializationService.toData(key);
        Operation operation = this.operationProvider.createContainsKeyOperation((Data)dataKey);
        OperationService operationService = this.getNodeEngine().getOperationService();
        int partitionId = this.getPartitionId((Data)dataKey);
        InternalCompletableFuture future = operationService.invokeOnPartition(this.getServiceName(), operation, partitionId);
        return (Boolean)future.join();
    }

    public void loadAll(Set<? extends K> keys, boolean replaceExistingValues, CompletionListener completionListener) {
        this.ensureOpen();
        CacheProxyUtil.validateNotNull(keys);
        for (Object key : keys) {
            CacheProxyUtil.validateConfiguredTypes(this.cacheConfig, key);
        }
        Set<Data> keysData = SetUtil.createHashSet(keys.size());
        for (Object key : keys) {
            CacheProxyUtil.validateNotNull(key);
            keysData.add((Data)this.serializationService.toData(key));
        }
        AbstractCacheProxyBase.LoadAllTask loadAllTask = new AbstractCacheProxyBase.LoadAllTask(this.operationProvider, keysData, replaceExistingValues, completionListener);
        try {
            this.submitLoadAllTask(loadAllTask);
        }
        catch (Exception e) {
            if (completionListener != null) {
                completionListener.onException(e);
            }
            throw new CacheException((Throwable)e);
        }
    }

    public void put(K key, V value) {
        this.put((Object)key, (Object)value, (ExpiryPolicy)null);
    }

    public V getAndPut(K key, V value) {
        return (V)this.getAndPut((Object)key, (Object)value, (ExpiryPolicy)null);
    }

    public void putAll(Map<? extends K, ? extends V> map) {
        this.putAll((Map)map, (ExpiryPolicy)null);
    }

    public boolean putIfAbsent(K key, V value) {
        return this.putIfAbsent((Object)key, (Object)value, (ExpiryPolicy)null);
    }

    public boolean remove(K key) {
        try {
            InternalCompletableFuture future = this.removeAsyncInternal(key, null, false, false, true);
            return (Boolean)future.get();
        }
        catch (Throwable e) {
            throw ExceptionUtil.rethrowAllowedTypeFirst(e, CacheException.class);
        }
    }

    public boolean remove(K key, V oldValue) {
        try {
            InternalCompletableFuture future = this.removeAsyncInternal(key, oldValue, true, false, true);
            return (Boolean)future.get();
        }
        catch (Throwable e) {
            throw ExceptionUtil.rethrowAllowedTypeFirst(e, CacheException.class);
        }
    }

    public V getAndRemove(K key) {
        try {
            InternalCompletableFuture future = this.removeAsyncInternal(key, null, false, true, true);
            return future.get();
        }
        catch (Throwable e) {
            throw ExceptionUtil.rethrowAllowedTypeFirst(e, CacheException.class);
        }
    }

    public boolean replace(K key, V oldValue, V newValue) {
        return this.replace((Object)key, (Object)oldValue, (Object)newValue, (ExpiryPolicy)null);
    }

    public boolean replace(K key, V value) {
        return this.replace((Object)key, (Object)value, (ExpiryPolicy)null);
    }

    public V getAndReplace(K key, V value) {
        return (V)this.getAndReplace((Object)key, (Object)value, (ExpiryPolicy)null);
    }

    public void removeAll(Set<? extends K> keys) {
        this.ensureOpen();
        CacheProxyUtil.validateNotNull(keys);
        if (keys.isEmpty()) {
            return;
        }
        this.removeAllInternal(keys);
    }

    public void removeAll() {
        this.ensureOpen();
        this.removeAllInternal(null);
    }

    public void clear() {
        this.ensureOpen();
        this.clearInternal();
    }

    public <C extends Configuration<K, V>> C getConfiguration(Class<C> clazz) {
        if (clazz.isInstance(this.cacheConfig)) {
            return (C)((Configuration)clazz.cast(this.cacheConfig.getAsReadOnly()));
        }
        throw new IllegalArgumentException("The configuration class " + clazz + " is not supported by this implementation");
    }

    public <T> T invoke(K key, EntryProcessor<K, V, T> entryProcessor, Object ... arguments) throws EntryProcessorException {
        this.ensureOpen();
        CacheProxyUtil.validateNotNull(key);
        Preconditions.checkNotNull(entryProcessor, "Entry Processor is null");
        Object keyData = this.serializationService.toData(key);
        Integer completionId = this.registerCompletionLatch(1);
        Operation op = this.operationProvider.createEntryProcessorOperation((Data)keyData, completionId, (EntryProcessor)entryProcessor, arguments);
        try {
            OperationService operationService = this.getNodeEngine().getOperationService();
            int partitionId = this.getPartitionId((Data)keyData);
            InternalCompletableFuture future = operationService.invokeOnPartition(this.getServiceName(), op, partitionId);
            Object safely = future.join();
            this.waitCompletionLatch(completionId);
            return (T)safely;
        }
        catch (CacheException ce) {
            this.deregisterCompletionLatch(completionId);
            throw ce;
        }
        catch (Exception e) {
            this.deregisterCompletionLatch(completionId);
            throw new EntryProcessorException((Throwable)e);
        }
    }

    public <T> Map<K, EntryProcessorResult<T>> invokeAll(Set<? extends K> keys, EntryProcessor<K, V, T> entryProcessor, Object ... arguments) {
        this.ensureOpen();
        CacheProxyUtil.validateNotNull(keys);
        Preconditions.checkNotNull(entryProcessor, "Entry Processor is null");
        Map<K, CacheEntryProcessorResult<T>> allResult = MapUtil.createHashMap(keys.size());
        for (K key : keys) {
            CacheEntryProcessorResult<T> ceResult;
            CacheProxyUtil.validateNotNull(key);
            try {
                T result = this.invoke(key, entryProcessor, arguments);
                ceResult = result != null ? new CacheEntryProcessorResult<T>(result) : null;
            }
            catch (Exception e) {
                ceResult = new CacheEntryProcessorResult<T>(e);
            }
            if (ceResult == null) continue;
            allResult.put(key, ceResult);
        }
        return allResult;
    }

    public <T> T unwrap(Class<T> clazz) {
        if (clazz.isAssignableFrom(this.getClass())) {
            return clazz.cast(this);
        }
        throw new IllegalArgumentException("Unwrapping to " + clazz + " is not supported by this implementation");
    }

    public void registerCacheEntryListener(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
        this.registerCacheEntryListener(cacheEntryListenerConfiguration, true);
    }

    @Override
    public void registerCacheEntryListener(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration, boolean addToConfig) {
        this.ensureOpen();
        Preconditions.checkNotNull(cacheEntryListenerConfiguration, "CacheEntryListenerConfiguration can't be null");
        CacheEventListenerAdaptor<K, V> entryListener = new CacheEventListenerAdaptor<K, V>(this, cacheEntryListenerConfiguration, this.getNodeEngine().getSerializationService());
        String regId = ((ICacheService)this.getService()).registerListener(this.getDistributedObjectName(), entryListener, entryListener, false);
        if (regId != null) {
            if (addToConfig) {
                this.cacheConfig.addCacheEntryListenerConfiguration(cacheEntryListenerConfiguration);
            }
            this.addListenerLocally(regId, cacheEntryListenerConfiguration);
            if (addToConfig) {
                this.updateCacheListenerConfigOnOtherNodes(cacheEntryListenerConfiguration, true);
            }
        }
    }

    public void deregisterCacheEntryListener(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
        Preconditions.checkNotNull(cacheEntryListenerConfiguration, "CacheEntryListenerConfiguration can't be null");
        String regId = this.getListenerIdLocal(cacheEntryListenerConfiguration);
        if (regId != null && ((ICacheService)this.getService()).deregisterListener(this.getDistributedObjectName(), regId)) {
            this.removeListenerLocally(cacheEntryListenerConfiguration);
            this.cacheConfig.removeCacheEntryListenerConfiguration(cacheEntryListenerConfiguration);
            this.updateCacheListenerConfigOnOtherNodes(cacheEntryListenerConfiguration, false);
        }
    }

    private void updateCacheListenerConfigOnOtherNodes(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration, boolean isRegister) {
        OperationService operationService = this.getNodeEngine().getOperationService();
        Set<Member> members = this.getNodeEngine().getClusterService().getMembers();
        for (Member member : members) {
            if (member.localMember()) continue;
            CacheListenerRegistrationOperation op = new CacheListenerRegistrationOperation(this.getDistributedObjectName(), cacheEntryListenerConfiguration, isRegister);
            operationService.invokeOnTarget("hz:impl:cacheService", op, member.getAddress());
        }
    }

    public Iterator<Cache.Entry<K, V>> iterator() {
        this.ensureOpen();
        return new ClusterWideIterator(this, false);
    }

    @Override
    public Iterator<Cache.Entry<K, V>> iterator(int fetchSize) {
        this.ensureOpen();
        return new ClusterWideIterator(this, fetchSize, false);
    }

    @Override
    public Iterator<Cache.Entry<K, V>> iterator(int fetchSize, int partitionId, boolean prefetchValues) {
        this.ensureOpen();
        return new CachePartitionIterator(this, fetchSize, partitionId, prefetchValues);
    }

    @Override
    public String addPartitionLostListener(CachePartitionLostListener listener) {
        Preconditions.checkNotNull(listener, "CachePartitionLostListener can't be null");
        CachePartitionLostEventFilter filter = new CachePartitionLostEventFilter();
        InternalCachePartitionLostListenerAdapter listenerAdapter = new InternalCachePartitionLostListenerAdapter(listener);
        this.injectDependencies(listener);
        EventRegistration registration = ((ICacheService)this.getService()).getNodeEngine().getEventService().registerListener("hz:impl:cacheService", this.name, filter, listenerAdapter);
        return registration.getId();
    }

    @Override
    public boolean removePartitionLostListener(String id) {
        Preconditions.checkNotNull(id, "Listener ID should not be null!");
        return ((ICacheService)this.getService()).getNodeEngine().getEventService().deregisterListener("hz:impl:cacheService", this.name, id);
    }

    @Override
    public ICompletableFuture<EventJournalInitialSubscriberState> subscribeToEventJournal(int partitionId) {
        CacheEventJournalSubscribeOperation op = new CacheEventJournalSubscribeOperation(this.nameWithPrefix);
        op.setPartitionId(partitionId);
        return this.getNodeEngine().getOperationService().invokeOnPartition(op);
    }

    @Override
    public <T> ICompletableFuture<ReadResultSet<T>> readFromEventJournal(long startSequence, int minSize, int maxSize, int partitionId, Predicate<? super EventJournalCacheEvent<K, V>> predicate, Function<? super EventJournalCacheEvent<K, V>, ? extends T> projection) {
        if (maxSize < minSize) {
            throw new IllegalArgumentException("maxSize " + maxSize + " must be greater or equal to minSize " + minSize);
        }
        CacheEventJournalReadOperation op = new CacheEventJournalReadOperation(this.nameWithPrefix, startSequence, minSize, maxSize, predicate, projection);
        op.setPartitionId(partitionId);
        return this.getNodeEngine().getOperationService().invokeOnPartition(op);
    }
}

