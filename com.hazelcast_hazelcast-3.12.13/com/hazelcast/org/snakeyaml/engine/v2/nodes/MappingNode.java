/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.nodes;

import com.hazelcast.org.snakeyaml.engine.v2.common.FlowStyle;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.Mark;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.CollectionNode;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.NodeTuple;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.NodeType;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.Tag;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class MappingNode
extends CollectionNode<NodeTuple> {
    private List<NodeTuple> value;
    private boolean merged = false;

    public MappingNode(Tag tag, boolean resolved, List<NodeTuple> value, FlowStyle flowStyle, Optional<Mark> startMark, Optional<Mark> endMark) {
        super(tag, flowStyle, startMark, endMark);
        Objects.requireNonNull("value in a Node is required.");
        this.value = value;
        this.resolved = resolved;
    }

    public MappingNode(Tag tag, List<NodeTuple> value, FlowStyle flowStyle) {
        this(tag, true, value, flowStyle, Optional.empty(), Optional.empty());
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.MAPPING;
    }

    @Override
    public List<NodeTuple> getValue() {
        return this.value;
    }

    public void setValue(List<NodeTuple> mergedValue) {
        this.value = mergedValue;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        for (NodeTuple node : this.getValue()) {
            buf.append("{ key=");
            buf.append(node.getKeyNode());
            buf.append("; value=");
            if (node.getValueNode() instanceof CollectionNode) {
                buf.append(System.identityHashCode(node.getValueNode()));
            } else {
                buf.append(node.toString());
            }
            buf.append(" }");
        }
        String values = buf.toString();
        return "<" + this.getClass().getName() + " (tag=" + this.getTag() + ", values=" + values + ")>";
    }

    public void setMerged(boolean merged) {
        this.merged = merged;
    }

    public boolean isMerged() {
        return this.merged;
    }
}

