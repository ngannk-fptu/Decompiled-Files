/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.messaging.saaj.soap.impl;

import com.sun.xml.messaging.saaj.soap.SOAPDocumentImpl;
import java.util.Objects;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class NodeListImpl
implements NodeList {
    private final SOAPDocumentImpl soapDocument;
    private final NodeList nodeList;

    public NodeListImpl(SOAPDocumentImpl soapDocument, NodeList nodeList) {
        this.soapDocument = Objects.requireNonNull(soapDocument);
        this.nodeList = Objects.requireNonNull(nodeList);
    }

    @Override
    public Node item(int index) {
        return this.soapDocument.findIfPresent(this.nodeList.item(index));
    }

    @Override
    public int getLength() {
        return this.nodeList.getLength();
    }
}

