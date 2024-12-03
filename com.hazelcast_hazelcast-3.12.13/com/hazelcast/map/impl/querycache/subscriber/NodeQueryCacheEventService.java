/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber;

import com.hazelcast.core.IMapEvent;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.map.EventLostEvent;
import com.hazelcast.map.impl.EntryEventFilter;
import com.hazelcast.map.impl.ListenerAdapter;
import com.hazelcast.map.impl.MapServiceContext;
import com.hazelcast.map.impl.event.EventData;
import com.hazelcast.map.impl.querycache.ListenerRegistrationHelper;
import com.hazelcast.map.impl.querycache.QueryCacheEventService;
import com.hazelcast.map.impl.querycache.QueryCacheListenerAdapter;
import com.hazelcast.map.impl.querycache.event.LocalCacheWideEventData;
import com.hazelcast.map.impl.querycache.event.LocalEntryEventData;
import com.hazelcast.map.impl.querycache.subscriber.QueryCacheEventListenerAdapters;
import com.hazelcast.map.listener.MapListener;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.query.impl.QueryEntry;
import com.hazelcast.query.impl.getters.Extractors;
import com.hazelcast.spi.EventFilter;
import com.hazelcast.spi.EventRegistration;
import com.hazelcast.spi.EventService;
import com.hazelcast.spi.NodeEngine;
import com.hazelcast.spi.impl.eventservice.impl.Registration;
import com.hazelcast.spi.impl.eventservice.impl.TrueEventFilter;
import com.hazelcast.spi.serialization.SerializationService;
import com.hazelcast.util.ContextMutexFactory;
import com.hazelcast.util.Preconditions;
import java.util.Collection;

