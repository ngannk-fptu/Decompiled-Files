/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.i18n.text.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;

public class DynamicPushbackInputStream
extends PushbackInputStream {
    private final int origsize;

    public DynamicPushbackInputStream(InputStream in) {
        super(in);
        this.origsize = 1;
    }

    public DynamicPushbackInputStream(InputStream in, int initialSize) {
        super(in, initialSize);
        this.origsize = initialSize;
    }

    public int clear() {
        int m = this.buf.length;
        this.buf = new byte[this.origsize];
        this.pos = this.origsize;
        return m;
    }

    public int shrink() {
        int m;
        int l;
        int s;
        int p;
        byte[] old = this.buf;
        if (this.pos == 0) {
            return 0;
        }
        int n = old.length - this.pos;
        if (n < this.origsize) {
            this.buf = new byte[this.origsize];
            p = this.pos;
            s = this.origsize - n;
            l = old.length - p;
            m = old.length - this.origsize;
            this.pos = s;
        } else {
            this.buf = new byte[n];
            p = this.pos;
            s = 0;
            l = n;
            m = old.length - l;
            this.pos = 0;
        }
        System.arraycopy(old, p, this.buf, s, l);
        return m;
    }

    private void resize(int len) {
        byte[] old = this.buf;
        this.buf = new byte[old.length + len];
        System.arraycopy(old, 0, this.buf, len, old.length);
    }

    public void unread(byte[] b, int off, int len) throws IOException {
        if (len > this.pos && this.pos + len > this.buf.length) {
            this.resize(len - this.pos);
            this.pos += len - this.pos;
        }
        super.unread(b, off, len);
    }

    public void unread(int b) throws IOException {
        if (this.pos == 0) {
            this.resize(1);
            ++this.pos;
        }
        super.unread(b);
    }

    public int read() throws IOException {
        int m = super.read();
        if (this.pos >= this.buf.length && this.buf.length > this.origsize) {
            this.shrink();
        }
        return m;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        this.available();
        int r = super.read(b, off, len);
        if (this.pos >= this.buf.length && this.buf.length > this.origsize) {
            this.shrink();
        }
        return r;
    }

    public long skip(long n) throws IOException {
        long r = super.skip(n);
        if (this.pos >= this.buf.length && this.buf.length > this.origsize) {
            this.shrink();
        }
        return r;
    }
}

