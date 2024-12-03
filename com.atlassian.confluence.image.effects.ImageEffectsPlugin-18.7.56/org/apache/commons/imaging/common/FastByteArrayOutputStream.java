/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.common;

import java.io.IOException;
import java.io.OutputStream;

class FastByteArrayOutputStream
extends OutputStream {
    private final byte[] bytes;
    private int count;

    FastByteArrayOutputStream(int length) {
        this.bytes = new byte[length];
    }

    @Override
    public void write(int value) throws IOException {
        if (this.count >= this.bytes.length) {
            throw new IOException("Write exceeded expected length (" + this.count + ", " + this.bytes.length + ")");
        }
        this.bytes[this.count] = (byte)value;
        ++this.count;
    }

    public byte[] toByteArray() {
        if (this.count < this.bytes.length) {
            byte[] result = new byte[this.count];
            System.arraycopy(this.bytes, 0, result, 0, this.count);
            return result;
        }
        return this.bytes;
    }

    public int getBytesWritten() {
        return this.count;
    }
}

