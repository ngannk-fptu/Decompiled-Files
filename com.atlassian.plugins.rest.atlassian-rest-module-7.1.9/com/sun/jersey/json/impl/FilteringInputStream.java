/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.json.impl;

import java.io.IOException;
import java.io.InputStream;

public abstract class FilteringInputStream
extends InputStream {
    private byte[] currentBuffer;
    private int cursor;

    protected abstract byte[] nextBytes() throws IOException;

    @Override
    public int available() throws IOException {
        if (this.currentBuffer == null) {
            return 0;
        }
        return this.currentBuffer.length - this.cursor;
    }

    @Override
    public synchronized void mark(int readlimit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean markSupported() {
        return false;
    }

    private void getNextBytesIfNothingAvailable() throws IOException {
        if (this.available() < 1) {
            this.currentBuffer = this.nextBytes();
            this.cursor = 0;
        }
    }

    @Override
    public int read() throws IOException {
        this.getNextBytesIfNothingAvailable();
        if (this.currentBuffer == null || this.currentBuffer.length == 0) {
            return -1;
        }
        return this.currentBuffer[this.cursor++];
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        this.getNextBytesIfNothingAvailable();
        if (this.currentBuffer == null) {
            return -1;
        }
        int availableBytes = this.currentBuffer.length - this.cursor;
        if (len >= availableBytes) {
            System.arraycopy(this.currentBuffer, this.cursor, b, off, availableBytes);
            this.currentBuffer = null;
            return availableBytes;
        }
        System.arraycopy(this.currentBuffer, this.cursor, b, off, len);
        this.cursor += len;
        return len;
    }

    @Override
    public synchronized void reset() throws IOException {
        throw new IOException();
    }

    @Override
    public long skip(long n) throws IOException {
        return super.skip(n);
    }

    public String toString() {
        return super.toString();
    }
}

