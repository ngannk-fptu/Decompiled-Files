/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.digests.GeneralDigest;
import org.bouncycastle.crypto.digests.Utils;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public class SM3Digest
extends GeneralDigest {
    private static final int DIGEST_LENGTH = 32;
    private static final int BLOCK_SIZE = 16;
    private int[] V = new int[8];
    private int[] inwords = new int[16];
    private int xOff;
    private int[] W = new int[68];
    private static final int[] T;

    public SM3Digest() {
        this(CryptoServicePurpose.ANY);
    }

    public SM3Digest(CryptoServicePurpose purpose) {
        super(purpose);
        CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
        this.reset();
    }

    public SM3Digest(SM3Digest t) {
        super(t);
        CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
        this.copyIn(t);
    }

    private void copyIn(SM3Digest t) {
        System.arraycopy(t.V, 0, this.V, 0, this.V.length);
        System.arraycopy(t.inwords, 0, this.inwords, 0, this.inwords.length);
        this.xOff = t.xOff;
    }

    @Override
    public String getAlgorithmName() {
        return "SM3";
    }

    @Override
    public int getDigestSize() {
        return 32;
    }

    @Override
    public Memoable copy() {
        return new SM3Digest(this);
    }

    @Override
    public void reset(Memoable other) {
        SM3Digest d = (SM3Digest)other;
        super.copyIn(d);
        this.copyIn(d);
    }

    @Override
    public void reset() {
        super.reset();
        this.V[0] = 1937774191;
        this.V[1] = 1226093241;
        this.V[2] = 388252375;
        this.V[3] = -628488704;
        this.V[4] = -1452330820;
        this.V[5] = 372324522;
        this.V[6] = -477237683;
        this.V[7] = -1325724082;
        this.xOff = 0;
    }

    @Override
    public int doFinal(byte[] out, int outOff) {
        this.finish();
        Pack.intToBigEndian(this.V, out, outOff);
        this.reset();
        return 32;
    }

    @Override
    protected void processWord(byte[] in, int inOff) {
        this.inwords[this.xOff++] = Pack.bigEndianToInt(in, inOff);
        if (this.xOff >= 16) {
            this.processBlock();
        }
    }

    @Override
    protected void processLength(long bitLength) {
        if (this.xOff > 14) {
            this.inwords[this.xOff] = 0;
            ++this.xOff;
            this.processBlock();
        }
        while (this.xOff < 14) {
            this.inwords[this.xOff] = 0;
            ++this.xOff;
        }
        this.inwords[this.xOff++] = (int)(bitLength >>> 32);
        this.inwords[this.xOff++] = (int)bitLength;
    }

    private int P0(int x) {
        int r9 = x << 9 | x >>> 23;
        int r17 = x << 17 | x >>> 15;
        return x ^ r9 ^ r17;
    }

    private int P1(int x) {
        int r15 = x << 15 | x >>> 17;
        int r23 = x << 23 | x >>> 9;
        return x ^ r15 ^ r23;
    }

    private int FF0(int x, int y, int z) {
        return x ^ y ^ z;
    }

    private int FF1(int x, int y, int z) {
        return x & y | x & z | y & z;
    }

    private int GG0(int x, int y, int z) {
        return x ^ y ^ z;
    }

    private int GG1(int x, int y, int z) {
        return x & y | ~x & z;
    }

    @Override
    protected void processBlock() {
        int TT2;
        int TT1;
        int W1j;
        int Wj;
        int SS2;
        int SS1;
        int s1_;
        int a12;
        int j;
        int j2;
        for (j2 = 0; j2 < 16; ++j2) {
            this.W[j2] = this.inwords[j2];
        }
        for (j2 = 16; j2 < 68; ++j2) {
            int wj3 = this.W[j2 - 3];
            int r15 = wj3 << 15 | wj3 >>> 17;
            int wj13 = this.W[j2 - 13];
            int r7 = wj13 << 7 | wj13 >>> 25;
            this.W[j2] = this.P1(this.W[j2 - 16] ^ this.W[j2 - 9] ^ r15) ^ r7 ^ this.W[j2 - 6];
        }
        int A = this.V[0];
        int B = this.V[1];
        int C = this.V[2];
        int D = this.V[3];
        int E = this.V[4];
        int F2 = this.V[5];
        int G = this.V[6];
        int H = this.V[7];
        for (j = 0; j < 16; ++j) {
            a12 = A << 12 | A >>> 20;
            s1_ = a12 + E + T[j];
            SS1 = s1_ << 7 | s1_ >>> 25;
            SS2 = SS1 ^ a12;
            Wj = this.W[j];
            W1j = Wj ^ this.W[j + 4];
            TT1 = this.FF0(A, B, C) + D + SS2 + W1j;
            TT2 = this.GG0(E, F2, G) + H + SS1 + Wj;
            D = C;
            C = B << 9 | B >>> 23;
            B = A;
            A = TT1;
            H = G;
            G = F2 << 19 | F2 >>> 13;
            F2 = E;
            E = this.P0(TT2);
        }
        for (j = 16; j < 64; ++j) {
            a12 = A << 12 | A >>> 20;
            s1_ = a12 + E + T[j];
            SS1 = s1_ << 7 | s1_ >>> 25;
            SS2 = SS1 ^ a12;
            Wj = this.W[j];
            W1j = Wj ^ this.W[j + 4];
            TT1 = this.FF1(A, B, C) + D + SS2 + W1j;
            TT2 = this.GG1(E, F2, G) + H + SS1 + Wj;
            D = C;
            C = B << 9 | B >>> 23;
            B = A;
            A = TT1;
            H = G;
            G = F2 << 19 | F2 >>> 13;
            F2 = E;
            E = this.P0(TT2);
        }
        this.V[0] = this.V[0] ^ A;
        this.V[1] = this.V[1] ^ B;
        this.V[2] = this.V[2] ^ C;
        this.V[3] = this.V[3] ^ D;
        this.V[4] = this.V[4] ^ E;
        this.V[5] = this.V[5] ^ F2;
        this.V[6] = this.V[6] ^ G;
        this.V[7] = this.V[7] ^ H;
        this.xOff = 0;
    }

    @Override
    protected CryptoServiceProperties cryptoServiceProperties() {
        return Utils.getDefaultProperties(this, 256, this.purpose);
    }

    static {
        int i;
        T = new int[64];
        for (i = 0; i < 16; ++i) {
            int t = 2043430169;
            SM3Digest.T[i] = t << i | t >>> 32 - i;
        }
        for (i = 16; i < 64; ++i) {
            int n = i % 32;
            int t = 2055708042;
            SM3Digest.T[i] = t << n | t >>> 32 - n;
        }
    }
}

