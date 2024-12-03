/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config.yaml;

import com.hazelcast.internal.yaml.YamlMapping;
import com.hazelcast.internal.yaml.YamlNode;

interface YamlOrderedMapping
extends YamlMapping {
    public YamlNode child(int var1);
}

