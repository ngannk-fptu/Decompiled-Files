/*
 * Decompiled with CFR 0.152.
 */
package org.xml.sax;

import org.xml.sax.AttributeList;
import org.xml.sax.DTDHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class HandlerBase
implements EntityResolver,
DTDHandler,
DocumentHandler,
ErrorHandler {
    public InputSource resolveEntity(String string, String string2) throws SAXException {
        return null;
    }

    public void notationDecl(String string, String string2, String string3) {
    }

    public void unparsedEntityDecl(String string, String string2, String string3, String string4) {
    }

    public void setDocumentLocator(Locator locator) {
    }

    public void startDocument() throws SAXException {
    }

    public void endDocument() throws SAXException {
    }

    public void startElement(String string, AttributeList attributeList) throws SAXException {
    }

    public void endElement(String string) throws SAXException {
    }

    public void characters(char[] cArray, int n, int n2) throws SAXException {
    }

    public void ignorableWhitespace(char[] cArray, int n, int n2) throws SAXException {
    }

    public void processingInstruction(String string, String string2) throws SAXException {
    }

    public void warning(SAXParseException sAXParseException) throws SAXException {
    }

    public void error(SAXParseException sAXParseException) throws SAXException {
    }

    public void fatalError(SAXParseException sAXParseException) throws SAXException {
        throw sAXParseException;
    }
}

