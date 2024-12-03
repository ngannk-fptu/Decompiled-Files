/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.llom;

import javax.xml.namespace.QName;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.llom.OMChildrenQNameIterator;

public class OMChildrenLocalNameIterator
extends OMChildrenQNameIterator {
    public OMChildrenLocalNameIterator(OMNode currentChild, String localName) {
        super(currentChild, new QName("", localName));
    }

    public boolean isEqual(QName searchQName, QName currentQName) {
        return searchQName.getLocalPart().equals(currentQName.getLocalPart());
    }
}

