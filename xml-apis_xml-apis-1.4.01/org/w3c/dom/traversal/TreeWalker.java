/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.traversal;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeFilter;

public interface TreeWalker {
    public Node getRoot();

    public int getWhatToShow();

    public NodeFilter getFilter();

    public boolean getExpandEntityReferences();

    public Node getCurrentNode();

    public void setCurrentNode(Node var1) throws DOMException;

    public Node parentNode();

    public Node firstChild();

    public Node lastChild();

    public Node previousSibling();

    public Node nextSibling();

    public Node previousNode();

    public Node nextNode();
}

