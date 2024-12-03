/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl.querycache.subscriber;

import com.hazelcast.core.IMapEvent;
import com.hazelcast.map.EventLostEvent;
import com.hazelcast.map.impl.ListenerAdapter;
import com.hazelcast.map.impl.MapListenerAdaptors;
import com.hazelcast.map.impl.querycache.subscriber.InternalQueryCacheListenerAdapter;
import com.hazelcast.map.listener.EventLostListener;
import com.hazelcast.map.listener.MapListener;
import com.hazelcast.util.ConstructorFunction;

public final class QueryCacheEventListenerAdapters {
    private static final ConstructorFunction<MapListener, ListenerAdapter> EVENT_LOST_LISTENER_ADAPTER = new ConstructorFunction<MapListener, ListenerAdapter>(){

        @Override
        public ListenerAdapter createNew(MapListener mapListener) {
            if (!(mapListener instanceof EventLostListener)) {
                return null;
            }
            final EventLostListener listener = (EventLostListener)mapListener;
            return new ListenerAdapter<IMapEvent>(){

                @Override
                public void onEvent(IMapEvent iMapEvent) {
                    listener.eventLost((EventLostEvent)iMapEvent);
                }
            };
        }
    };

    private QueryCacheEventListenerAdapters() {
    }

    static ListenerAdapter[] createQueryCacheListenerAdapters(MapListener mapListener) {
        ListenerAdapter[] mapListenerAdapters = MapListenerAdaptors.createListenerAdapters(mapListener);
        ListenerAdapter eventLostAdapter = EVENT_LOST_LISTENER_ADAPTER.createNew(mapListener);
        ListenerAdapter[] adapters = new ListenerAdapter[mapListenerAdapters.length + 1];
        System.arraycopy(mapListenerAdapters, 0, adapters, 0, mapListenerAdapters.length);
        adapters[mapListenerAdapters.length] = eventLostAdapter;
        return adapters;
    }

    public static ListenerAdapter createQueryCacheListenerAdaptor(MapListener mapListener) {
        return new InternalQueryCacheListenerAdapter(mapListener);
    }
}

