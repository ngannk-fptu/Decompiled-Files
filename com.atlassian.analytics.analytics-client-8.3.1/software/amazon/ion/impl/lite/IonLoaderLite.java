/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.ion.impl.lite;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import software.amazon.ion.IonCatalog;
import software.amazon.ion.IonDatagram;
import software.amazon.ion.IonException;
import software.amazon.ion.IonLoader;
import software.amazon.ion.IonReader;
import software.amazon.ion.IonSystem;
import software.amazon.ion.IonWriter;
import software.amazon.ion.impl.PrivateIonReaderFactory;
import software.amazon.ion.impl.PrivateIonWriterFactory;
import software.amazon.ion.impl.lite.IonDatagramLite;
import software.amazon.ion.impl.lite.IonSystemLite;

final class IonLoaderLite
implements IonLoader {
    private final IonSystemLite _system;
    private final IonCatalog _catalog;

    public IonLoaderLite(IonSystemLite system, IonCatalog catalog) {
        assert (system != null);
        assert (catalog != null);
        this._system = system;
        this._catalog = catalog;
    }

    public IonSystem getSystem() {
        return this._system;
    }

    public IonCatalog getCatalog() {
        return this._catalog;
    }

    private IonDatagramLite load_helper(IonReader reader) throws IOException {
        IonDatagramLite datagram = new IonDatagramLite(this._system, this._catalog);
        IonWriter writer = PrivateIonWriterFactory.makeWriter(datagram);
        writer.writeValues(reader);
        return datagram;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public IonDatagram load(File ionFile) throws IonException, IOException {
        FileInputStream ionData = new FileInputStream(ionFile);
        try {
            IonDatagram datagram;
            IonDatagram ionDatagram = datagram = this.load(ionData);
            return ionDatagram;
        }
        finally {
            ((InputStream)ionData).close();
        }
    }

    public IonDatagram load(String ionText) throws IonException {
        try {
            IonReader reader = PrivateIonReaderFactory.makeReader((IonSystem)this._system, this._catalog, ionText);
            IonDatagramLite datagram = this.load_helper(reader);
            return datagram;
        }
        catch (IOException e) {
            throw new IonException(e);
        }
    }

    public IonDatagram load(Reader ionText) throws IonException, IOException {
        try {
            IonReader reader = PrivateIonReaderFactory.makeReader((IonSystem)this._system, this._catalog, ionText);
            IonDatagramLite datagram = this.load_helper(reader);
            return datagram;
        }
        catch (IonException e) {
            IOException io = e.causeOfType(IOException.class);
            if (io != null) {
                throw io;
            }
            throw e;
        }
    }

    public IonDatagram load(byte[] ionData) throws IonException {
        try {
            IonReader reader = PrivateIonReaderFactory.makeReader((IonSystem)this._system, this._catalog, ionData, 0, ionData.length);
            IonDatagramLite datagram = this.load_helper(reader);
            return datagram;
        }
        catch (IOException e) {
            throw new IonException(e);
        }
    }

    public IonDatagram load(InputStream ionData) throws IonException, IOException {
        try {
            IonReader reader = PrivateIonReaderFactory.makeReader((IonSystem)this._system, this._catalog, ionData);
            IonDatagramLite datagram = this.load_helper(reader);
            return datagram;
        }
        catch (IonException e) {
            IOException io = e.causeOfType(IOException.class);
            if (io != null) {
                throw io;
            }
            throw e;
        }
    }
}

