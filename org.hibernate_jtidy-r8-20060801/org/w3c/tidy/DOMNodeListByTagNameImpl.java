/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

import org.w3c.dom.NodeList;
import org.w3c.tidy.Node;

public class DOMNodeListByTagNameImpl
implements NodeList {
    private Node first;
    private String tagName;
    private int currIndex;
    private int maxIndex;
    private Node currNode;

    protected DOMNodeListByTagNameImpl(Node first, String tagName) {
        this.first = first;
        this.tagName = tagName;
    }

    public org.w3c.dom.Node item(int index) {
        this.currIndex = 0;
        this.maxIndex = index;
        this.preTraverse(this.first);
        if (this.currIndex > this.maxIndex && this.currNode != null) {
            return this.currNode.getAdapter();
        }
        return null;
    }

    public int getLength() {
        this.currIndex = 0;
        this.maxIndex = Integer.MAX_VALUE;
        this.preTraverse(this.first);
        return this.currIndex;
    }

    protected void preTraverse(Node node) {
        if (node == null) {
            return;
        }
        if ((node.type == 5 || node.type == 7) && this.currIndex <= this.maxIndex && (this.tagName.equals("*") || this.tagName.equals(node.element))) {
            ++this.currIndex;
            this.currNode = node;
        }
        if (this.currIndex > this.maxIndex) {
            return;
        }
        node = node.content;
        while (node != null) {
            this.preTraverse(node);
            node = node.next;
        }
    }
}

