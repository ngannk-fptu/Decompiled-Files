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
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Pack;

public class NoekeonEngine
implements BlockCipher {
    private static final int SIZE = 16;
    private static final byte[] roundConstants = new byte[]{-128, 27, 54, 108, -40, -85, 77, -102, 47, 94, -68, 99, -58, -105, 53, 106, -44};
    private final int[] k = new int[4];
    private boolean _initialised = false;
    private boolean _forEncryption;

    @Override
    public String getAlgorithmName() {
        return "Noekeon";
    }

    @Override
    public int getBlockSize() {
        return 16;
    }

    @Override
    public void init(boolean forEncryption, CipherParameters params) {
        if (!(params instanceof KeyParameter)) {
            throw new IllegalArgumentException("invalid parameter passed to Noekeon init - " + params.getClass().getName());
        }
        KeyParameter p = (KeyParameter)params;
        byte[] key = p.getKey();
        if (key.length != 16) {
            throw new IllegalArgumentException("Key length not 128 bits.");
        }
        Pack.bigEndianToInt(key, 0, this.k, 0, 4);
        if (!forEncryption) {
            int a0 = this.k[0];
            int a1 = this.k[1];
            int a2 = this.k[2];
            int a3 = this.k[3];
            int t02 = a0 ^ a2;
            t02 ^= Integers.rotateLeft(t02, 8) ^ Integers.rotateLeft(t02, 24);
            int t13 = a1 ^ a3;
            t13 ^= Integers.rotateLeft(t13, 8) ^ Integers.rotateLeft(t13, 24);
            this.k[0] = a0 ^= t13;
            this.k[1] = a1 ^= t02;
            this.k[2] = a2 ^= t13;
            this.k[3] = a3 ^= t02;
        }
        this._forEncryption = forEncryption;
        this._initialised = true;
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), 128, params, Utils.getPurpose(forEncryption)));
    }

    @Override
    public int processBlock(byte[] in, int inOff, byte[] out, int outOff) {
        if (!this._initialised) {
            throw new IllegalStateException(this.getAlgorithmName() + " not initialised");
        }
        if (inOff > in.length - 16) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff > out.length - 16) {
            throw new OutputLengthException("output buffer too short");
        }
        return this._forEncryption ? this.encryptBlock(in, inOff, out, outOff) : this.decryptBlock(in, inOff, out, outOff);
    }

    @Override
    public void reset() {
    }

    private int encryptBlock(byte[] in, int inOff, byte[] out, int outOff) {
        int a0 = Pack.bigEndianToInt(in, inOff);
        int a1 = Pack.bigEndianToInt(in, inOff + 4);
        int a2 = Pack.bigEndianToInt(in, inOff + 8);
        int a3 = Pack.bigEndianToInt(in, inOff + 12);
        int k0 = this.k[0];
        int k1 = this.k[1];
        int k2 = this.k[2];
        int k3 = this.k[3];
        int round = 0;
        while (true) {
            int t02 = (a0 ^= roundConstants[round] & 0xFF) ^ a2;
            t02 ^= Integers.rotateLeft(t02, 8) ^ Integers.rotateLeft(t02, 24);
            a0 ^= k0;
            a2 ^= k2;
            int t13 = (a1 ^= k1) ^ (a3 ^= k3);
            t13 ^= Integers.rotateLeft(t13, 8) ^ Integers.rotateLeft(t13, 24);
            a0 ^= t13;
            a1 ^= t02;
            a2 ^= t13;
            a3 ^= t02;
            if (++round > 16) break;
            a1 = Integers.rotateLeft(a1, 1);
            a2 = Integers.rotateLeft(a2, 5);
            int t = a3 = Integers.rotateLeft(a3, 2);
            a1 ^= a3 | a2;
            a3 = a0 ^ a2 & ~a1;
            a2 = t ^ ~a1 ^ a2 ^ a3;
            a0 = t ^ a2 & (a1 ^= a3 | a2);
            a1 = Integers.rotateLeft(a1, 31);
            a2 = Integers.rotateLeft(a2, 27);
            a3 = Integers.rotateLeft(a3, 30);
        }
        Pack.intToBigEndian(a0, out, outOff);
        Pack.intToBigEndian(a1, out, outOff + 4);
        Pack.intToBigEndian(a2, out, outOff + 8);
        Pack.intToBigEndian(a3, out, outOff + 12);
        return 16;
    }

    private int decryptBlock(byte[] in, int inOff, byte[] out, int outOff) {
        int a0 = Pack.bigEndianToInt(in, inOff);
        int a1 = Pack.bigEndianToInt(in, inOff + 4);
        int a2 = Pack.bigEndianToInt(in, inOff + 8);
        int a3 = Pack.bigEndianToInt(in, inOff + 12);
        int k0 = this.k[0];
        int k1 = this.k[1];
        int k2 = this.k[2];
        int k3 = this.k[3];
        int round = 16;
        while (true) {
            int t02 = a0 ^ a2;
            t02 ^= Integers.rotateLeft(t02, 8) ^ Integers.rotateLeft(t02, 24);
            a0 ^= k0;
            a2 ^= k2;
            int t13 = (a1 ^= k1) ^ (a3 ^= k3);
            t13 ^= Integers.rotateLeft(t13, 8) ^ Integers.rotateLeft(t13, 24);
            a0 ^= t13;
            a1 ^= t02;
            a2 ^= t13;
            a3 ^= t02;
            a0 ^= roundConstants[round] & 0xFF;
            if (--round < 0) break;
            a1 = Integers.rotateLeft(a1, 1);
            a2 = Integers.rotateLeft(a2, 5);
            int t = a3 = Integers.rotateLeft(a3, 2);
            a1 ^= a3 | a2;
            a3 = a0 ^ a2 & ~a1;
            a2 = t ^ ~a1 ^ a2 ^ a3;
            a0 = t ^ a2 & (a1 ^= a3 | a2);
            a1 = Integers.rotateLeft(a1, 31);
            a2 = Integers.rotateLeft(a2, 27);
            a3 = Integers.rotateLeft(a3, 30);
        }
        Pack.intToBigEndian(a0, out, outOff);
        Pack.intToBigEndian(a1, out, outOff + 4);
        Pack.intToBigEndian(a2, out, outOff + 8);
        Pack.intToBigEndian(a3, out, outOff + 12);
        return 16;
    }
}

