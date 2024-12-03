/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.typed;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.codehaus.stax2.typed.Base64Variant;
import org.codehaus.stax2.typed.TypedArrayDecoder;
import org.codehaus.stax2.typed.TypedValueDecoder;

public interface TypedXMLStreamReader
extends XMLStreamReader {
    public boolean getElementAsBoolean() throws XMLStreamException;

    public int getElementAsInt() throws XMLStreamException;

    public long getElementAsLong() throws XMLStreamException;

    public float getElementAsFloat() throws XMLStreamException;

    public double getElementAsDouble() throws XMLStreamException;

    public BigInteger getElementAsInteger() throws XMLStreamException;

    public BigDecimal getElementAsDecimal() throws XMLStreamException;

    public QName getElementAsQName() throws XMLStreamException;

    public byte[] getElementAsBinary() throws XMLStreamException;

    public byte[] getElementAsBinary(Base64Variant var1) throws XMLStreamException;

    public void getElementAs(TypedValueDecoder var1) throws XMLStreamException;

    public int readElementAsBinary(byte[] var1, int var2, int var3, Base64Variant var4) throws XMLStreamException;

    public int readElementAsBinary(byte[] var1, int var2, int var3) throws XMLStreamException;

    public int readElementAsIntArray(int[] var1, int var2, int var3) throws XMLStreamException;

    public int readElementAsLongArray(long[] var1, int var2, int var3) throws XMLStreamException;

    public int readElementAsFloatArray(float[] var1, int var2, int var3) throws XMLStreamException;

    public int readElementAsDoubleArray(double[] var1, int var2, int var3) throws XMLStreamException;

    public int readElementAsArray(TypedArrayDecoder var1) throws XMLStreamException;

    public int getAttributeIndex(String var1, String var2);

    public boolean getAttributeAsBoolean(int var1) throws XMLStreamException;

    public int getAttributeAsInt(int var1) throws XMLStreamException;

    public long getAttributeAsLong(int var1) throws XMLStreamException;

    public float getAttributeAsFloat(int var1) throws XMLStreamException;

    public double getAttributeAsDouble(int var1) throws XMLStreamException;

    public BigInteger getAttributeAsInteger(int var1) throws XMLStreamException;

    public BigDecimal getAttributeAsDecimal(int var1) throws XMLStreamException;

    public QName getAttributeAsQName(int var1) throws XMLStreamException;

    public void getAttributeAs(int var1, TypedValueDecoder var2) throws XMLStreamException;

    public byte[] getAttributeAsBinary(int var1) throws XMLStreamException;

    public byte[] getAttributeAsBinary(int var1, Base64Variant var2) throws XMLStreamException;

    public int[] getAttributeAsIntArray(int var1) throws XMLStreamException;

    public long[] getAttributeAsLongArray(int var1) throws XMLStreamException;

    public float[] getAttributeAsFloatArray(int var1) throws XMLStreamException;

    public double[] getAttributeAsDoubleArray(int var1) throws XMLStreamException;

    public int getAttributeAsArray(int var1, TypedArrayDecoder var2) throws XMLStreamException;
}

