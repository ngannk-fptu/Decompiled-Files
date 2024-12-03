/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import software.amazon.ion.IonCatalog;
import software.amazon.ion.IonDatagram;
import software.amazon.ion.IonException;
import software.amazon.ion.IonSystem;

public interface IonLoader {
    public IonSystem getSystem();

    public IonCatalog getCatalog();

    public IonDatagram load(File var1) throws IonException, IOException;

    public IonDatagram load(String var1) throws IonException;

    public IonDatagram load(Reader var1) throws IonException, IOException;

    public IonDatagram load(byte[] var1) throws IonException;

    public IonDatagram load(InputStream var1) throws IonException, IOException;
}

