/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.CacheException
 *  javax.cache.CacheManager
 *  javax.cache.configuration.CacheEntryListenerConfiguration
 *  javax.cache.expiry.ExpiryPolicy
 */
package com.hazelcast.cache.impl;

import com.hazelcast.cache.CacheStatistics;
import com.hazelcast.cache.HazelcastCacheManager;
import com.hazelcast.cache.impl.AbstractCacheProxyBase;
import com.hazelcast.cache.impl.CacheClearResponse;
import com.hazelcast.cache.impl.CacheProxyUtil;
import com.hazelcast.cache.impl.CacheSyncListenerCompleter;
import com.hazelcast.cache.impl.HazelcastServerCacheManager;
import com.hazelcast.cache.impl.ICacheService;
import com.hazelcast.cache.impl.event.CachePartitionLostEventFilter;
import com.hazelcast.cache.impl.event.CachePartitionLostListener;
import com.hazelcast.cache.impl.event.InternalCachePartitionLostListenerAdapter;
import com.hazelcast.cache.impl.operation.MutableOperation;
import com.hazelcast.config.CacheConfig;
import com.hazelcast.config.CachePartitionLostListenerConfig;
import com.hazelcast.config.ListenerConfig;
import com.hazelcast.core.HazelcastInstanceNotActiveException;
import com.hazelcast.nio.ClassLoaderUtil;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.OperationFactory;
import com.hazelcast.spi.OperationService;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.SetUtil;
import java.util.Collection;
import java.util.EventListener;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.expiry.ExpiryPolicy;

