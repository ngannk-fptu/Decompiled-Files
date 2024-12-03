/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber;

import com.hazelcast.core.EntryEventType;
import com.hazelcast.core.IMapEvent;
import com.hazelcast.map.EventLostEvent;
import com.hazelcast.map.impl.ListenerAdapter;
import com.hazelcast.map.impl.querycache.subscriber.QueryCacheEventListenerAdapters;
import com.hazelcast.map.listener.MapListener;
import com.hazelcast.util.Preconditions;

class InternalQueryCacheListenerAdapter
implements ListenerAdapter<IMapEvent> {
    private final ListenerAdapter[] listenerAdapters;

    InternalQueryCacheListenerAdapter(MapListener mapListener) {
        Preconditions.checkNotNull(mapListener, "mapListener cannot be null");
        this.listenerAdapters = QueryCacheEventListenerAdapters.createQueryCacheListenerAdapters(mapListener);
    }

    @Override
    public void onEvent(IMapEvent event) {
        EntryEventType eventType = event.getEventType();
        if (eventType != null) {
            this.callListener(event, eventType.getType());
            return;
        }
        if (event instanceof EventLostEvent) {
            EventLostEvent eventLostEvent = (EventLostEvent)event;
            this.callListener(eventLostEvent, EventLostEvent.EVENT_TYPE);
            return;
        }
    }

    private void callListener(IMapEvent event, int eventType) {
        int adapterIndex = Integer.numberOfTrailingZeros(eventType);
        ListenerAdapter listenerAdapter = this.listenerAdapters[adapterIndex];
        if (listenerAdapter == null) {
            return;
        }
        listenerAdapter.onEvent(event);
    }
}

