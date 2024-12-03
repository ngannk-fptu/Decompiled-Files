/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config.yaml;

import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

final class EmptyNamedNodeMap
implements NamedNodeMap {
    private static final NamedNodeMap INSTANCE = new EmptyNamedNodeMap();

    private EmptyNamedNodeMap() {
    }

    @Override
    public Node getNamedItem(String name) {
        return null;
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
        return null;
    }

    @Override
    public int getLength() {
        return 0;
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

    static NamedNodeMap emptyNamedNodeMap() {
        return INSTANCE;
    }
}

