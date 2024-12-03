/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils;

import java.util.HashMap;
import java.util.Stack;
import javanet.staxutils.EntityDeclarationImpl;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ContentHandlerToXMLEventWriter
extends DefaultHandler {
    private final XMLEventWriter staxWriter;
    private final XMLEventFactory staxEventFactory;
    private Locator locator = null;
    private Location location = null;
    private final Stack prefixBindings;
    private final HashMap entityMap;

    public ContentHandlerToXMLEventWriter(XMLEventWriter staxCore) {
        this.staxWriter = staxCore;
        this.staxEventFactory = XMLEventFactory.newInstance();
        this.prefixBindings = new Stack();
        this.entityMap = new HashMap();
    }

    public void endDocument() throws SAXException {
        try {
            this.staxWriter.add(this.staxEventFactory.createEndDocument());
            this.staxWriter.flush();
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }

    public void startDocument() throws SAXException {
        try {
            this.staxWriter.add(this.staxEventFactory.createStartDocument());
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        try {
            Characters event = this.staxEventFactory.createCharacters(new String(ch, start, length));
            this.staxWriter.add(event);
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

    public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException {
        this.entityMap.put(name, new EntityDeclarationImpl(this.location, name, publicId, systemId, notationName, null));
    }

    public void skippedEntity(String name) throws SAXException {
        try {
            this.staxWriter.add(this.staxEventFactory.createEntityReference(name, (EntityDeclarationImpl)this.entityMap.get(name)));
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }

    public void setDocumentLocator(final Locator locator) {
        this.locator = locator;
        this.staxEventFactory.setLocation(new Location(){

            public int getLineNumber() {
                return locator.getLineNumber();
            }

            public int getColumnNumber() {
                return locator.getColumnNumber();
            }

            public int getCharacterOffset() {
                return -1;
            }

            public String getPublicId() {
                return locator.getPublicId();
            }

            public String getSystemId() {
                return locator.getSystemId();
            }
        });
    }

    public void processingInstruction(String target, String data) throws SAXException {
        try {
            this.staxWriter.add(this.staxEventFactory.createProcessingInstruction(target, data));
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
            this.staxWriter.add(this.staxEventFactory.createEndElement(this.getPrefix(qName), namespaceURI, localName));
        }
        catch (XMLStreamException e) {
            throw new SAXException(e);
        }
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        try {
            this.staxWriter.add(this.staxEventFactory.createStartElement(this.getPrefix(qName), namespaceURI, localName));
            while (this.prefixBindings.size() != 0) {
                String uri = (String)this.prefixBindings.pop();
                String prefix = (String)this.prefixBindings.pop();
                if (prefix.length() == 0) {
                    this.staxWriter.setDefaultNamespace(uri);
                } else {
                    this.staxWriter.setPrefix(prefix, uri);
                }
                if (prefix == null || "".equals(prefix) || "xmlns".equals(prefix)) {
                    this.staxWriter.add(this.staxEventFactory.createNamespace(uri));
                    continue;
                }
                this.staxWriter.add(this.staxEventFactory.createNamespace(prefix, uri));
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
            this.staxWriter.add(this.staxEventFactory.createAttribute(prefix, atts.getURI(i), atts.getLocalName(i), atts.getValue(i)));
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

