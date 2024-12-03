/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config.yaml;

import com.hazelcast.config.yaml.W3cDomUtil;
import com.hazelcast.config.yaml.YamlOrderedMapping;
import com.hazelcast.config.yaml.YamlOrderedMappingImpl;
import com.hazelcast.internal.yaml.YamlMapping;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

class NamedNodeMapAdapter
implements NamedNodeMap {
    private final YamlOrderedMapping yamlMapping;

    NamedNodeMapAdapter(YamlMapping yamlMapping) {
        this.yamlMapping = YamlOrderedMappingImpl.asOrderedMapping(yamlMapping);
    }

    @Override
    public Node getNamedItem(String name) {
        return W3cDomUtil.asW3cNode(this.yamlMapping.child(name));
    }

    @Override
    public Node setNamedItem(Node arg) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node removeNamedItem(String name) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node item(int index) {
        return W3cDomUtil.asW3cNode(this.yamlMapping.child(index));
    }

    @Override
    public int getLength() {
        return this.yamlMapping.childCount();
    }

    @Override
    public Node getNamedItemNS(String namespaceURI, String localName) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node setNamedItemNS(Node arg) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Node removeNamedItemNS(String namespaceURI, String localName) throws DOMException {
        throw new UnsupportedOperationException();
    }
}

