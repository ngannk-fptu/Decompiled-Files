/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.event;

import com.hazelcast.core.IMapEvent;
import com.hazelcast.core.MapEvent;
import com.hazelcast.core.Member;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.internal.nearcache.impl.invalidation.Invalidation;
import com.hazelcast.map.MapPartitionLostEvent;
import com.hazelcast.map.impl.DataAwareEntryEvent;
import com.hazelcast.map.impl.ListenerAdapter;
import com.hazelcast.map.impl.MapContainer;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.event.EntryEventData;
import com.hazelcast.map.impl.event.EventData;
import com.hazelcast.map.impl.event.MapEventData;
import com.hazelcast.map.impl.event.MapPartitionEventData;
import com.hazelcast.map.impl.querycache.event.BatchEventData;
import com.hazelcast.map.impl.querycache.event.BatchIMapEvent;
import com.hazelcast.map.impl.querycache.event.LocalCacheWideEventData;
import com.hazelcast.map.impl.querycache.event.LocalEntryEventData;
import com.hazelcast.map.impl.querycache.event.QueryCacheEventData;
import com.hazelcast.map.impl.querycache.event.SingleIMapEvent;
import com.hazelcast.map.impl.querycache.subscriber.EventPublisherHelper;
import com.hazelcast.spi.EventPublishingService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.serialization.SerializationService;

public class MapEventPublishingService
implements EventPublishingService<Object, ListenerAdapter> {
    private final MapServiceContext mapServiceContext;
    private final NodeEngine nodeEngine;
    private final SerializationService serializationService;

    public MapEventPublishingService(MapServiceContext mapServiceContext) {
        this.mapServiceContext = mapServiceContext;
        this.nodeEngine = mapServiceContext.getNodeEngine();
        this.serializationService = mapServiceContext.getNodeEngine().getSerializationService();
    }

    @Override
    public void dispatchEvent(Object eventData, ListenerAdapter listener) {
        if (eventData instanceof QueryCacheEventData) {
            this.dispatchQueryCacheEventData((QueryCacheEventData)eventData, listener);
            return;
        }
        if (eventData instanceof BatchEventData) {
            this.dispatchBatchEventData((BatchEventData)eventData, listener);
            return;
        }
        if (eventData instanceof LocalEntryEventData) {
            this.dispatchLocalEventData((LocalEntryEventData)eventData, listener);
            return;
        }
        if (eventData instanceof LocalCacheWideEventData) {
            this.dispatchLocalEventData((LocalCacheWideEventData)eventData, listener);
            return;
        }
        if (eventData instanceof EntryEventData) {
            this.dispatchEntryEventData((EntryEventData)eventData, listener);
            return;
        }
        if (eventData instanceof MapEventData) {
            this.dispatchMapEventData((MapEventData)eventData, listener);
            return;
        }
        if (eventData instanceof MapPartitionEventData) {
            this.dispatchMapPartitionLostEventData((MapPartitionEventData)eventData, listener);
            return;
        }
        if (eventData instanceof Invalidation) {
            listener.onEvent(eventData);
            this.incrementEventStats((Invalidation)eventData);
            return;
        }
        throw new IllegalArgumentException("Unknown event data [" + eventData + ']');
    }

    private void incrementEventStats(Invalidation data) {
        String mapName = data.getName();
        this.incrementEventStatsInternal(mapName);
    }

    private void incrementEventStats(IMapEvent event) {
        String mapName = event.getName();
        this.incrementEventStatsInternal(mapName);
    }

    private void incrementEventStatsInternal(String mapName) {
        MapContainer mapContainer = this.mapServiceContext.getMapContainer(mapName);
        if (mapContainer.getMapConfig().isStatisticsEnabled()) {
            this.mapServiceContext.getLocalMapStatsProvider().getLocalMapStatsImpl(mapName).incrementReceivedEvents();
        }
    }

    private void dispatchMapEventData(MapEventData mapEventData, ListenerAdapter listener) {
        Member member = this.getMember(mapEventData);
        MapEvent event = this.createMapEvent(mapEventData, member);
        this.callListener(listener, event);
    }

    private void dispatchMapPartitionLostEventData(MapPartitionEventData eventData, ListenerAdapter listener) {
        Member member = this.getMember(eventData);
        MapPartitionLostEvent event = this.createMapPartitionLostEventData(eventData, member);
        this.callListener(listener, event);
    }

    private MapPartitionLostEvent createMapPartitionLostEventData(MapPartitionEventData eventData, Member member) {
        return new MapPartitionLostEvent(eventData.getMapName(), member, eventData.getEventType(), eventData.getPartitionId());
    }

    private void dispatchQueryCacheEventData(QueryCacheEventData eventData, ListenerAdapter listener) {
        SingleIMapEvent mapEvent = this.createSingleIMapEvent(eventData);
        listener.onEvent(mapEvent);
    }

    private SingleIMapEvent createSingleIMapEvent(QueryCacheEventData eventData) {
        return new SingleIMapEvent(eventData);
    }

    private void dispatchLocalEventData(EventData eventData, ListenerAdapter listener) {
        IMapEvent event = EventPublisherHelper.createIMapEvent(eventData, null, this.nodeEngine.getLocalMember(), this.serializationService);
        listener.onEvent(event);
    }

    private void dispatchBatchEventData(BatchEventData batchEventData, ListenerAdapter listener) {
        BatchIMapEvent mapEvent = this.createBatchEvent(batchEventData);
        listener.onEvent(mapEvent);
    }

    private BatchIMapEvent createBatchEvent(BatchEventData batchEventData) {
        return new BatchIMapEvent(batchEventData);
    }

    private void callListener(ListenerAdapter listener, IMapEvent event) {
        listener.onEvent(event);
        this.incrementEventStats(event);
    }

    private MapEvent createMapEvent(MapEventData mapEventData, Member member) {
        return new MapEvent(mapEventData.getMapName(), member, mapEventData.getEventType(), mapEventData.getNumberOfEntries());
    }

    private void dispatchEntryEventData(EntryEventData entryEventData, ListenerAdapter listener) {
        Member member = this.getMember(entryEventData);
        DataAwareEntryEvent event = this.createDataAwareEntryEvent(entryEventData, member);
        this.callListener(listener, event);
    }

    private Member getMember(EventData eventData) {
        MemberImpl member = this.nodeEngine.getClusterService().getMember(eventData.getCaller());
        if (member == null) {
            member = new MemberImpl.Builder(eventData.getCaller()).version(this.nodeEngine.getVersion()).build();
        }
        return member;
    }

    private DataAwareEntryEvent createDataAwareEntryEvent(EntryEventData entryEventData, Member member) {
        return new DataAwareEntryEvent(member, entryEventData.getEventType(), entryEventData.getMapName(), entryEventData.getDataKey(), entryEventData.getDataNewValue(), entryEventData.getDataOldValue(), entryEventData.getDataMergingValue(), this.nodeEngine.getSerializationService());
    }
}

