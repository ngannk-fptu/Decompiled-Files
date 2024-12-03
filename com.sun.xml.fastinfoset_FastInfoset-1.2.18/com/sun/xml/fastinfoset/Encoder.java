/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset;

import com.sun.xml.fastinfoset.CommonResourceBundle;
import com.sun.xml.fastinfoset.EncodingConstants;
import com.sun.xml.fastinfoset.QualifiedName;
import com.sun.xml.fastinfoset.algorithm.BuiltInEncodingAlgorithmFactory;
import com.sun.xml.fastinfoset.org.apache.xerces.util.XMLChar;
import com.sun.xml.fastinfoset.util.CharArrayIntMap;
import com.sun.xml.fastinfoset.util.LocalNameQualifiedNamesMap;
import com.sun.xml.fastinfoset.util.StringIntMap;
import com.sun.xml.fastinfoset.vocab.SerializerVocabulary;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import org.jvnet.fastinfoset.EncodingAlgorithm;
import org.jvnet.fastinfoset.EncodingAlgorithmException;
import org.jvnet.fastinfoset.ExternalVocabulary;
import org.jvnet.fastinfoset.FastInfosetException;
import org.jvnet.fastinfoset.FastInfosetSerializer;
import org.jvnet.fastinfoset.VocabularyApplicationData;
import org.xml.sax.helpers.DefaultHandler;

