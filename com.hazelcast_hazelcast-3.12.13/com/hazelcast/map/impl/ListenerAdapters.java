/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.core.EntryListener;
import com.hazelcast.map.impl.EntryListenerAdaptors;
import com.hazelcast.map.impl.ListenerAdapter;
import com.hazelcast.map.impl.MapListenerAdaptors;
import com.hazelcast.map.listener.MapListener;

public final class ListenerAdapters {
    private ListenerAdapters() {
    }

    public static <T> ListenerAdapter<T> createListenerAdapter(Object listener) {
        if (listener instanceof ListenerAdapter) {
            return (ListenerAdapter)listener;
        }
        if (listener instanceof MapListener) {
            return MapListenerAdaptors.createMapListenerAdaptor((MapListener)listener);
        }
        if (listener instanceof EntryListener) {
            return EntryListenerAdaptors.createEntryListenerAdaptor((EntryListener)listener);
        }
        throw new IllegalArgumentException("Not a valid type to create a listener: " + listener.getClass().getSimpleName());
    }
}

