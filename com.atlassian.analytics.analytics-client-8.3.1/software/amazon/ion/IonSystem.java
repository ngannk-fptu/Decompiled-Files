/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Date;
import java.util.Iterator;
import software.amazon.ion.IonCatalog;
import software.amazon.ion.IonContainer;
import software.amazon.ion.IonDatagram;
import software.amazon.ion.IonLoader;
import software.amazon.ion.IonReader;
import software.amazon.ion.IonTimestamp;
import software.amazon.ion.IonValue;
import software.amazon.ion.IonWriter;
import software.amazon.ion.SymbolTable;
import software.amazon.ion.UnsupportedIonVersionException;
import software.amazon.ion.ValueFactory;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface IonSystem
extends ValueFactory {
    public SymbolTable getSystemSymbolTable();

    public SymbolTable getSystemSymbolTable(String var1) throws UnsupportedIonVersionException;

    public IonCatalog getCatalog();

    public SymbolTable newLocalSymbolTable(SymbolTable ... var1);

    public SymbolTable newSharedSymbolTable(String var1, int var2, Iterator<String> var3, SymbolTable ... var4);

    public SymbolTable newSharedSymbolTable(IonReader var1);

    public SymbolTable newSharedSymbolTable(IonReader var1, boolean var2);

    public IonLoader newLoader();

    public IonLoader newLoader(IonCatalog var1);

    public IonLoader getLoader();

    public Iterator<IonValue> iterate(Reader var1);

    public Iterator<IonValue> iterate(InputStream var1);

    public Iterator<IonValue> iterate(String var1);

    public Iterator<IonValue> iterate(byte[] var1);

    public IonValue singleValue(String var1);

    public IonValue singleValue(byte[] var1);

    public IonReader newReader(String var1);

    public IonReader newReader(byte[] var1);

    public IonReader newReader(byte[] var1, int var2, int var3);

    public IonReader newReader(InputStream var1);

    public IonReader newReader(Reader var1);

    public IonReader newReader(IonValue var1);

    public IonWriter newWriter(IonContainer var1);

    public IonWriter newTextWriter(OutputStream var1);

    public IonWriter newTextWriter(Appendable var1);

    public IonWriter newTextWriter(OutputStream var1, SymbolTable ... var2) throws IOException;

    public IonWriter newTextWriter(Appendable var1, SymbolTable ... var2) throws IOException;

    public IonWriter newBinaryWriter(OutputStream var1, SymbolTable ... var2);

    public IonDatagram newDatagram();

    public IonDatagram newDatagram(IonValue var1);

    public IonDatagram newDatagram(SymbolTable ... var1);

    public IonValue newValue(IonReader var1);

    public IonTimestamp newUtcTimestampFromMillis(long var1);

    public IonTimestamp newUtcTimestamp(Date var1);

    public IonTimestamp newCurrentUtcTimestamp();
}

