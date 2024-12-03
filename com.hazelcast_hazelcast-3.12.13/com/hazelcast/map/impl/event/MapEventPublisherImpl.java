/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.event;

import com.hazelcast.core.EntryEventType;
import com.hazelcast.core.EntryView;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.map.impl.EntryEventFilter;
import com.hazelcast.map.impl.EventListenerFilter;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapPartitionLostEventFilter;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.event.DefaultEntryEventFilteringStrategy;
import com.hazelcast.map.impl.event.EntryEventData;
import com.hazelcast.map.impl.event.EntryEventDataCache;
import com.hazelcast.map.impl.event.FilteringStrategy;
import com.hazelcast.map.impl.event.MapEventData;
import com.hazelcast.map.impl.event.MapEventPublisher;
import com.hazelcast.map.impl.event.MapPartitionEventData;
import com.hazelcast.map.impl.event.QueryCacheEventPublisher;
import com.hazelcast.map.impl.event.QueryCacheNaturalFilteringStrategy;
import com.hazelcast.map.impl.query.QueryEventFilter;
import com.hazelcast.map.impl.wan.MapReplicationRemove;
import com.hazelcast.map.impl.wan.MapReplicationUpdate;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.EventFilter;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.EventService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.impl.eventservice.impl.TrueEventFilter;
import com.hazelcast.spi.partition.IPartitionService;
import com.hazelcast.spi.properties.HazelcastProperty;
import com.hazelcast.util.Clock;
import com.hazelcast.util.CollectionUtil;
import com.hazelcast.wan.ReplicationEventObject;
import com.hazelcast.wan.WanReplicationPublisher;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

