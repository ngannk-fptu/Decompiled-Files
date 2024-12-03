/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xerces.util.XML11Char
 *  org.apache.xerces.util.XMLSymbols
 */
package org.outerj.daisy.diff.helper;

import org.apache.xerces.util.XML11Char;
import org.apache.xerces.util.XMLSymbols;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

class CleanBrokenAttributeQNamesHandler
implements ContentHandler {
    private ContentHandler consumer;

    public CleanBrokenAttributeQNamesHandler(ContentHandler consumer) {
        this.consumer = consumer;
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        this.consumer.characters(ch, start, length);
    }

    @Override
    public void endDocument() throws SAXException {
        this.consumer.endDocument();
    }

    @Override
    public void startDocument() throws SAXException {
        this.consumer.startDocument();
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        this.consumer.ignorableWhitespace(ch, start, length);
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        this.consumer.endPrefixMapping(prefix);
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        this.consumer.skippedEntity(name);
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        this.consumer.setDocumentLocator(locator);
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        this.consumer.processingInstruction(target, data);
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        this.consumer.startPrefixMapping(prefix, uri);
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        this.consumer.endElement(namespaceURI, localName, qName);
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        this.consumer.startElement(namespaceURI, localName, qName, CleanBrokenAttributeQNamesHandler.filterBrokenQNameAttributes(atts));
    }

    private static Attributes filterBrokenQNameAttributes(Attributes atts) {
        AttributesImpl filtered = new AttributesImpl();
        int l = atts.getLength();
        for (int i = 0; i < l; ++i) {
            String localName = atts.getLocalName(i);
            if (!XML11Char.isXML11ValidNCName((String)localName) || localName.equals(XMLSymbols.PREFIX_XMLNS)) continue;
            filtered.addAttribute(atts.getURI(i), localName, atts.getQName(i), atts.getType(i), atts.getValue(i));
        }
        return filtered;
    }
}

