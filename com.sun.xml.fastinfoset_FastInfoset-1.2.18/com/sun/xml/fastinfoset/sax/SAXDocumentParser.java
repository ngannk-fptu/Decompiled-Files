/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.sax;

import com.sun.xml.fastinfoset.CommonResourceBundle;
import com.sun.xml.fastinfoset.Decoder;
import com.sun.xml.fastinfoset.DecoderStateTables;
import com.sun.xml.fastinfoset.EncodingConstants;
import com.sun.xml.fastinfoset.QualifiedName;
import com.sun.xml.fastinfoset.algorithm.BuiltInEncodingAlgorithmFactory;
import com.sun.xml.fastinfoset.algorithm.BuiltInEncodingAlgorithmState;
import com.sun.xml.fastinfoset.sax.AttributesHolder;
import com.sun.xml.fastinfoset.sax.SystemIdResolver;
import com.sun.xml.fastinfoset.util.CharArray;
import com.sun.xml.fastinfoset.util.CharArrayString;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jvnet.fastinfoset.EncodingAlgorithm;
import org.jvnet.fastinfoset.EncodingAlgorithmException;
import org.jvnet.fastinfoset.FastInfosetException;
import org.jvnet.fastinfoset.sax.EncodingAlgorithmContentHandler;
import org.jvnet.fastinfoset.sax.FastInfosetReader;
import org.jvnet.fastinfoset.sax.PrimitiveTypeContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class SAXDocumentParser
extends Decoder
implements FastInfosetReader {
    private static final Logger logger = Logger.getLogger(SAXDocumentParser.class.getName());
    protected boolean _namespacePrefixesFeature = false;
    protected EntityResolver _entityResolver;
    protected DTDHandler _dtdHandler;
    protected ContentHandler _contentHandler;
    protected ErrorHandler _errorHandler;
    protected LexicalHandler _lexicalHandler;
    protected DeclHandler _declHandler;
    protected EncodingAlgorithmContentHandler _algorithmHandler;
    protected PrimitiveTypeContentHandler _primitiveHandler;
    protected BuiltInEncodingAlgorithmState builtInAlgorithmState = new BuiltInEncodingAlgorithmState();
    protected AttributesHolder _attributes;
    protected int[] _namespacePrefixes = new int[16];
    protected int _namespacePrefixesIndex;
    protected boolean _clearAttributes = false;

    public SAXDocumentParser() {
        DefaultHandler handler = new DefaultHandler();
        this._attributes = new AttributesHolder(this._registeredEncodingAlgorithms);
        this._entityResolver = handler;
        this._dtdHandler = handler;
        this._contentHandler = handler;
        this._errorHandler = handler;
        this._lexicalHandler = new LexicalHandlerImpl();
        this._declHandler = new DeclHandlerImpl();
    }

    protected void resetOnError() {
        this._clearAttributes = false;
        this._attributes.clear();
        this._namespacePrefixesIndex = 0;
        if (this._v != null) {
            this._v.prefix.clearCompletely();
        }
        this._duplicateAttributeVerifier.clear();
    }

    @Override
    public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name.equals("http://xml.org/sax/features/namespaces")) {
            return true;
        }
        if (name.equals("http://xml.org/sax/features/namespace-prefixes")) {
            return this._namespacePrefixesFeature;
        }
        if (name.equals("http://xml.org/sax/features/string-interning") || name.equals("http://jvnet.org/fastinfoset/parser/properties/string-interning")) {
            return this.getStringInterning();
        }
        throw new SAXNotRecognizedException(CommonResourceBundle.getInstance().getString("message.featureNotSupported") + name);
    }

    @Override
    public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name.equals("http://xml.org/sax/features/namespaces")) {
            if (!value) {
                throw new SAXNotSupportedException(name + ":" + value);
            }
        } else if (name.equals("http://xml.org/sax/features/namespace-prefixes")) {
            this._namespacePrefixesFeature = value;
        } else if (name.equals("http://xml.org/sax/features/string-interning") || name.equals("http://jvnet.org/fastinfoset/parser/properties/string-interning")) {
            this.setStringInterning(value);
        } else {
            throw new SAXNotRecognizedException(CommonResourceBundle.getInstance().getString("message.featureNotSupported") + name);
        }
    }

    @Override
    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name.equals("http://xml.org/sax/properties/lexical-handler")) {
            return this.getLexicalHandler();
        }
        if (name.equals("http://xml.org/sax/properties/declaration-handler")) {
            return this.getDeclHandler();
        }
        if (name.equals("http://jvnet.org/fastinfoset/parser/properties/external-vocabularies")) {
            return this.getExternalVocabularies();
        }
        if (name.equals("http://jvnet.org/fastinfoset/parser/properties/registered-encoding-algorithms")) {
            return this.getRegisteredEncodingAlgorithms();
        }
        if (name.equals("http://jvnet.org/fastinfoset/sax/properties/encoding-algorithm-content-handler")) {
            return this.getEncodingAlgorithmContentHandler();
        }
        if (name.equals("http://jvnet.org/fastinfoset/sax/properties/primitive-type-content-handler")) {
            return this.getPrimitiveTypeContentHandler();
        }
        throw new SAXNotRecognizedException(CommonResourceBundle.getInstance().getString("message.propertyNotRecognized", new Object[]{name}));
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (name.equals("http://xml.org/sax/properties/lexical-handler")) {
            if (!(value instanceof LexicalHandler)) throw new SAXNotSupportedException("http://xml.org/sax/properties/lexical-handler");
            this.setLexicalHandler((LexicalHandler)value);
            return;
        } else if (name.equals("http://xml.org/sax/properties/declaration-handler")) {
            if (!(value instanceof DeclHandler)) throw new SAXNotSupportedException("http://xml.org/sax/properties/lexical-handler");
            this.setDeclHandler((DeclHandler)value);
            return;
        } else if (name.equals("http://jvnet.org/fastinfoset/parser/properties/external-vocabularies")) {
            if (!(value instanceof Map)) throw new SAXNotSupportedException("http://jvnet.org/fastinfoset/parser/properties/external-vocabularies");
            this.setExternalVocabularies((Map)value);
            return;
        } else if (name.equals("http://jvnet.org/fastinfoset/parser/properties/registered-encoding-algorithms")) {
            if (!(value instanceof Map)) throw new SAXNotSupportedException("http://jvnet.org/fastinfoset/parser/properties/registered-encoding-algorithms");
            this.setRegisteredEncodingAlgorithms((Map)value);
            return;
        } else if (name.equals("http://jvnet.org/fastinfoset/sax/properties/encoding-algorithm-content-handler")) {
            if (!(value instanceof EncodingAlgorithmContentHandler)) throw new SAXNotSupportedException("http://jvnet.org/fastinfoset/sax/properties/encoding-algorithm-content-handler");
            this.setEncodingAlgorithmContentHandler((EncodingAlgorithmContentHandler)value);
            return;
        } else if (name.equals("http://jvnet.org/fastinfoset/sax/properties/primitive-type-content-handler")) {
            if (!(value instanceof PrimitiveTypeContentHandler)) throw new SAXNotSupportedException("http://jvnet.org/fastinfoset/sax/properties/primitive-type-content-handler");
            this.setPrimitiveTypeContentHandler((PrimitiveTypeContentHandler)value);
            return;
        } else {
            if (!name.equals("http://jvnet.org/fastinfoset/parser/properties/buffer-size")) throw new SAXNotRecognizedException(CommonResourceBundle.getInstance().getString("message.propertyNotRecognized", new Object[]{name}));
            if (!(value instanceof Integer)) throw new SAXNotSupportedException("http://jvnet.org/fastinfoset/parser/properties/buffer-size");
            this.setBufferSize((Integer)value);
        }
    }

    @Override
    public void setEntityResolver(EntityResolver resolver) {
        this._entityResolver = resolver;
    }

    @Override
    public EntityResolver getEntityResolver() {
        return this._entityResolver;
    }

    @Override
    public void setDTDHandler(DTDHandler handler) {
        this._dtdHandler = handler;
    }

    @Override
    public DTDHandler getDTDHandler() {
        return this._dtdHandler;
    }

    @Override
    public void setContentHandler(ContentHandler handler) {
        this._contentHandler = handler;
    }

    @Override
    public ContentHandler getContentHandler() {
        return this._contentHandler;
    }

    @Override
    public void setErrorHandler(ErrorHandler handler) {
        this._errorHandler = handler;
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return this._errorHandler;
    }

    @Override
    public void parse(InputSource input) throws IOException, SAXException {
        try {
            InputStream s = input.getByteStream();
            if (s == null) {
                String systemId = input.getSystemId();
                if (systemId == null) {
                    throw new SAXException(CommonResourceBundle.getInstance().getString("message.inputSource"));
                }
                this.parse(systemId);
            } else {
                this.parse(s);
            }
        }
        catch (FastInfosetException e) {
            logger.log(Level.FINE, "parsing error", e);
            throw new SAXException(e);
        }
    }

    @Override
    public void parse(String systemId) throws IOException, SAXException {
        try {
            systemId = SystemIdResolver.getAbsoluteURI(systemId);
            this.parse(new URL(systemId).openStream());
        }
        catch (FastInfosetException e) {
            logger.log(Level.FINE, "parsing error", e);
            throw new SAXException(e);
        }
    }

    @Override
    public final void parse(InputStream s) throws IOException, FastInfosetException, SAXException {
        this.setInputStream(s);
        this.parse();
    }

    @Override
    public void setLexicalHandler(LexicalHandler handler) {
        this._lexicalHandler = handler;
    }

    @Override
    public LexicalHandler getLexicalHandler() {
        return this._lexicalHandler;
    }

    @Override
    public void setDeclHandler(DeclHandler handler) {
        this._declHandler = handler;
    }

    @Override
    public DeclHandler getDeclHandler() {
        return this._declHandler;
    }

    @Override
    public void setEncodingAlgorithmContentHandler(EncodingAlgorithmContentHandler handler) {
        this._algorithmHandler = handler;
    }

    @Override
    public EncodingAlgorithmContentHandler getEncodingAlgorithmContentHandler() {
        return this._algorithmHandler;
    }

    @Override
    public void setPrimitiveTypeContentHandler(PrimitiveTypeContentHandler handler) {
        this._primitiveHandler = handler;
    }

    @Override
    public PrimitiveTypeContentHandler getPrimitiveTypeContentHandler() {
        return this._primitiveHandler;
    }

    public final void parse() throws FastInfosetException, IOException {
        if (this._octetBuffer.length < this._bufferSize) {
            this._octetBuffer = new byte[this._bufferSize];
        }
        try {
            this.reset();
            this.decodeHeader();
            if (this._parseFragments) {
                this.processDIIFragment();
            } else {
                this.processDII();
            }
        }
        catch (RuntimeException e) {
            try {
                this._errorHandler.fatalError(new SAXParseException(e.getClass().getName(), null, e));
            }
            catch (Exception exception) {
                // empty catch block
            }
            this.resetOnError();
            throw new FastInfosetException(e);
        }
        catch (FastInfosetException e) {
            try {
                this._errorHandler.fatalError(new SAXParseException(e.getClass().getName(), null, e));
            }
            catch (Exception exception) {
                // empty catch block
            }
            this.resetOnError();
            throw e;
        }
        catch (IOException e) {
            try {
                this._errorHandler.fatalError(new SAXParseException(e.getClass().getName(), null, e));
            }
            catch (Exception exception) {
                // empty catch block
            }
            this.resetOnError();
            throw e;
        }
    }

    protected final void processDII() throws FastInfosetException, IOException {
        try {
            this._contentHandler.startDocument();
        }
        catch (SAXException e) {
            throw new FastInfosetException("processDII", e);
        }
        this._b = this.read();
        if (this._b > 0) {
            this.processDIIOptionalProperties();
        }
        boolean firstElementHasOccured = false;
        boolean documentTypeDeclarationOccured = false;
        block28: while (!this._terminate || !firstElementHasOccured) {
            this._b = this.read();
            switch (DecoderStateTables.DII(this._b)) {
                case 0: {
                    this.processEII(this._elementNameTable._array[this._b], false);
                    firstElementHasOccured = true;
                    continue block28;
                }
                case 1: {
                    this.processEII(this._elementNameTable._array[this._b & 0x1F], true);
                    firstElementHasOccured = true;
                    continue block28;
                }
                case 2: {
                    this.processEII(this.decodeEIIIndexMedium(), (this._b & 0x40) > 0);
                    firstElementHasOccured = true;
                    continue block28;
                }
                case 3: {
                    this.processEII(this.decodeEIIIndexLarge(), (this._b & 0x40) > 0);
                    firstElementHasOccured = true;
                    continue block28;
                }
                case 5: {
                    QualifiedName qn = this.decodeLiteralQualifiedName(this._b & 3, this._elementNameTable.getNext());
                    this._elementNameTable.add(qn);
                    this.processEII(qn, (this._b & 0x40) > 0);
                    firstElementHasOccured = true;
                    continue block28;
                }
                case 4: {
                    this.processEIIWithNamespaces();
                    firstElementHasOccured = true;
                    continue block28;
                }
                case 20: {
                    if (documentTypeDeclarationOccured) {
                        throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.secondOccurenceOfDTDII"));
                    }
                    documentTypeDeclarationOccured = true;
                    String system_identifier = (this._b & 2) > 0 ? this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : "";
                    String public_identifier = (this._b & 1) > 0 ? this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : "";
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
                    if (this._notations != null) {
                        this._notations.clear();
                    }
                    if (this._unparsedEntities == null) continue block28;
                    this._unparsedEntities.clear();
                    continue block28;
                }
                case 18: {
                    this.processCommentII();
                    continue block28;
                }
                case 19: {
                    this.processProcessingII();
                    continue block28;
                }
                case 23: {
                    this._doubleTerminate = true;
                }
                case 22: {
                    this._terminate = true;
                    continue block28;
                }
            }
            throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingDII"));
        }
        block30: while (!this._terminate) {
            this._b = this.read();
            switch (DecoderStateTables.DII(this._b)) {
                case 18: {
                    this.processCommentII();
                    continue block30;
                }
                case 19: {
                    this.processProcessingII();
                    continue block30;
                }
                case 23: {
                    this._doubleTerminate = true;
                }
                case 22: {
                    this._terminate = true;
                    continue block30;
                }
            }
            throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingDII"));
        }
        try {
            this._contentHandler.endDocument();
        }
        catch (SAXException e) {
            throw new FastInfosetException("processDII", e);
        }
    }

    protected final void processDIIFragment() throws FastInfosetException, IOException {
        try {
            this._contentHandler.startDocument();
        }
        catch (SAXException e) {
            throw new FastInfosetException("processDII", e);
        }
        this._b = this.read();
        if (this._b > 0) {
            this.processDIIOptionalProperties();
        }
        block47: while (!this._terminate) {
            this._b = this.read();
            switch (DecoderStateTables.EII(this._b)) {
                case 0: {
                    this.processEII(this._elementNameTable._array[this._b], false);
                    continue block47;
                }
                case 1: {
                    this.processEII(this._elementNameTable._array[this._b & 0x1F], true);
                    continue block47;
                }
                case 2: {
                    this.processEII(this.decodeEIIIndexMedium(), (this._b & 0x40) > 0);
                    continue block47;
                }
                case 3: {
                    this.processEII(this.decodeEIIIndexLarge(), (this._b & 0x40) > 0);
                    continue block47;
                }
                case 5: {
                    QualifiedName qn = this.decodeLiteralQualifiedName(this._b & 3, this._elementNameTable.getNext());
                    this._elementNameTable.add(qn);
                    this.processEII(qn, (this._b & 0x40) > 0);
                    continue block47;
                }
                case 4: {
                    this.processEIIWithNamespaces();
                    continue block47;
                }
                case 6: {
                    this._octetBufferLength = (this._b & 1) + 1;
                    this.processUtf8CharacterString();
                    continue block47;
                }
                case 7: {
                    this._octetBufferLength = this.read() + 3;
                    this.processUtf8CharacterString();
                    continue block47;
                }
                case 8: {
                    this._octetBufferLength = (this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read()) + 259;
                    this.processUtf8CharacterString();
                    continue block47;
                }
                case 9: {
                    this._octetBufferLength = (this._b & 1) + 1;
                    this.decodeUtf16StringAsCharBuffer();
                    if ((this._b & 0x10) > 0) {
                        this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
                    }
                    try {
                        this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
                        continue block47;
                    }
                    catch (SAXException e) {
                        throw new FastInfosetException("processCII", e);
                    }
                }
                case 10: {
                    this._octetBufferLength = this.read() + 3;
                    this.decodeUtf16StringAsCharBuffer();
                    if ((this._b & 0x10) > 0) {
                        this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
                    }
                    try {
                        this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
                        continue block47;
                    }
                    catch (SAXException e) {
                        throw new FastInfosetException("processCII", e);
                    }
                }
                case 11: {
                    this._octetBufferLength = (this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read()) + 259;
                    this.decodeUtf16StringAsCharBuffer();
                    if ((this._b & 0x10) > 0) {
                        this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
                    }
                    try {
                        this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
                        continue block47;
                    }
                    catch (SAXException e) {
                        throw new FastInfosetException("processCII", e);
                    }
                }
                case 12: {
                    boolean addToTable = (this._b & 0x10) > 0;
                    this._identifier = (this._b & 2) << 6;
                    this._b = this.read();
                    this._identifier |= (this._b & 0xFC) >> 2;
                    this.decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(this._b);
                    this.decodeRestrictedAlphabetAsCharBuffer();
                    if (addToTable) {
                        this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
                    }
                    try {
                        this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
                        continue block47;
                    }
                    catch (SAXException e) {
                        throw new FastInfosetException("processCII", e);
                    }
                }
                case 13: {
                    boolean addToTable = (this._b & 0x10) > 0;
                    this._identifier = (this._b & 2) << 6;
                    this._b = this.read();
                    this._identifier |= (this._b & 0xFC) >> 2;
                    this.decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(this._b);
                    this.processCIIEncodingAlgorithm(addToTable);
                    continue block47;
                }
                case 14: {
                    int index = this._b & 0xF;
                    try {
                        this._contentHandler.characters(this._characterContentChunkTable._array, this._characterContentChunkTable._offset[index], this._characterContentChunkTable._length[index]);
                        continue block47;
                    }
                    catch (SAXException e) {
                        throw new FastInfosetException("processCII", e);
                    }
                }
                case 15: {
                    int index = ((this._b & 3) << 8 | this.read()) + 16;
                    try {
                        this._contentHandler.characters(this._characterContentChunkTable._array, this._characterContentChunkTable._offset[index], this._characterContentChunkTable._length[index]);
                        continue block47;
                    }
                    catch (SAXException e) {
                        throw new FastInfosetException("processCII", e);
                    }
                }
                case 16: {
                    int index = ((this._b & 3) << 16 | this.read() << 8 | this.read()) + 1040;
                    try {
                        this._contentHandler.characters(this._characterContentChunkTable._array, this._characterContentChunkTable._offset[index], this._characterContentChunkTable._length[index]);
                        continue block47;
                    }
                    catch (SAXException e) {
                        throw new FastInfosetException("processCII", e);
                    }
                }
                case 17: {
                    int index = (this.read() << 16 | this.read() << 8 | this.read()) + 263184;
                    try {
                        this._contentHandler.characters(this._characterContentChunkTable._array, this._characterContentChunkTable._offset[index], this._characterContentChunkTable._length[index]);
                        continue block47;
                    }
                    catch (SAXException e) {
                        throw new FastInfosetException("processCII", e);
                    }
                }
                case 18: {
                    this.processCommentII();
                    continue block47;
                }
                case 19: {
                    this.processProcessingII();
                    continue block47;
                }
                case 21: {
                    String entity_reference_name = this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherNCName);
                    String system_identifier = (this._b & 2) > 0 ? this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : "";
                    String public_identifier = (this._b & 1) > 0 ? this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : "";
                    try {
                        this._contentHandler.skippedEntity(entity_reference_name);
                        continue block47;
                    }
                    catch (SAXException e) {
                        throw new FastInfosetException("processUnexpandedEntityReferenceII", e);
                    }
                }
                case 23: {
                    this._doubleTerminate = true;
                }
                case 22: {
                    this._terminate = true;
                    continue block47;
                }
            }
            throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingEII"));
        }
        try {
            this._contentHandler.endDocument();
        }
        catch (SAXException e) {
            throw new FastInfosetException("processDII", e);
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
            throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.qNameOfEIINotInScope"));
        }
        if (hasAttributes) {
            this.processAIIs();
        }
        try {
            this._contentHandler.startElement(name.namespaceName, name.localName, name.qName, this._attributes);
        }
        catch (SAXException e) {
            logger.log(Level.FINE, "processEII error", e);
            throw new FastInfosetException("processEII", e);
        }
        if (this._clearAttributes) {
            this._attributes.clear();
            this._clearAttributes = false;
        }
        block47: while (!this._terminate) {
            this._b = this.read();
            switch (DecoderStateTables.EII(this._b)) {
                case 0: {
                    this.processEII(this._elementNameTable._array[this._b], false);
                    continue block47;
                }
                case 1: {
                    this.processEII(this._elementNameTable._array[this._b & 0x1F], true);
                    continue block47;
                }
                case 2: {
                    this.processEII(this.decodeEIIIndexMedium(), (this._b & 0x40) > 0);
                    continue block47;
                }
                case 3: {
                    this.processEII(this.decodeEIIIndexLarge(), (this._b & 0x40) > 0);
                    continue block47;
                }
                case 5: {
                    QualifiedName qn = this.decodeLiteralQualifiedName(this._b & 3, this._elementNameTable.getNext());
                    this._elementNameTable.add(qn);
                    this.processEII(qn, (this._b & 0x40) > 0);
                    continue block47;
                }
                case 4: {
                    this.processEIIWithNamespaces();
                    continue block47;
                }
                case 6: {
                    this._octetBufferLength = (this._b & 1) + 1;
                    this.processUtf8CharacterString();
                    continue block47;
                }
                case 7: {
                    this._octetBufferLength = this.read() + 3;
                    this.processUtf8CharacterString();
                    continue block47;
                }
                case 8: {
                    this._octetBufferLength = (this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read()) + 259;
                    this.processUtf8CharacterString();
                    continue block47;
                }
                case 9: {
                    this._octetBufferLength = (this._b & 1) + 1;
                    this.decodeUtf16StringAsCharBuffer();
                    if ((this._b & 0x10) > 0) {
                        this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
                    }
                    try {
                        this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
                        continue block47;
                    }
                    catch (SAXException e) {
                        throw new FastInfosetException("processCII", e);
                    }
                }
                case 10: {
                    this._octetBufferLength = this.read() + 3;
                    this.decodeUtf16StringAsCharBuffer();
                    if ((this._b & 0x10) > 0) {
                        this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
                    }
                    try {
                        this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
                        continue block47;
                    }
                    catch (SAXException e) {
                        throw new FastInfosetException("processCII", e);
                    }
                }
                case 11: {
                    this._octetBufferLength = (this.read() << 24 | this.read() << 16 | this.read() << 8 | this.read()) + 259;
                    this.decodeUtf16StringAsCharBuffer();
                    if ((this._b & 0x10) > 0) {
                        this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
                    }
                    try {
                        this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
                        continue block47;
                    }
                    catch (SAXException e) {
                        throw new FastInfosetException("processCII", e);
                    }
                }
                case 12: {
                    boolean addToTable = (this._b & 0x10) > 0;
                    this._identifier = (this._b & 2) << 6;
                    this._b = this.read();
                    this._identifier |= (this._b & 0xFC) >> 2;
                    this.decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(this._b);
                    this.decodeRestrictedAlphabetAsCharBuffer();
                    if (addToTable) {
                        this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
                    }
                    try {
                        this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
                        continue block47;
                    }
                    catch (SAXException e) {
                        throw new FastInfosetException("processCII", e);
                    }
                }
                case 13: {
                    boolean addToTable = (this._b & 0x10) > 0;
                    this._identifier = (this._b & 2) << 6;
                    this._b = this.read();
                    this._identifier |= (this._b & 0xFC) >> 2;
                    this.decodeOctetsOnSeventhBitOfNonIdentifyingStringOnThirdBit(this._b);
                    this.processCIIEncodingAlgorithm(addToTable);
                    continue block47;
                }
                case 14: {
                    int index = this._b & 0xF;
                    try {
                        this._contentHandler.characters(this._characterContentChunkTable._array, this._characterContentChunkTable._offset[index], this._characterContentChunkTable._length[index]);
                        continue block47;
                    }
                    catch (SAXException e) {
                        throw new FastInfosetException("processCII", e);
                    }
                }
                case 15: {
                    int index = ((this._b & 3) << 8 | this.read()) + 16;
                    try {
                        this._contentHandler.characters(this._characterContentChunkTable._array, this._characterContentChunkTable._offset[index], this._characterContentChunkTable._length[index]);
                        continue block47;
                    }
                    catch (SAXException e) {
                        throw new FastInfosetException("processCII", e);
                    }
                }
                case 16: {
                    int index = ((this._b & 3) << 16 | this.read() << 8 | this.read()) + 1040;
                    try {
                        this._contentHandler.characters(this._characterContentChunkTable._array, this._characterContentChunkTable._offset[index], this._characterContentChunkTable._length[index]);
                        continue block47;
                    }
                    catch (SAXException e) {
                        throw new FastInfosetException("processCII", e);
                    }
                }
                case 17: {
                    int index = (this.read() << 16 | this.read() << 8 | this.read()) + 263184;
                    try {
                        this._contentHandler.characters(this._characterContentChunkTable._array, this._characterContentChunkTable._offset[index], this._characterContentChunkTable._length[index]);
                        continue block47;
                    }
                    catch (SAXException e) {
                        throw new FastInfosetException("processCII", e);
                    }
                }
                case 18: {
                    this.processCommentII();
                    continue block47;
                }
                case 19: {
                    this.processProcessingII();
                    continue block47;
                }
                case 21: {
                    String entity_reference_name = this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherNCName);
                    String system_identifier = (this._b & 2) > 0 ? this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : "";
                    String public_identifier = (this._b & 1) > 0 ? this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherURI) : "";
                    try {
                        this._contentHandler.skippedEntity(entity_reference_name);
                        continue block47;
                    }
                    catch (SAXException e) {
                        throw new FastInfosetException("processUnexpandedEntityReferenceII", e);
                    }
                }
                case 23: {
                    this._doubleTerminate = true;
                }
                case 22: {
                    this._terminate = true;
                    continue block47;
                }
            }
            throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingEII"));
        }
        this._terminate = this._doubleTerminate;
        this._doubleTerminate = false;
        try {
            this._contentHandler.endElement(name.namespaceName, name.localName, name.qName);
        }
        catch (SAXException e) {
            throw new FastInfosetException("processEII", e);
        }
    }

    private final void processUtf8CharacterString() throws FastInfosetException, IOException {
        if ((this._b & 0x10) > 0) {
            this._characterContentChunkTable.ensureSize(this._octetBufferLength);
            int charactersOffset = this._characterContentChunkTable._arrayIndex;
            this.decodeUtf8StringAsCharBuffer(this._characterContentChunkTable._array, charactersOffset);
            this._characterContentChunkTable.add(this._charBufferLength);
            try {
                this._contentHandler.characters(this._characterContentChunkTable._array, charactersOffset, this._charBufferLength);
            }
            catch (SAXException e) {
                throw new FastInfosetException("processCII", e);
            }
        }
        this.decodeUtf8StringAsCharBuffer();
        try {
            this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
        }
        catch (SAXException e) {
            throw new FastInfosetException("processCII", e);
        }
    }

    protected final void processEIIWithNamespaces() throws FastInfosetException, IOException {
        boolean hasAttributes = (this._b & 0x40) > 0;
        boolean bl = this._clearAttributes = this._namespacePrefixesFeature;
        if (++this._prefixTable._declarationId == Integer.MAX_VALUE) {
            this._prefixTable.clearDeclarationIds();
        }
        String prefix = "";
        String namespaceName = "";
        int start = this._namespacePrefixesIndex;
        int b = this.read();
        while ((b & 0xFC) == 204) {
            if (this._namespacePrefixesIndex == this._namespacePrefixes.length) {
                int[] namespaceAIIs = new int[this._namespacePrefixesIndex * 3 / 2 + 1];
                System.arraycopy(this._namespacePrefixes, 0, namespaceAIIs, 0, this._namespacePrefixesIndex);
                this._namespacePrefixes = namespaceAIIs;
            }
            switch (b & 3) {
                case 0: {
                    namespaceName = "";
                    prefix = "";
                    this._namespacePrefixes[this._namespacePrefixesIndex++] = -1;
                    this._prefixIndex = -1;
                    this._namespaceNameIndex = -1;
                    break;
                }
                case 1: {
                    prefix = "";
                    namespaceName = this.decodeIdentifyingNonEmptyStringOnFirstBitAsNamespaceName(false);
                    this._namespacePrefixes[this._namespacePrefixesIndex++] = -1;
                    this._prefixIndex = -1;
                    break;
                }
                case 2: {
                    prefix = this.decodeIdentifyingNonEmptyStringOnFirstBitAsPrefix(false);
                    namespaceName = "";
                    this._namespaceNameIndex = -1;
                    this._namespacePrefixes[this._namespacePrefixesIndex++] = this._prefixIndex;
                    break;
                }
                case 3: {
                    prefix = this.decodeIdentifyingNonEmptyStringOnFirstBitAsPrefix(true);
                    namespaceName = this.decodeIdentifyingNonEmptyStringOnFirstBitAsNamespaceName(true);
                    this._namespacePrefixes[this._namespacePrefixesIndex++] = this._prefixIndex;
                }
            }
            this._prefixTable.pushScope(this._prefixIndex, this._namespaceNameIndex);
            if (this._namespacePrefixesFeature) {
                if (prefix != "") {
                    this._attributes.addAttribute(new QualifiedName("xmlns", "http://www.w3.org/2000/xmlns/", prefix), namespaceName);
                } else {
                    this._attributes.addAttribute(EncodingConstants.DEFAULT_NAMESPACE_DECLARATION, namespaceName);
                }
            }
            try {
                this._contentHandler.startPrefixMapping(prefix, namespaceName);
            }
            catch (SAXException e) {
                throw new IOException("processStartNamespaceAII");
            }
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
                QualifiedName qn = this.decodeLiteralQualifiedName(this._b & 3, this._elementNameTable.getNext());
                this._elementNameTable.add(qn);
                this.processEII(qn, hasAttributes);
                break;
            }
            default: {
                throw new IOException(CommonResourceBundle.getInstance().getString("message.IllegalStateDecodingEIIAfterAIIs"));
            }
        }
        try {
            for (int i = end - 1; i >= start; --i) {
                int prefixIndex = this._namespacePrefixes[i];
                this._prefixTable.popScope(prefixIndex);
                prefix = prefixIndex > 0 ? this._prefixTable.get(prefixIndex - 1) : (prefixIndex == -1 ? "" : "xml");
                this._contentHandler.endPrefixMapping(prefix);
            }
            this._namespacePrefixesIndex = start;
        }
        catch (SAXException e) {
            throw new IOException("processStartNamespaceAII");
        }
    }

    protected final void processAIIs() throws FastInfosetException, IOException {
        this._clearAttributes = true;
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
                    name = this.decodeLiteralQualifiedName(b & 3, this._attributeNameTable.getNext());
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
                if (this._addToTable) {
                    this._v.otherString.add(new CharArray(this._charBuffer, 0, this._charBufferLength, true));
                }
                try {
                    this._lexicalHandler.comment(this._charBuffer, 0, this._charBufferLength);
                    break;
                }
                catch (SAXException e) {
                    throw new FastInfosetException("processCommentII", e);
                }
            }
            case 2: {
                throw new IOException(CommonResourceBundle.getInstance().getString("message.commentIIAlgorithmNotSupported"));
            }
            case 1: {
                CharArray ca = this._v.otherString.get(this._integer);
                try {
                    this._lexicalHandler.comment(ca.ch, ca.start, ca.length);
                    break;
                }
                catch (SAXException e) {
                    throw new FastInfosetException("processCommentII", e);
                }
            }
            case 3: {
                try {
                    this._lexicalHandler.comment(this._charBuffer, 0, 0);
                    break;
                }
                catch (SAXException e) {
                    throw new FastInfosetException("processCommentII", e);
                }
            }
        }
    }

    protected final void processProcessingII() throws FastInfosetException, IOException {
        String target = this.decodeIdentifyingNonEmptyStringOnFirstBit(this._v.otherNCName);
        switch (this.decodeNonIdentifyingStringOnFirstBit()) {
            case 0: {
                String data = new String(this._charBuffer, 0, this._charBufferLength);
                if (this._addToTable) {
                    this._v.otherString.add(new CharArrayString(data));
                }
                try {
                    this._contentHandler.processingInstruction(target, data);
                    break;
                }
                catch (SAXException e) {
                    throw new FastInfosetException("processProcessingII", e);
                }
            }
            case 2: {
                throw new IOException(CommonResourceBundle.getInstance().getString("message.processingIIWithEncodingAlgorithm"));
            }
            case 1: {
                try {
                    this._contentHandler.processingInstruction(target, this._v.otherString.get(this._integer).toString());
                    break;
                }
                catch (SAXException e) {
                    throw new FastInfosetException("processProcessingII", e);
                }
            }
            case 3: {
                try {
                    this._contentHandler.processingInstruction(target, "");
                    break;
                }
                catch (SAXException e) {
                    throw new FastInfosetException("processProcessingII", e);
                }
            }
        }
    }

    protected final void processCIIEncodingAlgorithm(boolean addToTable) throws FastInfosetException, IOException {
        if (this._identifier < 9) {
            StringBuffer buffer;
            if (this._primitiveHandler != null) {
                this.processCIIBuiltInEncodingAlgorithmAsPrimitive();
            } else {
                if (this._algorithmHandler != null) {
                    Object array = this.processBuiltInEncodingAlgorithmAsObject();
                    try {
                        this._algorithmHandler.object(null, this._identifier, array);
                    }
                    catch (SAXException e) {
                        throw new FastInfosetException(e);
                    }
                }
                buffer = new StringBuffer();
                this.processBuiltInEncodingAlgorithmAsCharacters(buffer);
                try {
                    this._contentHandler.characters(buffer.toString().toCharArray(), 0, buffer.length());
                }
                catch (SAXException e) {
                    throw new FastInfosetException(e);
                }
            }
            if (addToTable) {
                buffer = new StringBuffer();
                this.processBuiltInEncodingAlgorithmAsCharacters(buffer);
                this._characterContentChunkTable.add(buffer.toString().toCharArray(), buffer.length());
            }
        } else if (this._identifier == 9) {
            this._octetBufferOffset -= this._octetBufferLength;
            this.decodeUtf8StringIntoCharBuffer();
            try {
                this._lexicalHandler.startCDATA();
                this._contentHandler.characters(this._charBuffer, 0, this._charBufferLength);
                this._lexicalHandler.endCDATA();
            }
            catch (SAXException e) {
                throw new FastInfosetException(e);
            }
            if (addToTable) {
                this._characterContentChunkTable.add(this._charBuffer, this._charBufferLength);
            }
        } else if (this._identifier >= 32 && this._algorithmHandler != null) {
            String URI2 = this._v.encodingAlgorithm.get(this._identifier - 32);
            if (URI2 == null) {
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.URINotPresent", new Object[]{this._identifier}));
            }
            EncodingAlgorithm ea = (EncodingAlgorithm)this._registeredEncodingAlgorithms.get(URI2);
            if (ea != null) {
                Object data = ea.decodeFromBytes(this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
                try {
                    this._algorithmHandler.object(URI2, this._identifier, data);
                }
                catch (SAXException e) {
                    throw new FastInfosetException(e);
                }
            }
            try {
                this._algorithmHandler.octets(URI2, this._identifier, this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
            }
            catch (SAXException e) {
                throw new FastInfosetException(e);
            }
            if (addToTable) {
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.addToTableNotSupported"));
            }
        } else {
            if (this._identifier >= 32) {
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.algorithmDataCannotBeReported"));
            }
            throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.identifiers10to31Reserved"));
        }
    }

    protected final void processCIIBuiltInEncodingAlgorithmAsPrimitive() throws FastInfosetException, IOException {
        try {
            switch (this._identifier) {
                case 0: 
                case 1: {
                    this._primitiveHandler.bytes(this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
                    break;
                }
                case 2: {
                    int length = BuiltInEncodingAlgorithmFactory.shortEncodingAlgorithm.getPrimtiveLengthFromOctetLength(this._octetBufferLength);
                    if (length > this.builtInAlgorithmState.shortArray.length) {
                        short[] array = new short[length * 3 / 2 + 1];
                        System.arraycopy(this.builtInAlgorithmState.shortArray, 0, array, 0, this.builtInAlgorithmState.shortArray.length);
                        this.builtInAlgorithmState.shortArray = array;
                    }
                    BuiltInEncodingAlgorithmFactory.shortEncodingAlgorithm.decodeFromBytesToShortArray(this.builtInAlgorithmState.shortArray, 0, this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
                    this._primitiveHandler.shorts(this.builtInAlgorithmState.shortArray, 0, length);
                    break;
                }
                case 3: {
                    int length = BuiltInEncodingAlgorithmFactory.intEncodingAlgorithm.getPrimtiveLengthFromOctetLength(this._octetBufferLength);
                    if (length > this.builtInAlgorithmState.intArray.length) {
                        int[] array = new int[length * 3 / 2 + 1];
                        System.arraycopy(this.builtInAlgorithmState.intArray, 0, array, 0, this.builtInAlgorithmState.intArray.length);
                        this.builtInAlgorithmState.intArray = array;
                    }
                    BuiltInEncodingAlgorithmFactory.intEncodingAlgorithm.decodeFromBytesToIntArray(this.builtInAlgorithmState.intArray, 0, this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
                    this._primitiveHandler.ints(this.builtInAlgorithmState.intArray, 0, length);
                    break;
                }
                case 4: {
                    int length = BuiltInEncodingAlgorithmFactory.longEncodingAlgorithm.getPrimtiveLengthFromOctetLength(this._octetBufferLength);
                    if (length > this.builtInAlgorithmState.longArray.length) {
                        long[] array = new long[length * 3 / 2 + 1];
                        System.arraycopy(this.builtInAlgorithmState.longArray, 0, array, 0, this.builtInAlgorithmState.longArray.length);
                        this.builtInAlgorithmState.longArray = array;
                    }
                    BuiltInEncodingAlgorithmFactory.longEncodingAlgorithm.decodeFromBytesToLongArray(this.builtInAlgorithmState.longArray, 0, this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
                    this._primitiveHandler.longs(this.builtInAlgorithmState.longArray, 0, length);
                    break;
                }
                case 5: {
                    int length = BuiltInEncodingAlgorithmFactory.booleanEncodingAlgorithm.getPrimtiveLengthFromOctetLength(this._octetBufferLength, this._octetBuffer[this._octetBufferStart] & 0xFF);
                    if (length > this.builtInAlgorithmState.booleanArray.length) {
                        boolean[] array = new boolean[length * 3 / 2 + 1];
                        System.arraycopy(this.builtInAlgorithmState.booleanArray, 0, array, 0, this.builtInAlgorithmState.booleanArray.length);
                        this.builtInAlgorithmState.booleanArray = array;
                    }
                    BuiltInEncodingAlgorithmFactory.booleanEncodingAlgorithm.decodeFromBytesToBooleanArray(this.builtInAlgorithmState.booleanArray, 0, length, this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
                    this._primitiveHandler.booleans(this.builtInAlgorithmState.booleanArray, 0, length);
                    break;
                }
                case 6: {
                    int length = BuiltInEncodingAlgorithmFactory.floatEncodingAlgorithm.getPrimtiveLengthFromOctetLength(this._octetBufferLength);
                    if (length > this.builtInAlgorithmState.floatArray.length) {
                        float[] array = new float[length * 3 / 2 + 1];
                        System.arraycopy(this.builtInAlgorithmState.floatArray, 0, array, 0, this.builtInAlgorithmState.floatArray.length);
                        this.builtInAlgorithmState.floatArray = array;
                    }
                    BuiltInEncodingAlgorithmFactory.floatEncodingAlgorithm.decodeFromBytesToFloatArray(this.builtInAlgorithmState.floatArray, 0, this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
                    this._primitiveHandler.floats(this.builtInAlgorithmState.floatArray, 0, length);
                    break;
                }
                case 7: {
                    int length = BuiltInEncodingAlgorithmFactory.doubleEncodingAlgorithm.getPrimtiveLengthFromOctetLength(this._octetBufferLength);
                    if (length > this.builtInAlgorithmState.doubleArray.length) {
                        double[] array = new double[length * 3 / 2 + 1];
                        System.arraycopy(this.builtInAlgorithmState.doubleArray, 0, array, 0, this.builtInAlgorithmState.doubleArray.length);
                        this.builtInAlgorithmState.doubleArray = array;
                    }
                    BuiltInEncodingAlgorithmFactory.doubleEncodingAlgorithm.decodeFromBytesToDoubleArray(this.builtInAlgorithmState.doubleArray, 0, this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
                    this._primitiveHandler.doubles(this.builtInAlgorithmState.doubleArray, 0, length);
                    break;
                }
                case 8: {
                    int length = BuiltInEncodingAlgorithmFactory.uuidEncodingAlgorithm.getPrimtiveLengthFromOctetLength(this._octetBufferLength);
                    if (length > this.builtInAlgorithmState.longArray.length) {
                        long[] array = new long[length * 3 / 2 + 1];
                        System.arraycopy(this.builtInAlgorithmState.longArray, 0, array, 0, this.builtInAlgorithmState.longArray.length);
                        this.builtInAlgorithmState.longArray = array;
                    }
                    BuiltInEncodingAlgorithmFactory.uuidEncodingAlgorithm.decodeFromBytesToLongArray(this.builtInAlgorithmState.longArray, 0, this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
                    this._primitiveHandler.uuids(this.builtInAlgorithmState.longArray, 0, length);
                    break;
                }
                case 9: {
                    throw new UnsupportedOperationException("CDATA");
                }
                default: {
                    throw new FastInfosetException(CommonResourceBundle.getInstance().getString("message.unsupportedAlgorithm", new Object[]{this._identifier}));
                }
            }
        }
        catch (SAXException e) {
            throw new FastInfosetException(e);
        }
    }

    protected final void processAIIEncodingAlgorithm(QualifiedName name, boolean addToTable) throws FastInfosetException, IOException {
        if (this._identifier < 9) {
            if (this._primitiveHandler != null || this._algorithmHandler != null) {
                Object data = this.processBuiltInEncodingAlgorithmAsObject();
                this._attributes.addAttributeWithAlgorithmData(name, null, this._identifier, data);
            } else {
                StringBuffer buffer = new StringBuffer();
                this.processBuiltInEncodingAlgorithmAsCharacters(buffer);
                this._attributes.addAttribute(name, buffer.toString());
            }
        } else if (this._identifier >= 32 && this._algorithmHandler != null) {
            String URI2 = this._v.encodingAlgorithm.get(this._identifier - 32);
            if (URI2 == null) {
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.URINotPresent", new Object[]{this._identifier}));
            }
            EncodingAlgorithm ea = (EncodingAlgorithm)this._registeredEncodingAlgorithms.get(URI2);
            if (ea != null) {
                Object data = ea.decodeFromBytes(this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
                this._attributes.addAttributeWithAlgorithmData(name, URI2, this._identifier, data);
            } else {
                byte[] data = new byte[this._octetBufferLength];
                System.arraycopy(this._octetBuffer, this._octetBufferStart, data, 0, this._octetBufferLength);
                this._attributes.addAttributeWithAlgorithmData(name, URI2, this._identifier, data);
            }
        } else {
            if (this._identifier >= 32) {
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.algorithmDataCannotBeReported"));
            }
            if (this._identifier == 9) {
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.CDATAAlgorithmNotSupported"));
            }
            throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.identifiers10to31Reserved"));
        }
        if (addToTable) {
            this._attributeValueTable.add(this._attributes.getValue(this._attributes.getIndex(name.qName)));
        }
    }

    protected final void processBuiltInEncodingAlgorithmAsCharacters(StringBuffer buffer) throws FastInfosetException, IOException {
        Object array = BuiltInEncodingAlgorithmFactory.getAlgorithm(this._identifier).decodeFromBytes(this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
        BuiltInEncodingAlgorithmFactory.getAlgorithm(this._identifier).convertToCharacters(array, buffer);
    }

    protected final Object processBuiltInEncodingAlgorithmAsObject() throws FastInfosetException, IOException {
        return BuiltInEncodingAlgorithmFactory.getAlgorithm(this._identifier).decodeFromBytes(this._octetBuffer, this._octetBufferStart, this._octetBufferLength);
    }

    private static final class DeclHandlerImpl
    implements DeclHandler {
        private DeclHandlerImpl() {
        }

        @Override
        public void elementDecl(String name, String model) throws SAXException {
        }

        @Override
        public void attributeDecl(String eName, String aName, String type, String mode, String value) throws SAXException {
        }

        @Override
        public void internalEntityDecl(String name, String value) throws SAXException {
        }

        @Override
        public void externalEntityDecl(String name, String publicId, String systemId) throws SAXException {
        }
    }

    private static final class LexicalHandlerImpl
    implements LexicalHandler {
        private LexicalHandlerImpl() {
        }

        @Override
        public void comment(char[] ch, int start, int end) {
        }

        @Override
        public void startDTD(String name, String publicId, String systemId) {
        }

        @Override
        public void endDTD() {
        }

        @Override
        public void startEntity(String name) {
        }

        @Override
        public void endEntity(String name) {
        }

        @Override
        public void startCDATA() {
        }

        @Override
        public void endCDATA() {
        }
    }
}

