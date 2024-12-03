/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.nodes;

import com.hazelcast.org.snakeyaml.engine.v2.common.FlowStyle;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.Mark;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.Node;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.Tag;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class CollectionNode<T>
extends Node {
    private FlowStyle flowStyle;

    public CollectionNode(Tag tag, FlowStyle flowStyle, Optional<Mark> startMark, Optional<Mark> endMark) {
        super(tag, startMark, endMark);
        this.setFlowStyle(flowStyle);
    }

    public abstract List<T> getValue();

    public FlowStyle getFlowStyle() {
        return this.flowStyle;
    }

    public void setFlowStyle(FlowStyle flowStyle) {
        Objects.requireNonNull(flowStyle, "Flow style must be provided.");
        this.flowStyle = flowStyle;
    }

    public void setEndMark(Optional<Mark> endMark) {
        this.endMark = endMark;
    }
}

