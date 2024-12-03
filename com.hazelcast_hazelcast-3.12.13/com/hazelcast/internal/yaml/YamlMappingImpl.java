/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.yaml;

import com.hazelcast.internal.yaml.AbstractYamlNode;
import com.hazelcast.internal.yaml.MutableYamlMapping;
import com.hazelcast.internal.yaml.YamlMapping;
import com.hazelcast.internal.yaml.YamlNameNodePair;
import com.hazelcast.internal.yaml.YamlNode;
import com.hazelcast.internal.yaml.YamlScalar;
import com.hazelcast.internal.yaml.YamlSequence;
import com.hazelcast.internal.yaml.YamlUtil;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

public class YamlMappingImpl
extends AbstractYamlNode
implements MutableYamlMapping {
    private Map<String, YamlNode> children = Collections.emptyMap();

    YamlMappingImpl(YamlNode parent, String nodeName) {
        super(parent, nodeName);
    }

    @Override
    public YamlNode child(String name) {
        return this.children.get(name);
    }

    @Override
    public YamlMapping childAsMapping(String name) {
        return YamlUtil.asMapping(this.child(name));
    }

    @Override
    public YamlSequence childAsSequence(String name) {
        return YamlUtil.asSequence(this.child(name));
    }

    @Override
    public YamlScalar childAsScalar(String name) {
        return YamlUtil.asScalar(this.child(name));
    }

    @Override
    public <T> T childAsScalarValue(String name) {
        return this.childAsScalar(name).nodeValue();
    }

    @Override
    public <T> T childAsScalarValue(String name, Class<T> type) {
        return this.childAsScalar(name).nodeValue(type);
    }

    @Override
    public Iterable<YamlNode> children() {
        return this.children.values();
    }

    @Override
    public Iterable<YamlNameNodePair> childrenPairs() {
        LinkedList<YamlNameNodePair> pairs = new LinkedList<YamlNameNodePair>();
        for (Map.Entry<String, YamlNode> child : this.children.entrySet()) {
            pairs.add(new YamlNameNodePair(child.getKey(), child.getValue()));
        }
        return pairs;
    }

    @Override
    public void addChild(String name, YamlNode node) {
        this.getOrCreateChildren().put(name, node);
    }

    @Override
    public void removeChild(String name) {
        this.children.remove(name);
    }

    private Map<String, YamlNode> getOrCreateChildren() {
        if (this.children == Collections.emptyMap()) {
            this.children = new LinkedHashMap<String, YamlNode>();
        }
        return this.children;
    }

    @Override
    public int childCount() {
        return this.children.size();
    }

    public String toString() {
        return "YamlMappingImpl{nodeName=" + this.nodeName() + ", children=" + this.children + '}';
    }
}

