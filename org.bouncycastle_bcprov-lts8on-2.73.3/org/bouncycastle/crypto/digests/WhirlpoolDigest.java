/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.digests.Utils;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public final class WhirlpoolDigest
implements ExtendedDigest,
Memoable {
    private static final int BITCOUNT_ARRAY_SIZE = 32;
    private static final int BYTE_LENGTH = 64;
    private static final int DIGEST_LENGTH_BYTES = 64;
    private static final int REDUCTION_POLYNOMIAL = 285;
    private static final int ROUNDS = 10;
    private static final int[] SBOX = new int[]{24, 35, 198, 232, 135, 184, 1, 79, 54, 166, 210, 245, 121, 111, 145, 82, 96, 188, 155, 142, 163, 12, 123, 53, 29, 224, 215, 194, 46, 75, 254, 87, 21, 119, 55, 229, 159, 240, 74, 218, 88, 201, 41, 10, 177, 160, 107, 133, 189, 93, 16, 244, 203, 62, 5, 103, 228, 39, 65, 139, 167, 125, 149, 216, 251, 238, 124, 102, 221, 23, 71, 158, 202, 45, 191, 7, 173, 90, 131, 51, 99, 2, 170, 113, 200, 25, 73, 217, 242, 227, 91, 136, 154, 38, 50, 176, 233, 15, 213, 128, 190, 205, 52, 72, 255, 122, 144, 95, 32, 104, 26, 174, 180, 84, 147, 34, 100, 241, 115, 18, 64, 8, 195, 236, 219, 161, 141, 61, 151, 0, 207, 43, 118, 130, 214, 27, 181, 175, 106, 80, 69, 243, 48, 239, 63, 85, 162, 234, 101, 186, 47, 192, 222, 28, 253, 77, 146, 117, 6, 138, 178, 230, 14, 31, 98, 212, 168, 150, 249, 197, 37, 89, 132, 114, 57, 76, 94, 120, 56, 140, 209, 165, 226, 97, 179, 33, 156, 30, 67, 199, 252, 4, 81, 153, 109, 13, 250, 223, 126, 36, 59, 171, 206, 17, 143, 78, 183, 235, 60, 129, 148, 247, 185, 19, 44, 211, 231, 110, 196, 3, 86, 68, 127, 169, 42, 187, 193, 83, 220, 11, 157, 108, 49, 116, 246, 70, 172, 137, 20, 225, 22, 58, 105, 9, 112, 182, 208, 237, 204, 66, 152, 164, 40, 92, 248, 134};
    private static final long[] C0 = new long[256];
    private static final long[] C1 = new long[256];
    private static final long[] C2 = new long[256];
    private static final long[] C3 = new long[256];
    private static final long[] C4 = new long[256];
    private static final long[] C5 = new long[256];
    private static final long[] C6 = new long[256];
    private static final long[] C7 = new long[256];
    private static final short[] EIGHT = new short[32];
    private final long[] _rc = new long[11];
    private final CryptoServicePurpose purpose;
    private byte[] _buffer = new byte[64];
    private int _bufferPos = 0;
    private short[] _bitCount = new short[32];
    private long[] _hash = new long[8];
    private long[] _K = new long[8];
    private long[] _L = new long[8];
    private long[] _block = new long[8];
    private long[] _state = new long[8];

    private static int mulX(int input) {
        return input << 1 ^ -(input >>> 7) & 0x11D;
    }

    private static long packIntoLong(int b7, int b6, int b5, int b4, int b3, int b2, int b1, int b0) {
        return (long)b7 << 56 ^ (long)b6 << 48 ^ (long)b5 << 40 ^ (long)b4 << 32 ^ (long)b3 << 24 ^ (long)b2 << 16 ^ (long)b1 << 8 ^ (long)b0;
    }

    public WhirlpoolDigest() {
        this(CryptoServicePurpose.ANY);
    }

    public WhirlpoolDigest(CryptoServicePurpose purpose) {
        this._rc[0] = 0L;
        for (int r = 1; r <= 10; ++r) {
            int i = 8 * (r - 1);
            this._rc[r] = C0[i] & 0xFF00000000000000L ^ C1[i + 1] & 0xFF000000000000L ^ C2[i + 2] & 0xFF0000000000L ^ C3[i + 3] & 0xFF00000000L ^ C4[i + 4] & 0xFF000000L ^ C5[i + 5] & 0xFF0000L ^ C6[i + 6] & 0xFF00L ^ C7[i + 7] & 0xFFL;
        }
        this.purpose = purpose;
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, this.getDigestSize(), purpose));
    }

    public WhirlpoolDigest(WhirlpoolDigest originalDigest) {
        this.purpose = originalDigest.purpose;
        this.reset(originalDigest);
        CryptoServicesRegistrar.checkConstraints(Utils.getDefaultProperties(this, this.getDigestSize(), this.purpose));
    }

    @Override
    public String getAlgorithmName() {
        return "Whirlpool";
    }

    @Override
    public int getDigestSize() {
        return 64;
    }

    @Override
    public int doFinal(byte[] out, int outOff) {
        this.finish();
        Pack.longToBigEndian(this._hash, out, outOff);
        this.reset();
        return this.getDigestSize();
    }

    @Override
    public void reset() {
        this._bufferPos = 0;
        Arrays.fill(this._bitCount, (short)0);
        Arrays.fill(this._buffer, (byte)0);
        Arrays.fill(this._hash, 0L);
        Arrays.fill(this._K, 0L);
        Arrays.fill(this._L, 0L);
        Arrays.fill(this._block, 0L);
        Arrays.fill(this._state, 0L);
    }

    private void processFilledBuffer(byte[] in, int inOff) {
        Pack.bigEndianToLong(this._buffer, 0, this._block);
        this.processBlock();
        this._bufferPos = 0;
        Arrays.fill(this._buffer, (byte)0);
    }

    protected void processBlock() {
        int i;
        for (i = 0; i < 8; ++i) {
            this._K[i] = this._hash[i];
            this._state[i] = this._block[i] ^ this._K[i];
        }
        for (int round = 1; round <= 10; ++round) {
            int i2;
            for (i2 = 0; i2 < 8; ++i2) {
                this._L[i2] = 0L;
                int n = i2;
                this._L[n] = this._L[n] ^ C0[(int)(this._K[i2 - 0 & 7] >>> 56) & 0xFF];
                int n2 = i2;
                this._L[n2] = this._L[n2] ^ C1[(int)(this._K[i2 - 1 & 7] >>> 48) & 0xFF];
                int n3 = i2;
                this._L[n3] = this._L[n3] ^ C2[(int)(this._K[i2 - 2 & 7] >>> 40) & 0xFF];
                int n4 = i2;
                this._L[n4] = this._L[n4] ^ C3[(int)(this._K[i2 - 3 & 7] >>> 32) & 0xFF];
                int n5 = i2;
                this._L[n5] = this._L[n5] ^ C4[(int)(this._K[i2 - 4 & 7] >>> 24) & 0xFF];
                int n6 = i2;
                this._L[n6] = this._L[n6] ^ C5[(int)(this._K[i2 - 5 & 7] >>> 16) & 0xFF];
                int n7 = i2;
                this._L[n7] = this._L[n7] ^ C6[(int)(this._K[i2 - 6 & 7] >>> 8) & 0xFF];
                int n8 = i2;
                this._L[n8] = this._L[n8] ^ C7[(int)this._K[i2 - 7 & 7] & 0xFF];
            }
            System.arraycopy(this._L, 0, this._K, 0, this._K.length);
            this._K[0] = this._K[0] ^ this._rc[round];
            for (i2 = 0; i2 < 8; ++i2) {
                this._L[i2] = this._K[i2];
                int n = i2;
                this._L[n] = this._L[n] ^ C0[(int)(this._state[i2 - 0 & 7] >>> 56) & 0xFF];
                int n9 = i2;
                this._L[n9] = this._L[n9] ^ C1[(int)(this._state[i2 - 1 & 7] >>> 48) & 0xFF];
                int n10 = i2;
                this._L[n10] = this._L[n10] ^ C2[(int)(this._state[i2 - 2 & 7] >>> 40) & 0xFF];
                int n11 = i2;
                this._L[n11] = this._L[n11] ^ C3[(int)(this._state[i2 - 3 & 7] >>> 32) & 0xFF];
                int n12 = i2;
                this._L[n12] = this._L[n12] ^ C4[(int)(this._state[i2 - 4 & 7] >>> 24) & 0xFF];
                int n13 = i2;
                this._L[n13] = this._L[n13] ^ C5[(int)(this._state[i2 - 5 & 7] >>> 16) & 0xFF];
                int n14 = i2;
                this._L[n14] = this._L[n14] ^ C6[(int)(this._state[i2 - 6 & 7] >>> 8) & 0xFF];
                int n15 = i2;
                this._L[n15] = this._L[n15] ^ C7[(int)this._state[i2 - 7 & 7] & 0xFF];
            }
            System.arraycopy(this._L, 0, this._state, 0, this._state.length);
        }
        for (i = 0; i < 8; ++i) {
            int n = i;
            this._hash[n] = this._hash[n] ^ (this._state[i] ^ this._block[i]);
        }
    }

    @Override
    public void update(byte in) {
        this._buffer[this._bufferPos] = in;
        if (++this._bufferPos == this._buffer.length) {
            this.processFilledBuffer(this._buffer, 0);
        }
        this.increment();
    }

    private void increment() {
        int carry = 0;
        for (int i = this._bitCount.length - 1; i >= 0; --i) {
            int sum = (this._bitCount[i] & 0xFF) + EIGHT[i] + carry;
            carry = sum >>> 8;
            this._bitCount[i] = (short)(sum & 0xFF);
        }
    }

    @Override
    public void update(byte[] in, int inOff, int len) {
        while (len > 0) {
            this.update(in[inOff]);
            ++inOff;
            --len;
        }
    }

    private void finish() {
        byte[] bitLength = this.copyBitLength();
        int n = this._bufferPos++;
        this._buffer[n] = (byte)(this._buffer[n] | 0x80);
        if (this._bufferPos == this._buffer.length) {
            this.processFilledBuffer(this._buffer, 0);
        }
        if (this._bufferPos > 32) {
            while (this._bufferPos != 0) {
                this.update((byte)0);
            }
        }
        while (this._bufferPos <= 32) {
            this.update((byte)0);
        }
        System.arraycopy(bitLength, 0, this._buffer, 32, bitLength.length);
        this.processFilledBuffer(this._buffer, 0);
    }

    private byte[] copyBitLength() {
        byte[] rv = new byte[32];
        for (int i = 0; i < rv.length; ++i) {
            rv[i] = (byte)(this._bitCount[i] & 0xFF);
        }
        return rv;
    }

    @Override
    public int getByteLength() {
        return 64;
    }

    @Override
    public Memoable copy() {
        return new WhirlpoolDigest(this);
    }

    @Override
    public void reset(Memoable other) {
        WhirlpoolDigest originalDigest = (WhirlpoolDigest)other;
        System.arraycopy(originalDigest._rc, 0, this._rc, 0, this._rc.length);
        System.arraycopy(originalDigest._buffer, 0, this._buffer, 0, this._buffer.length);
        this._bufferPos = originalDigest._bufferPos;
        System.arraycopy(originalDigest._bitCount, 0, this._bitCount, 0, this._bitCount.length);
        System.arraycopy(originalDigest._hash, 0, this._hash, 0, this._hash.length);
        System.arraycopy(originalDigest._K, 0, this._K, 0, this._K.length);
        System.arraycopy(originalDigest._L, 0, this._L, 0, this._L.length);
        System.arraycopy(originalDigest._block, 0, this._block, 0, this._block.length);
        System.arraycopy(originalDigest._state, 0, this._state, 0, this._state.length);
    }

    static {
        WhirlpoolDigest.EIGHT[31] = 8;
        for (int i = 0; i < 256; ++i) {
            int v1 = SBOX[i];
            int v2 = WhirlpoolDigest.mulX(v1);
            int v4 = WhirlpoolDigest.mulX(v2);
            int v5 = v4 ^ v1;
            int v8 = WhirlpoolDigest.mulX(v4);
            int v9 = v8 ^ v1;
            WhirlpoolDigest.C0[i] = WhirlpoolDigest.packIntoLong(v1, v1, v4, v1, v8, v5, v2, v9);
            WhirlpoolDigest.C1[i] = WhirlpoolDigest.packIntoLong(v9, v1, v1, v4, v1, v8, v5, v2);
            WhirlpoolDigest.C2[i] = WhirlpoolDigest.packIntoLong(v2, v9, v1, v1, v4, v1, v8, v5);
            WhirlpoolDigest.C3[i] = WhirlpoolDigest.packIntoLong(v5, v2, v9, v1, v1, v4, v1, v8);
            WhirlpoolDigest.C4[i] = WhirlpoolDigest.packIntoLong(v8, v5, v2, v9, v1, v1, v4, v1);
            WhirlpoolDigest.C5[i] = WhirlpoolDigest.packIntoLong(v1, v8, v5, v2, v9, v1, v1, v4);
            WhirlpoolDigest.C6[i] = WhirlpoolDigest.packIntoLong(v4, v1, v8, v5, v2, v9, v1, v1);
            WhirlpoolDigest.C7[i] = WhirlpoolDigest.packIntoLong(v1, v4, v1, v8, v5, v2, v9, v1);
        }
    }
}

