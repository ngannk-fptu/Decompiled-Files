/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.replicatedmap.impl;

import com.hazelcast.config.Config;
import com.hazelcast.config.ReplicatedMapConfig;
import com.hazelcast.core.EntryEventType;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.MapEvent;
import com.hazelcast.core.Member;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.map.impl.DataAwareEntryEvent;
import com.hazelcast.map.impl.event.EntryEventData;
import com.hazelcast.map.impl.event.EventData;
import com.hazelcast.map.impl.event.MapEventData;
import com.hazelcast.monitor.impl.LocalReplicatedMapStatsImpl;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.impl.QueryEntry;
import com.hazelcast.replicatedmap.ReplicatedMapCantBeCreatedOnLiteMemberException;
import com.hazelcast.replicatedmap.impl.ReplicatedMapService;
import com.hazelcast.replicatedmap.impl.record.AbstractReplicatedRecordStore;
import com.hazelcast.replicatedmap.impl.record.ReplicatedQueryEventFilter;
import com.hazelcast.replicatedmap.impl.record.ReplicatedRecordStore;
import com.hazelcast.spi.EventFilter;
import com.hazelcast.spi.EventPublishingService;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.EventService;
import com.hazelcast.spi.NodeEngine;
import java.util.Collection;
import java.util.EventListener;
import java.util.HashMap;

