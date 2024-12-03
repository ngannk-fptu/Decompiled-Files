/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.nodes;

import com.hazelcast.org.snakeyaml.engine.v2.common.ScalarStyle;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.Mark;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.Node;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.NodeType;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.Tag;
import java.util.Objects;
import java.util.Optional;

public class ScalarNode
extends Node {
    private ScalarStyle style;
    private String value;

    public ScalarNode(Tag tag, boolean resolved, String value, ScalarStyle style, Optional<Mark> startMark, Optional<Mark> endMark) {
        super(tag, startMark, endMark);
        Objects.requireNonNull(value, "value in a Node is required.");
        this.value = value;
        Objects.requireNonNull(style, "Scalar style must be provided.");
        this.style = style;
        this.resolved = resolved;
    }

    public ScalarNode(Tag tag, String value, ScalarStyle style) {
        this(tag, true, value, style, Optional.empty(), Optional.empty());
    }

    public ScalarStyle getScalarStyle() {
        return this.style;
    }

    @Override
    public NodeType getNodeType() {
        return NodeType.SCALAR;
    }

    public String getValue() {
        return this.value;
    }

    public String toString() {
        return "<" + this.getClass().getName() + " (tag=" + this.getTag() + ", value=" + this.getValue() + ")>";
    }

    public boolean isPlain() {
        return this.style == ScalarStyle.PLAIN;
    }
}

