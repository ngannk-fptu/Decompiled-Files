/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.jaxp;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLDocumentSource;

class TeeXMLDocumentFilterImpl
implements XMLDocumentFilter {
    private XMLDocumentHandler next;
    private XMLDocumentHandler side;
    private XMLDocumentSource source;

    TeeXMLDocumentFilterImpl() {
    }

    public XMLDocumentHandler getSide() {
        return this.side;
    }

    public void setSide(XMLDocumentHandler xMLDocumentHandler) {
        this.side = xMLDocumentHandler;
    }

    @Override
    public XMLDocumentSource getDocumentSource() {
        return this.source;
    }

    @Override
    public void setDocumentSource(XMLDocumentSource xMLDocumentSource) {
        this.source = xMLDocumentSource;
    }

    @Override
    public XMLDocumentHandler getDocumentHandler() {
        return this.next;
    }

    @Override
    public void setDocumentHandler(XMLDocumentHandler xMLDocumentHandler) {
        this.next = xMLDocumentHandler;
    }

    @Override
    public void characters(XMLString xMLString, Augmentations augmentations) throws XNIException {
        this.side.characters(xMLString, augmentations);
        this.next.characters(xMLString, augmentations);
    }

    @Override
    public void comment(XMLString xMLString, Augmentations augmentations) throws XNIException {
        this.side.comment(xMLString, augmentations);
        this.next.comment(xMLString, augmentations);
    }

    @Override
    public void doctypeDecl(String string, String string2, String string3, Augmentations augmentations) throws XNIException {
        this.side.doctypeDecl(string, string2, string3, augmentations);
        this.next.doctypeDecl(string, string2, string3, augmentations);
    }

    @Override
    public void emptyElement(QName qName, XMLAttributes xMLAttributes, Augmentations augmentations) throws XNIException {
        this.side.emptyElement(qName, xMLAttributes, augmentations);
        this.next.emptyElement(qName, xMLAttributes, augmentations);
    }

    @Override
    public void endCDATA(Augmentations augmentations) throws XNIException {
        this.side.endCDATA(augmentations);
        this.next.endCDATA(augmentations);
    }

    @Override
    public void endDocument(Augmentations augmentations) throws XNIException {
        this.side.endDocument(augmentations);
        this.next.endDocument(augmentations);
    }

    @Override
    public void endElement(QName qName, Augmentations augmentations) throws XNIException {
        this.side.endElement(qName, augmentations);
        this.next.endElement(qName, augmentations);
    }

    @Override
    public void endGeneralEntity(String string, Augmentations augmentations) throws XNIException {
        this.side.endGeneralEntity(string, augmentations);
        this.next.endGeneralEntity(string, augmentations);
    }

    @Override
    public void ignorableWhitespace(XMLString xMLString, Augmentations augmentations) throws XNIException {
        this.side.ignorableWhitespace(xMLString, augmentations);
        this.next.ignorableWhitespace(xMLString, augmentations);
    }

    @Override
    public void processingInstruction(String string, XMLString xMLString, Augmentations augmentations) throws XNIException {
        this.side.processingInstruction(string, xMLString, augmentations);
        this.next.processingInstruction(string, xMLString, augmentations);
    }

    @Override
    public void startCDATA(Augmentations augmentations) throws XNIException {
        this.side.startCDATA(augmentations);
        this.next.startCDATA(augmentations);
    }

    @Override
    public void startDocument(XMLLocator xMLLocator, String string, NamespaceContext namespaceContext, Augmentations augmentations) throws XNIException {
        this.side.startDocument(xMLLocator, string, namespaceContext, augmentations);
        this.next.startDocument(xMLLocator, string, namespaceContext, augmentations);
    }

    @Override
    public void startElement(QName qName, XMLAttributes xMLAttributes, Augmentations augmentations) throws XNIException {
        this.side.startElement(qName, xMLAttributes, augmentations);
        this.next.startElement(qName, xMLAttributes, augmentations);
    }

    @Override
    public void startGeneralEntity(String string, XMLResourceIdentifier xMLResourceIdentifier, String string2, Augmentations augmentations) throws XNIException {
        this.side.startGeneralEntity(string, xMLResourceIdentifier, string2, augmentations);
        this.next.startGeneralEntity(string, xMLResourceIdentifier, string2, augmentations);
    }

    @Override
    public void textDecl(String string, String string2, Augmentations augmentations) throws XNIException {
        this.side.textDecl(string, string2, augmentations);
        this.next.textDecl(string, string2, augmentations);
    }

    @Override
    public void xmlDecl(String string, String string2, String string3, Augmentations augmentations) throws XNIException {
        this.side.xmlDecl(string, string2, string3, augmentations);
        this.next.xmlDecl(string, string2, string3, augmentations);
    }
}

