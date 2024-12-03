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
import org.xml.sax.SAXParseException;

public class DefaultHandler
implements EntityResolver,
DTDHandler,
ContentHandler,
ErrorHandler {
    public InputSource resolveEntity(String string, String string2) throws IOException, SAXException {
        return null;
    }

    public void notationDecl(String string, String string2, String string3) throws SAXException {
    }

    public void unparsedEntityDecl(String string, String string2, String string3, String string4) throws SAXException {
    }

    public void setDocumentLocator(Locator locator) {
    }

    public void startDocument() throws SAXException {
    }

    public void endDocument() throws SAXException {
    }

    public void startPrefixMapping(String string, String string2) throws SAXException {
    }

    public void endPrefixMapping(String string) throws SAXException {
    }

    public void startElement(String string, String string2, String string3, Attributes attributes) throws SAXException {
    }

    public void endElement(String string, String string2, String string3) throws SAXException {
    }

    public void characters(char[] cArray, int n, int n2) throws SAXException {
    }

    public void ignorableWhitespace(char[] cArray, int n, int n2) throws SAXException {
    }

    public void processingInstruction(String string, String string2) throws SAXException {
    }

    public void skippedEntity(String string) throws SAXException {
    }

    public void warning(SAXParseException sAXParseException) throws SAXException {
    }

    public void error(SAXParseException sAXParseException) throws SAXException {
    }

    public void fatalError(SAXParseException sAXParseException) throws SAXException {
        throw sAXParseException;
    }
}

