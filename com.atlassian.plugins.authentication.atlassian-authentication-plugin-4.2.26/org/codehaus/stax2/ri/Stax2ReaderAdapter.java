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

    protected Stax2ReaderAdapter(XMLStreamReader xMLStreamReader) {
        super(xMLStreamReader);
    }

    public static XMLStreamReader2 wrapIfNecessary(XMLStreamReader xMLStreamReader) {
        if (xMLStreamReader instanceof XMLStreamReader2) {
            return (XMLStreamReader2)xMLStreamReader;
        }
        return new Stax2ReaderAdapter(xMLStreamReader);
    }

    public int next() throws XMLStreamException {
        if (this._typedContent != null) {
            this._typedContent = null;
            return 2;
        }
        int n = super.next();
        if (n == 1) {
            ++this._depth;
        } else if (n == 2) {
            --this._depth;
        }
        return n;
    }

    public String getElementText() throws XMLStreamException {
        boolean bl = this.getEventType() == 1;
        String string = super.getElementText();
        if (bl) {
            --this._depth;
        }
        return string;
    }

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

    public byte[] getElementAsBinary(Base64Variant base64Variant) throws XMLStreamException {
        Stax2Util.ByteAggregator byteAggregator = this._base64Decoder().getByteAggregator();
        byte[] byArray = byteAggregator.startAggregation();
        while (true) {
            int n;
            int n2 = 0;
            int n3 = byArray.length;
            do {
                if ((n = this.readElementAsBinary(byArray, n2, n3, base64Variant)) < 1) {
                    return byteAggregator.aggregateAll(byArray, n2);
                }
                n2 += n;
            } while ((n3 -= n) > 0);
            byArray = byteAggregator.addFullBlock(byArray);
        }
    }

    public void getElementAs(TypedValueDecoder typedValueDecoder) throws XMLStreamException {
        String string = this.getElementText();
        string = Stax2Util.trimSpaces(string);
        try {
            if (string == null) {
                typedValueDecoder.handleEmptyValue();
            } else {
                typedValueDecoder.decode(string);
            }
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

    public int readElementAsArray(TypedArrayDecoder typedArrayDecoder) throws XMLStreamException {
        String string;
        int n;
        if (this._typedContent == null) {
            int n2 = this.getEventType();
            if (n2 == 2) {
                return -1;
            }
            if (n2 != 1) {
                throw new IllegalStateException("First call to readElementAsArray() must be for a START_ELEMENT");
            }
            this._typedContent = this.getElementText();
        }
        String string2 = this._typedContent;
        int n3 = string2.length();
        int n4 = 0;
        String string3 = null;
        try {
            int n5;
            block4: for (n = 0; n < n3; ++n) {
                while (string2.charAt(n) <= ' ') {
                    if (++n < n3) continue;
                    break block4;
                }
                n5 = n++;
                while (n < n3 && string2.charAt(n) > ' ') {
                    ++n;
                }
                ++n4;
                string3 = string2.substring(n5, n);
                if (!typedArrayDecoder.decodeValue(string3)) continue;
                break;
            }
            string = (n5 = n3 - n) < 1 ? null : string2.substring(n);
        }
        catch (IllegalArgumentException illegalArgumentException) {
            try {
                Location location = this.getLocation();
                throw new TypedXMLStreamException(string3, illegalArgumentException.getMessage(), location, illegalArgumentException);
            }
            catch (Throwable throwable) {
                int n6 = n3 - n;
                this._typedContent = n6 < 1 ? null : string2.substring(n);
                throw throwable;
            }
        }
        this._typedContent = string;
        return n4 < 1 ? -1 : n4;
    }

    public int readElementAsBinary(byte[] byArray, int n, int n2) throws XMLStreamException {
        return this.readElementAsBinary(byArray, n, n2, Base64Variants.getDefaultVariant());
    }

    public int readElementAsBinary(byte[] byArray, int n, int n2, Base64Variant base64Variant) throws XMLStreamException {
        if (byArray == null) {
            throw new IllegalArgumentException("resultBuffer is null");
        }
        if (n < 0) {
            throw new IllegalArgumentException("Illegal offset (" + n + "), must be [0, " + byArray.length + "[");
        }
        if (n2 < 1 || n + n2 > byArray.length) {
            if (n2 == 0) {
                return 0;
            }
            throw new IllegalArgumentException("Illegal maxLength (" + n2 + "), has to be positive number, and offset+maxLength can not exceed" + byArray.length);
        }
        StringBase64Decoder stringBase64Decoder = this._base64Decoder();
        int n3 = this.getEventType();
        if ((1 << n3 & 0x1052) == 0) {
            if (n3 == 2) {
                if (!stringBase64Decoder.hasData()) {
                    return -1;
                }
            } else {
                this.throwNotStartElemOrTextual(n3);
            }
        }
        if (n3 == 1) {
            do {
                if ((n3 = this.next()) != 2) continue;
                return -1;
            } while (n3 == 5 || n3 == 3);
            if ((1 << n3 & 0x1250) == 0) {
                this.throwNotStartElemOrTextual(n3);
            }
            stringBase64Decoder.init(base64Variant, true, this.getText());
        }
        int n4 = 0;
        while (true) {
            int n5;
            try {
                n5 = stringBase64Decoder.decode(byArray, n, n2);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                throw this._constructTypeException(illegalArgumentException, "");
            }
            n += n5;
            n4 += n5;
            if ((n2 -= n5) < 1 || this.getEventType() == 2) break;
            while ((n3 = this.next()) == 5 || n3 == 3 || n3 == 6) {
            }
            if (n3 == 2) {
                int n6 = stringBase64Decoder.endOfContent();
                if (n6 < 0) {
                    throw this._constructTypeException("Incomplete base64 triplet at the end of decoded content", "");
                }
                if (n6 <= 0) break;
                continue;
            }
            if ((1 << n3 & 0x1250) == 0) {
                this.throwNotStartElemOrTextual(n3);
            }
            stringBase64Decoder.init(base64Variant, false, this.getText());
        }
        return n4 > 0 ? n4 : -1;
    }

    public int getAttributeIndex(String string, String string2) {
        return this.findAttributeIndex(string, string2);
    }

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
        string = Stax2Util.trimSpaces(string);
        try {
            if (string == null) {
                typedValueDecoder.handleEmptyValue();
            } else {
                typedValueDecoder.decode(string);
            }
        }
        catch (IllegalArgumentException illegalArgumentException) {
            throw this._constructTypeException(illegalArgumentException, string);
        }
    }

    public int[] getAttributeAsIntArray(int n) throws XMLStreamException {
        ValueDecoderFactory.IntArrayDecoder intArrayDecoder = this._decoderFactory().getIntArrayDecoder();
        this._getAttributeAsArray(intArrayDecoder, this.getAttributeValue(n));
        return intArrayDecoder.getValues();
    }

    public long[] getAttributeAsLongArray(int n) throws XMLStreamException {
        ValueDecoderFactory.LongArrayDecoder longArrayDecoder = this._decoderFactory().getLongArrayDecoder();
        this._getAttributeAsArray(longArrayDecoder, this.getAttributeValue(n));
        return longArrayDecoder.getValues();
    }

    public float[] getAttributeAsFloatArray(int n) throws XMLStreamException {
        ValueDecoderFactory.FloatArrayDecoder floatArrayDecoder = this._decoderFactory().getFloatArrayDecoder();
        this._getAttributeAsArray(floatArrayDecoder, this.getAttributeValue(n));
        return floatArrayDecoder.getValues();
    }

    public double[] getAttributeAsDoubleArray(int n) throws XMLStreamException {
        ValueDecoderFactory.DoubleArrayDecoder doubleArrayDecoder = this._decoderFactory().getDoubleArrayDecoder();
        this._getAttributeAsArray(doubleArrayDecoder, this.getAttributeValue(n));
        return doubleArrayDecoder.getValues();
    }

    public int getAttributeAsArray(int n, TypedArrayDecoder typedArrayDecoder) throws XMLStreamException {
        return this._getAttributeAsArray(typedArrayDecoder, this.getAttributeValue(n));
    }

    protected int _getAttributeAsArray(TypedArrayDecoder typedArrayDecoder, String string) throws XMLStreamException {
        int n;
        block5: {
            int n2 = 0;
            int n3 = 0;
            int n4 = string.length();
            String string2 = null;
            n = 0;
            try {
                while (n2 < n4) {
                    while (string.charAt(n2) <= ' ') {
                        if (++n2 < n4) continue;
                        break block5;
                    }
                    n3 = n2++;
                    while (n2 < n4 && string.charAt(n2) > ' ') {
                        ++n2;
                    }
                    int n5 = n2++;
                    string2 = string.substring(n3, n5);
                    ++n;
                    if (!typedArrayDecoder.decodeValue(string2) || this.checkExpand(typedArrayDecoder)) continue;
                    break;
                }
            }
            catch (IllegalArgumentException illegalArgumentException) {
                Location location = this.getLocation();
                throw new TypedXMLStreamException(string2, illegalArgumentException.getMessage(), location, illegalArgumentException);
            }
        }
        return n;
    }

    private final boolean checkExpand(TypedArrayDecoder typedArrayDecoder) {
        if (typedArrayDecoder instanceof ValueDecoderFactory.BaseArrayDecoder) {
            ((ValueDecoderFactory.BaseArrayDecoder)typedArrayDecoder).expand();
            return true;
        }
        return false;
    }

    public byte[] getAttributeAsBinary(int n) throws XMLStreamException {
        return this.getAttributeAsBinary(n, Base64Variants.getDefaultVariant());
    }

    public byte[] getAttributeAsBinary(int n, Base64Variant base64Variant) throws XMLStreamException {
        String string = this.getAttributeValue(n);
        StringBase64Decoder stringBase64Decoder = this._base64Decoder();
        stringBase64Decoder.init(base64Variant, true, string);
        try {
            return stringBase64Decoder.decodeCompletely();
        }
        catch (IllegalArgumentException illegalArgumentException) {
            throw new TypedXMLStreamException(string, illegalArgumentException.getMessage(), this.getLocation(), illegalArgumentException);
        }
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
            this.throwNotStartElem(this.getEventType());
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
            this.throwNotStartElem(this.getEventType());
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

    public int getDepth() {
        if (this.getEventType() == 2) {
            return this._depth + 1;
        }
        return this._depth;
    }

    public boolean isEmptyElement() throws XMLStreamException {
        return false;
    }

    public NamespaceContext getNonTransientNamespaceContext() {
        return null;
    }

    public String getPrefixedName() {
        switch (this.getEventType()) {
            case 1: 
            case 2: {
                String string = this.getPrefix();
                String string2 = this.getLocalName();
                if (string == null || string.length() == 0) {
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
        if ("".equals(string)) {
            string = null;
        }
        int n = this.getAttributeCount();
        for (int i = 0; i < n; ++i) {
            if (!this.getAttributeLocalName(i).equals(string2)) continue;
            String string3 = this.getAttributeNamespace(i);
            if (!(string == null ? string3 == null || string3.length() == 0 : string.equals(string3))) continue;
            return i;
        }
        return -1;
    }

    public int getIdAttributeIndex() {
        int n = this.getAttributeCount();
        for (int i = 0; i < n; ++i) {
            if (!"ID".equals(this.getAttributeType(i))) continue;
            return i;
        }
        return -1;
    }

    public int getNotationAttributeIndex() {
        int n = this.getAttributeCount();
        for (int i = 0; i < n; ++i) {
            if (!"NOTATION".equals(this.getAttributeType(i))) continue;
            return i;
        }
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
        if (this.getEventType() == 11) {
            return this.getText();
        }
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

    public XMLStreamLocation2 getStartLocation() {
        return this.getCurrentLocation();
    }

    public XMLStreamLocation2 getCurrentLocation() {
        return new Stax2LocationAdapter(this.getLocation());
    }

    public final XMLStreamLocation2 getEndLocation() throws XMLStreamException {
        return this.getCurrentLocation();
    }

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

    public ValidationProblemHandler setValidationProblemHandler(ValidationProblemHandler validationProblemHandler) {
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

    protected void throwNotStartElem(int n) {
        throw new IllegalStateException("Current event (" + Stax2Util.eventTypeDesc(n) + ") not START_ELEMENT");
    }

    protected void throwNotStartElemOrTextual(int n) {
        throw new IllegalStateException("Current event (" + Stax2Util.eventTypeDesc(n) + ") not START_ELEMENT, END_ELEMENT, CHARACTERS or CDATA");
    }

    protected TypedXMLStreamException _constructTypeException(IllegalArgumentException illegalArgumentException, String string) {
        XMLStreamLocation2 xMLStreamLocation2;
        String string2 = illegalArgumentException.getMessage();
        if (string2 == null) {
            string2 = "";
        }
        if ((xMLStreamLocation2 = this.getStartLocation()) == null) {
            return new TypedXMLStreamException(string, string2, illegalArgumentException);
        }
        return new TypedXMLStreamException(string, string2, xMLStreamLocation2, illegalArgumentException);
    }

    protected TypedXMLStreamException _constructTypeException(String string, String string2) {
        XMLStreamLocation2 xMLStreamLocation2 = this.getStartLocation();
        if (xMLStreamLocation2 == null) {
            return new TypedXMLStreamException(string2, string);
        }
        return new TypedXMLStreamException(string2, string, xMLStreamLocation2);
    }
}

