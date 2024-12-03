/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.IMapEvent;
import com.hazelcast.core.MapEvent;
import com.hazelcast.core.Member;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.internal.cluster.ClusterService;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import com.hazelcast.map.impl.DataAwareEntryEvent;
import com.hazelcast.map.impl.event.EntryEventData;
import com.hazelcast.map.impl.event.EventData;
import com.hazelcast.map.impl.event.MapEventData;
import com.hazelcast.multimap.impl.MultiMapService;

class MultiMapEventsDispatcher {
    private final ILogger logger = Logger.getLogger(MultiMapEventsDispatcher.class);
    private final ClusterService clusterService;
    private final MultiMapService multiMapService;

    MultiMapEventsDispatcher(MultiMapService multiMapService, ClusterService clusterService) {
        this.multiMapService = multiMapService;
        this.clusterService = clusterService;
    }

    private void incrementEventStats(IMapEvent event) {
        this.multiMapService.getLocalMultiMapStatsImpl(event.getName()).incrementReceivedEvents();
    }

    public void dispatchEvent(EventData eventData, EntryListener listener) {
        if (eventData instanceof EntryEventData) {
            this.dispatchEntryEventData(eventData, listener);
        } else if (eventData instanceof MapEventData) {
            this.dispatchMapEventData(eventData, listener);
        } else {
            throw new IllegalArgumentException("Unknown multimap event data");
        }
    }

    private void dispatchMapEventData(EventData eventData, EntryListener listener) {
        MapEventData mapEventData = (MapEventData)eventData;
        Member member = this.getMemberOrNull(eventData);
        if (member == null) {
            return;
        }
        MapEvent event = this.createMapEvent(mapEventData, member);
        this.dispatch0(event, listener);
        this.incrementEventStats(event);
    }

    private MapEvent createMapEvent(MapEventData mapEventData, Member member) {
        return new MapEvent(mapEventData.getMapName(), member, mapEventData.getEventType(), mapEventData.getNumberOfEntries());
    }

    private void dispatchEntryEventData(EventData eventData, EntryListener listener) {
        EntryEventData entryEventData = (EntryEventData)eventData;
        Member member = this.getMemberOrNull(eventData);
        DataAwareEntryEvent event = this.createDataAwareEntryEvent(entryEventData, member);
        this.dispatch0(event, listener);
        this.incrementEventStats(event);
    }

    private Member getMemberOrNull(EventData eventData) {
        MemberImpl member = this.clusterService.getMember(eventData.getCaller());
        if (member == null && this.logger.isInfoEnabled()) {
            this.logger.info("Dropping event " + eventData + " from unknown address:" + eventData.getCaller());
        }
        return member;
    }

    private DataAwareEntryEvent createDataAwareEntryEvent(EntryEventData entryEventData, Member member) {
        return new DataAwareEntryEvent(member, entryEventData.getEventType(), entryEventData.getMapName(), entryEventData.getDataKey(), entryEventData.getDataNewValue(), entryEventData.getDataOldValue(), entryEventData.getDataMergingValue(), this.multiMapService.getSerializationService());
    }

    private void dispatch0(IMapEvent event, EntryListener listener) {
        switch (event.getEventType()) {
            case ADDED: {
                listener.entryAdded((EntryEvent)event);
                break;
            }
            case REMOVED: {
                listener.entryRemoved((EntryEvent)event);
                break;
            }
            case CLEAR_ALL: {
                listener.mapCleared((MapEvent)event);
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid event type: " + (Object)((Object)event.getEventType()));
            }
        }
    }
}

