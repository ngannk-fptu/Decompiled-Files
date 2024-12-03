/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils;

import javanet.staxutils.SimpleLocation;
import javanet.staxutils.StaticLocation;
import javanet.staxutils.XMLStreamUtils;
import javanet.staxutils.error.IllegalStreamStateException;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public abstract class BaseXMLStreamReader
implements XMLStreamReader {
    protected String systemId;
    protected String encoding;

    public BaseXMLStreamReader() {
    }

    public BaseXMLStreamReader(String systemId, String encoding) {
        this.systemId = systemId;
        this.encoding = encoding;
    }

    public String getSystemId() {
        return this.systemId;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public String getEventTypeName() {
        return XMLStreamUtils.getEventTypeName(this.getEventType());
    }

    public int nextTag() throws XMLStreamException {
        int eventType = this.next();
        while (this.hasNext()) {
            switch (eventType) {
                case 1: 
                case 2: {
                    return eventType;
                }
                case 4: 
                case 12: {
                    if (!this.isWhiteSpace()) break;
                }
                case 3: 
                case 5: 
                case 6: {
                    break;
                }
            }
            eventType = this.next();
        }
        throw new XMLStreamException("Encountered " + this.getEventTypeName() + " when expecting START_ELEMENT or END_ELEMENT", this.getStableLocation());
    }

    public boolean isCharacters() {
        return this.getEventType() == 4;
    }

    public boolean isEndElement() {
        return this.getEventType() == 2;
    }

    public boolean isStartElement() {
        return this.getEventType() == 1;
    }

    public boolean isWhiteSpace() {
        return this.getEventType() == 6;
    }

    public boolean hasName() {
        switch (this.getEventType()) {
            case 1: 
            case 2: {
                return true;
            }
        }
        return false;
    }

    public String getPrefix() {
        switch (this.getEventType()) {
            case 1: 
            case 2: {
                return this.getName().getPrefix();
            }
        }
        throw new IllegalStreamStateException("Expected START_ELEMENT or END_ELEMENT but was " + this.getEventTypeName(), this.getStableLocation());
    }

    public boolean hasText() {
        switch (this.getEventType()) {
            case 4: 
            case 5: 
            case 6: 
            case 9: 
            case 12: {
                return true;
            }
        }
        return false;
    }

    public String getNamespaceURI(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException("Namespace prefix was null");
        }
        return this.getNamespaceContext().getNamespaceURI(prefix);
    }

    public String getNamespaceURI() {
        switch (this.getEventType()) {
            case 1: 
            case 2: {
                return this.getName().getNamespaceURI();
            }
        }
        throw new IllegalStreamStateException("Expected START_ELEMENT or END_ELEMENT state, but found " + this.getEventTypeName(), this.getStableLocation());
    }

    public String getAttributeLocalName(int index) {
        return this.getAttributeName(index).getLocalPart();
    }

    public String getAttributeNamespace(int index) {
        return this.getAttributeName(index).getNamespaceURI();
    }

    public String getAttributePrefix(int index) {
        return this.getAttributeName(index).getPrefix();
    }

    public void require(int type, String namespaceURI, String localName) throws XMLStreamException {
        int currType = this.getEventType();
        if (currType != type) {
            throw new XMLStreamException("Expected " + XMLStreamUtils.getEventTypeName(type) + " but found " + XMLStreamUtils.getEventTypeName(currType), this.getStableLocation());
        }
    }

    public String getElementText() throws XMLStreamException {
        if (this.getEventType() != 1) {
            throw new XMLStreamException("Expected START_ELEMENT but found " + this.getEventTypeName(), this.getStableLocation());
        }
        QName elemName = this.getName();
        Location elemLocation = this.getStableLocation();
        StringBuffer content = null;
        int eventType = this.next();
        while (eventType != 2) {
            if (this.hasText()) {
                if (content == null) {
                    content = new StringBuffer();
                }
            } else {
                throw new XMLStreamException("Encountered " + this.getEventTypeName() + " event within text-only element " + elemName, elemLocation);
            }
            content.append(this.getText());
            eventType = this.next();
        }
        return content == null ? "" : content.toString();
    }

    public Location getStableLocation() {
        Location location = this.getLocation();
        if (!(location instanceof StaticLocation)) {
            location = new SimpleLocation(location);
        }
        return location;
    }
}

