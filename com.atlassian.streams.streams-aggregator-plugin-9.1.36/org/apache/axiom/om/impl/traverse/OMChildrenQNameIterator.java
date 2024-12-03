/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.traverse;

import javax.xml.namespace.QName;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.traverse.OMChildrenIterator;
import org.apache.axiom.om.impl.traverse.OMFilterIterator;

public class OMChildrenQNameIterator
extends OMFilterIterator {
    private final QName givenQName;

    public OMChildrenQNameIterator(OMNode currentChild, QName givenQName) {
        super(new OMChildrenIterator(currentChild));
        this.givenQName = givenQName;
    }

    public boolean isEqual(QName searchQName, QName currentQName) {
        return searchQName.equals(currentQName);
    }

    protected boolean matches(OMNode node) {
        if (node instanceof OMElement) {
            QName thisQName = ((OMElement)node).getQName();
            return this.givenQName == null || this.isEqual(this.givenQName, thisQName);
        }
        return false;
    }

    public static boolean isEquals_Legacy(QName searchQName, QName currentQName) {
        String localPart = searchQName.getLocalPart();
        boolean localNameMatch = localPart == null || localPart.equals("") || currentQName != null && currentQName.getLocalPart().equals(localPart);
        String namespaceURI = searchQName.getNamespaceURI();
        boolean namespaceURIMatch = namespaceURI.equals("") || currentQName != null && currentQName.getNamespaceURI().equals(namespaceURI);
        return localNameMatch && namespaceURIMatch;
    }
}

