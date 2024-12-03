/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.messaging.saaj.soap.impl;

import com.sun.xml.messaging.saaj.soap.SOAPDocumentImpl;
import java.util.Objects;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class NamedNodeMapImpl
implements NamedNodeMap {
    private final NamedNodeMap namedNodeMap;
    private final SOAPDocumentImpl soapDocument;

    public NamedNodeMapImpl(NamedNodeMap namedNodeMap, SOAPDocumentImpl soapDocument) {
        Objects.requireNonNull(namedNodeMap);
        Objects.requireNonNull(soapDocument);
        this.namedNodeMap = namedNodeMap;
        this.soapDocument = soapDocument;
    }

    @Override
    public Node getNamedItem(String name) {
        return this.soapDocument.findIfPresent(this.namedNodeMap.getNamedItem(name));
    }

    @Override
    public Node setNamedItem(Node arg) throws DOMException {
        return this.namedNodeMap.setNamedItem(arg);
    }

    @Override
    public Node removeNamedItem(String name) throws DOMException {
        return this.namedNodeMap.removeNamedItem(name);
    }

    @Override
    public Node item(int index) {
        return this.soapDocument.findIfPresent(this.namedNodeMap.item(index));
    }

    @Override
    public int getLength() {
        return this.namedNodeMap.getLength();
    }

    @Override
    public Node getNamedItemNS(String namespaceURI, String localName) throws DOMException {
        return this.soapDocument.findIfPresent(this.namedNodeMap.getNamedItemNS(namespaceURI, localName));
    }

    @Override
    public Node setNamedItemNS(Node arg) throws DOMException {
        return this.namedNodeMap.setNamedItemNS(arg);
    }

    @Override
    public Node removeNamedItemNS(String namespaceURI, String localName) throws DOMException {
        return this.namedNodeMap.removeNamedItemNS(namespaceURI, localName);
    }
}

