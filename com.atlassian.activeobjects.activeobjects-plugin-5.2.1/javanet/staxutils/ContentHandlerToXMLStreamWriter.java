/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils;

import java.util.Stack;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ContentHandlerToXMLStreamWriter
extends DefaultHandler {
    private final XMLStreamWriter staxWriter;
    private final Stack prefixBindings;

    public ContentHandlerToXMLStreamWriter(XMLStreamWriter staxCore) {
        this.staxWriter = staxCore;
        this.prefixBindings = new Stack();
    }

    public void endDocument() throws SAXException {
        try {
            this.staxWriter.writeEndDocument();
            this.staxWriter.flush();
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }

    public void startDocument() throws SAXException {
        try {
            this.staxWriter.writeStartDocument();
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        try {
            this.staxWriter.writeCharacters(ch, start, length);
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        this.characters(ch, start, length);
    }

    public void endPrefixMapping(String prefix) throws SAXException {
    }

    public void skippedEntity(String name) throws SAXException {
        try {
            this.staxWriter.writeEntityRef(name);
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }

    public void setDocumentLocator(Locator locator) {
    }

    public void processingInstruction(String target, String data) throws SAXException {
        try {
            this.staxWriter.writeProcessingInstruction(target, data);
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        if (prefix.equals("xml")) {
            return;
        }
        if (prefix == null) {
            prefix = "";
        }
        this.prefixBindings.add(prefix);
        this.prefixBindings.add(uri);
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        try {
            this.staxWriter.writeEndElement();
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        try {
            this.staxWriter.writeStartElement(this.getPrefix(qName), localName, namespaceURI);
            while (this.prefixBindings.size() != 0) {
                String uri = (String)this.prefixBindings.pop();
                String prefix = (String)this.prefixBindings.pop();
                if (prefix.length() == 0) {
                    this.staxWriter.setDefaultNamespace(uri);
                } else {
                    this.staxWriter.setPrefix(prefix, uri);
                }
                this.staxWriter.writeNamespace(prefix, uri);
            }
            this.writeAttributes(atts);
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }

    private void writeAttributes(Attributes atts) throws XMLStreamException {
        for (int i = 0; i < atts.getLength(); ++i) {
            String prefix = this.getPrefix(atts.getQName(i));
            if (prefix.equals("xmlns")) continue;
            this.staxWriter.writeAttribute(prefix, atts.getURI(i), atts.getLocalName(i), atts.getValue(i));
        }
    }

    private String getPrefix(String qName) {
        int idx = qName.indexOf(58);
        if (idx == -1) {
            return "";
        }
        return qName.substring(0, idx);
    }
}

