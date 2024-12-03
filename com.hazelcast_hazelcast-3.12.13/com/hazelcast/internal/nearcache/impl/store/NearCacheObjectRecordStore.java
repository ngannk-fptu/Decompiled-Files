/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.nearcache.impl.store;

import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.internal.nearcache.impl.record.NearCacheObjectRecord;
import com.hazelcast.internal.nearcache.impl.store.BaseHeapNearCacheRecordStore;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.Clock;

public class NearCacheObjectRecordStore<K, V>
extends BaseHeapNearCacheRecordStore<K, V, NearCacheObjectRecord<V>> {
    public NearCacheObjectRecordStore(String name, NearCacheConfig nearCacheConfig, SerializationService serializationService, ClassLoader classLoader) {
        super(name, nearCacheConfig, serializationService, classLoader);
    }

    @Override
    protected long getKeyStorageMemoryCost(K key) {
        return 0L;
    }

    @Override
    protected long getRecordStorageMemoryCost(NearCacheObjectRecord record) {
        return 0L;
    }

    @Override
    protected NearCacheObjectRecord<V> createRecord(V value) {
        value = this.toValue(value);
        long creationTime = Clock.currentTimeMillis();
        if (this.timeToLiveMillis > 0L) {
            return new NearCacheObjectRecord<V>(value, creationTime, creationTime + this.timeToLiveMillis);
        }
        return new NearCacheObjectRecord<V>(value, creationTime, -1L);
    }

    @Override
    protected void updateRecordValue(NearCacheObjectRecord<V> record, V value) {
        record.setValue(this.toValue(value));
    }
}

