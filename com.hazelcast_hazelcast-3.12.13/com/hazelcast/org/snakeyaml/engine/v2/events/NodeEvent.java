/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.events;

import com.hazelcast.org.snakeyaml.engine.v2.common.Anchor;
import com.hazelcast.org.snakeyaml.engine.v2.events.Event;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.Mark;
import java.util.Objects;
import java.util.Optional;

public abstract class NodeEvent
extends Event {
    private final Optional<Anchor> anchor;

    public NodeEvent(Optional<Anchor> anchor, Optional<Mark> startMark, Optional<Mark> endMark) {
        super(startMark, endMark);
        Objects.requireNonNull(anchor, "Anchor cannot be null");
        this.anchor = anchor;
    }

    public Optional<Anchor> getAnchor() {
        return this.anchor;
    }
}

