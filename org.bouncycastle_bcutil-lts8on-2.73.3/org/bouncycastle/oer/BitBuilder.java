/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.oer;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import org.bouncycastle.util.Arrays;

public class BitBuilder {
    private static final byte[] bits = new byte[]{-128, 64, 32, 16, 8, 4, 2, 1};
    byte[] buf = new byte[1];
    int pos = 0;

    public BitBuilder writeBit(int bit) {
        if (this.pos / 8 >= this.buf.length) {
            byte[] newBytes = new byte[this.buf.length + 4];
            System.arraycopy(this.buf, 0, newBytes, 0, this.pos / 8);
            Arrays.clear((byte[])this.buf);
            this.buf = newBytes;
        }
        if (bit == 0) {
            int n = this.pos / 8;
            this.buf[n] = (byte)(this.buf[n] & ~bits[this.pos % 8]);
        } else {
            int n = this.pos / 8;
            this.buf[n] = (byte)(this.buf[n] | bits[this.pos % 8]);
        }
        ++this.pos;
        return this;
    }

    public BitBuilder writeBits(long value, int start) {
        for (int p = start - 1; p >= 0; --p) {
            int set = (value & 1L << p) > 0L ? 1 : 0;
            this.writeBit(set);
        }
        return this;
    }

    public BitBuilder writeBits(long value, int start, int len) {
        for (int p = start - 1; p >= start - len; --p) {
            int set = (value & 1L << p) != 0L ? 1 : 0;
            this.writeBit(set);
        }
        return this;
    }

    public int write(OutputStream outputStream) throws IOException {
        int l = (this.pos + this.pos % 8) / 8;
        outputStream.write(this.buf, 0, l);
        outputStream.flush();
        return l;
    }

    public int writeAndClear(OutputStream outputStream) throws IOException {
        int l = (this.pos + this.pos % 8) / 8;
        outputStream.write(this.buf, 0, l);
        outputStream.flush();
        this.zero();
        return l;
    }

    public void pad() {
        this.pos += this.pos % 8;
    }

    public void write7BitBytes(int value) {
        boolean writing = false;
        for (int t = 4; t >= 0; --t) {
            if (!writing && (value & 0xFE000000) != 0) {
                writing = true;
            }
            if (writing) {
                this.writeBit(t).writeBits(value, 32, 7);
            }
            value <<= 7;
        }
    }

    public void write7BitBytes(BigInteger value) {
        int size = (value.bitLength() + value.bitLength() % 8) / 8;
        BigInteger mask = BigInteger.valueOf(254L).shiftLeft(size * 8);
        boolean writing = false;
        for (int t = size; t >= 0; --t) {
            if (!writing && value.and(mask).compareTo(BigInteger.ZERO) != 0) {
                writing = true;
            }
            if (writing) {
                BigInteger b = value.and(mask).shiftRight(8 * size - 8);
                this.writeBit(t).writeBits(b.intValue(), 8, 7);
            }
            value = value.shiftLeft(7);
        }
    }

    public void zero() {
        Arrays.clear((byte[])this.buf);
        this.pos = 0;
    }
}

