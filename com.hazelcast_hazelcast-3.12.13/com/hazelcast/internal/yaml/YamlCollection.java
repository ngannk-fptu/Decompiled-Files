/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.yaml;

import com.hazelcast.internal.yaml.YamlNode;

public interface YamlCollection
extends YamlNode {
    public Iterable<YamlNode> children();

    public int childCount();
}

