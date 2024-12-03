/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.codehaus.stax2.XMLStreamLocation2;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.XMLStreamWriter2;
import org.codehaus.stax2.typed.Base64Variant;
import org.codehaus.stax2.util.StreamWriterDelegate;
import org.codehaus.stax2.validation.ValidationProblemHandler;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidator;

public class StreamWriter2Delegate
extends StreamWriterDelegate
implements XMLStreamWriter2 {
    protected XMLStreamWriter2 mDelegate2;

    public StreamWriter2Delegate(XMLStreamWriter2 xMLStreamWriter2) {
        super(xMLStreamWriter2);
    }

    public void setParent(XMLStreamWriter xMLStreamWriter) {
        super.setParent(xMLStreamWriter);
        this.mDelegate2 = (XMLStreamWriter2)xMLStreamWriter;
    }

    public void closeCompletely() throws XMLStreamException {
        this.mDelegate2.closeCompletely();
    }

    public void copyEventFromReader(XMLStreamReader2 xMLStreamReader2, boolean bl) throws XMLStreamException {
        this.mDelegate2.copyEventFromReader(xMLStreamReader2, bl);
    }

    public String getEncoding() {
        return this.mDelegate2.getEncoding();
    }

    public XMLStreamLocation2 getLocation() {
        return this.mDelegate2.getLocation();
    }

    public boolean isPropertySupported(String string) {
        return this.mDelegate2.isPropertySupported(string);
    }

    public boolean setProperty(String string, Object object) {
        return this.mDelegate2.setProperty(string, object);
    }

    public void writeCData(char[] cArray, int n, int n2) throws XMLStreamException {
        this.mDelegate2.writeCData(cArray, n, n2);
    }

    public void writeDTD(String string, String string2, String string3, String string4) throws XMLStreamException {
        this.mDelegate2.writeDTD(string, string2, string3, string4);
    }

    public void writeFullEndElement() throws XMLStreamException {
        this.mDelegate2.writeFullEndElement();
    }

    public void writeRaw(String string) throws XMLStreamException {
        this.mDelegate2.writeRaw(string);
    }

    public void writeRaw(String string, int n, int n2) throws XMLStreamException {
        this.mDelegate2.writeRaw(string, n, n2);
    }

    public void writeRaw(char[] cArray, int n, int n2) throws XMLStreamException {
        this.mDelegate2.writeRaw(cArray, n, n2);
    }

    public void writeSpace(String string) throws XMLStreamException {
        this.mDelegate2.writeSpace(string);
    }

    public void writeSpace(char[] cArray, int n, int n2) throws XMLStreamException {
        this.mDelegate2.writeSpace(cArray, n, n2);
    }

    public void writeStartDocument(String string, String string2, boolean bl) throws XMLStreamException {
        this.mDelegate2.writeStartDocument(string, string2, bl);
    }

    public void writeBinary(byte[] byArray, int n, int n2) throws XMLStreamException {
        this.mDelegate2.writeBinary(byArray, n, n2);
    }

    public void writeBinary(Base64Variant base64Variant, byte[] byArray, int n, int n2) throws XMLStreamException {
        this.mDelegate2.writeBinary(base64Variant, byArray, n, n2);
    }

    public void writeBinaryAttribute(String string, String string2, String string3, byte[] byArray) throws XMLStreamException {
        this.mDelegate2.writeBinaryAttribute(string, string2, string3, byArray);
    }

    public void writeBinaryAttribute(Base64Variant base64Variant, String string, String string2, String string3, byte[] byArray) throws XMLStreamException {
        this.mDelegate2.writeBinaryAttribute(base64Variant, string, string2, string3, byArray);
    }

    public void writeBoolean(boolean bl) throws XMLStreamException {
        this.mDelegate2.writeBoolean(bl);
    }

    public void writeBooleanAttribute(String string, String string2, String string3, boolean bl) throws XMLStreamException {
        this.mDelegate2.writeBooleanAttribute(string, string2, string3, bl);
    }

    public void writeDecimal(BigDecimal bigDecimal) throws XMLStreamException {
        this.mDelegate2.writeDecimal(bigDecimal);
    }

    public void writeDecimalAttribute(String string, String string2, String string3, BigDecimal bigDecimal) throws XMLStreamException {
        this.mDelegate2.writeDecimalAttribute(string, string2, string3, bigDecimal);
    }

    public void writeDouble(double d) throws XMLStreamException {
        this.mDelegate2.writeDouble(d);
    }

    public void writeDoubleArray(double[] dArray, int n, int n2) throws XMLStreamException {
        this.mDelegate2.writeDoubleArray(dArray, n, n2);
    }

    public void writeDoubleArrayAttribute(String string, String string2, String string3, double[] dArray) throws XMLStreamException {
        this.mDelegate2.writeDoubleArrayAttribute(string, string2, string3, dArray);
    }

    public void writeDoubleAttribute(String string, String string2, String string3, double d) throws XMLStreamException {
        this.mDelegate2.writeDoubleAttribute(string, string2, string3, d);
    }

    public void writeFloat(float f) throws XMLStreamException {
        this.mDelegate2.writeFloat(f);
    }

    public void writeFloatArray(float[] fArray, int n, int n2) throws XMLStreamException {
        this.mDelegate2.writeFloatArray(fArray, n, n2);
    }

    public void writeFloatArrayAttribute(String string, String string2, String string3, float[] fArray) throws XMLStreamException {
        this.mDelegate2.writeFloatArrayAttribute(string, string2, string3, fArray);
    }

    public void writeFloatAttribute(String string, String string2, String string3, float f) throws XMLStreamException {
        this.mDelegate2.writeFloatAttribute(string, string2, string3, f);
    }

    public void writeInt(int n) throws XMLStreamException {
        this.mDelegate2.writeInt(n);
    }

    public void writeIntArray(int[] nArray, int n, int n2) throws XMLStreamException {
        this.mDelegate2.writeIntArray(nArray, n, n2);
    }

    public void writeIntArrayAttribute(String string, String string2, String string3, int[] nArray) throws XMLStreamException {
        this.mDelegate2.writeIntArrayAttribute(string, string2, string3, nArray);
    }

    public void writeIntAttribute(String string, String string2, String string3, int n) throws XMLStreamException {
        this.mDelegate2.writeIntAttribute(string, string2, string3, n);
    }

    public void writeInteger(BigInteger bigInteger) throws XMLStreamException {
        this.mDelegate2.writeInteger(bigInteger);
    }

    public void writeIntegerAttribute(String string, String string2, String string3, BigInteger bigInteger) throws XMLStreamException {
        this.mDelegate2.writeIntegerAttribute(string, string2, string3, bigInteger);
    }

    public void writeLong(long l) throws XMLStreamException {
        this.mDelegate2.writeLong(l);
    }

    public void writeLongArray(long[] lArray, int n, int n2) throws XMLStreamException {
        this.mDelegate2.writeLongArray(lArray, n, n2);
    }

    public void writeLongArrayAttribute(String string, String string2, String string3, long[] lArray) throws XMLStreamException {
        this.mDelegate2.writeLongArrayAttribute(string, string2, string3, lArray);
    }

    public void writeLongAttribute(String string, String string2, String string3, long l) throws XMLStreamException {
        this.mDelegate2.writeLongAttribute(string, string2, string3, l);
    }

    public void writeQName(QName qName) throws XMLStreamException {
        this.mDelegate2.writeQName(qName);
    }

    public void writeQNameAttribute(String string, String string2, String string3, QName qName) throws XMLStreamException {
        this.mDelegate2.writeQNameAttribute(string, string2, string3, qName);
    }

    public ValidationProblemHandler setValidationProblemHandler(ValidationProblemHandler validationProblemHandler) {
        return this.mDelegate2.setValidationProblemHandler(validationProblemHandler);
    }

    public XMLValidator stopValidatingAgainst(XMLValidationSchema xMLValidationSchema) throws XMLStreamException {
        return this.mDelegate2.stopValidatingAgainst(xMLValidationSchema);
    }

    public XMLValidator stopValidatingAgainst(XMLValidator xMLValidator) throws XMLStreamException {
        return this.mDelegate2.stopValidatingAgainst(xMLValidator);
    }

    public XMLValidator validateAgainst(XMLValidationSchema xMLValidationSchema) throws XMLStreamException {
        return this.mDelegate2.validateAgainst(xMLValidationSchema);
    }
}

