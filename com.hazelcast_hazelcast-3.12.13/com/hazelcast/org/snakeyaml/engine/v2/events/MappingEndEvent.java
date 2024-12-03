/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.events;

import com.hazelcast.org.snakeyaml.engine.v2.events.CollectionEndEvent;
import com.hazelcast.org.snakeyaml.engine.v2.events.Event;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.Mark;
import java.util.Optional;

public final class MappingEndEvent
extends CollectionEndEvent {
    public MappingEndEvent(Optional<Mark> startMark, Optional<Mark> endMark) {
        super(startMark, endMark);
    }

    public MappingEndEvent() {
    }

    @Override
    public Event.ID getEventId() {
        return Event.ID.MappingEnd;
    }

    public String toString() {
        return "-MAP";
    }
}

