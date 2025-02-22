/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.events;

import com.hazelcast.org.snakeyaml.engine.v2.events.Event;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.Mark;
import java.util.Optional;

public final class StreamEndEvent
extends Event {
    public StreamEndEvent(Optional<Mark> startMark, Optional<Mark> endMark) {
        super(startMark, endMark);
    }

    public StreamEndEvent() {
    }

    @Override
    public Event.ID getEventId() {
        return Event.ID.StreamEnd;
    }

    public String toString() {
        return "-STR";
    }
}