public class NodeQueryCacheEventService
implements QueryCacheEventService<EventData> {
    private final EventService eventService;
    private final ContextMutexFactory mutexFactory;
    private final MapServiceContext mapServiceContext;

    public NodeQueryCacheEventService(MapServiceContext mapServiceContext, ContextMutexFactory mutexFactory) {
        this.mapServiceContext = mapServiceContext;
        NodeEngine nodeEngine = mapServiceContext.getNodeEngine();
        this.eventService = nodeEngine.getEventService();
        this.mutexFactory = mutexFactory;
    }

    @Override
    public void publish(String mapName, String cacheId, EventData eventData, int orderKey, Extractors extractors) {
        Preconditions.checkHasText(mapName, "mapName");
        Preconditions.checkHasText(cacheId, "cacheId");
        Preconditions.checkNotNull(eventData, "eventData cannot be null");
        this.publishLocalEvent(mapName, cacheId, eventData, extractors);
    }

    @Override
    public String addListener(String mapName, String cacheId, MapListener listener) {
        return this.addListener(mapName, cacheId, listener, null);
    }

    @Override
    public String addPublisherListener(String mapName, String cacheId, ListenerAdapter listenerAdapter) {
        String listenerName = ListenerRegistrationHelper.generateListenerName(mapName, cacheId);
        return this.mapServiceContext.addListenerAdapter(listenerAdapter, TrueEventFilter.INSTANCE, listenerName);
    }

    @Override
    public boolean removePublisherListener(String mapName, String cacheId, String listenerId) {
        String listenerName = ListenerRegistrationHelper.generateListenerName(mapName, cacheId);
        return this.mapServiceContext.removeEventListener(listenerName, listenerId);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String addListener(String mapName, String cacheId, MapListener listener, EventFilter filter) {
        Preconditions.checkHasText(mapName, "mapName");
        Preconditions.checkHasText(cacheId, "cacheId");
        Preconditions.checkNotNull(listener, "listener cannot be null");
        ListenerAdapter queryCacheListenerAdaptor = QueryCacheEventListenerAdapters.createQueryCacheListenerAdaptor(listener);
        SimpleQueryCacheListenerAdapter listenerAdaptor = new SimpleQueryCacheListenerAdapter(queryCacheListenerAdaptor);
        String listenerName = ListenerRegistrationHelper.generateListenerName(mapName, cacheId);
        ContextMutexFactory.Mutex mutex = this.mutexFactory.mutexFor(mapName);
        try {
            ContextMutexFactory.Mutex mutex2 = mutex;
            synchronized (mutex2) {
                EventRegistration registration = this.eventService.registerLocalListener("hz:impl:mapService", listenerName, filter == null ? TrueEventFilter.INSTANCE : filter, listenerAdaptor);
                String string = registration.getId();
                return string;
            }
        }
        finally {
            IOUtil.closeResource(mutex);
        }
    }

    @Override
    public boolean removeListener(String mapName, String cacheId, String listenerId) {
        String listenerName = ListenerRegistrationHelper.generateListenerName(mapName, cacheId);
        return this.eventService.deregisterListener("hz:impl:mapService", listenerName, listenerId);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeAllListeners(String mapName, String cacheId) {
        String listenerName = ListenerRegistrationHelper.generateListenerName(mapName, cacheId);
        ContextMutexFactory.Mutex mutex = this.mutexFactory.mutexFor(mapName);
        try {
            ContextMutexFactory.Mutex mutex2 = mutex;
            synchronized (mutex2) {
                this.eventService.deregisterAllListeners("hz:impl:mapService", listenerName);
            }
        }
        finally {
            IOUtil.closeResource(mutex);
        }
    }

    @Override
    public boolean hasListener(String mapName, String cacheId) {
        String listenerName = ListenerRegistrationHelper.generateListenerName(mapName, cacheId);
        Collection<EventRegistration> eventRegistrations = this.getRegistrations(listenerName);
        if (eventRegistrations.isEmpty()) {
            return false;
        }
        for (EventRegistration eventRegistration : eventRegistrations) {
            Registration registration = (Registration)eventRegistration;
            Object listener = registration.getListener();
            if (!(listener instanceof QueryCacheListenerAdapter)) continue;
            return true;
        }
        return false;
    }

    private void publishLocalEvent(String mapName, String cacheId, Object eventData, Extractors extractors) {
        String listenerName = ListenerRegistrationHelper.generateListenerName(mapName, cacheId);
        Collection<EventRegistration> eventRegistrations = this.getRegistrations(listenerName);
        if (eventRegistrations.isEmpty()) {
            return;
        }
        for (EventRegistration eventRegistration : eventRegistrations) {
            LocalEntryEventData localEntryEventData;
            Registration registration = (Registration)eventRegistration;
            Object listener = registration.getListener();
            if (!(listener instanceof QueryCacheListenerAdapter)) continue;
            LocalEntryEventData eventDataToPublish = eventData;
            int orderKey = -1;
            if (eventDataToPublish instanceof LocalCacheWideEventData) {
                orderKey = listenerName.hashCode();
            } else if (eventDataToPublish instanceof LocalEntryEventData && (localEntryEventData = (LocalEntryEventData)eventDataToPublish).getEventType() != EventLostEvent.EVENT_TYPE) {
                EventFilter filter = registration.getFilter();
                if (!this.canPassFilter(localEntryEventData, filter, extractors)) continue;
                boolean includeValue = this.isIncludeValue(filter);
                eventDataToPublish = includeValue ? localEntryEventData : localEntryEventData.cloneWithoutValue();
                Data keyData = localEntryEventData.getKeyData();
                orderKey = keyData == null ? -1 : keyData.hashCode();
            }
            this.publishEventInternal(registration, eventDataToPublish, orderKey);
        }
    }

    private boolean canPassFilter(LocalEntryEventData localEntryEventData, EventFilter filter, Extractors extractors) {
        if (filter == null || filter instanceof TrueEventFilter) {
            return true;
        }
        NodeEngine nodeEngine = this.mapServiceContext.getNodeEngine();
        SerializationService serializationService = nodeEngine.getSerializationService();
        Data keyData = localEntryEventData.getKeyData();
        Object value = this.getValueOrOldValue(localEntryEventData);
        QueryEntry entry = new QueryEntry((InternalSerializationService)serializationService, keyData, value, extractors);
        return filter.eval(entry);
    }

    private boolean isIncludeValue(EventFilter filter) {
        if (filter instanceof EntryEventFilter) {
            return ((EntryEventFilter)filter).isIncludeValue();
        }
        return true;
    }

    private Object getValueOrOldValue(LocalEntryEventData localEntryEventData) {
        Object value = localEntryEventData.getValue();
        return value != null ? value : localEntryEventData.getOldValue();
    }

    private Collection<EventRegistration> getRegistrations(String mapName) {
        return this.eventService.getRegistrations("hz:impl:mapService", mapName);
    }

    private void publishEventInternal(EventRegistration registration, Object eventData, int orderKey) {
        this.eventService.publishEvent("hz:impl:mapService", registration, eventData, orderKey);
    }

    @Override
    public void sendEventToSubscriber(String name, Object eventData, int orderKey) {
        Collection<EventRegistration> eventRegistrations = this.getRegistrations(name);
        if (eventRegistrations.isEmpty()) {
            return;
        }
        for (EventRegistration eventRegistration : eventRegistrations) {
            Registration registration = (Registration)eventRegistration;
            Object listener = registration.getListener();
            if (listener instanceof QueryCacheListenerAdapter) continue;
            this.publishEventInternal(registration, eventData, orderKey);
        }
    }

    private static class SimpleQueryCacheListenerAdapter
    implements QueryCacheListenerAdapter<IMapEvent> {
        private final ListenerAdapter listenerAdapter;

        SimpleQueryCacheListenerAdapter(ListenerAdapter listenerAdapter) {
            this.listenerAdapter = listenerAdapter;
        }

        @Override
        public void onEvent(IMapEvent event) {
            this.listenerAdapter.onEvent(event);
        }
    }
}

