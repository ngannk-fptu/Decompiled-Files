/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import software.amazon.ion.IonReader;
import software.amazon.ion.IonType;
import software.amazon.ion.SymbolTable;
import software.amazon.ion.SymbolToken;
import software.amazon.ion.Timestamp;

public interface IonWriter
extends Closeable,
Flushable {
    public SymbolTable getSymbolTable();

    public void flush() throws IOException;

    public void finish() throws IOException;

    public void close() throws IOException;

    public void setFieldName(String var1);

    public void setFieldNameSymbol(SymbolToken var1);

    public void setTypeAnnotations(String ... var1);

    public void setTypeAnnotationSymbols(SymbolToken ... var1);

    public void addTypeAnnotation(String var1);

    public void stepIn(IonType var1) throws IOException;

    public void stepOut() throws IOException;

    public boolean isInStruct();

    public void writeValue(IonReader var1) throws IOException;

    public void writeValues(IonReader var1) throws IOException;

    public void writeNull() throws IOException;

    public void writeNull(IonType var1) throws IOException;

    public void writeBool(boolean var1) throws IOException;

    public void writeInt(long var1) throws IOException;

    public void writeInt(BigInteger var1) throws IOException;

    public void writeFloat(double var1) throws IOException;

    public void writeDecimal(BigDecimal var1) throws IOException;

    public void writeTimestamp(Timestamp var1) throws IOException;

    public void writeSymbol(String var1) throws IOException;

    public void writeSymbolToken(SymbolToken var1) throws IOException;

    public void writeString(String var1) throws IOException;

    public void writeClob(byte[] var1) throws IOException;

    public void writeClob(byte[] var1, int var2, int var3) throws IOException;

    public void writeBlob(byte[] var1) throws IOException;

    public void writeBlob(byte[] var1, int var2, int var3) throws IOException;
}

