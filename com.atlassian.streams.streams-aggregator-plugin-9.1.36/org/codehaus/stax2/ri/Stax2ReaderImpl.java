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

    public Object getFeature(String string) {
        return null;
    }

    public void setFeature(String string, Object object) {
    }

    public boolean isPropertySupported(String string) {
        return false;
    }

    public boolean setProperty(String string, Object object) {
        return false;
    }

    public void skipElement() throws XMLStreamException {
        if (this.getEventType() != 1) {
            this.throwNotStartElem();
        }
        int n = 1;
        while (true) {
            int n2;
            if ((n2 = this.next()) == 1) {
                ++n;
                continue;
            }
            if (n2 == 2 && --n == 0) break;
        }
    }

    public AttributeInfo getAttributeInfo() throws XMLStreamException {
        if (this.getEventType() != 1) {
            this.throwNotStartElem();
        }
        return this;
    }

    public DTDInfo getDTDInfo() throws XMLStreamException {
        if (this.getEventType() != 11) {
            return null;
        }
        return this;
    }

    public final LocationInfo getLocationInfo() {
        return this;
    }

    public int getText(Writer writer, boolean bl) throws IOException, XMLStreamException {
        char[] cArray = this.getTextCharacters();
        int n = this.getTextStart();
        int n2 = this.getTextLength();
        if (n2 > 0) {
            writer.write(cArray, n, n2);
        }
        return n2;
    }

    public abstract int getDepth();

    public abstract boolean isEmptyElement() throws XMLStreamException;

    public abstract NamespaceContext getNonTransientNamespaceContext();

    public String getPrefixedName() {
        switch (this.getEventType()) {
            case 1: 
            case 2: {
                String string = this.getPrefix();
                String string2 = this.getLocalName();
                if (string == null) {
                    return string2;
                }
                StringBuffer stringBuffer = new StringBuffer(string2.length() + 1 + string.length());
                stringBuffer.append(string);
                stringBuffer.append(':');
                stringBuffer.append(string2);
                return stringBuffer.toString();
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

    public void closeCompletely() throws XMLStreamException {
        this.close();
    }

    public int findAttributeIndex(String string, String string2) {
        return -1;
    }

    public int getIdAttributeIndex() {
        return -1;
    }

    public int getNotationAttributeIndex() {
        return -1;
    }

    public Object getProcessedDTD() {
        return null;
    }

    public String getDTDRootName() {
        return null;
    }

    public String getDTDPublicId() {
        return null;
    }

    public String getDTDSystemId() {
        return null;
    }

    public String getDTDInternalSubset() {
        return null;
    }

    public DTDValidationSchema getProcessedDTDSchema() {
        return null;
    }

    public long getStartingByteOffset() {
        return -1L;
    }

    public long getStartingCharOffset() {
        return 0L;
    }

    public long getEndingByteOffset() throws XMLStreamException {
        return -1L;
    }

    public long getEndingCharOffset() throws XMLStreamException {
        return -1L;
    }

    public abstract XMLStreamLocation2 getStartLocation();

    public abstract XMLStreamLocation2 getCurrentLocation();

    public abstract XMLStreamLocation2 getEndLocation() throws XMLStreamException;

    public XMLValidator validateAgainst(XMLValidationSchema xMLValidationSchema) throws XMLStreamException {
        this.throwUnsupported();
        return null;
    }

    public XMLValidator stopValidatingAgainst(XMLValidationSchema xMLValidationSchema) throws XMLStreamException {
        this.throwUnsupported();
        return null;
    }

    public XMLValidator stopValidatingAgainst(XMLValidator xMLValidator) throws XMLStreamException {
        this.throwUnsupported();
        return null;
    }

    public abstract ValidationProblemHandler setValidationProblemHandler(ValidationProblemHandler var1);

    public boolean getElementAsBoolean() throws XMLStreamException {
        ValueDecoderFactory.BooleanDecoder booleanDecoder = this._decoderFactory().getBooleanDecoder();
        this.getElementAs(booleanDecoder);
        return booleanDecoder.getValue();
    }

    public int getElementAsInt() throws XMLStreamException {
        ValueDecoderFactory.IntDecoder intDecoder = this._decoderFactory().getIntDecoder();
        this.getElementAs(intDecoder);
        return intDecoder.getValue();
    }

    public long getElementAsLong() throws XMLStreamException {
        ValueDecoderFactory.LongDecoder longDecoder = this._decoderFactory().getLongDecoder();
        this.getElementAs(longDecoder);
        return longDecoder.getValue();
    }

    public float getElementAsFloat() throws XMLStreamException {
        ValueDecoderFactory.FloatDecoder floatDecoder = this._decoderFactory().getFloatDecoder();
        this.getElementAs(floatDecoder);
        return floatDecoder.getValue();
    }

    public double getElementAsDouble() throws XMLStreamException {
        ValueDecoderFactory.DoubleDecoder doubleDecoder = this._decoderFactory().getDoubleDecoder();
        this.getElementAs(doubleDecoder);
        return doubleDecoder.getValue();
    }

    public BigInteger getElementAsInteger() throws XMLStreamException {
        ValueDecoderFactory.IntegerDecoder integerDecoder = this._decoderFactory().getIntegerDecoder();
        this.getElementAs(integerDecoder);
        return integerDecoder.getValue();
    }

    public BigDecimal getElementAsDecimal() throws XMLStreamException {
        ValueDecoderFactory.DecimalDecoder decimalDecoder = this._decoderFactory().getDecimalDecoder();
        this.getElementAs(decimalDecoder);
        return decimalDecoder.getValue();
    }

    public QName getElementAsQName() throws XMLStreamException {
        ValueDecoderFactory.QNameDecoder qNameDecoder = this._decoderFactory().getQNameDecoder(this.getNamespaceContext());
        this.getElementAs(qNameDecoder);
        return qNameDecoder.getValue();
    }

    public byte[] getElementAsBinary() throws XMLStreamException {
        return this.getElementAsBinary(Base64Variants.getDefaultVariant());
    }

    public abstract byte[] getElementAsBinary(Base64Variant var1) throws XMLStreamException;

    public void getElementAs(TypedValueDecoder typedValueDecoder) throws XMLStreamException {
        String string = this.getElementText();
        try {
            typedValueDecoder.decode(string);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            throw this._constructTypeException(illegalArgumentException, string);
        }
    }

    public int readElementAsIntArray(int[] nArray, int n, int n2) throws XMLStreamException {
        return this.readElementAsArray(this._decoderFactory().getIntArrayDecoder(nArray, n, n2));
    }

    public int readElementAsLongArray(long[] lArray, int n, int n2) throws XMLStreamException {
        return this.readElementAsArray(this._decoderFactory().getLongArrayDecoder(lArray, n, n2));
    }

    public int readElementAsFloatArray(float[] fArray, int n, int n2) throws XMLStreamException {
        return this.readElementAsArray(this._decoderFactory().getFloatArrayDecoder(fArray, n, n2));
    }

    public int readElementAsDoubleArray(double[] dArray, int n, int n2) throws XMLStreamException {
        return this.readElementAsArray(this._decoderFactory().getDoubleArrayDecoder(dArray, n, n2));
    }

    public abstract int readElementAsArray(TypedArrayDecoder var1) throws XMLStreamException;

    public int readElementAsBinary(byte[] byArray, int n, int n2) throws XMLStreamException {
        return this.readElementAsBinary(Base64Variants.getDefaultVariant(), byArray, n, n2);
    }

    public abstract int readElementAsBinary(Base64Variant var1, byte[] var2, int var3, int var4) throws XMLStreamException;

    public abstract int getAttributeIndex(String var1, String var2);

    public boolean getAttributeAsBoolean(int n) throws XMLStreamException {
        ValueDecoderFactory.BooleanDecoder booleanDecoder = this._decoderFactory().getBooleanDecoder();
        this.getAttributeAs(n, booleanDecoder);
        return booleanDecoder.getValue();
    }

    public int getAttributeAsInt(int n) throws XMLStreamException {
        ValueDecoderFactory.IntDecoder intDecoder = this._decoderFactory().getIntDecoder();
        this.getAttributeAs(n, intDecoder);
        return intDecoder.getValue();
    }

    public long getAttributeAsLong(int n) throws XMLStreamException {
        ValueDecoderFactory.LongDecoder longDecoder = this._decoderFactory().getLongDecoder();
        this.getAttributeAs(n, longDecoder);
        return longDecoder.getValue();
    }

    public float getAttributeAsFloat(int n) throws XMLStreamException {
        ValueDecoderFactory.FloatDecoder floatDecoder = this._decoderFactory().getFloatDecoder();
        this.getAttributeAs(n, floatDecoder);
        return floatDecoder.getValue();
    }

    public double getAttributeAsDouble(int n) throws XMLStreamException {
        ValueDecoderFactory.DoubleDecoder doubleDecoder = this._decoderFactory().getDoubleDecoder();
        this.getAttributeAs(n, doubleDecoder);
        return doubleDecoder.getValue();
    }

    public BigInteger getAttributeAsInteger(int n) throws XMLStreamException {
        ValueDecoderFactory.IntegerDecoder integerDecoder = this._decoderFactory().getIntegerDecoder();
        this.getAttributeAs(n, integerDecoder);
        return integerDecoder.getValue();
    }

    public BigDecimal getAttributeAsDecimal(int n) throws XMLStreamException {
        ValueDecoderFactory.DecimalDecoder decimalDecoder = this._decoderFactory().getDecimalDecoder();
        this.getAttributeAs(n, decimalDecoder);
        return decimalDecoder.getValue();
    }

    public QName getAttributeAsQName(int n) throws XMLStreamException {
        ValueDecoderFactory.QNameDecoder qNameDecoder = this._decoderFactory().getQNameDecoder(this.getNamespaceContext());
        this.getAttributeAs(n, qNameDecoder);
        return qNameDecoder.getValue();
    }

    public void getAttributeAs(int n, TypedValueDecoder typedValueDecoder) throws XMLStreamException {
        String string = this.getAttributeValue(n);
        try {
            typedValueDecoder.decode(string);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            throw this._constructTypeException(illegalArgumentException, string);
        }
    }

    public int[] getAttributeAsIntArray(int n) throws XMLStreamException {
        ValueDecoderFactory.IntArrayDecoder intArrayDecoder = this._decoderFactory().getIntArrayDecoder();
        this.getAttributeAsArray(n, intArrayDecoder);
        return intArrayDecoder.getValues();
    }

    public long[] getAttributeAsLongArray(int n) throws XMLStreamException {
        ValueDecoderFactory.LongArrayDecoder longArrayDecoder = this._decoderFactory().getLongArrayDecoder();
        this.getAttributeAsArray(n, longArrayDecoder);
        return longArrayDecoder.getValues();
    }

    public float[] getAttributeAsFloatArray(int n) throws XMLStreamException {
        ValueDecoderFactory.FloatArrayDecoder floatArrayDecoder = this._decoderFactory().getFloatArrayDecoder();
        this.getAttributeAsArray(n, floatArrayDecoder);
        return floatArrayDecoder.getValues();
    }

    public double[] getAttributeAsDoubleArray(int n) throws XMLStreamException {
        ValueDecoderFactory.DoubleArrayDecoder doubleArrayDecoder = this._decoderFactory().getDoubleArrayDecoder();
        this.getAttributeAsArray(n, doubleArrayDecoder);
        return doubleArrayDecoder.getValues();
    }

    public abstract int getAttributeAsArray(int var1, TypedArrayDecoder var2) throws XMLStreamException;

    public byte[] getAttributeAsBinary(int n) throws XMLStreamException {
        return this.getAttributeAsBinary(Base64Variants.getDefaultVariant(), n);
    }

    public abstract byte[] getAttributeAsBinary(Base64Variant var1, int var2) throws XMLStreamException;

    protected ValueDecoderFactory _decoderFactory() {
        if (this._decoderFactory == null) {
            this._decoderFactory = new ValueDecoderFactory();
        }
        return this._decoderFactory;
    }

    protected TypedXMLStreamException _constructTypeException(IllegalArgumentException illegalArgumentException, String string) {
        return new TypedXMLStreamException(string, illegalArgumentException.getMessage(), this.getStartLocation(), illegalArgumentException);
    }

    protected void throwUnsupported() throws XMLStreamException {
        throw new XMLStreamException("Unsupported method");
    }

    protected void throwNotStartElem() {
        throw new IllegalStateException("Current state not START_ELEMENT");
    }
}

