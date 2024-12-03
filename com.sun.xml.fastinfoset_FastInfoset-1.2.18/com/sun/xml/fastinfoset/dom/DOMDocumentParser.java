/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.dom;

import com.sun.xml.fastinfoset.CommonResourceBundle;
import com.sun.xml.fastinfoset.Decoder;
import com.sun.xml.fastinfoset.DecoderStateTables;
import com.sun.xml.fastinfoset.QualifiedName;
import com.sun.xml.fastinfoset.algorithm.BuiltInEncodingAlgorithmFactory;
import com.sun.xml.fastinfoset.util.CharArray;
import com.sun.xml.fastinfoset.util.CharArrayString;
import java.io.IOException;
import java.io.InputStream;
import org.jvnet.fastinfoset.EncodingAlgorithm;
import org.jvnet.fastinfoset.EncodingAlgorithmException;
import org.jvnet.fastinfoset.FastInfosetException;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

public class DOMDocumentParser
extends Decoder {
    protected Document _document;
    protected Node _currentNode;
    protected Element _currentElement;
    protected Attr[] _namespaceAttributes = new Attr[16];
    protected int _namespaceAttributesIndex;
    protected int[] _namespacePrefixes = new int[16];
    protected int _namespacePrefixesIndex;

    public void parse(Document d, InputStream s) throws FastInfosetException, IOException {
        this._document = d;
        this._currentNode = this._document;
        this._namespaceAttributesIndex = 0;
        this.parse(s);
    }

    protected final void parse(InputStream s) throws FastInfosetException, IOException {
        this.setInputStream(s);
        this.parse();
    }

    protected void resetOnError() {
        this._namespacePrefixesIndex = 0;
        if (this._v == null) {
            this._prefixTable.clearCompletely();
        }
        this._duplicateAttributeVerifier.clear();
    }

    protected final void parse() throws FastInfosetException, IOException {
        try {
            this.reset();
            this.decodeHeader();
            this.processDII();
        }
        catch (RuntimeException e) {
            this.resetOnError();
            throw new FastInfosetException(e);
        }
        catch (FastInfosetException e) {
            this.resetOnError();
            throw e;
        }
        catch (IOException e) {
            this.resetOnError();
            throw e;
        }
    }

    protected final void processDII() throws FastInfosetException, IOException {
        this._b = this.read();
        if (this._b > 0) {
            this.processDIIOptionalProperties();
        }
        boolean firstElementHasOccured = false;
        boolean documentTypeDeclarationOccured = false;
        block24: while (!this._terminate || !firstElementHasOccured) {
            this._b = this.read();
            switch (DecoderStateTables.DII(this._b)) {
                case 0: {
                    this.processEII(this._elementNameTable._array[this._b], false);
                    firstElementHasOccured = true;
                    continue block24;
                }
                case 1: {
                    this.processEII(this._elementNameTable._array[this._b & 0x1F], true);
                    firstElementHasOccured = true;
                    continue block24;
                }
                case 2: {
                    this.processEII(this.decodeEIIIndexMedium(), (this._b & 0x40) > 0);
                    firstElementHasOccured = true;
                    continue block24;
                }
                case 3: {
                    this.processEII(this.decodeEIIIndexLarge(), (this._b & 0x40) > 0);
                    firstElementHasOccured = true;
                    continue block24;
                }
                case 5: {
                    QualifiedName qn = this.processLiteralQualifiedName(this._b & 3, this._elementNameTable.getNext());
                    this._elementNameTable.add(qn);
                    this.processEII(qn, (this._b & 0x40) > 0);
                    firstElementHasOccured = true;
                    continue block24;
                }
                case 4: {
                    this.processEIIWithNamespaces();
                    firstElementHasOccured = true;
                    continue block24;
                }
                case 20: {
                    if (documentTypeDeclarationOccured) {
                        throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.secondOccurenceOfDTDII"));
                    }
                    documentTypeDeclarationOccured = true;
                    String system_identifier = (this._b & 2) > 0 ? this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : null;
                    String public_identifier = (this._b & 1) > 0 ? this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : null;
                    this._b = this.read();
                    while (this._b == 225) {
                        switch (this.decodeNonIdentifyingStringOnFirstBit()) {
                            case 0: {
                                if (!this._addToTable) break;
                                this._v.otherString.add(new CharArray(this._charBuffer, 0, this._charBufferLength, true));
                                break;
                            }
                            case 2: {
                                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.processingIIWithEncodingAlgorithm"));
                            }
                            case 1: {
                                break;
                            }
                        }
                        this._b = this.read();
                    }
                    if ((this._b & 0xF0) != 240) {
                        throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.processingInstructionIIsNotTerminatedCorrectly"));
                    }
                    if (this._b == 255) {
                        this._terminate = true;
                    }
                    this._notations.clear();
                    this._unparsedEntities.clear();
                    continue block24;
                }
                case 18: {
                    this.processCommentII();
                    continue block24;
                }
                case 19: {
                    this.processProcessingII();
                    continue block24;
                }
                case 23: {
                    this._doubleTerminate = true;
                }
                case 22: {
                    this._terminate = true;
                    continue block24;
                }
            }
            throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingDII"));
        }
        block26: while (!this._terminate) {
            this._b = this.read();
            switch (DecoderStateTables.DII(this._b)) {
                case 18: {
                    this.processCommentII();
                    continue block26;
                }
                case 19: {
                    this.processProcessingII();
                    continue block26;
                }
                case 23: {
                    this._doubleTerminate = true;
                }
                case 22: {
                    this._terminate = true;
                    continue block26;
                }
            }
            throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingDII"));
        }
    }

    protected final void processDIIOptionalProperties() throws FastInfosetException, IOException {
        if (this._b == 32) {
            this.decodeInitialVocabulary();
            return;
        }
        if ((this._b & 0x40) > 0) {
            this.decodeAdditionalData();
        }
        if ((this._b & 0x20) > 0) {
            this.decodeInitialVocabulary();
        }
        if ((this._b & 0x10) > 0) {
            this.decodeNotations();
        }
        if ((this._b & 8) > 0) {
            this.decodeUnparsedEntities();
        }
        if ((this._b & 4) > 0) {
            this.decodeCharacterEncodingScheme();
        }
        if ((this._b & 2) > 0) {
            this.read();
        }
        if ((this._b & 1) > 0) {
            this.decodeVersion();
        }
    }

    protected final void processEII(QualifiedName name, boolean hasAttributes) throws FastInfosetException, IOException {
        if (this._prefixTable._currentInScope[name.prefixIndex] != name.namespaceNameIndex) {
            throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.qnameOfEIINotInScope"));
        }
        Node parentCurrentNode = this._currentNode;
        this._currentElement = this.createElement(name.namespaceName, name.qName, name.localName);
        this._currentNode = this._currentElement;
        if (this._namespaceAttributesIndex > 0) {
            for (int i = 0; i < this._namespaceAttributesIndex; ++i) {
                this._currentElement.setAttributeNode(this._namespaceAttributes[i]);
                this._namespaceAttributes[i] = null;
            }
            this._namespaceAttributesIndex = 0;
        }
        if (hasAttributes) {
            this.processAIIs();
        }
        parentCurrentNode.appendChild(this._currentElement);
        block26: while (!this._terminate) {
            this._b = this.read();
            switch (DecoderStateTables.EII(this._b)) {
                case 0: {
                    this.processEII(this._elementNameTable._array[this._b], false);
                    continue block26;
                }
                case 1: {
                    this.processEII(this._elementNameTable._array[this._b & 0x1F], true);
                    continue block26;
                }
                case 2: {
                    this.processEII(this.decodeEIIIndexMedium(), (this._b & 0x40) > 0);
                    continue block26;
                }
                case 3: {
                    this.processEII(this.decodeEIIIndexLarge(), (this._b & 0x40) > 0);
                    continue block26;
                }
                case 5: {
                    QualifiedName qn = this.processLiteralQualifiedName(this._b & 3, this._elementNameTable.getNext());
                    this._elementNameTable.add(qn);
                    this.processEII(qn, (this._b & 0x40) > 0);
                    continue block26;
                }
                case 4: {
                    this.processEIIWithNamespaces();
                    continue block26;
                }
                case 6: {
                    this._octetBufferLength = (this._b & 1) + 1;
                    this.appendOrCreateTextData(this.processUtf8CharacterString());
                    continue block26;
                }
                case 7: {
                    this._octetBufferLength = this.read() + 3;
                    this.appendOrCreateTextData(this.processUtf8CharacterString());
                    continue block26;
                }
                case 8: {
                    this._octetBufferLength = this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read();
                    this._octetBufferLength += 259;
                    this.appendOrCreateTextData(this.processUtf8CharacterString());
                    continue block26;
                }
                case 9: {
                    this._octetBufferLength = (this._b & 1) + 1;
                    String v = this.decodeUtf16StringAsString();
                    if ((this._b & 0x10) > 0) {
                        this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
                    }
                    this.appendOrCreateTextData(v);
                    continue block26;
                }
                case 10: {
                    this._octetBufferLength = this.read() + 3;
                    String v = this.decodeUtf16StringAsString();
                    if ((this._b & 0x10) > 0) {
                        this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
                    }
                    this.appendOrCreateTextData(v);
                    continue block26;
                }
                case 11: {
                    this._octetBufferLength = this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read();
                    this._octetBufferLength += 259;
                    String v = this.decodeUtf16StringAsString();
                    if ((this._b & 0x10) > 0) {
                        this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
                    }
                    this.appendOrCreateTextData(v);
                    continue block26;
                }
                case 12: {
                    boolean addToTable = (this._b & 0x10) > 0;
                    this._identifier = (this._b & 2) << 6;
                    this._b = this.read();
                    this._identifier |= (this._b & 0xFC) >> 2;
                    this.decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(this._b);
                    String v = this.decodeRestrictedAlphabetAsString();
                    if (addToTable) {
                        this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
                    }
                    this.appendOrCreateTextData(v);
                    continue block26;
                }
                case 13: {
                    boolean addToTable = (this._b & 0x10) > 0;
                    this._identifier = (this._b & 2) << 6;
                    this._b = this.read();
                    this._identifier |= (this._b & 0xFC) >> 2;
                    this.decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(this._b);
                    String s = this.convertEncodingAlgorithmDataToCharacters(false);
                    if (addToTable) {
                        this._characterContentChunkTable.add(s.toCharArray(), s.length());
                    }
                    this.appendOrCreateTextData(s);
                    continue block26;
                }
                case 14: {
                    String s = this._characterContentChunkTable.getString(this._b & 0xF);
                    this.appendOrCreateTextData(s);
                    continue block26;
                }
                case 15: {
                    int index = ((this._b & 3) << 8 | this.read()) + 16;
                    String s = this._characterContentChunkTable.getString(index);
                    this.appendOrCreateTextData(s);
                    continue block26;
                }
                case 16: {
                    int index = (this._b & 3) << 16 | this.read() << 8 | this.read();
                    String s = this._characterContentChunkTable.getString(index += 1040);
                    this.appendOrCreateTextData(s);
                    continue block26;
                }
                case 17: {
                    int index = this.read() << 16 | this.read() << 8 | this.read();
                    String s = this._characterContentChunkTable.getString(index += 263184);
                    this.appendOrCreateTextData(s);
                    continue block26;
                }
                case 18: {
                    this.processCommentII();
                    continue block26;
                }
                case 19: {
                    this.processProcessingII();
                    continue block26;
                }
                case 21: {
                    String entity_reference_name = this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherNCName);
                    String system_identifier = (this._b & 2) > 0 ? this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : null;
                    String public_identifier = (this._b & 1) > 0 ? this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : null;
                    continue block26;
                }
                case 23: {
                    this._doubleTerminate = true;
                }
                case 22: {
                    this._terminate = true;
                    continue block26;
                }
            }
            throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingEII"));
        }
        this._terminate = this._doubleTerminate;
        this._doubleTerminate = false;
        this._currentNode = parentCurrentNode;
    }

    private void appendOrCreateTextData(String textData) {
        Node lastChild = this._currentNode.getLastChild();
        if (lastChild instanceof Text) {
            ((Text)lastChild).appendData(textData);
        } else {
            this._currentNode.appendChild(this._document.createTextNode(textData));
        }
    }

    private final String processUtf8CharacterString() throws FastInfosetException, IOException {
        if ((this._b & 0x10) > 0) {
            this._characterContentChunkTable.ensureSize(this._octetBufferLength);
            int charactersOffset = this._characterContentChunkTable._arrayIndex;
            this.decodeUtf8StringAsCharBuffer(this._characterContentChunkTable._array, charactersOffset);
            this._characterContentChunkTable.add(this._charBufferLength);
            return this._characterContentChunkTable.getString(this._characterContentChunkTable._cachedIndex);
        }
        this.decodeUtf8StringAsCharBuffer();
        return new String(this._charBuffer, 0, this._charBufferLength);
    }

    protected final void processEIIWithNamespaces() throws FastInfosetException, IOException {
        boolean hasAttributes;
        boolean bl = hasAttributes = (this._b & 0x40) > 0;
        if (++this._prefixTable._declarationId == Integer.MAX_VALUE) {
            this._prefixTable.clearDeclarationIds();
        }
        Attr a = null;
        int start = this._namespacePrefixesIndex;
        int b = this.read();
        while ((b & 0xFC) == 204) {
            if (this._namespaceAttributesIndex == this._namespaceAttributes.length) {
                Attr[] newNamespaceAttributes = new Attr[this._namespaceAttributesIndex * 3 / 2 + 1];
                System.arraycopy(this._namespaceAttributes, 0, newNamespaceAttributes, 0, this._namespaceAttributesIndex);
                this._namespaceAttributes = newNamespaceAttributes;
            }
            if (this._namespacePrefixesIndex == this._namespacePrefixes.length) {
                int[] namespaceAIIs = new int[this._namespacePrefixesIndex * 3 / 2 + 1];
                System.arraycopy(this._namespacePrefixes, 0, namespaceAIIs, 0, this._namespacePrefixesIndex);
                this._namespacePrefixes = namespaceAIIs;
            }
            switch (b & 3) {
                case 0: {
                    a = this.createAttribute("http://www.w3.org/2000/xmlns/", "xmlns", "xmlns");
                    a.setValue("");
                    this._namespacePrefixes[this._namespacePrefixesIndex++] = -1;
                    this._namespaceNameIndex = -1;
                    this._prefixIndex = -1;
                    break;
                }
                case 1: {
                    a = this.createAttribute("http://www.w3.org/2000/xmlns/", "xmlns", "xmlns");
                    a.setValue(this.decodeIdentifyingNonEmptyStringOnFirstBitAsNamespaceName(false));
                    this._namespacePrefixes[this._namespacePrefixesIndex++] = -1;
                    this._prefixIndex = -1;
                    break;
                }
                case 2: {
                    String prefix = this.decodeIdentifyingNonEmptyStringOnFirstBitAsPrefix(false);
                    a = this.createAttribute("http://www.w3.org/2000/xmlns/", this.createQualifiedNameString(prefix), prefix);
                    a.setValue("");
                    this._namespaceNameIndex = -1;
                    this._namespacePrefixes[this._namespacePrefixesIndex++] = this._prefixIndex;
                    break;
                }
                case 3: {
                    String prefix = this.decodeIdentifyingNonEmptyStringOnFirstBitAsPrefix(true);
                    a = this.createAttribute("http://www.w3.org/2000/xmlns/", this.createQualifiedNameString(prefix), prefix);
                    a.setValue(this.decodeIdentifyingNonEmptyStringOnFirstBitAsNamespaceName(true));
                    this._namespacePrefixes[this._namespacePrefixesIndex++] = this._prefixIndex;
                }
            }
            this._prefixTable.pushScope(this._prefixIndex, this._namespaceNameIndex);
            this._namespaceAttributes[this._namespaceAttributesIndex++] = a;
            b = this.read();
        }
        if (b != 240) {
            throw new IOException(CommonResourceBundle.getInstance().getString("message.EIInamespaceNameNotTerminatedCorrectly"));
        }
        int end = this._namespacePrefixesIndex;
        this._b = this.read();
        switch (DecoderStateTables.EII(this._b)) {
            case 0: {
                this.processEII(this._elementNameTable._array[this._b], hasAttributes);
                break;
            }
            case 2: {
                this.processEII(this.decodeEIIIndexMedium(), hasAttributes);
                break;
            }
            case 3: {
                this.processEII(this.decodeEIIIndexLarge(), hasAttributes);
                break;
            }
            case 5: {
                QualifiedName qn = this.processLiteralQualifiedName(this._b & 3, this._elementNameTable.getNext());
                this._elementNameTable.add(qn);
                this.processEII(qn, hasAttributes);
                break;
            }
            default: {
                throw new IOException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingEIIAfterAIIs"));
            }
        }
        for (int i = start; i < end; ++i) {
            this._prefixTable.popScope(this._namespacePrefixes[i]);
        }
        this._namespacePrefixesIndex = start;
    }

    protected final QualifiedName processLiteralQualifiedName(int state, QualifiedName q) throws FastInfosetException, IOException {
        if (q == null) {
            q = new QualifiedName();
        }
        switch (state) {
            case 0: {
                return q.set(null, null, this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), -1, -1, this._identifier, null);
            }
            case 1: {
                return q.set(null, this.decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(false), this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), -1, this._namespaceNameIndex, this._identifier, null);
            }
            case 2: {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.qNameMissingNamespaceName"));
            }
            case 3: {
                return q.set(this.decodeIdentifyingNonEmptyStringIndexOnFirstBitAsPrefix(true), this.decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(true), this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), this._prefixIndex, this._namespaceNameIndex, this._identifier, this._charBuffer);
            }
        }
        throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingEII"));
    }

    protected final QualifiedName processLiteralQualifiedName(int state) throws FastInfosetException, IOException {
        switch (state) {
            case 0: {
                return new QualifiedName(null, null, this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), -1, -1, this._identifier, null);
            }
            case 1: {
                return new QualifiedName(null, this.decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(false), this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), -1, this._namespaceNameIndex, this._identifier, null);
            }
            case 2: {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.qNameMissingNamespaceName"));
            }
            case 3: {
                return new QualifiedName(this.decodeIdentifyingNonEmptyStringIndexOnFirstBitAsPrefix(true), this.decodeIdentifyingNonEmptyStringIndexOnFirstBitAsNamespaceName(true), this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.localName), this._prefixIndex, this._namespaceNameIndex, this._identifier, this._charBuffer);
            }
        }
        throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.decodingEII"));
    }

    protected final void processAIIs() throws FastInfosetException, IOException {
        if (++this._duplicateAttributeVerifier._currentIteration == Integer.MAX_VALUE) {
            this._duplicateAttributeVerifier.clear();
        }
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
                    this._doubleTerminate = true;
                }
                case 4: {
                    this._terminate = true;
                    continue block22;
                }
                default: {
                    throw new IOException(CommonResourceBundle.getInstance().getString("message.decodingAIIs"));
                }
            }
            if (name.prefixIndex > 0 && this._prefixTable._currentInScope[name.prefixIndex] != name.namespaceNameIndex) {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.AIIqNameNotInScope"));
            }
            this._duplicateAttributeVerifier.checkForDuplicateAttribute(name.attributeHash, name.attributeId);
            Attr a = this.createAttribute(name.namespaceName, name.qName, name.localName);
            b = this.read();
            switch (DecoderStateTables.NISTRING(b)) {
                case 0: {
                    boolean addToTable = (b & 0x40) > 0;
                    this._octetBufferLength = (b & 7) + 1;
                    String value = this.decodeUtf8StringAsString();
                    if (addToTable) {
                        this._attributeValueTable.add(value);
                    }
                    a.setValue(value);
                    this._currentElement.setAttributeNode(a);
                    break;
                }
                case 1: {
                    boolean addToTable = (b & 0x40) > 0;
                    this._octetBufferLength = this.read() + 9;
                    String value = this.decodeUtf8StringAsString();
                    if (addToTable) {
                        this._attributeValueTable.add(value);
                    }
                    a.setValue(value);
                    this._currentElement.setAttributeNode(a);
                    break;
                }
                case 2: {
                    boolean addToTable = (b & 0x40) > 0;
                    int length = this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read();
                    this._octetBufferLength = length + 265;
                    String value = this.decodeUtf8StringAsString();
                    if (addToTable) {
                        this._attributeValueTable.add(value);
                    }
                    a.setValue(value);
                    this._currentElement.setAttributeNode(a);
                    break;
                }
                case 3: {
                    boolean addToTable = (b & 0x40) > 0;
                    this._octetBufferLength = (b & 7) + 1;
                    String value = this.decodeUtf16StringAsString();
                    if (addToTable) {
                        this._attributeValueTable.add(value);
                    }
                    a.setValue(value);
                    this._currentElement.setAttributeNode(a);
                    break;
                }
                case 4: {
                    boolean addToTable = (b & 0x40) > 0;
                    this._octetBufferLength = this.read() + 9;
                    String value = this.decodeUtf16StringAsString();
                    if (addToTable) {
                        this._attributeValueTable.add(value);
                    }
                    a.setValue(value);
                    this._currentElement.setAttributeNode(a);
                    break;
                }
                case 5: {
                    boolean addToTable = (b & 0x40) > 0;
                    int length = this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read();
                    this._octetBufferLength = length + 265;
                    String value = this.decodeUtf16StringAsString();
                    if (addToTable) {
                        this._attributeValueTable.add(value);
                    }
                    a.setValue(value);
                    this._currentElement.setAttributeNode(a);
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
                    a.setValue(value);
                    this._currentElement.setAttributeNode(a);
                    break;
                }
                case 7: {
                    boolean addToTable = (b & 0x40) > 0;
                    this._identifier = (b & 0xF) << 4;
                    b = this.read();
                    this._identifier |= (b & 0xF0) >> 4;
                    this.decodeOctetsOnFifthBitOfNonIdentifyingStringOnFirstBit(b);
                    String value = this.convertEncodingAlgorithmDataToCharacters(true);
                    if (addToTable) {
                        this._attributeValueTable.add(value);
                    }
                    a.setValue(value);
                    this._currentElement.setAttributeNode(a);
                    break;
                }
                case 8: {
                    String value = this._attributeValueTable._array[b & 0x3F];
                    a.setValue(value);
                    this._currentElement.setAttributeNode(a);
                    break;
                }
                case 9: {
                    int index = ((b & 0x1F) << 8 | this.read()) + 64;
                    String value = this._attributeValueTable._array[index];
                    a.setValue(value);
                    this._currentElement.setAttributeNode(a);
                    break;
                }
                case 10: {
                    int index = ((b & 0xF) << 16 | this.read() << 8 | this.read()) + 8256;
                    String value = this._attributeValueTable._array[index];
                    a.setValue(value);
                    this._currentElement.setAttributeNode(a);
                    break;
                }
                case 11: {
                    a.setValue("");
                    this._currentElement.setAttributeNode(a);
                    break;
                }
                default: {
                    throw new IOException(CommonResourceBundle.getInstance().getString("message.decodingAIIValue"));
                }
            }
        } while (!this._terminate);
        this._duplicateAttributeVerifier._poolCurrent = this._duplicateAttributeVerifier._poolHead;
        this._terminate = this._doubleTerminate;
        this._doubleTerminate = false;
    }

    protected final void processCommentII() throws FastInfosetException, IOException {
        switch (this.decodeNonIdentifyingStringOnFirstBit()) {
            case 0: {
                String s = new String(this._charBuffer, 0, this._charBufferLength);
                if (this._addToTable) {
                    this._v.otherString.add(new CharArrayString(s, false));
                }
                this._currentNode.appendChild(this._document.createComment(s));
                break;
            }
            case 2: {
                throw new IOException(CommonResourceBundle.getInstance().getString("message.commentIIAlgorithmNotSupported"));
            }
            case 1: {
                String s = this._v.otherString.get(this._integer).toString();
                this._currentNode.appendChild(this._document.createComment(s));
                break;
            }
            case 3: {
                this._currentNode.appendChild(this._document.createComment(""));
            }
        }
    }

    protected final void processProcessingII() throws FastInfosetException, IOException {
        String target = this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherNCName);
        switch (this.decodeNonIdentifyingStringOnFirstBit()) {
            case 0: {
                String data = new String(this._charBuffer, 0, this._charBufferLength);
                if (this._addToTable) {
                    this._v.otherString.add(new CharArrayString(data, false));
                }
                this._currentNode.appendChild(this._document.createProcessingInstruction(target, data));
                break;
            }
            case 2: {
                throw new IOException(CommonResourceBundle.getInstance().getString("message.processingIIWithEncodingAlgorithm"));
            }
            case 1: {
                String data = this._v.otherString.get(this._integer).toString();
                this._currentNode.appendChild(this._document.createProcessingInstruction(target, data));
                break;
            }
            case 3: {
                this._currentNode.appendChild(this._document.createProcessingInstruction(target, ""));
            }
        }
    }

    protected Element createElement(String namespaceName, String qName, String localName) {
        return this._document.createElementNS(namespaceName, qName);
    }

    protected Attr createAttribute(String namespaceName, String qName, String localName) {
        return this._document.createAttributeNS(namespaceName, qName);
    }

    protected String convertEncodingAlgorithmDataToCharacters(boolean isAttributeValue) throws FastInfosetException, IOException {
        StringBuffer buffer = new StringBuffer();
        if (this._identifier < 9) {
            Object array = BuiltInEncodingAlgorithmFactory.getAlgorithm(this._identifier).decodeFromBytes(this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
            BuiltInEncodingAlgorithmFactory.getAlgorithm(this._identifier).convertToCharacters(array, buffer);
        } else {
            if (this._identifier == 9) {
                if (!isAttributeValue) {
                    this._octetBufferOffset -= this._octetBufferLength;
                    return this.decodeUtf8StringAsString();
                }
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.CDATAAlgorithmNotSupported"));
            }
            if (this._identifier >= 32) {
                String URI2 = this._v.encodingAlgorithm.get(this._identifier - 32);
                EncodingAlgorithm ea = (EncodingAlgorithm)this._registeredEncodingAlgorithms.get(URI2);
                if (ea != null) {
                    Object data = ea.decodeFromBytes(this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
                    ea.convertToCharacters(data, buffer);
                } else {
                    throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.algorithmDataCannotBeReported"));
                }
            }
        }
        return buffer.toString();
    }
}

