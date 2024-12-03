/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.org.snakeyaml.engine.v2.api;

import com.hazelcast.org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import com.hazelcast.org.snakeyaml.engine.v2.nodes.Node;

public interface ConstructNode {
    public Object construct(Node var1);

    default public void constructRecursive(Node node, Object object) {
        if (node.isRecursive()) {
            throw new IllegalStateException("Not implemented in " + this.getClass().getName());
        }
        throw new YamlEngineException("Unexpected recursive structure for Node: " + node);
    }
}

