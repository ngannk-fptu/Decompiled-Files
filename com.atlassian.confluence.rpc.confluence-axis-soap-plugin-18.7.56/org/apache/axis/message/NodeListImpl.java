/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class NodeListImpl
implements NodeList {
    List mNodes = new ArrayList();
    public static final NodeList EMPTY_NODELIST = new NodeListImpl(Collections.EMPTY_LIST);

    NodeListImpl() {
    }

    NodeListImpl(List nodes) {
        this();
        this.mNodes.addAll(nodes);
    }

    void addNode(Node node) {
        this.mNodes.add(node);
    }

    void addNodeList(NodeList nodes) {
        for (int i = 0; i < nodes.getLength(); ++i) {
            this.mNodes.add(nodes.item(i));
        }
    }

    public Node item(int index) {
        if (this.mNodes != null && this.mNodes.size() > index) {
            return (Node)this.mNodes.get(index);
        }
        return null;
    }

    public int getLength() {
        return this.mNodes.size();
    }
}

