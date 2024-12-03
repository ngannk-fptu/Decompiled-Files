/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

@Deprecated(forRemoval=true)
public class SizeLimitedInputStream
extends InputStream {
    private int maximumLength;
    private long currentLength;
    private InputStream wrappedInputStream;

    public SizeLimitedInputStream(InputStream wrappedInputStream, int maxBytesToRead) {
        this.wrappedInputStream = new BufferedInputStream(wrappedInputStream);
        this.maximumLength = maxBytesToRead;
    }

    @Override
    public int read() throws IOException {
        if (++this.currentLength > (long)this.maximumLength) {
            throw new IOException("Too much data retrieved: " + this.currentLength);
        }
        return this.wrappedInputStream.read();
    }

    @Override
    public long skip(long n) throws IOException {
        this.currentLength += n;
        if (this.currentLength > (long)this.maximumLength) {
            throw new IOException("Too much data retrieved: " + this.currentLength);
        }
        return this.wrappedInputStream.skip(n);
    }

    @Override
    public void close() throws IOException {
        this.wrappedInputStream.close();
    }

    @Override
    public int available() throws IOException {
        return this.wrappedInputStream.available();
    }

    @Override
    public synchronized void mark(int readlimit) {
        this.wrappedInputStream.mark(readlimit);
    }

    @Override
    public synchronized void reset() throws IOException {
        this.wrappedInputStream.reset();
    }

    @Override
    public boolean markSupported() {
        return this.wrappedInputStream.markSupported();
    }
}

