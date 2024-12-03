/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config.yaml;

import com.hazelcast.config.yaml.YamlOrderedMapping;
import com.hazelcast.internal.yaml.YamlMapping;
import com.hazelcast.internal.yaml.YamlNameNodePair;
import com.hazelcast.internal.yaml.YamlNode;
import com.hazelcast.internal.yaml.YamlScalar;
import com.hazelcast.internal.yaml.YamlSequence;
import java.util.ArrayList;
import java.util.List;

final class YamlOrderedMappingImpl
implements YamlOrderedMapping {
    private final YamlMapping wrappedMapping;
    private final List<YamlNode> randomAccessChildren;

    private YamlOrderedMappingImpl(YamlMapping wrappedMapping) {
        this.wrappedMapping = wrappedMapping;
        this.randomAccessChildren = new ArrayList<YamlNode>(wrappedMapping.childCount());
        this.copyChildren();
    }

    @Override
    public YamlNode child(String name) {
        return this.wrappedMapping.child(name);
    }

    @Override
    public YamlMapping childAsMapping(String name) {
        return this.wrappedMapping.childAsMapping(name);
    }

    @Override
    public YamlSequence childAsSequence(String name) {
        return this.wrappedMapping.childAsSequence(name);
    }

    @Override
    public YamlScalar childAsScalar(String name) {
        return this.wrappedMapping.childAsScalar(name);
    }

    @Override
    public <T> T childAsScalarValue(String name) {
        return this.wrappedMapping.childAsScalarValue(name);
    }

    @Override
    public <T> T childAsScalarValue(String name, Class<T> type) {
        return this.wrappedMapping.childAsScalarValue(name, type);
    }

    @Override
    public Iterable<YamlNode> children() {
        return this.wrappedMapping.children();
    }

    @Override
    public Iterable<YamlNameNodePair> childrenPairs() {
        return this.wrappedMapping.childrenPairs();
    }

    @Override
    public int childCount() {
        return this.wrappedMapping.childCount();
    }

    @Override
    public YamlNode parent() {
        return this.wrappedMapping.parent();
    }

    @Override
    public String nodeName() {
        return this.wrappedMapping.nodeName();
    }

    @Override
    public String path() {
        return this.wrappedMapping.path();
    }

    @Override
    public YamlNode child(int index) {
        if (index >= this.randomAccessChildren.size()) {
            return null;
        }
        return this.randomAccessChildren.get(index);
    }

    static YamlOrderedMappingImpl asOrderedMapping(YamlMapping yamlMapping) {
        return new YamlOrderedMappingImpl(yamlMapping);
    }

    private void copyChildren() {
        for (YamlNode child : this.wrappedMapping.children()) {
            this.randomAccessChildren.add(child);
        }
    }
}

