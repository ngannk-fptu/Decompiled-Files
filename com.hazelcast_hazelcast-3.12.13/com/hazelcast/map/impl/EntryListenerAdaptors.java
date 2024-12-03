/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryEventType;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.IMapEvent;
import com.hazelcast.core.MapEvent;
import com.hazelcast.map.impl.InternalEntryListenerAdapter;
import com.hazelcast.map.impl.ListenerAdapter;
import com.hazelcast.util.ConstructorFunction;
import java.util.EnumMap;
import java.util.Map;

public final class EntryListenerAdaptors {
    private static final Map<EntryEventType, ConstructorFunction<EntryListener, ListenerAdapter>> CONSTRUCTORS = new EnumMap<EntryEventType, ConstructorFunction<EntryListener, ListenerAdapter>>(EntryEventType.class);
    private static final ConstructorFunction<EntryListener, ListenerAdapter> ENTRY_ADDED_LISTENER_ADAPTER_CONSTRUCTOR = new ConstructorFunction<EntryListener, ListenerAdapter>(){

        @Override
        public ListenerAdapter createNew(final EntryListener listener) {
            return new ListenerAdapter<IMapEvent>(){

                @Override
                public void onEvent(IMapEvent event) {
                    listener.entryAdded((EntryEvent)event);
                }
            };
        }
    };
    private static final ConstructorFunction<EntryListener, ListenerAdapter> ENTRY_REMOVED_LISTENER_ADAPTER_CONSTRUCTOR = new ConstructorFunction<EntryListener, ListenerAdapter>(){

        @Override
        public ListenerAdapter createNew(final EntryListener listener) {
            return new ListenerAdapter<IMapEvent>(){

                @Override
                public void onEvent(IMapEvent event) {
                    listener.entryRemoved((EntryEvent)event);
                }
            };
        }
    };
    private static final ConstructorFunction<EntryListener, ListenerAdapter> ENTRY_EVICTED_LISTENER_ADAPTER_CONSTRUCTOR = new ConstructorFunction<EntryListener, ListenerAdapter>(){

        @Override
        public ListenerAdapter createNew(final EntryListener listener) {
            return new ListenerAdapter<IMapEvent>(){

                @Override
                public void onEvent(IMapEvent event) {
                    listener.entryEvicted((EntryEvent)event);
                }
            };
        }
    };
    private static final ConstructorFunction<EntryListener, ListenerAdapter> ENTRY_UPDATED_LISTENER_ADAPTER_CONSTRUCTOR = new ConstructorFunction<EntryListener, ListenerAdapter>(){

        @Override
        public ListenerAdapter createNew(final EntryListener listener) {
            return new ListenerAdapter<IMapEvent>(){

                @Override
                public void onEvent(IMapEvent event) {
                    listener.entryUpdated((EntryEvent)event);
                }
            };
        }
    };
    private static final ConstructorFunction<EntryListener, ListenerAdapter> MAP_EVICTED_LISTENER_ADAPTER_CONSTRUCTOR = new ConstructorFunction<EntryListener, ListenerAdapter>(){

        @Override
        public ListenerAdapter createNew(final EntryListener listener) {
            return new ListenerAdapter<IMapEvent>(){

                @Override
                public void onEvent(IMapEvent event) {
                    listener.mapEvicted((MapEvent)event);
                }
            };
        }
    };
    private static final ConstructorFunction<EntryListener, ListenerAdapter> MAP_CLEARED_LISTENER_ADAPTER_CONSTRUCTOR = new ConstructorFunction<EntryListener, ListenerAdapter>(){

        @Override
        public ListenerAdapter createNew(final EntryListener listener) {
            return new ListenerAdapter<IMapEvent>(){

                @Override
                public void onEvent(IMapEvent event) {
                    listener.mapCleared((MapEvent)event);
                }
            };
        }
    };

    private EntryListenerAdaptors() {
    }

    public static ListenerAdapter[] createListenerAdapters(EntryListener listener) {
        EntryEventType[] values = new EntryEventType[]{EntryEventType.ADDED, EntryEventType.REMOVED, EntryEventType.EVICTED, EntryEventType.UPDATED, EntryEventType.EVICT_ALL, EntryEventType.CLEAR_ALL};
        ListenerAdapter[] listenerAdapters = new ListenerAdapter[values.length];
        for (EntryEventType eventType : values) {
            listenerAdapters[eventType.ordinal()] = EntryListenerAdaptors.createListenerAdapter(eventType, listener);
        }
        return listenerAdapters;
    }

    private static ListenerAdapter createListenerAdapter(EntryEventType eventType, EntryListener listener) {
        ConstructorFunction<EntryListener, ListenerAdapter> constructorFunction = CONSTRUCTORS.get((Object)eventType);
        if (constructorFunction == null) {
            throw new IllegalArgumentException("First, define a ListenerAdapter for the event EntryEventType." + (Object)((Object)eventType));
        }
        return constructorFunction.createNew(listener);
    }

    static ListenerAdapter createEntryListenerAdaptor(EntryListener listener) {
        return new InternalEntryListenerAdapter(listener);
    }

    static {
        CONSTRUCTORS.put(EntryEventType.ADDED, ENTRY_ADDED_LISTENER_ADAPTER_CONSTRUCTOR);
        CONSTRUCTORS.put(EntryEventType.REMOVED, ENTRY_REMOVED_LISTENER_ADAPTER_CONSTRUCTOR);
        CONSTRUCTORS.put(EntryEventType.EVICTED, ENTRY_EVICTED_LISTENER_ADAPTER_CONSTRUCTOR);
        CONSTRUCTORS.put(EntryEventType.UPDATED, ENTRY_UPDATED_LISTENER_ADAPTER_CONSTRUCTOR);
        CONSTRUCTORS.put(EntryEventType.EVICT_ALL, MAP_EVICTED_LISTENER_ADAPTER_CONSTRUCTOR);
        CONSTRUCTORS.put(EntryEventType.CLEAR_ALL, MAP_CLEARED_LISTENER_ADAPTER_CONSTRUCTOR);
    }
}

