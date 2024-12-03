/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber.record;

import com.hazelcast.map.impl.querycache.subscriber.record.DataQueryCacheRecord;
import com.hazelcast.map.impl.querycache.subscriber.record.QueryCacheRecord;
import com.hazelcast.map.impl.querycache.subscriber.record.QueryCacheRecordFactory;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;

public class DataQueryCacheRecordFactory
implements QueryCacheRecordFactory {
    private final SerializationService serializationService;

    public DataQueryCacheRecordFactory(SerializationService serializationService) {
        this.serializationService = serializationService;
    }

    @Override
    public QueryCacheRecord createRecord(Data valueData) {
        return new DataQueryCacheRecord(valueData, this.serializationService);
    }

    @Override
    public boolean isEquals(Object value1, Object value2) {
        return this.serializationService.toData(value1).equals(this.serializationService.toData(value2));
    }
}

