/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.sax;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ContentHandlerDecorator
extends DefaultHandler {
    private ContentHandler handler;

    public ContentHandlerDecorator(ContentHandler handler) {
        assert (handler != null);
        this.handler = handler;
    }

    protected ContentHandlerDecorator() {
        this(new DefaultHandler());
    }

    protected void setContentHandler(ContentHandler handler) {
        assert (handler != null);
        this.handler = handler;
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        try {
            this.handler.startPrefixMapping(prefix, uri);
        }
        catch (SAXException e) {
            this.handleException(e);
        }
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        try {
            this.handler.endPrefixMapping(prefix);
        }
        catch (SAXException e) {
            this.handleException(e);
        }
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        try {
            this.handler.processingInstruction(target, data);
        }
        catch (SAXException e) {
            this.handleException(e);
        }
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        this.handler.setDocumentLocator(locator);
    }

    @Override
    public void startDocument() throws SAXException {
        try {
            this.handler.startDocument();
        }
        catch (SAXException e) {
            this.handleException(e);
        }
    }

    @Override
    public void endDocument() throws SAXException {
        try {
            this.handler.endDocument();
        }
        catch (SAXException e) {
            this.handleException(e);
        }
    }

    @Override
    public void startElement(String uri, String localName, String name, Attributes atts) throws SAXException {
        try {
            this.handler.startElement(uri, localName, name, atts);
        }
        catch (SAXException e) {
            this.handleException(e);
        }
    }

    @Override
    public void endElement(String uri, String localName, String name) throws SAXException {
        try {
            this.handler.endElement(uri, localName, name);
        }
        catch (SAXException e) {
            this.handleException(e);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        try {
            this.handler.characters(ch, start, length);
        }
        catch (SAXException e) {
            this.handleException(e);
        }
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        try {
            this.handler.ignorableWhitespace(ch, start, length);
        }
        catch (SAXException e) {
            this.handleException(e);
        }
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        try {
            this.handler.skippedEntity(name);
        }
        catch (SAXException e) {
            this.handleException(e);
        }
    }

    public String toString() {
        return this.handler.toString();
    }

    protected void handleException(SAXException exception) throws SAXException {
        throw exception;
    }
}