public class ReplicatedMapEventPublishingService
implements EventPublishingService {
    private final HashMap<String, Boolean> statisticsMap = new HashMap();
    private final ReplicatedMapService replicatedMapService;
    private final NodeEngine nodeEngine;
    private final Config config;
    private final EventService eventService;

    public ReplicatedMapEventPublishingService(ReplicatedMapService replicatedMapService) {
        this.replicatedMapService = replicatedMapService;
        this.nodeEngine = replicatedMapService.getNodeEngine();
        this.config = this.nodeEngine.getConfig();
        this.eventService = this.nodeEngine.getEventService();
    }

    public void dispatchEvent(Object event, Object listener) {
        if (event instanceof EntryEventData) {
            int partitionId;
            ReplicatedRecordStore recordStore;
            EntryEventData entryEventData = (EntryEventData)event;
            Member member = this.getMember(entryEventData);
            DataAwareEntryEvent entryEvent = this.createDataAwareEntryEvent(entryEventData, member);
            EntryListener entryListener = (EntryListener)listener;
            switch (entryEvent.getEventType()) {
                case ADDED: {
                    entryListener.entryAdded(entryEvent);
                    break;
                }
                case EVICTED: {
                    entryListener.entryEvicted(entryEvent);
                    break;
                }
                case UPDATED: {
                    entryListener.entryUpdated(entryEvent);
                    break;
                }
                case REMOVED: {
                    entryListener.entryRemoved(entryEvent);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("event type " + (Object)((Object)entryEvent.getEventType()) + " not supported");
                }
            }
            String mapName = ((EntryEventData)event).getMapName();
            Boolean statisticsEnabled = this.statisticsMap.get(mapName);
            if (statisticsEnabled == null) {
                ReplicatedMapConfig mapConfig = this.config.findReplicatedMapConfig(mapName);
                statisticsEnabled = mapConfig.isStatisticsEnabled();
                this.statisticsMap.put(mapName, statisticsEnabled);
            }
            if (statisticsEnabled.booleanValue() && (recordStore = this.replicatedMapService.getPartitionContainer(partitionId = this.nodeEngine.getPartitionService().getPartitionId(entryEventData.getDataKey())).getRecordStore(mapName)) instanceof AbstractReplicatedRecordStore) {
                LocalReplicatedMapStatsImpl stats = ((AbstractReplicatedRecordStore)recordStore).getStats();
                stats.incrementReceivedEvents();
            }
        } else if (event instanceof MapEventData) {
            MapEventData mapEventData = (MapEventData)event;
            Member member = this.getMember(mapEventData);
            MapEvent mapEvent = new MapEvent(mapEventData.getMapName(), member, mapEventData.getEventType(), mapEventData.getNumberOfEntries());
            EntryListener entryListener = (EntryListener)listener;
            EntryEventType type = EntryEventType.getByType(mapEventData.getEventType());
            switch (type) {
                case CLEAR_ALL: {
                    entryListener.mapCleared(mapEvent);
                    break;
                }
                default: {
                    throw new IllegalArgumentException("Unsupported EntryEventType: " + (Object)((Object)type));
                }
            }
        }
    }

    public String addEventListener(EventListener entryListener, EventFilter eventFilter, String mapName) {
        if (this.nodeEngine.getLocalMember().isLiteMember()) {
            throw new ReplicatedMapCantBeCreatedOnLiteMemberException(this.nodeEngine.getThisAddress());
        }
        EventRegistration registration = this.eventService.registerLocalListener("hz:impl:replicatedMapService", mapName, eventFilter, entryListener);
        return registration.getId();
    }

    public boolean removeEventListener(String mapName, String registrationId) {
        if (this.nodeEngine.getLocalMember().isLiteMember()) {
            throw new ReplicatedMapCantBeCreatedOnLiteMemberException(this.nodeEngine.getThisAddress());
        }
        if (registrationId == null) {
            throw new IllegalArgumentException("registrationId cannot be null");
        }
        return this.eventService.deregisterListener("hz:impl:replicatedMapService", mapName, registrationId);
    }

    public void fireMapClearedEvent(int deletedEntrySize, String name) {
        EventService eventService = this.nodeEngine.getEventService();
        Collection<EventRegistration> registrations = eventService.getRegistrations("hz:impl:replicatedMapService", name);
        if (registrations.isEmpty()) {
            return;
        }
        MapEventData mapEventData = new MapEventData(name, name, this.nodeEngine.getThisAddress(), EntryEventType.CLEAR_ALL.getType(), deletedEntrySize);
        eventService.publishEvent("hz:impl:replicatedMapService", registrations, (Object)mapEventData, name.hashCode());
    }

    private Member getMember(EventData eventData) {
        MemberImpl member = this.replicatedMapService.getNodeEngine().getClusterService().getMember(eventData.getCaller());
        if (member == null) {
            member = new MemberImpl.Builder(eventData.getCaller()).version(this.nodeEngine.getVersion()).build();
        }
        return member;
    }

    private DataAwareEntryEvent createDataAwareEntryEvent(EntryEventData entryEventData, Member member) {
        return new DataAwareEntryEvent(member, entryEventData.getEventType(), entryEventData.getMapName(), entryEventData.getDataKey(), entryEventData.getDataNewValue(), entryEventData.getDataOldValue(), entryEventData.getDataMergingValue(), this.nodeEngine.getSerializationService());
    }

    public void fireEntryListenerEvent(Data key, Data oldValue, Data value, String name, Address caller) {
        EntryEventType eventType = value == null ? EntryEventType.REMOVED : (oldValue == null ? EntryEventType.ADDED : EntryEventType.UPDATED);
        this.fireEntryListenerEvent(key, oldValue, value, eventType, name, caller);
    }

    public void fireEntryListenerEvent(Data key, Data oldValue, Data value, EntryEventType eventType, String name, Address caller) {
        Collection<EventRegistration> registrations = this.eventService.getRegistrations("hz:impl:replicatedMapService", name);
        if (registrations.isEmpty()) {
            return;
        }
        EntryEventData eventData = new EntryEventData(name, name, caller, key, value, oldValue, eventType.getType());
        for (EventRegistration registration : registrations) {
            if (!this.shouldPublish(key, oldValue, value, eventType, registration.getFilter())) continue;
            this.eventService.publishEvent("hz:impl:replicatedMapService", registration, (Object)eventData, key.hashCode());
        }
    }

    private boolean shouldPublish(Data key, Data oldValue, Data value, EntryEventType eventType, EventFilter filter) {
        Object queryEntry = null;
        if (filter instanceof ReplicatedQueryEventFilter) {
            Data testValue = eventType == EntryEventType.REMOVED ? oldValue : value;
            InternalSerializationService serializationService = (InternalSerializationService)this.nodeEngine.getSerializationService();
            queryEntry = new QueryEntry(serializationService, key, testValue, null);
        }
        return filter == null || filter.eval(queryEntry != null ? queryEntry : key);
    }
}

