/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jvnet.staxex.Base64Data
 *  org.jvnet.staxex.XMLStreamReaderEx
 */
package com.sun.xml.stream.buffer.stax;

import com.sun.xml.stream.buffer.MutableXMLStreamBuffer;
import com.sun.xml.stream.buffer.stax.StreamBufferCreator;
import java.util.HashMap;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.jvnet.staxex.Base64Data;
import org.jvnet.staxex.XMLStreamReaderEx;

public class StreamReaderBufferCreator
extends StreamBufferCreator {
    private int _eventType;
    private boolean _storeInScopeNamespacesOnElementFragment;
    private Map<String, Integer> _inScopePrefixes;

    public StreamReaderBufferCreator() {
    }

    public StreamReaderBufferCreator(MutableXMLStreamBuffer buffer) {
        this.setBuffer(buffer);
    }

    public MutableXMLStreamBuffer create(XMLStreamReader reader) throws XMLStreamException {
        if (this._buffer == null) {
            this.createBuffer();
        }
        this.store(reader);
        return this.getXMLStreamBuffer();
    }

    public MutableXMLStreamBuffer createElementFragment(XMLStreamReader reader, boolean storeInScopeNamespaces) throws XMLStreamException {
        if (this._buffer == null) {
            this.createBuffer();
        }
        if (!reader.hasNext()) {
            return this._buffer;
        }
        this._storeInScopeNamespacesOnElementFragment = storeInScopeNamespaces;
        this._eventType = reader.getEventType();
        if (this._eventType != 1) {
            do {
                this._eventType = reader.next();
            } while (this._eventType != 1 && this._eventType != 8);
        }
        if (storeInScopeNamespaces) {
            this._inScopePrefixes = new HashMap<String, Integer>();
        }
        this.storeElementAndChildren(reader);
        return this.getXMLStreamBuffer();
    }

    private void store(XMLStreamReader reader) throws XMLStreamException {
        if (!reader.hasNext()) {
            return;
        }
        this._eventType = reader.getEventType();
        switch (this._eventType) {
            case 7: {
                this.storeDocumentAndChildren(reader);
                break;
            }
            case 1: {
                this.storeElementAndChildren(reader);
                break;
            }
            default: {
                throw new XMLStreamException("XMLStreamReader not positioned at a document or element");
            }
        }
        this.increaseTreeCount();
    }

    private void storeDocumentAndChildren(XMLStreamReader reader) throws XMLStreamException {
        this.storeStructure(16);
        this._eventType = reader.next();
        block5: while (this._eventType != 8) {
            switch (this._eventType) {
                case 1: {
                    this.storeElementAndChildren(reader);
                    continue block5;
                }
                case 5: {
                    this.storeComment(reader);
                    break;
                }
                case 3: {
                    this.storeProcessingInstruction(reader);
                }
            }
            this._eventType = reader.next();
        }
        this.storeStructure(144);
    }

    private void storeElementAndChildren(XMLStreamReader reader) throws XMLStreamException {
        if (reader instanceof XMLStreamReaderEx) {
            this.storeElementAndChildrenEx((XMLStreamReaderEx)reader);
        } else {
            this.storeElementAndChildrenNoEx(reader);
        }
    }

    private void storeElementAndChildrenEx(XMLStreamReaderEx reader) throws XMLStreamException {
        int depth = 1;
        if (this._storeInScopeNamespacesOnElementFragment) {
            this.storeElementWithInScopeNamespaces((XMLStreamReader)reader);
        } else {
            this.storeElement((XMLStreamReader)reader);
        }
        while (depth > 0) {
            this._eventType = reader.next();
            switch (this._eventType) {
                case 1: {
                    ++depth;
                    this.storeElement((XMLStreamReader)reader);
                    break;
                }
                case 2: {
                    --depth;
                    this.storeStructure(144);
                    break;
                }
                case 13: {
                    this.storeNamespaceAttributes((XMLStreamReader)reader);
                    break;
                }
                case 10: {
                    this.storeAttributes((XMLStreamReader)reader);
                    break;
                }
                case 4: 
                case 6: 
                case 12: {
                    CharSequence c = reader.getPCDATA();
                    if (c instanceof Base64Data) {
                        this.storeStructure(92);
                        this.storeContentObject(c);
                        break;
                    }
                    this.storeContentCharacters(80, reader.getTextCharacters(), reader.getTextStart(), reader.getTextLength());
                    break;
                }
                case 5: {
                    this.storeComment((XMLStreamReader)reader);
                    break;
                }
                case 3: {
                    this.storeProcessingInstruction((XMLStreamReader)reader);
                }
            }
        }
        this._eventType = reader.next();
    }

    private void storeElementAndChildrenNoEx(XMLStreamReader reader) throws XMLStreamException {
        int depth = 1;
        if (this._storeInScopeNamespacesOnElementFragment) {
            this.storeElementWithInScopeNamespaces(reader);
        } else {
            this.storeElement(reader);
        }
        while (depth > 0) {
            this._eventType = reader.next();
            switch (this._eventType) {
                case 1: {
                    ++depth;
                    this.storeElement(reader);
                    break;
                }
                case 2: {
                    --depth;
                    this.storeStructure(144);
                    break;
                }
                case 13: {
                    this.storeNamespaceAttributes(reader);
                    break;
                }
                case 10: {
                    this.storeAttributes(reader);
                    break;
                }
                case 4: 
                case 6: 
                case 12: {
                    this.storeContentCharacters(80, reader.getTextCharacters(), reader.getTextStart(), reader.getTextLength());
                    break;
                }
                case 5: {
                    this.storeComment(reader);
                    break;
                }
                case 3: {
                    this.storeProcessingInstruction(reader);
                }
            }
        }
        this._eventType = reader.next();
    }

    private void storeElementWithInScopeNamespaces(XMLStreamReader reader) {
        this.storeQualifiedName(32, reader.getPrefix(), reader.getNamespaceURI(), reader.getLocalName());
        if (reader.getNamespaceCount() > 0) {
            this.storeNamespaceAttributes(reader);
        }
        if (reader.getAttributeCount() > 0) {
            this.storeAttributes(reader);
        }
    }

    private void storeElement(XMLStreamReader reader) {
        this.storeQualifiedName(32, reader.getPrefix(), reader.getNamespaceURI(), reader.getLocalName());
        if (reader.getNamespaceCount() > 0) {
            this.storeNamespaceAttributes(reader);
        }
        if (reader.getAttributeCount() > 0) {
            this.storeAttributes(reader);
        }
    }

    public void storeElement(String nsURI, String localName, String prefix, String[] ns) {
        this.storeQualifiedName(32, prefix, nsURI, localName);
        this.storeNamespaceAttributes(ns);
    }

    public void storeEndElement() {
        this.storeStructure(144);
    }

    private void storeNamespaceAttributes(XMLStreamReader reader) {
        int count = reader.getNamespaceCount();
        for (int i = 0; i < count; ++i) {
            this.storeNamespaceAttribute(reader.getNamespacePrefix(i), reader.getNamespaceURI(i));
        }
    }

    private void storeNamespaceAttributes(String[] ns) {
        for (int i = 0; i < ns.length; i += 2) {
            this.storeNamespaceAttribute(ns[i], ns[i + 1]);
        }
    }

    private void storeAttributes(XMLStreamReader reader) {
        int count = reader.getAttributeCount();
        for (int i = 0; i < count; ++i) {
            this.storeAttribute(reader.getAttributePrefix(i), reader.getAttributeNamespace(i), reader.getAttributeLocalName(i), reader.getAttributeType(i), reader.getAttributeValue(i));
        }
    }

    private void storeComment(XMLStreamReader reader) {
        this.storeContentCharacters(96, reader.getTextCharacters(), reader.getTextStart(), reader.getTextLength());
    }

    private void storeProcessingInstruction(XMLStreamReader reader) {
        this.storeProcessingInstruction(reader.getPITarget(), reader.getPIData());
    }
}

