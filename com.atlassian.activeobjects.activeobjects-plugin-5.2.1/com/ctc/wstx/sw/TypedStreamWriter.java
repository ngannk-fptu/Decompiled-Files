/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.sw;

import com.ctc.wstx.api.WriterConfig;
import com.ctc.wstx.cfg.ErrorConsts;
import com.ctc.wstx.exc.WstxIOException;
import com.ctc.wstx.sw.BaseStreamWriter;
import com.ctc.wstx.sw.XmlWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.ri.typed.AsciiValueEncoder;
import org.codehaus.stax2.ri.typed.ValueEncoderFactory;
import org.codehaus.stax2.typed.Base64Variant;
import org.codehaus.stax2.typed.Base64Variants;
import org.codehaus.stax2.validation.XMLValidator;

public abstract class TypedStreamWriter
extends BaseStreamWriter {
    protected ValueEncoderFactory mValueEncoderFactory;

    protected TypedStreamWriter(XmlWriter xw, String enc, WriterConfig cfg) {
        super(xw, enc, cfg);
    }

    protected final ValueEncoderFactory valueEncoderFactory() {
        if (this.mValueEncoderFactory == null) {
            this.mValueEncoderFactory = new ValueEncoderFactory();
        }
        return this.mValueEncoderFactory;
    }

    public void writeBoolean(boolean value) throws XMLStreamException {
        this.writeTypedElement(this.valueEncoderFactory().getEncoder(value));
    }

    public void writeInt(int value) throws XMLStreamException {
        this.writeTypedElement(this.valueEncoderFactory().getEncoder(value));
    }

    public void writeLong(long value) throws XMLStreamException {
        this.writeTypedElement(this.valueEncoderFactory().getEncoder(value));
    }

    public void writeFloat(float value) throws XMLStreamException {
        this.writeTypedElement(this.valueEncoderFactory().getEncoder(value));
    }

    public void writeDouble(double value) throws XMLStreamException {
        this.writeTypedElement(this.valueEncoderFactory().getEncoder(value));
    }

    public void writeInteger(BigInteger value) throws XMLStreamException {
        this.writeTypedElement(this.valueEncoderFactory().getScalarEncoder(value.toString()));
    }

    public void writeDecimal(BigDecimal value) throws XMLStreamException {
        this.writeTypedElement(this.valueEncoderFactory().getScalarEncoder(value.toString()));
    }

    public void writeQName(QName name) throws XMLStreamException {
        this.writeCharacters(this.serializeQName(name));
    }

    public final void writeIntArray(int[] value, int from, int length) throws XMLStreamException {
        this.writeTypedElement(this.valueEncoderFactory().getEncoder(value, from, length));
    }

    public void writeLongArray(long[] value, int from, int length) throws XMLStreamException {
        this.writeTypedElement(this.valueEncoderFactory().getEncoder(value, from, length));
    }

    public void writeFloatArray(float[] value, int from, int length) throws XMLStreamException {
        this.writeTypedElement(this.valueEncoderFactory().getEncoder(value, from, length));
    }

    public void writeDoubleArray(double[] value, int from, int length) throws XMLStreamException {
        this.writeTypedElement(this.valueEncoderFactory().getEncoder(value, from, length));
    }

    public void writeBinary(byte[] value, int from, int length) throws XMLStreamException {
        Base64Variant v = Base64Variants.getDefaultVariant();
        this.writeTypedElement(this.valueEncoderFactory().getEncoder(v, value, from, length));
    }

    public void writeBinary(Base64Variant v, byte[] value, int from, int length) throws XMLStreamException {
        this.writeTypedElement(this.valueEncoderFactory().getEncoder(v, value, from, length));
    }

    protected final void writeTypedElement(AsciiValueEncoder enc) throws XMLStreamException {
        if (this.mStartElementOpen) {
            this.closeStartElement(this.mEmptyElement);
        }
        if (this.mCheckStructure && this.inPrologOrEpilog()) {
            TypedStreamWriter.reportNwfStructure(ErrorConsts.WERR_PROLOG_NONWS_TEXT);
        }
        if (this.mVldContent <= 1) {
            this.reportInvalidContent(4);
        }
        try {
            XMLValidator vld;
            XMLValidator xMLValidator = vld = this.mVldContent == 3 ? this.mValidator : null;
            if (vld == null) {
                this.mWriter.writeTypedElement(enc);
            } else {
                this.mWriter.writeTypedElement(enc, vld, this.getCopyBuffer());
            }
        }
        catch (IOException ioe) {
            throw new WstxIOException(ioe);
        }
    }

    public void writeBooleanAttribute(String prefix, String nsURI, String localName, boolean value) throws XMLStreamException {
        this.writeTypedAttribute(prefix, nsURI, localName, this.valueEncoderFactory().getEncoder(value));
    }

    public void writeIntAttribute(String prefix, String nsURI, String localName, int value) throws XMLStreamException {
        this.writeTypedAttribute(prefix, nsURI, localName, this.valueEncoderFactory().getEncoder(value));
    }

    public void writeLongAttribute(String prefix, String nsURI, String localName, long value) throws XMLStreamException {
        this.writeTypedAttribute(prefix, nsURI, localName, this.valueEncoderFactory().getEncoder(value));
    }

    public void writeFloatAttribute(String prefix, String nsURI, String localName, float value) throws XMLStreamException {
        this.writeTypedAttribute(prefix, nsURI, localName, this.valueEncoderFactory().getEncoder(value));
    }

    public void writeDoubleAttribute(String prefix, String nsURI, String localName, double value) throws XMLStreamException {
        this.writeTypedAttribute(prefix, nsURI, localName, this.valueEncoderFactory().getEncoder(value));
    }

    public void writeIntegerAttribute(String prefix, String nsURI, String localName, BigInteger value) throws XMLStreamException {
        this.writeTypedAttribute(prefix, nsURI, localName, this.valueEncoderFactory().getScalarEncoder(value.toString()));
    }

    public void writeDecimalAttribute(String prefix, String nsURI, String localName, BigDecimal value) throws XMLStreamException {
        this.writeTypedAttribute(prefix, nsURI, localName, this.valueEncoderFactory().getScalarEncoder(value.toString()));
    }

    public void writeQNameAttribute(String prefix, String nsURI, String localName, QName name) throws XMLStreamException {
        this.writeAttribute(prefix, nsURI, localName, this.serializeQName(name));
    }

    public void writeIntArrayAttribute(String prefix, String nsURI, String localName, int[] value) throws XMLStreamException {
        this.writeTypedAttribute(prefix, nsURI, localName, this.valueEncoderFactory().getEncoder(value, 0, value.length));
    }

    public void writeLongArrayAttribute(String prefix, String nsURI, String localName, long[] value) throws XMLStreamException {
        this.writeTypedAttribute(prefix, nsURI, localName, this.valueEncoderFactory().getEncoder(value, 0, value.length));
    }

    public void writeFloatArrayAttribute(String prefix, String nsURI, String localName, float[] value) throws XMLStreamException {
        this.writeTypedAttribute(prefix, nsURI, localName, this.valueEncoderFactory().getEncoder(value, 0, value.length));
    }

    public void writeDoubleArrayAttribute(String prefix, String nsURI, String localName, double[] value) throws XMLStreamException {
        this.writeTypedAttribute(prefix, nsURI, localName, this.valueEncoderFactory().getEncoder(value, 0, value.length));
    }

    public void writeBinaryAttribute(String prefix, String nsURI, String localName, byte[] value) throws XMLStreamException {
        Base64Variant v = Base64Variants.getDefaultVariant();
        this.writeTypedAttribute(prefix, nsURI, localName, this.valueEncoderFactory().getEncoder(v, value, 0, value.length));
    }

    public void writeBinaryAttribute(Base64Variant v, String prefix, String nsURI, String localName, byte[] value) throws XMLStreamException {
        this.writeTypedAttribute(prefix, nsURI, localName, this.valueEncoderFactory().getEncoder(v, value, 0, value.length));
    }

    protected abstract void writeTypedAttribute(String var1, String var2, String var3, AsciiValueEncoder var4) throws XMLStreamException;

    private String serializeQName(QName name) throws XMLStreamException {
        String vp = this.validateQNamePrefix(name);
        String local = name.getLocalPart();
        if (vp == null || vp.length() == 0) {
            return local;
        }
        return vp + ":" + local;
    }
}

