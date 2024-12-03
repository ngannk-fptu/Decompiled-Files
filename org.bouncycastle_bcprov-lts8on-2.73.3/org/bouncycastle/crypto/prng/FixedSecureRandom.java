/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.prng;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;

public class FixedSecureRandom
extends SecureRandom {
    private byte[] _data;
    private int _index;
    private int _intPad;

    public FixedSecureRandom(byte[] value) {
        this(false, new byte[][]{value});
    }

    public FixedSecureRandom(byte[][] values) {
        this(false, values);
    }

    public FixedSecureRandom(boolean intPad, byte[] value) {
        this(intPad, new byte[][]{value});
    }

    public FixedSecureRandom(boolean intPad, byte[][] values) {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        for (int i = 0; i != values.length; ++i) {
            try {
                bOut.write(values[i]);
                continue;
            }
            catch (IOException e) {
                throw new IllegalArgumentException("can't save value array.");
            }
        }
        this._data = bOut.toByteArray();
        if (intPad) {
            this._intPad = this._data.length % 4;
        }
    }

    @Override
    public void nextBytes(byte[] bytes) {
        System.arraycopy(this._data, this._index, bytes, 0, bytes.length);
        this._index += bytes.length;
    }

    @Override
    public byte[] generateSeed(int numBytes) {
        byte[] bytes = new byte[numBytes];
        this.nextBytes(bytes);
        return bytes;
    }

    @Override
    public int nextInt() {
        int val = 0;
        val |= this.nextValue() << 24;
        val |= this.nextValue() << 16;
        if (this._intPad == 2) {
            --this._intPad;
        } else {
            val |= this.nextValue() << 8;
        }
        if (this._intPad == 1) {
            --this._intPad;
        } else {
            val |= this.nextValue();
        }
        return val;
    }

    @Override
    public long nextLong() {
        long val = 0L;
        val |= (long)this.nextValue() << 56;
        val |= (long)this.nextValue() << 48;
        val |= (long)this.nextValue() << 40;
        val |= (long)this.nextValue() << 32;
        val |= (long)this.nextValue() << 24;
        val |= (long)this.nextValue() << 16;
        val |= (long)this.nextValue() << 8;
        return val |= (long)this.nextValue();
    }

    public boolean isExhausted() {
        return this._index == this._data.length;
    }

    private int nextValue() {
        return this._data[this._index++] & 0xFF;
    }
}

