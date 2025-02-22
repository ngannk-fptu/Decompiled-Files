/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber.record;

import com.hazelcast.map.impl.querycache.subscriber.record.AbstractQueryCacheRecord;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;

class DataQueryCacheRecord
extends AbstractQueryCacheRecord {
    private final Data valueData;
    private final SerializationService serializationService;

    public DataQueryCacheRecord(Data valueData, SerializationService serializationService) {
        this.valueData = valueData;
        this.serializationService = serializationService;
    }

    public Object getValue() {
        return this.serializationService.toObject(this.valueData);
    }
}

