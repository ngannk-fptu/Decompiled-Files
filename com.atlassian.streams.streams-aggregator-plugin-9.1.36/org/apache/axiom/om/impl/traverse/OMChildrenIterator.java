/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.traverse;

import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.traverse.OMAbstractIterator;

public class OMChildrenIterator
extends OMAbstractIterator {
    public OMChildrenIterator(OMNode currentChild) {
        super(currentChild);
    }

    protected OMNode getNextNode(OMNode currentNode) {
        return currentNode.getNextOMSibling();
    }
}

