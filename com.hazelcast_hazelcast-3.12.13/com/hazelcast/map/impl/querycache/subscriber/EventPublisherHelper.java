/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber;

import com.hazelcast.core.EntryEventType;
import com.hazelcast.core.IMapEvent;
import com.hazelcast.core.MapEvent;
import com.hazelcast.core.Member;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.map.EventLostEvent;
import com.hazelcast.map.QueryCache;
import com.hazelcast.map.impl.DataAwareEntryEvent;
import com.hazelcast.map.impl.EntryEventFilter;
import com.hazelcast.map.impl.event.EventData;
import com.hazelcast.map.impl.querycache.QueryCacheContext;
import com.hazelcast.map.impl.querycache.QueryCacheEventService;
import com.hazelcast.map.impl.querycache.event.LocalCacheWideEventData;
import com.hazelcast.map.impl.querycache.event.LocalEntryEventData;
import com.hazelcast.map.impl.querycache.subscriber.DefaultQueryCache;
import com.hazelcast.map.impl.querycache.subscriber.InternalQueryCache;
import com.hazelcast.map.impl.querycache.subscriber.SubscriberContext;
import com.hazelcast.map.impl.querycache.subscriber.record.QueryCacheRecord;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.impl.getters.Extractors;
import com.hazelcast.spi.EventFilter;
import com.hazelcast.spi.serialization.SerializationService;

public final class EventPublisherHelper {
    private EventPublisherHelper() {
    }

    static void publishEntryEvent(QueryCacheContext context, String mapName, String cacheId, Data dataKey, Data dataNewValue, QueryCacheRecord oldRecord, EntryEventType eventType, Extractors extractors) {
        if (!EventPublisherHelper.hasListener(context, mapName, cacheId)) {
            return;
        }
        QueryCacheEventService eventService = EventPublisherHelper.getQueryCacheEventService(context);
        Object oldValue = EventPublisherHelper.getOldValue(oldRecord);
        LocalEntryEventData eventData = EventPublisherHelper.createLocalEntryEventData(cacheId, dataKey, dataNewValue, oldValue, eventType.getType(), -1, context);
        eventService.publish(mapName, cacheId, eventData, dataKey.hashCode(), extractors);
    }

    public static boolean hasListener(QueryCache queryCache) {
        DefaultQueryCache defaultQueryCache = (DefaultQueryCache)queryCache;
        QueryCacheEventService eventService = EventPublisherHelper.getQueryCacheEventService(defaultQueryCache.context);
        return eventService.hasListener(defaultQueryCache.mapName, defaultQueryCache.cacheId);
    }

    private static boolean hasListener(QueryCacheContext context, String mapName, String cacheId) {
        QueryCacheEventService eventService = EventPublisherHelper.getQueryCacheEventService(context);
        return eventService.hasListener(mapName, cacheId);
    }

    static void publishCacheWideEvent(InternalQueryCache queryCache, int numberOfEntriesAffected, EntryEventType eventType) {
        if (!EventPublisherHelper.hasListener(queryCache)) {
            return;
        }
        DefaultQueryCache defaultQueryCache = (DefaultQueryCache)queryCache;
        QueryCacheContext context = defaultQueryCache.context;
        String mapName = defaultQueryCache.mapName;
        String cacheId = defaultQueryCache.cacheId;
        QueryCacheEventService eventService = EventPublisherHelper.getQueryCacheEventService(context);
        LocalCacheWideEventData eventData = new LocalCacheWideEventData(cacheId, eventType.getType(), numberOfEntriesAffected);
        eventService.publish(mapName, cacheId, eventData, cacheId.hashCode(), queryCache.getExtractors());
    }

    private static Object getOldValue(QueryCacheRecord oldRecord) {
        return oldRecord == null ? null : oldRecord.getValue();
    }

    private static LocalEntryEventData createLocalEntryEventData(String cacheId, Data dataKey, Data dataNewValue, Object oldValue, int eventType, int partitionId, QueryCacheContext context) {
        InternalSerializationService serializationService = context.getSerializationService();
        return new LocalEntryEventData(serializationService, cacheId, eventType, dataKey, oldValue, dataNewValue, partitionId);
    }

    private static QueryCacheEventService getQueryCacheEventService(QueryCacheContext context) {
        SubscriberContext subscriberContext = context.getSubscriberContext();
        return subscriberContext.getEventService();
    }

    public static void publishEventLost(QueryCacheContext context, String mapName, String cacheId, int partitionId, Extractors extractors) {
        QueryCacheEventService eventService = EventPublisherHelper.getQueryCacheEventService(context);
        int orderKey = cacheId.hashCode();
        eventService.publish(mapName, cacheId, EventPublisherHelper.createLocalEntryEventData(cacheId, null, null, null, EventLostEvent.EVENT_TYPE, partitionId, context), orderKey, extractors);
    }

    public static IMapEvent createIMapEvent(EventData eventData, EventFilter filter, Member member, SerializationService serializationService) {
        String source = eventData.getSource();
        int eventType = eventData.getEventType();
        if (eventType == EventLostEvent.EVENT_TYPE) {
            LocalEntryEventData localEventData = (LocalEntryEventData)eventData;
            int partitionId = localEventData.getPartitionId();
            return new EventLostEvent(source, null, partitionId);
        }
        if (eventType == EntryEventType.CLEAR_ALL.getType() || eventType == EntryEventType.EVICT_ALL.getType()) {
            LocalCacheWideEventData localCacheWideEventData = (LocalCacheWideEventData)eventData;
            int numberOfEntriesAffected = localCacheWideEventData.getNumberOfEntriesAffected();
            return new MapEvent(source, null, eventType, numberOfEntriesAffected);
        }
        LocalEntryEventData localEntryEventData = (LocalEntryEventData)eventData;
        Data dataKey = localEntryEventData.getKeyData();
        Data dataNewValue = localEntryEventData.getValueData();
        Data dataOldValue = localEntryEventData.getOldValueData();
        boolean includeValue = EventPublisherHelper.isIncludeValue(filter);
        return new DataAwareEntryEvent(member, eventType, source, dataKey, includeValue ? dataNewValue : null, includeValue ? dataOldValue : null, null, serializationService);
    }

    private static boolean isIncludeValue(EventFilter filter) {
        if (filter instanceof EntryEventFilter) {
            return ((EntryEventFilter)filter).isIncludeValue();
        }
        return true;
    }
}

