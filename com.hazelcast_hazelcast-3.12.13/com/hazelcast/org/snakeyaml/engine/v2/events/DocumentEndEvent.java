/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.events;

import com.hazelcast.org.snakeyaml.engine.v2.events.Event;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.Mark;
import java.util.Optional;

public final class DocumentEndEvent
extends Event {
    private final boolean explicit;

    public DocumentEndEvent(boolean explicit, Optional<Mark> startMark, Optional<Mark> endMark) {
        super(startMark, endMark);
        this.explicit = explicit;
    }

    public DocumentEndEvent(boolean explicit) {
        this(explicit, Optional.empty(), Optional.empty());
    }

    public boolean isExplicit() {
        return this.explicit;
    }

    @Override
    public Event.ID getEventId() {
        return Event.ID.DocumentEnd;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("-DOC");
        if (this.isExplicit()) {
            builder.append(" ...");
        }
        return builder.toString();
    }
}

