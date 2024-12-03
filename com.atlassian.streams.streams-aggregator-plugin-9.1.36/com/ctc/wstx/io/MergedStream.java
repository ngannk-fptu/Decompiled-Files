/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.io;

import com.ctc.wstx.api.ReaderConfig;
import java.io.IOException;
import java.io.InputStream;

public final class MergedStream
extends InputStream {
    final ReaderConfig mConfig;
    final InputStream mIn;
    byte[] mData;
    int mPtr;
    final int mEnd;

    public MergedStream(ReaderConfig cfg, InputStream in, byte[] buf, int start, int end) {
        this.mConfig = cfg;
        this.mIn = in;
        this.mData = buf;
        this.mPtr = start;
        this.mEnd = end;
    }

    public int available() throws IOException {
        if (this.mData != null) {
            return this.mEnd - this.mPtr;
        }
        return this.mIn.available();
    }

    public void close() throws IOException {
        this.freeMergedBuffer();
        this.mIn.close();
    }

    public void mark(int readlimit) {
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

    public int read(byte[] b) throws IOException {
        return this.read(b, 0, b.length);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (this.mData != null) {
            int avail = this.mEnd - this.mPtr;
            if (len > avail) {
                len = avail;
            }
            System.arraycopy(this.mData, this.mPtr, b, off, len);
            this.mPtr += len;
            if (this.mPtr >= this.mEnd) {
                this.freeMergedBuffer();
            }
            return len;
        }
        return this.mIn.read(b, off, len);
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
                return n;
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
            byte[] data = this.mData;
            this.mData = null;
            if (this.mConfig != null) {
                this.mConfig.freeFullBBuffer(data);
            }
        }
    }
}

