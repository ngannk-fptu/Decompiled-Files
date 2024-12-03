/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.axiom.util.namespace.MapBasedNamespaceContext;
import org.apache.axiom.util.stax.DummyLocation;

public class WrappedTextNodeStreamReader
implements XMLStreamReader {
    private final QName wrapperElementName;
    private final Reader reader;
    private final int chunkSize;
    private int eventType = 7;
    private char[] charData;
    private int charDataLength;
    private NamespaceContext namespaceContext;

    public WrappedTextNodeStreamReader(QName wrapperElementName, Reader reader, int chunkSize) {
        this.wrapperElementName = wrapperElementName;
        this.reader = reader;
        this.chunkSize = chunkSize;
    }

    public WrappedTextNodeStreamReader(QName wrapperElementName, Reader reader) {
        this(wrapperElementName, reader, 4096);
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        return null;
    }

    public boolean hasNext() throws XMLStreamException {
        return this.eventType != 8;
    }

    public int next() throws XMLStreamException {
        switch (this.eventType) {
            case 7: {
                this.eventType = 1;
                break;
            }
            case 1: {
                this.charData = new char[this.chunkSize];
            }
            case 4: {
                try {
                    this.charDataLength = this.reader.read(this.charData);
                }
                catch (IOException ex) {
                    throw new XMLStreamException(ex);
                }
                if (this.charDataLength == -1) {
                    this.charData = null;
                    this.eventType = 2;
                    break;
                }
                this.eventType = 4;
                break;
            }
            case 2: {
                this.eventType = 8;
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
        return this.eventType;
    }

    public int nextTag() throws XMLStreamException {
        throw new XMLStreamException("Current event is not white space");
    }

    public int getEventType() {
        return this.eventType;
    }

    public boolean isStartElement() {
        return this.eventType == 1;
    }

    public boolean isEndElement() {
        return this.eventType == 2;
    }

    public boolean isCharacters() {
        return this.eventType == 4;
    }

    public boolean isWhiteSpace() {
        return false;
    }

    public boolean hasText() {
        return this.eventType == 4;
    }

    public boolean hasName() {
        return this.eventType == 1 || this.eventType == 2;
    }

    public void require(int type, String namespaceURI, String localName) throws XMLStreamException {
        if (type != this.eventType || namespaceURI != null && !namespaceURI.equals(this.getNamespaceURI()) || localName != null && !namespaceURI.equals(this.getLocalName())) {
            throw new XMLStreamException("Unexpected event type");
        }
    }

    public Location getLocation() {
        return DummyLocation.INSTANCE;
    }

    public void close() throws XMLStreamException {
        try {
            this.reader.close();
        }
        catch (IOException ex) {
            throw new XMLStreamException(ex);
        }
    }

    public String getEncoding() {
        return null;
    }

    public String getCharacterEncodingScheme() {
        return null;
    }

    public String getVersion() {
        return null;
    }

    public boolean standaloneSet() {
        return false;
    }

    public boolean isStandalone() {
        return true;
    }

    public NamespaceContext getNamespaceContext() {
        if (this.namespaceContext == null) {
            this.namespaceContext = new MapBasedNamespaceContext(Collections.singletonMap(this.wrapperElementName.getPrefix(), this.wrapperElementName.getNamespaceURI()));
        }
        return this.namespaceContext;
    }

    public String getNamespaceURI(String prefix) {
        String namespaceURI = this.getNamespaceContext().getNamespaceURI(prefix);
        return namespaceURI.equals("") ? null : prefix;
    }

    private void checkStartElement() {
        if (this.eventType != 1) {
            throw new IllegalStateException();
        }
    }

    public String getAttributeValue(String namespaceURI, String localName) {
        this.checkStartElement();
        return null;
    }

    public int getAttributeCount() {
        this.checkStartElement();
        return 0;
    }

    public QName getAttributeName(int index) {
        this.checkStartElement();
        throw new ArrayIndexOutOfBoundsException();
    }

    public String getAttributeLocalName(int index) {
        this.checkStartElement();
        throw new ArrayIndexOutOfBoundsException();
    }

    public String getAttributePrefix(int index) {
        this.checkStartElement();
        throw new ArrayIndexOutOfBoundsException();
    }

    public String getAttributeNamespace(int index) {
        this.checkStartElement();
        throw new ArrayIndexOutOfBoundsException();
    }

    public String getAttributeType(int index) {
        this.checkStartElement();
        throw new ArrayIndexOutOfBoundsException();
    }

    public String getAttributeValue(int index) {
        this.checkStartElement();
        throw new ArrayIndexOutOfBoundsException();
    }

    public boolean isAttributeSpecified(int index) {
        this.checkStartElement();
        throw new ArrayIndexOutOfBoundsException();
    }

    private void checkElement() {
        if (this.eventType != 1 && this.eventType != 2) {
            throw new IllegalStateException();
        }
    }

    public QName getName() {
        return null;
    }

    public String getLocalName() {
        this.checkElement();
        return this.wrapperElementName.getLocalPart();
    }

    public String getPrefix() {
        return this.wrapperElementName.getPrefix();
    }

    public String getNamespaceURI() {
        this.checkElement();
        return this.wrapperElementName.getNamespaceURI();
    }

    public int getNamespaceCount() {
        this.checkElement();
        return 1;
    }

    public String getNamespacePrefix(int index) {
        this.checkElement();
        if (index == 0) {
            return this.wrapperElementName.getPrefix();
        }
        throw new IndexOutOfBoundsException();
    }

    public String getNamespaceURI(int index) {
        this.checkElement();
        if (index == 0) {
            return this.wrapperElementName.getNamespaceURI();
        }
        throw new IndexOutOfBoundsException();
    }

    public String getElementText() throws XMLStreamException {
        if (this.eventType == 1) {
            try {
                int c;
                StringBuffer buffer = new StringBuffer();
                char[] cbuf = new char[4096];
                while ((c = this.reader.read(cbuf)) != -1) {
                    buffer.append(cbuf, 0, c);
                }
                this.eventType = 2;
                return buffer.toString();
            }
            catch (IOException ex) {
                throw new XMLStreamException(ex);
            }
        }
        throw new XMLStreamException("Current event is not a START_ELEMENT");
    }

    private void checkCharacters() {
        if (this.eventType != 4) {
            throw new IllegalStateException();
        }
    }

    public String getText() {
        this.checkCharacters();
        return new String(this.charData, 0, this.charDataLength);
    }

    public char[] getTextCharacters() {
        this.checkCharacters();
        return this.charData;
    }

    public int getTextStart() {
        this.checkCharacters();
        return 0;
    }

    public int getTextLength() {
        this.checkCharacters();
        return this.charDataLength;
    }

    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException {
        this.checkCharacters();
        int c = Math.min(this.charDataLength - sourceStart, length);
        System.arraycopy(this.charData, sourceStart, target, targetStart, c);
        return c;
    }

    public String getPIData() {
        throw new IllegalStateException();
    }

    public String getPITarget() {
        throw new IllegalStateException();
    }
}

