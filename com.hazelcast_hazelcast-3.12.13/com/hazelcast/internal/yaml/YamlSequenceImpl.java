/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.yaml;

import com.hazelcast.internal.yaml.AbstractYamlNode;
import com.hazelcast.internal.yaml.MutableYamlSequence;
import com.hazelcast.internal.yaml.YamlMapping;
import com.hazelcast.internal.yaml.YamlNode;
import com.hazelcast.internal.yaml.YamlScalar;
import com.hazelcast.internal.yaml.YamlSequence;
import com.hazelcast.internal.yaml.YamlUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class YamlSequenceImpl
extends AbstractYamlNode
implements MutableYamlSequence {
    private List<YamlNode> children = Collections.emptyList();

    YamlSequenceImpl(YamlNode parent, String nodeName) {
        super(parent, nodeName);
    }

    @Override
    public YamlNode child(int index) {
        if (index >= this.children.size()) {
            return null;
        }
        return this.children.get(index);
    }

    @Override
    public Iterable<YamlNode> children() {
        return this.children;
    }

    @Override
    public YamlMapping childAsMapping(int index) {
        return YamlUtil.asMapping(this.child(index));
    }

    @Override
    public YamlSequence childAsSequence(int index) {
        return YamlUtil.asSequence(this.child(index));
    }

    @Override
    public YamlScalar childAsScalar(int index) {
        return YamlUtil.asScalar(this.child(index));
    }

    @Override
    public <T> T childAsScalarValue(int index) {
        return this.childAsScalar(index).nodeValue();
    }

    @Override
    public <T> T childAsScalarValue(int index, Class<T> type) {
        return this.childAsScalar(index).nodeValue(type);
    }

    @Override
    public void addChild(YamlNode child) {
        this.getOrCreateChildren().add(child);
    }

    private List<YamlNode> getOrCreateChildren() {
        if (this.children == Collections.emptyList()) {
            this.children = new ArrayList<YamlNode>();
        }
        return this.children;
    }

    @Override
    public int childCount() {
        return this.children.size();
    }

    public String toString() {
        return "YamlSequenceImpl{nodeName=" + this.nodeName() + ", children=" + this.children + '}';
    }
}

