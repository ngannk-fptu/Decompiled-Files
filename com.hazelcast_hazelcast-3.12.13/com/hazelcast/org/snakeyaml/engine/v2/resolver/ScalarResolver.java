/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.resolver;

import com.hazelcast.org.snakeyaml.engine.v2.nodes.Tag;

public interface ScalarResolver {
    public Tag resolve(String var1, Boolean var2);
}

