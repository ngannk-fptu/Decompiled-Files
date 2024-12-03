/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.config.yaml;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class SingletonNodeList
implements NodeList {
    private final Node singletonItem;

    SingletonNodeList(Node singletonItem) {
        this.singletonItem = singletonItem;
    }

    @Override
    public Node item(int index) {
        if (index != 0) {
            return null;
        }
        return this.singletonItem;
    }

    @Override
    public int getLength() {
        return 1;
    }
}

