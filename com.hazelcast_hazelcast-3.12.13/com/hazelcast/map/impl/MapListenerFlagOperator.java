/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.core.EntryEventType;
import com.hazelcast.map.impl.InternalMapListenerAdapter;
import com.hazelcast.map.impl.ListenerAdapter;

public final class MapListenerFlagOperator {
    public static final int SET_ALL_LISTENER_FLAGS = MapListenerFlagOperator.setAndGetAllListenerFlags();

    private MapListenerFlagOperator() {
    }

    public static int setAndGetListenerFlags(ListenerAdapter listenerAdapter) {
        InternalMapListenerAdapter internalMapListenerAdapter = (InternalMapListenerAdapter)listenerAdapter;
        ListenerAdapter[] listenerAdapters = internalMapListenerAdapter.getListenerAdapters();
        EntryEventType[] values = EntryEventType.values();
        int listenerFlags = 0;
        for (EntryEventType eventType : values) {
            ListenerAdapter definedAdapter = listenerAdapters[eventType.ordinal()];
            if (definedAdapter == null) continue;
            listenerFlags |= eventType.getType();
        }
        return listenerFlags;
    }

    private static int setAndGetAllListenerFlags() {
        EntryEventType[] values;
        int listenerFlags = 0;
        for (EntryEventType eventType : values = EntryEventType.values()) {
            listenerFlags |= eventType.getType();
        }
        return listenerFlags;
    }
}

