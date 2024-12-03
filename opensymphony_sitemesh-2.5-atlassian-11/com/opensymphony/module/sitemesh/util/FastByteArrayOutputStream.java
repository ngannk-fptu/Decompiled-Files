/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

public class FastByteArrayOutputStream
extends ByteArrayOutputStream {
    private static final int DEFAULT_BLOCK_SIZE = 8192;
    private byte[] buffer;
    private LinkedList buffers;
    private int index;
    private int size;
    private int blockSize;

    public FastByteArrayOutputStream() {
        this(8192);
    }

    public FastByteArrayOutputStream(int aSize) {
        this.blockSize = aSize;
        this.buffer = new byte[this.blockSize];
    }

    public void writeTo(OutputStream out) throws IOException {
        if (this.buffers != null) {
            for (byte[] bytes : this.buffers) {
                out.write(bytes, 0, this.blockSize);
            }
        }
        out.write(this.buffer, 0, this.index);
    }

    public int size() {
        return this.size + this.index;
    }

    public byte[] toByteArray() {
        byte[] data = new byte[this.size()];
        int pos = 0;
        if (this.buffers != null) {
            for (byte[] bytes : this.buffers) {
                System.arraycopy(bytes, 0, data, pos, this.blockSize);
                pos += this.blockSize;
            }
        }
        System.arraycopy(this.buffer, 0, data, pos, this.index);
        return data;
    }

    public void write(int datum) {
        if (this.index == this.blockSize) {
            if (this.buffers == null) {
                this.buffers = new LinkedList();
            }
            this.buffers.addLast(this.buffer);
            this.buffer = new byte[this.blockSize];
            this.size += this.index;
            this.index = 0;
        }
        this.buffer[this.index++] = (byte)datum;
    }

    public void write(byte[] data, int offset, int length) {
        if (data == null) {
            throw new NullPointerException();
        }
        if (offset < 0 || offset + length > data.length || length < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (this.index + length >= this.blockSize) {
            for (int i = 0; i < length; ++i) {
                this.write(data[offset + i]);
            }
        } else {
            System.arraycopy(data, offset, this.buffer, this.index, length);
            this.index += length;
        }
    }

    public synchronized void reset() {
        this.buffer = new byte[this.blockSize];
        this.buffers = null;
    }

    public String toString(String enc) throws UnsupportedEncodingException {
        return new String(this.toByteArray(), enc);
    }

    public String toString() {
        return new String(this.toByteArray());
    }

    public void flush() throws IOException {
    }

    public void close() throws IOException {
    }
}

