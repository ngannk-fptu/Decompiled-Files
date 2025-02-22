/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.event;

import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.map.impl.querycache.event.DefaultQueryCacheEventData;
import com.hazelcast.map.impl.querycache.event.QueryCacheEventData;
import com.hazelcast.nio.serialization.Data;

public final class QueryCacheEventDataBuilder {
    private long sequence;
    private Data dataKey;
    private Data dataNewValue;
    private Data dataOldValue;
    private int eventType;
    private int partitionId;
    private InternalSerializationService serializationService;
    private final boolean includeValue;

    private QueryCacheEventDataBuilder(boolean includeValue) {
        this.includeValue = includeValue;
    }

    public static QueryCacheEventDataBuilder newQueryCacheEventDataBuilder(boolean includeValue) {
        return new QueryCacheEventDataBuilder(includeValue);
    }

    public QueryCacheEventDataBuilder withDataKey(Data dataKey) {
        this.dataKey = dataKey;
        return this;
    }

    public QueryCacheEventDataBuilder withDataNewValue(Data dataNewValue) {
        this.dataNewValue = this.includeValue ? dataNewValue : null;
        return this;
    }

    public QueryCacheEventDataBuilder withDataOldValue(Data dataOldValue) {
        this.dataOldValue = this.includeValue ? dataOldValue : null;
        return this;
    }

    public QueryCacheEventDataBuilder withSequence(long sequence) {
        this.sequence = sequence;
        return this;
    }

    public QueryCacheEventDataBuilder withEventType(int eventType) {
        this.eventType = eventType;
        return this;
    }

    public QueryCacheEventDataBuilder withPartitionId(int partitionId) {
        this.partitionId = partitionId;
        return this;
    }

    public QueryCacheEventDataBuilder withSerializationService(InternalSerializationService serializationService) {
        this.serializationService = serializationService;
        return this;
    }

    public QueryCacheEventData build() {
        DefaultQueryCacheEventData eventData = new DefaultQueryCacheEventData();
        eventData.setDataKey(this.dataKey);
        eventData.setDataNewValue(this.dataNewValue);
        eventData.setDataOldValue(this.dataOldValue);
        eventData.setSequence(this.sequence);
        eventData.setSerializationService(this.serializationService);
        eventData.setEventType(this.eventType);
        eventData.setPartitionId(this.partitionId);
        return eventData;
    }
}

