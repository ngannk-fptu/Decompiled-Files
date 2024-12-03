/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.axiom.om.impl.serialize;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class StreamWriterToContentHandlerConverter
implements ContentHandler {
    private static final Log log = LogFactory.getLog(StreamWriterToContentHandlerConverter.class);
    private XMLStreamWriter writer;

    public StreamWriterToContentHandlerConverter(XMLStreamWriter writer) {
        this.writer = writer;
    }

    public void endDocument() throws SAXException {
    }

    public void startDocument() throws SAXException {
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        try {
            this.writer.writeCharacters(ch, start, length);
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }

    public void endPrefixMapping(String prefix) throws SAXException {
    }

    public void skippedEntity(String name) throws SAXException {
    }

    public void setDocumentLocator(Locator locator) {
    }

    public void processingInstruction(String target, String data) throws SAXException {
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        try {
            this.writer.writeNamespace(prefix, uri);
            this.writer.setPrefix(prefix, uri);
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        try {
            this.writer.writeEndElement();
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }

    private String getPrefix(String qName) {
        if (qName != null) {
            return qName.substring(0, qName.indexOf(":"));
        }
        return null;
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        try {
            log.info((Object)("writing element {" + namespaceURI + '}' + localName + " directly to stream "));
            String prefix = this.getPrefix(qName);
            if (prefix == null) {
                this.writer.writeStartElement(namespaceURI, localName);
            } else {
                this.writer.writeStartElement(prefix, localName, namespaceURI);
            }
            if (atts != null) {
                int attCount = atts.getLength();
                for (int i = 0; i < attCount; ++i) {
                    this.writer.writeAttribute(atts.getURI(i), localName, atts.getValue(i));
                }
            }
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }
}

