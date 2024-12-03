/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.xml;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DefaultContentHandler
extends DefaultHandler {
    private final ContentHandler handler;

    public DefaultContentHandler(ContentHandler handler) {
        this.handler = handler;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        this.handler.characters(ch, start, length);
    }

    @Override
    public void endDocument() throws SAXException {
        this.handler.endDocument();
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        this.handler.endElement(namespaceURI, localName, qName);
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        this.handler.endPrefixMapping(prefix);
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        this.handler.ignorableWhitespace(ch, start, length);
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        this.handler.processingInstruction(target, data);
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        this.handler.setDocumentLocator(locator);
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        this.handler.skippedEntity(name);
    }

    @Override
    public void startDocument() throws SAXException {
        this.handler.startDocument();
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        this.handler.startElement(namespaceURI, localName, qName, atts);
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        this.handler.startPrefixMapping(prefix, uri);
    }
}

