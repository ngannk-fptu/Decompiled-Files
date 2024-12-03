/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.event;

import com.hazelcast.core.EntryEventType;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.map.impl.EntryEventFilter;
import com.hazelcast.map.impl.EventListenerFilter;
import com.hazelcast.map.impl.MapPartitionLostEventFilter;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.event.AbstractFilteringStrategy;
import com.hazelcast.map.impl.event.EntryEventData;
import com.hazelcast.map.impl.event.EntryEventDataCache;
import com.hazelcast.map.impl.query.QueryEventFilter;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.EventFilter;
import com.hazelcast.spi.impl.eventservice.impl.TrueEventFilter;
import java.util.Collection;
import java.util.Collections;

public class DefaultEntryEventFilteringStrategy
extends AbstractFilteringStrategy {
    public DefaultEntryEventFilteringStrategy(InternalSerializationService serializationService, MapServiceContext mapServiceContext) {
        super(serializationService, mapServiceContext);
    }

    @Override
    public int doFilter(EventFilter filter, Data dataKey, Object oldValue, Object dataValue, EntryEventType eventType, String mapNameOrNull) {
        if (filter instanceof MapPartitionLostEventFilter) {
            return -1;
        }
        if (filter instanceof EventListenerFilter) {
            if (!filter.eval(eventType.getType())) {
                return -1;
            }
            filter = ((EventListenerFilter)filter).getEventFilter();
        }
        if (filter instanceof TrueEventFilter) {
            return eventType.getType();
        }
        if (filter instanceof QueryEventFilter) {
            return this.processQueryEventFilter(filter, eventType, dataKey, oldValue, dataValue, mapNameOrNull) ? eventType.getType() : -1;
        }
        if (filter instanceof EntryEventFilter) {
            return this.processEntryEventFilter(filter, dataKey) ? eventType.getType() : -1;
        }
        throw new IllegalArgumentException("Unknown EventFilter type = [" + filter.getClass().getCanonicalName() + "]");
    }

    @Override
    public EntryEventDataCache getEntryEventDataCache() {
        return new DefaultEntryEventDataCache();
    }

    public String toString() {
        return "DefaultEntryEventFilteringStrategy";
    }

    private boolean processQueryEventFilter(EventFilter filter, EntryEventType eventType, Data dataKey, Object oldValue, Object dataValue, String mapNameOrNull) {
        Object testValue = eventType == EntryEventType.REMOVED || eventType == EntryEventType.EVICTED || eventType == EntryEventType.EXPIRED ? oldValue : dataValue;
        return this.evaluateQueryEventFilter(filter, dataKey, testValue, mapNameOrNull);
    }

    private class DefaultEntryEventDataCache
    implements EntryEventDataCache {
        EntryEventData eventDataIncludingValues;
        EntryEventData eventDataExcludingValues;

        private DefaultEntryEventDataCache() {
        }

        @Override
        public EntryEventData getOrCreateEventData(String mapName, Address caller, Data dataKey, Object newValue, Object oldValue, Object mergingValue, int eventType, boolean includingValues) {
            if (includingValues && this.eventDataIncludingValues != null) {
                return this.eventDataIncludingValues;
            }
            if (!includingValues && this.eventDataExcludingValues != null) {
                return this.eventDataExcludingValues;
            }
            EntryEventData entryEventData = new EntryEventData(DefaultEntryEventFilteringStrategy.this.getThisNodesAddress(), mapName, caller, dataKey, includingValues ? DefaultEntryEventFilteringStrategy.this.mapServiceContext.toData(newValue) : null, includingValues ? DefaultEntryEventFilteringStrategy.this.mapServiceContext.toData(oldValue) : null, includingValues ? DefaultEntryEventFilteringStrategy.this.mapServiceContext.toData(mergingValue) : null, eventType);
            if (includingValues) {
                this.eventDataIncludingValues = entryEventData;
            } else {
                this.eventDataExcludingValues = entryEventData;
            }
            return entryEventData;
        }

        @Override
        public boolean isEmpty() {
            return this.eventDataIncludingValues == null && this.eventDataExcludingValues == null;
        }

        @Override
        public Collection<EntryEventData> eventDataIncludingValues() {
            return this.eventDataIncludingValues == null ? null : Collections.singleton(this.eventDataIncludingValues);
        }

        @Override
        public Collection<EntryEventData> eventDataExcludingValues() {
            return this.eventDataExcludingValues == null ? null : Collections.singleton(this.eventDataExcludingValues);
        }
    }
}

