/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.traverse;

import java.util.Iterator;
import javax.xml.namespace.QName;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.traverse.OMFilterIterator;

public class OMQNameFilterIterator
extends OMFilterIterator {
    private final QName qname;

    public OMQNameFilterIterator(Iterator parent, QName qname) {
        super(parent);
        this.qname = qname;
    }

    protected boolean matches(OMNode node) {
        return node instanceof OMElement && ((OMElement)node).getQName().equals(this.qname);
    }
}

