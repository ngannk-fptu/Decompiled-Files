/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.llom;

import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMSerializable;
import org.apache.axiom.om.impl.llom.OMAbstractIterator;

public class OMDescendantsIterator
extends OMAbstractIterator {
    private int level;

    public OMDescendantsIterator(OMContainer container, boolean includeSelf) {
        super(includeSelf ? container : container.getFirstOMChild());
        this.level = includeSelf ? 0 : 1;
    }

    protected OMSerializable getNextNode(OMSerializable currentNode) {
        OMNode firstChild;
        if (currentNode instanceof OMContainer && (firstChild = ((OMContainer)currentNode).getFirstOMChild()) != null) {
            ++this.level;
            return firstChild;
        }
        OMSerializable node = currentNode;
        while (this.level != 0) {
            OMNode nextSibling = ((OMNode)node).getNextOMSibling();
            if (nextSibling != null) {
                return nextSibling;
            }
            node = ((OMNode)node).getParent();
            --this.level;
        }
        return null;
    }
}

