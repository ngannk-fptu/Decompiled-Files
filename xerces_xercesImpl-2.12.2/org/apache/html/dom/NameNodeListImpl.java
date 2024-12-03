/*
 * Decompiled with CFR 0.152.
 */
package org.apache.html.dom;

import org.apache.xerces.dom.DeepNodeListImpl;
import org.apache.xerces.dom.ElementImpl;
import org.apache.xerces.dom.NodeImpl;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NameNodeListImpl
extends DeepNodeListImpl
implements NodeList {
    public NameNodeListImpl(NodeImpl nodeImpl, String string) {
        super(nodeImpl, string);
    }

    @Override
    protected Node nextMatchingElementAfter(Node node) {
        while (node != null) {
            String string;
            Node node2;
            if (node.hasChildNodes()) {
                node = node.getFirstChild();
            } else if (node != this.rootNode && null != (node2 = node.getNextSibling())) {
                node = node2;
            } else {
                node2 = null;
                while (node != this.rootNode && (node2 = node.getNextSibling()) == null) {
                    node = node.getParentNode();
                }
                node = node2;
            }
            if (node == this.rootNode || node == null || node.getNodeType() != 1 || !(string = ((ElementImpl)node).getAttribute("name")).equals("*") && !string.equals(this.tagName)) continue;
            return node;
        }
        return null;
    }
}

