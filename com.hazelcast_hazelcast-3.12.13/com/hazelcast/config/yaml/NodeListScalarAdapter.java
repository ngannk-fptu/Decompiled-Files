/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config.yaml;

import com.hazelcast.config.yaml.ScalarTextNodeAdapter;
import com.hazelcast.internal.yaml.YamlScalar;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class NodeListScalarAdapter
implements NodeList {
    private final YamlScalar scalar;

    NodeListScalarAdapter(YamlScalar scalar) {
        this.scalar = scalar;
    }

    @Override
    public Node item(int index) {
        return new ScalarTextNodeAdapter(this.scalar);
    }

    @Override
    public int getLength() {
        return 1;
    }
}

