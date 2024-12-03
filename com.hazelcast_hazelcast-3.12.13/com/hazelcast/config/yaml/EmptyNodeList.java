/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config.yaml;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

final class EmptyNodeList
implements NodeList {
    private static final NodeList INSTANCE = new EmptyNodeList();

    private EmptyNodeList() {
    }

    @Override
    public Node item(int index) {
        return null;
    }

    @Override
    public int getLength() {
        return 0;
    }

    static NodeList emptyNodeList() {
        return INSTANCE;
    }
}

