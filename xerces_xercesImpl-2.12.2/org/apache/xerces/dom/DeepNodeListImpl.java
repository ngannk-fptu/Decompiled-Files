/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import java.util.ArrayList;
import org.apache.xerces.dom.ElementImpl;
import org.apache.xerces.dom.NodeImpl;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DeepNodeListImpl
implements NodeList {
    protected NodeImpl rootNode;
    protected String tagName;
    protected int changes = 0;
    protected ArrayList nodes;
    protected String nsName;
    protected boolean enableNS = false;

    public DeepNodeListImpl(NodeImpl nodeImpl, String string) {
        this.rootNode = nodeImpl;
        this.tagName = string;
        this.nodes = new ArrayList();
    }

    public DeepNodeListImpl(NodeImpl nodeImpl, String string, String string2) {
        this(nodeImpl, string2);
        this.nsName = string != null && string.length() != 0 ? string : null;
        this.enableNS = true;
    }

    @Override
    public int getLength() {
        this.item(Integer.MAX_VALUE);
        return this.nodes.size();
    }

    @Override
    public Node item(int n) {
        int n2;
        if (this.rootNode.changes() != this.changes) {
            this.nodes = new ArrayList();
            this.changes = this.rootNode.changes();
        }
        if (n < (n2 = this.nodes.size())) {
            return (Node)this.nodes.get(n);
        }
        Node node = n2 == 0 ? this.rootNode : (NodeImpl)this.nodes.get(n2 - 1);
        while (node != null && n >= this.nodes.size()) {
            if ((node = this.nextMatchingElementAfter(node)) == null) continue;
            this.nodes.add(node);
        }
        return node;
    }

    protected Node nextMatchingElementAfter(Node node) {
        while (node != null) {
            ElementImpl elementImpl;
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
            if (node == this.rootNode || node == null || node.getNodeType() != 1) continue;
            if (!this.enableNS) {
                if (!this.tagName.equals("*") && !((ElementImpl)node).getTagName().equals(this.tagName)) continue;
                return node;
            }
            if (this.tagName.equals("*")) {
                if (this.nsName != null && this.nsName.equals("*")) {
                    return node;
                }
                elementImpl = (ElementImpl)node;
                if ((this.nsName != null || elementImpl.getNamespaceURI() != null) && (this.nsName == null || !this.nsName.equals(elementImpl.getNamespaceURI()))) continue;
                return node;
            }
            elementImpl = (ElementImpl)node;
            if (elementImpl.getLocalName() == null || !elementImpl.getLocalName().equals(this.tagName)) continue;
            if (this.nsName != null && this.nsName.equals("*")) {
                return node;
            }
            if ((this.nsName != null || elementImpl.getNamespaceURI() != null) && (this.nsName == null || !this.nsName.equals(elementImpl.getNamespaceURI()))) continue;
            return node;
        }
        return null;
    }
}

