/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import org.bouncycastle.util.Strings;

class SSHBuilder {
    private final ByteArrayOutputStream bos = new ByteArrayOutputStream();

    SSHBuilder() {
    }

    public void u32(int value) {
        this.bos.write(value >>> 24 & 0xFF);
        this.bos.write(value >>> 16 & 0xFF);
        this.bos.write(value >>> 8 & 0xFF);
        this.bos.write(value & 0xFF);
    }

    public void writeBigNum(BigInteger n) {
        this.writeBlock(n.toByteArray());
    }

    public void writeBlock(byte[] value) {
        this.u32(value.length);
        try {
            this.bos.write(value);
        }
        catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public void writeBytes(byte[] value) {
        try {
            this.bos.write(value);
        }
        catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public void writeString(String str) {
        this.writeBlock(Strings.toByteArray(str));
    }

    public byte[] getBytes() {
        return this.bos.toByteArray();
    }

    public byte[] getPaddedBytes() {
        return this.getPaddedBytes(8);
    }

    public byte[] getPaddedBytes(int blockSize) {
        int align = this.bos.size() % blockSize;
        if (0 != align) {
            int padCount = blockSize - align;
            for (int i = 1; i <= padCount; ++i) {
                this.bos.write(i);
            }
        }
        return this.bos.toByteArray();
    }
}

