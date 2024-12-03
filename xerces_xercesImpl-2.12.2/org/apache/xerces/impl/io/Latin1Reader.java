/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public final class Latin1Reader
extends Reader {
    public static final int DEFAULT_BUFFER_SIZE = 2048;
    protected final InputStream fInputStream;
    protected final byte[] fBuffer;

    public Latin1Reader(InputStream inputStream) {
        this(inputStream, 2048);
    }

    public Latin1Reader(InputStream inputStream, int n) {
        this(inputStream, new byte[n]);
    }

    public Latin1Reader(InputStream inputStream, byte[] byArray) {
        this.fInputStream = inputStream;
        this.fBuffer = byArray;
    }

    @Override
    public int read() throws IOException {
        return this.fInputStream.read();
    }

    @Override
    public int read(char[] cArray, int n, int n2) throws IOException {
        if (n2 > this.fBuffer.length) {
            n2 = this.fBuffer.length;
        }
        int n3 = this.fInputStream.read(this.fBuffer, 0, n2);
        for (int i = 0; i < n3; ++i) {
            cArray[n + i] = (char)(this.fBuffer[i] & 0xFF);
        }
        return n3;
    }

    @Override
    public long skip(long l) throws IOException {
        return this.fInputStream.skip(l);
    }

    @Override
    public boolean ready() throws IOException {
        return false;
    }

    @Override
    public boolean markSupported() {
        return this.fInputStream.markSupported();
    }

    @Override
    public void mark(int n) throws IOException {
        this.fInputStream.mark(n);
    }

    @Override
    public void reset() throws IOException {
        this.fInputStream.reset();
    }

    @Override
    public void close() throws IOException {
        this.fInputStream.close();
    }
}

