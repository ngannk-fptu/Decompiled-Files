/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codec;

import com.sun.media.jai.codec.SeekableStream;
import java.io.IOException;

public class ByteArraySeekableStream
extends SeekableStream {
    private byte[] src;
    private int offset;
    private int length;
    private int pointer;

    public ByteArraySeekableStream(byte[] src, int offset, int length) throws IOException {
        this.src = src;
        this.offset = offset;
        this.length = length;
    }

    public ByteArraySeekableStream(byte[] src) throws IOException {
        this(src, 0, src.length);
    }

    public synchronized int available() {
        this.ensureOpen();
        return Math.min(this.offset + this.length, this.src.length) - this.pointer;
    }

    public boolean canSeekBackwards() {
        return true;
    }

    public long getFilePointer() {
        return this.pointer;
    }

    public void seek(long pos) {
        this.pointer = (int)pos;
    }

    public int read() {
        if (this.pointer < this.length + this.offset) {
            return this.src[this.pointer++ + this.offset] & 0xFF;
        }
        return -1;
    }

    public int read(byte[] b, int off, int len) {
        if (b == null) {
            throw new NullPointerException();
        }
        if (off < 0 || len < 0 || off + len > b.length) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0) {
            return 0;
        }
        int oldPointer = this.pointer;
        this.pointer = Math.min(this.pointer + len, this.length + this.offset);
        if (this.pointer == oldPointer) {
            return -1;
        }
        System.arraycopy(this.src, oldPointer, b, off, this.pointer - oldPointer);
        return this.pointer - oldPointer;
    }

    public int skipBytes(int n) {
        int oldPointer = this.pointer;
        this.pointer = Math.min(this.pointer + n, this.length + this.offset);
        return this.pointer - oldPointer;
    }

    public void close() {
    }

    public long length() {
        return this.length;
    }

    private void ensureOpen() {
    }
}

