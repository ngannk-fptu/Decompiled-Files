/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.parsers;

import org.apache.xerces.parsers.XMLParser;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLDTDContentModelHandler;
import org.apache.xerces.xni.XMLDTDHandler;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLDTDContentModelSource;
import org.apache.xerces.xni.parser.XMLDTDSource;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xni.parser.XMLParserConfiguration;

public abstract class AbstractXMLDocumentParser
extends XMLParser
implements XMLDocumentHandler,
XMLDTDHandler,
XMLDTDContentModelHandler {
    protected boolean fInDTD;
    protected XMLDocumentSource fDocumentSource;
    protected XMLDTDSource fDTDSource;
    protected XMLDTDContentModelSource fDTDContentModelSource;

    protected AbstractXMLDocumentParser(XMLParserConfiguration xMLParserConfiguration) {
        super(xMLParserConfiguration);
        xMLParserConfiguration.setDocumentHandler(this);
        xMLParserConfiguration.setDTDHandler(this);
        xMLParserConfiguration.setDTDContentModelHandler(this);
    }

    @Override
    public void startDocument(XMLLocator xMLLocator, String string, NamespaceContext namespaceContext, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void xmlDecl(String string, String string2, String string3, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void doctypeDecl(String string, String string2, String string3, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void startElement(QName qName, XMLAttributes xMLAttributes, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void emptyElement(QName qName, XMLAttributes xMLAttributes, Augmentations augmentations) throws XNIException {
        this.startElement(qName, xMLAttributes, augmentations);
        this.endElement(qName, augmentations);
    }

    @Override
    public void characters(XMLString xMLString, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void ignorableWhitespace(XMLString xMLString, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void endElement(QName qName, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void startCDATA(Augmentations augmentations) throws XNIException {
    }

    @Override
    public void endCDATA(Augmentations augmentations) throws XNIException {
    }

    @Override
    public void endDocument(Augmentations augmentations) throws XNIException {
    }

    @Override
    public void startGeneralEntity(String string, XMLResourceIdentifier xMLResourceIdentifier, String string2, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void textDecl(String string, String string2, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void endGeneralEntity(String string, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void comment(XMLString xMLString, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void processingInstruction(String string, XMLString xMLString, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void setDocumentSource(XMLDocumentSource xMLDocumentSource) {
        this.fDocumentSource = xMLDocumentSource;
    }

    @Override
    public XMLDocumentSource getDocumentSource() {
        return this.fDocumentSource;
    }

    @Override
    public void startDTD(XMLLocator xMLLocator, Augmentations augmentations) throws XNIException {
        this.fInDTD = true;
    }

    @Override
    public void startExternalSubset(XMLResourceIdentifier xMLResourceIdentifier, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void endExternalSubset(Augmentations augmentations) throws XNIException {
    }

    @Override
    public void startParameterEntity(String string, XMLResourceIdentifier xMLResourceIdentifier, String string2, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void endParameterEntity(String string, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void ignoredCharacters(XMLString xMLString, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void elementDecl(String string, String string2, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void startAttlist(String string, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void attributeDecl(String string, String string2, String string3, String[] stringArray, String string4, XMLString xMLString, XMLString xMLString2, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void endAttlist(Augmentations augmentations) throws XNIException {
    }

    @Override
    public void internalEntityDecl(String string, XMLString xMLString, XMLString xMLString2, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void externalEntityDecl(String string, XMLResourceIdentifier xMLResourceIdentifier, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void unparsedEntityDecl(String string, XMLResourceIdentifier xMLResourceIdentifier, String string2, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void notationDecl(String string, XMLResourceIdentifier xMLResourceIdentifier, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void startConditional(short s, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void endConditional(Augmentations augmentations) throws XNIException {
    }

    @Override
    public void endDTD(Augmentations augmentations) throws XNIException {
        this.fInDTD = false;
    }

    @Override
    public void setDTDSource(XMLDTDSource xMLDTDSource) {
        this.fDTDSource = xMLDTDSource;
    }

    @Override
    public XMLDTDSource getDTDSource() {
        return this.fDTDSource;
    }

    @Override
    public void startContentModel(String string, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void any(Augmentations augmentations) throws XNIException {
    }

    @Override
    public void empty(Augmentations augmentations) throws XNIException {
    }

    @Override
    public void startGroup(Augmentations augmentations) throws XNIException {
    }

    @Override
    public void pcdata(Augmentations augmentations) throws XNIException {
    }

    @Override
    public void element(String string, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void separator(short s, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void occurrence(short s, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void endGroup(Augmentations augmentations) throws XNIException {
    }

    @Override
    public void endContentModel(Augmentations augmentations) throws XNIException {
    }

    @Override
    public void setDTDContentModelSource(XMLDTDContentModelSource xMLDTDContentModelSource) {
        this.fDTDContentModelSource = xMLDTDContentModelSource;
    }

    @Override
    public XMLDTDContentModelSource getDTDContentModelSource() {
        return this.fDTDContentModelSource;
    }

    @Override
    protected void reset() throws XNIException {
        super.reset();
        this.fInDTD = false;
    }
}

