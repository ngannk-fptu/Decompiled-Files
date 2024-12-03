/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.util;

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.util.StreamReaderDelegate;
import org.codehaus.stax2.AttributeInfo;
import org.codehaus.stax2.DTDInfo;
import org.codehaus.stax2.LocationInfo;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.typed.Base64Variant;
import org.codehaus.stax2.typed.TypedArrayDecoder;
import org.codehaus.stax2.typed.TypedValueDecoder;
import org.codehaus.stax2.validation.ValidationProblemHandler;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidator;

public class StreamReader2Delegate
extends StreamReaderDelegate
implements XMLStreamReader2 {
    protected XMLStreamReader2 mDelegate2;

    public StreamReader2Delegate(XMLStreamReader2 xMLStreamReader2) {
        super(xMLStreamReader2);
        this.mDelegate2 = xMLStreamReader2;
    }

    public void setParent(XMLStreamReader xMLStreamReader) {
        super.setParent(xMLStreamReader);
        this.mDelegate2 = (XMLStreamReader2)xMLStreamReader;
    }

    public void closeCompletely() throws XMLStreamException {
        this.mDelegate2.closeCompletely();
    }

    public AttributeInfo getAttributeInfo() throws XMLStreamException {
        return this.mDelegate2.getAttributeInfo();
    }

    public DTDInfo getDTDInfo() throws XMLStreamException {
        return this.mDelegate2.getDTDInfo();
    }

    public int getDepth() {
        return this.mDelegate2.getDepth();
    }

    public Object getFeature(String string) {
        return this.mDelegate2.getFeature(string);
    }

    public LocationInfo getLocationInfo() {
        return this.mDelegate2.getLocationInfo();
    }

    public NamespaceContext getNonTransientNamespaceContext() {
        return this.mDelegate2.getNonTransientNamespaceContext();
    }

    public String getPrefixedName() {
        return this.mDelegate2.getPrefixedName();
    }

    public int getText(Writer writer, boolean bl) throws IOException, XMLStreamException {
        return this.mDelegate2.getText(writer, bl);
    }

    public boolean isEmptyElement() throws XMLStreamException {
        return this.mDelegate2.isEmptyElement();
    }

    public boolean isPropertySupported(String string) {
        return this.mDelegate2.isPropertySupported(string);
    }

    public void setFeature(String string, Object object) {
        this.mDelegate2.setFeature(string, object);
    }

    public boolean setProperty(String string, Object object) {
        return this.mDelegate2.setProperty(string, object);
    }

    public void skipElement() throws XMLStreamException {
        this.mDelegate2.skipElement();
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

    public int getAttributeIndex(String string, String string2) {
        return this.mDelegate2.getAttributeIndex(string, string2);
    }

    public boolean getAttributeAsBoolean(int n) throws XMLStreamException {
        return this.mDelegate2.getAttributeAsBoolean(n);
    }

    public BigDecimal getAttributeAsDecimal(int n) throws XMLStreamException {
        return this.mDelegate2.getAttributeAsDecimal(n);
    }

    public double getAttributeAsDouble(int n) throws XMLStreamException {
        return this.mDelegate2.getAttributeAsDouble(n);
    }

    public float getAttributeAsFloat(int n) throws XMLStreamException {
        return this.mDelegate2.getAttributeAsFloat(n);
    }

    public int getAttributeAsInt(int n) throws XMLStreamException {
        return this.mDelegate2.getAttributeAsInt(n);
    }

    public BigInteger getAttributeAsInteger(int n) throws XMLStreamException {
        return this.mDelegate2.getAttributeAsInteger(n);
    }

    public long getAttributeAsLong(int n) throws XMLStreamException {
        return this.mDelegate2.getAttributeAsLong(n);
    }

    public QName getAttributeAsQName(int n) throws XMLStreamException {
        return this.mDelegate2.getAttributeAsQName(n);
    }

    public int[] getAttributeAsIntArray(int n) throws XMLStreamException {
        return this.mDelegate2.getAttributeAsIntArray(n);
    }

    public long[] getAttributeAsLongArray(int n) throws XMLStreamException {
        return this.mDelegate2.getAttributeAsLongArray(n);
    }

    public float[] getAttributeAsFloatArray(int n) throws XMLStreamException {
        return this.mDelegate2.getAttributeAsFloatArray(n);
    }

    public double[] getAttributeAsDoubleArray(int n) throws XMLStreamException {
        return this.mDelegate2.getAttributeAsDoubleArray(n);
    }

    public void getElementAs(TypedValueDecoder typedValueDecoder) throws XMLStreamException {
        this.mDelegate2.getElementAs(typedValueDecoder);
    }

    public boolean getElementAsBoolean() throws XMLStreamException {
        return this.mDelegate2.getElementAsBoolean();
    }

    public BigDecimal getElementAsDecimal() throws XMLStreamException {
        return this.mDelegate2.getElementAsDecimal();
    }

    public double getElementAsDouble() throws XMLStreamException {
        return this.mDelegate2.getElementAsDouble();
    }

    public float getElementAsFloat() throws XMLStreamException {
        return this.mDelegate2.getElementAsFloat();
    }

    public int getElementAsInt() throws XMLStreamException {
        return this.mDelegate2.getElementAsInt();
    }

    public BigInteger getElementAsInteger() throws XMLStreamException {
        return this.mDelegate2.getElementAsInteger();
    }

    public long getElementAsLong() throws XMLStreamException {
        return this.mDelegate2.getElementAsLong();
    }

    public QName getElementAsQName() throws XMLStreamException {
        return this.mDelegate2.getElementAsQName();
    }

    public byte[] getElementAsBinary() throws XMLStreamException {
        return this.mDelegate2.getElementAsBinary();
    }

    public byte[] getElementAsBinary(Base64Variant base64Variant) throws XMLStreamException {
        return this.mDelegate2.getElementAsBinary(base64Variant);
    }

    public void getAttributeAs(int n, TypedValueDecoder typedValueDecoder) throws XMLStreamException {
        this.mDelegate2.getAttributeAs(n, typedValueDecoder);
    }

    public int getAttributeAsArray(int n, TypedArrayDecoder typedArrayDecoder) throws XMLStreamException {
        return this.mDelegate2.getAttributeAsArray(n, typedArrayDecoder);
    }

    public byte[] getAttributeAsBinary(int n) throws XMLStreamException {
        return this.mDelegate2.getAttributeAsBinary(n);
    }

    public byte[] getAttributeAsBinary(int n, Base64Variant base64Variant) throws XMLStreamException {
        return this.mDelegate2.getAttributeAsBinary(n, base64Variant);
    }

    public int readElementAsDoubleArray(double[] dArray, int n, int n2) throws XMLStreamException {
        return this.mDelegate2.readElementAsDoubleArray(dArray, n, n2);
    }

    public int readElementAsFloatArray(float[] fArray, int n, int n2) throws XMLStreamException {
        return this.mDelegate2.readElementAsFloatArray(fArray, n, n2);
    }

    public int readElementAsIntArray(int[] nArray, int n, int n2) throws XMLStreamException {
        return this.mDelegate2.readElementAsIntArray(nArray, n, n2);
    }

    public int readElementAsLongArray(long[] lArray, int n, int n2) throws XMLStreamException {
        return this.mDelegate2.readElementAsLongArray(lArray, n, n2);
    }

    public int readElementAsArray(TypedArrayDecoder typedArrayDecoder) throws XMLStreamException {
        return this.mDelegate2.readElementAsArray(typedArrayDecoder);
    }

    public int readElementAsBinary(byte[] byArray, int n, int n2) throws XMLStreamException {
        return this.mDelegate2.readElementAsBinary(byArray, n, n2);
    }

    public int readElementAsBinary(byte[] byArray, int n, int n2, Base64Variant base64Variant) throws XMLStreamException {
        return this.mDelegate2.readElementAsBinary(byArray, n, n2, base64Variant);
    }
}

