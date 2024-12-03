/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.server.io;

import java.io.IOException;
import java.io.InputStream;

public class BoundedInputStream
extends InputStream {
    private final InputStream in;
    private final int max;
    private int pos = 0;
    private int mark = -1;
    private boolean propagateClose = true;

    public BoundedInputStream(InputStream in, long size) {
        this.max = (int)size;
        this.in = in;
    }

    public BoundedInputStream(InputStream in) {
        this(in, -1L);
    }

    @Override
    public int read() throws IOException {
        if (this.max >= 0 && this.pos == this.max) {
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
        if (this.max >= 0 && this.pos >= this.max) {
            return -1;
        }
        int maxRead = this.max >= 0 ? Math.min(len, this.max - this.pos) : len;
        int bytesRead = this.in.read(b, off, maxRead);
        if (bytesRead == -1) {
            return -1;
        }
        this.pos += bytesRead;
        return bytesRead;
    }

    @Override
    public long skip(long n) throws IOException {
        long toSkip = this.max >= 0 ? Math.min(n, (long)(this.max - this.pos)) : n;
        long skippedBytes = this.in.skip(toSkip);
        this.pos = (int)((long)this.pos + skippedBytes);
        return skippedBytes;
    }

    @Override
    public int available() throws IOException {
        if (this.max >= 0 && this.pos >= this.max) {
            return 0;
        }
        return this.in.available();
    }

    public String toString() {
        return this.in.toString();
    }

    @Override
    public void close() throws IOException {
        if (this.propagateClose) {
            this.in.close();
        }
    }

    @Override
    public synchronized void reset() throws IOException {
        this.in.reset();
        this.pos = this.mark;
    }

    @Override
    public synchronized void mark(int readlimit) {
        this.in.mark(readlimit);
        this.mark = this.pos;
    }

    @Override
    public boolean markSupported() {
        return this.in.markSupported();
    }

    public boolean isPropagateClose() {
        return this.propagateClose;
    }

    public void setPropagateClose(boolean propagateClose) {
        this.propagateClose = propagateClose;
    }
}

