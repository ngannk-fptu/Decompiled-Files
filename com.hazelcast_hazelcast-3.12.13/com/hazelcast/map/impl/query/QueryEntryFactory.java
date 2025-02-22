/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.query;

import com.hazelcast.config.CacheDeserializedValues;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.impl.CachedQueryEntry;
import com.hazelcast.query.impl.QueryEntry;
import com.hazelcast.query.impl.QueryableEntry;
import com.hazelcast.query.impl.getters.Extractors;

public final class QueryEntryFactory {
    private final CacheDeserializedValues cacheDeserializedValues;
    private final InternalSerializationService serializationService;
    private final Extractors extractors;

    public QueryEntryFactory(CacheDeserializedValues cacheDeserializedValues, InternalSerializationService serializationService, Extractors extractors) {
        this.cacheDeserializedValues = cacheDeserializedValues;
        this.serializationService = serializationService;
        this.extractors = extractors;
    }

    public QueryableEntry newEntry(Data key, Object value) {
        switch (this.cacheDeserializedValues) {
            case NEVER: {
                return new QueryEntry(this.serializationService, key, value, this.extractors);
            }
        }
        return new CachedQueryEntry(this.serializationService, key, value, this.extractors);
    }
}

