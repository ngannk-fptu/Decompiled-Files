/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.event;

import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.map.impl.EntryEventFilter;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.event.FilteringStrategy;
import com.hazelcast.map.impl.query.QueryEventFilter;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.impl.CachedQueryEntry;
import com.hazelcast.query.impl.getters.Extractors;
import com.hazelcast.spi.EventFilter;

public abstract class AbstractFilteringStrategy
implements FilteringStrategy {
    protected final InternalSerializationService serializationService;
    protected final MapServiceContext mapServiceContext;

    public AbstractFilteringStrategy(InternalSerializationService serializationService, MapServiceContext mapServiceContext) {
        this.serializationService = serializationService;
        this.mapServiceContext = mapServiceContext;
    }

    protected String getThisNodesAddress() {
        return this.mapServiceContext.getNodeEngine().getThisAddress().toString();
    }

    protected boolean processEntryEventFilter(EventFilter filter, Data dataKey) {
        EntryEventFilter eventFilter = (EntryEventFilter)filter;
        return eventFilter.eval(dataKey);
    }

    protected boolean evaluateQueryEventFilter(EventFilter filter, Data dataKey, Object testValue, String mapNameOrNull) {
        Extractors extractors = this.getExtractorsForMapName(mapNameOrNull);
        QueryEventFilter queryEventFilter = (QueryEventFilter)filter;
        CachedQueryEntry entry = new CachedQueryEntry(this.serializationService, dataKey, testValue, extractors);
        return queryEventFilter.eval(entry);
    }

    private Extractors getExtractorsForMapName(String mapNameOrNull) {
        if (mapNameOrNull == null) {
            return Extractors.newBuilder(this.serializationService).build();
        }
        return this.mapServiceContext.getExtractors(mapNameOrNull);
    }
}

