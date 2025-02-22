/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.nodes;

import com.hazelcast.org.snakeyaml.engine.v2.nodes.Node;
import java.util.Objects;

public final class NodeTuple {
    private Node keyNode;
    private Node valueNode;

    public NodeTuple(Node keyNode, Node valueNode) {
        Objects.requireNonNull(keyNode, "keyNode must be provided.");
        Objects.requireNonNull(valueNode, "value Node must be provided");
        this.keyNode = keyNode;
        this.valueNode = valueNode;
    }

    public Node getKeyNode() {
        return this.keyNode;
    }

    public Node getValueNode() {
        return this.valueNode;
    }

    public String toString() {
        return "<NodeTuple keyNode=" + this.keyNode.toString() + "; valueNode=" + this.valueNode.toString() + ">";
    }
}

