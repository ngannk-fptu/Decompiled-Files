/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv.org_isorelax.verifier.impl;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class ForkContentHandler
implements ContentHandler {
    private final ContentHandler lhs;
    private final ContentHandler rhs;

    public ForkContentHandler(ContentHandler contenthandler, ContentHandler contenthandler1) {
        this.lhs = contenthandler;
        this.rhs = contenthandler1;
    }

    public static ContentHandler create(ContentHandler[] acontenthandler) {
        if (acontenthandler.length == 0) {
            throw new IllegalArgumentException();
        }
        ContentHandler obj = acontenthandler[0];
        for (int i = 1; i < acontenthandler.length; ++i) {
            obj = new ForkContentHandler(obj, acontenthandler[i]);
        }
        return obj;
    }

    public void setDocumentLocator(Locator locator) {
        this.lhs.setDocumentLocator(locator);
        this.rhs.setDocumentLocator(locator);
    }

    public void startDocument() throws SAXException {
        this.lhs.startDocument();
        this.rhs.startDocument();
    }

    public void endDocument() throws SAXException {
        this.lhs.endDocument();
        this.rhs.endDocument();
    }

    public void startPrefixMapping(String s, String s1) throws SAXException {
        this.lhs.startPrefixMapping(s, s1);
        this.rhs.startPrefixMapping(s, s1);
    }

    public void endPrefixMapping(String s) throws SAXException {
        this.lhs.endPrefixMapping(s);
        this.rhs.endPrefixMapping(s);
    }

    public void startElement(String s, String s1, String s2, Attributes attributes) throws SAXException {
        this.lhs.startElement(s, s1, s2, attributes);
        this.rhs.startElement(s, s1, s2, attributes);
    }

    public void endElement(String s, String s1, String s2) throws SAXException {
        this.lhs.endElement(s, s1, s2);
        this.rhs.endElement(s, s1, s2);
    }

    public void characters(char[] ac, int i, int j) throws SAXException {
        this.lhs.characters(ac, i, j);
        this.rhs.characters(ac, i, j);
    }

    public void ignorableWhitespace(char[] ac, int i, int j) throws SAXException {
        this.lhs.ignorableWhitespace(ac, i, j);
        this.rhs.ignorableWhitespace(ac, i, j);
    }

    public void processingInstruction(String s, String s1) throws SAXException {
        this.lhs.processingInstruction(s, s1);
        this.rhs.processingInstruction(s, s1);
    }

    public void skippedEntity(String s) throws SAXException {
        this.lhs.skippedEntity(s);
        this.rhs.skippedEntity(s);
    }
}

