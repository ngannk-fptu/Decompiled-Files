/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.proxy;

import com.hazelcast.config.MapConfig;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.core.ExecutionCallback;
import com.hazelcast.core.ICompletableFuture;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.internal.nearcache.NearCache;
import com.hazelcast.internal.nearcache.impl.invalidation.BatchNearCacheInvalidation;
import com.hazelcast.internal.nearcache.impl.invalidation.Invalidation;
import com.hazelcast.internal.nearcache.impl.invalidation.RepairingHandler;
import com.hazelcast.map.EntryProcessor;
import com.hazelcast.map.impl.MapEntries;
import com.hazelcast.map.impl.MapService;
import com.hazelcast.map.impl.nearcache.MapNearCacheManager;
import com.hazelcast.map.impl.nearcache.invalidation.InvalidationListener;
import com.hazelcast.map.impl.nearcache.invalidation.UuidFilter;
import com.hazelcast.map.impl.proxy.MapProxyImpl;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.Predicate;
import com.hazelcast.spi.ExecutionService;
import com.hazelcast.spi.InternalCompletableFuture;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.executor.CompletedFuture;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class NearCachedMapProxyImpl<K, V>
extends MapProxyImpl<K, V> {
    private final ClusterService clusterService;
    private final boolean cacheLocalEntries;
    private final boolean invalidateOnChange;
    private final boolean serializeKeys;
    private MapNearCacheManager mapNearCacheManager;
    private NearCache<Object, Object> nearCache;
    private RepairingHandler repairingHandler;
    private volatile String invalidationListenerId;

    public NearCachedMapProxyImpl(String name, MapService mapService, NodeEngine nodeEngine, MapConfig mapConfig) {
        super(name, mapService, nodeEngine, mapConfig);
        this.clusterService = nodeEngine.getClusterService();
        NearCacheConfig nearCacheConfig = mapConfig.getNearCacheConfig();
        this.cacheLocalEntries = nearCacheConfig.isCacheLocalEntries();
        this.invalidateOnChange = nearCacheConfig.isInvalidateOnChange();
        this.serializeKeys = nearCacheConfig.isSerializeKeys();
    }

    public NearCache<Object, Object> getNearCache() {
        return this.nearCache;
    }

    @Override
    public void initialize() {
        super.initialize();
        this.mapNearCacheManager = this.mapServiceContext.getMapNearCacheManager();
        this.nearCache = this.mapNearCacheManager.getOrCreateNearCache(this.name, this.mapConfig.getNearCacheConfig());
        if (this.invalidateOnChange) {
            this.registerInvalidationListener();
        }
    }

    @Override
    protected V getInternal(Object key) {
        Object value = this.getCachedValue(key = this.toNearCacheKeyWithStrategy(key), true);
        if (value != NearCache.NOT_CACHED) {
            return (V)value;
        }
        try {
            Data keyData = this.toDataWithStrategy(key);
            long reservationId = this.tryReserveForUpdate(key, keyData);
            value = super.getInternal(keyData);
            if (reservationId != -1L) {
                value = this.tryPublishReserved(key, value, reservationId);
            }
            return (V)value;
        }
        catch (Throwable throwable) {
            this.invalidateNearCache(key);
            throw ExceptionUtil.rethrow(throwable);
        }
    }

    @Override
    protected InternalCompletableFuture<Data> getAsyncInternal(Object key) {
        InternalCompletableFuture<Data> future;
        final Object ncKey = this.toNearCacheKeyWithStrategy(key);
        Object value = this.getCachedValue(ncKey, false);
        if (value != NearCache.NOT_CACHED) {
            ExecutionService executionService = this.getNodeEngine().getExecutionService();
            return new CompletedFuture<Data>(this.serializationService, value, executionService.getExecutor("hz:async"));
        }
        Data keyData = this.toDataWithStrategy(key);
        final long reservationId = this.tryReserveForUpdate(ncKey, keyData);
        try {
            future = super.getAsyncInternal(keyData);
        }
        catch (Throwable t) {
            this.invalidateNearCache(ncKey);
            throw ExceptionUtil.rethrow(t);
        }
        if (reservationId != -1L) {
            future.andThen(new ExecutionCallback<Data>(){

                @Override
                public void onResponse(Data value) {
                    NearCachedMapProxyImpl.this.nearCache.tryPublishReserved(ncKey, value, reservationId, false);
                }

                @Override
                public void onFailure(Throwable t) {
                    NearCachedMapProxyImpl.this.invalidateNearCache(ncKey);
                }
            });
        }
        return future;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected Data putInternal(Object key, Data value, long ttl, TimeUnit ttlUnit, long maxIdle, TimeUnit maxIdleUnit) {
        key = this.toNearCacheKeyWithStrategy(key);
        try {
            Data data = super.putInternal(key, value, ttl, ttlUnit, maxIdle, maxIdleUnit);
            return data;
        }
        finally {
            this.invalidateNearCache(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected boolean setTtlInternal(Object key, long ttl, TimeUnit timeUnit) {
        key = this.toNearCacheKeyWithStrategy(key);
        try {
            boolean bl = super.setTtlInternal(key, ttl, timeUnit);
            return bl;
        }
        finally {
            this.invalidateNearCache(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected boolean tryPutInternal(Object key, Data value, long timeout, TimeUnit timeunit) {
        key = this.toNearCacheKeyWithStrategy(key);
        try {
            boolean bl = super.tryPutInternal(key, value, timeout, timeunit);
            return bl;
        }
        finally {
            this.invalidateNearCache(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected Data putIfAbsentInternal(Object key, Data value, long ttl, TimeUnit ttlUnit, long maxIdle, TimeUnit maxIdleUnit) {
        key = this.toNearCacheKeyWithStrategy(key);
        try {
            Data data = super.putIfAbsentInternal(key, value, ttl, ttlUnit, maxIdle, maxIdleUnit);
            return data;
        }
        finally {
            this.invalidateNearCache(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void putTransientInternal(Object key, Data value, long ttl, TimeUnit ttlUnit, long maxIdle, TimeUnit maxIdleUnit) {
        key = this.toNearCacheKeyWithStrategy(key);
        try {
            super.putTransientInternal(key, value, ttl, ttlUnit, maxIdle, maxIdleUnit);
        }
        finally {
            this.invalidateNearCache(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected InternalCompletableFuture<Data> putAsyncInternal(Object key, Data value, long ttl, TimeUnit ttlUnit, long maxIdle, TimeUnit maxIdleUnit) {
        key = this.toNearCacheKeyWithStrategy(key);
        try {
            InternalCompletableFuture<Data> internalCompletableFuture = super.putAsyncInternal(key, value, ttl, ttlUnit, maxIdle, maxIdleUnit);
            return internalCompletableFuture;
        }
        finally {
            this.invalidateNearCache(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected InternalCompletableFuture<Data> setAsyncInternal(Object key, Data value, long ttl, TimeUnit ttlUnit, long maxIdle, TimeUnit maxIdleUnit) {
        key = this.toNearCacheKeyWithStrategy(key);
        try {
            InternalCompletableFuture<Data> internalCompletableFuture = super.setAsyncInternal(key, value, ttl, ttlUnit, maxIdle, maxIdleUnit);
            return internalCompletableFuture;
        }
        finally {
            this.invalidateNearCache(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected boolean replaceInternal(Object key, Data expect, Data update) {
        key = this.toNearCacheKeyWithStrategy(key);
        try {
            boolean bl = super.replaceInternal(key, expect, update);
            return bl;
        }
        finally {
            this.invalidateNearCache(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected Data replaceInternal(Object key, Data value) {
        key = this.toNearCacheKeyWithStrategy(key);
        try {
            Data data = super.replaceInternal(key, value);
            return data;
        }
        finally {
            this.invalidateNearCache(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void setInternal(Object key, Data value, long ttl, TimeUnit ttlUnit, long maxIdle, TimeUnit maxIdleUnit) {
        key = this.toNearCacheKeyWithStrategy(key);
        try {
            super.setInternal(key, value, ttl, ttlUnit, maxIdle, maxIdleUnit);
        }
        finally {
            this.invalidateNearCache(key);
        }
    }

    @Override
    protected boolean evictInternal(Object key) {
        key = this.toNearCacheKeyWithStrategy(key);
        try {
            boolean bl = super.evictInternal(key);
            return bl;
        }
        finally {
            this.invalidateNearCache(key);
        }
    }

    @Override
    protected void evictAllInternal() {
        try {
            super.evictAllInternal();
        }
        finally {
            this.nearCache.clear();
        }
    }

    @Override
    public void clearInternal() {
        try {
            super.clearInternal();
        }
        finally {
            this.nearCache.clear();
        }
    }

    @Override
    public void loadAllInternal(boolean replaceExistingValues) {
        try {
            super.loadAllInternal(replaceExistingValues);
        }
        finally {
            if (replaceExistingValues) {
                this.nearCache.clear();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void loadInternal(Set<K> keys, Iterable<Data> dataKeys, boolean replaceExistingValues) {
        Iterable<Data> ncKeys;
        if (this.serializeKeys) {
            dataKeys = this.convertToData(keys);
        }
        try {
            super.loadInternal(keys, dataKeys, replaceExistingValues);
            ncKeys = this.serializeKeys ? dataKeys : keys;
        }
        catch (Throwable throwable) {
            Iterable<Data> ncKeys2 = this.serializeKeys ? dataKeys : keys;
            for (Data key : ncKeys2) {
                this.invalidateNearCache(key);
            }
            throw throwable;
        }
        for (Data key : ncKeys) {
            this.invalidateNearCache(key);
        }
    }

    @Override
    protected Data removeInternal(Object key) {
        key = this.toNearCacheKeyWithStrategy(key);
        try {
            Data data = super.removeInternal(key);
            return data;
        }
        finally {
            this.invalidateNearCache(key);
        }
    }

    @Override
    protected void removeAllInternal(Predicate predicate) {
        try {
            super.removeAllInternal(predicate);
        }
        finally {
            this.nearCache.clear();
        }
    }

    @Override
    protected void deleteInternal(Object key) {
        key = this.toNearCacheKeyWithStrategy(key);
        try {
            super.deleteInternal(key);
        }
        finally {
            this.invalidateNearCache(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected boolean removeInternal(Object key, Data value) {
        key = this.toNearCacheKeyWithStrategy(key);
        try {
            boolean bl = super.removeInternal(key, value);
            return bl;
        }
        finally {
            this.invalidateNearCache(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected boolean tryRemoveInternal(Object key, long timeout, TimeUnit timeunit) {
        key = this.toNearCacheKeyWithStrategy(key);
        try {
            boolean bl = super.tryRemoveInternal(key, timeout, timeunit);
            return bl;
        }
        finally {
            this.invalidateNearCache(key);
        }
    }

    @Override
    protected InternalCompletableFuture<Data> removeAsyncInternal(Object key) {
        key = this.toNearCacheKeyWithStrategy(key);
        try {
            InternalCompletableFuture<Data> internalCompletableFuture = super.removeAsyncInternal(key);
            return internalCompletableFuture;
        }
        finally {
            this.invalidateNearCache(key);
        }
    }

    @Override
    protected boolean containsKeyInternal(Object key) {
        Object cachedValue = this.getCachedValue(key = this.toNearCacheKeyWithStrategy(key), false);
        if (cachedValue != NearCache.NOT_CACHED) {
            return cachedValue != null;
        }
        return super.containsKeyInternal(key);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void getAllInternal(Set<K> keys, List<Data> dataKeys, List<Object> resultingKeyValuePairs) {
        if (this.serializeKeys) {
            this.toDataKeysWithReservations(keys, (Collection<Data>)dataKeys, null, null);
        }
        LinkedList<Data> ncKeys = this.serializeKeys ? dataKeys : new LinkedList<Data>(keys);
        this.populateResultFromNearCache(ncKeys, resultingKeyValuePairs);
        if (ncKeys.isEmpty()) {
            return;
        }
        Map<Object, Long> reservations = MapUtil.createHashMap(ncKeys.size());
        Map<Data, Object> reverseKeyMap = null;
        if (!this.serializeKeys) {
            reverseKeyMap = MapUtil.createHashMap(ncKeys.size());
            this.toDataKeysWithReservations(ncKeys, dataKeys, reservations, reverseKeyMap);
        } else {
            this.createNearCacheReservations(ncKeys, reservations);
        }
        try {
            int currentSize = resultingKeyValuePairs.size();
            super.getAllInternal(keys, dataKeys, resultingKeyValuePairs);
            this.populateResultFromRemote(currentSize, resultingKeyValuePairs, reservations, reverseKeyMap);
        }
        finally {
            this.releaseReservedKeys(reservations);
        }
    }

    private void toDataKeysWithReservations(Collection<?> keys, Collection<Data> dataKeys, Map<Object, Long> reservations, Map<Data, Object> reverseKeyMap) {
        for (Object key : keys) {
            long reservationId;
            Data keyData = this.toDataWithStrategy(key);
            dataKeys.add(keyData);
            if (reservations != null && (reservationId = this.tryReserveForUpdate(key, keyData)) != -1L) {
                reservations.put(key, reservationId);
            }
            if (reverseKeyMap == null) continue;
            reverseKeyMap.put(keyData, key);
        }
    }

    private void populateResultFromNearCache(Collection keys, List<Object> resultingKeyValuePairs) {
        Iterator iterator = keys.iterator();
        while (iterator.hasNext()) {
            Object key = iterator.next();
            Object value = this.getCachedValue(key, true);
            if (value == null || value == NearCache.NOT_CACHED) continue;
            resultingKeyValuePairs.add(key);
            resultingKeyValuePairs.add(value);
            iterator.remove();
        }
    }

    private void createNearCacheReservations(Collection<Data> dataKeys, Map<Object, Long> reservations) {
        for (Data key : dataKeys) {
            long reservationId = this.tryReserveForUpdate(key, key);
            if (reservationId == -1L) continue;
            reservations.put(key, reservationId);
        }
    }

    private void populateResultFromRemote(int currentSize, List<Object> resultingKeyValuePairs, Map<Object, Long> reservations, Map<Data, Object> reverseKeyMap) {
        for (int i = currentSize; i < resultingKeyValuePairs.size(); i += 2) {
            Long reservationId;
            Data ncKey;
            Data keyData = (Data)resultingKeyValuePairs.get(i);
            Data valueData = (Data)resultingKeyValuePairs.get(i + 1);
            Data data = ncKey = this.serializeKeys ? keyData : reverseKeyMap.get(keyData);
            if (!this.serializeKeys) {
                resultingKeyValuePairs.set(i, ncKey);
            }
            if ((reservationId = reservations.get(ncKey)) == null) continue;
            Object cachedValue = this.tryPublishReserved(ncKey, valueData, reservationId);
            resultingKeyValuePairs.set(i + 1, cachedValue);
            reservations.remove(ncKey);
        }
    }

    private void releaseReservedKeys(Map<Object, Long> reservationResults) {
        for (Object key : reservationResults.keySet()) {
            this.invalidateNearCache(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void invokePutAllOperationFactory(long size, int[] partitions, MapEntries[] entries) throws Exception {
        try {
            super.invokePutAllOperationFactory(size, partitions, entries);
        }
        finally {
            if (this.serializeKeys) {
                for (MapEntries mapEntries : entries) {
                    if (mapEntries == null) continue;
                    for (int i = 0; i < mapEntries.size(); ++i) {
                        this.invalidateNearCache(mapEntries.getKey(i));
                    }
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void finalizePutAll(Map<?, ?> map) {
        try {
            super.finalizePutAll(map);
        }
        finally {
            if (!this.serializeKeys) {
                for (Object key : map.keySet()) {
                    this.invalidateNearCache(key);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Data executeOnKeyInternal(Object key, EntryProcessor entryProcessor) {
        key = this.toNearCacheKeyWithStrategy(key);
        try {
            Data data = super.executeOnKeyInternal(key, entryProcessor);
            return data;
        }
        finally {
            this.invalidateNearCache(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ICompletableFuture<Map<K, Object>> submitToKeysInternal(Set<K> keys, Set<Data> dataKeys, EntryProcessor entryProcessor) {
        Set<Data> ncKeys;
        ICompletableFuture iCompletableFuture;
        if (this.serializeKeys) {
            this.toDataCollectionWithNonNullKeyValidation(keys, dataKeys);
        }
        try {
            iCompletableFuture = super.submitToKeysInternal((Set)keys, (Set)dataKeys, entryProcessor);
            ncKeys = this.serializeKeys ? dataKeys : keys;
        }
        catch (Throwable throwable) {
            Set<Data> ncKeys2 = this.serializeKeys ? dataKeys : keys;
            for (Data key : ncKeys2) {
                this.invalidateNearCache(key);
            }
            throw throwable;
        }
        for (Data key : ncKeys) {
            this.invalidateNearCache(key);
        }
        return iCompletableFuture;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public InternalCompletableFuture<Object> executeOnKeyInternal(Object key, EntryProcessor entryProcessor, ExecutionCallback<Object> callback) {
        key = this.toNearCacheKeyWithStrategy(key);
        try {
            InternalCompletableFuture internalCompletableFuture = super.executeOnKeyInternal(key, entryProcessor, (ExecutionCallback)callback);
            return internalCompletableFuture;
        }
        finally {
            this.invalidateNearCache(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void executeOnEntriesInternal(EntryProcessor entryProcessor, Predicate predicate, List<Data> resultingKeyValuePairs) {
        try {
            super.executeOnEntriesInternal(entryProcessor, predicate, (List)resultingKeyValuePairs);
        }
        catch (Throwable throwable) {
            for (int i = 0; i < resultingKeyValuePairs.size(); i += 2) {
                Data key = resultingKeyValuePairs.get(i);
                this.invalidateNearCache(this.serializeKeys ? key : this.toObject(key));
            }
            throw throwable;
        }
        for (int i = 0; i < resultingKeyValuePairs.size(); i += 2) {
            Data key = resultingKeyValuePairs.get(i);
            this.invalidateNearCache(this.serializeKeys ? key : this.toObject(key));
        }
    }

    @Override
    protected void postDestroy() {
        try {
            if (this.invalidateOnChange) {
                this.mapNearCacheManager.deregisterRepairingHandler(this.name);
                this.removeEntryListener(this.invalidationListenerId);
            }
        }
        finally {
            super.postDestroy();
        }
    }

    protected void invalidateNearCache(Object key) {
        if (key == null) {
            return;
        }
        this.nearCache.invalidate(key);
    }

    private Object tryPublishReserved(Object key, Object value, long reservationId) {
        assert (value != NearCache.NOT_CACHED);
        Object cachedValue = this.nearCache.tryPublishReserved(key, value, reservationId, true);
        return cachedValue != null ? cachedValue : value;
    }

    private Object getCachedValue(Object key, boolean deserializeValue) {
        Object value = this.nearCache.get(key);
        if (value == null) {
            return NearCache.NOT_CACHED;
        }
        if (value == NearCache.CACHED_AS_NULL) {
            return null;
        }
        this.mapServiceContext.interceptAfterGet(this.name, value);
        return deserializeValue ? this.toObject(value) : value;
    }

    private long tryReserveForUpdate(Object key, Data keyData) {
        if (!this.cachingAllowedFor(keyData)) {
            return -1L;
        }
        return this.nearCache.tryReserveForUpdate(key, keyData);
    }

    private boolean cachingAllowedFor(Data keyData) {
        return this.cacheLocalEntries || this.clusterService.getLocalMember().isLiteMember() || !this.isOwn(keyData);
    }

    private boolean isOwn(Data key) {
        int partitionId = this.partitionService.getPartitionId(key);
        return this.partitionService.isPartitionOwner(partitionId);
    }

    private Object toNearCacheKeyWithStrategy(Object key) {
        return this.serializeKeys ? this.serializationService.toData(key, this.partitionStrategy) : key;
    }

    public String addNearCacheInvalidationListener(InvalidationListener listener) {
        String localMemberUuid = this.getNodeEngine().getClusterService().getLocalMember().getUuid();
        UuidFilter eventFilter = new UuidFilter(localMemberUuid);
        return this.mapServiceContext.addEventListener(listener, eventFilter, this.name);
    }

    private void registerInvalidationListener() {
        this.repairingHandler = this.mapNearCacheManager.newRepairingHandler(this.name, this.nearCache);
        this.invalidationListenerId = this.addNearCacheInvalidationListener(new NearCacheInvalidationListener());
    }

    private final class NearCacheInvalidationListener
    implements InvalidationListener {
        private NearCacheInvalidationListener() {
        }

        @Override
        public void onInvalidate(Invalidation invalidation) {
            assert (invalidation != null);
            if (invalidation instanceof BatchNearCacheInvalidation) {
                List<Invalidation> batch = ((BatchNearCacheInvalidation)invalidation).getInvalidations();
                for (Invalidation single : batch) {
                    this.handleInternal(single);
                }
            } else {
                this.handleInternal(invalidation);
            }
        }

        private void handleInternal(Invalidation single) {
            NearCachedMapProxyImpl.this.repairingHandler.handle(single.getKey(), single.getSourceUuid(), single.getPartitionUuid(), single.getSequence());
        }
    }
}

