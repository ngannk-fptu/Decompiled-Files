/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.nodes;

import com.hazelcast.org.snakeyaml.engine.v2.common.FlowStyle;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.Mark;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.CollectionNode;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.Node;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.NodeType;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.Tag;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SequenceNode
extends CollectionNode<Node> {
    private final List<Node> value;

    public SequenceNode(Tag tag, boolean resolved, List<Node> value, FlowStyle flowStyle, Optional<Mark> startMark, Optional<Mark> endMark) {
        super(tag, flowStyle, startMark, endMark);
        Objects.requireNonNull(value, "value in a Node is required.");
        this.value = value;
        this.resolved = resolved;
    }

    public SequenceNode(Tag tag, List<Node> value, FlowStyle flowStyle) {
        this(tag, true, value, flowStyle, Optional.empty(), Optional.empty());
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.SEQUENCE;
    }

    @Override
    public List<Node> getValue() {
        return this.value;
    }

    public String toString() {
        return "<" + this.getClass().getName() + " (tag=" + this.getTag() + ", value=" + this.getValue() + ")>";
    }
}

