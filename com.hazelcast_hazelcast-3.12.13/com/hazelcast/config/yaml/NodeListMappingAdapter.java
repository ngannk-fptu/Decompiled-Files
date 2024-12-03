/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config.yaml;

import com.hazelcast.config.yaml.W3cDomUtil;
import com.hazelcast.config.yaml.YamlOrderedMapping;
import com.hazelcast.config.yaml.YamlOrderedMappingImpl;
import com.hazelcast.internal.yaml.YamlMapping;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class NodeListMappingAdapter
implements NodeList {
    private final YamlOrderedMapping yamlMapping;

    NodeListMappingAdapter(YamlMapping yamlMapping) {
        this.yamlMapping = YamlOrderedMappingImpl.asOrderedMapping(yamlMapping);
    }

    @Override
    public Node item(int index) {
        return W3cDomUtil.asW3cNode(this.yamlMapping.child(index));
    }

    @Override
    public int getLength() {
        return this.yamlMapping.childCount();
    }
}

