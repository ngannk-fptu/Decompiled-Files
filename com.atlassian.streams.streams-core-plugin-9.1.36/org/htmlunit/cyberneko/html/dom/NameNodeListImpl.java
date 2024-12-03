/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.html.dom;

import org.htmlunit.cyberneko.xerces.dom.DeepNodeListImpl;
import org.htmlunit.cyberneko.xerces.dom.ElementImpl;
import org.htmlunit.cyberneko.xerces.dom.NodeImpl;
import org.w3c.dom.Node;

public class NameNodeListImpl
extends DeepNodeListImpl {
    public NameNodeListImpl(NodeImpl rootNode, String tagName) {
        super(rootNode, tagName);
    }

    @Override
    protected Node nextMatchingElementAfter(Node current) {
        while (current != null) {
            String name;
            Node next;
            if (current.hasChildNodes()) {
                current = current.getFirstChild();
            } else if (current != this.rootNode && null != (next = current.getNextSibling())) {
                current = next;
            } else {
                next = null;
                while (current != this.rootNode && (next = current.getNextSibling()) == null) {
                    current = current.getParentNode();
                }
                current = next;
            }
            if (current == this.rootNode || current == null || current.getNodeType() != 1 || !"*".equals(name = ((ElementImpl)current).getAttribute("name")) && !name.equals(this.tagName)) continue;
            return current;
        }
        return null;
    }
}

