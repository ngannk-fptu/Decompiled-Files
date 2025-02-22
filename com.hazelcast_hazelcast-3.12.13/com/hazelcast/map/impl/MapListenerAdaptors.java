/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.core.EntryEvent;
import com.hazelcast.core.EntryEventType;
import com.hazelcast.core.IMapEvent;
import com.hazelcast.core.MapEvent;
import com.hazelcast.internal.nearcache.impl.invalidation.Invalidation;
import com.hazelcast.map.impl.InternalMapListenerAdapter;
import com.hazelcast.map.impl.ListenerAdapter;
import com.hazelcast.map.impl.nearcache.invalidation.InvalidationListener;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryEvictedListener;
import com.hazelcast.map.listener.EntryExpiredListener;
import com.hazelcast.map.listener.EntryLoadedListener;
import com.hazelcast.map.listener.EntryMergedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;
import com.hazelcast.map.listener.MapClearedListener;
import com.hazelcast.map.listener.MapEvictedListener;
import com.hazelcast.map.listener.MapListener;
import com.hazelcast.util.ConstructorFunction;
import java.util.EnumMap;
import java.util.Map;

public final class MapListenerAdaptors {
    private static final Map<EntryEventType, ConstructorFunction<MapListener, ListenerAdapter>> CONSTRUCTORS = new EnumMap<EntryEventType, ConstructorFunction<MapListener, ListenerAdapter>>(EntryEventType.class);
    private static final ConstructorFunction<MapListener, ListenerAdapter> ENTRY_ADDED_LISTENER_ADAPTER_CONSTRUCTOR = new ConstructorFunction<MapListener, ListenerAdapter>(){

        @Override
        public ListenerAdapter createNew(MapListener mapListener) {
            if (!(mapListener instanceof EntryAddedListener)) {
                return null;
            }
            final EntryAddedListener listener = (EntryAddedListener)mapListener;
            return new ListenerAdapter<IMapEvent>(){

                @Override
                public void onEvent(IMapEvent event) {
                    listener.entryAdded((EntryEvent)event);
                }
            };
        }
    };
    private static final ConstructorFunction<MapListener, ListenerAdapter> ENTRY_REMOVED_LISTENER_ADAPTER_CONSTRUCTOR = new ConstructorFunction<MapListener, ListenerAdapter>(){

        @Override
        public ListenerAdapter createNew(MapListener mapListener) {
            if (!(mapListener instanceof EntryRemovedListener)) {
                return null;
            }
            final EntryRemovedListener listener = (EntryRemovedListener)mapListener;
            return new ListenerAdapter<IMapEvent>(){

                @Override
                public void onEvent(IMapEvent event) {
                    listener.entryRemoved((EntryEvent)event);
                }
            };
        }
    };
    private static final ConstructorFunction<MapListener, ListenerAdapter> ENTRY_EVICTED_LISTENER_ADAPTER_CONSTRUCTOR = new ConstructorFunction<MapListener, ListenerAdapter>(){

        @Override
        public ListenerAdapter createNew(MapListener mapListener) {
            if (!(mapListener instanceof EntryEvictedListener)) {
                return null;
            }
            final EntryEvictedListener listener = (EntryEvictedListener)mapListener;
            return new ListenerAdapter<IMapEvent>(){

                @Override
                public void onEvent(IMapEvent event) {
                    listener.entryEvicted((EntryEvent)event);
                }
            };
        }
    };
    private static final ConstructorFunction<MapListener, ListenerAdapter> ENTRY_UPDATED_LISTENER_ADAPTER_CONSTRUCTOR = new ConstructorFunction<MapListener, ListenerAdapter>(){

        @Override
        public ListenerAdapter createNew(MapListener mapListener) {
            if (!(mapListener instanceof EntryUpdatedListener)) {
                return null;
            }
            final EntryUpdatedListener listener = (EntryUpdatedListener)mapListener;
            return new ListenerAdapter<IMapEvent>(){

                @Override
                public void onEvent(IMapEvent event) {
                    listener.entryUpdated((EntryEvent)event);
                }
            };
        }
    };
    private static final ConstructorFunction<MapListener, ListenerAdapter> MAP_EVICTED_LISTENER_ADAPTER_CONSTRUCTOR = new ConstructorFunction<MapListener, ListenerAdapter>(){

        @Override
        public ListenerAdapter createNew(MapListener mapListener) {
            if (!(mapListener instanceof MapEvictedListener)) {
                return null;
            }
            final MapEvictedListener listener = (MapEvictedListener)mapListener;
            return new ListenerAdapter<IMapEvent>(){

                @Override
                public void onEvent(IMapEvent event) {
                    listener.mapEvicted((MapEvent)event);
                }
            };
        }
    };
    private static final ConstructorFunction<MapListener, ListenerAdapter> MAP_CLEARED_LISTENER_ADAPTER_CONSTRUCTOR = new ConstructorFunction<MapListener, ListenerAdapter>(){

        @Override
        public ListenerAdapter createNew(MapListener mapListener) {
            if (!(mapListener instanceof MapClearedListener)) {
                return null;
            }
            final MapClearedListener listener = (MapClearedListener)mapListener;
            return new ListenerAdapter<IMapEvent>(){

                @Override
                public void onEvent(IMapEvent event) {
                    listener.mapCleared((MapEvent)event);
                }
            };
        }
    };
    private static final ConstructorFunction<MapListener, ListenerAdapter> ENTRY_MERGED_LISTENER_ADAPTER_CONSTRUCTOR = new ConstructorFunction<MapListener, ListenerAdapter>(){

        @Override
        public ListenerAdapter createNew(MapListener mapListener) {
            if (!(mapListener instanceof EntryMergedListener)) {
                return null;
            }
            final EntryMergedListener listener = (EntryMergedListener)mapListener;
            return new ListenerAdapter<IMapEvent>(){

                @Override
                public void onEvent(IMapEvent event) {
                    listener.entryMerged((EntryEvent)event);
                }
            };
        }
    };
    private static final ConstructorFunction<MapListener, ListenerAdapter> INVALIDATION_LISTENER = new ConstructorFunction<MapListener, ListenerAdapter>(){

        @Override
        public ListenerAdapter createNew(MapListener mapListener) {
            if (!(mapListener instanceof InvalidationListener)) {
                return null;
            }
            final InvalidationListener listener = (InvalidationListener)mapListener;
            return new ListenerAdapter<Invalidation>(){

                @Override
                public void onEvent(Invalidation event) {
                    listener.onInvalidate(event);
                }
            };
        }
    };
    private static final ConstructorFunction<MapListener, ListenerAdapter> ENTRY_EXPIRED_LISTENER_ADAPTER_CONSTRUCTOR = new ConstructorFunction<MapListener, ListenerAdapter>(){

        @Override
        public ListenerAdapter createNew(MapListener mapListener) {
            if (!(mapListener instanceof EntryExpiredListener)) {
                return null;
            }
            final EntryExpiredListener listener = (EntryExpiredListener)mapListener;
            return new ListenerAdapter<IMapEvent>(){

                @Override
                public void onEvent(IMapEvent event) {
                    listener.entryExpired((EntryEvent)event);
                }
            };
        }
    };
    private static final ConstructorFunction<MapListener, ListenerAdapter> ENTRY_LOADED_LISTENER_ADAPTER_CONSTRUCTOR = new ConstructorFunction<MapListener, ListenerAdapter>(){

        @Override
        public ListenerAdapter createNew(MapListener mapListener) {
            if (!(mapListener instanceof EntryLoadedListener)) {
                return null;
            }
            final EntryLoadedListener listener = (EntryLoadedListener)mapListener;
            return new ListenerAdapter<IMapEvent>(){

                @Override
                public void onEvent(IMapEvent event) {
                    listener.entryLoaded((EntryEvent)event);
                }
            };
        }
    };

