/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils;

import java.util.Iterator;
import javanet.staxutils.DummyLocator;
import javanet.staxutils.StAXReaderToContentHandler;
import javanet.staxutils.helpers.XMLFilterImplEx;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class XMLEventReaderToContentHandler
implements StAXReaderToContentHandler {
    private final XMLEventReader staxEventReader;
    private XMLFilterImplEx filter;

    public XMLEventReaderToContentHandler(XMLEventReader staxCore, XMLFilterImplEx filter) {
        this.staxEventReader = staxCore;
        this.filter = filter;
    }

    public void bridge() throws XMLStreamException {
        try {
            int depth = 0;
            XMLEvent event = this.staxEventReader.peek();
            boolean readWhole = false;
            if (event.isStartDocument()) {
                readWhole = true;
            } else if (!event.isStartElement()) {
                throw new IllegalStateException();
            }
            while (!(event = this.staxEventReader.nextEvent()).isStartElement()) {
            }
            this.handleStartDocument(event);
            block18: while (true) {
                switch (event.getEventType()) {
                    case 1: {
                        ++depth;
                        this.handleStartElement(event.asStartElement());
                        break;
                    }
                    case 2: {
                        this.handleEndElement(event.asEndElement());
                        if (--depth != 0) break;
                        break block18;
                    }
                    case 4: {
                        this.handleCharacters(event.asCharacters());
                        break;
                    }
                    case 9: {
                        this.handleEntityReference();
                        break;
                    }
                    case 3: {
                        this.handlePI((ProcessingInstruction)event);
                        break;
                    }
                    case 5: {
                        this.handleComment((Comment)event);
                        break;
                    }
                    case 11: {
                        this.handleDTD();
                        break;
                    }
                    case 10: {
                        this.handleAttribute();
                        break;
                    }
                    case 13: {
                        this.handleNamespace();
                        break;
                    }
                    case 12: {
                        this.handleCDATA();
                        break;
                    }
                    case 15: {
                        this.handleEntityDecl();
                        break;
                    }
                    case 14: {
                        this.handleNotationDecl();
                        break;
                    }
                    case 6: {
                        this.handleSpace();
                        break;
                    }
                    default: {
                        throw new InternalError("processing event: " + event);
                    }
                }
                event = this.staxEventReader.nextEvent();
            }
            this.handleEndDocument();
            if (readWhole) {
                while (this.staxEventReader.hasNext()) {
                    this.staxEventReader.nextEvent();
                }
            }
        }
        catch (SAXException e) {
            throw new XMLStreamException(e);
        }
    }

    private void handleEndDocument() throws SAXException {
        this.filter.endDocument();
    }

    private void handleStartDocument(XMLEvent event) throws SAXException {
        final Location location = event.getLocation();
        if (location != null) {
            this.filter.setDocumentLocator(new Locator(){

                public int getColumnNumber() {
                    return location.getColumnNumber();
                }

                public int getLineNumber() {
                    return location.getLineNumber();
                }

                public String getPublicId() {
                    return location.getPublicId();
                }

                public String getSystemId() {
                    return location.getSystemId();
                }
            });
        } else {
            this.filter.setDocumentLocator(new DummyLocator());
        }
        this.filter.startDocument();
    }

    private void handlePI(ProcessingInstruction event) throws XMLStreamException {
        try {
            this.filter.processingInstruction(event.getTarget(), event.getData());
        }
        catch (SAXException e) {
            throw new XMLStreamException(e);
        }
    }

    private void handleCharacters(Characters event) throws XMLStreamException {
        try {
            this.filter.characters(event.getData().toCharArray(), 0, event.getData().length());
        }
        catch (SAXException e) {
            throw new XMLStreamException(e);
        }
    }

    private void handleEndElement(EndElement event) throws XMLStreamException {
        QName qName = event.getName();
        try {
            String prefix = qName.getPrefix();
            String rawname = prefix == null || prefix.length() == 0 ? qName.getLocalPart() : prefix + ':' + qName.getLocalPart();
            this.filter.endElement(qName.getNamespaceURI(), qName.getLocalPart(), rawname);
            Iterator<Namespace> i = event.getNamespaces();
            while (i.hasNext()) {
                String nsprefix = i.next().getPrefix();
                if (nsprefix == null) {
                    nsprefix = "";
                }
                this.filter.endPrefixMapping(nsprefix);
            }
        }
        catch (SAXException e) {
            throw new XMLStreamException(e);
        }
    }

    private void handleStartElement(StartElement event) throws XMLStreamException {
        try {
            String prefix;
            Iterator<Namespace> i = event.getNamespaces();
            while (i.hasNext()) {
                prefix = i.next().getPrefix();
                if (prefix == null) {
                    prefix = "";
                }
                this.filter.startPrefixMapping(prefix, event.getNamespaceURI(prefix));
            }
            QName qName = event.getName();
            prefix = qName.getPrefix();
            String rawname = prefix == null || prefix.length() == 0 ? qName.getLocalPart() : prefix + ':' + qName.getLocalPart();
            Attributes saxAttrs = this.getAttributes(event);
            this.filter.startElement(qName.getNamespaceURI(), qName.getLocalPart(), rawname, saxAttrs);
        }
        catch (SAXException e) {
            throw new XMLStreamException(e);
        }
    }

    private Attributes getAttributes(StartElement event) {
        String uri;
        Iterator<Attribute> i;
        AttributesImpl attrs = new AttributesImpl();
        if (!event.isStartElement()) {
            throw new InternalError("getAttributes() attempting to process: " + event);
        }
        if (this.filter.getNamespacePrefixes()) {
            i = event.getNamespaces();
            while (i.hasNext()) {
                String prefix;
                Namespace staxNamespace = (Namespace)i.next();
                uri = staxNamespace.getNamespaceURI();
                if (uri == null) {
                    uri = "";
                }
                if ((prefix = staxNamespace.getPrefix()) == null) {
                    prefix = "";
                }
                String qName = "xmlns";
                if (prefix.length() == 0) {
                    prefix = qName;
                } else {
                    qName = qName + ':' + prefix;
                }
                attrs.addAttribute("http://www.w3.org/2000/xmlns/", prefix, qName, "CDATA", uri);
            }
        }
        i = event.getAttributes();
        while (i.hasNext()) {
            Attribute staxAttr = i.next();
            uri = staxAttr.getName().getNamespaceURI();
            if (uri == null) {
                uri = "";
            }
            String localName = staxAttr.getName().getLocalPart();
            String prefix = staxAttr.getName().getPrefix();
            String qName = prefix == null || prefix.length() == 0 ? localName : prefix + ':' + localName;
            String type = staxAttr.getDTDType();
            String value = staxAttr.getValue();
            attrs.addAttribute(uri, localName, qName, type, value);
        }
        return attrs;
    }

    private void handleNamespace() {
    }

    private void handleAttribute() {
    }

    private void handleDTD() {
    }

    private void handleComment(Comment comment) throws XMLStreamException {
        try {
            String text = comment.getText();
            this.filter.comment(text.toCharArray(), 0, text.length());
        }
        catch (SAXException e) {
            throw new XMLStreamException(e);
        }
    }

    private void handleEntityReference() {
    }

    private void handleSpace() {
    }

    private void handleNotationDecl() {
    }

    private void handleEntityDecl() {
    }

    private void handleCDATA() {
    }
}

