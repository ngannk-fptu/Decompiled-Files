/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.core.EntryEventType;
import com.hazelcast.core.EntryListener;
import com.hazelcast.core.IMapEvent;
import com.hazelcast.map.impl.EntryListenerAdaptors;
import com.hazelcast.map.impl.ListenerAdapter;
import com.hazelcast.util.Preconditions;

class InternalEntryListenerAdapter
implements ListenerAdapter<IMapEvent> {
    private final ListenerAdapter[] listenerAdapters;

    InternalEntryListenerAdapter(EntryListener listener) {
        Preconditions.isNotNull(listener, "listener");
        this.listenerAdapters = EntryListenerAdaptors.createListenerAdapters(listener);
    }

    @Override
    public void onEvent(IMapEvent event) {
        EntryEventType eventType = event.getEventType();
        ListenerAdapter listenerAdapter = this.listenerAdapters[eventType.ordinal()];
        if (listenerAdapter == null) {
            return;
        }
        listenerAdapter.onEvent(event);
    }
}

