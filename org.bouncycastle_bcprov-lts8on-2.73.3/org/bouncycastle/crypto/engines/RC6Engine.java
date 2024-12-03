/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.engines.Utils;
import org.bouncycastle.crypto.params.KeyParameter;

public class RC6Engine
implements BlockCipher {
    private static final int wordSize = 32;
    private static final int bytesPerWord = 4;
    private static final int _noRounds = 20;
    private int[] _S = null;
    private static final int P32 = -1209970333;
    private static final int Q32 = -1640531527;
    private static final int LGW = 5;
    private boolean forEncryption;

    @Override
    public String getAlgorithmName() {
        return "RC6";
    }

    @Override
    public int getBlockSize() {
        return 16;
    }

    @Override
    public void init(boolean forEncryption, CipherParameters params) {
        if (!(params instanceof KeyParameter)) {
            throw new IllegalArgumentException("invalid parameter passed to RC6 init - " + params.getClass().getName());
        }
        KeyParameter p = (KeyParameter)params;
        this.forEncryption = forEncryption;
        byte[] key = p.getKey();
        this.setKey(key);
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), key.length * 8, params, Utils.getPurpose(forEncryption)));
    }

    @Override
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) {
        int blockSize = this.getBlockSize();
        if (this._S == null) {
            throw new IllegalStateException("RC6 engine not initialised");
        }
        if (inOff + blockSize > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff + blockSize > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        return this.forEncryption ? this.encryptBlock(in, inOff, out, outOff) : this.decryptBlock(in, inOff, out, outOff);
    }

    @Override
    public void reset() {
    }

    private void setKey(byte[] key) {
        int i;
        int c = (key.length + 3) / 4;
        if (c == 0) {
            c = 1;
        }
        int[] L = new int[(key.length + 4 - 1) / 4];
        for (i = key.length - 1; i >= 0; --i) {
            L[i / 4] = (L[i / 4] << 8) + (key[i] & 0xFF);
        }
        this._S = new int[44];
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
        int A = this.bytesToWord(in, inOff);
        int B = this.bytesToWord(in, inOff + 4);
        int C = this.bytesToWord(in, inOff + 8);
        int D = this.bytesToWord(in, inOff + 12);
        B += this._S[0];
        D += this._S[1];
        for (int i = 1; i <= 20; ++i) {
            int t = 0;
            int u = 0;
            t = B * (2 * B + 1);
            t = this.rotateLeft(t, 5);
            u = D * (2 * D + 1);
            u = this.rotateLeft(u, 5);
            A ^= t;
            A = this.rotateLeft(A, u);
            C ^= u;
            C = this.rotateLeft(C, t);
            int temp = A += this._S[2 * i];
            A = B;
            B = C += this._S[2 * i + 1];
            C = D;
            D = temp;
        }
        this.wordToBytes(A += this._S[42], out, outOff);
        this.wordToBytes(B, out, outOff + 4);
        this.wordToBytes(C += this._S[43], out, outOff + 8);
        this.wordToBytes(D, out, outOff + 12);
        return 16;
    }

    private int decryptBlock(byte[] in, int inOff, byte[] out, int outOff) {
        int A = this.bytesToWord(in, inOff);
        int B = this.bytesToWord(in, inOff + 4);
        int C = this.bytesToWord(in, inOff + 8);
        int D = this.bytesToWord(in, inOff + 12);
        C -= this._S[43];
        A -= this._S[42];
        for (int i = 20; i >= 1; --i) {
            int t = 0;
            int u = 0;
            int temp = D;
            D = C;
            C = B;
            B = A;
            A = temp;
            t = B * (2 * B + 1);
            t = this.rotateLeft(t, 5);
            u = D * (2 * D + 1);
            u = this.rotateLeft(u, 5);
            C -= this._S[2 * i + 1];
            C = this.rotateRight(C, t);
            C ^= u;
            A -= this._S[2 * i];
            A = this.rotateRight(A, u);
            A ^= t;
        }
        this.wordToBytes(A, out, outOff);
        this.wordToBytes(B -= this._S[0], out, outOff + 4);
        this.wordToBytes(C, out, outOff + 8);
        this.wordToBytes(D -= this._S[1], out, outOff + 12);
        return 16;
    }

    private int rotateLeft(int x, int y) {
        return x << y | x >>> -y;
    }

    private int rotateRight(int x, int y) {
        return x >>> y | x << -y;
    }

    private int bytesToWord(byte[] src, int srcOff) {
        int word = 0;
        for (int i = 3; i >= 0; --i) {
            word = (word << 8) + (src[i + srcOff] & 0xFF);
        }
        return word;
    }

    private void wordToBytes(int word, byte[] dst, int dstOff) {
        for (int i = 0; i < 4; ++i) {
            dst[i + dstOff] = (byte)word;
            word >>>= 8;
        }
    }
}

