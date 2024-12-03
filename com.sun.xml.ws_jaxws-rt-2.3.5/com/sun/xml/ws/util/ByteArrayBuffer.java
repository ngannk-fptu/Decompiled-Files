/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ByteArrayBuffer
extends OutputStream {
    protected byte[] buf;
    private int count;
    private static final int CHUNK_SIZE = 4096;

    public ByteArrayBuffer() {
        this(32);
    }

    public ByteArrayBuffer(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException();
        }
        this.buf = new byte[size];
    }

    public ByteArrayBuffer(byte[] data) {
        this(data, data.length);
    }

    public ByteArrayBuffer(byte[] data, int length) {
        this.buf = data;
        this.count = length;
    }

    public final void write(InputStream in) throws IOException {
        int cap;
        int sz;
        while ((sz = in.read(this.buf, this.count, cap = this.buf.length - this.count)) >= 0) {
            this.count += sz;
            if (cap != sz) continue;
            this.ensureCapacity(this.buf.length * 2);
        }
        return;
    }

    @Override
    public final void write(int b) {
        int newcount = this.count + 1;
        this.ensureCapacity(newcount);
        this.buf[this.count] = (byte)b;
        this.count = newcount;
    }

    @Override
    public final void write(byte[] b, int off, int len) {
        int newcount = this.count + len;
        this.ensureCapacity(newcount);
        System.arraycopy(b, off, this.buf, this.count, len);
        this.count = newcount;
    }

    private void ensureCapacity(int newcount) {
        if (newcount > this.buf.length) {
            byte[] newbuf = new byte[Math.max(this.buf.length << 1, newcount)];
            System.arraycopy(this.buf, 0, newbuf, 0, this.count);
            this.buf = newbuf;
        }
    }

    public final void writeTo(OutputStream out) throws IOException {
        int remaining = this.count;
        int off = 0;
        while (remaining > 0) {
            int chunk = remaining > 4096 ? 4096 : remaining;
            out.write(this.buf, off, chunk);
            remaining -= chunk;
            off += chunk;
        }
    }

    public final void reset() {
        this.count = 0;
    }

    public final byte[] toByteArray() {
        byte[] newbuf = new byte[this.count];
        System.arraycopy(this.buf, 0, newbuf, 0, this.count);
        return newbuf;
    }

    public final int size() {
        return this.count;
    }

    public final byte[] getRawData() {
        return this.buf;
    }

    @Override
    public void close() throws IOException {
    }

    public final InputStream newInputStream() {
        return new ByteArrayInputStream(this.buf, 0, this.count);
    }

    public final InputStream newInputStream(int start, int length) {
        return new ByteArrayInputStream(this.buf, start, length);
    }

    public String toString() {
        return new String(this.buf, 0, this.count);
    }
}

