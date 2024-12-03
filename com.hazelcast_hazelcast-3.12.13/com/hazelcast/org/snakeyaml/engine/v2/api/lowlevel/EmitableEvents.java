/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.api.lowlevel;

import com.hazelcast.org.snakeyaml.engine.v2.emitter.Emitable;
import com.hazelcast.org.snakeyaml.engine.v2.events.Event;
import java.util.ArrayList;
import java.util.List;

class EmitableEvents
implements Emitable {
    private List<Event> events = new ArrayList<Event>();

    EmitableEvents() {
    }

    @Override
    public void emit(Event event) {
        this.events.add(event);
    }

    public List<Event> getEvents() {
        return this.events;
    }
}

