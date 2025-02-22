/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.engines;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.OutputLengthException;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.KeyParameter;

public abstract class SerpentEngineBase
implements BlockCipher {
    protected static final int BLOCK_SIZE = 16;
    static final int ROUNDS = 32;
    static final int PHI = -1640531527;
    protected boolean encrypting;
    protected int[] wKey;
    protected int keyBits;

    SerpentEngineBase() {
        CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), 256));
    }

    @Override
    public void init(boolean encrypting, CipherParameters params) {
        if (params instanceof KeyParameter) {
            this.encrypting = encrypting;
            byte[] keyBytes = ((KeyParameter)params).getKey();
            this.wKey = this.makeWorkingKey(keyBytes);
            CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties(this.getAlgorithmName(), keyBytes.length * 8, params, this.getPurpose()));
            return;
        }
        throw new IllegalArgumentException("invalid parameter passed to " + this.getAlgorithmName() + " init - " + params.getClass().getName());
    }

    @Override
    public String getAlgorithmName() {
        return "Serpent";
    }

    @Override
    public int getBlockSize() {
        return 16;
    }

    @Override
    public final int processBlock(byte[] in, int inOff, byte[] out, int outOff) {
        if (this.wKey == null) {
            throw new IllegalStateException(this.getAlgorithmName() + " not initialised");
        }
        if (inOff + 16 > in.length) {
            throw new DataLengthException("input buffer too short");
        }
        if (outOff + 16 > out.length) {
            throw new OutputLengthException("output buffer too short");
        }
        if (this.encrypting) {
            this.encryptBlock(in, inOff, out, outOff);
        } else {
            this.decryptBlock(in, inOff, out, outOff);
        }
        return 16;
    }

    @Override
    public void reset() {
    }

    protected static int rotateLeft(int x, int bits) {
        return x << bits | x >>> -bits;
    }

    protected static int rotateRight(int x, int bits) {
        return x >>> bits | x << -bits;
    }

    protected final void sb0(int[] X, int a, int b, int c, int d) {
        int t1 = a ^ d;
        int t3 = c ^ t1;
        int t4 = b ^ t3;
        X[3] = a & d ^ t4;
        int t7 = a ^ b & t1;
        X[2] = t4 ^ (c | t7);
        int t12 = X[3] & (t3 ^ t7);
        X[1] = ~t3 ^ t12;
        X[0] = t12 ^ ~t7;
    }

    protected final void ib0(int[] X, int a, int b, int c, int d) {
        int t1 = ~a;
        int t2 = a ^ b;
        int t4 = d ^ (t1 | t2);
        int t5 = c ^ t4;
        X[2] = t2 ^ t5;
        int t8 = t1 ^ d & t2;
        X[1] = t4 ^ X[2] & t8;
        X[3] = a & t4 ^ (t5 | X[1]);
        X[0] = X[3] ^ (t5 ^ t8);
    }

    protected final void sb1(int[] X, int a, int b, int c, int d) {
        int t2 = b ^ ~a;
        int t5 = c ^ (a | t2);
        X[2] = d ^ t5;
        int t7 = b ^ (d | t2);
        int t8 = t2 ^ X[2];
        X[3] = t8 ^ t5 & t7;
        int t11 = t5 ^ t7;
        X[1] = X[3] ^ t11;
        X[0] = t5 ^ t8 & t11;
    }

    protected final void ib1(int[] X, int a, int b, int c, int d) {
        int t1 = b ^ d;
        int t3 = a ^ b & t1;
        int t4 = t1 ^ t3;
        X[3] = c ^ t4;
        int t7 = b ^ t1 & t3;
        int t8 = X[3] | t7;
        X[1] = t3 ^ t8;
        int t10 = ~X[1];
        int t11 = X[3] ^ t7;
        X[0] = t10 ^ t11;
        X[2] = t4 ^ (t10 | t11);
    }

    protected final void sb2(int[] X, int a, int b, int c, int d) {
        int t1 = ~a;
        int t2 = b ^ d;
        int t3 = c & t1;
        X[0] = t2 ^ t3;
        int t5 = c ^ t1;
        int t6 = c ^ X[0];
        int t7 = b & t6;
        X[3] = t5 ^ t7;
        X[2] = a ^ (d | t7) & (X[0] | t5);
        X[1] = t2 ^ X[3] ^ (X[2] ^ (d | t1));
    }

    protected final void ib2(int[] X, int a, int b, int c, int d) {
        int t1 = b ^ d;
        int t2 = ~t1;
        int t3 = a ^ c;
        int t4 = c ^ t1;
        int t5 = b & t4;
        X[0] = t3 ^ t5;
        int t7 = a | t2;
        int t8 = d ^ t7;
        int t9 = t3 | t8;
        X[3] = t1 ^ t9;
        int t11 = ~t4;
        int t12 = X[0] | X[3];
        X[1] = t11 ^ t12;
        X[2] = d & t11 ^ (t3 ^ t12);
    }

    protected final void sb3(int[] X, int a, int b, int c, int d) {
        int t1 = a ^ b;
        int t2 = a & c;
        int t3 = a | d;
        int t4 = c ^ d;
        int t5 = t1 & t3;
        int t6 = t2 | t5;
        X[2] = t4 ^ t6;
        int t8 = b ^ t3;
        int t9 = t6 ^ t8;
        int t10 = t4 & t9;
        X[0] = t1 ^ t10;
        int t12 = X[2] & X[0];
        X[1] = t9 ^ t12;
        X[3] = (b | d) ^ (t4 ^ t12);
    }

    protected final void ib3(int[] X, int a, int b, int c, int d) {
        int t1 = a | b;
        int t2 = b ^ c;
        int t3 = b & t2;
        int t4 = a ^ t3;
        int t5 = c ^ t4;
        int t6 = d | t4;
        X[0] = t2 ^ t6;
        int t8 = t2 | t6;
        int t9 = d ^ t8;
        X[2] = t5 ^ t9;
        int t11 = t1 ^ t9;
        int t12 = X[0] & t11;
        X[3] = t4 ^ t12;
        X[1] = X[3] ^ (X[0] ^ t11);
    }

    protected final void sb4(int[] X, int a, int b, int c, int d) {
        int t1 = a ^ d;
        int t2 = d & t1;
        int t3 = c ^ t2;
        int t4 = b | t3;
        X[3] = t1 ^ t4;
        int t6 = ~b;
        int t7 = t1 | t6;
        X[0] = t3 ^ t7;
        int t9 = a & X[0];
        int t10 = t1 ^ t6;
        int t11 = t4 & t10;
        X[2] = t9 ^ t11;
        X[1] = a ^ t3 ^ t10 & X[2];
    }

    protected final void ib4(int[] X, int a, int b, int c, int d) {
        int t1 = c | d;
        int t2 = a & t1;
        int t3 = b ^ t2;
        int t4 = a & t3;
        int t5 = c ^ t4;
        X[1] = d ^ t5;
        int t7 = ~a;
        int t8 = t5 & X[1];
        X[3] = t3 ^ t8;
        int t10 = X[1] | t7;
        int t11 = d ^ t10;
        X[0] = X[3] ^ t11;
        X[2] = t3 & t11 ^ (X[1] ^ t7);
    }

    protected final void sb5(int[] X, int a, int b, int c, int d) {
        int t1 = ~a;
        int t2 = a ^ b;
        int t3 = a ^ d;
        int t4 = c ^ t1;
        int t5 = t2 | t3;
        X[0] = t4 ^ t5;
        int t7 = d & X[0];
        int t8 = t2 ^ X[0];
        X[1] = t7 ^ t8;
        int t10 = t1 | X[0];
        int t11 = t2 | t7;
        int t12 = t3 ^ t10;
        X[2] = t11 ^ t12;
        X[3] = b ^ t7 ^ X[1] & t12;
    }

    protected final void ib5(int[] X, int a, int b, int c, int d) {
        int t1 = ~c;
        int t2 = b & t1;
        int t3 = d ^ t2;
        int t4 = a & t3;
        int t5 = b ^ t1;
        X[3] = t4 ^ t5;
        int t7 = b | X[3];
        int t8 = a & t7;
        X[1] = t3 ^ t8;
        int t10 = a | d;
        int t11 = t1 ^ t7;
        X[0] = t10 ^ t11;
        X[2] = b & t10 ^ (t4 | a ^ c);
    }

    protected final void sb6(int[] X, int a, int b, int c, int d) {
        int t1 = ~a;
        int t2 = a ^ d;
        int t3 = b ^ t2;
        int t4 = t1 | t2;
        int t5 = c ^ t4;
        X[1] = b ^ t5;
        int t7 = t2 | X[1];
        int t8 = d ^ t7;
        int t9 = t5 & t8;
        X[2] = t3 ^ t9;
        int t11 = t5 ^ t8;
        X[0] = X[2] ^ t11;
        X[3] = ~t5 ^ t3 & t11;
    }

    protected final void ib6(int[] X, int a, int b, int c, int d) {
        int t1 = ~a;
        int t2 = a ^ b;
        int t3 = c ^ t2;
        int t4 = c | t1;
        int t5 = d ^ t4;
        X[1] = t3 ^ t5;
        int t7 = t3 & t5;
        int t8 = t2 ^ t7;
        int t9 = b | t8;
        X[3] = t5 ^ t9;
        int t11 = b | X[3];
        X[0] = t8 ^ t11;
        X[2] = d & t1 ^ (t3 ^ t11);
    }

    protected final void sb7(int[] X, int a, int b, int c, int d) {
        int t1 = b ^ c;
        int t2 = c & t1;
        int t3 = d ^ t2;
        int t4 = a ^ t3;
        int t5 = d | t1;
        int t6 = t4 & t5;
        X[1] = b ^ t6;
        int t8 = t3 | X[1];
        int t9 = a & t4;
        X[3] = t1 ^ t9;
        int t11 = t4 ^ t8;
        int t12 = X[3] & t11;
        X[2] = t3 ^ t12;
        X[0] = ~t11 ^ X[3] & X[2];
    }

    protected final void ib7(int[] X, int a, int b, int c, int d) {
        int t3 = c | a & b;
        int t4 = d & (a | b);
        X[3] = t3 ^ t4;
        int t6 = ~d;
        int t7 = b ^ t4;
        int t9 = t7 | X[3] ^ t6;
        X[1] = a ^ t9;
        X[0] = c ^ t7 ^ (d | X[1]);
        X[2] = t3 ^ X[1] ^ (X[0] ^ a & X[3]);
    }

    protected final void LT(int[] X) {
        int x0 = SerpentEngineBase.rotateLeft(X[0], 13);
        int x2 = SerpentEngineBase.rotateLeft(X[2], 3);
        int x1 = X[1] ^ x0 ^ x2;
        int x3 = X[3] ^ x2 ^ x0 << 3;
        X[1] = SerpentEngineBase.rotateLeft(x1, 1);
        X[3] = SerpentEngineBase.rotateLeft(x3, 7);
        X[0] = SerpentEngineBase.rotateLeft(x0 ^ X[1] ^ X[3], 5);
        X[2] = SerpentEngineBase.rotateLeft(x2 ^ X[3] ^ X[1] << 7, 22);
    }

    protected final void inverseLT(int[] X) {
        int x2 = SerpentEngineBase.rotateRight(X[2], 22) ^ X[3] ^ X[1] << 7;
        int x0 = SerpentEngineBase.rotateRight(X[0], 5) ^ X[1] ^ X[3];
        int x3 = SerpentEngineBase.rotateRight(X[3], 7);
        int x1 = SerpentEngineBase.rotateRight(X[1], 1);
        X[3] = x3 ^ x2 ^ x0 << 3;
        X[1] = x1 ^ x0 ^ x2;
        X[2] = SerpentEngineBase.rotateRight(x2, 3);
        X[0] = SerpentEngineBase.rotateRight(x0, 13);
    }

    protected abstract int[] makeWorkingKey(byte[] var1);

    protected abstract void encryptBlock(byte[] var1, int var2, byte[] var3, int var4);

    protected abstract void decryptBlock(byte[] var1, int var2, byte[] var3, int var4);

    private CryptoServicePurpose getPurpose() {
        if (this.wKey == null) {
            return CryptoServicePurpose.ANY;
        }
        return this.encrypting ? CryptoServicePurpose.ENCRYPTION : CryptoServicePurpose.DECRYPTION;
    }
}

