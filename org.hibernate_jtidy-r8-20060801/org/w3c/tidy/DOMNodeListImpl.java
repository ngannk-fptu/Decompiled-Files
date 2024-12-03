/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

import org.w3c.dom.NodeList;
import org.w3c.tidy.Node;

public class DOMNodeListImpl
implements NodeList {
    private Node parent;

    protected DOMNodeListImpl(Node parent) {
        this.parent = parent;
    }

    public org.w3c.dom.Node item(int index) {
        if (this.parent == null) {
            return null;
        }
        Node node = this.parent.content;
        for (int i = 0; node != null && i < index; ++i) {
            node = node.next;
        }
        if (node != null) {
            return node.getAdapter();
        }
        return null;
    }

    public int getLength() {
        if (this.parent == null) {
            return 0;
        }
        int len = 0;
        Node node = this.parent.content;
        while (node != null) {
            ++len;
            node = node.next;
        }
        return len;
    }
}

