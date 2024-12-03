/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.messaging.saaj.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TeeInputStream
extends InputStream {
    protected InputStream source;
    protected OutputStream copySink;

    public TeeInputStream(InputStream source, OutputStream sink) {
        this.copySink = sink;
        this.source = source;
    }

    @Override
    public int read() throws IOException {
        int result = this.source.read();
        this.copySink.write(result);
        return result;
    }

    @Override
    public int available() throws IOException {
        return this.source.available();
    }

    @Override
    public void close() throws IOException {
        this.source.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        this.source.mark(readlimit);
    }

    @Override
    public boolean markSupported() {
        return this.source.markSupported();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int result = this.source.read(b, off, len);
        this.copySink.write(b, off, len);
        return result;
    }

    @Override
    public int read(byte[] b) throws IOException {
        int result = this.source.read(b);
        this.copySink.write(b);
        return result;
    }

    @Override
    public synchronized void reset() throws IOException {
        this.source.reset();
    }

    @Override
    public long skip(long n) throws IOException {
        return this.source.skip(n);
    }
}

