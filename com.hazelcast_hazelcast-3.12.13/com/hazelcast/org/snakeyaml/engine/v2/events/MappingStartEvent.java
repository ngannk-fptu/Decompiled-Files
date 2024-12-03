/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.events;

import com.hazelcast.org.snakeyaml.engine.v2.common.Anchor;
import com.hazelcast.org.snakeyaml.engine.v2.common.FlowStyle;
import com.hazelcast.org.snakeyaml.engine.v2.events.CollectionStartEvent;
import com.hazelcast.org.snakeyaml.engine.v2.events.Event;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.Mark;
import java.util.Optional;

public final class MappingStartEvent
extends CollectionStartEvent {
    public MappingStartEvent(Optional<Anchor> anchor, Optional<String> tag, boolean implicit, FlowStyle flowStyle, Optional<Mark> startMark, Optional<Mark> endMark) {
        super(anchor, tag, implicit, flowStyle, startMark, endMark);
    }

    public MappingStartEvent(Optional<Anchor> anchor, Optional<String> tag, boolean implicit, FlowStyle flowStyle) {
        this(anchor, tag, implicit, flowStyle, Optional.empty(), Optional.empty());
    }

    @Override
    public Event.ID getEventId() {
        return Event.ID.MappingStart;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("+MAP");
        if (this.getFlowStyle() == FlowStyle.FLOW) {
            // empty if block
        }
        builder.append(super.toString());
        return builder.toString();
    }
}

