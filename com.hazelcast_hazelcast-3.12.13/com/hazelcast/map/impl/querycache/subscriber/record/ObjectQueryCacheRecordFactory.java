/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber.record;

import com.hazelcast.map.impl.querycache.subscriber.record.ObjectQueryCacheRecord;
import com.hazelcast.map.impl.querycache.subscriber.record.QueryCacheRecord;
import com.hazelcast.map.impl.querycache.subscriber.record.QueryCacheRecordFactory;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;

public class ObjectQueryCacheRecordFactory
implements QueryCacheRecordFactory {
    private final SerializationService serializationService;

    public ObjectQueryCacheRecordFactory(SerializationService serializationService) {
        this.serializationService = serializationService;
    }

    @Override
    public QueryCacheRecord createRecord(Data valueData) {
        return new ObjectQueryCacheRecord(valueData, this.serializationService);
    }

    @Override
    public boolean isEquals(Object value1, Object value2) {
        Object v2;
        Object v1 = value1 instanceof Data ? this.serializationService.toObject(value1) : value1;
        Object object = v2 = value2 instanceof Data ? this.serializationService.toObject(value2) : value2;
        if (v1 == null && v2 == null) {
            return true;
        }
        if (v1 == null) {
            return false;
        }
        if (v2 == null) {
            return false;
        }
        return v1.equals(v2);
    }
}

