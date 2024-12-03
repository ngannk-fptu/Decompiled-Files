/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.events;

import com.hazelcast.org.snakeyaml.engine.v2.events.Event;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.Mark;
import java.util.Optional;

public final class StreamStartEvent
extends Event {
    public StreamStartEvent(Optional<Mark> startMark, Optional<Mark> endMark) {
        super(startMark, endMark);
    }

    public StreamStartEvent() {
    }

    @Override
    public Event.ID getEventId() {
        return Event.ID.StreamStart;
    }

    public String toString() {
        return "+STR";
    }
}

