/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.SOAPException
 *  org.jvnet.staxex.util.DOMStreamReader
 */
package com.sun.xml.messaging.saaj.util.stax;

import com.sun.xml.messaging.saaj.soap.LazyEnvelope;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.jvnet.staxex.util.DOMStreamReader;
import org.w3c.dom.Node;

public class LazyEnvelopeStaxReader
extends DOMStreamReader {
    XMLStreamReader payloadReader = null;
    boolean usePayloadReaderDelegate = false;
    private QName bodyQName;

    public LazyEnvelopeStaxReader(LazyEnvelope env) throws SOAPException, XMLStreamException {
        super((Node)((Object)env));
        this.bodyQName = new QName(env.getNamespaceURI(), "Body");
        this.payloadReader = env.getStaxBridge().getPayloadReader();
        int eventType = this.getEventType();
        while (eventType != 1) {
            eventType = this.nextTag();
        }
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.getProperty(name);
        }
        return super.getProperty(name);
    }

    public int next() throws XMLStreamException {
        this.checkReaderStatus(true);
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.getEventType();
        }
        return this.getEventType();
    }

    public void require(int type, String namespaceURI, String localName) throws XMLStreamException {
        if (this.usePayloadReaderDelegate) {
            this.payloadReader.require(type, namespaceURI, localName);
        } else {
            super.require(type, namespaceURI, localName);
        }
    }

    public String getElementText() throws XMLStreamException {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.getElementText();
        }
        return super.getElementText();
    }

    public int nextTag() throws XMLStreamException {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.nextTag();
        }
        return super.nextTag();
    }

    public boolean hasNext() throws XMLStreamException {
        this.checkReaderStatus(false);
        boolean hasNext = this.usePayloadReaderDelegate ? this.payloadReader.hasNext() : super.hasNext();
        return hasNext;
    }

    private void checkReaderStatus(boolean advanceToNext) throws XMLStreamException {
        if (this.usePayloadReaderDelegate) {
            if (!this.payloadReader.hasNext()) {
                this.usePayloadReaderDelegate = false;
            }
        } else if (1 == this.getEventType() && this.bodyQName.equals(this.getName())) {
            this.usePayloadReaderDelegate = true;
            advanceToNext = false;
        }
        if (advanceToNext) {
            if (this.usePayloadReaderDelegate) {
                this.payloadReader.next();
            } else {
                super.next();
            }
        }
    }

    public void close() throws XMLStreamException {
        if (this.usePayloadReaderDelegate) {
            this.payloadReader.close();
        } else {
            super.close();
        }
    }

    public String getNamespaceURI(String prefix) {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.getNamespaceURI(prefix);
        }
        return super.getNamespaceURI(prefix);
    }

    public boolean isStartElement() {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.isStartElement();
        }
        return super.isStartElement();
    }

    public boolean isEndElement() {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.isEndElement();
        }
        return super.isEndElement();
    }

    public boolean isCharacters() {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.isCharacters();
        }
        return super.isEndElement();
    }

    public boolean isWhiteSpace() {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.isWhiteSpace();
        }
        return super.isWhiteSpace();
    }

    public String getAttributeValue(String namespaceURI, String localName) {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.getAttributeValue(namespaceURI, localName);
        }
        return super.getAttributeValue(namespaceURI, localName);
    }

    public int getAttributeCount() {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.getAttributeCount();
        }
        return super.getAttributeCount();
    }

    public QName getAttributeName(int index) {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.getAttributeName(index);
        }
        return super.getAttributeName(index);
    }

    public String getAttributeNamespace(int index) {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.getAttributeNamespace(index);
        }
        return super.getAttributeNamespace(index);
    }

    public String getAttributeLocalName(int index) {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.getAttributeLocalName(index);
        }
        return super.getAttributeLocalName(index);
    }

    public String getAttributePrefix(int index) {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.getAttributePrefix(index);
        }
        return super.getAttributePrefix(index);
    }

    public String getAttributeType(int index) {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.getAttributeType(index);
        }
        return super.getAttributeType(index);
    }

    public String getAttributeValue(int index) {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.getAttributeValue(index);
        }
        return super.getAttributeValue(index);
    }

    public boolean isAttributeSpecified(int index) {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.isAttributeSpecified(index);
        }
        return super.isAttributeSpecified(index);
    }

    public int getNamespaceCount() {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.getNamespaceCount();
        }
        return super.getNamespaceCount();
    }

    public String getNamespacePrefix(int index) {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.getNamespacePrefix(index);
        }
        return super.getNamespacePrefix(index);
    }

    public String getNamespaceURI(int index) {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.getNamespaceURI(index);
        }
        return super.getNamespaceURI(index);
    }

    public NamespaceContext getNamespaceContext() {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.getNamespaceContext();
        }
        return super.getNamespaceContext();
    }

    public int getEventType() {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.getEventType();
        }
        return super.getEventType();
    }

    public String getText() {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.getText();
        }
        return super.getText();
    }

    public char[] getTextCharacters() {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.getTextCharacters();
        }
        return super.getTextCharacters();
    }

    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.getTextCharacters(sourceStart, target, targetStart, length);
        }
        return super.getTextCharacters(sourceStart, target, targetStart, length);
    }

    public int getTextStart() {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.getTextStart();
        }
        return super.getTextStart();
    }

    public int getTextLength() {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.getTextLength();
        }
        return super.getTextLength();
    }

    public String getEncoding() {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.getEncoding();
        }
        return super.getEncoding();
    }

    public boolean hasText() {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.hasText();
        }
        return super.hasText();
    }

    public Location getLocation() {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.getLocation();
        }
        return super.getLocation();
    }

    public QName getName() {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.getName();
        }
        return super.getName();
    }

    public String getLocalName() {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.getLocalName();
        }
        return super.getLocalName();
    }

    public boolean hasName() {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.hasName();
        }
        return super.hasName();
    }

    public String getNamespaceURI() {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.getNamespaceURI();
        }
        return super.getNamespaceURI();
    }

    public String getPrefix() {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.getPrefix();
        }
        return super.getPrefix();
    }

    public String getVersion() {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.getVersion();
        }
        return super.getVersion();
    }

    public boolean isStandalone() {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.isStandalone();
        }
        return super.isStandalone();
    }

    public boolean standaloneSet() {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.standaloneSet();
        }
        return super.standaloneSet();
    }

    public String getCharacterEncodingScheme() {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.getCharacterEncodingScheme();
        }
        return super.getCharacterEncodingScheme();
    }

    public String getPITarget() {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.getPITarget();
        }
        return super.getPITarget();
    }

    public String getPIData() {
        if (this.usePayloadReaderDelegate) {
            return this.payloadReader.getPIData();
        }
        return super.getPIData();
    }
}

