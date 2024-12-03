/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.traverse;

import java.util.Iterator;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.traverse.OMFilterIterator;

public class OMQualifiedNameFilterIterator
extends OMFilterIterator {
    private final String prefix;
    private final String localName;

    public OMQualifiedNameFilterIterator(Iterator parent, String qualifiedName) {
        super(parent);
        int idx = qualifiedName.indexOf(58);
        if (idx == -1) {
            this.prefix = null;
            this.localName = qualifiedName;
        } else {
            this.prefix = qualifiedName.substring(0, idx);
            this.localName = qualifiedName.substring(idx + 1);
        }
    }

    protected boolean matches(OMNode node) {
        if (node instanceof OMElement) {
            OMElement element = (OMElement)node;
            if (!this.localName.equals(element.getLocalName())) {
                return false;
            }
            OMNamespace ns = ((OMElement)node).getNamespace();
            if (this.prefix == null) {
                return ns == null || ns.getPrefix().length() == 0;
            }
            return ns != null && this.prefix.equals(ns.getPrefix());
        }
        return false;
    }
}

