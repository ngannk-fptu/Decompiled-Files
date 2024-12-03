/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.cache.impl.record;

import com.hazelcast.cache.impl.record.CacheDataRecord;
import com.hazelcast.cache.impl.record.CacheObjectRecord;
import com.hazelcast.cache.impl.record.CacheRecord;
import com.hazelcast.config.InMemoryFormat;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;

public class CacheRecordFactory<R extends CacheRecord> {
    protected InMemoryFormat inMemoryFormat;
    protected SerializationService serializationService;

    public CacheRecordFactory(InMemoryFormat inMemoryFormat, SerializationService serializationService) {
        this.inMemoryFormat = inMemoryFormat;
        this.serializationService = serializationService;
    }

    public R newRecordWithExpiry(Object value, long creationTime, long expiryTime) {
        CacheRecord record;
        switch (this.inMemoryFormat) {
            case BINARY: {
                Object dataValue = this.serializationService.toData(value);
                record = this.createCacheDataRecord((Data)dataValue, creationTime, expiryTime);
                break;
            }
            case OBJECT: {
                Object objectValue = this.serializationService.toObject(value);
                record = this.createCacheObjectRecord(objectValue, creationTime, expiryTime);
                break;
            }
            case NATIVE: {
                throw new IllegalArgumentException("Native storage format is supported in Hazelcast Enterprise only. Make sure you have Hazelcast Enterprise JARs on your classpath!");
            }
            default: {
                throw new IllegalArgumentException("Invalid storage format: " + (Object)((Object)this.inMemoryFormat));
            }
        }
        return (R)record;
    }

    protected CacheRecord createCacheDataRecord(Data dataValue, long creationTime, long expiryTime) {
        return new CacheDataRecord(dataValue, creationTime, expiryTime);
    }

    protected CacheRecord createCacheObjectRecord(Object objectValue, long creationTime, long expiryTime) {
        return new CacheObjectRecord(objectValue, creationTime, expiryTime);
    }

    public static boolean isExpiredAt(long expirationTime, long now) {
        return expirationTime > -1L && expirationTime <= now;
    }
}

