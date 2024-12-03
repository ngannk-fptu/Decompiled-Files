/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.io.input;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class BoundedInputStream
extends FilterInputStream {
    private final long maxCount;
    private long count;
    private long mark = -1L;
    private boolean propagateClose = true;

    public BoundedInputStream(InputStream in) {
        this(in, -1L);
    }

    public BoundedInputStream(InputStream inputStream, long maxLength) {
        super(inputStream);
        this.maxCount = maxLength;
    }

    @Override
    public int available() throws IOException {
        if (this.isMaxLength()) {
            this.onMaxLength(this.maxCount, this.count);
            return 0;
        }
        return this.in.available();
    }

    @Override
    public void close() throws IOException {
        if (this.propagateClose) {
            this.in.close();
        }
    }

    public long getCount() {
        return this.count;
    }

    public long getMaxLength() {
        return this.maxCount;
    }

    private boolean isMaxLength() {
        return this.maxCount >= 0L && this.count >= this.maxCount;
    }

    public boolean isPropagateClose() {
        return this.propagateClose;
    }

    @Override
    public synchronized void mark(int readlimit) {
        this.in.mark(readlimit);
        this.mark = this.count;
    }

    @Override
    public boolean markSupported() {
        return this.in.markSupported();
    }

    protected void onMaxLength(long maxLength, long count) throws IOException {
    }

    @Override
    public int read() throws IOException {
        if (this.isMaxLength()) {
            this.onMaxLength(this.maxCount, this.count);
            return -1;
        }
        int result = this.in.read();
        ++this.count;
        return result;
    }

    @Override
    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (this.isMaxLength()) {
            this.onMaxLength(this.maxCount, this.count);
            return -1;
        }
        long maxRead = this.maxCount >= 0L ? Math.min((long)len, this.maxCount - this.count) : (long)len;
        int bytesRead = this.in.read(b, off, (int)maxRead);
        if (bytesRead == -1) {
            return -1;
        }
        this.count += (long)bytesRead;
        return bytesRead;
    }

    @Override
    public synchronized void reset() throws IOException {
        this.in.reset();
        this.count = this.mark;
    }

    public void setPropagateClose(boolean propagateClose) {
        this.propagateClose = propagateClose;
    }

    @Override
    public long skip(long n) throws IOException {
        long toSkip = this.maxCount >= 0L ? Math.min(n, this.maxCount - this.count) : n;
        long skippedBytes = this.in.skip(toSkip);
        this.count += skippedBytes;
        return skippedBytes;
    }

    public String toString() {
        return this.in.toString();
    }
}