abstract class AbstractInternalCacheProxy<K, V>
extends AbstractCacheProxyBase<K, V>
implements CacheSyncListenerCompleter {
    private static final long MAX_COMPLETION_LATCH_WAIT_TIME = TimeUnit.MINUTES.toMillis(5L);
    private static final long COMPLETION_LATCH_WAIT_TIME_STEP = TimeUnit.SECONDS.toMillis(1L);
    private final AtomicInteger completionIdCounter = new AtomicInteger();
    private final ConcurrentMap<CacheEntryListenerConfiguration, String> asyncListenerRegistrations;
    private final ConcurrentMap<CacheEntryListenerConfiguration, String> syncListenerRegistrations;
    private final ConcurrentMap<Integer, CountDownLatch> syncLocks;
    private AtomicReference<HazelcastServerCacheManager> cacheManagerRef = new AtomicReference();

    AbstractInternalCacheProxy(CacheConfig<K, V> cacheConfig, NodeEngine nodeEngine, ICacheService cacheService) {
        super(cacheConfig, nodeEngine, cacheService);
        this.asyncListenerRegistrations = new ConcurrentHashMap<CacheEntryListenerConfiguration, String>();
        this.syncListenerRegistrations = new ConcurrentHashMap<CacheEntryListenerConfiguration, String>();
        this.syncLocks = new ConcurrentHashMap<Integer, CountDownLatch>();
        List<CachePartitionLostListenerConfig> configs = cacheConfig.getPartitionLostListenerConfigs();
        for (CachePartitionLostListenerConfig listenerConfig : configs) {
            CachePartitionLostListener listener = (CachePartitionLostListener)this.initializeListener(listenerConfig);
            if (listener == null) continue;
            CachePartitionLostEventFilter filter = new CachePartitionLostEventFilter();
            InternalCachePartitionLostListenerAdapter listenerAdapter = new InternalCachePartitionLostListenerAdapter(listener);
            ((ICacheService)this.getService()).getNodeEngine().getEventService().registerListener("hz:impl:cacheService", this.name, filter, listenerAdapter);
        }
    }

    public CacheManager getCacheManager() {
        return this.cacheManagerRef.get();
    }

    @Override
    public void setCacheManager(HazelcastCacheManager cacheManager) {
        assert (cacheManager instanceof HazelcastServerCacheManager);
        if (this.cacheManagerRef.get() == cacheManager) {
            return;
        }
        if (!this.cacheManagerRef.compareAndSet(null, (HazelcastServerCacheManager)cacheManager)) {
            if (this.cacheManagerRef.get() == cacheManager) {
                return;
            }
            throw new IllegalStateException("Cannot overwrite a Cache's CacheManager.");
        }
    }

    @Override
    public void resetCacheManager() {
        this.cacheManagerRef.set(null);
    }

    @Override
    protected void postDestroy() {
        CacheManager cacheManager = this.cacheManagerRef.get();
        if (cacheManager != null) {
            cacheManager.destroyCache(this.getName());
        }
        this.resetCacheManager();
    }

    @Override
    public void countDownCompletionLatch(int countDownLatchId) {
        if (countDownLatchId != -1) {
            CountDownLatch countDownLatch = (CountDownLatch)this.syncLocks.get(countDownLatchId);
            if (countDownLatch == null) {
                return;
            }
            countDownLatch.countDown();
            if (countDownLatch.getCount() == 0L) {
                this.deregisterCompletionLatch(countDownLatchId);
            }
        }
    }

    @Override
    protected void closeListeners() {
        this.deregisterAllCacheEntryListener(this.syncListenerRegistrations.values());
        this.deregisterAllCacheEntryListener(this.asyncListenerRegistrations.values());
        this.syncListenerRegistrations.clear();
        this.asyncListenerRegistrations.clear();
        this.notifyAndClearSyncListenerLatches();
    }

    @Override
    public CacheStatistics getLocalCacheStatistics() {
        return ((ICacheService)this.getService()).createCacheStatIfAbsent(this.cacheConfig.getNameWithPrefix());
    }

    <T> InternalCompletableFuture<T> invoke(Operation op, int partitionId, boolean completionOperation) {
        Integer completionId = null;
        if (completionOperation) {
            completionId = this.registerCompletionLatch(1);
            if (op instanceof MutableOperation) {
                ((MutableOperation)((Object)op)).setCompletionId(completionId);
            }
        }
        try {
            InternalCompletableFuture future = this.getNodeEngine().getOperationService().invokeOnPartition(this.getServiceName(), op, partitionId);
            if (completionOperation) {
                this.waitCompletionLatch(completionId);
            }
            InternalCompletableFuture internalCompletableFuture = future;
            return internalCompletableFuture;
        }
        catch (Throwable e) {
            if (e instanceof IllegalStateException) {
                this.close();
            }
            throw ExceptionUtil.rethrowAllowedTypeFirst(e, CacheException.class);
        }
        finally {
            if (completionOperation) {
                this.deregisterCompletionLatch(completionId);
            }
        }
    }

    <T> InternalCompletableFuture<T> invoke(Operation op, Data keyData, boolean completionOperation) {
        int partitionId = this.getPartitionId(keyData);
        return this.invoke(op, partitionId, completionOperation);
    }

    <T> InternalCompletableFuture<T> removeAsyncInternal(K key, V oldValue, boolean hasOldValue, boolean isGet, boolean withCompletionEvent) {
        this.ensureOpen();
        if (hasOldValue) {
            CacheProxyUtil.validateNotNull(key, oldValue);
            CacheProxyUtil.validateConfiguredTypes(this.cacheConfig, key, oldValue);
        } else {
            CacheProxyUtil.validateNotNull(key);
            CacheProxyUtil.validateConfiguredTypes(this.cacheConfig, key);
        }
        Object keyData = this.serializationService.toData(key);
        Object valueData = this.serializationService.toData(oldValue);
        Operation operation = isGet ? this.operationProvider.createGetAndRemoveOperation((Data)keyData, -1) : this.operationProvider.createRemoveOperation((Data)keyData, (Data)valueData, -1);
        return this.invoke(operation, (Data)keyData, withCompletionEvent);
    }

    <T> InternalCompletableFuture<T> replaceAsyncInternal(K key, V oldValue, V newValue, ExpiryPolicy expiryPolicy, boolean hasOldValue, boolean isGet, boolean withCompletionEvent) {
        this.ensureOpen();
        if (hasOldValue) {
            CacheProxyUtil.validateNotNull(key, oldValue, newValue);
            CacheProxyUtil.validateConfiguredTypes(this.cacheConfig, key, oldValue, newValue);
        } else {
            CacheProxyUtil.validateNotNull(key, newValue);
            CacheProxyUtil.validateConfiguredTypes(this.cacheConfig, key, newValue);
        }
        Object keyData = this.serializationService.toData(key);
        Object oldValueData = this.serializationService.toData(oldValue);
        Object newValueData = this.serializationService.toData(newValue);
        Operation operation = isGet ? this.operationProvider.createGetAndReplaceOperation((Data)keyData, (Data)newValueData, expiryPolicy, -1) : this.operationProvider.createReplaceOperation((Data)keyData, (Data)oldValueData, (Data)newValueData, expiryPolicy, -1);
        return this.invoke(operation, (Data)keyData, withCompletionEvent);
    }

    <T> InternalCompletableFuture<T> putAsyncInternal(K key, V value, ExpiryPolicy expiryPolicy, boolean isGet, boolean withCompletionEvent) {
        this.ensureOpen();
        CacheProxyUtil.validateNotNull(key, value);
        CacheProxyUtil.validateConfiguredTypes(this.cacheConfig, key, value);
        Object keyData = this.serializationService.toData(key);
        Object valueData = this.serializationService.toData(value);
        Operation op = this.operationProvider.createPutOperation((Data)keyData, (Data)valueData, expiryPolicy, isGet, -1);
        return this.invoke(op, (Data)keyData, withCompletionEvent);
    }

    InternalCompletableFuture<Boolean> putIfAbsentAsyncInternal(K key, V value, ExpiryPolicy expiryPolicy, boolean withCompletionEvent) {
        this.ensureOpen();
        CacheProxyUtil.validateNotNull(key, value);
        CacheProxyUtil.validateConfiguredTypes(this.cacheConfig, key, value);
        Object keyData = this.serializationService.toData(key);
        Object valueData = this.serializationService.toData(value);
        Operation operation = this.operationProvider.createPutIfAbsentOperation((Data)keyData, (Data)valueData, expiryPolicy, -1);
        return this.invoke(operation, (Data)keyData, withCompletionEvent);
    }

    void clearInternal() {
        try {
            OperationService operationService = this.getNodeEngine().getOperationService();
            OperationFactory operationFactory = this.operationProvider.createClearOperationFactory();
            Map<Integer, Object> results = operationService.invokeOnAllPartitions(this.getServiceName(), operationFactory);
            for (Object result : results.values()) {
                Object response;
                if (result == null || !(result instanceof CacheClearResponse) || !((response = ((CacheClearResponse)result).getResponse()) instanceof Throwable)) continue;
                throw (Throwable)response;
            }
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrowAllowedTypeFirst(t, CacheException.class);
        }
    }

    void removeAllInternal(Set<? extends K> keys) {
        Set<Data> keysData = null;
        if (keys != null) {
            keysData = SetUtil.createHashSet(keys.size());
            for (K key : keys) {
                CacheProxyUtil.validateNotNull(key);
                keysData.add((Data)this.serializationService.toData(key));
            }
        }
        int partitionCount = this.getNodeEngine().getPartitionService().getPartitionCount();
        Integer completionId = this.registerCompletionLatch(partitionCount);
        OperationService operationService = this.getNodeEngine().getOperationService();
        OperationFactory operationFactory = this.operationProvider.createRemoveAllOperationFactory(keysData, completionId);
        try {
            Map<Integer, Object> results = operationService.invokeOnAllPartitions(this.getServiceName(), operationFactory);
            int completionCount = 0;
            for (Object result : results.values()) {
                if (result == null || !(result instanceof CacheClearResponse)) continue;
                Object response = ((CacheClearResponse)result).getResponse();
                if (response instanceof Boolean) {
                    ++completionCount;
                }
                if (!(response instanceof Throwable)) continue;
                throw (Throwable)response;
            }
            this.waitCompletionLatch(completionId, partitionCount - completionCount);
        }
        catch (Throwable t) {
            this.deregisterCompletionLatch(completionId);
            throw ExceptionUtil.rethrowAllowedTypeFirst(t, CacheException.class);
        }
    }

    void addListenerLocally(String regId, CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
        if (cacheEntryListenerConfiguration.isSynchronous()) {
            this.syncListenerRegistrations.putIfAbsent(cacheEntryListenerConfiguration, regId);
        } else {
            this.asyncListenerRegistrations.putIfAbsent(cacheEntryListenerConfiguration, regId);
        }
    }

    String removeListenerLocally(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
        ConcurrentMap<CacheEntryListenerConfiguration, String> regs = cacheEntryListenerConfiguration.isSynchronous() ? this.syncListenerRegistrations : this.asyncListenerRegistrations;
        return (String)regs.remove(cacheEntryListenerConfiguration);
    }

    String getListenerIdLocal(CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration) {
        ConcurrentMap<CacheEntryListenerConfiguration, String> regs = cacheEntryListenerConfiguration.isSynchronous() ? this.syncListenerRegistrations : this.asyncListenerRegistrations;
        return (String)regs.get(cacheEntryListenerConfiguration);
    }

    private void deregisterAllCacheEntryListener(Collection<String> listenerRegistrations) {
        ICacheService service = (ICacheService)this.getService();
        for (String regId : listenerRegistrations) {
            service.deregisterListener(this.nameWithPrefix, regId);
        }
    }

    private void notifyAndClearSyncListenerLatches() {
        Collection latches = this.syncLocks.values();
        Iterator iterator = latches.iterator();
        while (iterator.hasNext()) {
            CountDownLatch latch = (CountDownLatch)iterator.next();
            iterator.remove();
            while (latch.getCount() > 0L) {
                latch.countDown();
            }
        }
    }

    Integer registerCompletionLatch(int count) {
        if (!this.syncListenerRegistrations.isEmpty()) {
            int id = this.completionIdCounter.incrementAndGet();
            int size = this.syncListenerRegistrations.size();
            CountDownLatch countDownLatch = new CountDownLatch(count * size);
            this.syncLocks.put(id, countDownLatch);
            return id;
        }
        return -1;
    }

    void deregisterCompletionLatch(Integer countDownLatchId) {
        if (countDownLatchId != -1) {
            this.syncLocks.remove(countDownLatchId);
        }
    }

    void waitCompletionLatch(Integer countDownLatchId) {
        CountDownLatch countDownLatch;
        if (countDownLatchId != -1 && (countDownLatch = (CountDownLatch)this.syncLocks.get(countDownLatchId)) != null) {
            this.awaitLatch(countDownLatch);
        }
    }

    private void waitCompletionLatch(Integer countDownLatchId, int offset) {
        CountDownLatch countDownLatch;
        if (countDownLatchId != -1 && (countDownLatch = (CountDownLatch)this.syncLocks.get(countDownLatchId)) != null) {
            for (int i = 0; i < offset; ++i) {
                countDownLatch.countDown();
            }
            this.awaitLatch(countDownLatch);
        }
    }

    private void awaitLatch(CountDownLatch countDownLatch) {
        try {
            for (long currentTimeoutMs = MAX_COMPLETION_LATCH_WAIT_TIME; currentTimeoutMs > 0L && !countDownLatch.await(COMPLETION_LATCH_WAIT_TIME_STEP, TimeUnit.MILLISECONDS); currentTimeoutMs -= COMPLETION_LATCH_WAIT_TIME_STEP) {
                if (!this.getNodeEngine().isRunning()) {
                    throw new HazelcastInstanceNotActiveException();
                }
                if (!this.isClosed()) continue;
                throw new IllegalStateException("Cache (" + this.nameWithPrefix + ") is closed!");
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            ExceptionUtil.sneakyThrow(e);
        }
    }

    private <T extends EventListener> T initializeListener(ListenerConfig listenerConfig) {
        EventListener listener = null;
        if (listenerConfig.getImplementation() != null) {
            listener = listenerConfig.getImplementation();
        } else if (listenerConfig.getClassName() != null) {
            try {
                listener = (EventListener)ClassLoaderUtil.newInstance(this.getNodeEngine().getConfigClassLoader(), listenerConfig.getClassName());
            }
            catch (Exception e) {
                throw ExceptionUtil.rethrow(e);
            }
        }
        this.injectDependencies(listener);
        return (T)listener;
    }
}

