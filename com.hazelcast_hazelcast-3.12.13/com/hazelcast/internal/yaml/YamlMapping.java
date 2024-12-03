/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.yaml;

import com.hazelcast.internal.yaml.YamlCollection;
import com.hazelcast.internal.yaml.YamlNameNodePair;
import com.hazelcast.internal.yaml.YamlNode;
import com.hazelcast.internal.yaml.YamlScalar;
import com.hazelcast.internal.yaml.YamlSequence;

public interface YamlMapping
extends YamlCollection {
    public YamlNode child(String var1);

    public Iterable<YamlNameNodePair> childrenPairs();

    public YamlMapping childAsMapping(String var1);

    public YamlSequence childAsSequence(String var1);

    public YamlScalar childAsScalar(String var1);

    public <T> T childAsScalarValue(String var1);

    public <T> T childAsScalarValue(String var1, Class<T> var2);
}

