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
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.AttributeInfo;
import org.codehaus.stax2.DTDInfo;
import org.codehaus.stax2.LocationInfo;
import org.codehaus.stax2.XMLStreamLocation2;
import org.codehaus.stax2.XMLStreamReader2;
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

public abstract class Stax2ReaderImpl
implements XMLStreamReader2,
AttributeInfo,
DTDInfo,
LocationInfo {
    protected ValueDecoderFactory _decoderFactory;

    protected Stax2ReaderImpl() {
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
            this.throwNotStartElem();
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
            this.throwNotStartElem();
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
    public abstract int getDepth();

    @Override
    public abstract boolean isEmptyElement() throws XMLStreamException;

    @Override
    public abstract NamespaceContext getNonTransientNamespaceContext();

    @Override
    public String getPrefixedName() {
        switch (this.getEventType()) {
            case 1: 
            case 2: {
                String prefix = this.getPrefix();
                String ln = this.getLocalName();
                if (prefix == null) {
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
        return -1;
    }

    @Override
    public int getIdAttributeIndex() {
        return -1;
    }

    @Override
    public int getNotationAttributeIndex() {
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
    public abstract XMLStreamLocation2 getStartLocation();

    @Override
    public abstract XMLStreamLocation2 getCurrentLocation();

    @Override
    public abstract XMLStreamLocation2 getEndLocation() throws XMLStreamException;

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
    public abstract ValidationProblemHandler setValidationProblemHandler(ValidationProblemHandler var1);

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
    public abstract byte[] getElementAsBinary(Base64Variant var1) throws XMLStreamException;

    @Override
    public void getElementAs(TypedValueDecoder tvd) throws XMLStreamException {
        String value = this.getElementText();
        try {
            tvd.decode(value);
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
    public abstract int readElementAsArray(TypedArrayDecoder var1) throws XMLStreamException;

    @Override
    public int readElementAsBinary(byte[] resultBuffer, int offset, int maxLength) throws XMLStreamException {
        return this.readElementAsBinary(Base64Variants.getDefaultVariant(), resultBuffer, offset, maxLength);
    }

    public abstract int readElementAsBinary(Base64Variant var1, byte[] var2, int var3, int var4) throws XMLStreamException;

    @Override
    public abstract int getAttributeIndex(String var1, String var2);

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
        try {
            tvd.decode(value);
        }
        catch (IllegalArgumentException iae) {
            throw this._constructTypeException(iae, value);
        }
    }

    @Override
    public int[] getAttributeAsIntArray(int index) throws XMLStreamException {
        ValueDecoderFactory.IntArrayDecoder dec = this._decoderFactory().getIntArrayDecoder();
        this.getAttributeAsArray(index, dec);
        return dec.getValues();
    }

    @Override
    public long[] getAttributeAsLongArray(int index) throws XMLStreamException {
        ValueDecoderFactory.LongArrayDecoder dec = this._decoderFactory().getLongArrayDecoder();
        this.getAttributeAsArray(index, dec);
        return dec.getValues();
    }

    @Override
    public float[] getAttributeAsFloatArray(int index) throws XMLStreamException {
        ValueDecoderFactory.FloatArrayDecoder dec = this._decoderFactory().getFloatArrayDecoder();
        this.getAttributeAsArray(index, dec);
        return dec.getValues();
    }

    @Override
    public double[] getAttributeAsDoubleArray(int index) throws XMLStreamException {
        ValueDecoderFactory.DoubleArrayDecoder dec = this._decoderFactory().getDoubleArrayDecoder();
        this.getAttributeAsArray(index, dec);
        return dec.getValues();
    }

    @Override
    public abstract int getAttributeAsArray(int var1, TypedArrayDecoder var2) throws XMLStreamException;

    @Override
    public byte[] getAttributeAsBinary(int index) throws XMLStreamException {
        return this.getAttributeAsBinary(Base64Variants.getDefaultVariant(), index);
    }

    public abstract byte[] getAttributeAsBinary(Base64Variant var1, int var2) throws XMLStreamException;

    protected ValueDecoderFactory _decoderFactory() {
        if (this._decoderFactory == null) {
            this._decoderFactory = new ValueDecoderFactory();
        }
        return this._decoderFactory;
    }

    protected TypedXMLStreamException _constructTypeException(IllegalArgumentException iae, String lexicalValue) {
        return new TypedXMLStreamException(lexicalValue, iae.getMessage(), this.getStartLocation(), iae);
    }

    protected void throwUnsupported() throws XMLStreamException {
        throw new XMLStreamException("Unsupported method");
    }

    protected void throwNotStartElem() {
        throw new IllegalStateException("Current state not START_ELEMENT");
    }
}

