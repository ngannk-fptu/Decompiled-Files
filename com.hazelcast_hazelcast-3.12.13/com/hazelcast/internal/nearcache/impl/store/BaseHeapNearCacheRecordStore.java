/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.nearcache.impl.store;

import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.config.NearCachePreloaderConfig;
import com.hazelcast.core.IBiFunction;
import com.hazelcast.internal.adapter.DataStructureAdapter;
import com.hazelcast.internal.eviction.EvictionChecker;
import com.hazelcast.internal.nearcache.NearCacheRecord;
import com.hazelcast.internal.nearcache.impl.maxsize.EntryCountNearCacheEvictionChecker;
import com.hazelcast.internal.nearcache.impl.preloader.NearCachePreloader;
import com.hazelcast.internal.nearcache.impl.store.AbstractNearCacheRecordStore;
import com.hazelcast.internal.nearcache.impl.store.HeapNearCacheRecordMap;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import java.util.Map;

public abstract class BaseHeapNearCacheRecordStore<K, V, R extends NearCacheRecord>
extends AbstractNearCacheRecordStore<K, V, K, R, HeapNearCacheRecordMap<K, R>> {
    private static final int DEFAULT_INITIAL_CAPACITY = 1000;
    private final NearCachePreloader<K> nearCachePreloader;
    private final IBiFunction<? super K, ? super R, ? extends R> invalidatorFunction = this.createInvalidatorFunction();

    BaseHeapNearCacheRecordStore(String name, NearCacheConfig nearCacheConfig, SerializationService serializationService, ClassLoader classLoader) {
        super(nearCacheConfig, serializationService, classLoader);
        NearCachePreloaderConfig preloaderConfig = nearCacheConfig.getPreloaderConfig();
        this.nearCachePreloader = preloaderConfig.isEnabled() ? new NearCachePreloader(name, preloaderConfig, this.nearCacheStats, serializationService) : null;
    }

    @Override
    protected EvictionChecker createNearCacheEvictionChecker(EvictionConfig evictionConfig, NearCacheConfig nearCacheConfig) {
        EvictionConfig.MaxSizePolicy maxSizePolicy = evictionConfig.getMaximumSizePolicy();
        if (maxSizePolicy != EvictionConfig.MaxSizePolicy.ENTRY_COUNT) {
            throw new IllegalArgumentException(String.format("Invalid max-size policy (%s) for %s! Only %s is supported.", new Object[]{maxSizePolicy, this.getClass().getName(), EvictionConfig.MaxSizePolicy.ENTRY_COUNT}));
        }
        return new EntryCountNearCacheEvictionChecker(evictionConfig.getSize(), this.records);
    }

    @Override
    protected HeapNearCacheRecordMap<K, R> createNearCacheRecordMap(NearCacheConfig nearCacheConfig) {
        return new HeapNearCacheRecordMap(this.serializationService, 1000);
    }

    @Override
    public R getRecord(K key) {
        return (R)((NearCacheRecord)((HeapNearCacheRecordMap)this.records).get(key));
    }

    @Override
    protected R putRecord(K key, R record) {
        NearCacheRecord oldRecord = (NearCacheRecord)((HeapNearCacheRecordMap)this.records).put(key, record);
        this.nearCacheStats.incrementOwnedEntryMemoryCost(this.getTotalStorageMemoryCost(key, record));
        if (oldRecord != null) {
            this.nearCacheStats.decrementOwnedEntryMemoryCost(this.getTotalStorageMemoryCost(key, oldRecord));
        }
        return (R)oldRecord;
    }

    @Override
    protected boolean containsRecordKey(K key) {
        return ((HeapNearCacheRecordMap)this.records).containsKey(key);
    }

    @Override
    public void onEvict(K key, R record, boolean wasExpired) {
        super.onEvict(key, record, wasExpired);
        this.nearCacheStats.decrementOwnedEntryMemoryCost(this.getTotalStorageMemoryCost(key, record));
    }

    @Override
    public void doExpiration() {
        for (Map.Entry entry : ((HeapNearCacheRecordMap)this.records).entrySet()) {
            Object key = entry.getKey();
            NearCacheRecord value = (NearCacheRecord)entry.getValue();
            if (!this.isRecordExpired(value)) continue;
            this.invalidate(key);
            this.onExpire(key, value);
        }
    }

    @Override
    public void loadKeys(DataStructureAdapter<Object, ?> adapter) {
        if (this.nearCachePreloader != null) {
            this.nearCachePreloader.loadKeys(adapter);
        }
    }

    @Override
    public void storeKeys() {
        if (this.nearCachePreloader != null) {
            this.nearCachePreloader.storeKeys(((HeapNearCacheRecordMap)this.records).keySet().iterator());
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        if (this.nearCachePreloader != null) {
            this.nearCachePreloader.destroy();
        }
    }

    @Override
    protected R getOrCreateToReserve(K key, Data keyData) {
        return (R)((NearCacheRecord)((HeapNearCacheRecordMap)this.records).applyIfAbsent(key, new AbstractNearCacheRecordStore.ReserveForUpdateFunction(keyData)));
    }

    @Override
    protected V updateAndGetReserved(K key, final V value, final long reservationId, boolean deserialize) {
        NearCacheRecord existingRecord = (NearCacheRecord)((HeapNearCacheRecordMap)this.records).applyIfPresent(key, new IBiFunction<K, R, R>(){

            @Override
            public R apply(K key, R reservedRecord) {
                return BaseHeapNearCacheRecordStore.this.updateReservedRecordInternal(key, value, reservedRecord, reservationId);
            }
        });
        if (existingRecord == null || !deserialize) {
            return null;
        }
        Object cachedValue = existingRecord.getValue();
        return cachedValue instanceof Data ? this.toValue(cachedValue) : cachedValue;
    }

    @Override
    public void invalidate(K key) {
        ((HeapNearCacheRecordMap)this.records).applyIfPresent(key, this.invalidatorFunction);
        this.nearCacheStats.incrementInvalidationRequests();
    }

    private IBiFunction<K, R, R> createInvalidatorFunction() {
        return new IBiFunction<K, R, R>(){

            @Override
            public R apply(K key, R record) {
                if (BaseHeapNearCacheRecordStore.this.canUpdateStats(record)) {
                    BaseHeapNearCacheRecordStore.this.nearCacheStats.decrementOwnedEntryCount();
                    BaseHeapNearCacheRecordStore.this.nearCacheStats.decrementOwnedEntryMemoryCost(BaseHeapNearCacheRecordStore.this.getTotalStorageMemoryCost(key, record));
                    BaseHeapNearCacheRecordStore.this.nearCacheStats.incrementInvalidations();
                }
                return null;
            }
        };
    }
}

