/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.io;

import com.ctc.wstx.api.ReaderConfig;
import java.io.IOException;
import java.io.Reader;

public final class MergedReader
extends Reader {
    final ReaderConfig mConfig;
    final Reader mIn;
    char[] mData;
    int mPtr;
    final int mEnd;

    public MergedReader(ReaderConfig cfg, Reader in, char[] buf, int start, int end) {
        this.mConfig = cfg;
        this.mIn = in;
        this.mData = buf;
        this.mPtr = start;
        this.mEnd = end;
        if (buf != null && start >= end) {
            throw new IllegalArgumentException("Trying to construct MergedReader with empty contents (start " + start + ", end " + end + ")");
        }
    }

    public void close() throws IOException {
        this.freeMergedBuffer();
        this.mIn.close();
    }

    public void mark(int readlimit) throws IOException {
        if (this.mData == null) {
            this.mIn.mark(readlimit);
        }
    }

    public boolean markSupported() {
        return this.mData == null && this.mIn.markSupported();
    }

    public int read() throws IOException {
        if (this.mData != null) {
            int c = this.mData[this.mPtr++] & 0xFF;
            if (this.mPtr >= this.mEnd) {
                this.freeMergedBuffer();
            }
            return c;
        }
        return this.mIn.read();
    }

    public int read(char[] cbuf) throws IOException {
        return this.read(cbuf, 0, cbuf.length);
    }

    public int read(char[] cbuf, int off, int len) throws IOException {
        if (this.mData != null) {
            int avail = this.mEnd - this.mPtr;
            if (len > avail) {
                len = avail;
            }
            System.arraycopy(this.mData, this.mPtr, cbuf, off, len);
            this.mPtr += len;
            if (this.mPtr >= this.mEnd) {
                this.freeMergedBuffer();
            }
            return len;
        }
        return this.mIn.read(cbuf, off, len);
    }

    public boolean ready() throws IOException {
        return this.mData != null || this.mIn.ready();
    }

    public void reset() throws IOException {
        if (this.mData == null) {
            this.mIn.reset();
        }
    }

    public long skip(long n) throws IOException {
        long count = 0L;
        if (this.mData != null) {
            int amount = this.mEnd - this.mPtr;
            if ((long)amount > n) {
                this.mPtr += (int)n;
                return amount;
            }
            this.freeMergedBuffer();
            count += (long)amount;
            n -= (long)amount;
        }
        if (n > 0L) {
            count += this.mIn.skip(n);
        }
        return count;
    }

    private void freeMergedBuffer() {
        if (this.mData != null) {
            char[] data = this.mData;
            this.mData = null;
            if (this.mConfig != null) {
                this.mConfig.freeSmallCBuffer(data);
            }
        }
    }
}

