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
    protected XMLStreamReader2 _delegate2;

    public StreamReader2Delegate(XMLStreamReader2 sr) {
        super(sr);
        this._delegate2 = sr;
    }

    @Override
    public void setParent(XMLStreamReader pr) {
        super.setParent(pr);
        this._delegate2 = (XMLStreamReader2)pr;
    }

    @Override
    public void closeCompletely() throws XMLStreamException {
        this._delegate2.closeCompletely();
    }

    @Override
    public AttributeInfo getAttributeInfo() throws XMLStreamException {
        return this._delegate2.getAttributeInfo();
    }

    @Override
    public DTDInfo getDTDInfo() throws XMLStreamException {
        return this._delegate2.getDTDInfo();
    }

    @Override
    public int getDepth() {
        return this._delegate2.getDepth();
    }

    @Override
    public Object getFeature(String name) {
        return this._delegate2.getFeature(name);
    }

    @Override
    public LocationInfo getLocationInfo() {
        return this._delegate2.getLocationInfo();
    }

    @Override
    public NamespaceContext getNonTransientNamespaceContext() {
        return this._delegate2.getNonTransientNamespaceContext();
    }

    @Override
    public String getPrefixedName() {
        return this._delegate2.getPrefixedName();
    }

    @Override
    public int getText(Writer w, boolean preserveContents) throws IOException, XMLStreamException {
        return this._delegate2.getText(w, preserveContents);
    }

    @Override
    public boolean isEmptyElement() throws XMLStreamException {
        return this._delegate2.isEmptyElement();
    }

    @Override
    public boolean isPropertySupported(String name) {
        return this._delegate2.isPropertySupported(name);
    }

    @Override
    public void setFeature(String name, Object value) {
        this._delegate2.setFeature(name, value);
    }

    @Override
    public boolean setProperty(String name, Object value) {
        return this._delegate2.setProperty(name, value);
    }

    @Override
    public void skipElement() throws XMLStreamException {
        this._delegate2.skipElement();
    }

    @Override
    public ValidationProblemHandler setValidationProblemHandler(ValidationProblemHandler h) {
        return this._delegate2.setValidationProblemHandler(h);
    }

    @Override
    public XMLValidator stopValidatingAgainst(XMLValidationSchema schema) throws XMLStreamException {
        return this._delegate2.stopValidatingAgainst(schema);
    }

    @Override
    public XMLValidator stopValidatingAgainst(XMLValidator validator) throws XMLStreamException {
        return this._delegate2.stopValidatingAgainst(validator);
    }

    @Override
    public XMLValidator validateAgainst(XMLValidationSchema schema) throws XMLStreamException {
        return this._delegate2.validateAgainst(schema);
    }

    @Override
    public int getAttributeIndex(String namespaceURI, String localName) {
        return this._delegate2.getAttributeIndex(namespaceURI, localName);
    }

    @Override
    public boolean getAttributeAsBoolean(int index) throws XMLStreamException {
        return this._delegate2.getAttributeAsBoolean(index);
    }

    @Override
    public BigDecimal getAttributeAsDecimal(int index) throws XMLStreamException {
        return this._delegate2.getAttributeAsDecimal(index);
    }

    @Override
    public double getAttributeAsDouble(int index) throws XMLStreamException {
        return this._delegate2.getAttributeAsDouble(index);
    }

    @Override
    public float getAttributeAsFloat(int index) throws XMLStreamException {
        return this._delegate2.getAttributeAsFloat(index);
    }

    @Override
    public int getAttributeAsInt(int index) throws XMLStreamException {
        return this._delegate2.getAttributeAsInt(index);
    }

    @Override
    public BigInteger getAttributeAsInteger(int index) throws XMLStreamException {
        return this._delegate2.getAttributeAsInteger(index);
    }

    @Override
    public long getAttributeAsLong(int index) throws XMLStreamException {
        return this._delegate2.getAttributeAsLong(index);
    }

    @Override
    public QName getAttributeAsQName(int index) throws XMLStreamException {
        return this._delegate2.getAttributeAsQName(index);
    }

    @Override
    public int[] getAttributeAsIntArray(int index) throws XMLStreamException {
        return this._delegate2.getAttributeAsIntArray(index);
    }

    @Override
    public long[] getAttributeAsLongArray(int index) throws XMLStreamException {
        return this._delegate2.getAttributeAsLongArray(index);
    }

    @Override
    public float[] getAttributeAsFloatArray(int index) throws XMLStreamException {
        return this._delegate2.getAttributeAsFloatArray(index);
    }

    @Override
    public double[] getAttributeAsDoubleArray(int index) throws XMLStreamException {
        return this._delegate2.getAttributeAsDoubleArray(index);
    }

    @Override
    public void getElementAs(TypedValueDecoder tvd) throws XMLStreamException {
        this._delegate2.getElementAs(tvd);
    }

    @Override
    public boolean getElementAsBoolean() throws XMLStreamException {
        return this._delegate2.getElementAsBoolean();
    }

    @Override
    public BigDecimal getElementAsDecimal() throws XMLStreamException {
        return this._delegate2.getElementAsDecimal();
    }

    @Override
    public double getElementAsDouble() throws XMLStreamException {
        return this._delegate2.getElementAsDouble();
    }

    @Override
    public float getElementAsFloat() throws XMLStreamException {
        return this._delegate2.getElementAsFloat();
    }

    @Override
    public int getElementAsInt() throws XMLStreamException {
        return this._delegate2.getElementAsInt();
    }

    @Override
    public BigInteger getElementAsInteger() throws XMLStreamException {
        return this._delegate2.getElementAsInteger();
    }

    @Override
    public long getElementAsLong() throws XMLStreamException {
        return this._delegate2.getElementAsLong();
    }

    @Override
    public QName getElementAsQName() throws XMLStreamException {
        return this._delegate2.getElementAsQName();
    }

    @Override
    public byte[] getElementAsBinary() throws XMLStreamException {
        return this._delegate2.getElementAsBinary();
    }

    @Override
    public byte[] getElementAsBinary(Base64Variant v) throws XMLStreamException {
        return this._delegate2.getElementAsBinary(v);
    }

    @Override
    public void getAttributeAs(int index, TypedValueDecoder tvd) throws XMLStreamException {
        this._delegate2.getAttributeAs(index, tvd);
    }

    @Override
    public int getAttributeAsArray(int index, TypedArrayDecoder tad) throws XMLStreamException {
        return this._delegate2.getAttributeAsArray(index, tad);
    }

    @Override
    public byte[] getAttributeAsBinary(int index) throws XMLStreamException {
        return this._delegate2.getAttributeAsBinary(index);
    }

    @Override
    public byte[] getAttributeAsBinary(int index, Base64Variant v) throws XMLStreamException {
        return this._delegate2.getAttributeAsBinary(index, v);
    }

    @Override
    public int readElementAsDoubleArray(double[] value, int from, int length) throws XMLStreamException {
        return this._delegate2.readElementAsDoubleArray(value, from, length);
    }

    @Override
    public int readElementAsFloatArray(float[] value, int from, int length) throws XMLStreamException {
        return this._delegate2.readElementAsFloatArray(value, from, length);
    }

    @Override
    public int readElementAsIntArray(int[] value, int from, int length) throws XMLStreamException {
        return this._delegate2.readElementAsIntArray(value, from, length);
    }

    @Override
    public int readElementAsLongArray(long[] value, int from, int length) throws XMLStreamException {
        return this._delegate2.readElementAsLongArray(value, from, length);
    }

    @Override
    public int readElementAsArray(TypedArrayDecoder tad) throws XMLStreamException {
        return this._delegate2.readElementAsArray(tad);
    }

    @Override
    public int readElementAsBinary(byte[] resultBuffer, int offset, int maxLength) throws XMLStreamException {
        return this._delegate2.readElementAsBinary(resultBuffer, offset, maxLength);
    }

    @Override
    public int readElementAsBinary(byte[] resultBuffer, int offset, int maxLength, Base64Variant v) throws XMLStreamException {
        return this._delegate2.readElementAsBinary(resultBuffer, offset, maxLength, v);
    }
}

