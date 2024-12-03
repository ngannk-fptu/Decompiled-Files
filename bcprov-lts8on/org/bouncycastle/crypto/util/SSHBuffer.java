/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.util;

import java.math.BigInteger;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Strings;

class SSHBuffer {
    private final byte[] buffer;
    private int pos = 0;

    public SSHBuffer(byte[] magic, byte[] buffer) {
        this.buffer = buffer;
        for (int i = 0; i != magic.length; ++i) {
            if (magic[i] == buffer[i]) continue;
            throw new IllegalArgumentException("magic-number incorrect");
        }
        this.pos += magic.length;
    }

    public SSHBuffer(byte[] buffer) {
        this.buffer = buffer;
    }

    public int readU32() {
        if (this.pos > this.buffer.length - 4) {
            throw new IllegalArgumentException("4 bytes for U32 exceeds buffer.");
        }
        int i = (this.buffer[this.pos++] & 0xFF) << 24;
        i |= (this.buffer[this.pos++] & 0xFF) << 16;
        i |= (this.buffer[this.pos++] & 0xFF) << 8;
        return i |= this.buffer[this.pos++] & 0xFF;
    }

    public String readString() {
        return Strings.fromByteArray(this.readBlock());
    }

    public byte[] readBlock() {
        int len = this.readU32();
        if (len == 0) {
            return new byte[0];
        }
        if (this.pos > this.buffer.length - len) {
            throw new IllegalArgumentException("not enough data for block");
        }
        int start = this.pos;
        this.pos += len;
        return Arrays.copyOfRange(this.buffer, start, this.pos);
    }

    public void skipBlock() {
        int len = this.readU32();
        if (this.pos > this.buffer.length - len) {
            throw new IllegalArgumentException("not enough data for block");
        }
        this.pos += len;
    }

    public byte[] readPaddedBlock() {
        return this.readPaddedBlock(8);
    }

    public byte[] readPaddedBlock(int blockSize) {
        int lastByte;
        int len = this.readU32();
        if (len == 0) {
            return new byte[0];
        }
        if (this.pos > this.buffer.length - len) {
            throw new IllegalArgumentException("not enough data for block");
        }
        int align = len % blockSize;
        if (0 != align) {
            throw new IllegalArgumentException("missing padding");
        }
        int start = this.pos;
        this.pos += len;
        int end = this.pos;
        if (len > 0 && 0 < (lastByte = this.buffer[this.pos - 1] & 0xFF) && lastByte < blockSize) {
            int padCount = lastByte;
            int i = 1;
            int padPos = end -= padCount;
            while (i <= padCount) {
                if (i != (this.buffer[padPos] & 0xFF)) {
                    throw new IllegalArgumentException("incorrect padding");
                }
                ++i;
                ++padPos;
            }
        }
        return Arrays.copyOfRange(this.buffer, start, end);
    }

    public BigInteger readBigNumPositive() {
        int len = this.readU32();
        if (this.pos + len > this.buffer.length) {
            throw new IllegalArgumentException("not enough data for big num");
        }
        int start = this.pos;
        this.pos += len;
        byte[] d = Arrays.copyOfRange(this.buffer, start, this.pos);
        return new BigInteger(1, d);
    }

    public byte[] getBuffer() {
        return Arrays.clone(this.buffer);
    }

    public boolean hasRemaining() {
        return this.pos < this.buffer.length;
    }
}

