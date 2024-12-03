/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.fpe;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.params.FPEParameters;
import org.bouncycastle.util.Pack;

public abstract class FPEEngine {
    protected final BlockCipher baseCipher;
    protected boolean forEncryption;
    protected FPEParameters fpeParameters;

    protected FPEEngine(BlockCipher baseCipher) {
        this.baseCipher = baseCipher;
    }

    public int processBlock(byte[] inBuf, int inOff, int length, byte[] outBuf, int outOff) {
        if (this.fpeParameters == null) {
            throw new IllegalStateException("FPE engine not initialized");
        }
        if (length < 0) {
            throw new IllegalArgumentException("input length cannot be negative");
        }
        if (inBuf == null || outBuf == null) {
            throw new NullPointerException("buffer value is null");
        }
        if (inBuf.length < inOff + length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outBuf.length < outOff + length) {
            throw new OutputLengthException("output buffer too short");
        }
        if (this.forEncryption) {
            return this.encryptBlock(inBuf, inOff, length, outBuf, outOff);
        }
        return this.decryptBlock(inBuf, inOff, length, outBuf, outOff);
    }

    protected static short[] toShortArray(byte[] buf) {
        if ((buf.length & 1) != 0) {
            throw new IllegalArgumentException("data must be an even number of bytes for a wide radix");
        }
        short[] rv = new short[buf.length / 2];
        for (int i = 0; i != rv.length; ++i) {
            rv[i] = Pack.bigEndianToShort(buf, i * 2);
        }
        return rv;
    }

    protected static byte[] toByteArray(short[] buf) {
        byte[] rv = new byte[buf.length * 2];
        for (int i = 0; i != buf.length; ++i) {
            Pack.shortToBigEndian(buf[i], rv, i * 2);
        }
        return rv;
    }

    public abstract void init(boolean var1, CipherParameters var2);

    public abstract String getAlgorithmName();

    protected abstract int encryptBlock(byte[] var1, int var2, int var3, byte[] var4, int var5);

    protected abstract int decryptBlock(byte[] var1, int var2, int var3, byte[] var4, int var5);
}

