/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.llom;

import org.apache.axiom.om.NodeUnavailableException;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.builder.StAXBuilder;
import org.apache.axiom.om.impl.llom.IChildNode;
import org.apache.axiom.om.impl.llom.IParentNode;
import org.apache.axiom.om.impl.llom.OMContainerHelper;

public final class OMNodeHelper {
    private OMNodeHelper() {
    }

    public static OMNode getNextOMSibling(IChildNode node) throws OMException {
        IParentNode parent;
        OMNode nextSibling = node.getNextOMSiblingIfAvailable();
        if (nextSibling == null && (parent = node.getIParentNode()) != null && parent.getBuilder() != null) {
            switch (parent.getState()) {
                case 2: {
                    ((StAXBuilder)parent.getBuilder()).debugDiscarded(parent);
                    throw new NodeUnavailableException();
                }
                case 0: {
                    do {
                        OMContainerHelper.buildNext(parent);
                    } while (parent.getState() == 0 && (nextSibling = node.getNextOMSiblingIfAvailable()) == null);
                }
            }
        }
        return nextSibling;
    }
}

