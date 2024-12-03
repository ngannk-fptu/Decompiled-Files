/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.map.impl;

import com.hazelcast.map.impl.MapDataSerializerHook;
import com.hazelcast.map.impl.MapListenerFlagOperator;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.EventFilter;
import com.hazelcast.spi.impl.eventservice.impl.TrueEventFilter;
import java.io.IOException;

public class EventListenerFilter
implements EventFilter,
IdentifiedDataSerializable {
    private int listenerFlags;
    private EventFilter eventFilter;

    public EventListenerFilter() {
        this(MapListenerFlagOperator.SET_ALL_LISTENER_FLAGS, TrueEventFilter.INSTANCE);
    }

    public EventListenerFilter(int listenerFlags, EventFilter eventFilter) {
        this.listenerFlags = listenerFlags;
        this.eventFilter = eventFilter == null ? TrueEventFilter.INSTANCE : eventFilter;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeObject(this.eventFilter);
        out.writeInt(this.listenerFlags);
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.eventFilter = (EventFilter)in.readObject();
        this.listenerFlags = in.readInt();
    }

    @Override
    public boolean eval(Object object) {
        Integer eventType = (Integer)object;
        return (this.listenerFlags & eventType) != 0;
    }

    public EventFilter getEventFilter() {
        return this.eventFilter;
    }

    public String toString() {
        return "EventListenerFilter{listenerFlags=" + this.listenerFlags + ", eventFilter=" + this.eventFilter + '}';
    }

    @Override
    public int getFactoryId() {
        return MapDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 92;
    }
}

