/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.yaml;

import com.hazelcast.internal.yaml.MutableYamlNode;
import com.hazelcast.internal.yaml.YamlNode;
import com.hazelcast.internal.yaml.YamlSequence;

public interface MutableYamlSequence
extends YamlSequence,
MutableYamlNode {
    public void addChild(YamlNode var1);
}

