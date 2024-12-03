/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.engines.Utils;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.RC5Parameters;

public class RC532Engine
implements BlockCipher {
    private int _noRounds = 12;
    private int[] _S = null;
    private static final int P32 = -1209970333;
    private static final int Q32 = -1640531527;
    private boolean forEncryption;

    @Override
    public String getAlgorithmName() {
        return "RC5-32";
    }

    @Override
    public int getBlockSize() {
        return 8;
    }

    @Override
    public void init(boolean forEncryption, CipherParameters params) {
        byte[] key;
        if (params instanceof RC5Parameters) {
            RC5Parameters p = (RC5Parameters)params;
            this._noRounds = p.getRounds();
            key = p.getKey();
            this.setKey(key);
        } else if (params instanceof KeyParameter) {
            KeyParameter p = (KeyParameter)params;
            key = p.getKey();
            this.setKey(key);
        } else {
            throw new IllegalArgumentException("invalid parameter passed to RC532 init - " + params.getClass().getName());
        }
        this.forEncryption = forEncryption;
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
        int[] L = new int[(key.length + 3) / 4];
        for (i = 0; i != key.length; ++i) {
            int n = i / 4;
            L[n] = L[n] + ((key[i] & 0xFF) << 8 * (i % 4));
        }
        this._S = new int[2 * (this._noRounds + 1)];
        this._S[0] = -1209970333;
        for (i = 1; i < this._S.length; ++i) {
            this._S[i] = this._S[i - 1] + -1640531527;
        }
        int iter = L.length > this._S.length ? 3 * L.length : 3 * this._S.length;
        int A = 0;
        int B = 0;
        int i2 = 0;
        int j = 0;
        for (int k = 0; k < iter; ++k) {
            A = this._S[i2] = this.rotateLeft(this._S[i2] + A + B, 3);
            B = L[j] = this.rotateLeft(L[j] + A + B, A + B);
            i2 = (i2 + 1) % this._S.length;
            j = (j + 1) % L.length;
        }
    }

    private int encryptBlock(byte[] in, int inOff, byte[] out, int outOff) {
        int A = this.bytesToWord(in, inOff) + this._S[0];
        int B = this.bytesToWord(in, inOff + 4) + this._S[1];
        for (int i = 1; i <= this._noRounds; ++i) {
            A = this.rotateLeft(A ^ B, B) + this._S[2 * i];
            B = this.rotateLeft(B ^ A, A) + this._S[2 * i + 1];
        }
        this.wordToBytes(A, out, outOff);
        this.wordToBytes(B, out, outOff + 4);
        return 8;
    }

    private int decryptBlock(byte[] in, int inOff, byte[] out, int outOff) {
        int A = this.bytesToWord(in, inOff);
        int B = this.bytesToWord(in, inOff + 4);
        for (int i = this._noRounds; i >= 1; --i) {
            B = this.rotateRight(B - this._S[2 * i + 1], A) ^ A;
            A = this.rotateRight(A - this._S[2 * i], B) ^ B;
        }
        this.wordToBytes(A - this._S[0], out, outOff);
        this.wordToBytes(B - this._S[1], out, outOff + 4);
        return 8;
    }

    private int rotateLeft(int x, int y) {
        return x << (y & 0x1F) | x >>> 32 - (y & 0x1F);
    }

    private int rotateRight(int x, int y) {
        return x >>> (y & 0x1F) | x << 32 - (y & 0x1F);
    }

    private int bytesToWord(byte[] src, int srcOff) {
        return src[srcOff] & 0xFF | (src[srcOff + 1] & 0xFF) << 8 | (src[srcOff + 2] & 0xFF) << 16 | (src[srcOff + 3] & 0xFF) << 24;
    }

    private void wordToBytes(int word, byte[] dst, int dstOff) {
        dst[dstOff] = (byte)word;
        dst[dstOff + 1] = (byte)(word >> 8);
        dst[dstOff + 2] = (byte)(word >> 16);
        dst[dstOff + 3] = (byte)(word >> 24);
    }
}

