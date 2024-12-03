/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.yaml;

import com.hazelcast.internal.yaml.MutableYamlNode;
import com.hazelcast.internal.yaml.YamlNode;
import com.hazelcast.internal.yaml.YamlUtil;

public abstract class AbstractYamlNode
implements MutableYamlNode {
    private final YamlNode parent;
    private String nodeName;
    private String path;

    AbstractYamlNode(YamlNode parent, String nodeName) {
        this.parent = parent;
        this.nodeName = nodeName;
        this.path = YamlUtil.constructPath(parent, nodeName);
    }

    @Override
    public String nodeName() {
        return this.nodeName != null ? this.nodeName : "<unnamed>";
    }

    @Override
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
        this.path = YamlUtil.constructPath(this.parent, nodeName);
    }

    @Override
    public YamlNode parent() {
        return this.parent;
    }

    @Override
    public String path() {
        return this.path;
    }
}

