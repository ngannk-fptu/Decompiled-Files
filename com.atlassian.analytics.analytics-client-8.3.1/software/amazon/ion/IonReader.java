/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion;

import java.io.Closeable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Iterator;
import software.amazon.ion.Decimal;
import software.amazon.ion.IntegerSize;
import software.amazon.ion.IonType;
import software.amazon.ion.SymbolTable;
import software.amazon.ion.SymbolToken;
import software.amazon.ion.Timestamp;
import software.amazon.ion.facet.Faceted;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface IonReader
extends Closeable,
Faceted {
    public IonType next();

    public void stepIn();

    public void stepOut();

    public int getDepth();

    public SymbolTable getSymbolTable();

    public IonType getType();

    public IntegerSize getIntegerSize();

    public String[] getTypeAnnotations();

    public SymbolToken[] getTypeAnnotationSymbols();

    public Iterator<String> iterateTypeAnnotations();

    public String getFieldName();

    public SymbolToken getFieldNameSymbol();

    public boolean isNullValue();

    public boolean isInStruct();

    public boolean booleanValue();

    public int intValue();

    public long longValue();

    public BigInteger bigIntegerValue();

    public double doubleValue();

    public BigDecimal bigDecimalValue();

    public Decimal decimalValue();

    public Date dateValue();

    public Timestamp timestampValue();

    public String stringValue();

    public SymbolToken symbolValue();

    public int byteSize();

    public byte[] newBytes();

    public int getBytes(byte[] var1, int var2, int var3);
}

