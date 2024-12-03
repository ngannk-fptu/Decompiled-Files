/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.StreamReaderDelegate;
import org.codehaus.stax2.AttributeInfo;
import org.codehaus.stax2.DTDInfo;
import org.codehaus.stax2.LocationInfo;
import org.codehaus.stax2.XMLStreamLocation2;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.ri.Stax2LocationAdapter;
import org.codehaus.stax2.ri.Stax2Util;
import org.codehaus.stax2.ri.typed.StringBase64Decoder;
import org.codehaus.stax2.ri.typed.ValueDecoderFactory;
import org.codehaus.stax2.typed.Base64Variant;
import org.codehaus.stax2.typed.Base64Variants;
import org.codehaus.stax2.typed.TypedArrayDecoder;
import org.codehaus.stax2.typed.TypedValueDecoder;
import org.codehaus.stax2.typed.TypedXMLStreamException;
import org.codehaus.stax2.validation.DTDValidationSchema;
import org.codehaus.stax2.validation.ValidationProblemHandler;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidator;

public class Stax2ReaderAdapter
extends StreamReaderDelegate
implements XMLStreamReader2,
AttributeInfo,
DTDInfo,
LocationInfo {
    static final int INT_SPACE = 32;
    private static final int MASK_GET_ELEMENT_TEXT = 4688;
    protected static final int MASK_TYPED_ACCESS_BINARY = 4178;
    protected ValueDecoderFactory _decoderFactory;
    protected StringBase64Decoder _base64Decoder = null;
    protected int _depth = 0;
    protected String _typedContent;

    protected Stax2ReaderAdapter(XMLStreamReader sr) {
        super(sr);
    }

    public static XMLStreamReader2 wrapIfNecessary(XMLStreamReader sr) {
        if (sr instanceof XMLStreamReader2) {
            return (XMLStreamReader2)sr;
        }
        return new Stax2ReaderAdapter(sr);
    }

    @Override
    public int next() throws XMLStreamException {
        if (this._typedContent != null) {
            this._typedContent = null;
            return 2;
        }
        int type = super.next();
        if (type == 1) {
            ++this._depth;
        } else if (type == 2) {
            --this._depth;
        }
        return type;
    }

    @Override
    public String getElementText() throws XMLStreamException {
        boolean hadStart = this.getEventType() == 1;
        String text = super.getElementText();
        if (hadStart) {
            --this._depth;
        }
        return text;
    }

    @Override
    public boolean getElementAsBoolean() throws XMLStreamException {
        ValueDecoderFactory.BooleanDecoder dec = this._decoderFactory().getBooleanDecoder();
        this.getElementAs(dec);
        return dec.getValue();
    }

    @Override
    public int getElementAsInt() throws XMLStreamException {
        ValueDecoderFactory.IntDecoder dec = this._decoderFactory().getIntDecoder();
        this.getElementAs(dec);
        return dec.getValue();
    }

    @Override
    public long getElementAsLong() throws XMLStreamException {
        ValueDecoderFactory.LongDecoder dec = this._decoderFactory().getLongDecoder();
        this.getElementAs(dec);
        return dec.getValue();
    }

    @Override
    public float getElementAsFloat() throws XMLStreamException {
        ValueDecoderFactory.FloatDecoder dec = this._decoderFactory().getFloatDecoder();
        this.getElementAs(dec);
        return dec.getValue();
    }

    @Override
    public double getElementAsDouble() throws XMLStreamException {
        ValueDecoderFactory.DoubleDecoder dec = this._decoderFactory().getDoubleDecoder();
        this.getElementAs(dec);
        return dec.getValue();
    }

    @Override
    public BigInteger getElementAsInteger() throws XMLStreamException {
        ValueDecoderFactory.IntegerDecoder dec = this._decoderFactory().getIntegerDecoder();
        this.getElementAs(dec);
        return dec.getValue();
    }

    @Override
    public BigDecimal getElementAsDecimal() throws XMLStreamException {
        ValueDecoderFactory.DecimalDecoder dec = this._decoderFactory().getDecimalDecoder();
        this.getElementAs(dec);
        return dec.getValue();
    }

    @Override
    public QName getElementAsQName() throws XMLStreamException {
        ValueDecoderFactory.QNameDecoder dec = this._decoderFactory().getQNameDecoder(this.getNamespaceContext());
        this.getElementAs(dec);
        return dec.getValue();
    }

    @Override
    public byte[] getElementAsBinary() throws XMLStreamException {
        return this.getElementAsBinary(Base64Variants.getDefaultVariant());
    }

    @Override
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

    @Override
    public void getElementAs(TypedValueDecoder tvd) throws XMLStreamException {
        String value = this.getElementText();
        value = Stax2Util.trimSpaces(value);
        try {
            if (value == null) {
                tvd.handleEmptyValue();
            } else {
                tvd.decode(value);
            }
        }
        catch (IllegalArgumentException iae) {
            throw this._constructTypeException(iae, value);
        }
    }

    @Override
    public int readElementAsIntArray(int[] value, int from, int length) throws XMLStreamException {
        return this.readElementAsArray(this._decoderFactory().getIntArrayDecoder(value, from, length));
    }

    @Override
    public int readElementAsLongArray(long[] value, int from, int length) throws XMLStreamException {
        return this.readElementAsArray(this._decoderFactory().getLongArrayDecoder(value, from, length));
    }

    @Override
    public int readElementAsFloatArray(float[] value, int from, int length) throws XMLStreamException {
        return this.readElementAsArray(this._decoderFactory().getFloatArrayDecoder(value, from, length));
    }

    @Override
    public int readElementAsDoubleArray(double[] value, int from, int length) throws XMLStreamException {
        return this.readElementAsArray(this._decoderFactory().getDoubleArrayDecoder(value, from, length));
    }

    @Override
    public int readElementAsArray(TypedArrayDecoder tad) throws XMLStreamException {
        String string;
        int ptr;
        if (this._typedContent == null) {
            int type = this.getEventType();
            if (type == 2) {
                return -1;
            }
            if (type != 1) {
                throw new IllegalStateException("First call to readElementAsArray() must be for a START_ELEMENT");
            }
            this._typedContent = this.getElementText();
        }
        String input = this._typedContent;
        int end = input.length();
        int count = 0;
        String value = null;
        try {
            int len;
            block4: for (ptr = 0; ptr < end; ++ptr) {
                while (input.charAt(ptr) <= ' ') {
                    if (++ptr < end) continue;
                    break block4;
                }
                int start = ptr++;
                while (ptr < end && input.charAt(ptr) > ' ') {
                    ++ptr;
                }
                ++count;
                value = input.substring(start, ptr);
                if (!tad.decodeValue(value)) continue;
                break;
            }
            string = (len = end - ptr) < 1 ? null : input.substring(ptr);
        }
        catch (IllegalArgumentException iae) {
            try {
                Location loc = this.getLocation();
                throw new TypedXMLStreamException(value, iae.getMessage(), loc, iae);
            }
            catch (Throwable throwable) {
                int len = end - ptr;
                this._typedContent = len < 1 ? null : input.substring(ptr);
                throw throwable;
            }
        }
        this._typedContent = string;
        return count < 1 ? -1 : count;
    }

    @Override
    public int readElementAsBinary(byte[] resultBuffer, int offset, int maxLength) throws XMLStreamException {
        return this.readElementAsBinary(resultBuffer, offset, maxLength, Base64Variants.getDefaultVariant());
    }

    @Override
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
        StringBase64Decoder dec = this._base64Decoder();
        int type = this.getEventType();
        if ((1 << type & 0x1052) == 0) {
            if (type == 2) {
                if (!dec.hasData()) {
                    return -1;
                }
            } else {
                this.throwNotStartElemOrTextual(type);
            }
        }
        if (type == 1) {
            do {
                if ((type = this.next()) != 2) continue;
                return -1;
            } while (type == 5 || type == 3);
            if ((1 << type & 0x1250) == 0) {
                this.throwNotStartElemOrTextual(type);
            }
            dec.init(v, true, this.getText());
        }
        int totalCount = 0;
        while (true) {
            int count;
            try {
                count = dec.decode(resultBuffer, offset, maxLength);
            }
            catch (IllegalArgumentException iae) {
                throw this._constructTypeException(iae, "");
            }
            offset += count;
            totalCount += count;
            if ((maxLength -= count) < 1 || this.getEventType() == 2) break;
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
            if ((1 << type & 0x1250) == 0) {
                this.throwNotStartElemOrTextual(type);
            }
            dec.init(v, false, this.getText());
        }
        return totalCount > 0 ? totalCount : -1;
    }

    @Override
    public int getAttributeIndex(String namespaceURI, String localName) {
        return this.findAttributeIndex(namespaceURI, localName);
    }

    @Override
    public boolean getAttributeAsBoolean(int index) throws XMLStreamException {
        ValueDecoderFactory.BooleanDecoder dec = this._decoderFactory().getBooleanDecoder();
        this.getAttributeAs(index, dec);
        return dec.getValue();
    }

    @Override
    public int getAttributeAsInt(int index) throws XMLStreamException {
        ValueDecoderFactory.IntDecoder dec = this._decoderFactory().getIntDecoder();
        this.getAttributeAs(index, dec);
        return dec.getValue();
    }

    @Override
    public long getAttributeAsLong(int index) throws XMLStreamException {
        ValueDecoderFactory.LongDecoder dec = this._decoderFactory().getLongDecoder();
        this.getAttributeAs(index, dec);
        return dec.getValue();
    }

    @Override
    public float getAttributeAsFloat(int index) throws XMLStreamException {
        ValueDecoderFactory.FloatDecoder dec = this._decoderFactory().getFloatDecoder();
        this.getAttributeAs(index, dec);
        return dec.getValue();
    }

    @Override
    public double getAttributeAsDouble(int index) throws XMLStreamException {
        ValueDecoderFactory.DoubleDecoder dec = this._decoderFactory().getDoubleDecoder();
        this.getAttributeAs(index, dec);
        return dec.getValue();
    }

    @Override
    public BigInteger getAttributeAsInteger(int index) throws XMLStreamException {
        ValueDecoderFactory.IntegerDecoder dec = this._decoderFactory().getIntegerDecoder();
        this.getAttributeAs(index, dec);
        return dec.getValue();
    }

    @Override
    public BigDecimal getAttributeAsDecimal(int index) throws XMLStreamException {
        ValueDecoderFactory.DecimalDecoder dec = this._decoderFactory().getDecimalDecoder();
        this.getAttributeAs(index, dec);
        return dec.getValue();
    }

    @Override
    public QName getAttributeAsQName(int index) throws XMLStreamException {
        ValueDecoderFactory.QNameDecoder dec = this._decoderFactory().getQNameDecoder(this.getNamespaceContext());
        this.getAttributeAs(index, dec);
        return dec.getValue();
    }

    @Override
    public void getAttributeAs(int index, TypedValueDecoder tvd) throws XMLStreamException {
        String value = this.getAttributeValue(index);
        value = Stax2Util.trimSpaces(value);
        try {
            if (value == null) {
                tvd.handleEmptyValue();
            } else {
                tvd.decode(value);
            }
        }
        catch (IllegalArgumentException iae) {
            throw this._constructTypeException(iae, value);
        }
    }

    @Override
    public int[] getAttributeAsIntArray(int index) throws XMLStreamException {
        ValueDecoderFactory.IntArrayDecoder dec = this._decoderFactory().getIntArrayDecoder();
        this._getAttributeAsArray(dec, this.getAttributeValue(index));
        return dec.getValues();
    }

    @Override
    public long[] getAttributeAsLongArray(int index) throws XMLStreamException {
        ValueDecoderFactory.LongArrayDecoder dec = this._decoderFactory().getLongArrayDecoder();
        this._getAttributeAsArray(dec, this.getAttributeValue(index));
        return dec.getValues();
    }

    @Override
    public float[] getAttributeAsFloatArray(int index) throws XMLStreamException {
        ValueDecoderFactory.FloatArrayDecoder dec = this._decoderFactory().getFloatArrayDecoder();
        this._getAttributeAsArray(dec, this.getAttributeValue(index));
        return dec.getValues();
    }

    @Override
    public double[] getAttributeAsDoubleArray(int index) throws XMLStreamException {
        ValueDecoderFactory.DoubleArrayDecoder dec = this._decoderFactory().getDoubleArrayDecoder();
        this._getAttributeAsArray(dec, this.getAttributeValue(index));
        return dec.getValues();
    }

    @Override
    public int getAttributeAsArray(int index, TypedArrayDecoder tad) throws XMLStreamException {
        return this._getAttributeAsArray(tad, this.getAttributeValue(index));
    }

    protected int _getAttributeAsArray(TypedArrayDecoder tad, String attrValue) throws XMLStreamException {
        int count;
        block5: {
            int ptr = 0;
            int start = 0;
            int end = attrValue.length();
            String lexical = null;
            count = 0;
            try {
                while (ptr < end) {
                    while (attrValue.charAt(ptr) <= ' ') {
                        if (++ptr < end) continue;
                        break block5;
                    }
                    start = ptr++;
                    while (ptr < end && attrValue.charAt(ptr) > ' ') {
                        ++ptr;
                    }
                    int tokenEnd = ptr++;
                    lexical = attrValue.substring(start, tokenEnd);
                    ++count;
                    if (!tad.decodeValue(lexical) || this.checkExpand(tad)) continue;
                    break;
                }
            }
            catch (IllegalArgumentException iae) {
                Location loc = this.getLocation();
                throw new TypedXMLStreamException(lexical, iae.getMessage(), loc, iae);
            }
        }
        return count;
    }

    private final boolean checkExpand(TypedArrayDecoder tad) {
        if (tad instanceof ValueDecoderFactory.BaseArrayDecoder) {
            ((ValueDecoderFactory.BaseArrayDecoder)tad).expand();
            return true;
        }
        return false;
    }

    @Override
    public byte[] getAttributeAsBinary(int index) throws XMLStreamException {
        return this.getAttributeAsBinary(index, Base64Variants.getDefaultVariant());
    }

    @Override
    public byte[] getAttributeAsBinary(int index, Base64Variant v) throws XMLStreamException {
        String lexical = this.getAttributeValue(index);
        StringBase64Decoder dec = this._base64Decoder();
        dec.init(v, true, lexical);
        try {
            return dec.decodeCompletely();
        }
        catch (IllegalArgumentException iae) {
            throw new TypedXMLStreamException(lexical, iae.getMessage(), this.getLocation(), iae);
        }
    }

    @Override
    @Deprecated
    public Object getFeature(String name) {
        return null;
    }

    @Override
    @Deprecated
    public void setFeature(String name, Object value) {
    }

    @Override
    public boolean isPropertySupported(String name) {
        return false;
    }

    @Override
    public boolean setProperty(String name, Object value) {
        return false;
    }

    @Override
    public void skipElement() throws XMLStreamException {
        if (this.getEventType() != 1) {
            this.throwNotStartElem(this.getEventType());
        }
        int nesting = 1;
        while (true) {
            int type;
            if ((type = this.next()) == 1) {
                ++nesting;
                continue;
            }
            if (type == 2 && --nesting == 0) break;
        }
    }

    @Override
    public AttributeInfo getAttributeInfo() throws XMLStreamException {
        if (this.getEventType() != 1) {
            this.throwNotStartElem(this.getEventType());
        }
        return this;
    }

    @Override
    public DTDInfo getDTDInfo() throws XMLStreamException {
        if (this.getEventType() != 11) {
            return null;
        }
        return this;
    }

    @Override
    public final LocationInfo getLocationInfo() {
        return this;
    }

    @Override
    public int getText(Writer w, boolean preserveContents) throws IOException, XMLStreamException {
        char[] cbuf = this.getTextCharacters();
        int start = this.getTextStart();
        int len = this.getTextLength();
        if (len > 0) {
            w.write(cbuf, start, len);
        }
        return len;
    }

    @Override
    public int getDepth() {
        if (this.getEventType() == 2) {
            return this._depth + 1;
        }
        return this._depth;
    }

    @Override
    public boolean isEmptyElement() throws XMLStreamException {
        return false;
    }

    @Override
    public NamespaceContext getNonTransientNamespaceContext() {
        return null;
    }

    @Override
    public String getPrefixedName() {
        switch (this.getEventType()) {
            case 1: 
            case 2: {
                String prefix = this.getPrefix();
                String ln = this.getLocalName();
                if (prefix == null || prefix.length() == 0) {
                    return ln;
                }
                StringBuffer sb = new StringBuffer(ln.length() + 1 + prefix.length());
                sb.append(prefix);
                sb.append(':');
                sb.append(ln);
                return sb.toString();
            }
            case 9: {
                return this.getLocalName();
            }
            case 3: {
                return this.getPITarget();
            }
            case 11: {
                return this.getDTDRootName();
            }
        }
        throw new IllegalStateException("Current state not START_ELEMENT, END_ELEMENT, ENTITY_REFERENCE, PROCESSING_INSTRUCTION or DTD");
    }

    @Override
    public void closeCompletely() throws XMLStreamException {
        this.close();
    }

    @Override
    public int findAttributeIndex(String nsURI, String localName) {
        if ("".equals(nsURI)) {
            nsURI = null;
        }
        int len = this.getAttributeCount();
        for (int i = 0; i < len; ++i) {
            if (!this.getAttributeLocalName(i).equals(localName)) continue;
            String otherUri = this.getAttributeNamespace(i);
            if (!(nsURI == null ? otherUri == null || otherUri.length() == 0 : nsURI.equals(otherUri))) continue;
            return i;
        }
        return -1;
    }

    @Override
    public int getIdAttributeIndex() {
        int len = this.getAttributeCount();
        for (int i = 0; i < len; ++i) {
            if (!"ID".equals(this.getAttributeType(i))) continue;
            return i;
        }
        return -1;
    }

    @Override
    public int getNotationAttributeIndex() {
        int len = this.getAttributeCount();
        for (int i = 0; i < len; ++i) {
            if (!"NOTATION".equals(this.getAttributeType(i))) continue;
            return i;
        }
        return -1;
    }

    @Override
    public Object getProcessedDTD() {
        return null;
    }

    @Override
    public String getDTDRootName() {
        return null;
    }

    @Override
    public String getDTDPublicId() {
        return null;
    }

    @Override
    public String getDTDSystemId() {
        return null;
    }

    @Override
    public String getDTDInternalSubset() {
        if (this.getEventType() == 11) {
            return this.getText();
        }
        return null;
    }

    @Override
    public DTDValidationSchema getProcessedDTDSchema() {
        return null;
    }

    @Override
    public long getStartingByteOffset() {
        return -1L;
    }

    @Override
    public long getStartingCharOffset() {
        return 0L;
    }

    @Override
    public long getEndingByteOffset() throws XMLStreamException {
        return -1L;
    }

    @Override
    public long getEndingCharOffset() throws XMLStreamException {
        return -1L;
    }

    @Override
    public XMLStreamLocation2 getStartLocation() {
        return this.getCurrentLocation();
    }

    @Override
    public XMLStreamLocation2 getCurrentLocation() {
        return new Stax2LocationAdapter(this.getLocation());
    }

    @Override
    public final XMLStreamLocation2 getEndLocation() throws XMLStreamException {
        return this.getCurrentLocation();
    }

    @Override
    public XMLValidator validateAgainst(XMLValidationSchema schema) throws XMLStreamException {
        this.throwUnsupported();
        return null;
    }

    @Override
    public XMLValidator stopValidatingAgainst(XMLValidationSchema schema) throws XMLStreamException {
        this.throwUnsupported();
        return null;
    }

    @Override
    public XMLValidator stopValidatingAgainst(XMLValidator validator) throws XMLStreamException {
        this.throwUnsupported();
        return null;
    }

    @Override
    public ValidationProblemHandler setValidationProblemHandler(ValidationProblemHandler h) {
        return null;
    }

    protected ValueDecoderFactory _decoderFactory() {
        if (this._decoderFactory == null) {
            this._decoderFactory = new ValueDecoderFactory();
        }
        return this._decoderFactory;
    }

    protected StringBase64Decoder _base64Decoder() {
        if (this._base64Decoder == null) {
            this._base64Decoder = new StringBase64Decoder();
        }
        return this._base64Decoder;
    }

    protected void throwUnsupported() throws XMLStreamException {
        throw new XMLStreamException("Unsupported method");
    }

    protected void throwNotStartElem(int type) {
        throw new IllegalStateException("Current event (" + Stax2Util.eventTypeDesc(type) + ") not START_ELEMENT");
    }

    protected void throwNotStartElemOrTextual(int type) {
        throw new IllegalStateException("Current event (" + Stax2Util.eventTypeDesc(type) + ") not START_ELEMENT, END_ELEMENT, CHARACTERS or CDATA");
    }

    protected TypedXMLStreamException _constructTypeException(IllegalArgumentException iae, String lexicalValue) {
        XMLStreamLocation2 loc;
        String msg = iae.getMessage();
        if (msg == null) {
            msg = "";
        }
        if ((loc = this.getStartLocation()) == null) {
            return new TypedXMLStreamException(lexicalValue, msg, iae);
        }
        return new TypedXMLStreamException(lexicalValue, msg, loc, iae);
    }

    protected TypedXMLStreamException _constructTypeException(String msg, String lexicalValue) {
        XMLStreamLocation2 loc = this.getStartLocation();
        if (loc == null) {
            return new TypedXMLStreamException(lexicalValue, msg);
        }
        return new TypedXMLStreamException(lexicalValue, msg, loc);
    }
}

