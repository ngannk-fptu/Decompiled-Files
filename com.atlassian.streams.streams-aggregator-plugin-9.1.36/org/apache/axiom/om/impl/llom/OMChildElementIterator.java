/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.llom;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.traverse.OMChildrenIterator;
import org.apache.axiom.om.impl.traverse.OMFilterIterator;

public class OMChildElementIterator
extends OMFilterIterator {
    public OMChildElementIterator(OMElement currentChild) {
        super(new OMChildrenIterator(currentChild));
    }

    protected boolean matches(OMNode node) {
        return node instanceof OMElement;
    }
}

