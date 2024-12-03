/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.dataformat.cbor;

import com.fasterxml.jackson.core.Base64Variant;
import com.fasterxml.jackson.core.FormatFeature;
import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.StreamReadCapability;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.core.base.ParserMinimalBase;
import com.fasterxml.jackson.core.io.IOContext;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.core.json.DupDetector;
import com.fasterxml.jackson.core.sym.ByteQuadsCanonicalizer;
import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.fasterxml.jackson.core.util.JacksonFeatureSet;
import com.fasterxml.jackson.core.util.TextBuffer;
import com.fasterxml.jackson.dataformat.cbor.CBORConstants;
import com.fasterxml.jackson.dataformat.cbor.CBORReadContext;
import com.fasterxml.jackson.dataformat.cbor.PackageVersion;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class CBORParser
extends ParserMinimalBase {
    private static final Charset UTF8 = StandardCharsets.UTF_8;
    private static final int[] UTF8_UNIT_CODES = CBORConstants.sUtf8UnitLengths;
    private static final double MATH_POW_2_10 = Math.pow(2.0, 10.0);
    private static final double MATH_POW_2_NEG14 = Math.pow(2.0, -14.0);
    protected static final int LONGEST_NON_CHUNKED_BINARY = 250000;
    protected ObjectCodec _objectCodec;
    protected final IOContext _ioContext;
    protected boolean _closed;
    protected int _inputPtr = 0;
    protected int _inputEnd = 0;
    protected long _currInputProcessed = 0L;
    protected int _currInputRow = 1;
    protected int _currInputRowStart = 0;
    protected long _tokenInputTotal = 0L;
    protected int _tokenInputRow = 1;
    protected int _tokenInputCol = 0;
    protected CBORReadContext _streamReadContext;
    protected final TextBuffer _textBuffer;
    protected char[] _nameCopyBuffer = null;
    protected boolean _nameCopied = false;
    protected ByteArrayBuilder _byteArrayBuilder = null;
    protected byte[] _binaryValue;
    private int _chunkLeft;
    private int _chunkEnd;
    protected int _tagValue = -1;
    protected boolean _tokenIncomplete = false;
    protected int _typeByte;
    protected InputStream _inputStream;
    protected byte[] _inputBuffer;
    protected boolean _bufferRecyclable;
    protected final ByteQuadsCanonicalizer _symbols;
    protected int[] _quadBuffer = NO_INTS;
    protected int _quad1;
    protected int _quad2;
    protected int _quad3;
    protected final boolean _symbolsCanonical;
    static final BigInteger BI_MIN_INT = BigInteger.valueOf(Integer.MIN_VALUE);
    static final BigInteger BI_MAX_INT = BigInteger.valueOf(Integer.MAX_VALUE);
    static final BigInteger BI_MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);
    static final BigInteger BI_MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);
    static final BigDecimal BD_MIN_LONG = new BigDecimal(BI_MIN_LONG);
    static final BigDecimal BD_MAX_LONG = new BigDecimal(BI_MAX_LONG);
    static final BigDecimal BD_MIN_INT = new BigDecimal(BI_MIN_INT);
    static final BigDecimal BD_MAX_INT = new BigDecimal(BI_MAX_INT);
    protected int _numTypesValid = 0;
    protected int _numberInt;
    protected long _numberLong;
    protected float _numberFloat;
    protected double _numberDouble;
    protected BigInteger _numberBigInt;
    protected BigDecimal _numberBigDecimal;
    private static final BigInteger BIT_63 = BigInteger.ONE.shiftLeft(63);

    public CBORParser(IOContext ctxt, int parserFeatures, int cborFeatures, ObjectCodec codec, ByteQuadsCanonicalizer sym, InputStream in, byte[] inputBuffer, int start, int end, boolean bufferRecyclable) {
        super(parserFeatures);
        this._ioContext = ctxt;
        this._objectCodec = codec;
        this._symbols = sym;
        this._symbolsCanonical = sym.isCanonicalizing();
        this._inputStream = in;
        this._inputBuffer = inputBuffer;
        this._inputPtr = start;
        this._inputEnd = end;
        this._bufferRecyclable = bufferRecyclable;
        this._textBuffer = ctxt.constructTextBuffer();
        DupDetector dups = JsonParser.Feature.STRICT_DUPLICATE_DETECTION.enabledIn(parserFeatures) ? DupDetector.rootDetector(this) : null;
        this._streamReadContext = CBORReadContext.createRootContext(dups);
        this._tokenInputRow = -1;
        this._tokenInputCol = -1;
    }

    @Override
    public ObjectCodec getCodec() {
        return this._objectCodec;
    }

    @Override
    public void setCodec(ObjectCodec c) {
        this._objectCodec = c;
    }

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    @Override
    public int getFormatFeatures() {
        return 0;
    }

    @Override
    public JacksonFeatureSet<StreamReadCapability> getReadCapabilities() {
        return DEFAULT_READ_CAPABILITIES;
    }

    public int getCurrentTag() {
        return this._tagValue;
    }

    @Override
    public int releaseBuffered(OutputStream out) throws IOException {
        int count = this._inputEnd - this._inputPtr;
        if (count < 1) {
            return 0;
        }
        int origPtr = this._inputPtr;
        out.write(this._inputBuffer, origPtr, count);
        return count;
    }

    @Override
    public Object getInputSource() {
        return this._inputStream;
    }

    @Override
    public JsonLocation getTokenLocation() {
        return new JsonLocation(this._ioContext.contentReference(), this._tokenInputTotal, -1L, -1, (int)this._tokenInputTotal);
    }

    @Override
    public JsonLocation getCurrentLocation() {
        long offset = this._currInputProcessed + (long)this._inputPtr;
        return new JsonLocation(this._ioContext.contentReference(), offset, -1L, -1, (int)offset);
    }

    @Override
    public String getCurrentName() throws IOException {
        if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
            CBORReadContext parent = this._streamReadContext.getParent();
            return parent.getCurrentName();
        }
        return this._streamReadContext.getCurrentName();
    }

    @Override
    public void overrideCurrentName(String name) {
        CBORReadContext ctxt = this._streamReadContext;
        if (this._currToken == JsonToken.START_OBJECT || this._currToken == JsonToken.START_ARRAY) {
            ctxt = ctxt.getParent();
        }
        try {
            ctxt.setCurrentName(name);
        }
        catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public void close() throws IOException {
        if (!this._closed) {
            this._closed = true;
            this._symbols.release();
            try {
                this._closeInput();
            }
            finally {
                this._releaseBuffers();
            }
        }
    }

    @Override
    public boolean isClosed() {
        return this._closed;
    }

    @Override
    public CBORReadContext getParsingContext() {
        return this._streamReadContext;
    }

    @Override
    public boolean hasTextCharacters() {
        if (this._currToken == JsonToken.VALUE_STRING) {
            return this._textBuffer.hasTextAsCharacters();
        }
        if (this._currToken == JsonToken.FIELD_NAME) {
            return this._nameCopied;
        }
        return false;
    }

    protected void _releaseBuffers() throws IOException {
        Object[] buf;
        if (this._bufferRecyclable && (buf = this._inputBuffer) != null) {
            this._inputBuffer = null;
            this._ioContext.releaseReadIOBuffer((byte[])buf);
        }
        this._textBuffer.releaseBuffers();
        buf = this._nameCopyBuffer;
        if (buf != null) {
            this._nameCopyBuffer = null;
            this._ioContext.releaseNameCopyBuffer((char[])buf);
        }
    }

    @Override
    public JsonToken nextToken() throws IOException {
        this._numTypesValid = 0;
        if (this._tokenIncomplete) {
            this._skipIncomplete();
        }
        this._tokenInputTotal = this._currInputProcessed + (long)this._inputPtr;
        this._binaryValue = null;
        if (this._streamReadContext.inObject()) {
            if (this._currToken != JsonToken.FIELD_NAME) {
                this._tagValue = -1;
                if (!this._streamReadContext.expectMoreValues()) {
                    this._streamReadContext = this._streamReadContext.getParent();
                    this._currToken = JsonToken.END_OBJECT;
                    return this._currToken;
                }
                this._currToken = this._decodePropertyName();
                return this._currToken;
            }
        } else if (!this._streamReadContext.expectMoreValues()) {
            this._tagValue = -1;
            this._streamReadContext = this._streamReadContext.getParent();
            this._currToken = JsonToken.END_ARRAY;
            return this._currToken;
        }
        if (this._inputPtr >= this._inputEnd && !this.loadMore()) {
            return this._eofAsNextToken();
        }
        int ch = this._inputBuffer[this._inputPtr++] & 0xFF;
        int type = ch >> 5;
        int lowBits = ch & 0x1F;
        if (type == 6) {
            this._tagValue = this._decodeTag(lowBits);
            if (this._inputPtr >= this._inputEnd && !this.loadMore()) {
                return this._eofAsNextToken();
            }
            ch = this._inputBuffer[this._inputPtr++] & 0xFF;
            type = ch >> 5;
            lowBits = ch & 0x1F;
        } else {
            this._tagValue = -1;
        }
        switch (type) {
            case 0: {
                this._numTypesValid = 1;
                if (lowBits <= 23) {
                    this._numberInt = lowBits;
                } else {
                    switch (lowBits - 24) {
                        case 0: {
                            this._numberInt = this._decode8Bits();
                            break;
                        }
                        case 1: {
                            this._numberInt = this._decode16Bits();
                            break;
                        }
                        case 2: {
                            int v = this._decode32Bits();
                            if (v >= 0) {
                                this._numberInt = v;
                                break;
                            }
                            long l = v;
                            this._numberLong = l & 0xFFFFFFFFL;
                            this._numTypesValid = 2;
                            break;
                        }
                        case 3: {
                            long l = this._decode64Bits();
                            if (l >= 0L) {
                                this._numberLong = l;
                                this._numTypesValid = 2;
                                break;
                            }
                            this._numberBigInt = this._bigPositive(l);
                            this._numTypesValid = 4;
                            break;
                        }
                        default: {
                            this._invalidToken(ch);
                        }
                    }
                }
                this._currToken = JsonToken.VALUE_NUMBER_INT;
                return this._currToken;
            }
            case 1: {
                this._numTypesValid = 1;
                if (lowBits <= 23) {
                    this._numberInt = -lowBits - 1;
                } else {
                    switch (lowBits - 24) {
                        case 0: {
                            this._numberInt = -this._decode8Bits() - 1;
                            break;
                        }
                        case 1: {
                            this._numberInt = -this._decode16Bits() - 1;
                            break;
                        }
                        case 2: {
                            int v = this._decode32Bits();
                            if (v < 0) {
                                long unsignedBase = (long)v & 0xFFFFFFFFL;
                                this._numberLong = -unsignedBase - 1L;
                                this._numTypesValid = 2;
                                break;
                            }
                            this._numberInt = -v - 1;
                            break;
                        }
                        case 3: {
                            long l = this._decode64Bits();
                            if (l >= 0L) {
                                this._numberLong = -l - 1L;
                                this._numTypesValid = 2;
                                break;
                            }
                            this._numberBigInt = this._bigNegative(l);
                            this._numTypesValid = 4;
                            break;
                        }
                        default: {
                            this._invalidToken(ch);
                        }
                    }
                }
                this._currToken = JsonToken.VALUE_NUMBER_INT;
                return this._currToken;
            }
            case 2: {
                this._typeByte = ch;
                this._tokenIncomplete = true;
                if (this._tagValue >= 0) {
                    return this._handleTaggedBinary(this._tagValue);
                }
                this._currToken = JsonToken.VALUE_EMBEDDED_OBJECT;
                return this._currToken;
            }
            case 3: {
                this._typeByte = ch;
                this._tokenIncomplete = true;
                this._currToken = JsonToken.VALUE_STRING;
                return this._currToken;
            }
            case 4: {
                int len = this._decodeExplicitLength(lowBits);
                if (this._tagValue >= 0) {
                    return this._handleTaggedArray(this._tagValue, len);
                }
                this._streamReadContext = this._streamReadContext.createChildArrayContext(len);
                this._currToken = JsonToken.START_ARRAY;
                return this._currToken;
            }
            case 5: {
                this._currToken = JsonToken.START_OBJECT;
                int len = this._decodeExplicitLength(lowBits);
                this._streamReadContext = this._streamReadContext.createChildObjectContext(len);
                return this._currToken;
            }
            case 6: {
                this._reportError("Multiple tags not allowed per value (first tag: " + this._tagValue + ")");
            }
        }
        switch (lowBits) {
            case 20: {
                this._currToken = JsonToken.VALUE_FALSE;
                return this._currToken;
            }
            case 21: {
                this._currToken = JsonToken.VALUE_TRUE;
                return this._currToken;
            }
            case 22: {
                this._currToken = JsonToken.VALUE_NULL;
                return this._currToken;
            }
            case 23: {
                this._currToken = this._decodeUndefinedValue();
                return this._currToken;
            }
            case 25: {
                this._numberFloat = this._decodeHalfSizeFloat();
                this._numTypesValid = 32;
                this._currToken = JsonToken.VALUE_NUMBER_FLOAT;
                return this._currToken;
            }
            case 26: {
                this._numberFloat = Float.intBitsToFloat(this._decode32Bits());
                this._numTypesValid = 32;
                this._currToken = JsonToken.VALUE_NUMBER_FLOAT;
                return this._currToken;
            }
            case 27: {
                this._numberDouble = Double.longBitsToDouble(this._decode64Bits());
                this._numTypesValid = 8;
                this._currToken = JsonToken.VALUE_NUMBER_FLOAT;
                return this._currToken;
            }
            case 31: {
                if (this._streamReadContext.inArray() && !this._streamReadContext.hasExpectedLength()) {
                    this._streamReadContext = this._streamReadContext.getParent();
                    this._currToken = JsonToken.END_ARRAY;
                    return this._currToken;
                }
                this._reportUnexpectedBreak();
            }
        }
        this._currToken = this._decodeSimpleValue(lowBits, ch);
        return this._currToken;
    }

    protected String _numberToName(int ch, boolean neg) throws IOException {
        int i;
        int lowBits = ch & 0x1F;
        if (lowBits <= 23) {
            i = lowBits;
        } else {
            switch (lowBits) {
                case 24: {
                    i = this._decode8Bits();
                    break;
                }
                case 25: {
                    i = this._decode16Bits();
                    break;
                }
                case 26: {
                    long l;
                    i = this._decode32Bits();
                    if (i >= 0) break;
                    if (neg) {
                        long unsignedBase = (long)i & 0xFFFFFFFFL;
                        l = -unsignedBase - 1L;
                    } else {
                        l = i;
                        l &= 0xFFFFFFFFL;
                    }
                    return String.valueOf(l);
                }
                case 27: {
                    long l = this._decode64Bits();
                    if (neg) {
                        l = -l - 1L;
                    }
                    return String.valueOf(l);
                }
                default: {
                    throw this._constructReadException("Invalid length indicator for ints (%d), token 0x%s", lowBits, Integer.toHexString(ch));
                }
            }
        }
        if (neg) {
            i = -i - 1;
        }
        return String.valueOf(i);
    }

    protected JsonToken _handleTaggedBinary(int tag) throws IOException {
        boolean neg;
        if (tag == 2) {
            neg = false;
        } else if (tag == 3) {
            neg = true;
        } else {
            this._currToken = JsonToken.VALUE_EMBEDDED_OBJECT;
            return this._currToken;
        }
        this._finishToken();
        if (this._binaryValue.length == 0) {
            this._numberBigInt = BigInteger.ZERO;
        } else {
            BigInteger nr = new BigInteger(this._binaryValue);
            if (neg) {
                nr = nr.negate();
            }
            this._numberBigInt = nr;
        }
        this._numTypesValid = 4;
        this._tagValue = -1;
        this._currToken = JsonToken.VALUE_NUMBER_INT;
        return this._currToken;
    }

    protected JsonToken _handleTaggedArray(int tag, int len) throws IOException {
        JsonParser.NumberType numberType;
        this._streamReadContext = this._streamReadContext.createChildArrayContext(len);
        if (tag != 4) {
            this._currToken = JsonToken.START_ARRAY;
            return this._currToken;
        }
        this._currToken = JsonToken.START_ARRAY;
        if (len != 2) {
            this._reportError("Unexpected array size (" + len + ") for tagged 'bigfloat' value; should have exactly 2 number elements");
        }
        if (!this._checkNextIsIntInArray("bigfloat")) {
            this._reportError("Unexpected token (" + (Object)((Object)this.currentToken()) + ") as the first part of 'bigfloat' value: should get VALUE_NUMBER_INT");
        }
        int exp = -this.getIntValue();
        if (!this._checkNextIsIntInArray("bigfloat")) {
            this._reportError("Unexpected token (" + (Object)((Object)this.currentToken()) + ") as the second part of 'bigfloat' value: should get VALUE_NUMBER_INT");
        }
        BigDecimal dec = (numberType = this.getNumberType()) == JsonParser.NumberType.BIG_INTEGER ? new BigDecimal(this.getBigIntegerValue(), exp) : BigDecimal.valueOf(this.getLongValue(), exp);
        if (!this._checkNextIsEndArray()) {
            this._reportError("Unexpected token (" + (Object)((Object)this.currentToken()) + ") after 2 elements of 'bigfloat' value");
        }
        this._numberBigDecimal = dec;
        this._numTypesValid = 16;
        this._currToken = JsonToken.VALUE_NUMBER_FLOAT;
        return this._currToken;
    }

    protected final boolean _checkNextIsIntInArray(String typeDesc) throws IOException {
        if (!this._streamReadContext.expectMoreValues()) {
            this._tagValue = -1;
            this._streamReadContext = this._streamReadContext.getParent();
            this._currToken = JsonToken.END_ARRAY;
            return false;
        }
        if (this._inputPtr >= this._inputEnd && !this.loadMore()) {
            this._eofAsNextToken();
            return false;
        }
        int ch = this._inputBuffer[this._inputPtr++] & 0xFF;
        int type = ch >> 5;
        int lowBits = ch & 0x1F;
        int tagValue = -1;
        if (type == 6) {
            tagValue = this._decodeTag(lowBits);
            if (this._inputPtr >= this._inputEnd && !this.loadMore()) {
                this._eofAsNextToken();
                return false;
            }
            ch = this._inputBuffer[this._inputPtr++] & 0xFF;
            type = ch >> 5;
            lowBits = ch & 0x1F;
        }
        switch (type) {
            case 0: {
                this._numTypesValid = 1;
                if (lowBits <= 23) {
                    this._numberInt = lowBits;
                } else {
                    switch (lowBits - 24) {
                        case 0: {
                            this._numberInt = this._decode8Bits();
                            break;
                        }
                        case 1: {
                            this._numberInt = this._decode16Bits();
                            break;
                        }
                        case 2: {
                            int v = this._decode32Bits();
                            if (v >= 0) {
                                this._numberInt = v;
                                break;
                            }
                            long l = v;
                            this._numberLong = l & 0xFFFFFFFFL;
                            this._numTypesValid = 2;
                            break;
                        }
                        case 3: {
                            long l = this._decode64Bits();
                            if (l >= 0L) {
                                this._numberLong = l;
                                this._numTypesValid = 2;
                                break;
                            }
                            this._numberBigInt = this._bigPositive(l);
                            this._numTypesValid = 4;
                            break;
                        }
                        default: {
                            this._invalidToken(ch);
                        }
                    }
                }
                this._currToken = JsonToken.VALUE_NUMBER_INT;
                return true;
            }
            case 1: {
                this._numTypesValid = 1;
                if (lowBits <= 23) {
                    this._numberInt = -lowBits - 1;
                } else {
                    switch (lowBits - 24) {
                        case 0: {
                            this._numberInt = -this._decode8Bits() - 1;
                            break;
                        }
                        case 1: {
                            this._numberInt = -this._decode16Bits() - 1;
                            break;
                        }
                        case 2: {
                            int v = this._decode32Bits();
                            if (v < 0) {
                                long unsignedBase = (long)v & 0xFFFFFFFFL;
                                this._numberLong = -unsignedBase - 1L;
                                this._numTypesValid = 2;
                                break;
                            }
                            this._numberInt = -v - 1;
                            break;
                        }
                        case 3: {
                            long l = this._decode64Bits();
                            if (l >= 0L) {
                                this._numberLong = -l - 1L;
                                this._numTypesValid = 2;
                                break;
                            }
                            this._numberBigInt = this._bigNegative(l);
                            this._numTypesValid = 4;
                            break;
                        }
                        default: {
                            this._invalidToken(ch);
                        }
                    }
                }
                this._currToken = JsonToken.VALUE_NUMBER_INT;
                return true;
            }
            case 2: {
                if (tagValue < 0) break;
                this._typeByte = ch;
                this._tokenIncomplete = true;
                this._currToken = this._handleTaggedBinary(tagValue);
                return this._currToken == JsonToken.VALUE_NUMBER_INT;
            }
            case 6: {
                this._reportError("Multiple tags not allowed per value (first tag: " + tagValue + ")");
            }
        }
        --this._inputPtr;
        this.nextToken();
        return false;
    }

    protected final boolean _checkNextIsEndArray() throws IOException {
        if (!this._streamReadContext.expectMoreValues()) {
            this._tagValue = -1;
            this._streamReadContext = this._streamReadContext.getParent();
            this._currToken = JsonToken.END_ARRAY;
            return true;
        }
        byte ch = this._inputBuffer[this._inputPtr++];
        int type = ch >> 5 & 7;
        int tagValue = -1;
        if (type == 6) {
            tagValue = this._decodeTag(ch & 0x1F);
            if (this._inputPtr >= this._inputEnd && !this.loadMore()) {
                this._eofAsNextToken();
                return false;
            }
            if ((type = (ch = this._inputBuffer[this._inputPtr++]) >> 5 & 7) == 6) {
                this._reportError("Multiple tags not allowed per value (first tag: " + tagValue + ")");
            }
        }
        --this._inputPtr;
        return this.nextToken() == JsonToken.END_ARRAY;
    }

    @Override
    public void finishToken() throws IOException {
        if (this._tokenIncomplete) {
            this._finishToken();
        }
    }

    @Override
    public boolean nextFieldName(SerializableString str) throws IOException {
        if (this._streamReadContext.inObject() && this._currToken != JsonToken.FIELD_NAME) {
            int lenMarker;
            byte ch;
            this._numTypesValid = 0;
            if (this._tokenIncomplete) {
                this._skipIncomplete();
            }
            this._tokenInputTotal = this._currInputProcessed + (long)this._inputPtr;
            this._binaryValue = null;
            this._tagValue = -1;
            if (!this._streamReadContext.expectMoreValues()) {
                this._streamReadContext = this._streamReadContext.getParent();
                this._currToken = JsonToken.END_OBJECT;
                return false;
            }
            int ptr = this._inputPtr;
            byte[] nameBytes = str.asQuotedUTF8();
            int byteLen = nameBytes.length;
            if (ptr + byteLen + 1 < this._inputEnd && ((ch = this._inputBuffer[ptr++]) >> 5 & 7) == 3 && (lenMarker = ch & 0x1F) <= 24) {
                if (lenMarker == 23) {
                    lenMarker = this._inputBuffer[ptr++] & 0xFF;
                }
                if (lenMarker == byteLen) {
                    int i = 0;
                    while (true) {
                        if (i == lenMarker) {
                            this._inputPtr = ptr + i;
                            this._streamReadContext.setCurrentName(str.getValue());
                            this._currToken = JsonToken.FIELD_NAME;
                            return true;
                        }
                        if (nameBytes[i] != this._inputBuffer[ptr + i]) break;
                        ++i;
                    }
                }
            }
        }
        return this.nextToken() == JsonToken.FIELD_NAME && str.getValue().equals(this.getCurrentName());
    }

    @Override
    public String nextFieldName() throws IOException {
        if (this._streamReadContext.inObject() && this._currToken != JsonToken.FIELD_NAME) {
            String name;
            byte ch;
            int type;
            this._numTypesValid = 0;
            if (this._tokenIncomplete) {
                this._skipIncomplete();
            }
            this._tokenInputTotal = this._currInputProcessed + (long)this._inputPtr;
            this._binaryValue = null;
            this._tagValue = -1;
            if (!this._streamReadContext.expectMoreValues()) {
                this._streamReadContext = this._streamReadContext.getParent();
                this._currToken = JsonToken.END_OBJECT;
                return null;
            }
            if (this._inputPtr >= this._inputEnd && !this.loadMore()) {
                this._eofAsNextToken();
            }
            if ((type = (ch = this._inputBuffer[this._inputPtr++]) >> 5 & 7) != 3) {
                if (ch == -1) {
                    if (!this._streamReadContext.hasExpectedLength()) {
                        this._streamReadContext = this._streamReadContext.getParent();
                        this._currToken = JsonToken.END_OBJECT;
                        return null;
                    }
                    this._reportUnexpectedBreak();
                }
                this._decodeNonStringName(ch);
                this._currToken = JsonToken.FIELD_NAME;
                return this.getText();
            }
            int lenMarker = ch & 0x1F;
            if (lenMarker <= 23) {
                if (lenMarker == 0) {
                    name = "";
                } else {
                    if (this._inputEnd - this._inputPtr < lenMarker) {
                        this._loadToHaveAtLeast(lenMarker);
                    }
                    if (this._symbolsCanonical) {
                        name = this._findDecodedFromSymbols(lenMarker);
                        if (name != null) {
                            this._inputPtr += lenMarker;
                        } else {
                            name = this._decodeContiguousName(lenMarker);
                            name = this._addDecodedToSymbols(lenMarker, name);
                        }
                    } else {
                        name = this._decodeContiguousName(lenMarker);
                    }
                }
            } else {
                int actualLen = this._decodeExplicitLength(lenMarker);
                name = actualLen < 0 ? this._decodeChunkedName() : this._decodeLongerName(actualLen);
            }
            this._streamReadContext.setCurrentName(name);
            this._currToken = JsonToken.FIELD_NAME;
            return name;
        }
        return this.nextToken() == JsonToken.FIELD_NAME ? this.getCurrentName() : null;
    }

    @Override
    public String nextTextValue() throws IOException {
        this._numTypesValid = 0;
        if (this._tokenIncomplete) {
            this._skipIncomplete();
        }
        this._tokenInputTotal = this._currInputProcessed + (long)this._inputPtr;
        this._binaryValue = null;
        this._tagValue = -1;
        if (this._streamReadContext.inObject()) {
            if (this._currToken != JsonToken.FIELD_NAME) {
                this._tagValue = -1;
                if (!this._streamReadContext.expectMoreValues()) {
                    this._streamReadContext = this._streamReadContext.getParent();
                    this._currToken = JsonToken.END_OBJECT;
                    return null;
                }
                this._currToken = this._decodePropertyName();
                return null;
            }
        } else if (!this._streamReadContext.expectMoreValues()) {
            this._tagValue = -1;
            this._streamReadContext = this._streamReadContext.getParent();
            this._currToken = JsonToken.END_ARRAY;
            return null;
        }
        if (this._inputPtr >= this._inputEnd && !this.loadMore()) {
            this._eofAsNextToken();
            return null;
        }
        int ch = this._inputBuffer[this._inputPtr++] & 0xFF;
        int type = ch >> 5;
        int lowBits = ch & 0x1F;
        if (type == 6) {
            this._tagValue = this._decodeTag(lowBits);
            if (this._inputPtr >= this._inputEnd && !this.loadMore()) {
                this._eofAsNextToken();
                return null;
            }
            ch = this._inputBuffer[this._inputPtr++] & 0xFF;
            type = ch >> 5;
            lowBits = ch & 0x1F;
        } else {
            this._tagValue = -1;
        }
        switch (type) {
            case 0: {
                this._numTypesValid = 1;
                if (lowBits <= 23) {
                    this._numberInt = lowBits;
                } else {
                    switch (lowBits - 24) {
                        case 0: {
                            this._numberInt = this._decode8Bits();
                            break;
                        }
                        case 1: {
                            this._numberInt = this._decode16Bits();
                            break;
                        }
                        case 2: {
                            int v = this._decode32Bits();
                            if (v < 0) {
                                long l = v;
                                this._numberLong = l & 0xFFFFFFFFL;
                                this._numTypesValid = 2;
                                break;
                            }
                            this._numberInt = v;
                            break;
                        }
                        case 3: {
                            long l = this._decode64Bits();
                            if (l >= 0L) {
                                this._numberLong = l;
                                this._numTypesValid = 2;
                                break;
                            }
                            this._numberBigInt = this._bigPositive(l);
                            this._numTypesValid = 4;
                            break;
                        }
                        default: {
                            this._invalidToken(ch);
                        }
                    }
                }
                this._currToken = JsonToken.VALUE_NUMBER_INT;
                return null;
            }
            case 1: {
                this._numTypesValid = 1;
                if (lowBits <= 23) {
                    this._numberInt = -lowBits - 1;
                } else {
                    switch (lowBits - 24) {
                        case 0: {
                            this._numberInt = -this._decode8Bits() - 1;
                            break;
                        }
                        case 1: {
                            this._numberInt = -this._decode16Bits() - 1;
                            break;
                        }
                        case 2: {
                            int v = this._decode32Bits();
                            if (v < 0) {
                                long unsignedBase = (long)v & 0xFFFFFFFFL;
                                this._numberLong = -unsignedBase - 1L;
                                this._numTypesValid = 2;
                                break;
                            }
                            this._numberInt = -v - 1;
                            break;
                        }
                        case 3: {
                            long l = this._decode64Bits();
                            if (l >= 0L) {
                                this._numberLong = l;
                                this._numTypesValid = 2;
                                break;
                            }
                            this._numberBigInt = this._bigNegative(l);
                            this._numTypesValid = 4;
                            break;
                        }
                        default: {
                            this._invalidToken(ch);
                        }
                    }
                }
                this._currToken = JsonToken.VALUE_NUMBER_INT;
                return null;
            }
            case 2: {
                this._typeByte = ch;
                this._tokenIncomplete = true;
                this._currToken = JsonToken.VALUE_EMBEDDED_OBJECT;
                return null;
            }
            case 3: {
                this._typeByte = ch;
                this._tokenIncomplete = true;
                this._currToken = JsonToken.VALUE_STRING;
                return this._finishTextToken(ch);
            }
            case 4: {
                this._currToken = JsonToken.START_ARRAY;
                int len = this._decodeExplicitLength(lowBits);
                this._streamReadContext = this._streamReadContext.createChildArrayContext(len);
                return null;
            }
            case 5: {
                this._currToken = JsonToken.START_OBJECT;
                int len = this._decodeExplicitLength(lowBits);
                this._streamReadContext = this._streamReadContext.createChildObjectContext(len);
                return null;
            }
            case 6: {
                this._reportError("Multiple tags not allowed per value (first tag: " + this._tagValue + ")");
            }
        }
        switch (lowBits) {
            case 20: {
                this._currToken = JsonToken.VALUE_FALSE;
                return null;
            }
            case 21: {
                this._currToken = JsonToken.VALUE_TRUE;
                return null;
            }
            case 22: {
                this._currToken = JsonToken.VALUE_NULL;
                return null;
            }
            case 23: {
                this._currToken = this._decodeUndefinedValue();
                return null;
            }
            case 25: {
                this._numberFloat = this._decodeHalfSizeFloat();
                this._numTypesValid = 32;
                this._currToken = JsonToken.VALUE_NUMBER_FLOAT;
                return null;
            }
            case 26: {
                this._numberFloat = Float.intBitsToFloat(this._decode32Bits());
                this._numTypesValid = 32;
                this._currToken = JsonToken.VALUE_NUMBER_FLOAT;
                return null;
            }
            case 27: {
                this._numberDouble = Double.longBitsToDouble(this._decode64Bits());
                this._numTypesValid = 8;
                this._currToken = JsonToken.VALUE_NUMBER_FLOAT;
                return null;
            }
            case 31: {
                if (this._streamReadContext.inArray() && !this._streamReadContext.hasExpectedLength()) {
                    this._streamReadContext = this._streamReadContext.getParent();
                    this._currToken = JsonToken.END_ARRAY;
                    return null;
                }
                this._reportUnexpectedBreak();
            }
        }
        this._currToken = this._decodeSimpleValue(lowBits, ch);
        return null;
    }

    @Override
    public int nextIntValue(int defaultValue) throws IOException {
        if (this.nextToken() == JsonToken.VALUE_NUMBER_INT) {
            return this.getIntValue();
        }
        return defaultValue;
    }

    @Override
    public long nextLongValue(long defaultValue) throws IOException {
        if (this.nextToken() == JsonToken.VALUE_NUMBER_INT) {
            return this.getLongValue();
        }
        return defaultValue;
    }

    @Override
    public Boolean nextBooleanValue() throws IOException {
        JsonToken t = this.nextToken();
        if (t == JsonToken.VALUE_TRUE) {
            return Boolean.TRUE;
        }
        if (t == JsonToken.VALUE_FALSE) {
            return Boolean.FALSE;
        }
        return null;
    }

    @Override
    public String getText() throws IOException {
        JsonToken t = this._currToken;
        if (this._tokenIncomplete && t == JsonToken.VALUE_STRING) {
            return this._finishTextToken(this._typeByte);
        }
        if (t == JsonToken.VALUE_STRING) {
            return this._textBuffer.contentsAsString();
        }
        if (t == null) {
            return null;
        }
        if (t == JsonToken.FIELD_NAME) {
            return this._streamReadContext.getCurrentName();
        }
        if (t.isNumeric()) {
            return this.getNumberValue().toString();
        }
        return this._currToken.asString();
    }

    @Override
    public char[] getTextCharacters() throws IOException {
        if (this._currToken != null) {
            if (this._tokenIncomplete) {
                this._finishToken();
            }
            if (this._currToken == JsonToken.VALUE_STRING) {
                return this._textBuffer.getTextBuffer();
            }
            if (this._currToken == JsonToken.FIELD_NAME) {
                return this._streamReadContext.getCurrentName().toCharArray();
            }
            if (this._currToken == JsonToken.VALUE_NUMBER_INT || this._currToken == JsonToken.VALUE_NUMBER_FLOAT) {
                return this.getNumberValue().toString().toCharArray();
            }
            return this._currToken.asCharArray();
        }
        return null;
    }

    @Override
    public int getTextLength() throws IOException {
        if (this._currToken != null) {
            if (this._tokenIncomplete) {
                this._finishToken();
            }
            if (this._currToken == JsonToken.VALUE_STRING) {
                return this._textBuffer.size();
            }
            if (this._currToken == JsonToken.FIELD_NAME) {
                return this._streamReadContext.getCurrentName().length();
            }
            if (this._currToken == JsonToken.VALUE_NUMBER_INT || this._currToken == JsonToken.VALUE_NUMBER_FLOAT) {
                return this.getNumberValue().toString().length();
            }
            return this._currToken.asCharArray().length;
        }
        return 0;
    }

    @Override
    public int getTextOffset() throws IOException {
        return 0;
    }

    @Override
    public String getValueAsString() throws IOException {
        if (this._tokenIncomplete && this._currToken == JsonToken.VALUE_STRING) {
            return this._finishTextToken(this._typeByte);
        }
        if (this._currToken == JsonToken.VALUE_STRING) {
            return this._textBuffer.contentsAsString();
        }
        if (this._currToken == null || this._currToken == JsonToken.VALUE_NULL || !this._currToken.isScalarValue()) {
            return null;
        }
        return this.getText();
    }

    @Override
    public String getValueAsString(String defaultValue) throws IOException {
        if (!(this._currToken == JsonToken.VALUE_STRING || this._currToken != null && this._currToken != JsonToken.VALUE_NULL && this._currToken.isScalarValue())) {
            return defaultValue;
        }
        return this.getText();
    }

    @Override
    public int getText(Writer writer) throws IOException {
        JsonToken t;
        if (this._tokenIncomplete) {
            this._finishToken();
        }
        if ((t = this._currToken) == JsonToken.VALUE_STRING) {
            return this._textBuffer.contentsToWriter(writer);
        }
        if (t == JsonToken.FIELD_NAME) {
            String n = this._streamReadContext.getCurrentName();
            writer.write(n);
            return n.length();
        }
        if (t != null) {
            if (t.isNumeric()) {
                return this._textBuffer.contentsToWriter(writer);
            }
            char[] ch = t.asCharArray();
            writer.write(ch);
            return ch.length;
        }
        return 0;
    }

    @Override
    public byte[] getBinaryValue(Base64Variant b64variant) throws IOException {
        if (this._currToken == JsonToken.VALUE_EMBEDDED_OBJECT) {
            if (this._tokenIncomplete) {
                this._finishToken();
            }
        } else {
            if (this._currToken == JsonToken.VALUE_STRING) {
                return this._getBinaryFromString(b64variant);
            }
            throw this._constructReadException("Current token (%s) not VALUE_EMBEDDED_OBJECT or VALUE_STRING, can not access as binary", (Object)this.currentToken());
        }
        return this._binaryValue;
    }

    @Override
    public Object getEmbeddedObject() throws IOException {
        if (this._tokenIncomplete) {
            this._finishToken();
        }
        if (this._currToken == JsonToken.VALUE_EMBEDDED_OBJECT) {
            return this._binaryValue;
        }
        return null;
    }

    @Override
    public int readBinaryValue(Base64Variant b64variant, OutputStream out) throws IOException {
        if (this._currToken != JsonToken.VALUE_EMBEDDED_OBJECT) {
            if (this._currToken == JsonToken.VALUE_STRING) {
                byte[] b = this._getBinaryFromString(b64variant);
                int len = b.length;
                out.write(b, 0, len);
                return len;
            }
            throw this._constructReadException("Current token (%s) not VALUE_EMBEDDED_OBJECT or VALUE_STRING, can not access as binary", (Object)this.currentToken());
        }
        if (!this._tokenIncomplete) {
            if (this._binaryValue == null) {
                return 0;
            }
            int len = this._binaryValue.length;
            out.write(this._binaryValue, 0, len);
            return len;
        }
        this._tokenIncomplete = false;
        int len = this._decodeExplicitLength(this._typeByte & 0x1F);
        if (len >= 0) {
            return this._readAndWriteBytes(out, len);
        }
        int total = 0;
        while ((len = this._decodeChunkLength(2)) >= 0) {
            total += this._readAndWriteBytes(out, len);
        }
        return total;
    }

    private int _readAndWriteBytes(OutputStream out, int total) throws IOException {
        int count;
        for (int left = total; left > 0; left -= count) {
            int avail = this._inputEnd - this._inputPtr;
            if (this._inputPtr >= this._inputEnd) {
                if (!this.loadMore()) {
                    this._reportIncompleteBinaryRead(total, total - left);
                }
                avail = this._inputEnd - this._inputPtr;
            }
            count = Math.min(avail, left);
            out.write(this._inputBuffer, this._inputPtr, count);
            this._inputPtr += count;
        }
        this._tokenIncomplete = false;
        return total;
    }

    private final byte[] _getBinaryFromString(Base64Variant variant) throws IOException {
        if (this._tokenIncomplete) {
            this._finishToken();
        }
        if (this._binaryValue == null) {
            ByteArrayBuilder builder = this._getByteArrayBuilder();
            this._decodeBase64(this.getText(), builder, variant);
            this._binaryValue = builder.toByteArray();
        }
        return this._binaryValue;
    }

    @Override
    public boolean isNaN() {
        if (this._currToken == JsonToken.VALUE_NUMBER_FLOAT) {
            if ((this._numTypesValid & 8) != 0) {
                double d = this._numberDouble;
                return Double.isNaN(d) || Double.isInfinite(d);
            }
            if ((this._numTypesValid & 0x20) != 0) {
                float f = this._numberFloat;
                return Float.isNaN(f) || Float.isInfinite(f);
            }
        }
        return false;
    }

    @Override
    public Number getNumberValue() throws IOException {
        if (this._numTypesValid == 0) {
            this._checkNumericValue(0);
        }
        if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
            if ((this._numTypesValid & 1) != 0) {
                return this._numberInt;
            }
            if ((this._numTypesValid & 2) != 0) {
                return this._numberLong;
            }
            if ((this._numTypesValid & 4) != 0) {
                return this._numberBigInt;
            }
            return this._numberBigDecimal;
        }
        if ((this._numTypesValid & 0x10) != 0) {
            return this._numberBigDecimal;
        }
        if ((this._numTypesValid & 8) != 0) {
            return this._numberDouble;
        }
        if ((this._numTypesValid & 0x20) == 0) {
            this._throwInternal();
        }
        return Float.valueOf(this._numberFloat);
    }

    @Override
    public final Number getNumberValueExact() throws IOException {
        return this.getNumberValue();
    }

    @Override
    public JsonParser.NumberType getNumberType() throws IOException {
        if (this._numTypesValid == 0) {
            this._checkNumericValue(0);
        }
        if (this._currToken == JsonToken.VALUE_NUMBER_INT) {
            if ((this._numTypesValid & 1) != 0) {
                return JsonParser.NumberType.INT;
            }
            if ((this._numTypesValid & 2) != 0) {
                return JsonParser.NumberType.LONG;
            }
            return JsonParser.NumberType.BIG_INTEGER;
        }
        if ((this._numTypesValid & 0x10) != 0) {
            return JsonParser.NumberType.BIG_DECIMAL;
        }
        if ((this._numTypesValid & 8) != 0) {
            return JsonParser.NumberType.DOUBLE;
        }
        return JsonParser.NumberType.FLOAT;
    }

    @Override
    public int getIntValue() throws IOException {
        if ((this._numTypesValid & 1) == 0) {
            if (this._numTypesValid == 0) {
                this._checkNumericValue(1);
            }
            if ((this._numTypesValid & 1) == 0) {
                this.convertNumberToInt();
            }
        }
        return this._numberInt;
    }

    @Override
    public long getLongValue() throws IOException {
        if ((this._numTypesValid & 2) == 0) {
            if (this._numTypesValid == 0) {
                this._checkNumericValue(2);
            }
            if ((this._numTypesValid & 2) == 0) {
                this.convertNumberToLong();
            }
        }
        return this._numberLong;
    }

    @Override
    public BigInteger getBigIntegerValue() throws IOException {
        if ((this._numTypesValid & 4) == 0) {
            if (this._numTypesValid == 0) {
                this._checkNumericValue(4);
            }
            if ((this._numTypesValid & 4) == 0) {
                this.convertNumberToBigInteger();
            }
        }
        return this._numberBigInt;
    }

    @Override
    public float getFloatValue() throws IOException {
        if ((this._numTypesValid & 0x20) == 0) {
            if (this._numTypesValid == 0) {
                this._checkNumericValue(32);
            }
            if ((this._numTypesValid & 0x20) == 0) {
                this.convertNumberToFloat();
            }
        }
        return this._numberFloat;
    }

    @Override
    public double getDoubleValue() throws IOException {
        if ((this._numTypesValid & 8) == 0) {
            if (this._numTypesValid == 0) {
                this._checkNumericValue(8);
            }
            if ((this._numTypesValid & 8) == 0) {
                this.convertNumberToDouble();
            }
        }
        return this._numberDouble;
    }

    @Override
    public BigDecimal getDecimalValue() throws IOException {
        if ((this._numTypesValid & 0x10) == 0) {
            if (this._numTypesValid == 0) {
                this._checkNumericValue(16);
            }
            if ((this._numTypesValid & 0x10) == 0) {
                this.convertNumberToBigDecimal();
            }
        }
        return this._numberBigDecimal;
    }

    protected void _checkNumericValue(int expType) throws IOException {
        if (this._currToken == JsonToken.VALUE_NUMBER_INT || this._currToken == JsonToken.VALUE_NUMBER_FLOAT) {
            return;
        }
        this._reportError("Current token (" + (Object)((Object)this.currentToken()) + ") not numeric, can not use numeric value accessors");
    }

    protected void convertNumberToInt() throws IOException {
        if ((this._numTypesValid & 2) != 0) {
            int result = (int)this._numberLong;
            if ((long)result != this._numberLong) {
                this._reportError("Numeric value (" + this.getText() + ") out of range of int");
            }
            this._numberInt = result;
        } else if ((this._numTypesValid & 4) != 0) {
            if (BI_MIN_INT.compareTo(this._numberBigInt) > 0 || BI_MAX_INT.compareTo(this._numberBigInt) < 0) {
                this.reportOverflowInt();
            }
            this._numberInt = this._numberBigInt.intValue();
        } else if ((this._numTypesValid & 8) != 0) {
            if (this._numberDouble < -2.147483648E9 || this._numberDouble > 2.147483647E9) {
                this.reportOverflowInt();
            }
            this._numberInt = (int)this._numberDouble;
        } else if ((this._numTypesValid & 0x20) != 0) {
            if ((double)this._numberFloat < -2.147483648E9 || (double)this._numberFloat > 2.147483647E9) {
                this.reportOverflowInt();
            }
            this._numberInt = (int)this._numberFloat;
        } else if ((this._numTypesValid & 0x10) != 0) {
            if (BD_MIN_INT.compareTo(this._numberBigDecimal) > 0 || BD_MAX_INT.compareTo(this._numberBigDecimal) < 0) {
                this.reportOverflowInt();
            }
            this._numberInt = this._numberBigDecimal.intValue();
        } else {
            this._throwInternal();
        }
        this._numTypesValid |= 1;
    }

    protected void convertNumberToLong() throws IOException {
        if ((this._numTypesValid & 1) != 0) {
            this._numberLong = this._numberInt;
        } else if ((this._numTypesValid & 4) != 0) {
            if (BI_MIN_LONG.compareTo(this._numberBigInt) > 0 || BI_MAX_LONG.compareTo(this._numberBigInt) < 0) {
                this.reportOverflowLong();
            }
            this._numberLong = this._numberBigInt.longValue();
        } else if ((this._numTypesValid & 8) != 0) {
            if (this._numberDouble < -9.223372036854776E18 || this._numberDouble > 9.223372036854776E18) {
                this.reportOverflowLong();
            }
            this._numberLong = (long)this._numberDouble;
        } else if ((this._numTypesValid & 0x20) != 0) {
            if ((double)this._numberFloat < -9.223372036854776E18 || (double)this._numberFloat > 9.223372036854776E18) {
                this.reportOverflowInt();
            }
            this._numberLong = (long)this._numberFloat;
        } else if ((this._numTypesValid & 0x10) != 0) {
            if (BD_MIN_LONG.compareTo(this._numberBigDecimal) > 0 || BD_MAX_LONG.compareTo(this._numberBigDecimal) < 0) {
                this.reportOverflowLong();
            }
            this._numberLong = this._numberBigDecimal.longValue();
        } else {
            this._throwInternal();
        }
        this._numTypesValid |= 2;
    }

    protected void convertNumberToBigInteger() throws IOException {
        if ((this._numTypesValid & 0x10) != 0) {
            this._numberBigInt = this._numberBigDecimal.toBigInteger();
        } else if ((this._numTypesValid & 2) != 0) {
            this._numberBigInt = BigInteger.valueOf(this._numberLong);
        } else if ((this._numTypesValid & 1) != 0) {
            this._numberBigInt = BigInteger.valueOf(this._numberInt);
        } else if ((this._numTypesValid & 8) != 0) {
            this._numberBigInt = BigDecimal.valueOf(this._numberDouble).toBigInteger();
        } else if ((this._numTypesValid & 0x20) != 0) {
            this._numberBigInt = BigDecimal.valueOf(this._numberFloat).toBigInteger();
        } else {
            this._throwInternal();
        }
        this._numTypesValid |= 4;
    }

    protected void convertNumberToFloat() throws IOException {
        if ((this._numTypesValid & 0x10) != 0) {
            this._numberFloat = this._numberBigDecimal.floatValue();
        } else if ((this._numTypesValid & 4) != 0) {
            this._numberFloat = this._numberBigInt.floatValue();
        } else if ((this._numTypesValid & 8) != 0) {
            this._numberFloat = (float)this._numberDouble;
        } else if ((this._numTypesValid & 2) != 0) {
            this._numberFloat = this._numberLong;
        } else if ((this._numTypesValid & 1) != 0) {
            this._numberFloat = this._numberInt;
        } else {
            this._throwInternal();
        }
        this._numTypesValid |= 0x20;
    }

    protected void convertNumberToDouble() throws IOException {
        if ((this._numTypesValid & 0x10) != 0) {
            this._numberDouble = this._numberBigDecimal.doubleValue();
        } else if ((this._numTypesValid & 0x20) != 0) {
            this._numberDouble = this._numberFloat;
        } else if ((this._numTypesValid & 4) != 0) {
            this._numberDouble = this._numberBigInt.doubleValue();
        } else if ((this._numTypesValid & 2) != 0) {
            this._numberDouble = this._numberLong;
        } else if ((this._numTypesValid & 1) != 0) {
            this._numberDouble = this._numberInt;
        } else {
            this._throwInternal();
        }
        this._numTypesValid |= 8;
    }

    protected void convertNumberToBigDecimal() throws IOException {
        if ((this._numTypesValid & 0x28) != 0) {
            this._numberBigDecimal = NumberInput.parseBigDecimal(this.getText());
        } else if ((this._numTypesValid & 4) != 0) {
            this._numberBigDecimal = new BigDecimal(this._numberBigInt);
        } else if ((this._numTypesValid & 2) != 0) {
            this._numberBigDecimal = BigDecimal.valueOf(this._numberLong);
        } else if ((this._numTypesValid & 1) != 0) {
            this._numberBigDecimal = BigDecimal.valueOf(this._numberInt);
        } else {
            this._throwInternal();
        }
        this._numTypesValid |= 0x10;
    }

    protected void _finishToken() throws IOException {
        int len;
        this._tokenIncomplete = false;
        int ch = this._typeByte;
        int type = ch >> 5 & 7;
        ch &= 0x1F;
        if (type != 3) {
            if (type == 2) {
                this._binaryValue = this._finishBytes(this._decodeExplicitLength(ch));
                return;
            }
            this._throwInternal();
        }
        if ((len = this._decodeExplicitLength(ch)) <= 0) {
            if (len < 0) {
                this._finishChunkedText();
            } else {
                this._textBuffer.resetWithEmpty();
            }
            return;
        }
        int available = this._inputEnd - this._inputPtr;
        int needed = len + 3;
        if (available >= needed || this._inputBuffer.length >= needed && this._tryToLoadToHaveAtLeast(needed)) {
            this._finishShortText(len);
            return;
        }
        this._finishLongText(len);
    }

    protected String _finishTextToken(int ch) throws IOException {
        int len;
        this._tokenIncomplete = false;
        int type = ch >> 5 & 7;
        ch &= 0x1F;
        if (type != 3) {
            this._throwInternal();
        }
        if ((len = this._decodeExplicitLength(ch)) <= 0) {
            if (len == 0) {
                this._textBuffer.resetWithEmpty();
                return "";
            }
            this._finishChunkedText();
            return this._textBuffer.contentsAsString();
        }
        int available = this._inputEnd - this._inputPtr;
        int needed = Math.max(len + 3, len);
        if (available >= needed || this._inputBuffer.length >= needed && this._tryToLoadToHaveAtLeast(needed)) {
            return this._finishShortText(len);
        }
        this._finishLongText(len);
        return this._textBuffer.contentsAsString();
    }

    private final String _finishShortText(int len) throws IOException {
        int i;
        char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
        if (outBuf.length < len) {
            outBuf = this._textBuffer.expandCurrentSegment(len);
        }
        int outPtr = 0;
        int inPtr = this._inputPtr;
        this._inputPtr += len;
        byte[] inputBuf = this._inputBuffer;
        int end = inPtr + len;
        while ((i = inputBuf[inPtr]) >= 0) {
            outBuf[outPtr++] = (char)i;
            if (++inPtr != end) continue;
            return this._textBuffer.setCurrentAndReturn(outPtr);
        }
        int[] codes = UTF8_UNIT_CODES;
        do {
            i = inputBuf[inPtr++] & 0xFF;
            switch (codes[i]) {
                case 0: {
                    break;
                }
                case 1: {
                    byte c2 = inputBuf[inPtr++];
                    if ((c2 & 0xC0) != 128) {
                        this._reportInvalidOther(c2 & 0xFF, inPtr);
                    }
                    i = (i & 0x1F) << 6 | c2 & 0x3F;
                    break;
                }
                case 2: {
                    byte c3;
                    byte c2 = inputBuf[inPtr++];
                    if ((c2 & 0xC0) != 128) {
                        this._reportInvalidOther(c2 & 0xFF, inPtr);
                    }
                    if (((c3 = inputBuf[inPtr++]) & 0xC0) != 128) {
                        this._reportInvalidOther(c3 & 0xFF, inPtr);
                    }
                    i = (i & 0xF) << 12 | (c2 & 0x3F) << 6 | c3 & 0x3F;
                    break;
                }
                case 3: {
                    i = (i & 7) << 18 | (inputBuf[inPtr++] & 0x3F) << 12 | (inputBuf[inPtr++] & 0x3F) << 6 | inputBuf[inPtr++] & 0x3F;
                    outBuf[outPtr++] = (char)(0xD800 | (i -= 65536) >> 10);
                    i = 0xDC00 | i & 0x3FF;
                    break;
                }
                default: {
                    this._reportInvalidInitial(i);
                }
            }
            outBuf[outPtr++] = (char)i;
        } while (inPtr < end);
        return this._textBuffer.setCurrentAndReturn(outPtr);
    }

    private final void _finishLongText(int len) throws IOException {
        char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
        int outPtr = 0;
        int[] codes = UTF8_UNIT_CODES;
        int outEnd = outBuf.length;
        while (--len >= 0) {
            int c = this._nextByte() & 0xFF;
            int code = codes[c];
            if (code == 0 && outPtr < outEnd) {
                outBuf[outPtr++] = (char)c;
                continue;
            }
            if ((len -= code) < 0) {
                throw this._constructReadException("Malformed UTF-8 character at the end of a (non-chunked) text segment");
            }
            switch (code) {
                case 0: {
                    break;
                }
                case 1: {
                    int d = this._nextByte();
                    if ((d & 0xC0) != 128) {
                        this._reportInvalidOther(d & 0xFF, this._inputPtr);
                    }
                    c = (c & 0x1F) << 6 | d & 0x3F;
                    break;
                }
                case 2: {
                    c = this._decodeUTF8_3(c);
                    break;
                }
                case 3: {
                    c = this._decodeUTF8_4(c);
                    if (outPtr >= outBuf.length) {
                        outBuf = this._textBuffer.finishCurrentSegment();
                        outPtr = 0;
                        outEnd = outBuf.length;
                    }
                    outBuf[outPtr++] = (char)(0xD800 | c >> 10);
                    c = 0xDC00 | c & 0x3FF;
                    break;
                }
                default: {
                    this._reportInvalidChar(c);
                }
            }
            if (outPtr >= outEnd) {
                outBuf = this._textBuffer.finishCurrentSegment();
                outPtr = 0;
                outEnd = outBuf.length;
            }
            outBuf[outPtr++] = (char)c;
        }
        this._textBuffer.setCurrentLength(outPtr);
    }

    private final void _finishChunkedText() throws IOException {
        char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
        int outPtr = 0;
        int[] codes = UTF8_UNIT_CODES;
        int outEnd = outBuf.length;
        byte[] input = this._inputBuffer;
        this._chunkEnd = this._inputPtr;
        this._chunkLeft = 0;
        while (true) {
            int c;
            int code;
            if (this._inputPtr >= this._chunkEnd) {
                if (this._chunkLeft == 0) {
                    int len = this._decodeChunkLength(3);
                    if (len <= 0) {
                        if (len != 0) break;
                        continue;
                    }
                    this._chunkLeft = len;
                    int end = this._inputPtr + len;
                    if (end <= this._inputEnd) {
                        this._chunkLeft = 0;
                        this._chunkEnd = end;
                    } else {
                        this._chunkLeft = end - this._inputEnd;
                        this._chunkEnd = this._inputEnd;
                    }
                }
                if (this._inputPtr >= this._inputEnd) {
                    this.loadMoreGuaranteed();
                    int end = this._inputPtr + this._chunkLeft;
                    if (end <= this._inputEnd) {
                        this._chunkLeft = 0;
                        this._chunkEnd = end;
                    } else {
                        this._chunkLeft = end - this._inputEnd;
                        this._chunkEnd = this._inputEnd;
                    }
                }
            }
            if ((code = codes[c = input[this._inputPtr++] & 0xFF]) == 0 && outPtr < outEnd) {
                outBuf[outPtr++] = (char)c;
                continue;
            }
            switch (code) {
                case 0: {
                    break;
                }
                case 1: {
                    int d = this._nextChunkedByte();
                    if ((d & 0xC0) != 128) {
                        this._reportInvalidOther(d & 0xFF, this._inputPtr);
                    }
                    c = (c & 0x1F) << 6 | d & 0x3F;
                    break;
                }
                case 2: {
                    c = this._decodeChunkedUTF8_3(c);
                    break;
                }
                case 3: {
                    c = this._decodeChunkedUTF8_4(c);
                    if (outPtr >= outBuf.length) {
                        outBuf = this._textBuffer.finishCurrentSegment();
                        outPtr = 0;
                        outEnd = outBuf.length;
                    }
                    outBuf[outPtr++] = (char)(0xD800 | c >> 10);
                    c = 0xDC00 | c & 0x3FF;
                    break;
                }
                default: {
                    this._reportInvalidChar(c);
                }
            }
            if (outPtr >= outEnd) {
                outBuf = this._textBuffer.finishCurrentSegment();
                outPtr = 0;
                outEnd = outBuf.length;
            }
            outBuf[outPtr++] = (char)c;
        }
        this._textBuffer.setCurrentLength(outPtr);
    }

    private final int _nextByte() throws IOException {
        int inPtr = this._inputPtr;
        if (inPtr < this._inputEnd) {
            byte ch = this._inputBuffer[inPtr];
            this._inputPtr = inPtr + 1;
            return ch;
        }
        this.loadMoreGuaranteed();
        return this._inputBuffer[this._inputPtr++];
    }

    private final int _nextChunkedByte() throws IOException {
        int inPtr = this._inputPtr;
        if (inPtr >= this._chunkEnd) {
            return this._nextChunkedByte2();
        }
        byte ch = this._inputBuffer[inPtr];
        this._inputPtr = inPtr + 1;
        return ch;
    }

    private final int _nextChunkedByte2() throws IOException {
        int end;
        int len;
        if (this._inputPtr >= this._inputEnd) {
            this.loadMoreGuaranteed();
            if (this._chunkLeft > 0) {
                int end2 = this._inputPtr + this._chunkLeft;
                if (end2 <= this._inputEnd) {
                    this._chunkLeft = 0;
                    this._chunkEnd = end2;
                } else {
                    this._chunkLeft = end2 - this._inputEnd;
                    this._chunkEnd = this._inputEnd;
                }
                return this._inputBuffer[this._inputPtr++];
            }
        }
        if ((len = this._decodeChunkLength(3)) <= 0) {
            this._reportInvalidEOF(": chunked Text ends with partial UTF-8 character", JsonToken.VALUE_STRING);
        }
        if (this._inputPtr >= this._inputEnd) {
            this.loadMoreGuaranteed();
        }
        if ((end = this._inputPtr + len) <= this._inputEnd) {
            this._chunkLeft = 0;
            this._chunkEnd = end;
        } else {
            this._chunkLeft = end - this._inputEnd;
            this._chunkEnd = this._inputEnd;
        }
        return this._inputBuffer[this._inputPtr++];
    }

    protected byte[] _finishBytes(int len) throws IOException {
        if (len <= 0) {
            if (len == 0) {
                return NO_BYTES;
            }
            return this._finishChunkedBytes();
        }
        if (len > 250000) {
            return this._finishLongContiguousBytes(len);
        }
        byte[] b = new byte[len];
        int expLen = len;
        if (this._inputPtr >= this._inputEnd && !this.loadMore()) {
            this._reportIncompleteBinaryRead(expLen, 0);
        }
        int ptr = 0;
        while (true) {
            int toAdd = Math.min(len, this._inputEnd - this._inputPtr);
            System.arraycopy(this._inputBuffer, this._inputPtr, b, ptr, toAdd);
            this._inputPtr += toAdd;
            ptr += toAdd;
            if ((len -= toAdd) <= 0) {
                return b;
            }
            if (this.loadMore()) continue;
            this._reportIncompleteBinaryRead(expLen, ptr);
        }
    }

    protected byte[] _finishChunkedBytes() throws IOException {
        ByteArrayBuilder bb = this._getByteArrayBuilder();
        block0: while (true) {
            int ch;
            if (this._inputPtr >= this._inputEnd) {
                this.loadMoreGuaranteed();
            }
            if ((ch = this._inputBuffer[this._inputPtr++] & 0xFF) == 255) break;
            int type = ch >> 5;
            if (type != 2) {
                throw this._constructReadException("Mismatched chunk in chunked content: expected %d but encountered %d", 2, type);
            }
            int len = this._decodeExplicitLength(ch & 0x1F);
            if (len < 0) {
                throw this._constructReadException("Illegal chunked-length indicator within chunked-length value (type %d)", 2);
            }
            int chunkLen = len;
            while (true) {
                if (len <= 0) continue block0;
                int avail = this._inputEnd - this._inputPtr;
                if (this._inputPtr >= this._inputEnd) {
                    if (!this.loadMore()) {
                        this._reportIncompleteBinaryRead(chunkLen, chunkLen - len);
                    }
                    avail = this._inputEnd - this._inputPtr;
                }
                int count = Math.min(avail, len);
                bb.write(this._inputBuffer, this._inputPtr, count);
                this._inputPtr += count;
                len -= count;
            }
            break;
        }
        return bb.toByteArray();
    }

    protected byte[] _finishLongContiguousBytes(int expLen) throws IOException {
        try (ByteArrayBuilder bb = new ByteArrayBuilder(125000);){
            int count;
            for (int left = expLen; left > 0; left -= count) {
                int avail = this._inputEnd - this._inputPtr;
                if (avail <= 0) {
                    if (!this.loadMore()) {
                        this._reportIncompleteBinaryRead(expLen, expLen - left);
                    }
                    avail = this._inputEnd - this._inputPtr;
                }
                count = Math.min(avail, left);
                bb.write(this._inputBuffer, this._inputPtr, count);
                this._inputPtr += count;
            }
            byte[] byArray = bb.toByteArray();
            return byArray;
        }
    }

    protected final JsonToken _decodePropertyName() throws IOException {
        String name;
        byte ch;
        int type;
        if (this._inputPtr >= this._inputEnd && !this.loadMore()) {
            this._eofAsNextToken();
        }
        if ((type = (ch = this._inputBuffer[this._inputPtr++]) >> 5 & 7) != 3) {
            if (ch == -1) {
                if (!this._streamReadContext.hasExpectedLength()) {
                    this._streamReadContext = this._streamReadContext.getParent();
                    return JsonToken.END_OBJECT;
                }
                this._reportUnexpectedBreak();
            }
            this._decodeNonStringName(ch);
            return JsonToken.FIELD_NAME;
        }
        int lenMarker = ch & 0x1F;
        if (lenMarker <= 23) {
            if (lenMarker == 0) {
                name = "";
            } else {
                if (this._inputEnd - this._inputPtr < lenMarker) {
                    this._loadToHaveAtLeast(lenMarker);
                }
                if (this._symbolsCanonical) {
                    name = this._findDecodedFromSymbols(lenMarker);
                    if (name != null) {
                        this._inputPtr += lenMarker;
                    } else {
                        name = this._decodeContiguousName(lenMarker);
                        name = this._addDecodedToSymbols(lenMarker, name);
                    }
                } else {
                    name = this._decodeContiguousName(lenMarker);
                }
            }
        } else {
            int actualLen = this._decodeExplicitLength(lenMarker);
            name = actualLen < 0 ? this._decodeChunkedName() : this._decodeLongerName(actualLen);
        }
        this._streamReadContext.setCurrentName(name);
        return JsonToken.FIELD_NAME;
    }

    private final String _decodeContiguousName(int len) throws IOException {
        int i;
        int code;
        int outPtr = 0;
        char[] outBuf = this._textBuffer.emptyAndGetCurrentSegment();
        if (outBuf.length < len) {
            outBuf = this._textBuffer.expandCurrentSegment(len);
        }
        int inPtr = this._inputPtr;
        this._inputPtr += len;
        int[] codes = UTF8_UNIT_CODES;
        byte[] inBuf = this._inputBuffer;
        int end = inPtr + len;
        while ((code = codes[i = inBuf[inPtr] & 0xFF]) == 0) {
            outBuf[outPtr++] = (char)i;
            if (++inPtr != end) continue;
            return this._textBuffer.setCurrentAndReturn(outPtr);
        }
        while (inPtr < end) {
            if ((code = codes[i = inBuf[inPtr++] & 0xFF]) != 0) {
                if (inPtr + code > end) {
                    int firstCharOffset = len - (end - inPtr) - 1;
                    this._reportTruncatedUTF8InName(len, firstCharOffset, i, code);
                }
                switch (code) {
                    case 1: {
                        byte c2 = inBuf[inPtr++];
                        if ((c2 & 0xC0) != 128) {
                            this._reportInvalidOther(c2 & 0xFF, inPtr);
                        }
                        i = (i & 0x1F) << 6 | c2 & 0x3F;
                        break;
                    }
                    case 2: {
                        byte c3;
                        byte c2 = inBuf[inPtr++];
                        if ((c2 & 0xC0) != 128) {
                            this._reportInvalidOther(c2 & 0xFF, inPtr);
                        }
                        if (((c3 = inBuf[inPtr++]) & 0xC0) != 128) {
                            this._reportInvalidOther(c3 & 0xFF, inPtr);
                        }
                        i = (i & 0xF) << 12 | (c2 & 0x3F) << 6 | c3 & 0x3F;
                        break;
                    }
                    case 3: {
                        i = (i & 7) << 18 | (inBuf[inPtr++] & 0x3F) << 12 | (inBuf[inPtr++] & 0x3F) << 6 | inBuf[inPtr++] & 0x3F;
                        outBuf[outPtr++] = (char)(0xD800 | (i -= 65536) >> 10);
                        i = 0xDC00 | i & 0x3FF;
                        break;
                    }
                    default: {
                        throw this._constructReadException("Invalid UTF-8 byte 0x%s in Object property name", Integer.toHexString(i));
                    }
                }
            }
            outBuf[outPtr++] = (char)i;
        }
        return this._textBuffer.setCurrentAndReturn(outPtr);
    }

    private final String _decodeLongerName(int len) throws IOException {
        if (len == 0) {
            return "";
        }
        if (this._inputEnd - this._inputPtr < len) {
            if (len >= this._inputBuffer.length) {
                this._finishLongText(len);
                return this._textBuffer.contentsAsString();
            }
            this._loadToHaveAtLeast(len);
        }
        if (this._symbolsCanonical) {
            String name = this._findDecodedFromSymbols(len);
            if (name != null) {
                this._inputPtr += len;
                return name;
            }
            name = this._decodeContiguousName(len);
            return this._addDecodedToSymbols(len, name);
        }
        return this._decodeContiguousName(len);
    }

    private final String _decodeChunkedName() throws IOException {
        this._finishChunkedText();
        return this._textBuffer.contentsAsString();
    }

    protected final void _decodeNonStringName(int ch) throws IOException {
        String name;
        int type = ch >> 5 & 7;
        if (type == 0) {
            name = this._numberToName(ch, false);
        } else if (type == 1) {
            name = this._numberToName(ch, true);
        } else if (type == 2) {
            int blen = this._decodeExplicitLength(ch & 0x1F);
            byte[] b = this._finishBytes(blen);
            name = new String(b, UTF8);
        } else {
            if ((ch & 0xFF) == 255) {
                this._reportUnexpectedBreak();
            }
            throw this._constructReadException("Unsupported major type (%d) for CBOR Objects, not (yet?) supported, only Strings", type);
        }
        this._streamReadContext.setCurrentName(name);
    }

    private final String _findDecodedFromSymbols(int len) throws IOException {
        if (len < 5) {
            int inPtr = this._inputPtr;
            byte[] inBuf = this._inputBuffer;
            int q = inBuf[inPtr] & 0xFF;
            if (len > 1) {
                q = (q << 8) + (inBuf[++inPtr] & 0xFF);
                if (len > 2) {
                    q = (q << 8) + (inBuf[++inPtr] & 0xFF);
                    if (len > 3) {
                        q = (q << 8) + (inBuf[++inPtr] & 0xFF);
                    }
                }
            }
            this._quad1 = q;
            return this._symbols.findName(q);
        }
        byte[] inBuf = this._inputBuffer;
        int inPtr = this._inputPtr;
        int q1 = inBuf[inPtr++] & 0xFF;
        q1 = q1 << 8 | inBuf[inPtr++] & 0xFF;
        q1 = q1 << 8 | inBuf[inPtr++] & 0xFF;
        q1 = q1 << 8 | inBuf[inPtr++] & 0xFF;
        if (len < 9) {
            int q2 = inBuf[inPtr++] & 0xFF;
            int left = len - 5;
            if (left > 0) {
                q2 = (q2 << 8) + (inBuf[inPtr++] & 0xFF);
                if (left > 1) {
                    q2 = (q2 << 8) + (inBuf[inPtr++] & 0xFF);
                    if (left > 2) {
                        q2 = (q2 << 8) + (inBuf[inPtr++] & 0xFF);
                    }
                }
            }
            this._quad1 = q1;
            this._quad2 = q2;
            return this._symbols.findName(q1, q2);
        }
        int q2 = inBuf[inPtr++] & 0xFF;
        q2 = q2 << 8 | inBuf[inPtr++] & 0xFF;
        q2 = q2 << 8 | inBuf[inPtr++] & 0xFF;
        q2 = q2 << 8 | inBuf[inPtr++] & 0xFF;
        if (len < 13) {
            int q3 = inBuf[inPtr++] & 0xFF;
            int left = len - 9;
            if (left > 0) {
                q3 = (q3 << 8) + (inBuf[inPtr++] & 0xFF);
                if (left > 1) {
                    q3 = (q3 << 8) + (inBuf[inPtr++] & 0xFF);
                    if (left > 2) {
                        q3 = (q3 << 8) + (inBuf[inPtr++] & 0xFF);
                    }
                }
            }
            this._quad1 = q1;
            this._quad2 = q2;
            this._quad3 = q3;
            return this._symbols.findName(q1, q2, q3);
        }
        return this._findDecodedLong(len, q1, q2);
    }

    private final String _findDecodedLong(int len, int q1, int q2) throws IOException {
        int q;
        int bufLen = len + 3 >> 2;
        if (bufLen > this._quadBuffer.length) {
            this._quadBuffer = CBORParser._growArrayTo(this._quadBuffer, bufLen);
        }
        this._quadBuffer[0] = q1;
        this._quadBuffer[1] = q2;
        int offset = 2;
        int inPtr = this._inputPtr + 8;
        len -= 8;
        byte[] inBuf = this._inputBuffer;
        do {
            q = inBuf[inPtr++] & 0xFF;
            q = q << 8 | inBuf[inPtr++] & 0xFF;
            q = q << 8 | inBuf[inPtr++] & 0xFF;
            q = q << 8 | inBuf[inPtr++] & 0xFF;
            this._quadBuffer[offset++] = q;
        } while ((len -= 4) > 3);
        if (len > 0) {
            q = inBuf[inPtr] & 0xFF;
            if (len > 1) {
                q = (q << 8) + (inBuf[++inPtr] & 0xFF);
                if (len > 2) {
                    q = (q << 8) + (inBuf[++inPtr] & 0xFF);
                }
            }
            this._quadBuffer[offset++] = q;
        }
        return this._symbols.findName(this._quadBuffer, offset);
    }

    private final String _addDecodedToSymbols(int len, String name) {
        if (len < 5) {
            return this._symbols.addName(name, this._quad1);
        }
        if (len < 9) {
            return this._symbols.addName(name, this._quad1, this._quad2);
        }
        if (len < 13) {
            return this._symbols.addName(name, this._quad1, this._quad2, this._quad3);
        }
        int qlen = len + 3 >> 2;
        return this._symbols.addName(name, this._quadBuffer, qlen);
    }

    private static int[] _growArrayTo(int[] arr, int minSize) {
        return Arrays.copyOf(arr, minSize + 4);
    }

    protected void _skipIncomplete() throws IOException {
        int lowBits;
        this._tokenIncomplete = false;
        int type = this._typeByte >> 5 & 7;
        if (type != 3 && type != 2) {
            this._throwInternal();
        }
        if ((lowBits = this._typeByte & 0x1F) <= 23) {
            if (lowBits > 0) {
                this._skipBytes(lowBits);
            }
            return;
        }
        switch (lowBits) {
            case 24: {
                this._skipBytes(this._decode8Bits());
                break;
            }
            case 25: {
                this._skipBytes(this._decode16Bits());
                break;
            }
            case 26: {
                this._skipBytes(this._decode32Bits());
                break;
            }
            case 27: {
                this._skipBytesL(this._decode64Bits());
                break;
            }
            case 31: {
                this._skipChunked(type);
                break;
            }
            default: {
                this._invalidToken(this._typeByte);
            }
        }
    }

    protected void _skipChunked(int expectedType) throws IOException {
        block7: while (true) {
            int ch;
            if (this._inputPtr >= this._inputEnd) {
                this.loadMoreGuaranteed();
            }
            if ((ch = this._inputBuffer[this._inputPtr++] & 0xFF) == 255) {
                return;
            }
            int type = ch >> 5;
            if (type != expectedType) {
                throw this._constructError("Mismatched chunk in chunked content: expected " + expectedType + " but encountered " + type);
            }
            int lowBits = ch & 0x1F;
            if (lowBits <= 23) {
                if (lowBits <= 0) continue;
                this._skipBytes(lowBits);
                continue;
            }
            switch (lowBits) {
                case 24: {
                    this._skipBytes(this._decode8Bits());
                    continue block7;
                }
                case 25: {
                    this._skipBytes(this._decode16Bits());
                    continue block7;
                }
                case 26: {
                    this._skipBytes(this._decode32Bits());
                    continue block7;
                }
                case 27: {
                    this._skipBytesL(this._decode64Bits());
                    continue block7;
                }
                case 31: {
                    throw this._constructReadException("Illegal chunked-length indicator within chunked-length value (type %d)", expectedType);
                }
            }
            this._invalidToken(this._typeByte);
        }
    }

    protected void _skipBytesL(long llen) throws IOException {
        while (llen > Integer.MAX_VALUE) {
            this._skipBytes(Integer.MAX_VALUE);
            llen -= Integer.MAX_VALUE;
        }
        this._skipBytes((int)llen);
    }

    protected void _skipBytes(int len) throws IOException {
        while (true) {
            int toAdd = Math.min(len, this._inputEnd - this._inputPtr);
            this._inputPtr += toAdd;
            if ((len -= toAdd) <= 0) {
                return;
            }
            this.loadMoreGuaranteed();
        }
    }

    private final int _decodeTag(int lowBits) throws IOException {
        if (lowBits <= 23) {
            return lowBits;
        }
        switch (lowBits - 24) {
            case 0: {
                return this._decode8Bits();
            }
            case 1: {
                return this._decode16Bits();
            }
            case 2: {
                return this._decode32Bits();
            }
            case 3: {
                long l = this._decode64Bits();
                if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
                    throw this._constructReadException("Illegal Tag value: %d", l);
                }
                return (int)l;
            }
        }
        throw this._constructReadException("Invalid low bits for Tag token: 0x%s", Integer.toHexString(lowBits));
    }

    private final int _decodeExplicitLength(int lowBits) throws IOException {
        if (lowBits == 31) {
            return -1;
        }
        if (lowBits <= 23) {
            return lowBits;
        }
        switch (lowBits - 24) {
            case 0: {
                return this._decode8Bits();
            }
            case 1: {
                return this._decode16Bits();
            }
            case 2: {
                return this._decode32Bits();
            }
            case 3: {
                long l = this._decode64Bits();
                if (l < 0L || l > Integer.MAX_VALUE) {
                    throw this._constructError("Illegal length for " + (Object)((Object)this.currentToken()) + ": " + l);
                }
                return (int)l;
            }
        }
        throw this._constructError(String.format("Invalid length for %s: 0x%02X,", new Object[]{this.currentToken(), lowBits}));
    }

    private int _decodeChunkLength(int expType) throws IOException {
        int ch;
        if (this._inputPtr >= this._inputEnd) {
            this.loadMoreGuaranteed();
        }
        if ((ch = this._inputBuffer[this._inputPtr++] & 0xFF) == 255) {
            return -1;
        }
        int type = ch >> 5;
        if (type != expType) {
            throw this._constructError(String.format("Mismatched chunk in chunked content: expected major type %d but encountered %d (byte 0x%02X)", expType, type, ch));
        }
        int len = this._decodeExplicitLength(ch & 0x1F);
        if (len < 0) {
            throw this._constructReadException("Illegal chunked-length indicator within chunked-length value (major type %d)", expType);
        }
        return len;
    }

    private float _decodeHalfSizeFloat() throws IOException {
        int i16 = this._decode16Bits() & 0xFFFF;
        boolean neg = i16 >> 15 != 0;
        int e = i16 >> 10 & 0x1F;
        int f = i16 & 0x3FF;
        if (e == 0) {
            float result = (float)(MATH_POW_2_NEG14 * ((double)f / MATH_POW_2_10));
            return neg ? -result : result;
        }
        if (e == 31) {
            if (f != 0) {
                return Float.NaN;
            }
            return neg ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
        }
        float result = (float)(Math.pow(2.0, e - 15) * (1.0 + (double)f / MATH_POW_2_10));
        return neg ? -result : result;
    }

    private final int _decode8Bits() throws IOException {
        if (this._inputPtr >= this._inputEnd) {
            this.loadMoreGuaranteed();
        }
        return this._inputBuffer[this._inputPtr++] & 0xFF;
    }

    private final int _decode16Bits() throws IOException {
        int ptr = this._inputPtr;
        if (ptr + 1 >= this._inputEnd) {
            return this._slow16();
        }
        byte[] b = this._inputBuffer;
        int v = ((b[ptr] & 0xFF) << 8) + (b[ptr + 1] & 0xFF);
        this._inputPtr = ptr + 2;
        return v;
    }

    private final int _slow16() throws IOException {
        if (this._inputPtr >= this._inputEnd) {
            this.loadMoreGuaranteed();
        }
        int v = this._inputBuffer[this._inputPtr++] & 0xFF;
        if (this._inputPtr >= this._inputEnd) {
            this.loadMoreGuaranteed();
        }
        return (v << 8) + (this._inputBuffer[this._inputPtr++] & 0xFF);
    }

    private final int _decode32Bits() throws IOException {
        int ptr = this._inputPtr;
        if (ptr + 3 >= this._inputEnd) {
            return this._slow32();
        }
        byte[] b = this._inputBuffer;
        int v = (b[ptr++] << 24) + ((b[ptr++] & 0xFF) << 16) + ((b[ptr++] & 0xFF) << 8) + (b[ptr++] & 0xFF);
        this._inputPtr = ptr;
        return v;
    }

    private final int _slow32() throws IOException {
        if (this._inputPtr >= this._inputEnd) {
            this.loadMoreGuaranteed();
        }
        int v = this._inputBuffer[this._inputPtr++];
        if (this._inputPtr >= this._inputEnd) {
            this.loadMoreGuaranteed();
        }
        v = (v << 8) + (this._inputBuffer[this._inputPtr++] & 0xFF);
        if (this._inputPtr >= this._inputEnd) {
            this.loadMoreGuaranteed();
        }
        v = (v << 8) + (this._inputBuffer[this._inputPtr++] & 0xFF);
        if (this._inputPtr >= this._inputEnd) {
            this.loadMoreGuaranteed();
        }
        return (v << 8) + (this._inputBuffer[this._inputPtr++] & 0xFF);
    }

    private final long _decode64Bits() throws IOException {
        int ptr = this._inputPtr;
        if (ptr + 7 >= this._inputEnd) {
            return this._slow64();
        }
        byte[] b = this._inputBuffer;
        int i1 = (b[ptr++] << 24) + ((b[ptr++] & 0xFF) << 16) + ((b[ptr++] & 0xFF) << 8) + (b[ptr++] & 0xFF);
        int i2 = (b[ptr++] << 24) + ((b[ptr++] & 0xFF) << 16) + ((b[ptr++] & 0xFF) << 8) + (b[ptr++] & 0xFF);
        this._inputPtr = ptr;
        return CBORParser._long(i1, i2);
    }

    private final long _slow64() throws IOException {
        return CBORParser._long(this._decode32Bits(), this._decode32Bits());
    }

    private static final long _long(int i1, int i2) {
        long l1 = i1;
        long l2 = i2;
        l2 = l2 << 32 >>> 32;
        return (l1 << 32) + l2;
    }

    protected JsonToken _decodeUndefinedValue() throws IOException {
        return JsonToken.VALUE_NULL;
    }

    public JsonToken _decodeSimpleValue(int lowBits, int ch) throws IOException {
        if (lowBits > 24) {
            this._invalidToken(ch);
        }
        if (lowBits < 24) {
            this._numberInt = lowBits;
        } else {
            if (this._inputPtr >= this._inputEnd) {
                this.loadMoreGuaranteed();
            }
            this._numberInt = this._inputBuffer[this._inputPtr++] & 0xFF;
            if (this._numberInt < 32) {
                throw this._constructError("Invalid second byte for simple value: 0x" + Integer.toHexString(this._numberInt) + " (only values 0x20 - 0xFF allowed)");
            }
        }
        this._numTypesValid = 1;
        return JsonToken.VALUE_NUMBER_INT;
    }

    private final int _decodeUTF8_3(int c1) throws IOException {
        c1 &= 0xF;
        int d = this._nextByte();
        if ((d & 0xC0) != 128) {
            this._reportInvalidOther(d & 0xFF, this._inputPtr);
        }
        int c = c1 << 6 | d & 0x3F;
        d = this._nextByte();
        if ((d & 0xC0) != 128) {
            this._reportInvalidOther(d & 0xFF, this._inputPtr);
        }
        c = c << 6 | d & 0x3F;
        return c;
    }

    private final int _decodeChunkedUTF8_3(int c1) throws IOException {
        c1 &= 0xF;
        int d = this._nextChunkedByte();
        if ((d & 0xC0) != 128) {
            this._reportInvalidOther(d & 0xFF, this._inputPtr);
        }
        int c = c1 << 6 | d & 0x3F;
        d = this._nextChunkedByte();
        if ((d & 0xC0) != 128) {
            this._reportInvalidOther(d & 0xFF, this._inputPtr);
        }
        c = c << 6 | d & 0x3F;
        return c;
    }

    private final int _decodeUTF8_4(int c) throws IOException {
        int d = this._nextByte();
        if ((d & 0xC0) != 128) {
            this._reportInvalidOther(d & 0xFF, this._inputPtr);
        }
        c = (c & 7) << 6 | d & 0x3F;
        d = this._nextByte();
        if ((d & 0xC0) != 128) {
            this._reportInvalidOther(d & 0xFF, this._inputPtr);
        }
        c = c << 6 | d & 0x3F;
        d = this._nextByte();
        if ((d & 0xC0) != 128) {
            this._reportInvalidOther(d & 0xFF, this._inputPtr);
        }
        return (c << 6 | d & 0x3F) - 65536;
    }

    private final int _decodeChunkedUTF8_4(int c) throws IOException {
        int d = this._nextChunkedByte();
        if ((d & 0xC0) != 128) {
            this._reportInvalidOther(d & 0xFF, this._inputPtr);
        }
        c = (c & 7) << 6 | d & 0x3F;
        d = this._nextChunkedByte();
        if ((d & 0xC0) != 128) {
            this._reportInvalidOther(d & 0xFF, this._inputPtr);
        }
        c = c << 6 | d & 0x3F;
        d = this._nextChunkedByte();
        if ((d & 0xC0) != 128) {
            this._reportInvalidOther(d & 0xFF, this._inputPtr);
        }
        return (c << 6 | d & 0x3F) - 65536;
    }

    protected boolean loadMore() throws IOException {
        if (this._inputStream != null) {
            this._currInputProcessed += (long)this._inputEnd;
            int count = this._inputStream.read(this._inputBuffer, 0, this._inputBuffer.length);
            if (count > 0) {
                this._inputPtr = 0;
                this._inputEnd = count;
                return true;
            }
            this._closeInput();
            if (count == 0) {
                throw new IOException("InputStream.read() returned 0 characters when trying to read " + this._inputBuffer.length + " bytes");
            }
        }
        return false;
    }

    protected void loadMoreGuaranteed() throws IOException {
        if (!this.loadMore()) {
            this._reportInvalidEOF();
        }
    }

    protected final void _loadToHaveAtLeast(int minAvailable) throws IOException {
        if (this._inputStream == null) {
            throw this._constructError("Needed to read " + minAvailable + " bytes, reached end-of-input");
        }
        int amount = this._inputEnd - this._inputPtr;
        if (amount > 0 && this._inputPtr > 0) {
            System.arraycopy(this._inputBuffer, this._inputPtr, this._inputBuffer, 0, amount);
            this._inputEnd = amount;
        } else {
            this._inputEnd = 0;
        }
        this._currInputProcessed += (long)this._inputPtr;
        this._inputPtr = 0;
        while (this._inputEnd < minAvailable) {
            int count = this._inputStream.read(this._inputBuffer, this._inputEnd, this._inputBuffer.length - this._inputEnd);
            if (count < 1) {
                this._closeInput();
                if (count == 0) {
                    throw new IOException("InputStream.read() returned 0 characters when trying to read " + amount + " bytes");
                }
                throw this._constructError("Needed to read " + minAvailable + " bytes, missed " + minAvailable + " before end-of-input");
            }
            this._inputEnd += count;
        }
    }

    protected final boolean _tryToLoadToHaveAtLeast(int minAvailable) throws IOException {
        if (this._inputStream == null) {
            return false;
        }
        int amount = this._inputEnd - this._inputPtr;
        if (amount > 0 && this._inputPtr > 0) {
            System.arraycopy(this._inputBuffer, this._inputPtr, this._inputBuffer, 0, amount);
            this._inputEnd = amount;
        } else {
            this._inputEnd = 0;
        }
        this._currInputProcessed += (long)this._inputPtr;
        this._inputPtr = 0;
        while (this._inputEnd < minAvailable) {
            int count = this._inputStream.read(this._inputBuffer, this._inputEnd, this._inputBuffer.length - this._inputEnd);
            if (count < 1) {
                this._closeInput();
                return false;
            }
            this._inputEnd += count;
        }
        return true;
    }

    protected ByteArrayBuilder _getByteArrayBuilder() {
        if (this._byteArrayBuilder == null) {
            this._byteArrayBuilder = new ByteArrayBuilder();
        } else {
            this._byteArrayBuilder.reset();
        }
        return this._byteArrayBuilder;
    }

    protected void _closeInput() throws IOException {
        if (this._inputStream != null) {
            if (this._ioContext.isResourceManaged() || this.isEnabled(JsonParser.Feature.AUTO_CLOSE_SOURCE)) {
                this._inputStream.close();
            }
            this._inputStream = null;
        }
    }

    @Override
    protected void _handleEOF() throws JsonParseException {
        String startLocDesc;
        if (this._streamReadContext.inRoot()) {
            return;
        }
        JsonLocation loc = this._streamReadContext.startLocation(this._ioContext.contentReference());
        String string = startLocDesc = loc == null ? "[N/A]" : loc.sourceDescription();
        if (this._streamReadContext.hasExpectedLength()) {
            int expMore = this._streamReadContext.getRemainingExpectedLength();
            if (this._streamReadContext.inArray()) {
                this._reportInvalidEOF(String.format(" in Array value: expected %d more elements (start token at %s)", expMore, startLocDesc), null);
            } else {
                this._reportInvalidEOF(String.format(" in Object value: expected %d more properties (start token at %s)", expMore, startLocDesc), null);
            }
        } else if (this._streamReadContext.inArray()) {
            this._reportInvalidEOF(String.format(" in Array value: expected an element or close marker (0xFF) (start token at %s)", startLocDesc), null);
        } else {
            this._reportInvalidEOF(String.format(" in Object value: expected a property or close marker (0xFF) (start token at %s)", startLocDesc), null);
        }
    }

    protected JsonToken _eofAsNextToken() throws IOException {
        this._tagValue = -1;
        this.close();
        this._handleEOF();
        this._currToken = null;
        return null;
    }

    protected void _invalidToken(int ch) throws JsonParseException {
        if ((ch &= 0xFF) == 255) {
            throw this._constructError("Mismatched BREAK byte (0xFF): encountered where value expected");
        }
        throw this._constructError("Invalid CBOR value token (first byte): 0x" + Integer.toHexString(ch));
    }

    protected void _reportUnexpectedBreak() throws IOException {
        if (this._streamReadContext.inRoot()) {
            throw this._constructError("Unexpected Break (0xFF) token in Root context");
        }
        throw this._constructError("Unexpected Break (0xFF) token in definite length (" + this._streamReadContext.getExpectedLength() + ") " + (this._streamReadContext.inObject() ? "Object" : "Array"));
    }

    protected void _reportInvalidChar(int c) throws JsonParseException {
        if (c < 32) {
            this._throwInvalidSpace(c);
        }
        this._reportInvalidInitial(c);
    }

    protected void _reportInvalidInitial(int mask) throws JsonParseException {
        this._reportError("Invalid UTF-8 start byte 0x" + Integer.toHexString(mask));
    }

    protected void _reportInvalidOther(int mask) throws JsonParseException {
        this._reportError("Invalid UTF-8 middle byte 0x" + Integer.toHexString(mask));
    }

    protected void _reportInvalidOther(int mask, int ptr) throws JsonParseException {
        this._inputPtr = ptr;
        this._reportInvalidOther(mask);
    }

    protected void _reportIncompleteBinaryRead(int expLen, int actLen) throws IOException {
        this._reportInvalidEOF(String.format(" for Binary value: expected %d bytes, only found %d", expLen, actLen), this._currToken);
    }

    private String _reportTruncatedUTF8InName(int strLenBytes, int truncatedCharOffset, int firstUTFByteValue, int bytesExpected) throws IOException {
        throw this._constructReadException(String.format("Truncated UTF-8 character in Map key (%d bytes): byte 0x%02X at offset #%d indicated %d more bytes needed", strLenBytes, firstUTFByteValue, truncatedCharOffset, bytesExpected));
    }

    private final BigInteger _bigPositive(long l) {
        BigInteger biggie = BigInteger.valueOf(l << 1 >>> 1);
        return biggie.or(BIT_63);
    }

    private final BigInteger _bigNegative(long l) {
        BigInteger unsignedBase = this._bigPositive(l);
        return unsignedBase.negate().subtract(BigInteger.ONE);
    }

    public static enum Feature implements FormatFeature
    {

        final boolean _defaultState;
        final int _mask;

        public static int collectDefaults() {
            int flags = 0;
            for (Feature f : Feature.values()) {
                if (!f.enabledByDefault()) continue;
                flags |= f.getMask();
            }
            return flags;
        }

        private Feature(boolean defaultState) {
            this._defaultState = defaultState;
            this._mask = 1 << this.ordinal();
        }

        @Override
        public boolean enabledByDefault() {
            return this._defaultState;
        }

        @Override
        public int getMask() {
            return this._mask;
        }

        @Override
        public boolean enabledIn(int flags) {
            return (flags & this._mask) != 0;
        }
    }
}

