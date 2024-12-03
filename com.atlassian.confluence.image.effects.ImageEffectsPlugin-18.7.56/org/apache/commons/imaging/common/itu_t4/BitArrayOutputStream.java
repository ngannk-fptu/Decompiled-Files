/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.common.itu_t4;

import java.io.OutputStream;

class BitArrayOutputStream
extends OutputStream {
    private byte[] buffer;
    private int bytesWritten;
    private int cache;
    private int cacheMask = 128;

    BitArrayOutputStream() {
        this.buffer = new byte[16];
    }

    BitArrayOutputStream(int size) {
        this.buffer = new byte[size];
    }

    public int size() {
        return this.bytesWritten;
    }

    public byte[] toByteArray() {
        this.flush();
        if (this.bytesWritten == this.buffer.length) {
            return this.buffer;
        }
        byte[] out = new byte[this.bytesWritten];
        System.arraycopy(this.buffer, 0, out, 0, this.bytesWritten);
        return out;
    }

    @Override
    public void close() {
        this.flush();
    }

    @Override
    public void flush() {
        if (this.cacheMask != 128) {
            this.writeByte(this.cache);
            this.cache = 0;
            this.cacheMask = 128;
        }
    }

    @Override
    public void write(int b) {
        this.flush();
        this.writeByte(b);
    }

    public void writeBit(int bit) {
        if (bit != 0) {
            this.cache |= this.cacheMask;
        }
        this.cacheMask >>>= 1;
        if (this.cacheMask == 0) {
            this.flush();
        }
    }

    public int getBitsAvailableInCurrentByte() {
        int count = 0;
        for (int mask = this.cacheMask; mask != 0; mask >>>= 1) {
            ++count;
        }
        return count;
    }

    private void writeByte(int b) {
        if (this.bytesWritten >= this.buffer.length) {
            byte[] bigger = new byte[this.buffer.length * 2];
            System.arraycopy(this.buffer, 0, bigger, 0, this.bytesWritten);
            this.buffer = bigger;
        }
        this.buffer[this.bytesWritten++] = (byte)b;
    }
}

