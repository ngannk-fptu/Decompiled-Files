/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.llom;

import javax.xml.namespace.QName;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.llom.OMChildrenQNameIterator;

public class OMChildrenNamespaceIterator
extends OMChildrenQNameIterator {
    public OMChildrenNamespaceIterator(OMNode currentChild, String uri) {
        super(currentChild, new QName(uri, "dummyName"));
    }

    public boolean isEqual(QName searchQName, QName currentQName) {
        return searchQName.getNamespaceURI().equals(currentQName.getNamespaceURI());
    }
}

