/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.stax;

import com.sun.xml.fastinfoset.CommonResourceBundle;
import com.sun.xml.fastinfoset.Decoder;
import com.sun.xml.fastinfoset.DecoderStateTables;
import com.sun.xml.fastinfoset.OctetBufferListener;
import com.sun.xml.fastinfoset.QualifiedName;
import com.sun.xml.fastinfoset.algorithm.BuiltInEncodingAlgorithmFactory;
import com.sun.xml.fastinfoset.org.apache.xerces.util.XMLChar;
import com.sun.xml.fastinfoset.sax.AttributesHolder;
import com.sun.xml.fastinfoset.stax.EventLocation;
import com.sun.xml.fastinfoset.stax.StAXManager;
import com.sun.xml.fastinfoset.util.CharArray;
import com.sun.xml.fastinfoset.util.CharArrayString;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.jvnet.fastinfoset.EncodingAlgorithm;
import org.jvnet.fastinfoset.EncodingAlgorithmException;
import org.jvnet.fastinfoset.FastInfosetException;
import org.jvnet.fastinfoset.stax.FastInfosetStreamReader;

public class StAXDocumentParser
extends Decoder
implements XMLStreamReader,
FastInfosetStreamReader,
OctetBufferListener {
    private static final Logger logger = Logger.getLogger(StAXDocumentParser.class.getName());
    protected static final int INTERNAL_STATE_START_DOCUMENT = 0;
    protected static final int INTERNAL_STATE_START_ELEMENT_TERMINATE = 1;
    protected static final int INTERNAL_STATE_SINGLE_TERMINATE_ELEMENT_WITH_NAMESPACES = 2;
    protected static final int INTERNAL_STATE_DOUBLE_TERMINATE_ELEMENT = 3;
    protected static final int INTERNAL_STATE_END_DOCUMENT = 4;
    protected static final int INTERNAL_STATE_VOID = -1;
    protected int _internalState;
    protected int _eventType;
    protected QualifiedName[] _qNameStack = new QualifiedName[32];
    protected int[] _namespaceAIIsStartStack = new int[32];
    protected int[] _namespaceAIIsEndStack = new int[32];
    protected int _stackCount = -1;
    protected String[] _namespaceAIIsPrefix = new String[32];
    protected String[] _namespaceAIIsNamespaceName = new String[32];
    protected int[] _namespaceAIIsPrefixIndex = new int[32];
    protected int _namespaceAIIsIndex;
    protected int _currentNamespaceAIIsStart;
    protected int _currentNamespaceAIIsEnd;
    protected QualifiedName _qualifiedName;
    protected AttributesHolder _attributes = new AttributesHolder();
    protected boolean _clearAttributes = false;
    protected char[] _characters;
    protected int _charactersOffset;
    protected String _algorithmURI;
    protected int _algorithmId;
    protected boolean _isAlgorithmDataCloned;
    protected byte[] _algorithmData;
    protected int _algorithmDataOffset;
    protected int _algorithmDataLength;
    protected String _piTarget;
    protected String _piData;
    protected NamespaceContextImpl _nsContext = new NamespaceContextImpl();
    protected String _characterEncodingScheme;
    protected StAXManager _manager;
    private byte[] base64TaleBytes = new byte[3];
    private int base64TaleLength;

    public StAXDocumentParser() {
        this.reset();
        this._manager = new StAXManager(1);
    }

    public StAXDocumentParser(InputStream s) {
        this();
        this.setInputStream(s);
        this._manager = new StAXManager(1);
    }

    public StAXDocumentParser(InputStream s, StAXManager manager) {
        this(s);
        this._manager = manager;
    }

    @Override
    public void setInputStream(InputStream s) {
        super.setInputStream(s);
        this.reset();
    }

    @Override
    public void reset() {
        super.reset();
        if (this._internalState != 0 && this._internalState != 4) {
            for (int i = this._namespaceAIIsIndex - 1; i >= 0; --i) {
                this._prefixTable.popScopeWithPrefixEntry(this._namespaceAIIsPrefixIndex[i]);
            }
            this._stackCount = -1;
            this._namespaceAIIsIndex = 0;
            this._characters = null;
            this._algorithmData = null;
        }
        this._characterEncodingScheme = "UTF-8";
        this._eventType = 7;
        this._internalState = 0;
    }

    protected void resetOnError() {
        super.reset();
        if (this._v != null) {
            this._prefixTable.clearCompletely();
        }
        this._duplicateAttributeVerifier.clear();
        this._stackCount = -1;
        this._namespaceAIIsIndex = 0;
        this._characters = null;
        this._algorithmData = null;
        this._eventType = 7;
        this._internalState = 0;
    }

    @Override
    public Object getProperty(String name) throws IllegalArgumentException {
        if (this._manager != null) {
            return this._manager.getProperty(name);
        }
        return null;
    }

    @Override
    public int next() throws XMLStreamException {
        try {
            if (this._internalState != -1) {
                switch (this._internalState) {
                    case 0: {
                        this.decodeHeader();
                        this.processDII();
                        this._internalState = -1;
                        break;
                    }
                    case 1: {
                        if (this._currentNamespaceAIIsEnd > 0) {
                            for (int i = this._currentNamespaceAIIsEnd - 1; i >= this._currentNamespaceAIIsStart; --i) {
                                this._prefixTable.popScopeWithPrefixEntry(this._namespaceAIIsPrefixIndex[i]);
                            }
                            this._namespaceAIIsIndex = this._currentNamespaceAIIsStart;
                        }
                        this.popStack();
                        this._internalState = -1;
                        this._eventType = 2;
                        return 2;
                    }
                    case 2: {
                        for (int i = this._currentNamespaceAIIsEnd - 1; i >= this._currentNamespaceAIIsStart; --i) {
                            this._prefixTable.popScopeWithPrefixEntry(this._namespaceAIIsPrefixIndex[i]);
                        }
                        this._namespaceAIIsIndex = this._currentNamespaceAIIsStart;
                        this._internalState = -1;
                        break;
                    }
                    case 3: {
                        if (this._currentNamespaceAIIsEnd > 0) {
                            for (int i = this._currentNamespaceAIIsEnd - 1; i >= this._currentNamespaceAIIsStart; --i) {
                                this._prefixTable.popScopeWithPrefixEntry(this._namespaceAIIsPrefixIndex[i]);
                            }
                            this._namespaceAIIsIndex = this._currentNamespaceAIIsStart;
                        }
                        if (this._stackCount == -1) {
                            this._internalState = 4;
                            this._eventType = 8;
                            return 8;
                        }
                        this.popStack();
                        this._internalState = this._currentNamespaceAIIsEnd > 0 ? 2 : -1;
                        this._eventType = 2;
                        return 2;
                    }
                    case 4: {
                        throw new NoSuchElementException(CommonResourceBundle.getInstance().getString("message.noMoreEvents"));
                    }
                }
            }
            this._characters = null;
            this._algorithmData = null;
            this._currentNamespaceAIIsEnd = 0;
            int b = this.read();
            switch (DecoderStateTables.EII(b)) {
                case 0: {
                    this.processEII(this._elementNameTable._array[b], false);
                    return this._eventType;
                }
                case 1: {
                    this.processEII(this._elementNameTable._array[b & 0x1F], true);
                    return this._eventType;
                }
                case 2: {
                    this.processEII(this.processEIIIndexMedium(b), (b & 0x40) > 0);
                    return this._eventType;
                }
                case 3: {
                    this.processEII(this.processEIIIndexLarge(b), (b & 0x40) > 0);
                    return this._eventType;
                }
                case 5: {
                    QualifiedName qn = this.processLiteralQualifiedName(b & 3, this._elementNameTable.getNext());
                    this._elementNameTable.add(qn);
                    this.processEII(qn, (b & 0x40) > 0);
                    return this._eventType;
                }
                case 4: {
                    this.processEIIWithNamespaces((b & 0x40) > 0);
                    return this._eventType;
                }
                case 6: {
                    this._octetBufferLength = (b & 1) + 1;
                    this.processUtf8CharacterString(b);
                    this._eventType = 4;
                    return 4;
                }
                case 7: {
                    this._octetBufferLength = this.read() + 3;
                    this.processUtf8CharacterString(b);
                    this._eventType = 4;
                    return 4;
                }
                case 8: {
                    this._octetBufferLength = (this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read()) + 259;
                    this.processUtf8CharacterString(b);
                    this._eventType = 4;
                    return 4;
                }
                case 9: {
                    this._octetBufferLength = (b & 1) + 1;
                    this.processUtf16CharacterString(b);
                    this._eventType = 4;
                    return 4;
                }
                case 10: {
                    this._octetBufferLength = this.read() + 3;
                    this.processUtf16CharacterString(b);
                    this._eventType = 4;
                    return 4;
                }
                case 11: {
                    this._octetBufferLength = (this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read()) + 259;
                    this.processUtf16CharacterString(b);
                    this._eventType = 4;
                    return 4;
                }
                case 12: {
                    boolean addToTable = (b & 0x10) > 0;
                    this._identifier = (b & 2) << 6;
                    int b2 = this.read();
                    this._identifier |= (b2 & 0xFC) >> 2;
                    this.decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(b2);
                    this.decodeRestrictedAlphabetAsCharBuffer();
                    if (addToTable) {
                        this._charactersOffset = this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
                        this._characters = this._characterContentChunkTable._array;
                    } else {
                        this._characters = this._charBuffer;
                        this._charactersOffset = 0;
                    }
                    this._eventType = 4;
                    return 4;
                }
                case 13: {
                    boolean addToTable = (b & 0x10) > 0;
                    this._algorithmId = (b & 2) << 6;
                    int b2 = this.read();
                    this._algorithmId |= (b2 & 0xFC) >> 2;
                    this.decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(b2);
                    this.processCIIEncodingAlgorithm(addToTable);
                    if (this._algorithmId == 9) {
                        this._eventType = 12;
                        return 12;
                    }
                    this._eventType = 4;
                    return 4;
                }
                case 14: {
                    int index;
                    this._characterContentChunkTable._cachedIndex = index = b & 0xF;
                    this._characters = this._characterContentChunkTable._array;
                    this._charactersOffset = this._characterContentChunkTable._offset[index];
                    this._charBufferLength = this._characterContentChunkTable._length[index];
                    this._eventType = 4;
                    return 4;
                }
                case 15: {
                    int index;
                    this._characterContentChunkTable._cachedIndex = index = ((b & 3) << 8 | this.read()) + 16;
                    this._characters = this._characterContentChunkTable._array;
                    this._charactersOffset = this._characterContentChunkTable._offset[index];
                    this._charBufferLength = this._characterContentChunkTable._length[index];
                    this._eventType = 4;
                    return 4;
                }
                case 16: {
                    int index;
                    this._characterContentChunkTable._cachedIndex = index = ((b & 3) << 16 | this.read() << 8 | this.read()) + 1040;
                    this._characters = this._characterContentChunkTable._array;
                    this._charactersOffset = this._characterContentChunkTable._offset[index];
                    this._charBufferLength = this._characterContentChunkTable._length[index];
                    this._eventType = 4;
                    return 4;
                }
                case 17: {
                    int index;
                    this._characterContentChunkTable._cachedIndex = index = (this.read() << 16 | this.read() << 8 | this.read()) + 263184;
                    this._characters = this._characterContentChunkTable._array;
                    this._charactersOffset = this._characterContentChunkTable._offset[index];
                    this._charBufferLength = this._characterContentChunkTable._length[index];
                    this._eventType = 4;
                    return 4;
                }
                case 18: {
                    this.processCommentII();
                    return this._eventType;
                }
                case 19: {
                    this.processProcessingII();
                    return this._eventType;
                }
                case 21: {
                    this.processUnexpandedEntityReference(b);
                    return this.next();
                }
                case 23: {
                    if (this._stackCount != -1) {
                        this.popStack();
                        this._internalState = 3;
                        this._eventType = 2;
                        return 2;
                    }
                    this._internalState = 4;
                    this._eventType = 8;
                    return 8;
                }
                case 22: {
                    if (this._stackCount != -1) {
                        this.popStack();
                        if (this._currentNamespaceAIIsEnd > 0) {
                            this._internalState = 2;
                        }
                        this._eventType = 2;
                        return 2;
                    }
                    this._internalState = 4;
                    this._eventType = 8;
                    return 8;
                }
            }
            throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingEII"));
        }
        catch (IOException e) {
            this.resetOnError();
            logger.log(Level.FINE, "next() exception", e);
            throw new XMLStreamException(e);
        }
        catch (FastInfosetException e) {
            this.resetOnError();
            logger.log(Level.FINE, "next() exception", e);
            throw new XMLStreamException(e);
        }
        catch (RuntimeException e) {
            this.resetOnError();
            logger.log(Level.FINE, "next() exception", e);
            throw e;
        }
    }

    private final void processUtf8CharacterString(int b) throws IOException {
        if ((b & 0x10) > 0) {
            this._characterContentChunkTable.ensureSize(this._octetBufferLength);
            this._characters = this._characterContentChunkTable._array;
            this._charactersOffset = this._characterContentChunkTable._arrayIndex;
            this.decodeUtf8StringAsCharBuffer(this._characterContentChunkTable._array, this._charactersOffset);
            this._characterContentChunkTable.add(this._charBufferLength);
        } else {
            this.decodeUtf8StringAsCharBuffer();
            this._characters = this._charBuffer;
            this._charactersOffset = 0;
        }
    }

    private final void processUtf16CharacterString(int b) throws IOException {
        this.decodeUtf16StringAsCharBuffer();
        if ((b & 0x10) > 0) {
            this._charactersOffset = this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
            this._characters = this._characterContentChunkTable._array;
        } else {
            this._characters = this._charBuffer;
            this._charactersOffset = 0;
        }
    }

    private void popStack() {
        this._qualifiedName = this._qNameStack[this._stackCount];
        this._currentNamespaceAIIsStart = this._namespaceAIIsStartStack[this._stackCount];
        this._currentNamespaceAIIsEnd = this._namespaceAIIsEndStack[this._stackCount];
        this._qNameStack[this._stackCount--] = null;
    }

    @Override
    public final void require(int type, String namespaceURI, String localName) throws XMLStreamException {
        if (type != this._eventType) {
            throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.eventTypeNotMatch", new Object[]{StAXDocumentParser.getEventTypeString(type)}));
        }
        if (namespaceURI != null && !namespaceURI.equals(this.getNamespaceURI())) {
            throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.namespaceURINotMatch", new Object[]{namespaceURI}));
        }
        if (localName != null && !localName.equals(this.getLocalName())) {
            throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.localNameNotMatch", new Object[]{localName}));
        }
    }

    @Override
    public final String getElementText() throws XMLStreamException {
        if (this.getEventType() != 1) {
            throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.mustBeOnSTARTELEMENT"), this.getLocation());
        }
        this.next();
        return this.getElementText(true);
    }

    public final String getElementText(boolean startElementRead) throws XMLStreamException {
        if (!startElementRead) {
            throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.mustBeOnSTARTELEMENT"), this.getLocation());
        }
        int eventType = this.getEventType();
        StringBuilder content = new StringBuilder();
        while (eventType != 2) {
            if (eventType == 4 || eventType == 12 || eventType == 6 || eventType == 9) {
                content.append(this.getText());
            } else if (eventType != 3 && eventType != 5) {
                if (eventType == 8) {
                    throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.unexpectedEOF"));
                }
                if (eventType == 1) {
                    throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.getElementTextExpectTextOnly"), this.getLocation());
                }
                throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.unexpectedEventType") + StAXDocumentParser.getEventTypeString(eventType), this.getLocation());
            }
            eventType = this.next();
        }
        return content.toString();
    }

    @Override
    public final int nextTag() throws XMLStreamException {
        this.next();
        return this.nextTag(true);
    }

    public final int nextTag(boolean currentTagRead) throws XMLStreamException {
        int eventType = this.getEventType();
        if (!currentTagRead) {
            eventType = this.next();
        }
        while (eventType == 4 && this.isWhiteSpace() || eventType == 12 && this.isWhiteSpace() || eventType == 6 || eventType == 3 || eventType == 5) {
            eventType = this.next();
        }
        if (eventType != 1 && eventType != 2) {
            throw new XMLStreamException(CommonResourceBundle.getInstance().getString("message.expectedStartOrEnd"), this.getLocation());
        }
        return eventType;
    }

    @Override
    public final boolean hasNext() throws XMLStreamException {
        return this._eventType != 8;
    }

    @Override
    public void close() throws XMLStreamException {
        try {
            super.closeIfRequired();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    @Override
    public final String getNamespaceURI(String prefix) {
        String namespace = this.getNamespaceDecl(prefix);
        if (namespace == null) {
            if (prefix == null) {
                throw new IllegalArgumentException(CommonResourceBundle.getInstance().getString("message.nullPrefix"));
            }
            return null;
        }
        return namespace;
    }

    @Override
    public final boolean isStartElement() {
        return this._eventType == 1;
    }

    @Override
    public final boolean isEndElement() {
        return this._eventType == 2;
    }

    @Override
    public final boolean isCharacters() {
        return this._eventType == 4;
    }

    @Override
    public final boolean isWhiteSpace() {
        if (this.isCharacters() || this._eventType == 12) {
            char[] ch = this.getTextCharacters();
            int start = this.getTextStart();
            int length = this.getTextLength();
            for (int i = start; i < start + length; ++i) {
                if (XMLChar.isSpace(ch[i])) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    @Override
    public final String getAttributeValue(String namespaceURI, String localName) {
        if (this._eventType != 1) {
            throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetAttributeValue"));
        }
        if (localName == null) {
            throw new IllegalArgumentException();
        }
        if (namespaceURI != null) {
            for (int i = 0; i < this._attributes.getLength(); ++i) {
                if (!this._attributes.getLocalName(i).equals(localName) || !this._attributes.getURI(i).equals(namespaceURI)) continue;
                return this._attributes.getValue(i);
            }
        } else {
            for (int i = 0; i < this._attributes.getLength(); ++i) {
                if (!this._attributes.getLocalName(i).equals(localName)) continue;
                return this._attributes.getValue(i);
            }
        }
        return null;
    }

    @Override
    public final int getAttributeCount() {
        if (this._eventType != 1) {
            throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetAttributeValue"));
        }
        return this._attributes.getLength();
    }

    @Override
    public final QName getAttributeName(int index) {
        if (this._eventType != 1) {
            throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetAttributeValue"));
        }
        return this._attributes.getQualifiedName(index).getQName();
    }

    @Override
    public final String getAttributeNamespace(int index) {
        if (this._eventType != 1) {
            throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetAttributeValue"));
        }
        return this._attributes.getURI(index);
    }

    @Override
    public final String getAttributeLocalName(int index) {
        if (this._eventType != 1) {
            throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetAttributeValue"));
        }
        return this._attributes.getLocalName(index);
    }

    @Override
    public final String getAttributePrefix(int index) {
        if (this._eventType != 1) {
            throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetAttributeValue"));
        }
        return this._attributes.getPrefix(index);
    }

    @Override
    public final String getAttributeType(int index) {
        if (this._eventType != 1) {
            throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetAttributeValue"));
        }
        return this._attributes.getType(index);
    }

    @Override
    public final String getAttributeValue(int index) {
        if (this._eventType != 1) {
            throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetAttributeValue"));
        }
        return this._attributes.getValue(index);
    }

    @Override
    public final boolean isAttributeSpecified(int index) {
        return false;
    }

    @Override
    public final int getNamespaceCount() {
        if (this._eventType == 1 || this._eventType == 2) {
            return this._currentNamespaceAIIsEnd > 0 ? this._currentNamespaceAIIsEnd - this._currentNamespaceAIIsStart : 0;
        }
        throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetNamespaceCount"));
    }

    @Override
    public final String getNamespacePrefix(int index) {
        if (this._eventType == 1 || this._eventType == 2) {
            return this._namespaceAIIsPrefix[this._currentNamespaceAIIsStart + index];
        }
        throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetNamespacePrefix"));
    }

    @Override
    public final String getNamespaceURI(int index) {
        if (this._eventType == 1 || this._eventType == 2) {
            return this._namespaceAIIsNamespaceName[this._currentNamespaceAIIsStart + index];
        }
        throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetNamespacePrefix"));
    }

    @Override
    public final NamespaceContext getNamespaceContext() {
        return this._nsContext;
    }

    @Override
    public final int getEventType() {
        return this._eventType;
    }

    @Override
    public final String getText() {
        if (this._characters == null) {
            this.checkTextState();
        }
        if (this._characters == this._characterContentChunkTable._array) {
            return this._characterContentChunkTable.getString(this._characterContentChunkTable._cachedIndex);
        }
        return new String(this._characters, this._charactersOffset, this._charBufferLength);
    }

    @Override
    public final char[] getTextCharacters() {
        if (this._characters == null) {
            this.checkTextState();
        }
        return this._characters;
    }

    @Override
    public final int getTextStart() {
        if (this._characters == null) {
            this.checkTextState();
        }
        return this._charactersOffset;
    }

    @Override
    public final int getTextLength() {
        if (this._characters == null) {
            this.checkTextState();
        }
        return this._charBufferLength;
    }

    @Override
    public final int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException {
        if (this._characters == null) {
            this.checkTextState();
        }
        try {
            int bytesToCopy = Math.min(this._charBufferLength, length);
            System.arraycopy(this._characters, this._charactersOffset + sourceStart, target, targetStart, bytesToCopy);
            return bytesToCopy;
        }
        catch (IndexOutOfBoundsException e) {
            throw new XMLStreamException(e);
        }
    }

    protected final void checkTextState() {
        if (this._algorithmData == null) {
            throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.InvalidStateForText"));
        }
        try {
            this.convertEncodingAlgorithmDataToCharacters();
        }
        catch (Exception e) {
            throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.InvalidStateForText"));
        }
    }

    @Override
    public final String getEncoding() {
        return this._characterEncodingScheme;
    }

    @Override
    public final boolean hasText() {
        return this._characters != null;
    }

    @Override
    public final Location getLocation() {
        return EventLocation.getNilLocation();
    }

    @Override
    public final QName getName() {
        if (this._eventType == 1 || this._eventType == 2) {
            return this._qualifiedName.getQName();
        }
        throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetName"));
    }

    @Override
    public final String getLocalName() {
        if (this._eventType == 1 || this._eventType == 2) {
            return this._qualifiedName.localName;
        }
        throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetLocalName"));
    }

    @Override
    public final boolean hasName() {
        return this._eventType == 1 || this._eventType == 2;
    }

    @Override
    public final String getNamespaceURI() {
        if (this._eventType == 1 || this._eventType == 2) {
            return this._qualifiedName.namespaceName;
        }
        throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetNamespaceURI"));
    }

    @Override
    public final String getPrefix() {
        if (this._eventType == 1 || this._eventType == 2) {
            return this._qualifiedName.prefix;
        }
        throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetPrefix"));
    }

    @Override
    public final String getVersion() {
        return null;
    }

    @Override
    public final boolean isStandalone() {
        return false;
    }

    @Override
    public final boolean standaloneSet() {
        return false;
    }

    @Override
    public final String getCharacterEncodingScheme() {
        return null;
    }

    @Override
    public final String getPITarget() {
        if (this._eventType != 3) {
            throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetPITarget"));
        }
        return this._piTarget;
    }

    @Override
    public final String getPIData() {
        if (this._eventType != 3) {
            throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetPIData"));
        }
        return this._piData;
    }

    public final String getNameString() {
        if (this._eventType == 1 || this._eventType == 2) {
            return this._qualifiedName.getQNameString();
        }
        throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetName"));
    }

    public final String getAttributeNameString(int index) {
        if (this._eventType != 1) {
            throw new IllegalStateException(CommonResourceBundle.getInstance().getString("message.invalidCallingGetAttributeValue"));
        }
        return this._attributes.getQualifiedName(index).getQNameString();
    }

    public final String getTextAlgorithmURI() {
        return this._algorithmURI;
    }

    public final int getTextAlgorithmIndex() {
        return this._algorithmId;
    }

    public final boolean hasTextAlgorithmBytes() {
        return this._algorithmData != null;
    }

    public final byte[] getTextAlgorithmBytes() {
        if (this._algorithmData == null) {
            return null;
        }
        byte[] algorithmData = new byte[this._algorithmData.length];
        System.arraycopy(this._algorithmData, 0, algorithmData, 0, this._algorithmData.length);
        return algorithmData;
    }

    public final byte[] getTextAlgorithmBytesClone() {
        if (this._algorithmData == null) {
            return null;
        }
        byte[] algorithmData = new byte[this._algorithmDataLength];
        System.arraycopy(this._algorithmData, this._algorithmDataOffset, algorithmData, 0, this._algorithmDataLength);
        return algorithmData;
    }

    public final int getTextAlgorithmStart() {
        return this._algorithmDataOffset;
    }

    public final int getTextAlgorithmLength() {
        return this._algorithmDataLength;
    }

    public final int getTextAlgorithmBytes(int sourceStart, byte[] target, int targetStart, int length) throws XMLStreamException {
        try {
            System.arraycopy(this._algorithmData, sourceStart, target, targetStart, length);
            return length;
        }
        catch (IndexOutOfBoundsException e) {
            throw new XMLStreamException(e);
        }
    }

    @Override
    public final int peekNext() throws XMLStreamException {
        try {
            switch (DecoderStateTables.EII(this.peek(this))) {
                case 0: 
                case 1: 
                case 2: 
                case 3: 
                case 4: 
                case 5: {
                    return 1;
                }
                case 6: 
                case 7: 
                case 8: 
                case 9: 
                case 10: 
                case 11: 
                case 12: 
                case 13: 
                case 14: 
                case 15: 
                case 16: 
                case 17: {
                    return 4;
                }
                case 18: {
                    return 5;
                }
                case 19: {
                    return 3;
                }
                case 21: {
                    return 9;
                }
                case 22: 
                case 23: {
                    return this._stackCount != -1 ? 2 : 8;
                }
            }
            throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingEII"));
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
        catch (FastInfosetException e) {
            throw new XMLStreamException(e);
        }
    }

    @Override
    public void onBeforeOctetBufferOverwrite() {
        if (this._algorithmData != null) {
            this._algorithmData = this.getTextAlgorithmBytesClone();
            this._algorithmDataOffset = 0;
            this._isAlgorithmDataCloned = true;
        }
    }

    @Override
    public final int accessNamespaceCount() {
        return this._currentNamespaceAIIsEnd > 0 ? this._currentNamespaceAIIsEnd - this._currentNamespaceAIIsStart : 0;
    }

    @Override
    public final String accessLocalName() {
        return this._qualifiedName.localName;
    }

    @Override
    public final String accessNamespaceURI() {
        return this._qualifiedName.namespaceName;
    }

    @Override
    public final String accessPrefix() {
        return this._qualifiedName.prefix;
    }

    @Override
    public final char[] accessTextCharacters() {
        if (this._characters == null) {
            return null;
        }
        char[] clonedCharacters = new char[this._characters.length];
        System.arraycopy(this._characters, 0, clonedCharacters, 0, this._characters.length);
        return clonedCharacters;
    }

    @Override
    public final int accessTextStart() {
        return this._charactersOffset;
    }

    @Override
    public final int accessTextLength() {
        return this._charBufferLength;
    }

    protected final void processDII() throws FastInfosetException, IOException {
        int b = this.read();
        if (b > 0) {
            this.processDIIOptionalProperties(b);
        }
    }

    protected final void processDIIOptionalProperties(int b) throws FastInfosetException, IOException {
        if (b == 32) {
            this.decodeInitialVocabulary();
            return;
        }
        if ((b & 0x40) > 0) {
            this.decodeAdditionalData();
        }
        if ((b & 0x20) > 0) {
            this.decodeInitialVocabulary();
        }
        if ((b & 0x10) > 0) {
            this.decodeNotations();
        }
        if ((b & 8) > 0) {
            this.decodeUnparsedEntities();
        }
        if ((b & 4) > 0) {
            this._characterEncodingScheme = this.decodeCharacterEncodingScheme();
        }
        if ((b & 2) > 0) {
            boolean bl;
            boolean bl2 = bl = this.read() > 0;
        }
        if ((b & 1) > 0) {
            this.decodeVersion();
        }
    }

    protected final void resizeNamespaceAIIs() {
        String[] namespaceAIIsPrefix = new String[this._namespaceAIIsIndex * 2];
        System.arraycopy(this._namespaceAIIsPrefix, 0, namespaceAIIsPrefix, 0, this._namespaceAIIsIndex);
        this._namespaceAIIsPrefix = namespaceAIIsPrefix;
        String[] namespaceAIIsNamespaceName = new String[this._namespaceAIIsIndex * 2];
        System.arraycopy(this._namespaceAIIsNamespaceName, 0, namespaceAIIsNamespaceName, 0, this._namespaceAIIsIndex);
        this._namespaceAIIsNamespaceName = namespaceAIIsNamespaceName;
        int[] namespaceAIIsPrefixIndex = new int[this._namespaceAIIsIndex * 2];
        System.arraycopy(this._namespaceAIIsPrefixIndex, 0, namespaceAIIsPrefixIndex, 0, this._namespaceAIIsIndex);
        this._namespaceAIIsPrefixIndex = namespaceAIIsPrefixIndex;
    }

    protected final void processEIIWithNamespaces(boolean hasAttributes) throws FastInfosetException, IOException {
        if (++this._prefixTable._declarationId == Integer.MAX_VALUE) {
            this._prefixTable.clearDeclarationIds();
        }
        this._currentNamespaceAIIsStart = this._namespaceAIIsIndex;
        String prefix = "";
        String namespaceName = "";
        int b = this.read();
        while ((b & 0xFC) == 204) {
            if (this._namespaceAIIsIndex == this._namespaceAIIsPrefix.length) {
                this.resizeNamespaceAIIs();
            }
            switch (b & 3) {
                case 0: {
                    this._namespaceAIIsNamespaceName[this._namespaceAIIsIndex] = "";
                    this._namespaceAIIsPrefix[this._namespaceAIIsIndex] = "";
                    namespaceName = "";
                    prefix = "";
                    this._namespaceAIIsPrefixIndex[this._namespaceAIIsIndex++] = -1;
                    this._prefixIndex = -1;
                    this._namespaceNameIndex = -1;
                    break;
                }
                case 1: {
                    this._namespaceAIIsPrefix[this._namespaceAIIsIndex] = "";
                    prefix = "";
                    namespaceName = this._namespaceAIIsNamespaceName[this._namespaceAIIsIndex] = this.decodeIdentifyingNonEmptyStringOnFirstBitAsNamespaceName(false);
                    this._namespaceAIIsPrefixIndex[this._namespaceAIIsIndex++] = -1;
                    this._prefixIndex = -1;
                    break;
                }
                case 2: {
                    prefix = this._namespaceAIIsPrefix[this._namespaceAIIsIndex] = this.decodeIdentifyingNonEmptyStringOnFirstBitAsPrefix(false);
                    this._namespaceAIIsNamespaceName[this._namespaceAIIsIndex] = "";
                    namespaceName = "";
                    this._namespaceNameIndex = -1;
                    this._namespaceAIIsPrefixIndex[this._namespaceAIIsIndex++] = this._prefixIndex;
                    break;
                }
                case 3: {
                    prefix = this._namespaceAIIsPrefix[this._namespaceAIIsIndex] = this.decodeIdentifyingNonEmptyStringOnFirstBitAsPrefix(true);
                    namespaceName = this._namespaceAIIsNamespaceName[this._namespaceAIIsIndex] = this.decodeIdentifyingNonEmptyStringOnFirstBitAsNamespaceName(true);
                    this._namespaceAIIsPrefixIndex[this._namespaceAIIsIndex++] = this._prefixIndex;
                }
            }
            this._prefixTable.pushScopeWithPrefixEntry(prefix, namespaceName, this._prefixIndex, this._namespaceNameIndex);
            b = this.read();
        }
        if (b != 240) {
            throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.EIInamespaceNameNotTerminatedCorrectly"));
        }
        this._currentNamespaceAIIsEnd = this._namespaceAIIsIndex;
        b = this.read();
        switch (DecoderStateTables.EII(b)) {
            case 0: {
                this.processEII(this._elementNameTable._array[b], hasAttributes);
                break;
            }
            case 2: {
                this.processEII(this.processEIIIndexMedium(b), hasAttributes);
                break;
            }
            case 3: {
                this.processEII(this.processEIIIndexLarge(b), hasAttributes);
                break;
            }
            case 5: {
                QualifiedName qn = this.processLiteralQualifiedName(b & 3, this._elementNameTable.getNext());
                this._elementNameTable.add(qn);
                this.processEII(qn, hasAttributes);
                break;
            }
            default: {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingEIIAfterAIIs"));
            }
        }
    }

    protected final void processEII(QualifiedName name, boolean hasAttributes) throws FastInfosetException, IOException {
        if (this._prefixTable._currentInScope[name.prefixIndex] != name.namespaceNameIndex) {
            throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.qnameOfEIINotInScope"));
        }
        this._eventType = 1;
        this._qualifiedName = name;
        if (this._clearAttributes) {
            this._attributes.clear();
            this._clearAttributes = false;
        }
        if (hasAttributes) {
            this.processAIIs();
        }
        ++this._stackCount;
        if (this._stackCount == this._qNameStack.length) {
            QualifiedName[] qNameStack = new QualifiedName[this._qNameStack.length * 2];
            System.arraycopy(this._qNameStack, 0, qNameStack, 0, this._qNameStack.length);
            this._qNameStack = qNameStack;
            int[] namespaceAIIsStartStack = new int[this._namespaceAIIsStartStack.length * 2];
            System.arraycopy(this._namespaceAIIsStartStack, 0, namespaceAIIsStartStack, 0, this._namespaceAIIsStartStack.length);
            this._namespaceAIIsStartStack = namespaceAIIsStartStack;
            int[] namespaceAIIsEndStack = new int[this._namespaceAIIsEndStack.length * 2];
            System.arraycopy(this._namespaceAIIsEndStack, 0, namespaceAIIsEndStack, 0, this._namespaceAIIsEndStack.length);
            this._namespaceAIIsEndStack = namespaceAIIsEndStack;
        }
        this._qNameStack[this._stackCount] = this._qualifiedName;
        this._namespaceAIIsStartStack[this._stackCount] = this._currentNamespaceAIIsStart;
        this._namespaceAIIsEndStack[this._stackCount] = this._currentNamespaceAIIsEnd;
    }

    protected final void processAIIs() throws FastInfosetException, IOException {
        if (++this._duplicateAttributeVerifier._currentIteration == Integer.MAX_VALUE) {
            this._duplicateAttributeVerifier.clear();
        }
        this._clearAttributes = true;
        boolean terminate = false;
        block22: do {
            QualifiedName name;
            int b = this.read();
            switch (DecoderStateTables.AII(b)) {
                case 0: {
                    name = this._attributeNameTable._array[b];
                    break;
                }
                case 1: {
                    int i = ((b & 0x1F) << 8 | this.read()) + 64;
                    name = this._attributeNameTable._array[i];
                    break;
                }
                case 2: {
                    int i = ((b & 0xF) << 16 | this.read() << 8 | this.read()) + 8256;
                    name = this._attributeNameTable._array[i];
                    break;
                }
                case 3: {
                    name = this.processLiteralQualifiedName(b & 3, this._attributeNameTable.getNext());
                    name.createAttributeValues(256);
                    this._attributeNameTable.add(name);
                    break;
                }
                case 5: {
                    this._internalState = 1;
                }
                case 4: {
                    terminate = true;
                    continue block22;
                }
                default: {
                    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingAIIs"));
                }
            }
            if (name.prefixIndex > 0 && this._prefixTable._currentInScope[name.prefixIndex] != name.namespaceNameIndex) {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.AIIqNameNotInScope"));
            }
            this._duplicateAttributeVerifier.checkForDuplicateAttribute(name.attributeHash, name.attributeId);
            b = this.read();
            switch (DecoderStateTables.NISTRING(b)) {
                case 0: {
                    this._octetBufferLength = (b & 7) + 1;
                    String value = this.decodeUtf8StringAsString();
                    if ((b & 0x40) > 0) {
                        this._attributeValueTable.add(value);
                    }
                    this._attributes.addAttribute(name, value);
                    break;
                }
                case 1: {
                    this._octetBufferLength = this.read() + 9;
                    String value = this.decodeUtf8StringAsString();
                    if ((b & 0x40) > 0) {
                        this._attributeValueTable.add(value);
                    }
                    this._attributes.addAttribute(name, value);
                    break;
                }
                case 2: {
                    this._octetBufferLength = (this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read()) + 265;
                    String value = this.decodeUtf8StringAsString();
                    if ((b & 0x40) > 0) {
                        this._attributeValueTable.add(value);
                    }
                    this._attributes.addAttribute(name, value);
                    break;
                }
                case 3: {
                    this._octetBufferLength = (b & 7) + 1;
                    String value = this.decodeUtf16StringAsString();
                    if ((b & 0x40) > 0) {
                        this._attributeValueTable.add(value);
                    }
                    this._attributes.addAttribute(name, value);
                    break;
                }
                case 4: {
                    this._octetBufferLength = this.read() + 9;
                    String value = this.decodeUtf16StringAsString();
                    if ((b & 0x40) > 0) {
                        this._attributeValueTable.add(value);
                    }
                    this._attributes.addAttribute(name, value);
                    break;
                }
                case 5: {
                    this._octetBufferLength = (this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read()) + 265;
                    String value = this.decodeUtf16StringAsString();
                    if ((b & 0x40) > 0) {
                        this._attributeValueTable.add(value);
                    }
                    this._attributes.addAttribute(name, value);
                    break;
                }
                case 6: {
                    boolean addToTable = (b & 0x40) > 0;
                    this._identifier = (b & 0xF) << 4;
                    b = this.read();
                    this._identifier |= (b & 0xF0) >> 4;
                    this.decodeOctetsOnFifthBitOfNonIdentifyingStringOnFirstBit(b);
                    String value = this.decodeRestrictedAlphabetAsString();
                    if (addToTable) {
                        this._attributeValueTable.add(value);
                    }
                    this._attributes.addAttribute(name, value);
                    break;
                }
                case 7: {
                    boolean addToTable = (b & 0x40) > 0;
                    this._identifier = (b & 0xF) << 4;
                    b = this.read();
                    this._identifier |= (b & 0xF0) >> 4;
                    this.decodeOctetsOnFifthBitOfNonIdentifyingStringOnFirstBit(b);
                    this.processAIIEncodingAlgorithm(name, addToTable);
                    break;
                }
                case 8: {
                    this._attributes.addAttribute(name, this._attributeValueTable._array[b & 0x3F]);
                    break;
                }
                case 9: {
                    int index = ((b & 0x1F) << 8 | this.read()) + 64;
                    this._attributes.addAttribute(name, this._attributeValueTable._array[index]);
                    break;
                }
                case 10: {
                    int index = ((b & 0xF) << 16 | this.read() << 8 | this.read()) + 8256;
                    this._attributes.addAttribute(name, this._attributeValueTable._array[index]);
                    break;
                }
                case 11: {
                    this._attributes.addAttribute(name, "");
                    break;
                }
                default: {
                    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingAIIValue"));
                }
            }
        } while (!terminate);
        this._duplicateAttributeVerifier._poolCurrent = this._duplicateAttributeVerifier._poolHead;
    }

    protected final QualifiedName processEIIIndexMedium(int b) throws FastInfosetException, IOException {
        int i = ((b & 7) << 8 | this.read()) + 32;
        return this._elementNameTable._array[i];
    }

    protected final QualifiedName processEIIIndexLarge(int b) throws FastInfosetException, IOException {
        int i = (b & 0x30) == 32 ? ((b & 7) << 16 | this.read() << 8 | this.read()) + 2080 : ((this.read() & 0xF) << 16 | this.read() << 8 | this.read()) + 526368;
        return this._elementNameTable._array[i];
    }

    protected final QualifiedName processLiteralQualifiedName(int state, QualifiedName q) throws FastInfosetException, IOException {
        if (q == null) {
            q = new QualifiedName();
        }
        switch (state) {
            case 0: {
                return q.set("", "", this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), "", 0, -1, -1, this._identifier);
            }
            case 1: {
                return q.set("", this.decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(false), this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), "", 0, -1, this._namespaceNameIndex, this._identifier);
            }
            case 2: {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.qNameMissingNamespaceName"));
            }
            case 3: {
                return q.set(this.decodeIdentifyingNonEmptyStringIndexOnFirstBitAsPrefix(true), this.decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(true), this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), "", 0, this._prefixIndex, this._namespaceNameIndex, this._identifier);
            }
        }
        throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingEII"));
    }

    protected final void processCommentII() throws FastInfosetException, IOException {
        this._eventType = 5;
        switch (this.decodeNonIdentifyingStringOnFirstBit()) {
            case 0: {
                if (this._addToTable) {
                    this._v.otherString.add(new CharArray(this._charBuffer, 0, this._charBufferLength, true));
                }
                this._characters = this._charBuffer;
                this._charactersOffset = 0;
                break;
            }
            case 2: {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.commentIIAlgorithmNotSupported"));
            }
            case 1: {
                CharArray ca = this._v.otherString.get(this._integer);
                this._characters = ca.ch;
                this._charactersOffset = ca.start;
                this._charBufferLength = ca.length;
                break;
            }
            case 3: {
                this._characters = this._charBuffer;
                this._charactersOffset = 0;
                this._charBufferLength = 0;
            }
        }
    }

    protected final void processProcessingII() throws FastInfosetException, IOException {
        this._eventType = 3;
        this._piTarget = this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherNCName);
        switch (this.decodeNonIdentifyingStringOnFirstBit()) {
            case 0: {
                this._piData = new String(this._charBuffer, 0, this._charBufferLength);
                if (!this._addToTable) break;
                this._v.otherString.add(new CharArrayString(this._piData));
                break;
            }
            case 2: {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.processingIIWithEncodingAlgorithm"));
            }
            case 1: {
                this._piData = this._v.otherString.get(this._integer).toString();
                break;
            }
            case 3: {
                this._piData = "";
            }
        }
    }

    protected final void processUnexpandedEntityReference(int b) throws FastInfosetException, IOException {
        String public_identifier;
        this._eventType = 9;
        String entity_reference_name = this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherNCName);
        String system_identifier = (b & 2) > 0 ? this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : "";
        String string = public_identifier = (b & 1) > 0 ? this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : "";
        if (logger.isLoggable(Level.FINEST)) {
            logger.log(Level.FINEST, "processUnexpandedEntityReference: entity_reference_name={0} system_identifier={1}public_identifier={2}", new Object[]{entity_reference_name, system_identifier, public_identifier});
        }
    }

    protected final void processCIIEncodingAlgorithm(boolean addToTable) throws FastInfosetException, IOException {
        this._algorithmData = this._octetBuffer;
        this._algorithmDataOffset = this._octetBufferStart;
        this._algorithmDataLength = this._octetBufferLength;
        this._isAlgorithmDataCloned = false;
        if (this._algorithmId >= 32) {
            this._algorithmURI = this._v.encodingAlgorithm.get(this._algorithmId - 32);
            if (this._algorithmURI == null) {
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.URINotPresent", new Object[]{this._identifier}));
            }
        } else if (this._algorithmId > 9) {
            throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.identifiers10to31Reserved"));
        }
        if (addToTable) {
            this.convertEncodingAlgorithmDataToCharacters();
            this._characterContentChunkTable.add(this._characters, this._characters.length);
        }
    }

    protected final void processAIIEncodingAlgorithm(QualifiedName name, boolean addToTable) throws FastInfosetException, IOException {
        Object algorithmData;
        EncodingAlgorithm ea = null;
        String URI2 = null;
        if (this._identifier >= 32) {
            URI2 = this._v.encodingAlgorithm.get(this._identifier - 32);
            if (URI2 == null) {
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.URINotPresent", new Object[]{this._identifier}));
            }
            if (this._registeredEncodingAlgorithms != null) {
                ea = (EncodingAlgorithm)this._registeredEncodingAlgorithms.get(URI2);
            }
        } else {
            if (this._identifier >= 9) {
                if (this._identifier == 9) {
                    throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.CDATAAlgorithmNotSupported"));
                }
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.identifiers10to31Reserved"));
            }
            ea = BuiltInEncodingAlgorithmFactory.getAlgorithm(this._identifier);
        }
        if (ea != null) {
            algorithmData = ea.decodeFromBytes(this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
        } else {
            byte[] data = new byte[this._octetBufferLength];
            System.arraycopy(this._octetBuffer, this._octetBufferStart, data, 0, this._octetBufferLength);
            algorithmData = data;
        }
        this._attributes.addAttributeWithAlgorithmData(name, URI2, this._identifier, algorithmData);
        if (addToTable) {
            this._attributeValueTable.add(this._attributes.getValue(this._attributes.getIndex(name.qName)));
        }
    }

    protected final void convertEncodingAlgorithmDataToCharacters() throws FastInfosetException, IOException {
        StringBuffer buffer = new StringBuffer();
        if (this._algorithmId == 1) {
            this.convertBase64AlorithmDataToCharacters(buffer);
        } else if (this._algorithmId < 9) {
            Object array = BuiltInEncodingAlgorithmFactory.getAlgorithm(this._algorithmId).decodeFromBytes(this._algorithmData, this._algorithmDataOffset, this._algorithmDataLength);
            BuiltInEncodingAlgorithmFactory.getAlgorithm(this._algorithmId).convertToCharacters(array, buffer);
        } else {
            if (this._algorithmId == 9) {
                this._octetBufferOffset -= this._octetBufferLength;
                this.decodeUtf8StringIntoCharBuffer();
                this._characters = this._charBuffer;
                this._charactersOffset = 0;
                return;
            }
            if (this._algorithmId >= 32) {
                EncodingAlgorithm ea = (EncodingAlgorithm)this._registeredEncodingAlgorithms.get(this._algorithmURI);
                if (ea != null) {
                    Object data = ea.decodeFromBytes(this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
                    ea.convertToCharacters(data, buffer);
                } else {
                    throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.algorithmDataCannotBeReported"));
                }
            }
        }
        this._characters = new char[buffer.length()];
        buffer.getChars(0, buffer.length(), this._characters, 0);
        this._charactersOffset = 0;
        this._charBufferLength = this._characters.length;
    }

    protected void convertBase64AlorithmDataToCharacters(StringBuffer buffer) throws EncodingAlgorithmException, IOException {
        int taleBytesRemaining;
        int afterTaleOffset = 0;
        if (this.base64TaleLength > 0) {
            int bytesToCopy = Math.min(3 - this.base64TaleLength, this._algorithmDataLength);
            System.arraycopy(this._algorithmData, this._algorithmDataOffset, this.base64TaleBytes, this.base64TaleLength, bytesToCopy);
            if (this.base64TaleLength + bytesToCopy != 3) {
                if (!this.isBase64Follows()) {
                    this.base64DecodeWithCloning(buffer, this.base64TaleBytes, 0, this.base64TaleLength + bytesToCopy);
                    return;
                }
                this.base64TaleLength += bytesToCopy;
                return;
            }
            this.base64DecodeWithCloning(buffer, this.base64TaleBytes, 0, 3);
            afterTaleOffset = bytesToCopy;
            this.base64TaleLength = 0;
        }
        int n = taleBytesRemaining = this.isBase64Follows() ? (this._algorithmDataLength - afterTaleOffset) % 3 : 0;
        if (this._isAlgorithmDataCloned) {
            this.base64DecodeWithoutCloning(buffer, this._algorithmData, this._algorithmDataOffset + afterTaleOffset, this._algorithmDataLength - afterTaleOffset - taleBytesRemaining);
        } else {
            this.base64DecodeWithCloning(buffer, this._algorithmData, this._algorithmDataOffset + afterTaleOffset, this._algorithmDataLength - afterTaleOffset - taleBytesRemaining);
        }
        if (taleBytesRemaining > 0) {
            System.arraycopy(this._algorithmData, this._algorithmDataOffset + this._algorithmDataLength - taleBytesRemaining, this.base64TaleBytes, 0, taleBytesRemaining);
            this.base64TaleLength = taleBytesRemaining;
        }
    }

    private void base64DecodeWithCloning(StringBuffer dstBuffer, byte[] data, int offset, int length) throws EncodingAlgorithmException {
        Object array = BuiltInEncodingAlgorithmFactory.base64EncodingAlgorithm.decodeFromBytes(data, offset, length);
        BuiltInEncodingAlgorithmFactory.base64EncodingAlgorithm.convertToCharacters(array, dstBuffer);
    }

    private void base64DecodeWithoutCloning(StringBuffer dstBuffer, byte[] data, int offset, int length) throws EncodingAlgorithmException {
        BuiltInEncodingAlgorithmFactory.base64EncodingAlgorithm.convertToCharacters(data, offset, length, dstBuffer);
    }

    public boolean isBase64Follows() throws IOException {
        int b = this.peek(this);
        switch (DecoderStateTables.EII(b)) {
            case 13: {
                int algorithmId = (b & 2) << 6;
                int b2 = this.peek2(this);
                return (algorithmId |= (b2 & 0xFC) >> 2) == 1;
            }
        }
        return false;
    }

    public final String getNamespaceDecl(String prefix) {
        return this._prefixTable.getNamespaceFromPrefix(prefix);
    }

    public final String getURI(String prefix) {
        return this.getNamespaceDecl(prefix);
    }

    public final Iterator getPrefixes() {
        return this._prefixTable.getPrefixes();
    }

    public final AttributesHolder getAttributesHolder() {
        return this._attributes;
    }

    public final void setManager(StAXManager manager) {
        this._manager = manager;
    }

    static final String getEventTypeString(int eventType) {
        switch (eventType) {
            case 1: {
                return "START_ELEMENT";
            }
            case 2: {
                return "END_ELEMENT";
            }
            case 3: {
                return "PROCESSING_INSTRUCTION";
            }
            case 4: {
                return "CHARACTERS";
            }
            case 5: {
                return "COMMENT";
            }
            case 7: {
                return "START_DOCUMENT";
            }
            case 8: {
                return "END_DOCUMENT";
            }
            case 9: {
                return "ENTITY_REFERENCE";
            }
            case 10: {
                return "ATTRIBUTE";
            }
            case 11: {
                return "DTD";
            }
            case 12: {
                return "CDATA";
            }
        }
        return "UNKNOWN_EVENT_TYPE";
    }

    protected class NamespaceContextImpl
    implements NamespaceContext {
        protected NamespaceContextImpl() {
        }

        @Override
        public final String getNamespaceURI(String prefix) {
            return StAXDocumentParser.this._prefixTable.getNamespaceFromPrefix(prefix);
        }

        @Override
        public final String getPrefix(String namespaceURI) {
            return StAXDocumentParser.this._prefixTable.getPrefixFromNamespace(namespaceURI);
        }

        public final Iterator getPrefixes(String namespaceURI) {
            return StAXDocumentParser.this._prefixTable.getPrefixesFromNamespace(namespaceURI);
        }
    }
}