public class MapEventPublisherImpl
implements MapEventPublisher {
    public static final String PROP_LISTENER_WITH_PREDICATE_PRODUCES_NATURAL_EVENT_TYPES = "hazelcast.map.entry.filtering.natural.event.types";
    public static final HazelcastProperty LISTENER_WITH_PREDICATE_PRODUCES_NATURAL_EVENT_TYPES = new HazelcastProperty("hazelcast.map.entry.filtering.natural.event.types", false);
    protected final NodeEngine nodeEngine;
    protected final EventService eventService;
    protected final IPartitionService partitionService;
    protected final MapServiceContext mapServiceContext;
    protected final FilteringStrategy filteringStrategy;
    protected final InternalSerializationService serializationService;
    protected final QueryCacheEventPublisher queryCacheEventPublisher;

    public MapEventPublisherImpl(MapServiceContext mapServiceContext) {
        this.mapServiceContext = mapServiceContext;
        this.nodeEngine = mapServiceContext.getNodeEngine();
        this.partitionService = this.nodeEngine.getPartitionService();
        this.serializationService = (InternalSerializationService)this.nodeEngine.getSerializationService();
        this.eventService = this.nodeEngine.getEventService();
        this.filteringStrategy = this.nodeEngine.getProperties().getBoolean(LISTENER_WITH_PREDICATE_PRODUCES_NATURAL_EVENT_TYPES) ? new QueryCacheNaturalFilteringStrategy(this.serializationService, mapServiceContext) : new DefaultEntryEventFilteringStrategy(this.serializationService, mapServiceContext);
        this.queryCacheEventPublisher = new QueryCacheEventPublisher(this.filteringStrategy, mapServiceContext.getQueryCacheContext(), this.serializationService);
    }

    @Override
    public void publishWanUpdate(String mapName, EntryView<Data, Data> entryView, boolean hasLoadProvenance) {
        if (!this.isOwnedPartition(entryView.getKey())) {
            return;
        }
        MapContainer mapContainer = this.mapServiceContext.getMapContainer(mapName);
        Object wanMergePolicy = mapContainer.getWanMergePolicy();
        MapReplicationUpdate event = new MapReplicationUpdate(mapName, wanMergePolicy, entryView);
        this.publishWanEvent(mapName, event);
    }

    @Override
    public void publishWanRemove(String mapName, Data key) {
        if (!this.isOwnedPartition(key)) {
            return;
        }
        MapReplicationRemove event = new MapReplicationRemove(mapName, key, Clock.currentTimeMillis());
        this.publishWanEvent(mapName, event);
    }

    protected void publishWanEvent(String mapName, ReplicationEventObject event) {
        MapContainer mapContainer = this.mapServiceContext.getMapContainer(mapName);
        WanReplicationPublisher wanReplicationPublisher = mapContainer.getWanReplicationPublisher();
        if (this.isOwnedPartition(event.getKey())) {
            wanReplicationPublisher.publishReplicationEvent("hz:impl:mapService", event);
        } else {
            wanReplicationPublisher.publishReplicationEventBackup("hz:impl:mapService", event);
        }
    }

    private boolean isOwnedPartition(Data dataKey) {
        int partitionId = this.partitionService.getPartitionId(dataKey);
        return this.partitionService.getPartition(partitionId, false).isLocal();
    }

    @Override
    public void publishMapEvent(Address caller, String mapName, EntryEventType eventType, int numberOfEntriesAffected) {
        Collection<EventRegistration> mapsListenerRegistrations = this.getRegistrations(mapName);
        if (CollectionUtil.isEmpty(mapsListenerRegistrations)) {
            return;
        }
        ArrayList<EventRegistration> registrations = null;
        for (EventRegistration registration : mapsListenerRegistrations) {
            EventFilter filter = registration.getFilter();
            if (filter instanceof EventListenerFilter && !filter.eval(eventType.getType()) || filter instanceof MapPartitionLostEventFilter) continue;
            if (registrations == null) {
                registrations = new ArrayList<EventRegistration>();
            }
            registrations.add(registration);
        }
        if (CollectionUtil.isEmpty(registrations)) {
            return;
        }
        String source = this.getThisNodesAddress();
        MapEventData mapEventData = new MapEventData(source, mapName, caller, eventType.getType(), numberOfEntriesAffected);
        this.publishEventInternal(registrations, mapEventData, mapName.hashCode());
    }

    @Override
    public void publishEvent(Address caller, String mapName, EntryEventType eventType, Data dataKey, Object oldValue, Object dataValue) {
        this.publishEvent(caller, mapName, eventType, dataKey, oldValue, dataValue, null);
    }

    @Override
    public void publishEvent(Address caller, String mapName, EntryEventType eventType, Data dataKey, Object oldValue, Object value, Object mergingValue) {
        Collection<EventRegistration> registrations = this.getRegistrations(mapName);
        if (CollectionUtil.isEmpty(registrations)) {
            return;
        }
        this.publishEvent(registrations, caller, mapName, eventType, dataKey, oldValue, value, mergingValue);
    }

    private void publishEvent(Collection<EventRegistration> registrations, Address caller, String mapName, EntryEventType eventType, Data dataKey, Object oldValue, Object newValue, Object mergingValue) {
        EntryEventDataCache eventDataCache = this.filteringStrategy.getEntryEventDataCache();
        int orderKey = this.pickOrderKey(dataKey);
        for (EventRegistration registration : registrations) {
            EventFilter filter = registration.getFilter();
            int eventTypeForPublishing = this.filteringStrategy.doFilter(filter, dataKey, oldValue, newValue, eventType, mapName);
            if (eventTypeForPublishing == -1) continue;
            EntryEventData eventDataToBePublished = eventDataCache.getOrCreateEventData(mapName, caller, dataKey, newValue, oldValue, mergingValue, eventTypeForPublishing, MapEventPublisherImpl.isIncludeValue(filter));
            this.eventService.publishEvent("hz:impl:mapService", registration, (Object)eventDataToBePublished, orderKey);
        }
        if (!eventDataCache.isEmpty()) {
            this.postPublishEvent(eventDataCache.eventDataIncludingValues(), eventDataCache.eventDataExcludingValues());
        }
    }

    protected void postPublishEvent(Collection<EntryEventData> eventDataIncludingValues, Collection<EntryEventData> eventDataExcludingValues) {
        if (eventDataIncludingValues != null) {
            for (EntryEventData entryEventData : eventDataIncludingValues) {
                this.queryCacheEventPublisher.addEventToQueryCache(entryEventData);
            }
        }
    }

    static boolean isIncludeValue(EventFilter filter) {
        if (filter instanceof EventListenerFilter) {
            filter = ((EventListenerFilter)filter).getEventFilter();
        }
        if (filter instanceof TrueEventFilter) {
            return true;
        }
        if (filter instanceof QueryEventFilter) {
            return ((QueryEventFilter)filter).isIncludeValue();
        }
        if (filter instanceof EntryEventFilter) {
            return ((EntryEventFilter)filter).isIncludeValue();
        }
        throw new IllegalArgumentException("Unknown EventFilter type = [" + filter.getClass().getCanonicalName() + "]");
    }

    @Override
    public void publishMapPartitionLostEvent(Address caller, String mapName, int partitionId) {
        LinkedList<EventRegistration> registrations = new LinkedList<EventRegistration>();
        for (EventRegistration registration : this.getRegistrations(mapName)) {
            if (!(registration.getFilter() instanceof MapPartitionLostEventFilter)) continue;
            registrations.add(registration);
        }
        if (registrations.isEmpty()) {
            return;
        }
        String thisNodesAddress = this.getThisNodesAddress();
        MapPartitionEventData eventData = new MapPartitionEventData(thisNodesAddress, mapName, caller, partitionId);
        this.publishEventInternal(registrations, eventData, partitionId);
    }

    @Override
    public void hintMapEvent(Address caller, String mapName, EntryEventType eventType, int numberOfEntriesAffected, int partitionId) {
        this.queryCacheEventPublisher.hintMapEvent(caller, mapName, eventType, numberOfEntriesAffected, partitionId);
    }

    @Override
    public void addEventToQueryCache(Object eventData) {
        this.queryCacheEventPublisher.addEventToQueryCache(eventData);
    }

    @Override
    public boolean hasEventListener(String mapName) {
        return this.eventService.hasEventRegistration("hz:impl:mapService", mapName);
    }

    protected Collection<EventRegistration> getRegistrations(String mapName) {
        return this.eventService.getRegistrations("hz:impl:mapService", mapName);
    }

    private int pickOrderKey(Data key) {
        return key == null ? -1 : key.hashCode();
    }

    protected void publishEventInternal(Collection<EventRegistration> registrations, Object eventData, int orderKey) {
        this.eventService.publishEvent("hz:impl:mapService", registrations, eventData, orderKey);
        this.queryCacheEventPublisher.addEventToQueryCache(eventData);
    }

    private String getThisNodesAddress() {
        Address thisAddress = this.nodeEngine.getThisAddress();
        return thisAddress.toString();
    }
}

