/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.event;

import com.hazelcast.core.EntryEventType;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.map.impl.event.DefaultEntryEventFilteringStrategy;
import com.hazelcast.map.impl.event.EntryEventData;
import com.hazelcast.map.impl.event.EventData;
import com.hazelcast.map.impl.event.FilteringStrategy;
import com.hazelcast.map.impl.event.MapEventPublisherImpl;
import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.accumulator.Accumulator;
import com.hazelcast.map.impl.querycache.event.DefaultQueryCacheEventData;
import com.hazelcast.map.impl.querycache.event.QueryCacheEventData;
import com.hazelcast.map.impl.querycache.event.QueryCacheEventDataBuilder;
import com.hazelcast.map.impl.querycache.publisher.MapPublisherRegistry;
import com.hazelcast.map.impl.querycache.publisher.PartitionAccumulatorRegistry;
import com.hazelcast.map.impl.querycache.publisher.PublisherContext;
import com.hazelcast.map.impl.querycache.publisher.PublisherRegistry;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.EventFilter;
import com.hazelcast.util.CollectionUtil;
import com.hazelcast.util.Preconditions;
import java.util.Collection;
import java.util.Collections;

public class QueryCacheEventPublisher {
    private final FilteringStrategy filteringStrategy;
    private final QueryCacheContext queryCacheContext;
    private final InternalSerializationService serializationService;

    public QueryCacheEventPublisher(FilteringStrategy filteringStrategy, QueryCacheContext queryCacheContext, InternalSerializationService serializationService) {
        this.filteringStrategy = filteringStrategy;
        this.queryCacheContext = queryCacheContext;
        this.serializationService = serializationService;
    }

    public void addEventToQueryCache(Object eventData) {
        Preconditions.checkInstanceOf(EventData.class, eventData, "eventData");
        String mapName = ((EventData)eventData).getMapName();
        int eventType = ((EventData)eventData).getEventType();
        if (EntryEventType.EXPIRED.getType() == eventType) {
            return;
        }
        Collection<PartitionAccumulatorRegistry> partitionAccumulatorRegistries = this.getPartitionAccumulatorRegistries(mapName);
        if (CollectionUtil.isEmpty(partitionAccumulatorRegistries)) {
            return;
        }
        if (!(eventData instanceof EntryEventData)) {
            return;
        }
        EntryEventData entryEvenData = (EntryEventData)eventData;
        Data dataKey = entryEvenData.getDataKey();
        Data dataNewValue = entryEvenData.getDataNewValue();
        Data dataOldValue = entryEvenData.getDataOldValue();
        int partitionId = this.queryCacheContext.getPartitionId(entryEvenData.dataKey);
        for (PartitionAccumulatorRegistry registry : partitionAccumulatorRegistries) {
            DefaultQueryCacheEventData singleEventData = (DefaultQueryCacheEventData)this.convertQueryCacheEventDataOrNull(registry, dataKey, dataNewValue, dataOldValue, eventType, partitionId, mapName);
            if (singleEventData == null) continue;
            Accumulator accumulator = registry.getOrCreate(partitionId);
            accumulator.accumulate(singleEventData);
        }
    }

    public void hintMapEvent(Address caller, String mapName, EntryEventType eventType, int numberOfEntriesAffected, int partitionId) {
        Collection<PartitionAccumulatorRegistry> partitionAccumulatorRegistries = this.getPartitionAccumulatorRegistries(mapName);
        for (PartitionAccumulatorRegistry accumulatorRegistry : partitionAccumulatorRegistries) {
            Accumulator accumulator = accumulatorRegistry.getOrCreate(partitionId);
            QueryCacheEventData singleEventData = QueryCacheEventDataBuilder.newQueryCacheEventDataBuilder(false).withPartitionId(partitionId).withEventType(eventType.getType()).build();
            accumulator.accumulate(singleEventData);
        }
    }

    private QueryCacheEventData convertQueryCacheEventDataOrNull(PartitionAccumulatorRegistry registry, Data dataKey, Data dataNewValue, Data dataOldValue, int eventTypeId, int partitionId, String mapName) {
        int producedEventTypeId;
        EventFilter eventFilter = registry.getEventFilter();
        Object eventType = EntryEventType.getByType(eventTypeId);
        eventType = this.filteringStrategy instanceof DefaultEntryEventFilteringStrategy ? this.getCQCEventTypeOrNull((EntryEventType)((Object)eventType), eventFilter, dataKey, dataNewValue, dataOldValue, mapName) : ((producedEventTypeId = this.filteringStrategy.doFilter(eventFilter, dataKey, dataOldValue, dataNewValue, (EntryEventType)((Object)eventType), mapName)) == -1 ? null : EntryEventType.getByType(producedEventTypeId));
        if (eventType == null) {
            return null;
        }
        boolean includeValue = MapEventPublisherImpl.isIncludeValue(eventFilter);
        return QueryCacheEventDataBuilder.newQueryCacheEventDataBuilder(includeValue).withPartitionId(partitionId).withDataKey(dataKey).withDataNewValue(dataNewValue).withEventType(eventType.getType()).withDataOldValue(dataOldValue).withSerializationService(this.serializationService).build();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private EntryEventType getCQCEventTypeOrNull(EntryEventType eventType, EventFilter eventFilter, Data dataKey, Data dataNewValue, Data dataOldValue, String mapName) {
        boolean newValueMatching;
        boolean bl = newValueMatching = this.filteringStrategy.doFilter(eventFilter, dataKey, dataOldValue, dataNewValue, eventType, mapName) != -1;
        if (eventType == EntryEventType.UPDATED) {
            boolean oldValueMatching;
            boolean bl2 = oldValueMatching = this.filteringStrategy.doFilter(eventFilter, dataKey, null, dataOldValue, EntryEventType.ADDED, mapName) != -1;
            if (oldValueMatching) {
                if (newValueMatching) return eventType;
                return EntryEventType.REMOVED;
            }
            if (!newValueMatching) return null;
            return EntryEventType.ADDED;
        }
        if (newValueMatching) return eventType;
        return null;
    }

    private Collection<PartitionAccumulatorRegistry> getPartitionAccumulatorRegistries(String mapName) {
        PublisherContext publisherContext = this.queryCacheContext.getPublisherContext();
        MapPublisherRegistry mapPublisherRegistry = publisherContext.getMapPublisherRegistry();
        PublisherRegistry publisherRegistry = mapPublisherRegistry.getOrNull(mapName);
        if (publisherRegistry == null) {
            return Collections.emptySet();
        }
        return publisherRegistry.getAll().values();
    }
}

