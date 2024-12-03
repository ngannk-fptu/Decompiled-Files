/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.map.impl;

import com.hazelcast.core.EntryEventType;
import com.hazelcast.core.IMapEvent;
import com.hazelcast.map.impl.ListenerAdapter;
import com.hazelcast.map.impl.MapListenerAdaptors;
import com.hazelcast.map.listener.MapListener;
import com.hazelcast.util.Preconditions;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class InternalMapListenerAdapter
implements ListenerAdapter<IMapEvent> {
    private final ListenerAdapter[] listenerAdapters;

    InternalMapListenerAdapter(MapListener mapListener) {
        Preconditions.isNotNull(mapListener, "mapListener");
        this.listenerAdapters = MapListenerAdaptors.createListenerAdapters(mapListener);
    }

    @Override
    public void onEvent(IMapEvent event) {
        EntryEventType eventType = event.getEventType();
        if (eventType == null) {
            return;
        }
        ListenerAdapter listenerAdapter = this.listenerAdapters[eventType.ordinal()];
        if (listenerAdapter == null) {
            return;
        }
        listenerAdapter.onEvent(event);
    }

    @SuppressFBWarnings(value={"EI_EXPOSE_REP"}, justification="listenerAdapters internal state is never changed")
    public ListenerAdapter[] getListenerAdapters() {
        return this.listenerAdapters;
    }
}

