/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.messaging.saaj.util;

import com.sun.xml.messaging.saaj.util.ByteInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class ByteOutputStream
extends OutputStream {
    protected byte[] buf;
    protected int count = 0;

    public ByteOutputStream() {
        this(1024);
    }

    public ByteOutputStream(int size) {
        this.buf = new byte[size];
    }

    public void write(InputStream in) throws IOException {
        if (in instanceof ByteArrayInputStream) {
            int size = in.available();
            this.ensureCapacity(size);
            this.count += in.read(this.buf, this.count, size);
            return;
        }
        int cap;
        int sz;
        while ((sz = in.read(this.buf, this.count, cap = this.buf.length - this.count)) >= 0) {
            this.count += sz;
            if (cap != sz) continue;
            this.ensureCapacity(this.count);
        }
        return;
    }

    @Override
    public void write(int b) {
        this.ensureCapacity(1);
        this.buf[this.count] = (byte)b;
        ++this.count;
    }

    private void ensureCapacity(int space) {
        int newcount = space + this.count;
        if (newcount > this.buf.length) {
            byte[] newbuf = new byte[Math.max(this.buf.length << 1, newcount)];
            System.arraycopy(this.buf, 0, newbuf, 0, this.count);
            this.buf = newbuf;
        }
    }

    @Override
    public void write(byte[] b, int off, int len) {
        this.ensureCapacity(len);
        System.arraycopy(b, off, this.buf, this.count, len);
        this.count += len;
    }

    @Override
    public void write(byte[] b) {
        this.write(b, 0, b.length);
    }

    public void writeAsAscii(String s) {
        int len = s.length();
        this.ensureCapacity(len);
        int ptr = this.count;
        for (int i = 0; i < len; ++i) {
            this.buf[ptr++] = (byte)s.charAt(i);
        }
        this.count = ptr;
    }

    public void writeTo(OutputStream out) throws IOException {
        out.write(this.buf, 0, this.count);
    }

    public void reset() {
        this.count = 0;
    }

    @Deprecated
    public byte[] toByteArray() {
        byte[] newbuf = new byte[this.count];
        System.arraycopy(this.buf, 0, newbuf, 0, this.count);
        return newbuf;
    }

    public int size() {
        return this.count;
    }

    public ByteInputStream newInputStream() {
        return new ByteInputStream(this.buf, this.count);
    }

    public String toString() {
        return new String(this.buf, 0, this.count);
    }

    @Override
    public void close() {
    }

    public byte[] getBytes() {
        return this.buf;
    }

    public int getCount() {
        return this.count;
    }
}

