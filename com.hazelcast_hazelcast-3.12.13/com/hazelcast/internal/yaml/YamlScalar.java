/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.yaml;

import com.hazelcast.internal.yaml.YamlNode;

public interface YamlScalar
extends YamlNode {
    public <T> boolean isA(Class<T> var1);

    public <T> T nodeValue();

    public <T> T nodeValue(Class<T> var1);
}

