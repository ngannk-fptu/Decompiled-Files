/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.io;

import java.io.IOException;
import java.io.InputStream;

public class BoundedInputStream
extends InputStream {
    private static final int EOF = -1;
    private final long max;
    private final InputStream in;
    private long pos;

    public BoundedInputStream(long max, InputStream in) {
        this.max = max;
        this.in = in;
    }

    @Override
    public int read() throws IOException {
        if (this.max >= 0L && this.pos >= this.max) {
            return -1;
        }
        int result = this.in.read();
        ++this.pos;
        return result;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (this.max >= 0L && this.pos >= this.max) {
            return -1;
        }
        long maxRead = this.max >= 0L ? Math.min((long)len, this.max - this.pos) : (long)len;
        int bytesRead = this.in.read(b, off, (int)maxRead);
        if (bytesRead == -1) {
            return -1;
        }
        this.pos += (long)bytesRead;
        return bytesRead;
    }

    @Override
    public long skip(long n) throws IOException {
        long toSkip = this.max >= 0L ? Math.min(n, this.max - this.pos) : n;
        long skippedBytes = this.in.skip(toSkip);
        this.pos += skippedBytes;
        return skippedBytes;
    }

    @Override
    public void reset() throws IOException {
        this.in.reset();
        this.pos = 0L;
    }

    @Override
    public void mark(int readLimit) {
        this.in.mark(readLimit);
    }

    public boolean hasHitBound() {
        return this.pos >= this.max;
    }
}

