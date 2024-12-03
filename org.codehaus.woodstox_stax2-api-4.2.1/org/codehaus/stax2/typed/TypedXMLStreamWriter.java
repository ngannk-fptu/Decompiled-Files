/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.typed;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.codehaus.stax2.typed.Base64Variant;

public interface TypedXMLStreamWriter
extends XMLStreamWriter {
    public void writeBoolean(boolean var1) throws XMLStreamException;

    public void writeInt(int var1) throws XMLStreamException;

    public void writeLong(long var1) throws XMLStreamException;

    public void writeFloat(float var1) throws XMLStreamException;

    public void writeDouble(double var1) throws XMLStreamException;

    public void writeInteger(BigInteger var1) throws XMLStreamException;

    public void writeDecimal(BigDecimal var1) throws XMLStreamException;

    public void writeQName(QName var1) throws XMLStreamException;

    public void writeBinary(byte[] var1, int var2, int var3) throws XMLStreamException;

    public void writeBinary(Base64Variant var1, byte[] var2, int var3, int var4) throws XMLStreamException;

    public void writeIntArray(int[] var1, int var2, int var3) throws XMLStreamException;

    public void writeLongArray(long[] var1, int var2, int var3) throws XMLStreamException;

    public void writeFloatArray(float[] var1, int var2, int var3) throws XMLStreamException;

    public void writeDoubleArray(double[] var1, int var2, int var3) throws XMLStreamException;

    public void writeBooleanAttribute(String var1, String var2, String var3, boolean var4) throws XMLStreamException;

    public void writeIntAttribute(String var1, String var2, String var3, int var4) throws XMLStreamException;

    public void writeLongAttribute(String var1, String var2, String var3, long var4) throws XMLStreamException;

    public void writeFloatAttribute(String var1, String var2, String var3, float var4) throws XMLStreamException;

    public void writeDoubleAttribute(String var1, String var2, String var3, double var4) throws XMLStreamException;

    public void writeIntegerAttribute(String var1, String var2, String var3, BigInteger var4) throws XMLStreamException;

    public void writeDecimalAttribute(String var1, String var2, String var3, BigDecimal var4) throws XMLStreamException;

    public void writeQNameAttribute(String var1, String var2, String var3, QName var4) throws XMLStreamException;

    public void writeBinaryAttribute(String var1, String var2, String var3, byte[] var4) throws XMLStreamException;

    public void writeBinaryAttribute(Base64Variant var1, String var2, String var3, String var4, byte[] var5) throws XMLStreamException;

    public void writeIntArrayAttribute(String var1, String var2, String var3, int[] var4) throws XMLStreamException;

    public void writeLongArrayAttribute(String var1, String var2, String var3, long[] var4) throws XMLStreamException;

    public void writeFloatArrayAttribute(String var1, String var2, String var3, float[] var4) throws XMLStreamException;

    public void writeDoubleArrayAttribute(String var1, String var2, String var3, double[] var4) throws XMLStreamException;
}

