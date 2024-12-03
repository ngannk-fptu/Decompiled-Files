/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.sr;

import com.ctc.wstx.api.ReaderConfig;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.io.BranchingReaderSource;
import com.ctc.wstx.io.InputBootstrapper;
import com.ctc.wstx.io.WstxInputData;
import com.ctc.wstx.sr.BasicStreamReader;
import com.ctc.wstx.sr.InputElementStack;
import com.ctc.wstx.sr.ReaderCreator;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.ri.Stax2Util;
import org.codehaus.stax2.ri.typed.CharArrayBase64Decoder;
import org.codehaus.stax2.ri.typed.ValueDecoderFactory;
import org.codehaus.stax2.typed.Base64Variant;
import org.codehaus.stax2.typed.Base64Variants;
import org.codehaus.stax2.typed.TypedArrayDecoder;
import org.codehaus.stax2.typed.TypedValueDecoder;
import org.codehaus.stax2.typed.TypedXMLStreamException;

public class TypedStreamReader
extends BasicStreamReader {
    protected static final int MASK_TYPED_ACCESS_ARRAY = 4182;
    protected static final int MASK_TYPED_ACCESS_BINARY = 4178;
    static final int MIN_BINARY_CHUNK = 2000;
    protected ValueDecoderFactory _decoderFactory;
    protected CharArrayBase64Decoder _base64Decoder = null;

    protected TypedStreamReader(InputBootstrapper bs, BranchingReaderSource input, ReaderCreator owner, ReaderConfig cfg, InputElementStack elemStack, boolean forER) throws XMLStreamException {
        super(bs, input, owner, cfg, elemStack, forER);
    }

    public static TypedStreamReader createStreamReader(BranchingReaderSource input, ReaderCreator owner, ReaderConfig cfg, InputBootstrapper bs, boolean forER) throws XMLStreamException {
        TypedStreamReader sr = new TypedStreamReader(bs, input, owner, cfg, TypedStreamReader.createElementStack(cfg), forER);
        return sr;
    }

    public boolean getElementAsBoolean() throws XMLStreamException {
        ValueDecoderFactory.BooleanDecoder dec = this._decoderFactory().getBooleanDecoder();
        this.getElementAs(dec);
        return dec.getValue();
    }

    public int getElementAsInt() throws XMLStreamException {
        ValueDecoderFactory.IntDecoder dec = this._decoderFactory().getIntDecoder();
        this.getElementAs(dec);
        return dec.getValue();
    }

    public long getElementAsLong() throws XMLStreamException {
        ValueDecoderFactory.LongDecoder dec = this._decoderFactory().getLongDecoder();
        this.getElementAs(dec);
        return dec.getValue();
    }

    public float getElementAsFloat() throws XMLStreamException {
        ValueDecoderFactory.FloatDecoder dec = this._decoderFactory().getFloatDecoder();
        this.getElementAs(dec);
        return dec.getValue();
    }

    public double getElementAsDouble() throws XMLStreamException {
        ValueDecoderFactory.DoubleDecoder dec = this._decoderFactory().getDoubleDecoder();
        this.getElementAs(dec);
        return dec.getValue();
    }

    public BigInteger getElementAsInteger() throws XMLStreamException {
        ValueDecoderFactory.IntegerDecoder dec = this._decoderFactory().getIntegerDecoder();
        this.getElementAs(dec);
        return dec.getValue();
    }

    public BigDecimal getElementAsDecimal() throws XMLStreamException {
        ValueDecoderFactory.DecimalDecoder dec = this._decoderFactory().getDecimalDecoder();
        this.getElementAs(dec);
        return dec.getValue();
    }

    public QName getElementAsQName() throws XMLStreamException {
        ValueDecoderFactory.QNameDecoder dec = this._decoderFactory().getQNameDecoder(this.getNamespaceContext());
        this.getElementAs(dec);
        return this._verifyQName(dec.getValue());
    }

    public final byte[] getElementAsBinary() throws XMLStreamException {
        return this.getElementAsBinary(Base64Variants.getDefaultVariant());
    }

    public byte[] getElementAsBinary(Base64Variant v) throws XMLStreamException {
        Stax2Util.ByteAggregator aggr = this._base64Decoder().getByteAggregator();
        byte[] buffer = aggr.startAggregation();
        while (true) {
            int readCount;
            int offset = 0;
            int len = buffer.length;
            do {
                if ((readCount = this.readElementAsBinary(buffer, offset, len, v)) < 1) {
                    return aggr.aggregateAll(buffer, offset);
                }
                offset += readCount;
            } while ((len -= readCount) > 0);
            buffer = aggr.addFullBlock(buffer);
        }
    }

    public void getElementAs(TypedValueDecoder tvd) throws XMLStreamException {
        int type;
        int type2;
        if (this.mCurrToken != 1) {
            this.throwParseError(ErrorConsts.ERR_STATE_NOT_STELEM);
        }
        if (this.mStEmptyElem) {
            this.mStEmptyElem = false;
            this.mCurrToken = 2;
            this._handleEmptyValue(tvd);
            return;
        }
        do {
            if ((type2 = this.next()) != 2) continue;
            this._handleEmptyValue(tvd);
            return;
        } while (type2 == 5 || type2 == 3);
        if ((1 << type2 & 0x1250) == 0) {
            this.throwParseError("Expected a text token, got " + this.tokenTypeDesc(type2) + ".");
        }
        if (this.mTokenState < 3) {
            this.readCoalescedText(this.mCurrToken, false);
        }
        if (this.mInputPtr + 1 < this.mInputEnd && this.mInputBuffer[this.mInputPtr] == '<' && this.mInputBuffer[this.mInputPtr + 1] == '/') {
            this.mInputPtr += 2;
            this.mCurrToken = 2;
            try {
                this.mTextBuffer.decode(tvd);
            }
            catch (IllegalArgumentException iae) {
                throw this._constructTypeException(iae, this.mTextBuffer.contentsAsString());
            }
            this.readEndElem();
            return;
        }
        int extra = 1 + (this.mTextBuffer.size() >> 1);
        StringBuffer sb = this.mTextBuffer.contentsAsStringBuffer(extra);
        while ((type = this.next()) != 2) {
            if ((1 << type & 0x1250) != 0) {
                if (this.mTokenState < 3) {
                    this.readCoalescedText(type, false);
                }
                this.mTextBuffer.contentsToStringBuffer(sb);
                continue;
            }
            if (type == 5 || type == 3) continue;
            this.throwParseError("Expected a text token, got " + this.tokenTypeDesc(type) + ".");
        }
        String str = sb.toString();
        String tstr = Stax2Util.trimSpaces(str);
        if (tstr == null) {
            this._handleEmptyValue(tvd);
        } else {
            try {
                tvd.decode(tstr);
            }
            catch (IllegalArgumentException iae) {
                throw this._constructTypeException(iae, str);
            }
        }
    }

    public int readElementAsIntArray(int[] value, int from, int length) throws XMLStreamException {
        return this.readElementAsArray(this._decoderFactory().getIntArrayDecoder(value, from, length));
    }

    public int readElementAsLongArray(long[] value, int from, int length) throws XMLStreamException {
        return this.readElementAsArray(this._decoderFactory().getLongArrayDecoder(value, from, length));
    }

    public int readElementAsFloatArray(float[] value, int from, int length) throws XMLStreamException {
        return this.readElementAsArray(this._decoderFactory().getFloatArrayDecoder(value, from, length));
    }

    public int readElementAsDoubleArray(double[] value, int from, int length) throws XMLStreamException {
        return this.readElementAsArray(this._decoderFactory().getDoubleArrayDecoder(value, from, length));
    }

    public final int readElementAsArray(TypedArrayDecoder dec) throws XMLStreamException {
        int type = this.mCurrToken;
        if ((1 << type & 0x1056) == 0) {
            this.throwNotTextualOrElem(type);
        }
        if (type == 1) {
            if (this.mStEmptyElem) {
                this.mStEmptyElem = false;
                this.mCurrToken = 2;
                return -1;
            }
            do {
                if ((type = this.next()) != 2) continue;
                return -1;
            } while (type == 5 || type == 3);
            if (type != 4 && type != 12) {
                throw this._constructUnexpectedInTyped(type);
            }
        }
        int count = 0;
        while (type != 2) {
            if (type == 4 || type == 12 || type == 6) {
                if (this.mTokenState < 3) {
                    this.readCoalescedText(type, false);
                }
            } else {
                if (type == 5 || type == 3) {
                    type = this.next();
                    continue;
                }
                throw this._constructUnexpectedInTyped(type);
            }
            count += this.mTextBuffer.decodeElements(dec, this);
            if (!dec.hasRoom()) break;
            type = this.next();
        }
        return count > 0 ? count : -1;
    }

    public final int readElementAsBinary(byte[] resultBuffer, int offset, int maxLength) throws XMLStreamException {
        return this.readElementAsBinary(resultBuffer, offset, maxLength, Base64Variants.getDefaultVariant());
    }

    public int readElementAsBinary(byte[] resultBuffer, int offset, int maxLength, Base64Variant v) throws XMLStreamException {
        if (resultBuffer == null) {
            throw new IllegalArgumentException("resultBuffer is null");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("Illegal offset (" + offset + "), must be [0, " + resultBuffer.length + "[");
        }
        if (maxLength < 1 || offset + maxLength > resultBuffer.length) {
            if (maxLength == 0) {
                return 0;
            }
            throw new IllegalArgumentException("Illegal maxLength (" + maxLength + "), has to be positive number, and offset+maxLength can not exceed" + resultBuffer.length);
        }
        CharArrayBase64Decoder dec = this._base64Decoder();
        int type = this.mCurrToken;
        if ((1 << type & 0x1052) == 0) {
            if (type == 2) {
                if (!dec.hasData()) {
                    return -1;
                }
            } else {
                this.throwNotTextualOrElem(type);
            }
        } else if (type == 1) {
            if (this.mStEmptyElem) {
                this.mStEmptyElem = false;
                this.mCurrToken = 2;
                return -1;
            }
            do {
                if ((type = this.next()) != 2) continue;
                return -1;
            } while (type == 5 || type == 3);
            if (this.mTokenState < this.mStTextThreshold) {
                this.finishToken(false);
            }
            this._initBinaryChunks(v, dec, type, true);
        }
        int totalCount = 0;
        while (true) {
            int count;
            try {
                count = dec.decode(resultBuffer, offset, maxLength);
            }
            catch (IllegalArgumentException iae) {
                throw this._constructTypeException(iae.getMessage(), "");
            }
            offset += count;
            totalCount += count;
            if ((maxLength -= count) < 1 || this.mCurrToken == 2) break;
            while ((type = this.next()) == 5 || type == 3 || type == 6) {
            }
            if (type == 2) {
                int left = dec.endOfContent();
                if (left < 0) {
                    throw this._constructTypeException("Incomplete base64 triplet at the end of decoded content", "");
                }
                if (left <= 0) break;
                continue;
            }
            if (this.mTokenState < this.mStTextThreshold) {
                this.finishToken(false);
            }
            this._initBinaryChunks(v, dec, type, false);
        }
        return totalCount > 0 ? totalCount : -1;
    }

    private final void _initBinaryChunks(Base64Variant v, CharArrayBase64Decoder dec, int type, boolean isFirst) throws XMLStreamException {
        if (type == 4) {
            if (this.mTokenState < this.mStTextThreshold) {
                this.mTokenState = this.readTextSecondary(2000, false) ? 3 : 2;
            }
        } else if (type == 12) {
            if (this.mTokenState < this.mStTextThreshold) {
                this.mTokenState = this.readCDataSecondary(2000) ? 3 : 2;
            }
        } else {
            throw this._constructUnexpectedInTyped(type);
        }
        this.mTextBuffer.initBinaryChunks(v, dec, isFirst);
    }

    public int getAttributeIndex(String namespaceURI, String localName) {
        if (this.mCurrToken != 1) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_STELEM);
        }
        return this.mElementStack.findAttributeIndex(namespaceURI, localName);
    }

    public boolean getAttributeAsBoolean(int index) throws XMLStreamException {
        ValueDecoderFactory.BooleanDecoder dec = this._decoderFactory().getBooleanDecoder();
        this.getAttributeAs(index, dec);
        return dec.getValue();
    }

    public int getAttributeAsInt(int index) throws XMLStreamException {
        ValueDecoderFactory.IntDecoder dec = this._decoderFactory().getIntDecoder();
        this.getAttributeAs(index, dec);
        return dec.getValue();
    }

    public long getAttributeAsLong(int index) throws XMLStreamException {
        ValueDecoderFactory.LongDecoder dec = this._decoderFactory().getLongDecoder();
        this.getAttributeAs(index, dec);
        return dec.getValue();
    }

    public float getAttributeAsFloat(int index) throws XMLStreamException {
        ValueDecoderFactory.FloatDecoder dec = this._decoderFactory().getFloatDecoder();
        this.getAttributeAs(index, dec);
        return dec.getValue();
    }

    public double getAttributeAsDouble(int index) throws XMLStreamException {
        ValueDecoderFactory.DoubleDecoder dec = this._decoderFactory().getDoubleDecoder();
        this.getAttributeAs(index, dec);
        return dec.getValue();
    }

    public BigInteger getAttributeAsInteger(int index) throws XMLStreamException {
        ValueDecoderFactory.IntegerDecoder dec = this._decoderFactory().getIntegerDecoder();
        this.getAttributeAs(index, dec);
        return dec.getValue();
    }

    public BigDecimal getAttributeAsDecimal(int index) throws XMLStreamException {
        ValueDecoderFactory.DecimalDecoder dec = this._decoderFactory().getDecimalDecoder();
        this.getAttributeAs(index, dec);
        return dec.getValue();
    }

    public QName getAttributeAsQName(int index) throws XMLStreamException {
        ValueDecoderFactory.QNameDecoder dec = this._decoderFactory().getQNameDecoder(this.getNamespaceContext());
        this.getAttributeAs(index, dec);
        return this._verifyQName(dec.getValue());
    }

    public void getAttributeAs(int index, TypedValueDecoder tvd) throws XMLStreamException {
        if (this.mCurrToken != 1) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_STELEM);
        }
        try {
            this.mAttrCollector.decodeValue(index, tvd);
        }
        catch (IllegalArgumentException iae) {
            throw this._constructTypeException(iae, this.mAttrCollector.getValue(index));
        }
    }

    public int[] getAttributeAsIntArray(int index) throws XMLStreamException {
        ValueDecoderFactory.IntArrayDecoder dec = this._decoderFactory().getIntArrayDecoder();
        this.getAttributeAsArray(index, dec);
        return dec.getValues();
    }

    public long[] getAttributeAsLongArray(int index) throws XMLStreamException {
        ValueDecoderFactory.LongArrayDecoder dec = this._decoderFactory().getLongArrayDecoder();
        this.getAttributeAsArray(index, dec);
        return dec.getValues();
    }

    public float[] getAttributeAsFloatArray(int index) throws XMLStreamException {
        ValueDecoderFactory.FloatArrayDecoder dec = this._decoderFactory().getFloatArrayDecoder();
        this.getAttributeAsArray(index, dec);
        return dec.getValues();
    }

    public double[] getAttributeAsDoubleArray(int index) throws XMLStreamException {
        ValueDecoderFactory.DoubleArrayDecoder dec = this._decoderFactory().getDoubleArrayDecoder();
        this.getAttributeAsArray(index, dec);
        return dec.getValues();
    }

    public int getAttributeAsArray(int index, TypedArrayDecoder tad) throws XMLStreamException {
        if (this.mCurrToken != 1) {
            throw new IllegalStateException(ErrorConsts.ERR_STATE_NOT_STELEM);
        }
        return this.mAttrCollector.decodeValues(index, tad, this);
    }

    public byte[] getAttributeAsBinary(int index) throws XMLStreamException {
        return this.getAttributeAsBinary(index, Base64Variants.getDefaultVariant());
    }

    public byte[] getAttributeAsBinary(int index, Base64Variant v) throws XMLStreamException {
        return this.mAttrCollector.decodeBinary(index, v, this._base64Decoder(), this);
    }

    protected QName _verifyQName(QName n) throws TypedXMLStreamException {
        String ln = n.getLocalPart();
        int ix = WstxInputData.findIllegalNameChar(ln, this.mCfgNsEnabled, this.mXml11);
        if (ix >= 0) {
            String prefix = n.getPrefix();
            String pname = prefix != null && prefix.length() > 0 ? prefix + ":" + ln : ln;
            throw this._constructTypeException("Invalid local name \"" + ln + "\" (character at #" + ix + " is invalid)", pname);
        }
        return n;
    }

    protected ValueDecoderFactory _decoderFactory() {
        if (this._decoderFactory == null) {
            this._decoderFactory = new ValueDecoderFactory();
        }
        return this._decoderFactory;
    }

    protected CharArrayBase64Decoder _base64Decoder() {
        if (this._base64Decoder == null) {
            this._base64Decoder = new CharArrayBase64Decoder();
        }
        return this._base64Decoder;
    }

    private void _handleEmptyValue(TypedValueDecoder dec) throws XMLStreamException {
        try {
            dec.handleEmptyValue();
        }
        catch (IllegalArgumentException iae) {
            throw this._constructTypeException(iae, "");
        }
    }

    protected TypedXMLStreamException _constructTypeException(IllegalArgumentException iae, String lexicalValue) {
        return new TypedXMLStreamException(lexicalValue, iae.getMessage(), this.getStartLocation(), iae);
    }
}

