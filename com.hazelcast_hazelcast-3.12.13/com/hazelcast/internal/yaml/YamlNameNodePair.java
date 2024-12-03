/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.yaml;

import com.hazelcast.internal.yaml.YamlNode;

public class YamlNameNodePair {
    private final String nodeName;
    private final YamlNode childNode;

    YamlNameNodePair(String nodeName, YamlNode childNode) {
        this.nodeName = nodeName;
        this.childNode = childNode;
    }

    public String nodeName() {
        return this.nodeName;
    }

    public YamlNode childNode() {
        return this.childNode;
    }
}

