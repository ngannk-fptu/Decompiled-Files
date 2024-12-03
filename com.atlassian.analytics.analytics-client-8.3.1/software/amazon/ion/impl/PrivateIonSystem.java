/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl;

import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;
import software.amazon.ion.IonContainer;
import software.amazon.ion.IonReader;
import software.amazon.ion.IonStruct;
import software.amazon.ion.IonSystem;
import software.amazon.ion.IonValue;
import software.amazon.ion.IonWriter;
import software.amazon.ion.SymbolTable;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Deprecated
public interface PrivateIonSystem
extends IonSystem {
    public SymbolTable newSharedSymbolTable(IonStruct var1);

    public Iterator<IonValue> systemIterate(String var1);

    public Iterator<IonValue> systemIterate(Reader var1);

    public Iterator<IonValue> systemIterate(byte[] var1);

    public Iterator<IonValue> systemIterate(InputStream var1);

    public IonReader newSystemReader(Reader var1);

    public IonReader newSystemReader(byte[] var1);

    public IonReader newSystemReader(byte[] var1, int var2, int var3);

    public IonReader newSystemReader(String var1);

    public IonReader newSystemReader(InputStream var1);

    public IonReader newSystemReader(IonValue var1);

    public IonWriter newTreeWriter(IonContainer var1);

    public IonWriter newTreeSystemWriter(IonContainer var1);

    public boolean valueIsSharedSymbolTable(IonValue var1);

    public boolean isStreamCopyOptimized();
}

