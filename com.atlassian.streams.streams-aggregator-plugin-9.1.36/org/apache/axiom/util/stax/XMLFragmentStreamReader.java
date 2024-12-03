/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax;

import java.util.NoSuchElementException;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class XMLFragmentStreamReader
implements XMLStreamReader {
    private static final int STATE_START_DOCUMENT = 0;
    private static final int STATE_IN_FRAGMENT = 1;
    private static final int STATE_FRAGMENT_END = 2;
    private static final int STATE_END_DOCUMENT = 3;
    private XMLStreamReader parent;
    private int state;
    private int depth;

    public XMLFragmentStreamReader(XMLStreamReader parent) {
        this.parent = parent;
        if (parent.getEventType() != 1) {
            throw new IllegalStateException("Expected START_ELEMENT as current event");
        }
    }

    public int getEventType() {
        switch (this.state) {
            case 0: {
                return 7;
            }
            case 1: {
                return this.parent.getEventType();
            }
            case 2: {
                return 2;
            }
            case 3: {
                return 8;
            }
        }
        throw new IllegalStateException();
    }

    public int next() throws XMLStreamException {
        switch (this.state) {
            case 0: {
                this.state = 1;
                return 1;
            }
            case 1: {
                int type = this.parent.next();
                switch (type) {
                    case 1: {
                        ++this.depth;
                        break;
                    }
                    case 2: {
                        if (this.depth == 0) {
                            this.state = 2;
                            break;
                        }
                        --this.depth;
                    }
                }
                return type;
            }
            case 2: {
                this.parent.next();
                this.state = 3;
                return 8;
            }
        }
        throw new NoSuchElementException("End of document reached");
    }

    public int nextTag() throws XMLStreamException {
        switch (this.state) {
            case 0: {
                this.state = 1;
                return 1;
            }
            case 2: 
            case 3: {
                throw new NoSuchElementException();
            }
        }
        int result = this.parent.nextTag();
        switch (result) {
            case 1: {
                ++this.depth;
                break;
            }
            case 2: {
                if (this.depth == 0) {
                    this.state = 2;
                    break;
                }
                --this.depth;
            }
        }
        return result;
    }

    public void close() throws XMLStreamException {
        this.parent = null;
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        return this.parent.getProperty(name);
    }

    public String getCharacterEncodingScheme() {
        if (this.state == 0) {
            return null;
        }
        throw new IllegalStateException();
    }

    public String getEncoding() {
        if (this.state == 0) {
            return null;
        }
        throw new IllegalStateException();
    }

    public String getVersion() {
        return "1.0";
    }

    public boolean isStandalone() {
        return true;
    }

    public boolean standaloneSet() {
        return false;
    }

    public Location getLocation() {
        return this.parent.getLocation();
    }

    public int getAttributeCount() {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getAttributeCount();
    }

    public String getAttributeLocalName(int index) {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getAttributeLocalName(index);
    }

    public QName getAttributeName(int index) {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getAttributeName(index);
    }

    public String getAttributeNamespace(int index) {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getAttributeNamespace(index);
    }

    public String getAttributePrefix(int index) {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getAttributePrefix(index);
    }

    public String getAttributeType(int index) {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getAttributeType(index);
    }

    public String getAttributeValue(int index) {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getAttributeValue(index);
    }

    public boolean isAttributeSpecified(int index) {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.isAttributeSpecified(index);
    }

    public String getAttributeValue(String namespaceURI, String localName) {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getAttributeValue(namespaceURI, localName);
    }

    public String getElementText() throws XMLStreamException {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getElementText();
    }

    public String getLocalName() {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getLocalName();
    }

    public QName getName() {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getName();
    }

    public String getPrefix() {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getPrefix();
    }

    public String getNamespaceURI() {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getNamespaceURI();
    }

    public int getNamespaceCount() {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getNamespaceCount();
    }

    public String getNamespacePrefix(int index) {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getNamespacePrefix(index);
    }

    public String getNamespaceURI(int index) {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getNamespaceURI(index);
    }

    public String getNamespaceURI(String prefix) {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getNamespaceURI(prefix);
    }

    public NamespaceContext getNamespaceContext() {
        return this.parent.getNamespaceContext();
    }

    public String getPIData() {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getPIData();
    }

    public String getPITarget() {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getPITarget();
    }

    public String getText() {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getText();
    }

    public char[] getTextCharacters() {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getTextCharacters();
    }

    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.getTextCharacters(sourceStart, target, targetStart, length);
    }

    public int getTextLength() {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getTextLength();
    }

    public int getTextStart() {
        if (this.state == 0 || this.state == 3) {
            throw new IllegalStateException();
        }
        return this.parent.getTextStart();
    }

    public boolean hasName() {
        return this.state != 0 && this.state != 3 && this.parent.hasName();
    }

    public boolean hasNext() throws XMLStreamException {
        return this.state != 3;
    }

    public boolean hasText() {
        return this.state != 0 && this.state != 3 && this.parent.hasText();
    }

    public boolean isCharacters() {
        return this.state != 0 && this.state != 3 && this.parent.isCharacters();
    }

    public boolean isStartElement() {
        return this.state != 0 && this.state != 3 && this.parent.isStartElement();
    }

    public boolean isEndElement() {
        return this.state != 0 && this.state != 3 && this.parent.isEndElement();
    }

    public boolean isWhiteSpace() {
        return this.state != 0 && this.state != 3 && this.parent.isWhiteSpace();
    }

    public void require(int type, String namespaceURI, String localName) throws XMLStreamException {
        switch (this.state) {
            case 0: {
                if (type == 7) break;
                throw new XMLStreamException("Expected START_DOCUMENT");
            }
            case 3: {
                if (type == 8) break;
                throw new XMLStreamException("Expected END_DOCUMENT");
            }
            default: {
                this.parent.require(type, namespaceURI, localName);
            }
        }
    }
}

