/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.util.xml;

import org.xml.sax.AttributeList;
import org.xml.sax.DocumentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class DocumentFilter
implements DocumentHandler {
    public DocumentHandler next;

    public DocumentFilter(DocumentHandler next) {
        this.next = next;
    }

    public void startDocument() throws SAXException {
        this.next.startDocument();
    }

    public void endDocument() throws SAXException {
        this.next.endDocument();
    }

    public void startElement(String name, AttributeList atts) throws SAXException {
        this.next.startElement(name, atts);
    }

    public void endElement(String name) throws SAXException {
        this.next.endElement(name);
    }

    public void characters(char[] buf, int start, int len) throws SAXException {
        this.next.characters(buf, start, len);
    }

    public void ignorableWhitespace(char[] buf, int start, int len) throws SAXException {
        this.next.ignorableWhitespace(buf, start, len);
    }

    public void processingInstruction(String target, String data) throws SAXException {
        this.next.processingInstruction(target, data);
    }

    public void setDocumentLocator(Locator loc) {
        this.next.setDocumentLocator(loc);
    }
}