public abstract class Encoder
extends DefaultHandler
implements FastInfosetSerializer {
    public static final String CHARACTER_ENCODING_SCHEME_SYSTEM_PROPERTY = "com.sun.xml.fastinfoset.serializer.character-encoding-scheme";
    protected static final String _characterEncodingSchemeSystemDefault;
    private static int[] NUMERIC_CHARACTERS_TABLE;
    private static int[] DATE_TIME_CHARACTERS_TABLE;
    private boolean _ignoreDTD;
    private boolean _ignoreComments;
    private boolean _ignoreProcessingInstructions;
    private boolean _ignoreWhiteSpaceTextContent;
    private boolean _useLocalNameAsKeyForQualifiedNameLookup;
    private boolean _encodingStringsAsUtf8 = true;
    private int _nonIdentifyingStringOnThirdBitCES;
    private int _nonIdentifyingStringOnFirstBitCES;
    private Map _registeredEncodingAlgorithms = new HashMap();
    protected SerializerVocabulary _v;
    protected VocabularyApplicationData _vData;
    private boolean _vIsInternal;
    protected boolean _terminate = false;
    protected int _b;
    protected OutputStream _s;
    protected char[] _charBuffer = new char[512];
    protected byte[] _octetBuffer = new byte[1024];
    protected int _octetBufferIndex;
    protected int _markIndex = -1;
    protected int minAttributeValueSize = 0;
    protected int maxAttributeValueSize = 32;
    protected int attributeValueMapTotalCharactersConstraint = 0x3FFFFFFF;
    protected int minCharacterContentChunkSize = 0;
    protected int maxCharacterContentChunkSize = 32;
    protected int characterContentChunkMapTotalCharactersConstraint = 0x3FFFFFFF;
    private int _bitsLeftInOctet;
    private EncodingBufferOutputStream _encodingBufferOutputStream = new EncodingBufferOutputStream();
    private byte[] _encodingBuffer = new byte[512];
    private int _encodingBufferIndex;

    private static String getDefaultEncodingScheme() {
        String p = System.getProperty(CHARACTER_ENCODING_SCHEME_SYSTEM_PROPERTY, "UTF-8");
        if (p.equals("UTF-16BE")) {
            return "UTF-16BE";
        }
        return "UTF-8";
    }

    private static int maxCharacter(String alphabet) {
        char c = '\u0000';
        for (int i = 0; i < alphabet.length(); ++i) {
            if (c >= alphabet.charAt(i)) continue;
            c = alphabet.charAt(i);
        }
        return c;
    }

    protected Encoder() {
        this.setCharacterEncodingScheme(_characterEncodingSchemeSystemDefault);
    }

    protected Encoder(boolean useLocalNameAsKeyForQualifiedNameLookup) {
        this.setCharacterEncodingScheme(_characterEncodingSchemeSystemDefault);
        this._useLocalNameAsKeyForQualifiedNameLookup = useLocalNameAsKeyForQualifiedNameLookup;
    }

    @Override
    public final void setIgnoreDTD(boolean ignoreDTD) {
        this._ignoreDTD = ignoreDTD;
    }

    @Override
    public final boolean getIgnoreDTD() {
        return this._ignoreDTD;
    }

    @Override
    public final void setIgnoreComments(boolean ignoreComments) {
        this._ignoreComments = ignoreComments;
    }

    @Override
    public final boolean getIgnoreComments() {
        return this._ignoreComments;
    }

    @Override
    public final void setIgnoreProcesingInstructions(boolean ignoreProcesingInstructions) {
        this._ignoreProcessingInstructions = ignoreProcesingInstructions;
    }

    @Override
    public final boolean getIgnoreProcesingInstructions() {
        return this._ignoreProcessingInstructions;
    }

    @Override
    public final void setIgnoreWhiteSpaceTextContent(boolean ignoreWhiteSpaceTextContent) {
        this._ignoreWhiteSpaceTextContent = ignoreWhiteSpaceTextContent;
    }

    @Override
    public final boolean getIgnoreWhiteSpaceTextContent() {
        return this._ignoreWhiteSpaceTextContent;
    }

    @Override
    public void setCharacterEncodingScheme(String characterEncodingScheme) {
        if (characterEncodingScheme.equals("UTF-16BE")) {
            this._encodingStringsAsUtf8 = false;
            this._nonIdentifyingStringOnThirdBitCES = 132;
            this._nonIdentifyingStringOnFirstBitCES = 16;
        } else {
            this._encodingStringsAsUtf8 = true;
            this._nonIdentifyingStringOnThirdBitCES = 128;
            this._nonIdentifyingStringOnFirstBitCES = 0;
        }
    }

    @Override
    public String getCharacterEncodingScheme() {
        return this._encodingStringsAsUtf8 ? "UTF-8" : "UTF-16BE";
    }

    @Override
    public void setRegisteredEncodingAlgorithms(Map algorithms) {
        this._registeredEncodingAlgorithms = algorithms;
        if (this._registeredEncodingAlgorithms == null) {
            this._registeredEncodingAlgorithms = new HashMap();
        }
    }

    @Override
    public Map getRegisteredEncodingAlgorithms() {
        return this._registeredEncodingAlgorithms;
    }

    @Override
    public int getMinCharacterContentChunkSize() {
        return this.minCharacterContentChunkSize;
    }

    @Override
    public void setMinCharacterContentChunkSize(int size) {
        if (size < 0) {
            size = 0;
        }
        this.minCharacterContentChunkSize = size;
    }

    @Override
    public int getMaxCharacterContentChunkSize() {
        return this.maxCharacterContentChunkSize;
    }

    @Override
    public void setMaxCharacterContentChunkSize(int size) {
        if (size < 0) {
            size = 0;
        }
        this.maxCharacterContentChunkSize = size;
    }

    @Override
    public int getCharacterContentChunkMapMemoryLimit() {
        return this.characterContentChunkMapTotalCharactersConstraint * 2;
    }

    @Override
    public void setCharacterContentChunkMapMemoryLimit(int size) {
        if (size < 0) {
            size = 0;
        }
        this.characterContentChunkMapTotalCharactersConstraint = size / 2;
    }

    public boolean isCharacterContentChunkLengthMatchesLimit(int length) {
        return length >= this.minCharacterContentChunkSize && length < this.maxCharacterContentChunkSize;
    }

    public boolean canAddCharacterContentToTable(int length, CharArrayIntMap map) {
        return map.getTotalCharacterCount() + length < this.characterContentChunkMapTotalCharactersConstraint;
    }

    @Override
    public int getMinAttributeValueSize() {
        return this.minAttributeValueSize;
    }

    @Override
    public void setMinAttributeValueSize(int size) {
        if (size < 0) {
            size = 0;
        }
        this.minAttributeValueSize = size;
    }

    @Override
    public int getMaxAttributeValueSize() {
        return this.maxAttributeValueSize;
    }

    @Override
    public void setMaxAttributeValueSize(int size) {
        if (size < 0) {
            size = 0;
        }
        this.maxAttributeValueSize = size;
    }

    @Override
    public void setAttributeValueMapMemoryLimit(int size) {
        if (size < 0) {
            size = 0;
        }
        this.attributeValueMapTotalCharactersConstraint = size / 2;
    }

    @Override
    public int getAttributeValueMapMemoryLimit() {
        return this.attributeValueMapTotalCharactersConstraint * 2;
    }

    public boolean isAttributeValueLengthMatchesLimit(int length) {
        return length >= this.minAttributeValueSize && length < this.maxAttributeValueSize;
    }

    public boolean canAddAttributeToTable(int length) {
        return this._v.attributeValue.getTotalCharacterCount() + length < this.attributeValueMapTotalCharactersConstraint;
    }

    @Override
    public void setExternalVocabulary(ExternalVocabulary v) {
        this._v = new SerializerVocabulary();
        SerializerVocabulary ev = new SerializerVocabulary(v.vocabulary, this._useLocalNameAsKeyForQualifiedNameLookup);
        this._v.setExternalVocabulary(v.URI, ev, false);
        this._vIsInternal = true;
    }

    @Override
    public void setVocabularyApplicationData(VocabularyApplicationData data) {
        this._vData = data;
    }

    @Override
    public VocabularyApplicationData getVocabularyApplicationData() {
        return this._vData;
    }

    @Override
    public void reset() {
        this._terminate = false;
    }

    @Override
    public void setOutputStream(OutputStream s) {
        this._octetBufferIndex = 0;
        this._markIndex = -1;
        this._s = s;
    }

    public void setVocabulary(SerializerVocabulary vocabulary) {
        this._v = vocabulary;
        this._vIsInternal = false;
    }

    protected final void encodeHeader(boolean encodeXmlDecl) throws IOException {
        if (encodeXmlDecl) {
            this._s.write(EncodingConstants.XML_DECLARATION_VALUES[0]);
        }
        this._s.write(EncodingConstants.BINARY_HEADER);
    }

    protected final void encodeInitialVocabulary() throws IOException {
        if (this._v == null) {
            this._v = new SerializerVocabulary();
            this._vIsInternal = true;
        } else if (this._vIsInternal) {
            this._v.clear();
            if (this._vData != null) {
                this._vData.clear();
            }
        }
        if (!this._v.hasInitialVocabulary() && !this._v.hasExternalVocabulary()) {
            this.write(0);
        } else if (this._v.hasInitialVocabulary()) {
            this._b = 32;
            this.write(this._b);
            SerializerVocabulary initialVocabulary = this._v.getReadOnlyVocabulary();
            if (initialVocabulary.hasExternalVocabulary()) {
                this._b = 16;
                this.write(this._b);
                this.write(0);
            }
            if (initialVocabulary.hasExternalVocabulary()) {
                this.encodeNonEmptyOctetStringOnSecondBit(this._v.getExternalVocabularyURI());
            }
        } else if (this._v.hasExternalVocabulary()) {
            this._b = 32;
            this.write(this._b);
            this._b = 16;
            this.write(this._b);
            this.write(0);
            this.encodeNonEmptyOctetStringOnSecondBit(this._v.getExternalVocabularyURI());
        }
    }

    protected final void encodeDocumentTermination() throws IOException {
        this.encodeElementTermination();
        this.encodeTermination();
        this._flush();
        this._s.flush();
    }

    protected final void encodeElementTermination() throws IOException {
        this._terminate = true;
        switch (this._b) {
            case 240: {
                this._b = 255;
                break;
            }
            case 255: {
                this.write(255);
            }
            default: {
                this._b = 240;
            }
        }
    }

    protected final void encodeTermination() throws IOException {
        if (this._terminate) {
            this.write(this._b);
            this._b = 0;
            this._terminate = false;
        }
    }

    protected final void encodeNamespaceAttribute(String prefix, String uri) throws IOException {
        this._b = 204;
        if (prefix.length() > 0) {
            this._b |= 2;
        }
        if (uri.length() > 0) {
            this._b |= 1;
        }
        this.write(this._b);
        if (prefix.length() > 0) {
            this.encodeIdentifyingNonEmptyStringOnFirstBit(prefix, this._v.prefix);
        }
        if (uri.length() > 0) {
            this.encodeIdentifyingNonEmptyStringOnFirstBit(uri, this._v.namespaceName);
        }
    }

    protected final void encodeCharacters(char[] ch, int offset, int length) throws IOException {
        boolean addToTable = this.isCharacterContentChunkLengthMatchesLimit(length);
        this.encodeNonIdentifyingStringOnThirdBit(ch, offset, length, this._v.characterContentChunk, addToTable, true);
    }

    protected final void encodeCharactersNoClone(char[] ch, int offset, int length) throws IOException {
        boolean addToTable = this.isCharacterContentChunkLengthMatchesLimit(length);
        this.encodeNonIdentifyingStringOnThirdBit(ch, offset, length, this._v.characterContentChunk, addToTable, false);
    }

    protected final void encodeNumericFourBitCharacters(char[] ch, int offset, int length, boolean addToTable) throws FastInfosetException, IOException {
        this.encodeFourBitCharacters(0, NUMERIC_CHARACTERS_TABLE, ch, offset, length, addToTable);
    }

    protected final void encodeDateTimeFourBitCharacters(char[] ch, int offset, int length, boolean addToTable) throws FastInfosetException, IOException {
        this.encodeFourBitCharacters(1, DATE_TIME_CHARACTERS_TABLE, ch, offset, length, addToTable);
    }

    protected final void encodeFourBitCharacters(int id, int[] table, char[] ch, int offset, int length, boolean addToTable) throws FastInfosetException, IOException {
        if (addToTable) {
            int index;
            boolean canAddCharacterContentToTable = this.canAddCharacterContentToTable(length, this._v.characterContentChunk);
            int n = index = canAddCharacterContentToTable ? this._v.characterContentChunk.obtainIndex(ch, offset, length, true) : this._v.characterContentChunk.get(ch, offset, length);
            if (index != -1) {
                this._b = 160;
                this.encodeNonZeroIntegerOnFourthBit(index);
                return;
            }
            this._b = canAddCharacterContentToTable ? 152 : 136;
        } else {
            this._b = 136;
        }
        this.write(this._b);
        this._b = id << 2;
        this.encodeNonEmptyFourBitCharacterStringOnSeventhBit(table, ch, offset, length);
    }

    protected final void encodeAlphabetCharacters(String alphabet, char[] ch, int offset, int length, boolean addToTable) throws FastInfosetException, IOException {
        if (addToTable) {
            int index;
            boolean canAddCharacterContentToTable = this.canAddCharacterContentToTable(length, this._v.characterContentChunk);
            int n = index = canAddCharacterContentToTable ? this._v.characterContentChunk.obtainIndex(ch, offset, length, true) : this._v.characterContentChunk.get(ch, offset, length);
            if (index != -1) {
                this._b = 160;
                this.encodeNonZeroIntegerOnFourthBit(index);
                return;
            }
            this._b = canAddCharacterContentToTable ? 152 : 136;
        } else {
            this._b = 136;
        }
        int id = this._v.restrictedAlphabet.get(alphabet);
        if (id == -1) {
            throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.restrictedAlphabetNotPresent"));
        }
        this._b |= ((id += 32) & 0xC0) >> 6;
        this.write(this._b);
        this._b = (id & 0x3F) << 2;
        this.encodeNonEmptyNBitCharacterStringOnSeventhBit(alphabet, ch, offset, length);
    }

    protected final void encodeProcessingInstruction(String target, String data) throws IOException {
        this.write(225);
        this.encodeIdentifyingNonEmptyStringOnFirstBit(target, this._v.otherNCName);
        boolean addToTable = this.isCharacterContentChunkLengthMatchesLimit(data.length());
        this.encodeNonIdentifyingStringOnFirstBit(data, this._v.otherString, addToTable);
    }

    protected final void encodeDocumentTypeDeclaration(String systemId, String publicId) throws IOException {
        this._b = 196;
        if (systemId != null && systemId.length() > 0) {
            this._b |= 2;
        }
        if (publicId != null && publicId.length() > 0) {
            this._b |= 1;
        }
        this.write(this._b);
        if (systemId != null && systemId.length() > 0) {
            this.encodeIdentifyingNonEmptyStringOnFirstBit(systemId, this._v.otherURI);
        }
        if (publicId != null && publicId.length() > 0) {
            this.encodeIdentifyingNonEmptyStringOnFirstBit(publicId, this._v.otherURI);
        }
    }

    protected final void encodeComment(char[] ch, int offset, int length) throws IOException {
        this.write(226);
        boolean addToTable = this.isCharacterContentChunkLengthMatchesLimit(length);
        this.encodeNonIdentifyingStringOnFirstBit(ch, offset, length, this._v.otherString, addToTable, true);
    }

    protected final void encodeCommentNoClone(char[] ch, int offset, int length) throws IOException {
        this.write(226);
        boolean addToTable = this.isCharacterContentChunkLengthMatchesLimit(length);
        this.encodeNonIdentifyingStringOnFirstBit(ch, offset, length, this._v.otherString, addToTable, false);
    }

    protected final void encodeElementQualifiedNameOnThirdBit(String namespaceURI, String prefix, String localName) throws IOException {
        LocalNameQualifiedNamesMap.Entry entry = this._v.elementName.obtainEntry(localName);
        if (entry._valueIndex > 0) {
            QualifiedName[] names = entry._value;
            for (int i = 0; i < entry._valueIndex; ++i) {
                if (prefix != names[i].prefix && !prefix.equals(names[i].prefix) || namespaceURI != names[i].namespaceName && !namespaceURI.equals(names[i].namespaceName)) continue;
                this.encodeNonZeroIntegerOnThirdBit(names[i].index);
                return;
            }
        }
        this.encodeLiteralElementQualifiedNameOnThirdBit(namespaceURI, prefix, localName, entry);
    }

    protected final void encodeLiteralElementQualifiedNameOnThirdBit(String namespaceURI, String prefix, String localName, LocalNameQualifiedNamesMap.Entry entry) throws IOException {
        QualifiedName name = new QualifiedName(prefix, namespaceURI, localName, "", this._v.elementName.getNextIndex());
        entry.addQualifiedName(name);
        int namespaceURIIndex = -1;
        int prefixIndex = -1;
        if (namespaceURI.length() > 0) {
            namespaceURIIndex = this._v.namespaceName.get(namespaceURI);
            if (namespaceURIIndex == -1) {
                throw new IOException(CommonResourceBundle.getInstance().getString("message.namespaceURINotIndexed", new Object[]{namespaceURI}));
            }
            if (prefix.length() > 0 && (prefixIndex = this._v.prefix.get(prefix)) == -1) {
                throw new IOException(CommonResourceBundle.getInstance().getString("message.prefixNotIndexed", new Object[]{prefix}));
            }
        }
        int localNameIndex = this._v.localName.obtainIndex(localName);
        this._b |= 0x3C;
        if (namespaceURIIndex >= 0) {
            this._b |= 1;
            if (prefixIndex >= 0) {
                this._b |= 2;
            }
        }
        this.write(this._b);
        if (namespaceURIIndex >= 0) {
            if (prefixIndex >= 0) {
                this.encodeNonZeroIntegerOnSecondBitFirstBitOne(prefixIndex);
            }
            this.encodeNonZeroIntegerOnSecondBitFirstBitOne(namespaceURIIndex);
        }
        if (localNameIndex >= 0) {
            this.encodeNonZeroIntegerOnSecondBitFirstBitOne(localNameIndex);
        } else {
            this.encodeNonEmptyOctetStringOnSecondBit(localName);
        }
    }

    protected final void encodeAttributeQualifiedNameOnSecondBit(String namespaceURI, String prefix, String localName) throws IOException {
        LocalNameQualifiedNamesMap.Entry entry = this._v.attributeName.obtainEntry(localName);
        if (entry._valueIndex > 0) {
            QualifiedName[] names = entry._value;
            for (int i = 0; i < entry._valueIndex; ++i) {
                if (prefix != names[i].prefix && !prefix.equals(names[i].prefix) || namespaceURI != names[i].namespaceName && !namespaceURI.equals(names[i].namespaceName)) continue;
                this.encodeNonZeroIntegerOnSecondBitFirstBitZero(names[i].index);
                return;
            }
        }
        this.encodeLiteralAttributeQualifiedNameOnSecondBit(namespaceURI, prefix, localName, entry);
    }

    protected final boolean encodeLiteralAttributeQualifiedNameOnSecondBit(String namespaceURI, String prefix, String localName, LocalNameQualifiedNamesMap.Entry entry) throws IOException {
        int namespaceURIIndex = -1;
        int prefixIndex = -1;
        if (namespaceURI.length() > 0) {
            namespaceURIIndex = this._v.namespaceName.get(namespaceURI);
            if (namespaceURIIndex == -1) {
                if (namespaceURI == "http://www.w3.org/2000/xmlns/" || namespaceURI.equals("http://www.w3.org/2000/xmlns/")) {
                    return false;
                }
                throw new IOException(CommonResourceBundle.getInstance().getString("message.namespaceURINotIndexed", new Object[]{namespaceURI}));
            }
            if (prefix.length() > 0 && (prefixIndex = this._v.prefix.get(prefix)) == -1) {
                throw new IOException(CommonResourceBundle.getInstance().getString("message.prefixNotIndexed", new Object[]{prefix}));
            }
        }
        int localNameIndex = this._v.localName.obtainIndex(localName);
        QualifiedName name = new QualifiedName(prefix, namespaceURI, localName, "", this._v.attributeName.getNextIndex());
        entry.addQualifiedName(name);
        this._b = 120;
        if (namespaceURI.length() > 0) {
            this._b |= 1;
            if (prefix.length() > 0) {
                this._b |= 2;
            }
        }
        this.write(this._b);
        if (namespaceURIIndex >= 0) {
            if (prefixIndex >= 0) {
                this.encodeNonZeroIntegerOnSecondBitFirstBitOne(prefixIndex);
            }
            this.encodeNonZeroIntegerOnSecondBitFirstBitOne(namespaceURIIndex);
        } else if (namespaceURI != "") {
            this.encodeNonEmptyOctetStringOnSecondBit("xml");
            this.encodeNonEmptyOctetStringOnSecondBit("http://www.w3.org/XML/1998/namespace");
        }
        if (localNameIndex >= 0) {
            this.encodeNonZeroIntegerOnSecondBitFirstBitOne(localNameIndex);
        } else {
            this.encodeNonEmptyOctetStringOnSecondBit(localName);
        }
        return true;
    }

    protected final void encodeNonIdentifyingStringOnFirstBit(String s, StringIntMap map, boolean addToTable, boolean mustBeAddedToTable) throws IOException {
        if (s == null || s.length() == 0) {
            this.write(255);
        } else if (addToTable || mustBeAddedToTable) {
            int index;
            boolean canAddAttributeToTable = mustBeAddedToTable || this.canAddAttributeToTable(s.length());
            int n = index = canAddAttributeToTable ? map.obtainIndex(s) : map.get(s);
            if (index != -1) {
                this.encodeNonZeroIntegerOnSecondBitFirstBitOne(index);
            } else if (canAddAttributeToTable) {
                this._b = 0x40 | this._nonIdentifyingStringOnFirstBitCES;
                this.encodeNonEmptyCharacterStringOnFifthBit(s);
            } else {
                this._b = this._nonIdentifyingStringOnFirstBitCES;
                this.encodeNonEmptyCharacterStringOnFifthBit(s);
            }
        } else {
            this._b = this._nonIdentifyingStringOnFirstBitCES;
            this.encodeNonEmptyCharacterStringOnFifthBit(s);
        }
    }

    protected final void encodeNonIdentifyingStringOnFirstBit(String s, CharArrayIntMap map, boolean addToTable) throws IOException {
        if (s == null || s.length() == 0) {
            this.write(255);
        } else if (addToTable) {
            int index;
            char[] ch = s.toCharArray();
            int length = s.length();
            boolean canAddCharacterContentToTable = this.canAddCharacterContentToTable(length, map);
            int n = index = canAddCharacterContentToTable ? map.obtainIndex(ch, 0, length, false) : map.get(ch, 0, length);
            if (index != -1) {
                this.encodeNonZeroIntegerOnSecondBitFirstBitOne(index);
            } else if (canAddCharacterContentToTable) {
                this._b = 0x40 | this._nonIdentifyingStringOnFirstBitCES;
                this.encodeNonEmptyCharacterStringOnFifthBit(ch, 0, length);
            } else {
                this._b = this._nonIdentifyingStringOnFirstBitCES;
                this.encodeNonEmptyCharacterStringOnFifthBit(s);
            }
        } else {
            this._b = this._nonIdentifyingStringOnFirstBitCES;
            this.encodeNonEmptyCharacterStringOnFifthBit(s);
        }
    }

    protected final void encodeNonIdentifyingStringOnFirstBit(char[] ch, int offset, int length, CharArrayIntMap map, boolean addToTable, boolean clone) throws IOException {
        if (length == 0) {
            this.write(255);
        } else if (addToTable) {
            int index;
            boolean canAddCharacterContentToTable = this.canAddCharacterContentToTable(length, map);
            int n = index = canAddCharacterContentToTable ? map.obtainIndex(ch, offset, length, clone) : map.get(ch, offset, length);
            if (index != -1) {
                this.encodeNonZeroIntegerOnSecondBitFirstBitOne(index);
            } else if (canAddCharacterContentToTable) {
                this._b = 0x40 | this._nonIdentifyingStringOnFirstBitCES;
                this.encodeNonEmptyCharacterStringOnFifthBit(ch, offset, length);
            } else {
                this._b = this._nonIdentifyingStringOnFirstBitCES;
                this.encodeNonEmptyCharacterStringOnFifthBit(ch, offset, length);
            }
        } else {
            this._b = this._nonIdentifyingStringOnFirstBitCES;
            this.encodeNonEmptyCharacterStringOnFifthBit(ch, offset, length);
        }
    }

    protected final void encodeNumericNonIdentifyingStringOnFirstBit(String s, boolean addToTable, boolean mustBeAddedToTable) throws IOException, FastInfosetException {
        this.encodeNonIdentifyingStringOnFirstBit(0, NUMERIC_CHARACTERS_TABLE, s, addToTable, mustBeAddedToTable);
    }

    protected final void encodeDateTimeNonIdentifyingStringOnFirstBit(String s, boolean addToTable, boolean mustBeAddedToTable) throws IOException, FastInfosetException {
        this.encodeNonIdentifyingStringOnFirstBit(1, DATE_TIME_CHARACTERS_TABLE, s, addToTable, mustBeAddedToTable);
    }

    protected final void encodeNonIdentifyingStringOnFirstBit(int id, int[] table, String s, boolean addToTable, boolean mustBeAddedToTable) throws IOException, FastInfosetException {
        if (s == null || s.length() == 0) {
            this.write(255);
            return;
        }
        if (addToTable || mustBeAddedToTable) {
            int index;
            boolean canAddAttributeToTable = mustBeAddedToTable || this.canAddAttributeToTable(s.length());
            int n = index = canAddAttributeToTable ? this._v.attributeValue.obtainIndex(s) : this._v.attributeValue.get(s);
            if (index != -1) {
                this.encodeNonZeroIntegerOnSecondBitFirstBitOne(index);
                return;
            }
            this._b = canAddAttributeToTable ? 96 : 32;
        } else {
            this._b = 32;
        }
        this.write(this._b | (id & 0xF0) >> 4);
        this._b = (id & 0xF) << 4;
        int length = s.length();
        int octetPairLength = length / 2;
        int octetSingleLength = length % 2;
        this.encodeNonZeroOctetStringLengthOnFifthBit(octetPairLength + octetSingleLength);
        this.encodeNonEmptyFourBitCharacterString(table, s.toCharArray(), 0, octetPairLength, octetSingleLength);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected final void encodeNonIdentifyingStringOnFirstBit(String URI, int id, Object data) throws FastInfosetException, IOException {
        if (URI != null) {
            id = this._v.encodingAlgorithm.get(URI);
            if (id == -1) {
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.EncodingAlgorithmURI", new Object[]{URI}));
            }
            id += 32;
            EncodingAlgorithm ea = (EncodingAlgorithm)this._registeredEncodingAlgorithms.get(URI);
            if (ea != null) {
                this.encodeAIIObjectAlgorithmData(id, data, ea);
                return;
            } else {
                if (!(data instanceof byte[])) throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.nullEncodingAlgorithmURI"));
                byte[] d = (byte[])data;
                this.encodeAIIOctetAlgorithmData(id, d, 0, d.length);
            }
            return;
        } else if (id <= 9) {
            int length = 0;
            switch (id) {
                case 0: 
                case 1: {
                    length = ((byte[])data).length;
                    break;
                }
                case 2: {
                    length = ((short[])data).length;
                    break;
                }
                case 3: {
                    length = ((int[])data).length;
                    break;
                }
                case 4: 
                case 8: {
                    length = ((long[])data).length;
                    break;
                }
                case 5: {
                    length = ((boolean[])data).length;
                    break;
                }
                case 6: {
                    length = ((float[])data).length;
                    break;
                }
                case 7: {
                    length = ((double[])data).length;
                    break;
                }
                case 9: {
                    throw new UnsupportedOperationException(CommonResourceBundle.getInstance().getString("message.CDATA"));
                }
                default: {
                    throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.UnsupportedBuiltInAlgorithm", new Object[]{id}));
                }
            }
            this.encodeAIIBuiltInAlgorithmData(id, data, 0, length);
            return;
        } else {
            if (id < 32) throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.identifiers10to31Reserved"));
            if (!(data instanceof byte[])) throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.nullEncodingAlgorithmURI"));
            byte[] d = (byte[])data;
            this.encodeAIIOctetAlgorithmData(id, d, 0, d.length);
        }
    }

    protected final void encodeAIIOctetAlgorithmData(int id, byte[] d, int offset, int length) throws IOException {
        this.write(0x30 | (id & 0xF0) >> 4);
        this._b = (id & 0xF) << 4;
        this.encodeNonZeroOctetStringLengthOnFifthBit(length);
        this.write(d, offset, length);
    }

    protected final void encodeAIIObjectAlgorithmData(int id, Object data, EncodingAlgorithm ea) throws FastInfosetException, IOException {
        this.write(0x30 | (id & 0xF0) >> 4);
        this._b = (id & 0xF) << 4;
        this._encodingBufferOutputStream.reset();
        ea.encodeToOutputStream(data, this._encodingBufferOutputStream);
        this.encodeNonZeroOctetStringLengthOnFifthBit(this._encodingBufferIndex);
        this.write(this._encodingBuffer, this._encodingBufferIndex);
    }

    protected final void encodeAIIBuiltInAlgorithmData(int id, Object data, int offset, int length) throws IOException {
        this.write(0x30 | (id & 0xF0) >> 4);
        this._b = (id & 0xF) << 4;
        int octetLength = BuiltInEncodingAlgorithmFactory.getAlgorithm(id).getOctetLengthFromPrimitiveLength(length);
        this.encodeNonZeroOctetStringLengthOnFifthBit(octetLength);
        this.ensureSize(octetLength);
        BuiltInEncodingAlgorithmFactory.getAlgorithm(id).encodeToBytes(data, offset, length, this._octetBuffer, this._octetBufferIndex);
        this._octetBufferIndex += octetLength;
    }

    protected final void encodeNonIdentifyingStringOnThirdBit(char[] ch, int offset, int length, CharArrayIntMap map, boolean addToTable, boolean clone) throws IOException {
        if (addToTable) {
            int index;
            boolean canAddCharacterContentToTable = this.canAddCharacterContentToTable(length, map);
            int n = index = canAddCharacterContentToTable ? map.obtainIndex(ch, offset, length, clone) : map.get(ch, offset, length);
            if (index != -1) {
                this._b = 160;
                this.encodeNonZeroIntegerOnFourthBit(index);
            } else if (canAddCharacterContentToTable) {
                this._b = 0x10 | this._nonIdentifyingStringOnThirdBitCES;
                this.encodeNonEmptyCharacterStringOnSeventhBit(ch, offset, length);
            } else {
                this._b = this._nonIdentifyingStringOnThirdBitCES;
                this.encodeNonEmptyCharacterStringOnSeventhBit(ch, offset, length);
            }
        } else {
            this._b = this._nonIdentifyingStringOnThirdBitCES;
            this.encodeNonEmptyCharacterStringOnSeventhBit(ch, offset, length);
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected final void encodeNonIdentifyingStringOnThirdBit(String URI, int id, Object data) throws FastInfosetException, IOException {
        if (URI != null) {
            id = this._v.encodingAlgorithm.get(URI);
            if (id == -1) {
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.EncodingAlgorithmURI", new Object[]{URI}));
            }
            id += 32;
            EncodingAlgorithm ea = (EncodingAlgorithm)this._registeredEncodingAlgorithms.get(URI);
            if (ea != null) {
                this.encodeCIIObjectAlgorithmData(id, data, ea);
                return;
            } else {
                if (!(data instanceof byte[])) throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.nullEncodingAlgorithmURI"));
                byte[] d = (byte[])data;
                this.encodeCIIOctetAlgorithmData(id, d, 0, d.length);
            }
            return;
        } else if (id <= 9) {
            int length = 0;
            switch (id) {
                case 0: 
                case 1: {
                    length = ((byte[])data).length;
                    break;
                }
                case 2: {
                    length = ((short[])data).length;
                    break;
                }
                case 3: {
                    length = ((int[])data).length;
                    break;
                }
                case 4: 
                case 8: {
                    length = ((long[])data).length;
                    break;
                }
                case 5: {
                    length = ((boolean[])data).length;
                    break;
                }
                case 6: {
                    length = ((float[])data).length;
                    break;
                }
                case 7: {
                    length = ((double[])data).length;
                    break;
                }
                case 9: {
                    throw new UnsupportedOperationException(CommonResourceBundle.getInstance().getString("message.CDATA"));
                }
                default: {
                    throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.UnsupportedBuiltInAlgorithm", new Object[]{id}));
                }
            }
            this.encodeCIIBuiltInAlgorithmData(id, data, 0, length);
            return;
        } else {
            if (id < 32) throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.identifiers10to31Reserved"));
            if (!(data instanceof byte[])) throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.nullEncodingAlgorithmURI"));
            byte[] d = (byte[])data;
            this.encodeCIIOctetAlgorithmData(id, d, 0, d.length);
        }
    }

    protected final void encodeNonIdentifyingStringOnThirdBit(String URI, int id, byte[] d, int offset, int length) throws FastInfosetException, IOException {
        if (URI != null) {
            id = this._v.encodingAlgorithm.get(URI);
            if (id == -1) {
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.EncodingAlgorithmURI", new Object[]{URI}));
            }
            id += 32;
        }
        this.encodeCIIOctetAlgorithmData(id, d, offset, length);
    }

    protected final void encodeCIIOctetAlgorithmData(int id, byte[] d, int offset, int length) throws IOException {
        this.write(0x8C | (id & 0xC0) >> 6);
        this._b = (id & 0x3F) << 2;
        this.encodeNonZeroOctetStringLengthOnSenventhBit(length);
        this.write(d, offset, length);
    }

    protected final void encodeCIIObjectAlgorithmData(int id, Object data, EncodingAlgorithm ea) throws FastInfosetException, IOException {
        this.write(0x8C | (id & 0xC0) >> 6);
        this._b = (id & 0x3F) << 2;
        this._encodingBufferOutputStream.reset();
        ea.encodeToOutputStream(data, this._encodingBufferOutputStream);
        this.encodeNonZeroOctetStringLengthOnSenventhBit(this._encodingBufferIndex);
        this.write(this._encodingBuffer, this._encodingBufferIndex);
    }

    protected final void encodeCIIBuiltInAlgorithmData(int id, Object data, int offset, int length) throws FastInfosetException, IOException {
        this.write(0x8C | (id & 0xC0) >> 6);
        this._b = (id & 0x3F) << 2;
        int octetLength = BuiltInEncodingAlgorithmFactory.getAlgorithm(id).getOctetLengthFromPrimitiveLength(length);
        this.encodeNonZeroOctetStringLengthOnSenventhBit(octetLength);
        this.ensureSize(octetLength);
        BuiltInEncodingAlgorithmFactory.getAlgorithm(id).encodeToBytes(data, offset, length, this._octetBuffer, this._octetBufferIndex);
        this._octetBufferIndex += octetLength;
    }

    protected final void encodeCIIBuiltInAlgorithmDataAsCDATA(char[] ch, int offset, int length) throws FastInfosetException, IOException {
        this.write(140);
        this._b = 36;
        length = this.encodeUTF8String(ch, offset, length);
        this.encodeNonZeroOctetStringLengthOnSenventhBit(length);
        this.write(this._encodingBuffer, length);
    }

    protected final void encodeIdentifyingNonEmptyStringOnFirstBit(String s, StringIntMap map) throws IOException {
        int index = map.obtainIndex(s);
        if (index == -1) {
            this.encodeNonEmptyOctetStringOnSecondBit(s);
        } else {
            this.encodeNonZeroIntegerOnSecondBitFirstBitOne(index);
        }
    }

    protected final void encodeNonEmptyOctetStringOnSecondBit(String s) throws IOException {
        int length = this.encodeUTF8String(s);
        this.encodeNonZeroOctetStringLengthOnSecondBit(length);
        this.write(this._encodingBuffer, length);
    }

    protected final void encodeNonZeroOctetStringLengthOnSecondBit(int length) throws IOException {
        if (length < 65) {
            this.write(length - 1);
        } else if (length < 321) {
            this.write(64);
            this.write(length - 65);
        } else {
            this.write(96);
            this.write((length -= 321) >>> 24);
            this.write(length >> 16 & 0xFF);
            this.write(length >> 8 & 0xFF);
            this.write(length & 0xFF);
        }
    }

    protected final void encodeNonEmptyCharacterStringOnFifthBit(String s) throws IOException {
        int length = this._encodingStringsAsUtf8 ? this.encodeUTF8String(s) : this.encodeUtf16String(s);
        this.encodeNonZeroOctetStringLengthOnFifthBit(length);
        this.write(this._encodingBuffer, length);
    }

    protected final void encodeNonEmptyCharacterStringOnFifthBit(char[] ch, int offset, int length) throws IOException {
        length = this._encodingStringsAsUtf8 ? this.encodeUTF8String(ch, offset, length) : this.encodeUtf16String(ch, offset, length);
        this.encodeNonZeroOctetStringLengthOnFifthBit(length);
        this.write(this._encodingBuffer, length);
    }

    protected final void encodeNonZeroOctetStringLengthOnFifthBit(int length) throws IOException {
        if (length < 9) {
            this.write(this._b | length - 1);
        } else if (length < 265) {
            this.write(this._b | 8);
            this.write(length - 9);
        } else {
            this.write(this._b | 0xC);
            this.write((length -= 265) >>> 24);
            this.write(length >> 16 & 0xFF);
            this.write(length >> 8 & 0xFF);
            this.write(length & 0xFF);
        }
    }

    protected final void encodeNonEmptyCharacterStringOnSeventhBit(char[] ch, int offset, int length) throws IOException {
        length = this._encodingStringsAsUtf8 ? this.encodeUTF8String(ch, offset, length) : this.encodeUtf16String(ch, offset, length);
        this.encodeNonZeroOctetStringLengthOnSenventhBit(length);
        this.write(this._encodingBuffer, length);
    }

    protected final void encodeNonEmptyFourBitCharacterStringOnSeventhBit(int[] table, char[] ch, int offset, int length) throws FastInfosetException, IOException {
        int octetPairLength = length / 2;
        int octetSingleLength = length % 2;
        this.encodeNonZeroOctetStringLengthOnSenventhBit(octetPairLength + octetSingleLength);
        this.encodeNonEmptyFourBitCharacterString(table, ch, offset, octetPairLength, octetSingleLength);
    }

    protected final void encodeNonEmptyFourBitCharacterString(int[] table, char[] ch, int offset, int octetPairLength, int octetSingleLength) throws FastInfosetException, IOException {
        this.ensureSize(octetPairLength + octetSingleLength);
        int v = 0;
        for (int i = 0; i < octetPairLength; ++i) {
            if ((v = table[ch[offset++]] << 4 | table[ch[offset++]]) < 0) {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.characterOutofAlphabetRange"));
            }
            this._octetBuffer[this._octetBufferIndex++] = (byte)v;
        }
        if (octetSingleLength == 1) {
            v = table[ch[offset]] << 4 | 0xF;
            if (v < 0) {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.characterOutofAlphabetRange"));
            }
            this._octetBuffer[this._octetBufferIndex++] = (byte)v;
        }
    }

    protected final void encodeNonEmptyNBitCharacterStringOnSeventhBit(String alphabet, char[] ch, int offset, int length) throws FastInfosetException, IOException {
        int bitsPerCharacter = 1;
        while (1 << bitsPerCharacter <= alphabet.length()) {
            ++bitsPerCharacter;
        }
        int bits = length * bitsPerCharacter;
        int octets = bits / 8;
        int bitsOfLastOctet = bits % 8;
        int totalOctets = octets + (bitsOfLastOctet > 0 ? 1 : 0);
        this.encodeNonZeroOctetStringLengthOnSenventhBit(totalOctets);
        this.resetBits();
        this.ensureSize(totalOctets);
        int v = 0;
        for (int i = 0; i < length; ++i) {
            char c = ch[offset + i];
            for (v = 0; v < alphabet.length() && c != alphabet.charAt(v); ++v) {
            }
            if (v == alphabet.length()) {
                throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.characterOutofAlphabetRange"));
            }
            this.writeBits(bitsPerCharacter, v);
        }
        if (bitsOfLastOctet > 0) {
            this._b |= (1 << 8 - bitsOfLastOctet) - 1;
            this.write(this._b);
        }
    }

    private final void resetBits() {
        this._bitsLeftInOctet = 8;
        this._b = 0;
    }

    private final void writeBits(int bits, int v) throws IOException {
        while (bits > 0) {
            int bit = (v & 1 << --bits) > 0 ? 1 : 0;
            this._b |= bit << --this._bitsLeftInOctet;
            if (this._bitsLeftInOctet != 0) continue;
            this.write(this._b);
            this._bitsLeftInOctet = 8;
            this._b = 0;
        }
    }

    protected final void encodeNonZeroOctetStringLengthOnSenventhBit(int length) throws IOException {
        if (length < 3) {
            this.write(this._b | length - 1);
        } else if (length < 259) {
            this.write(this._b | 2);
            this.write(length - 3);
        } else {
            this.write(this._b | 3);
            this.write((length -= 259) >>> 24);
            this.write(length >> 16 & 0xFF);
            this.write(length >> 8 & 0xFF);
            this.write(length & 0xFF);
        }
    }

    protected final void encodeNonZeroIntegerOnSecondBitFirstBitOne(int i) throws IOException {
        if (i < 64) {
            this.write(0x80 | i);
        } else if (i < 8256) {
            this._b = 0xC0 | (i -= 64) >> 8;
            this.write(this._b);
            this.write(i & 0xFF);
        } else if (i < 0x100000) {
            this._b = 0xE0 | (i -= 8256) >> 16;
            this.write(this._b);
            this.write(i >> 8 & 0xFF);
            this.write(i & 0xFF);
        } else {
            throw new IOException(CommonResourceBundle.getInstance().getString("message.integerMaxSize", new Object[]{0x100000}));
        }
    }

    protected final void encodeNonZeroIntegerOnSecondBitFirstBitZero(int i) throws IOException {
        if (i < 64) {
            this.write(i);
        } else if (i < 8256) {
            this._b = 0x40 | (i -= 64) >> 8;
            this.write(this._b);
            this.write(i & 0xFF);
        } else {
            this._b = 0x60 | (i -= 8256) >> 16;
            this.write(this._b);
            this.write(i >> 8 & 0xFF);
            this.write(i & 0xFF);
        }
    }

    protected final void encodeNonZeroIntegerOnThirdBit(int i) throws IOException {
        if (i < 32) {
            this.write(this._b | i);
        } else if (i < 2080) {
            this._b |= 0x20 | (i -= 32) >> 8;
            this.write(this._b);
            this.write(i & 0xFF);
        } else if (i < 526368) {
            this._b |= 0x28 | (i -= 2080) >> 16;
            this.write(this._b);
            this.write(i >> 8 & 0xFF);
            this.write(i & 0xFF);
        } else {
            this._b |= 0x30;
            this.write(this._b);
            this.write((i -= 526368) >> 16);
            this.write(i >> 8 & 0xFF);
            this.write(i & 0xFF);
        }
    }

    protected final void encodeNonZeroIntegerOnFourthBit(int i) throws IOException {
        if (i < 16) {
            this.write(this._b | i);
        } else if (i < 1040) {
            this._b |= 0x10 | (i -= 16) >> 8;
            this.write(this._b);
            this.write(i & 0xFF);
        } else if (i < 263184) {
            this._b |= 0x14 | (i -= 1040) >> 16;
            this.write(this._b);
            this.write(i >> 8 & 0xFF);
            this.write(i & 0xFF);
        } else {
            this._b |= 0x18;
            this.write(this._b);
            this.write((i -= 263184) >> 16);
            this.write(i >> 8 & 0xFF);
            this.write(i & 0xFF);
        }
    }

    protected final void encodeNonEmptyUTF8StringAsOctetString(int b, String s, int[] constants) throws IOException {
        char[] ch = s.toCharArray();
        this.encodeNonEmptyUTF8StringAsOctetString(b, ch, 0, ch.length, constants);
    }

    protected final void encodeNonEmptyUTF8StringAsOctetString(int b, char[] ch, int offset, int length, int[] constants) throws IOException {
        length = this.encodeUTF8String(ch, offset, length);
        this.encodeNonZeroOctetStringLength(b, length, constants);
        this.write(this._encodingBuffer, length);
    }

    protected final void encodeNonZeroOctetStringLength(int b, int length, int[] constants) throws IOException {
        if (length < constants[0]) {
            this.write(b | length - 1);
        } else if (length < constants[1]) {
            this.write(b | constants[2]);
            this.write(length - constants[0]);
        } else {
            this.write(b | constants[3]);
            this.write((length -= constants[1]) >>> 24);
            this.write(length >> 16 & 0xFF);
            this.write(length >> 8 & 0xFF);
            this.write(length & 0xFF);
        }
    }

    protected final void encodeNonZeroInteger(int b, int i, int[] constants) throws IOException {
        if (i < constants[0]) {
            this.write(b | i);
        } else if (i < constants[1]) {
            this.write(b | constants[3] | (i -= constants[0]) >> 8);
            this.write(i & 0xFF);
        } else if (i < constants[2]) {
            this.write(b | constants[4] | (i -= constants[1]) >> 16);
            this.write(i >> 8 & 0xFF);
            this.write(i & 0xFF);
        } else if (i < 0x100000) {
            this.write(b | constants[5]);
            this.write((i -= constants[2]) >> 16);
            this.write(i >> 8 & 0xFF);
            this.write(i & 0xFF);
        } else {
            throw new IOException(CommonResourceBundle.getInstance().getString("message.integerMaxSize", new Object[]{0x100000}));
        }
    }

    protected final void mark() {
        this._markIndex = this._octetBufferIndex;
    }

    protected final void resetMark() {
        this._markIndex = -1;
    }

    protected final boolean hasMark() {
        return this._markIndex != -1;
    }

    protected final void write(int i) throws IOException {
        if (this._octetBufferIndex < this._octetBuffer.length) {
            this._octetBuffer[this._octetBufferIndex++] = (byte)i;
        } else if (this._markIndex == -1) {
            this._s.write(this._octetBuffer);
            this._octetBufferIndex = 1;
            this._octetBuffer[0] = (byte)i;
        } else {
            this.resize(this._octetBuffer.length * 3 / 2);
            this._octetBuffer[this._octetBufferIndex++] = (byte)i;
        }
    }

    protected final void write(byte[] b, int length) throws IOException {
        this.write(b, 0, length);
    }

    protected final void write(byte[] b, int offset, int length) throws IOException {
        if (this._octetBufferIndex + length < this._octetBuffer.length) {
            System.arraycopy(b, offset, this._octetBuffer, this._octetBufferIndex, length);
            this._octetBufferIndex += length;
        } else if (this._markIndex == -1) {
            this._s.write(this._octetBuffer, 0, this._octetBufferIndex);
            this._s.write(b, offset, length);
            this._octetBufferIndex = 0;
        } else {
            this.resize((this._octetBuffer.length + length) * 3 / 2 + 1);
            System.arraycopy(b, offset, this._octetBuffer, this._octetBufferIndex, length);
            this._octetBufferIndex += length;
        }
    }

    private void ensureSize(int length) {
        if (this._octetBufferIndex + length > this._octetBuffer.length) {
            this.resize((this._octetBufferIndex + length) * 3 / 2 + 1);
        }
    }

    private void resize(int length) {
        byte[] b = new byte[length];
        System.arraycopy(this._octetBuffer, 0, b, 0, this._octetBufferIndex);
        this._octetBuffer = b;
    }

    private void _flush() throws IOException {
        if (this._octetBufferIndex > 0) {
            this._s.write(this._octetBuffer, 0, this._octetBufferIndex);
            this._octetBufferIndex = 0;
        }
    }

    protected final int encodeUTF8String(String s) throws IOException {
        int length = s.length();
        if (length < this._charBuffer.length) {
            s.getChars(0, length, this._charBuffer, 0);
            return this.encodeUTF8String(this._charBuffer, 0, length);
        }
        char[] ch = s.toCharArray();
        return this.encodeUTF8String(ch, 0, length);
    }

    private void ensureEncodingBufferSizeForUtf8String(int length) {
        int newLength = 4 * length;
        if (this._encodingBuffer.length < newLength) {
            this._encodingBuffer = new byte[newLength];
        }
    }

    protected final int encodeUTF8String(char[] ch, int offset, int length) throws IOException {
        int bpos = 0;
        this.ensureEncodingBufferSizeForUtf8String(length);
        int end = offset + length;
        while (end != offset) {
            char c;
            if ((c = ch[offset++]) < '\u0080') {
                this._encodingBuffer[bpos++] = (byte)c;
                continue;
            }
            if (c < '\u0800') {
                this._encodingBuffer[bpos++] = (byte)(0xC0 | c >> 6);
                this._encodingBuffer[bpos++] = (byte)(0x80 | c & 0x3F);
                continue;
            }
            if (c > '\uffff') continue;
            if (!XMLChar.isHighSurrogate(c) && !XMLChar.isLowSurrogate(c)) {
                this._encodingBuffer[bpos++] = (byte)(0xE0 | c >> 12);
                this._encodingBuffer[bpos++] = (byte)(0x80 | c >> 6 & 0x3F);
                this._encodingBuffer[bpos++] = (byte)(0x80 | c & 0x3F);
                continue;
            }
            this.encodeCharacterAsUtf8FourByte(c, ch, offset, end, bpos);
            bpos += 4;
            ++offset;
        }
        return bpos;
    }

    private void encodeCharacterAsUtf8FourByte(int c, char[] ch, int chpos, int chend, int bpos) throws IOException {
        if (chpos == chend) {
            throw new IOException("");
        }
        char d = ch[chpos];
        if (!XMLChar.isLowSurrogate(d)) {
            throw new IOException("");
        }
        int uc = ((c & 0x3FF) << 10 | d & 0x3FF) + 65536;
        if (uc < 0 || uc >= 0x200000) {
            throw new IOException("");
        }
        this._encodingBuffer[bpos++] = (byte)(0xF0 | uc >> 18);
        this._encodingBuffer[bpos++] = (byte)(0x80 | uc >> 12 & 0x3F);
        this._encodingBuffer[bpos++] = (byte)(0x80 | uc >> 6 & 0x3F);
        this._encodingBuffer[bpos++] = (byte)(0x80 | uc & 0x3F);
    }

    protected final int encodeUtf16String(String s) throws IOException {
        int length = s.length();
        if (length < this._charBuffer.length) {
            s.getChars(0, length, this._charBuffer, 0);
            return this.encodeUtf16String(this._charBuffer, 0, length);
        }
        char[] ch = s.toCharArray();
        return this.encodeUtf16String(ch, 0, length);
    }

    private void ensureEncodingBufferSizeForUtf16String(int length) {
        int newLength = 2 * length;
        if (this._encodingBuffer.length < newLength) {
            this._encodingBuffer = new byte[newLength];
        }
    }

    protected final int encodeUtf16String(char[] ch, int offset, int length) throws IOException {
        int byteLength = 0;
        this.ensureEncodingBufferSizeForUtf16String(length);
        int n = offset + length;
        for (int i = offset; i < n; ++i) {
            char c = ch[i];
            this._encodingBuffer[byteLength++] = (byte)(c >> 8);
            this._encodingBuffer[byteLength++] = (byte)(c & 0xFF);
        }
        return byteLength;
    }

    public static String getPrefixFromQualifiedName(String qName) {
        int i = qName.indexOf(58);
        String prefix = "";
        if (i != -1) {
            prefix = qName.substring(0, i);
        }
        return prefix;
    }

    public static boolean isWhiteSpace(char[] ch, int start, int length) {
        if (!XMLChar.isSpace(ch[start])) {
            return false;
        }
        int end = start + length;
        while (++start < end && XMLChar.isSpace(ch[start])) {
        }
        return start == end;
    }

    public static boolean isWhiteSpace(String s) {
        if (!XMLChar.isSpace(s.charAt(0))) {
            return false;
        }
        int end = s.length();
        int start = 1;
        while (start < end && XMLChar.isSpace(s.charAt(start++))) {
        }
        return start == end;
    }

    static /* synthetic */ byte[] access$202(Encoder x0, byte[] x1) {
        x0._encodingBuffer = x1;
        return x1;
    }

    static {
        int i;
        _characterEncodingSchemeSystemDefault = Encoder.getDefaultEncodingScheme();
        NUMERIC_CHARACTERS_TABLE = new int[Encoder.maxCharacter("0123456789-+.E ") + 1];
        DATE_TIME_CHARACTERS_TABLE = new int[Encoder.maxCharacter("0123456789-:TZ ") + 1];
        for (i = 0; i < NUMERIC_CHARACTERS_TABLE.length; ++i) {
            Encoder.NUMERIC_CHARACTERS_TABLE[i] = -1;
        }
        for (i = 0; i < DATE_TIME_CHARACTERS_TABLE.length; ++i) {
            Encoder.DATE_TIME_CHARACTERS_TABLE[i] = -1;
        }
        for (i = 0; i < "0123456789-+.E ".length(); ++i) {
            Encoder.NUMERIC_CHARACTERS_TABLE["0123456789-+.E ".charAt((int)i)] = i;
        }
        for (i = 0; i < "0123456789-:TZ ".length(); ++i) {
            Encoder.DATE_TIME_CHARACTERS_TABLE["0123456789-:TZ ".charAt((int)i)] = i;
        }
    }

    private class EncodingBufferOutputStream
    extends OutputStream {
        private EncodingBufferOutputStream() {
        }

        @Override
        public void write(int b) throws IOException {
            if (Encoder.this._encodingBufferIndex < Encoder.this._encodingBuffer.length) {
                ((Encoder)Encoder.this)._encodingBuffer[((Encoder)Encoder.this)._encodingBufferIndex++] = (byte)b;
            } else {
                byte[] newbuf = new byte[Math.max(Encoder.this._encodingBuffer.length << 1, Encoder.this._encodingBufferIndex)];
                System.arraycopy(Encoder.this._encodingBuffer, 0, newbuf, 0, Encoder.this._encodingBufferIndex);
                Encoder.access$202(Encoder.this, newbuf);
                ((Encoder)Encoder.this)._encodingBuffer[((Encoder)Encoder.this)._encodingBufferIndex++] = (byte)b;
            }
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
                throw new IndexOutOfBoundsException();
            }
            if (len == 0) {
                return;
            }
            int newoffset = Encoder.this._encodingBufferIndex + len;
            if (newoffset > Encoder.this._encodingBuffer.length) {
                byte[] newbuf = new byte[Math.max(Encoder.this._encodingBuffer.length << 1, newoffset)];
                System.arraycopy(Encoder.this._encodingBuffer, 0, newbuf, 0, Encoder.this._encodingBufferIndex);
                Encoder.access$202(Encoder.this, newbuf);
            }
            System.arraycopy(b, off, Encoder.this._encodingBuffer, Encoder.this._encodingBufferIndex, len);
            Encoder.this._encodingBufferIndex = newoffset;
        }

        public int getLength() {
            return Encoder.this._encodingBufferIndex;
        }

        public void reset() {
            Encoder.this._encodingBufferIndex = 0;
        }
    }
}

