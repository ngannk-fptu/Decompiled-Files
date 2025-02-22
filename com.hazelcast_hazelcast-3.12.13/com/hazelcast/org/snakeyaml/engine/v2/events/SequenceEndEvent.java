/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.events;

import com.hazelcast.org.snakeyaml.engine.v2.events.CollectionEndEvent;
import com.hazelcast.org.snakeyaml.engine.v2.events.Event;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.Mark;
import java.util.Optional;

public final class SequenceEndEvent
extends CollectionEndEvent {
    public SequenceEndEvent(Optional<Mark> startMark, Optional<Mark> endMark) {
        super(startMark, endMark);
    }

    public SequenceEndEvent() {
    }

    @Override
    public Event.ID getEventId() {
        return Event.ID.SequenceEnd;
    }

    public String toString() {
        return "-SEQ";
    }
}

