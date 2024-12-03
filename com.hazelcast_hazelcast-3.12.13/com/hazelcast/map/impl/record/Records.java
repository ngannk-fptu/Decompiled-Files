/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.record;

import com.hazelcast.map.impl.record.Record;
import com.hazelcast.map.impl.record.RecordInfo;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;

public final class Records {
    private Records() {
    }

    public static void applyRecordInfo(Record record, RecordInfo replicationInfo) {
        record.setVersion(replicationInfo.getVersion());
        record.setHits(replicationInfo.getHits());
        record.setTtl(replicationInfo.getTtl());
        record.setMaxIdle(replicationInfo.getMaxIdle());
        record.setCreationTime(replicationInfo.getCreationTime());
        record.setLastAccessTime(replicationInfo.getLastAccessTime());
        record.setLastUpdateTime(replicationInfo.getLastUpdateTime());
        record.setExpirationTime(replicationInfo.getExpirationTime());
        record.setLastStoredTime(replicationInfo.getLastStoredTime());
    }

    public static RecordInfo buildRecordInfo(Record record) {
        RecordInfo info = new RecordInfo();
        info.setVersion(record.getVersion());
        info.setHits(record.getHits());
        info.setCreationTime(record.getCreationTime());
        info.setLastAccessTime(record.getLastAccessTime());
        info.setLastUpdateTime(record.getLastUpdateTime());
        info.setTtl(record.getTtl());
        info.setMaxIdle(record.getMaxIdle());
        info.setExpirationTime(record.getExpirationTime());
        info.setLastStoredTime(record.getLastStoredTime());
        return info;
    }

    public static Object getCachedValue(Record record) {
        Object cachedValue;
        do {
            if ((cachedValue = record.getCachedValueUnsafe()) instanceof Thread) continue;
            return cachedValue;
        } while ((cachedValue = ThreadWrapper.unwrapOrNull(cachedValue)) == null);
        return cachedValue;
    }

    public static Object getValueOrCachedValue(Record record, SerializationService serializationService) {
        Object cachedValue = record.getCachedValueUnsafe();
        if (cachedValue == Record.NOT_CACHED) {
            return record.getValue();
        }
        while (true) {
            if (cachedValue == null) {
                Object valueBeforeCas = record.getValue();
                if (!Records.shouldCache(valueBeforeCas)) {
                    return valueBeforeCas;
                }
                Object fromCache = Records.tryStoreIntoCache(record, valueBeforeCas, serializationService);
                if (fromCache != null) {
                    return fromCache;
                }
            } else if (cachedValue instanceof Thread) {
                if ((cachedValue = ThreadWrapper.unwrapOrNull(cachedValue)) != null) {
                    return cachedValue;
                }
            } else {
                return cachedValue;
            }
            Thread.yield();
            cachedValue = record.getCachedValueUnsafe();
        }
    }

    private static Object tryStoreIntoCache(Record record, Object valueBeforeCas, SerializationService serializationService) {
        Thread currentThread = Thread.currentThread();
        if (!record.casCachedValue(null, currentThread)) {
            return null;
        }
        Object valueAfterCas = record.getValue();
        Object object = null;
        try {
            object = serializationService.toObject(valueBeforeCas);
        }
        catch (RuntimeException e) {
            record.casCachedValue(currentThread, null);
            throw e;
        }
        if (valueAfterCas == valueBeforeCas) {
            Object wrappedObject = ThreadWrapper.wrapIfNeeded(object);
            record.casCachedValue(currentThread, wrappedObject);
        } else {
            record.casCachedValue(currentThread, null);
        }
        return object;
    }

    static boolean shouldCache(Object value) {
        return value instanceof Data && !((Data)value).isPortable();
    }

    private static final class ThreadWrapper
    extends Thread {
        private final Thread wrappedValue;

        private ThreadWrapper(Thread wrappedValue) {
            this.wrappedValue = wrappedValue;
        }

        static Object unwrapOrNull(Object o) {
            if (o instanceof ThreadWrapper) {
                return ((ThreadWrapper)o).wrappedValue;
            }
            return null;
        }

        static Object wrapIfNeeded(Object object) {
            if (object instanceof Thread) {
                return new ThreadWrapper((Thread)object);
            }
            return object;
        }
    }
}

