/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public final class FastByteArrayOutputStream
extends ByteArrayOutputStream {
    public FastByteArrayOutputStream(int n) {
        super(n);
    }

    public FastByteArrayOutputStream(byte[] byArray) {
        super(0);
        this.buf = byArray;
        this.count = byArray.length;
    }

    @Override
    public void write(byte[] byArray, int n, int n2) {
        if (n < 0 || n > byArray.length || n2 < 0 || n + n2 > byArray.length || n + n2 < 0) {
            throw new IndexOutOfBoundsException();
        }
        if (n2 == 0) {
            return;
        }
        int n3 = this.count + n2;
        this.growIfNeeded(n3);
        System.arraycopy(byArray, n, this.buf, this.count, n2);
        this.count = n3;
    }

    @Override
    public void write(int n) {
        int n2 = this.count + 1;
        this.growIfNeeded(n2);
        this.buf[this.count] = (byte)n;
        this.count = n2;
    }

    private void growIfNeeded(int n) {
        if (n > this.buf.length) {
            int n2 = Math.max(this.buf.length << 1, n);
            this.buf = Arrays.copyOf(this.buf, n2);
        }
    }

    @Override
    public void writeTo(OutputStream outputStream) throws IOException {
        outputStream.write(this.buf, 0, this.count);
    }

    @Override
    public byte[] toByteArray() {
        return Arrays.copyOf(this.buf, this.count);
    }

    public ByteArrayInputStream createInputStream() {
        return new ByteArrayInputStream(this.buf, 0, this.count);
    }
}

