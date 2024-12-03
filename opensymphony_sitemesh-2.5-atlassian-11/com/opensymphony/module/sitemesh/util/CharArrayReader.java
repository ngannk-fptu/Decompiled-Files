/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.util;

import java.io.IOException;
import java.io.Reader;

public class CharArrayReader
extends Reader {
    protected char[] buf;
    protected int pos;
    protected int markedPos = 0;
    protected int count;

    public CharArrayReader(char[] buf) {
        this.buf = buf;
        this.pos = 0;
        this.count = buf.length;
    }

    public CharArrayReader(char[] buf, int offset, int length) {
        if (offset < 0 || offset > buf.length || length < 0 || offset + length < 0) {
            throw new IllegalArgumentException();
        }
        this.buf = buf;
        this.pos = offset;
        this.count = Math.min(offset + length, buf.length);
        this.markedPos = offset;
    }

    public int read() throws IOException {
        if (this.pos >= this.count) {
            return -1;
        }
        return this.buf[this.pos++];
    }

    public int read(char[] b, int off, int len) throws IOException {
        if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return 0;
        }
        if (this.pos >= this.count) {
            return -1;
        }
        if (this.pos + len > this.count) {
            len = this.count - this.pos;
        }
        if (len <= 0) {
            return 0;
        }
        System.arraycopy(this.buf, this.pos, b, off, len);
        this.pos += len;
        return len;
    }

    public long skip(long n) throws IOException {
        if ((long)this.pos + n > (long)this.count) {
            n = this.count - this.pos;
        }
        if (n < 0L) {
            return 0L;
        }
        this.pos = (int)((long)this.pos + n);
        return n;
    }

    public boolean ready() throws IOException {
        return this.count - this.pos > 0;
    }

    public boolean markSupported() {
        return true;
    }

    public void mark(int readAheadLimit) throws IOException {
        this.markedPos = this.pos;
    }

    public void reset() throws IOException {
        this.pos = this.markedPos;
    }

    public void close() {
        this.buf = null;
    }
}

