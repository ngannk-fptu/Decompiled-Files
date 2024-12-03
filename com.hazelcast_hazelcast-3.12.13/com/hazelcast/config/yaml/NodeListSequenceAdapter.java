/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config.yaml;

import com.hazelcast.config.yaml.W3cDomUtil;
import com.hazelcast.internal.yaml.YamlSequence;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class NodeListSequenceAdapter
implements NodeList {
    private final YamlSequence yamlSequence;

    NodeListSequenceAdapter(YamlSequence yamlSequence) {
        this.yamlSequence = yamlSequence;
    }

    @Override
    public Node item(int index) {
        return W3cDomUtil.asW3cNode(this.yamlSequence.child(index));
    }

    @Override
    public int getLength() {
        return this.yamlSequence.childCount();
    }
}

