/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils;

import javanet.staxutils.DummyLocator;
import javanet.staxutils.StAXReaderToContentHandler;
import javanet.staxutils.helpers.XMLFilterImplEx;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public class XMLStreamReaderToContentHandler
implements StAXReaderToContentHandler {
    private final XMLStreamReader staxStreamReader;
    private XMLFilterImplEx filter;

    public XMLStreamReaderToContentHandler(XMLStreamReader staxCore, XMLFilterImplEx filter) {
        this.staxStreamReader = staxCore;
        this.filter = filter;
    }

    public void bridge() throws XMLStreamException {
        try {
            int depth = 0;
            boolean isDocument = false;
            this.handleStartDocument();
            int event = this.staxStreamReader.getEventType();
            if (event == 7) {
                isDocument = true;
                event = this.staxStreamReader.next();
                while (event != 1) {
                    switch (event) {
                        case 5: {
                            this.handleComment();
                            break;
                        }
                        case 3: {
                            this.handlePI();
                        }
                    }
                    event = this.staxStreamReader.next();
                }
            }
            if (event != 1) {
                throw new IllegalStateException("The current event is not START_ELEMENT\n but" + event);
            }
            do {
                switch (event) {
                    case 1: {
                        ++depth;
                        this.handleStartElement();
                        break;
                    }
                    case 2: {
                        this.handleEndElement();
                        --depth;
                        break;
                    }
                    case 4: {
                        this.handleCharacters();
                        break;
                    }
                    case 9: {
                        this.handleEntityReference();
                        break;
                    }
                    case 3: {
                        this.handlePI();
                        break;
                    }
                    case 5: {
                        this.handleComment();
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
                event = this.staxStreamReader.next();
            } while (depth != 0);
            if (isDocument) {
                while (event != 8) {
                    switch (event) {
                        case 5: {
                            this.handleComment();
                            break;
                        }
                        case 3: {
                            this.handlePI();
                        }
                    }
                    event = this.staxStreamReader.next();
                }
            }
            this.handleEndDocument();
        }
        catch (SAXException e) {
            throw new XMLStreamException(e);
        }
    }

    private void handleEndDocument() throws SAXException {
        this.filter.endDocument();
    }

    private void handleStartDocument() throws SAXException {
        final Location location = this.staxStreamReader.getLocation();
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

    private void handlePI() throws XMLStreamException {
        try {
            this.filter.processingInstruction(this.staxStreamReader.getPITarget(), this.staxStreamReader.getPIData());
        }
        catch (SAXException e) {
            throw new XMLStreamException(e);
        }
    }

    private void handleCharacters() throws XMLStreamException {
        int textLength = this.staxStreamReader.getTextLength();
        int textStart = this.staxStreamReader.getTextStart();
        char[] chars = new char[textLength];
        this.staxStreamReader.getTextCharacters(textStart, chars, 0, textLength);
        try {
            this.filter.characters(chars, 0, chars.length);
        }
        catch (SAXException e) {
            throw new XMLStreamException(e);
        }
    }

    private void handleEndElement() throws XMLStreamException {
        QName qName = this.staxStreamReader.getName();
        try {
            String prefix = qName.getPrefix();
            String rawname = prefix == null || prefix.length() == 0 ? qName.getLocalPart() : prefix + ':' + qName.getLocalPart();
            this.filter.endElement(qName.getNamespaceURI(), qName.getLocalPart(), rawname);
            int nsCount = this.staxStreamReader.getNamespaceCount();
            for (int i = nsCount - 1; i >= 0; --i) {
                String nsprefix = this.staxStreamReader.getNamespacePrefix(i);
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

    private void handleStartElement() throws XMLStreamException {
        try {
            int nsCount = this.staxStreamReader.getNamespaceCount();
            for (int i = 0; i < nsCount; ++i) {
                String prefix;
                String uri = this.staxStreamReader.getNamespaceURI(i);
                if (uri == null) {
                    uri = "";
                }
                if ((prefix = this.staxStreamReader.getNamespacePrefix(i)) == null) {
                    prefix = "";
                }
                this.filter.startPrefixMapping(prefix, uri);
            }
            QName qName = this.staxStreamReader.getName();
            String prefix = qName.getPrefix();
            String rawname = prefix == null || prefix.length() == 0 ? qName.getLocalPart() : prefix + ':' + qName.getLocalPart();
            Attributes attrs = this.getAttributes();
            this.filter.startElement(qName.getNamespaceURI(), qName.getLocalPart(), rawname, attrs);
        }
        catch (SAXException e) {
            throw new XMLStreamException(e);
        }
    }

    private Attributes getAttributes() {
        String uri;
        int i;
        AttributesImpl attrs = new AttributesImpl();
        int eventType = this.staxStreamReader.getEventType();
        if (eventType != 10 && eventType != 1) {
            throw new InternalError("getAttributes() attempting to process: " + eventType);
        }
        if (this.filter.getNamespacePrefixes()) {
            for (i = 0; i < this.staxStreamReader.getNamespaceCount(); ++i) {
                String prefix;
                uri = this.staxStreamReader.getNamespaceURI(i);
                if (uri == null) {
                    uri = "";
                }
                if ((prefix = this.staxStreamReader.getNamespacePrefix(i)) == null) {
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
        for (i = 0; i < this.staxStreamReader.getAttributeCount(); ++i) {
            uri = this.staxStreamReader.getAttributeNamespace(i);
            if (uri == null) {
                uri = "";
            }
            String localName = this.staxStreamReader.getAttributeLocalName(i);
            String prefix = this.staxStreamReader.getAttributePrefix(i);
            String qName = prefix == null || prefix.length() == 0 ? localName : prefix + ':' + localName;
            String type = this.staxStreamReader.getAttributeType(i);
            String value = this.staxStreamReader.getAttributeValue(i);
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

    private void handleComment() throws XMLStreamException {
        int textLength = this.staxStreamReader.getTextLength();
        int textStart = this.staxStreamReader.getTextStart();
        char[] chars = new char[textLength];
        this.staxStreamReader.getTextCharacters(textStart, chars, 0, textLength);
        try {
            this.filter.comment(chars, 0, textLength);
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

