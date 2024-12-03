/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.engines.Utils;
import org.bouncycastle.crypto.params.RC5Parameters;

public class RC564Engine
implements BlockCipher {
    private static final int wordSize = 64;
    private static final int bytesPerWord = 8;
    private int _noRounds = 12;
    private long[] _S = null;
    private static final long P64 = -5196783011329398165L;
    private static final long Q64 = -7046029254386353131L;
    private boolean forEncryption;

    @Override
    public String getAlgorithmName() {
        return "RC5-64";
    }

    @Override
    public int getBlockSize() {
        return 16;
    }

    @Override
    public void init(boolean forEncryption, CipherParameters params) {
        if (!(params instanceof RC5Parameters)) {
            throw new IllegalArgumentException("invalid parameter passed to RC564 init - " + params.getClass().getName());
        }
        RC5Parameters p = (RC5Parameters)params;
        this.forEncryption = forEncryption;
        this._noRounds = p.getRounds();
        byte[] key = p.getKey();
        this.setKey(key);
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), key.length * 8, params, Utils.getPurpose(forEncryption)));
    }

    @Override
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) {
        return this.forEncryption ? this.encryptBlock(in, inOff, out, outOff) : this.decryptBlock(in, inOff, out, outOff);
    }

    @Override
    public void reset() {
    }

    private void setKey(byte[] key) {
        int i;
        long[] L = new long[(key.length + 7) / 8];
        for (i = 0; i != key.length; ++i) {
            int n = i / 8;
            L[n] = L[n] + ((long)(key[i] & 0xFF) << 8 * (i % 8));
        }
        this._S = new long[2 * (this._noRounds + 1)];
        this._S[0] = -5196783011329398165L;
        for (i = 1; i < this._S.length; ++i) {
            this._S[i] = this._S[i - 1] + -7046029254386353131L;
        }
        int iter = L.length > this._S.length ? 3 * L.length : 3 * this._S.length;
        long A = 0L;
        long B = 0L;
        int i2 = 0;
        int j = 0;
        for (int k = 0; k < iter; ++k) {
            A = this._S[i2] = this.rotateLeft(this._S[i2] + A + B, 3L);
            B = L[j] = this.rotateLeft(L[j] + A + B, A + B);
            i2 = (i2 + 1) % this._S.length;
            j = (j + 1) % L.length;
        }
    }

    private int encryptBlock(byte[] in, int inOff, byte[] out, int outOff) {
        long A = this.bytesToWord(in, inOff) + this._S[0];
        long B = this.bytesToWord(in, inOff + 8) + this._S[1];
        for (int i = 1; i <= this._noRounds; ++i) {
            A = this.rotateLeft(A ^ B, B) + this._S[2 * i];
            B = this.rotateLeft(B ^ A, A) + this._S[2 * i + 1];
        }
        this.wordToBytes(A, out, outOff);
        this.wordToBytes(B, out, outOff + 8);
        return 16;
    }

    private int decryptBlock(byte[] in, int inOff, byte[] out, int outOff) {
        long A = this.bytesToWord(in, inOff);
        long B = this.bytesToWord(in, inOff + 8);
        for (int i = this._noRounds; i >= 1; --i) {
            B = this.rotateRight(B - this._S[2 * i + 1], A) ^ A;
            A = this.rotateRight(A - this._S[2 * i], B) ^ B;
        }
        this.wordToBytes(A - this._S[0], out, outOff);
        this.wordToBytes(B - this._S[1], out, outOff + 8);
        return 16;
    }

    private long rotateLeft(long x, long y) {
        return x << (int)(y & 0x3FL) | x >>> (int)(64L - (y & 0x3FL));
    }

    private long rotateRight(long x, long y) {
        return x >>> (int)(y & 0x3FL) | x << (int)(64L - (y & 0x3FL));
    }

    private long bytesToWord(byte[] src, int srcOff) {
        long word = 0L;
        for (int i = 7; i >= 0; --i) {
            word = (word << 8) + (long)(src[i + srcOff] & 0xFF);
        }
        return word;
    }

    private void wordToBytes(long word, byte[] dst, int dstOff) {
        for (int i = 0; i < 8; ++i) {
            dst[i + dstOff] = (byte)word;
            word >>>= 8;
        }
    }
}

