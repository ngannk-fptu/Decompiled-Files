/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.util;

import java.io.IOException;
import java.io.InputStream;

public class BufferingInputStream
extends InputStream {
    private static final int BYTE_MASK = 255;
    private final InputStream in;
    private final byte[] buf;
    private int position;
    private int limit;

    public BufferingInputStream(InputStream in, int bufferSize) {
        this.in = in;
        this.buf = new byte[bufferSize];
    }

    @Override
    public int read() throws IOException {
        if (!this.ensureDataInBuffer()) {
            return -1;
        }
        return this.buf[this.position++] & 0xFF;
    }

    @Override
    public int read(byte[] destBuf, int off, int len) throws IOException {
        if (!this.ensureDataInBuffer()) {
            return -1;
        }
        int transferredCount = Math.min(this.limit - this.position, len);
        System.arraycopy(this.buf, this.position, destBuf, off, transferredCount);
        this.position += transferredCount;
        return transferredCount;
    }

    @Override
    public void close() throws IOException {
        this.in.close();
    }

    private boolean ensureDataInBuffer() throws IOException {
        if (this.position != this.limit) {
            return true;
        }
        this.position = 0;
        int newLimit = this.in.read(this.buf);
        if (newLimit == -1) {
            this.limit = 0;
            return false;
        }
        this.limit = newLimit;
        return true;
    }
}

