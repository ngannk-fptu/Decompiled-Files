/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.security.stax.impl;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Comment;
import javax.xml.stream.events.DTD;
import javax.xml.stream.events.EntityReference;
import javax.xml.stream.events.Namespace;
import javax.xml.stream.events.ProcessingInstruction;
import javax.xml.stream.events.StartDocument;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.stax.ext.InputProcessorChain;
import org.apache.xml.security.stax.ext.XMLSecurityProperties;
import org.apache.xml.security.stax.ext.stax.XMLSecEvent;
import org.apache.xml.security.stax.ext.stax.XMLSecStartElement;

public class XMLSecurityStreamReader
implements XMLStreamReader {
    private final InputProcessorChain inputProcessorChain;
    private XMLSecEvent currentXMLSecEvent;
    private final boolean skipDocumentEvents;
    private String version;
    private boolean standalone;
    private boolean standaloneSet;
    private String characterEncodingScheme;
    private static final String ERR_STATE_NOT_ELEM = "Current state not START_ELEMENT or END_ELEMENT";
    private static final String ERR_STATE_NOT_STELEM = "Current state not START_ELEMENT";
    private static final String ERR_STATE_NOT_PI = "Current state not PROCESSING_INSTRUCTION";
    private static final int MASK_GET_TEXT = 6768;

    public XMLSecurityStreamReader(InputProcessorChain inputProcessorChain, XMLSecurityProperties securityProperties) {
        this.inputProcessorChain = inputProcessorChain;
        this.skipDocumentEvents = securityProperties.isSkipDocumentEvents();
    }

    @Override
    public Object getProperty(String name) throws IllegalArgumentException {
        if ("javax.xml.stream.isNamespaceAware".equals(name)) {
            return true;
        }
        return null;
    }

    @Override
    public int next() throws XMLStreamException {
        int eventType;
        try {
            this.inputProcessorChain.reset();
            this.currentXMLSecEvent = this.inputProcessorChain.processEvent();
            eventType = this.currentXMLSecEvent.getEventType();
            if (eventType == 7) {
                StartDocument startDocument = (StartDocument)((Object)this.currentXMLSecEvent);
                this.version = startDocument.getVersion();
                if (startDocument.encodingSet()) {
                    this.characterEncodingScheme = startDocument.getCharacterEncodingScheme();
                }
                this.standalone = startDocument.isStandalone();
                this.standaloneSet = startDocument.standaloneSet();
                if (this.skipDocumentEvents) {
                    this.currentXMLSecEvent = this.inputProcessorChain.processEvent();
                    eventType = this.currentXMLSecEvent.getEventType();
                }
            }
        }
        catch (XMLSecurityException e) {
            throw new XMLStreamException(e);
        }
        return eventType;
    }

    private XMLSecEvent getCurrentEvent() {
        return this.currentXMLSecEvent;
    }

    @Override
    public void require(int type, String namespaceURI, String localName) throws XMLStreamException {
        XMLSecEvent xmlSecEvent = this.getCurrentEvent();
        if (xmlSecEvent.getEventType() != type) {
            throw new XMLStreamException("Event type mismatch");
        }
        if (localName != null) {
            if (xmlSecEvent.getEventType() != 1 && xmlSecEvent.getEventType() != 2 && xmlSecEvent.getEventType() != 9) {
                throw new XMLStreamException("Expected non-null local name, but current token not a START_ELEMENT, END_ELEMENT or ENTITY_REFERENCE (was " + xmlSecEvent.getEventType() + ")");
            }
            String n = this.getLocalName();
            if (!n.equals(localName)) {
                throw new XMLStreamException("Expected local name '" + localName + "'; current local name '" + n + "'.");
            }
        }
        if (namespaceURI != null) {
            if (xmlSecEvent.getEventType() != 1 && xmlSecEvent.getEventType() != 2) {
                throw new XMLStreamException("Expected non-null NS URI, but current token not a START_ELEMENT or END_ELEMENT (was " + xmlSecEvent.getEventType() + ")");
            }
            String uri = this.getNamespaceURI();
            if (namespaceURI.length() == 0) {
                if (uri != null && uri.length() > 0) {
                    throw new XMLStreamException("Expected empty namespace, instead have '" + uri + "'.");
                }
            } else if (!namespaceURI.equals(uri)) {
                throw new XMLStreamException("Expected namespace '" + namespaceURI + "'; have '" + uri + "'.");
            }
        }
    }

    @Override
    public String getElementText() throws XMLStreamException {
        XMLSecEvent xmlSecEvent = this.getCurrentEvent();
        if (xmlSecEvent.getEventType() != 1) {
            throw new XMLStreamException("Not positioned on a start element");
        }
        StringBuilder stringBuilder = new StringBuilder();
        block5: while (true) {
            int type = this.next();
            switch (type) {
                case 2: {
                    break block5;
                }
                case 3: 
                case 5: {
                    continue block5;
                }
                case 4: 
                case 6: 
                case 9: 
                case 12: {
                    stringBuilder.append(this.getText());
                    break;
                }
                default: {
                    throw new XMLStreamException("Expected a text token, got " + type + ".");
                }
            }
        }
        return stringBuilder.toString();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public int nextTag() throws XMLStreamException {
        int next;
        block5: while (true) {
            next = this.next();
            switch (next) {
                case 3: 
                case 5: 
                case 6: {
                    continue block5;
                }
                case 4: 
                case 12: {
                    if (!this.isWhiteSpace()) throw new XMLStreamException("Received non-all-whitespace CHARACTERS or CDATA event in nextTag().");
                    continue block5;
                }
                case 1: 
                case 2: {
                    return next;
                }
            }
            break;
        }
        throw new XMLStreamException("Received event " + next + ", instead of START_ELEMENT or END_ELEMENT.");
    }

    @Override
    public boolean hasNext() throws XMLStreamException {
        return this.currentXMLSecEvent == null || this.currentXMLSecEvent.getEventType() != 8;
    }

    @Override
    public void close() throws XMLStreamException {
        try {
            this.inputProcessorChain.reset();
            this.inputProcessorChain.doFinal();
        }
        catch (XMLSecurityException e) {
            throw new XMLStreamException(e);
        }
    }

    @Override
    public String getNamespaceURI(String prefix) {
        XMLSecEvent xmlSecEvent = this.getCurrentEvent();
        switch (xmlSecEvent.getEventType()) {
            case 1: {
                return xmlSecEvent.asStartElement().getNamespaceURI(prefix);
            }
            case 2: {
                XMLSecStartElement xmlSecStartElement = xmlSecEvent.asEndElement().getParentXMLSecStartElement();
                if (xmlSecStartElement != null) {
                    return xmlSecStartElement.getNamespaceURI(prefix);
                }
                return null;
            }
        }
        throw new IllegalStateException(ERR_STATE_NOT_ELEM);
    }

    @Override
    public boolean isStartElement() {
        return this.getCurrentEvent().isStartElement();
    }

    @Override
    public boolean isEndElement() {
        return this.getCurrentEvent().isEndElement();
    }

    @Override
    public boolean isCharacters() {
        return this.getCurrentEvent().isCharacters();
    }

    @Override
    public boolean isWhiteSpace() {
        XMLSecEvent xmlSecEvent = this.getCurrentEvent();
        return xmlSecEvent.isCharacters() && xmlSecEvent.asCharacters().isWhiteSpace();
    }

    @Override
    public String getAttributeValue(String namespaceURI, String localName) {
        XMLSecEvent xmlSecEvent = this.getCurrentEvent();
        if (xmlSecEvent.getEventType() != 1) {
            throw new IllegalStateException(ERR_STATE_NOT_STELEM);
        }
        Attribute attribute = xmlSecEvent.asStartElement().getAttributeByName(new QName(namespaceURI, localName));
        if (attribute != null) {
            return attribute.getValue();
        }
        return null;
    }

    @Override
    public int getAttributeCount() {
        XMLSecEvent xmlSecEvent = this.getCurrentEvent();
        if (xmlSecEvent.getEventType() != 1) {
            throw new IllegalStateException(ERR_STATE_NOT_STELEM);
        }
        return xmlSecEvent.asStartElement().getOnElementDeclaredAttributes().size();
    }

    @Override
    public QName getAttributeName(int index) {
        XMLSecEvent xmlSecEvent = this.getCurrentEvent();
        if (xmlSecEvent.getEventType() != 1) {
            throw new IllegalStateException(ERR_STATE_NOT_STELEM);
        }
        return xmlSecEvent.asStartElement().getOnElementDeclaredAttributes().get(index).getName();
    }

    @Override
    public String getAttributeNamespace(int index) {
        XMLSecEvent xmlSecEvent = this.getCurrentEvent();
        if (xmlSecEvent.getEventType() != 1) {
            throw new IllegalStateException(ERR_STATE_NOT_STELEM);
        }
        return xmlSecEvent.asStartElement().getOnElementDeclaredAttributes().get(index).getAttributeNamespace().getNamespaceURI();
    }

    @Override
    public String getAttributeLocalName(int index) {
        XMLSecEvent xmlSecEvent = this.getCurrentEvent();
        if (xmlSecEvent.getEventType() != 1) {
            throw new IllegalStateException(ERR_STATE_NOT_STELEM);
        }
        return xmlSecEvent.asStartElement().getOnElementDeclaredAttributes().get(index).getName().getLocalPart();
    }

    @Override
    public String getAttributePrefix(int index) {
        XMLSecEvent xmlSecEvent = this.getCurrentEvent();
        if (xmlSecEvent.getEventType() != 1) {
            throw new IllegalStateException(ERR_STATE_NOT_STELEM);
        }
        return xmlSecEvent.asStartElement().getOnElementDeclaredAttributes().get(index).getName().getPrefix();
    }

    @Override
    public String getAttributeType(int index) {
        XMLSecEvent xmlSecEvent = this.getCurrentEvent();
        if (xmlSecEvent.getEventType() != 1) {
            throw new IllegalStateException(ERR_STATE_NOT_STELEM);
        }
        return xmlSecEvent.asStartElement().getOnElementDeclaredAttributes().get(index).getDTDType();
    }

    @Override
    public String getAttributeValue(int index) {
        XMLSecEvent xmlSecEvent = this.getCurrentEvent();
        if (xmlSecEvent.getEventType() != 1) {
            throw new IllegalStateException(ERR_STATE_NOT_STELEM);
        }
        return xmlSecEvent.asStartElement().getOnElementDeclaredAttributes().get(index).getValue();
    }

    @Override
    public boolean isAttributeSpecified(int index) {
        XMLSecEvent xmlSecEvent = this.getCurrentEvent();
        if (xmlSecEvent.getEventType() != 1) {
            throw new IllegalStateException(ERR_STATE_NOT_STELEM);
        }
        return xmlSecEvent.asStartElement().getOnElementDeclaredAttributes().get(index).isSpecified();
    }

    @Override
    public int getNamespaceCount() {
        XMLSecEvent xmlSecEvent = this.getCurrentEvent();
        switch (xmlSecEvent.getEventType()) {
            case 1: {
                return xmlSecEvent.asStartElement().getOnElementDeclaredNamespaces().size();
            }
            case 2: {
                int count = 0;
                Iterator<Namespace> namespaceIterator = xmlSecEvent.asEndElement().getNamespaces();
                while (namespaceIterator.hasNext()) {
                    namespaceIterator.next();
                    ++count;
                }
                return count;
            }
        }
        throw new IllegalStateException(ERR_STATE_NOT_ELEM);
    }

    @Override
    public String getNamespacePrefix(int index) {
        XMLSecEvent xmlSecEvent = this.getCurrentEvent();
        switch (xmlSecEvent.getEventType()) {
            case 1: {
                return xmlSecEvent.asStartElement().getOnElementDeclaredNamespaces().get(index).getPrefix();
            }
            case 2: {
                int count = 0;
                Iterator<Namespace> namespaceIterator = xmlSecEvent.asEndElement().getNamespaces();
                while (namespaceIterator.hasNext()) {
                    Namespace namespace = namespaceIterator.next();
                    if (count == index) {
                        return namespace.getPrefix();
                    }
                    ++count;
                }
                throw new ArrayIndexOutOfBoundsException(index);
            }
        }
        throw new IllegalStateException(ERR_STATE_NOT_ELEM);
    }

    @Override
    public String getNamespaceURI(int index) {
        XMLSecEvent xmlSecEvent = this.getCurrentEvent();
        if (xmlSecEvent.getEventType() != 1) {
            throw new IllegalStateException(ERR_STATE_NOT_STELEM);
        }
        return xmlSecEvent.asStartElement().getOnElementDeclaredNamespaces().get(index).getNamespaceURI();
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        XMLSecEvent xmlSecEvent = this.getCurrentEvent();
        if (xmlSecEvent.getEventType() != 1) {
            throw new IllegalStateException(ERR_STATE_NOT_STELEM);
        }
        return xmlSecEvent.asStartElement().getNamespaceContext();
    }

    @Override
    public int getEventType() {
        XMLSecEvent xmlSecEvent = this.getCurrentEvent();
        if (xmlSecEvent == null) {
            try {
                return this.next();
            }
            catch (XMLStreamException e) {
                throw new IllegalStateException(e);
            }
        }
        if (xmlSecEvent.isCharacters() && xmlSecEvent.asCharacters().isIgnorableWhiteSpace()) {
            return 6;
        }
        return xmlSecEvent.getEventType();
    }

    @Override
    public String getText() {
        XMLSecEvent xmlSecEvent = this.getCurrentEvent();
        switch (xmlSecEvent.getEventType()) {
            case 9: {
                return ((EntityReference)((Object)xmlSecEvent)).getDeclaration().getReplacementText();
            }
            case 11: {
                return ((DTD)((Object)xmlSecEvent)).getDocumentTypeDeclaration();
            }
            case 5: {
                return ((Comment)((Object)xmlSecEvent)).getText();
            }
            case 4: 
            case 6: 
            case 12: {
                return xmlSecEvent.asCharacters().getData();
            }
        }
        throw new IllegalStateException("Current state not TEXT");
    }

    @Override
    public char[] getTextCharacters() {
        XMLSecEvent xmlSecEvent = this.getCurrentEvent();
        switch (xmlSecEvent.getEventType()) {
            case 9: {
                return ((EntityReference)((Object)xmlSecEvent)).getDeclaration().getReplacementText().toCharArray();
            }
            case 11: {
                return ((DTD)((Object)xmlSecEvent)).getDocumentTypeDeclaration().toCharArray();
            }
            case 5: {
                return ((Comment)((Object)xmlSecEvent)).getText().toCharArray();
            }
            case 4: 
            case 6: 
            case 12: {
                return xmlSecEvent.asCharacters().getText();
            }
        }
        throw new IllegalStateException("Current state not TEXT");
    }

    @Override
    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException {
        XMLSecEvent xmlSecEvent = this.getCurrentEvent();
        switch (xmlSecEvent.getEventType()) {
            case 9: {
                ((EntityReference)((Object)xmlSecEvent)).getDeclaration().getReplacementText().getChars(sourceStart, sourceStart + length, target, targetStart);
                return length;
            }
            case 11: {
                ((DTD)((Object)xmlSecEvent)).getDocumentTypeDeclaration().getChars(sourceStart, sourceStart + length, target, targetStart);
                return length;
            }
            case 5: {
                ((Comment)((Object)xmlSecEvent)).getText().getChars(sourceStart, sourceStart + length, target, targetStart);
                return length;
            }
            case 4: 
            case 6: 
            case 12: {
                xmlSecEvent.asCharacters().getData().getChars(sourceStart, sourceStart + length, target, targetStart);
                return length;
            }
        }
        throw new IllegalStateException("Current state not TEXT");
    }

    @Override
    public int getTextStart() {
        return 0;
    }

    @Override
    public int getTextLength() {
        XMLSecEvent xmlSecEvent = this.getCurrentEvent();
        switch (xmlSecEvent.getEventType()) {
            case 9: {
                return ((EntityReference)((Object)xmlSecEvent)).getDeclaration().getReplacementText().length();
            }
            case 11: {
                return ((DTD)((Object)xmlSecEvent)).getDocumentTypeDeclaration().length();
            }
            case 5: {
                return ((Comment)((Object)xmlSecEvent)).getText().length();
            }
            case 4: 
            case 6: 
            case 12: {
                return xmlSecEvent.asCharacters().getData().length();
            }
        }
        throw new IllegalStateException("Current state not TEXT");
    }

    @Override
    public String getEncoding() {
        return this.inputProcessorChain.getDocumentContext().getEncoding();
    }

    @Override
    public boolean hasText() {
        XMLSecEvent xmlSecEvent = this.getCurrentEvent();
        return (1 << xmlSecEvent.getEventType() & 0x1A70) != 0;
    }

    @Override
    public Location getLocation() {
        return new Location(){

            @Override
            public int getLineNumber() {
                return -1;
            }

            @Override
            public int getColumnNumber() {
                return -1;
            }

            @Override
            public int getCharacterOffset() {
                return -1;
            }

            @Override
            public String getPublicId() {
                return null;
            }

            @Override
            public String getSystemId() {
                return null;
            }
        };
    }

    @Override
    public QName getName() {
        XMLSecEvent xmlSecEvent = this.getCurrentEvent();
        switch (xmlSecEvent.getEventType()) {
            case 1: {
                return xmlSecEvent.asStartElement().getName();
            }
            case 2: {
                return xmlSecEvent.asEndElement().getName();
            }
        }
        throw new IllegalStateException(ERR_STATE_NOT_ELEM);
    }

    @Override
    public String getLocalName() {
        XMLSecEvent xmlSecEvent = this.getCurrentEvent();
        switch (xmlSecEvent.getEventType()) {
            case 1: {
                return xmlSecEvent.asStartElement().getName().getLocalPart();
            }
            case 2: {
                return xmlSecEvent.asEndElement().getName().getLocalPart();
            }
        }
        throw new IllegalStateException(ERR_STATE_NOT_ELEM);
    }

    @Override
    public boolean hasName() {
        XMLSecEvent xmlSecEvent = this.getCurrentEvent();
        return xmlSecEvent.getEventType() == 1 || xmlSecEvent.getEventType() == 2;
    }

    @Override
    public String getNamespaceURI() {
        XMLSecEvent xmlSecEvent = this.getCurrentEvent();
        switch (xmlSecEvent.getEventType()) {
            case 1: {
                return xmlSecEvent.asStartElement().getName().getNamespaceURI();
            }
            case 2: {
                return xmlSecEvent.asEndElement().getName().getNamespaceURI();
            }
        }
        throw new IllegalStateException(ERR_STATE_NOT_ELEM);
    }

    @Override
    public String getPrefix() {
        XMLSecEvent xmlSecEvent = this.getCurrentEvent();
        switch (xmlSecEvent.getEventType()) {
            case 1: {
                return xmlSecEvent.asStartElement().getName().getPrefix();
            }
            case 2: {
                return xmlSecEvent.asEndElement().getName().getPrefix();
            }
        }
        throw new IllegalStateException(ERR_STATE_NOT_ELEM);
    }

    @Override
    public String getVersion() {
        return this.version;
    }

    @Override
    public boolean isStandalone() {
        return this.standalone;
    }

    @Override
    public boolean standaloneSet() {
        return this.standaloneSet;
    }

    @Override
    public String getCharacterEncodingScheme() {
        return this.characterEncodingScheme;
    }

    @Override
    public String getPITarget() {
        XMLSecEvent xmlSecEvent = this.getCurrentEvent();
        if (xmlSecEvent.getEventType() != 3) {
            throw new IllegalStateException(ERR_STATE_NOT_PI);
        }
        return ((ProcessingInstruction)((Object)xmlSecEvent)).getTarget();
    }

    @Override
    public String getPIData() {
        XMLSecEvent xmlSecEvent = this.getCurrentEvent();
        if (xmlSecEvent.getEventType() != 3) {
            throw new IllegalStateException(ERR_STATE_NOT_PI);
        }
        return ((ProcessingInstruction)((Object)xmlSecEvent)).getData();
    }
}

