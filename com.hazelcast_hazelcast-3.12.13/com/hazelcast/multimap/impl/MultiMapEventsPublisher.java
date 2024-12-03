/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.multimap.impl;

import com.hazelcast.core.EntryEventType;
import com.hazelcast.map.impl.event.EntryEventData;
import com.hazelcast.map.impl.event.MapEventData;
import com.hazelcast.multimap.impl.MultiMapEventFilter;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.EventService;
import com.hazelcast.spi.NodeEngine;
import java.util.Collection;

public class MultiMapEventsPublisher {
    private final NodeEngine nodeEngine;

    public MultiMapEventsPublisher(NodeEngine nodeEngine) {
        this.nodeEngine = nodeEngine;
    }

    public void publishMultiMapEvent(String mapName, EntryEventType eventType, int numberOfEntriesAffected) {
        EventService eventService = this.nodeEngine.getEventService();
        Collection<EventRegistration> registrations = eventService.getRegistrations("hz:impl:multiMapService", mapName);
        if (registrations.isEmpty()) {
            return;
        }
        Address caller = this.nodeEngine.getThisAddress();
        String source = caller.toString();
        MapEventData mapEventData = new MapEventData(source, mapName, caller, eventType.getType(), numberOfEntriesAffected);
        eventService.publishEvent("hz:impl:multiMapService", registrations, (Object)mapEventData, mapName.hashCode());
    }

    public final void publishEntryEvent(String multiMapName, EntryEventType eventType, Data key, Object newValue, Object oldValue) {
        EventService eventService = this.nodeEngine.getEventService();
        Collection<EventRegistration> registrations = eventService.getRegistrations("hz:impl:multiMapService", multiMapName);
        for (EventRegistration registration : registrations) {
            MultiMapEventFilter filter = (MultiMapEventFilter)registration.getFilter();
            if (filter.getKey() != null && !filter.getKey().equals(key)) continue;
            Data dataNewValue = filter.isIncludeValue() ? this.nodeEngine.toData(newValue) : null;
            Data dataOldValue = filter.isIncludeValue() ? this.nodeEngine.toData(oldValue) : null;
            Address caller = this.nodeEngine.getThisAddress();
            String source = caller.toString();
            EntryEventData event = new EntryEventData(source, multiMapName, caller, key, dataNewValue, dataOldValue, eventType.getType());
            eventService.publishEvent("hz:impl:multiMapService", registration, (Object)event, multiMapName.hashCode());
        }
    }
}

