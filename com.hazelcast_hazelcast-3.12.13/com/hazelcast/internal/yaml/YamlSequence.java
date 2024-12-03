/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.yaml;

import com.hazelcast.internal.yaml.YamlCollection;
import com.hazelcast.internal.yaml.YamlMapping;
import com.hazelcast.internal.yaml.YamlNode;
import com.hazelcast.internal.yaml.YamlScalar;

public interface YamlSequence
extends YamlCollection {
    public YamlNode child(int var1);

    public YamlMapping childAsMapping(int var1);

    public YamlSequence childAsSequence(int var1);

    public YamlScalar childAsScalar(int var1);

    public <T> T childAsScalarValue(int var1);

    public <T> T childAsScalarValue(int var1, Class<T> var2);
}