    private MapListenerAdaptors() {
    }

    public static ListenerAdapter[] createListenerAdapters(MapListener mapListener) {
        EntryEventType[] values = EntryEventType.values();
        ListenerAdapter[] listenerAdapters = new ListenerAdapter[values.length];
        for (EntryEventType eventType : values) {
            listenerAdapters[eventType.ordinal()] = MapListenerAdaptors.createListenerAdapter(eventType, mapListener);
        }
        return listenerAdapters;
    }

    private static ListenerAdapter createListenerAdapter(EntryEventType eventType, MapListener mapListener) {
        ConstructorFunction<MapListener, ListenerAdapter> constructorFunction = CONSTRUCTORS.get((Object)eventType);
        if (constructorFunction == null) {
            throw new IllegalArgumentException("First, define a ListenerAdapter for the event EntryEventType." + (Object)((Object)eventType));
        }
        return constructorFunction.createNew(mapListener);
    }

    static ListenerAdapter createMapListenerAdaptor(MapListener mapListener) {
        return new InternalMapListenerAdapter(mapListener);
    }

    public static Map<EntryEventType, ConstructorFunction<MapListener, ListenerAdapter>> getConstructors() {
        return CONSTRUCTORS;
    }

    static {
        CONSTRUCTORS.put(EntryEventType.ADDED, ENTRY_ADDED_LISTENER_ADAPTER_CONSTRUCTOR);
        CONSTRUCTORS.put(EntryEventType.LOADED, ENTRY_LOADED_LISTENER_ADAPTER_CONSTRUCTOR);
        CONSTRUCTORS.put(EntryEventType.REMOVED, ENTRY_REMOVED_LISTENER_ADAPTER_CONSTRUCTOR);
        CONSTRUCTORS.put(EntryEventType.EVICTED, ENTRY_EVICTED_LISTENER_ADAPTER_CONSTRUCTOR);
        CONSTRUCTORS.put(EntryEventType.UPDATED, ENTRY_UPDATED_LISTENER_ADAPTER_CONSTRUCTOR);
        CONSTRUCTORS.put(EntryEventType.MERGED, ENTRY_MERGED_LISTENER_ADAPTER_CONSTRUCTOR);
        CONSTRUCTORS.put(EntryEventType.EXPIRED, ENTRY_EXPIRED_LISTENER_ADAPTER_CONSTRUCTOR);
        CONSTRUCTORS.put(EntryEventType.EVICT_ALL, MAP_EVICTED_LISTENER_ADAPTER_CONSTRUCTOR);
        CONSTRUCTORS.put(EntryEventType.CLEAR_ALL, MAP_CLEARED_LISTENER_ADAPTER_CONSTRUCTOR);
        CONSTRUCTORS.put(EntryEventType.INVALIDATION, INVALIDATION_LISTENER);
    }
}

