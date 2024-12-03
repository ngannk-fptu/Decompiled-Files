/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;

public class ProxyingInputStream
extends InputStream {
    private InputStream inputStream;
    private static Method readAllBytes;
    private static Method readNBytes1;
    private static Method readNBytes3;
    private static Method skipNBytes;
    private static Method transferTo;

    public ProxyingInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public int read() throws IOException {
        return this.inputStream.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.inputStream.read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return this.inputStream.read(b, off, len);
    }

    @Override
    public byte[] readAllBytes() throws IOException {
        if (readAllBytes == null) {
            throw new UnsupportedOperationException();
        }
        try {
            return (byte[])readAllBytes.invoke((Object)this.inputStream, new Object[0]);
        }
        catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public byte[] readNBytes(int len) throws IOException {
        if (readNBytes1 == null) {
            throw new UnsupportedOperationException();
        }
        try {
            return (byte[])readNBytes1.invoke((Object)this.inputStream, len);
        }
        catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public int readNBytes(byte[] b, int off, int len) throws IOException {
        if (readNBytes3 == null) {
            throw new UnsupportedOperationException();
        }
        try {
            return (Integer)readNBytes3.invoke((Object)this.inputStream, b, off, len);
        }
        catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public int available() throws IOException {
        return this.inputStream.available();
    }

    @Override
    public boolean markSupported() {
        return this.inputStream.markSupported();
    }

    @Override
    public synchronized void mark(int readlimit) {
        this.inputStream.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        this.inputStream.reset();
    }

    @Override
    public long skip(long n) throws IOException {
        return this.inputStream.skip(n);
    }

    @Override
    public void skipNBytes(long n) throws IOException {
        if (skipNBytes == null) {
            throw new UnsupportedOperationException();
        }
        try {
            skipNBytes.invoke((Object)this.inputStream, n);
        }
        catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public long transferTo(OutputStream out) throws IOException {
        if (transferTo == null) {
            throw new UnsupportedOperationException();
        }
        try {
            return (Long)transferTo.invoke((Object)this.inputStream, out);
        }
        catch (Exception e) {
            throw new IOException(e);
        }
    }

    public String toString() {
        return this.inputStream.toString();
    }

    @Override
    public void close() throws IOException {
        if (this.inputStream != null) {
            try {
                this.inputStream.close();
            }
            finally {
                this.inputStream = null;
            }
        }
    }

    static {
        try {
            readAllBytes = InputStream.class.getDeclaredMethod("readAllBytes", new Class[0]);
        }
        catch (NoSuchMethodException | SecurityException exception) {
            // empty catch block
        }
        try {
            readNBytes1 = InputStream.class.getDeclaredMethod("readNBytes", Integer.TYPE);
        }
        catch (NoSuchMethodException | SecurityException exception) {
            // empty catch block
        }
        try {
            readNBytes3 = InputStream.class.getDeclaredMethod("readNBytes", byte[].class, Integer.TYPE, Integer.TYPE);
        }
        catch (NoSuchMethodException | SecurityException exception) {
            // empty catch block
        }
        try {
            skipNBytes = InputStream.class.getDeclaredMethod("skipNBytes", Long.TYPE);
        }
        catch (NoSuchMethodException | SecurityException exception) {
            // empty catch block
        }
        try {
            transferTo = InputStream.class.getDeclaredMethod("transferTo", OutputStream.class);
        }
        catch (NoSuchMethodException | SecurityException exception) {
            // empty catch block
        }
    }
}

