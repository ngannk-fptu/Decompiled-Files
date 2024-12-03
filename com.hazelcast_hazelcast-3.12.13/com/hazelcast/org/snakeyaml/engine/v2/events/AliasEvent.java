/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.events;

import com.hazelcast.org.snakeyaml.engine.v2.common.Anchor;
import com.hazelcast.org.snakeyaml.engine.v2.events.Event;
import com.hazelcast.org.snakeyaml.engine.v2.events.NodeEvent;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.Mark;
import java.util.Optional;

public final class AliasEvent
extends NodeEvent {
    private final Anchor alias;

    public AliasEvent(Optional<Anchor> anchor, Optional<Mark> startMark, Optional<Mark> endMark) {
        super(anchor, startMark, endMark);
        if (!anchor.isPresent()) {
            throw new NullPointerException("Anchor is required in AliasEvent");
        }
        this.alias = anchor.get();
    }

    public AliasEvent(Optional<Anchor> anchor) {
        this(anchor, Optional.empty(), Optional.empty());
    }

    @Override
    public Event.ID getEventId() {
        return Event.ID.Alias;
    }

    public String toString() {
        return "=ALI *" + this.alias;
    }

    public Anchor getAlias() {
        return this.alias;
    }
}

