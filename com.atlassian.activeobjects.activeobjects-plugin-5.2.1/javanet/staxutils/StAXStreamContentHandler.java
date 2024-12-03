/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils;

import java.util.Iterator;
import javanet.staxutils.StAXContentHandler;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class StAXStreamContentHandler
extends StAXContentHandler {
    private XMLStreamWriter writer;

    public StAXStreamContentHandler() {
    }

    public StAXStreamContentHandler(XMLStreamWriter writer) {
        this.writer = writer;
    }

    public XMLStreamWriter getStreamWriter() {
        return this.writer;
    }

    public void setStreamWriter(XMLStreamWriter writer) {
        this.writer = writer;
    }

    public void startDocument() throws SAXException {
        super.startDocument();
        try {
            this.writer.writeStartDocument();
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }

    public void endDocument() throws SAXException {
        try {
            this.writer.writeEndDocument();
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
        super.endDocument();
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        try {
            String[] qname = new String[]{null, null};
            StAXStreamContentHandler.parseQName(qName, qname);
            this.writer.writeStartElement(qname[0], qname[1], uri);
            if (this.namespaces != null) {
                Iterator prefixes = this.namespaces.getDeclaredPrefixes();
                while (prefixes.hasNext()) {
                    String prefix = (String)prefixes.next();
                    String nsURI = this.namespaces.getNamespaceURI(prefix);
                    if (prefix.length() == 0) {
                        this.writer.setDefaultNamespace(nsURI);
                    } else {
                        this.writer.setPrefix(prefix, nsURI);
                    }
                    this.writer.writeNamespace(prefix, nsURI);
                }
            }
            int s = attributes.getLength();
            for (int i = 0; i < s; ++i) {
                StAXStreamContentHandler.parseQName(attributes.getQName(i), qname);
                String attrPrefix = qname[0];
                String attrLocal = qname[1];
                String attrQName = attributes.getQName(i);
                String attrValue = attributes.getValue(i);
                String attrURI = attributes.getURI(i);
                if ("xmlns".equals(attrQName) || "xmlns".equals(attrPrefix)) {
                    String nsURI = this.namespaces.getNamespaceURI(attrPrefix);
                    if (nsURI != null) continue;
                    if (attrPrefix.length() == 0) {
                        this.writer.setDefaultNamespace(attrValue);
                    } else {
                        this.writer.setPrefix(attrPrefix, attrValue);
                    }
                    this.writer.writeNamespace(attrPrefix, attrValue);
                    continue;
                }
                if (attrPrefix.length() > 0) {
                    this.writer.writeAttribute(attrPrefix, attrURI, attrLocal, attrValue);
                    continue;
                }
                this.writer.writeAttribute(attrQName, attrValue);
            }
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
        finally {
            super.startElement(uri, localName, qName, attributes);
        }
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        try {
            this.writer.writeEndElement();
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
        finally {
            super.endElement(uri, localName, qName);
        }
    }

    public void comment(char[] ch, int start, int length) throws SAXException {
        super.comment(ch, start, length);
        try {
            this.writer.writeComment(new String(ch, start, length));
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        try {
            if (!this.isCDATA) {
                this.writer.writeCharacters(ch, start, length);
            }
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }

    public void endCDATA() throws SAXException {
        try {
            this.writer.writeCData(this.CDATABuffer.toString());
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
        super.endCDATA();
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        super.ignorableWhitespace(ch, start, length);
        try {
            this.writer.writeCharacters(ch, start, length);
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }

    public void processingInstruction(String target, String data) throws SAXException {
        super.processingInstruction(target, data);
        try {
            this.writer.writeProcessingInstruction(target, data);
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }
}

