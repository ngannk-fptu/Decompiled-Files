/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber.record;

import com.hazelcast.map.impl.querycache.subscriber.record.AbstractQueryCacheRecord;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.serialization.SerializationService;

class ObjectQueryCacheRecord
extends AbstractQueryCacheRecord {
    private final Object value;

    public ObjectQueryCacheRecord(Data valueData, SerializationService serializationService) {
        this.value = serializationService.toObject(valueData);
    }

    public Object getValue() {
        return this.value;
    }
}

