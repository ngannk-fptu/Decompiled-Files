/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.traverse;

import javax.xml.namespace.QName;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.traverse.OMChildrenQNameIterator;

public class OMChildrenLegacyQNameIterator
extends OMChildrenQNameIterator {
    public OMChildrenLegacyQNameIterator(OMNode currentChild, QName qName) {
        super(currentChild, qName);
    }

    public boolean isEqual(QName searchQName, QName currentQName) {
        String localPart = searchQName.getLocalPart();
        boolean localNameMatch = localPart == null || localPart.equals("") || currentQName != null && currentQName.getLocalPart().equals(localPart);
        String namespaceURI = searchQName.getNamespaceURI();
        boolean namespaceURIMatch = namespaceURI.equals("") || currentQName != null && currentQName.getNamespaceURI().equals(namespaceURI);
        return localNameMatch && namespaceURIMatch;
    }
}

