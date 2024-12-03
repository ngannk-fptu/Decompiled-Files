/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.traverse;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.traverse.OMAbstractIterator;

public class OMDescendantsIterator
extends OMAbstractIterator {
    private int level;

    public OMDescendantsIterator(OMNode firstNode) {
        super(firstNode);
    }

    protected OMNode getNextNode(OMNode currentNode) {
        OMNode firstChild;
        if (currentNode instanceof OMContainer && (firstChild = ((OMContainer)((Object)currentNode)).getFirstOMChild()) != null) {
            ++this.level;
            return firstChild;
        }
        OMNode node = currentNode;
        OMNode nextSibling;
        while ((nextSibling = node.getNextOMSibling()) == null) {
            if (this.level == 0) {
                return null;
            }
            node = (OMNode)((Object)node.getParent());
            --this.level;
        }
        return nextSibling;
    }
}

