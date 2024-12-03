/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.yaml;

import com.hazelcast.internal.yaml.MutableYamlNode;
import com.hazelcast.internal.yaml.YamlMapping;
import com.hazelcast.internal.yaml.YamlNode;

public interface MutableYamlMapping
extends YamlMapping,
MutableYamlNode {
    public void addChild(String var1, YamlNode var2);

    public void removeChild(String var1);
}

