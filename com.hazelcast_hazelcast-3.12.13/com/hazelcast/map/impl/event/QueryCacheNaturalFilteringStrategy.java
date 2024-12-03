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
import com.hazelcast.map.impl.nearcache.invalidation.UuidFilter;
import com.hazelcast.map.impl.query.QueryEventFilter;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.EventFilter;
import com.hazelcast.spi.impl.eventservice.impl.TrueEventFilter;
import com.hazelcast.util.MapUtil;
import com.hazelcast.util.collection.Int2ObjectHashMap;
import java.util.Collection;

public class QueryCacheNaturalFilteringStrategy
extends AbstractFilteringStrategy {
    private static final int EVENT_DATA_MAP_CAPACITY = 4;

    public QueryCacheNaturalFilteringStrategy(InternalSerializationService serializationService, MapServiceContext mapServiceContext) {
        super(serializationService, mapServiceContext);
    }

    @Override
    public int doFilter(EventFilter filter, Data dataKey, Object oldValue, Object dataValue, EntryEventType eventType, String mapNameOrNull) {
        if (filter instanceof MapPartitionLostEventFilter) {
            return -1;
        }
        EventListenerFilter filterAsEventListenerFilter = null;
        boolean originalFilterEventTypeMatches = true;
        if (filter instanceof EventListenerFilter) {
            int type = eventType.getType();
            if (type == EntryEventType.INVALIDATION.getType()) {
                return -1;
            }
            originalFilterEventTypeMatches = filter.eval(type);
            filterAsEventListenerFilter = (EventListenerFilter)filter;
            if ((filter = ((EventListenerFilter)filter).getEventFilter()) instanceof UuidFilter) {
                return -1;
            }
        }
        if (originalFilterEventTypeMatches && filter instanceof TrueEventFilter) {
            return eventType.getType();
        }
        if (filter instanceof QueryEventFilter) {
            int effectiveEventType = this.processQueryEventFilterWithAlternativeEventType(filter, eventType, dataKey, oldValue, dataValue, mapNameOrNull);
            if (effectiveEventType == -1) {
                return -1;
            }
            if (filterAsEventListenerFilter != null && effectiveEventType != eventType.getType()) {
                return filterAsEventListenerFilter.eval(effectiveEventType) ? effectiveEventType : -1;
            }
            return effectiveEventType;
        }
        if (filter instanceof EntryEventFilter) {
            return originalFilterEventTypeMatches && this.processEntryEventFilter(filter, dataKey) ? eventType.getType() : -1;
        }
        throw new IllegalArgumentException("Unknown EventFilter type = [" + filter.getClass().getCanonicalName() + "]");
    }

    @Override
    public EntryEventDataCache getEntryEventDataCache() {
        return new EntryEventDataPerEventTypeCache();
    }

    public String toString() {
        return "QueryCacheNaturalFilteringStrategy";
    }

    private int processQueryEventFilterWithAlternativeEventType(EventFilter filter, EntryEventType eventType, Data dataKey, Object dataOldValue, Object dataValue, String mapNameOrNull) {
        if (eventType == EntryEventType.UPDATED) {
            boolean newValueMatches = this.evaluateQueryEventFilter(filter, dataKey, dataValue, mapNameOrNull);
            boolean oldValueMatches = this.evaluateQueryEventFilter(filter, dataKey, dataOldValue, mapNameOrNull);
            if (oldValueMatches) {
                return newValueMatches ? EntryEventType.UPDATED.getType() : EntryEventType.REMOVED.getType();
            }
            return newValueMatches ? EntryEventType.ADDED.getType() : -1;
        }
        Object testValue = eventType == EntryEventType.REMOVED || eventType == EntryEventType.EVICTED || eventType == EntryEventType.EXPIRED ? dataOldValue : dataValue;
        return this.evaluateQueryEventFilter(filter, dataKey, testValue, mapNameOrNull) ? eventType.getType() : -1;
    }

    private class EntryEventDataPerEventTypeCache
    implements EntryEventDataCache {
        Int2ObjectHashMap<EntryEventData> eventDataIncludingValues;
        Int2ObjectHashMap<EntryEventData> eventDataExcludingValues;
        boolean empty = true;

        private EntryEventDataPerEventTypeCache() {
        }

        @Override
        public EntryEventData getOrCreateEventData(String mapName, Address caller, Data dataKey, Object newValue, Object oldValue, Object mergingValue, int eventType, boolean includingValues) {
            if (includingValues) {
                if (this.eventDataIncludingValues == null) {
                    this.eventDataIncludingValues = MapUtil.createInt2ObjectHashMap(4);
                }
                return this.getOrCreateEventData(this.eventDataIncludingValues, mapName, caller, dataKey, newValue, oldValue, mergingValue, eventType);
            }
            if (this.eventDataExcludingValues == null) {
                this.eventDataExcludingValues = MapUtil.createInt2ObjectHashMap(4);
            }
            return this.getOrCreateEventData(this.eventDataExcludingValues, mapName, caller, dataKey, null, null, null, eventType);
        }

        @Override
        public boolean isEmpty() {
            return this.empty;
        }

        @Override
        public Collection<EntryEventData> eventDataIncludingValues() {
            return this.eventDataIncludingValues == null ? null : this.eventDataIncludingValues.values();
        }

        @Override
        public Collection<EntryEventData> eventDataExcludingValues() {
            return this.eventDataExcludingValues == null ? null : this.eventDataExcludingValues.values();
        }

        private EntryEventData getOrCreateEventData(Int2ObjectHashMap<EntryEventData> eventDataPerEventType, String mapName, Address caller, Data dataKey, Object newValue, Object oldValue, Object mergingValue, int eventType) {
            if (eventDataPerEventType.containsKey(eventType)) {
                return eventDataPerEventType.get(eventType);
            }
            Data dataOldValue = oldValue == null ? null : QueryCacheNaturalFilteringStrategy.this.mapServiceContext.toData(oldValue);
            Data dataNewValue = newValue == null ? null : QueryCacheNaturalFilteringStrategy.this.mapServiceContext.toData(newValue);
            Data dataMergingValue = mergingValue == null ? null : QueryCacheNaturalFilteringStrategy.this.mapServiceContext.toData(mergingValue);
            EntryEventData entryEventData = new EntryEventData(QueryCacheNaturalFilteringStrategy.this.getThisNodesAddress(), mapName, caller, dataKey, dataNewValue, dataOldValue, dataMergingValue, eventType);
            eventDataPerEventType.put(eventType, entryEventData);
            if (this.empty) {
                this.empty = false;
            }
            return entryEventData;
        }
    }
}

