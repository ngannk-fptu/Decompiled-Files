/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff.helper;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

class MergeCharacterEventsHandler
implements ContentHandler {
    private ContentHandler consumer;
    private char[] ch;
    private int start = 0;
    private int length = 0;

    public MergeCharacterEventsHandler(ContentHandler consumer) {
        this.consumer = consumer;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        char[] newCh = new char[this.length + length];
        if (this.ch != null) {
            System.arraycopy(this.ch, this.start, newCh, 0, this.length);
        }
        System.arraycopy(ch, start, newCh, this.length, length);
        this.start = 0;
        this.length = newCh.length;
        this.ch = newCh;
    }

    private void flushCharacters() throws SAXException {
        if (this.ch != null) {
            this.consumer.characters(this.ch, this.start, this.length);
            this.ch = null;
            this.start = 0;
            this.length = 0;
        }
    }

    @Override
    public void endDocument() throws SAXException {
        this.flushCharacters();
        this.consumer.endDocument();
    }

    @Override
    public void startDocument() throws SAXException {
        this.flushCharacters();
        this.consumer.startDocument();
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        this.flushCharacters();
        this.consumer.ignorableWhitespace(ch, start, length);
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        this.flushCharacters();
        this.consumer.endPrefixMapping(prefix);
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        this.flushCharacters();
        this.consumer.skippedEntity(name);
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        this.consumer.setDocumentLocator(locator);
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        this.flushCharacters();
        this.consumer.processingInstruction(target, data);
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        this.flushCharacters();
        this.consumer.startPrefixMapping(prefix, uri);
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        this.flushCharacters();
        this.consumer.endElement(namespaceURI, localName, qName);
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        this.flushCharacters();
        this.consumer.startElement(namespaceURI, localName, qName, atts);
    }
}

