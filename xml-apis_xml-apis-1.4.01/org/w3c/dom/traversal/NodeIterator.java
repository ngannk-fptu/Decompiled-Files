/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.traversal;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.NodeFilter;

public interface NodeIterator {
    public Node getRoot();

    public int getWhatToShow();

    public NodeFilter getFilter();

    public boolean getExpandEntityReferences();

    public Node nextNode() throws DOMException;

    public Node previousNode() throws DOMException;

    public void detach();
}

