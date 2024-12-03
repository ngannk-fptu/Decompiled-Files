/*
 * Decompiled with CFR 0.152.
 */
package org.xml.sax.helpers;

import java.io.IOException;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;

public class XMLFilterImpl
implements XMLFilter,
EntityResolver,
DTDHandler,
ContentHandler,
ErrorHandler {
    private XMLReader parent = null;
    private Locator locator = null;
    private EntityResolver entityResolver = null;
    private DTDHandler dtdHandler = null;
    private ContentHandler contentHandler = null;
    private ErrorHandler errorHandler = null;

    public XMLFilterImpl() {
    }

    public XMLFilterImpl(XMLReader xMLReader) {
        this.setParent(xMLReader);
    }

    public void setParent(XMLReader xMLReader) {
        this.parent = xMLReader;
    }

    public XMLReader getParent() {
        return this.parent;
    }

    public void setFeature(String string, boolean bl) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (this.parent == null) {
            throw new SAXNotRecognizedException("Feature: " + string);
        }
        this.parent.setFeature(string, bl);
    }

    public boolean getFeature(String string) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (this.parent != null) {
            return this.parent.getFeature(string);
        }
        throw new SAXNotRecognizedException("Feature: " + string);
    }

    public void setProperty(String string, Object object) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (this.parent == null) {
            throw new SAXNotRecognizedException("Property: " + string);
        }
        this.parent.setProperty(string, object);
    }

    public Object getProperty(String string) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (this.parent != null) {
            return this.parent.getProperty(string);
        }
        throw new SAXNotRecognizedException("Property: " + string);
    }

    public void setEntityResolver(EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }

    public EntityResolver getEntityResolver() {
        return this.entityResolver;
    }

    public void setDTDHandler(DTDHandler dTDHandler) {
        this.dtdHandler = dTDHandler;
    }

    public DTDHandler getDTDHandler() {
        return this.dtdHandler;
    }

    public void setContentHandler(ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

    public ContentHandler getContentHandler() {
        return this.contentHandler;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public ErrorHandler getErrorHandler() {
        return this.errorHandler;
    }

    public void parse(InputSource inputSource) throws SAXException, IOException {
        this.setupParse();
        this.parent.parse(inputSource);
    }

    public void parse(String string) throws SAXException, IOException {
        this.parse(new InputSource(string));
    }

    public InputSource resolveEntity(String string, String string2) throws SAXException, IOException {
        if (this.entityResolver != null) {
            return this.entityResolver.resolveEntity(string, string2);
        }
        return null;
    }

    public void notationDecl(String string, String string2, String string3) throws SAXException {
        if (this.dtdHandler != null) {
            this.dtdHandler.notationDecl(string, string2, string3);
        }
    }

    public void unparsedEntityDecl(String string, String string2, String string3, String string4) throws SAXException {
        if (this.dtdHandler != null) {
            this.dtdHandler.unparsedEntityDecl(string, string2, string3, string4);
        }
    }

    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
        if (this.contentHandler != null) {
            this.contentHandler.setDocumentLocator(locator);
        }
    }

    public void startDocument() throws SAXException {
        if (this.contentHandler != null) {
            this.contentHandler.startDocument();
        }
    }

    public void endDocument() throws SAXException {
        if (this.contentHandler != null) {
            this.contentHandler.endDocument();
        }
    }

    public void startPrefixMapping(String string, String string2) throws SAXException {
        if (this.contentHandler != null) {
            this.contentHandler.startPrefixMapping(string, string2);
        }
    }

    public void endPrefixMapping(String string) throws SAXException {
        if (this.contentHandler != null) {
            this.contentHandler.endPrefixMapping(string);
        }
    }

    public void startElement(String string, String string2, String string3, Attributes attributes) throws SAXException {
        if (this.contentHandler != null) {
            this.contentHandler.startElement(string, string2, string3, attributes);
        }
    }

    public void endElement(String string, String string2, String string3) throws SAXException {
        if (this.contentHandler != null) {
            this.contentHandler.endElement(string, string2, string3);
        }
    }

    public void characters(char[] cArray, int n, int n2) throws SAXException {
        if (this.contentHandler != null) {
            this.contentHandler.characters(cArray, n, n2);
        }
    }

    public void ignorableWhitespace(char[] cArray, int n, int n2) throws SAXException {
        if (this.contentHandler != null) {
            this.contentHandler.ignorableWhitespace(cArray, n, n2);
        }
    }

    public void processingInstruction(String string, String string2) throws SAXException {
        if (this.contentHandler != null) {
            this.contentHandler.processingInstruction(string, string2);
        }
    }

    public void skippedEntity(String string) throws SAXException {
        if (this.contentHandler != null) {
            this.contentHandler.skippedEntity(string);
        }
    }

    public void warning(SAXParseException sAXParseException) throws SAXException {
        if (this.errorHandler != null) {
            this.errorHandler.warning(sAXParseException);
        }
    }

    public void error(SAXParseException sAXParseException) throws SAXException {
        if (this.errorHandler != null) {
            this.errorHandler.error(sAXParseException);
        }
    }

    public void fatalError(SAXParseException sAXParseException) throws SAXException {
        if (this.errorHandler != null) {
            this.errorHandler.fatalError(sAXParseException);
        }
    }

    private void setupParse() {
        if (this.parent == null) {
            throw new NullPointerException("No parent for filter");
        }
        this.parent.setEntityResolver(this);
        this.parent.setDTDHandler(this);
        this.parent.setContentHandler(this);
        this.parent.setErrorHandler(this);
    }
}

