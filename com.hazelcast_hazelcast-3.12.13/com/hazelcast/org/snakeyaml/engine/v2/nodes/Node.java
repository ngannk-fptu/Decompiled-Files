/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.nodes;

import com.hazelcast.org.snakeyaml.engine.v2.common.Anchor;
import com.hazelcast.org.snakeyaml.engine.v2.exceptions.Mark;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.NodeType;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.Tag;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public abstract class Node {
    private Tag tag;
    private Optional<Mark> startMark;
    protected Optional<Mark> endMark;
    private boolean recursive;
    private Optional<Anchor> anchor;
    private Map<String, Object> properties;
    protected boolean resolved;

    public Node(Tag tag, Optional<Mark> startMark, Optional<Mark> endMark) {
        this.setTag(tag);
        this.startMark = startMark;
        this.endMark = endMark;
        this.recursive = false;
        this.resolved = true;
        this.anchor = Optional.empty();
        this.properties = null;
    }

    public Tag getTag() {
        return this.tag;
    }

    public Optional<Mark> getEndMark() {
        return this.endMark;
    }

    public abstract NodeType getNodeType();

    public Optional<Mark> getStartMark() {
        return this.startMark;
    }

    public void setTag(Tag tag) {
        Objects.requireNonNull(tag, "tag in a Node is required.");
        this.tag = tag;
    }

    public final boolean equals(Object obj) {
        return super.equals(obj);
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    public boolean isRecursive() {
        return this.recursive;
    }

    public final int hashCode() {
        return super.hashCode();
    }

    public Optional<Anchor> getAnchor() {
        return this.anchor;
    }

    public void setAnchor(Optional<Anchor> anchor) {
        this.anchor = anchor;
    }

    public Object setProperty(String key, Object value) {
        if (this.properties == null) {
            this.properties = new HashMap<String, Object>();
        }
        return this.properties.put(key, value);
    }

    public Object getProperty(String key) {
        if (this.properties == null) {
            return null;
        }
        return this.properties.get(key);
    }
}

