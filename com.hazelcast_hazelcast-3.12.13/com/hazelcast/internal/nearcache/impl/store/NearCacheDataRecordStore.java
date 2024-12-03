/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.nearcache.impl.store;

import com.hazelcast.config.NearCacheConfig;
import com.hazelcast.internal.nearcache.impl.record.NearCacheDataRecord;
import com.hazelcast.internal.nearcache.impl.store.BaseHeapNearCacheRecordStore;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.Clock;

public class NearCacheDataRecordStore<K, V>
extends BaseHeapNearCacheRecordStore<K, V, NearCacheDataRecord> {
    public NearCacheDataRecordStore(String name, NearCacheConfig nearCacheConfig, SerializationService serializationService, ClassLoader classLoader) {
        super(name, nearCacheConfig, serializationService, classLoader);
    }

    @Override
    protected long getKeyStorageMemoryCost(K key) {
        if (key instanceof Data) {
            return REFERENCE_SIZE + (long)((Data)key).getHeapCost();
        }
        return 0L;
    }

    @Override
    protected long getRecordStorageMemoryCost(NearCacheDataRecord record) {
        if (record == null) {
            return 0L;
        }
        Data value = (Data)record.getValue();
        return REFERENCE_SIZE + REFERENCE_SIZE + 4L + REFERENCE_SIZE + 16L + (long)(value != null ? value.getHeapCost() : 0) + 40L + 4L;
    }

    @Override
    protected NearCacheDataRecord createRecord(V value) {
        Data dataValue = this.toData(value);
        long creationTime = Clock.currentTimeMillis();
        if (this.timeToLiveMillis > 0L) {
            return new NearCacheDataRecord(dataValue, creationTime, creationTime + this.timeToLiveMillis);
        }
        return new NearCacheDataRecord(dataValue, creationTime, -1L);
    }

    @Override
    protected void updateRecordValue(NearCacheDataRecord record, V value) {
        record.setValue(this.toData(value));
    }
}

