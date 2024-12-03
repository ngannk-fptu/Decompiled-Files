/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.digests;

import org.bouncycastle.crypto.CryptoServiceProperties;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.digests.Utils;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class KeccakDigest
implements ExtendedDigest {
    private static long[] KeccakRoundConstants = new long[]{1L, 32898L, -9223372036854742902L, -9223372034707259392L, 32907L, 0x80000001L, -9223372034707259263L, -9223372036854743031L, 138L, 136L, 0x80008009L, 0x8000000AL, 0x8000808BL, -9223372036854775669L, -9223372036854742903L, -9223372036854743037L, -9223372036854743038L, -9223372036854775680L, 32778L, -9223372034707292150L, -9223372034707259263L, -9223372036854742912L, 0x80000001L, -9223372034707259384L};
    protected final CryptoServicePurpose purpose;
    protected long[] state = new long[25];
    protected byte[] dataQueue = new byte[192];
    protected int rate;
    protected int bitsInQueue;
    protected int fixedOutputLength;
    protected boolean squeezing;

    public KeccakDigest() {
        this(288, CryptoServicePurpose.ANY);
    }

    public KeccakDigest(CryptoServicePurpose purpose) {
        this(288, purpose);
    }

    public KeccakDigest(int bitLength) {
        this(bitLength, CryptoServicePurpose.ANY);
    }

    public KeccakDigest(int bitLength, CryptoServicePurpose purpose) {
        this.purpose = purpose;
        this.init(bitLength);
        CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
    }

    public KeccakDigest(KeccakDigest source) {
        this.purpose = source.purpose;
        System.arraycopy(source.state, 0, this.state, 0, source.state.length);
        System.arraycopy(source.dataQueue, 0, this.dataQueue, 0, source.dataQueue.length);
        this.rate = source.rate;
        this.bitsInQueue = source.bitsInQueue;
        this.fixedOutputLength = source.fixedOutputLength;
        this.squeezing = source.squeezing;
        CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
    }

    @Override
    public String getAlgorithmName() {
        return "Keccak-" + this.fixedOutputLength;
    }

    @Override
    public int getDigestSize() {
        return this.fixedOutputLength / 8;
    }

    @Override
    public void update(byte in) {
        this.absorb(in);
    }

    @Override
    public void update(byte[] in, int inOff, int len) {
        this.absorb(in, inOff, len);
    }

    @Override
    public int doFinal(byte[] out, int outOff) {
        this.squeeze(out, outOff, this.fixedOutputLength);
        this.reset();
        return this.getDigestSize();
    }

    protected int doFinal(byte[] out, int outOff, byte partialByte, int partialBits) {
        if (partialBits > 0) {
            this.absorbBits(partialByte, partialBits);
        }
        this.squeeze(out, outOff, this.fixedOutputLength);
        this.reset();
        return this.getDigestSize();
    }

    @Override
    public void reset() {
        this.init(this.fixedOutputLength);
    }

    @Override
    public int getByteLength() {
        return this.rate / 8;
    }

    private void init(int bitLength) {
        switch (bitLength) {
            case 128: 
            case 224: 
            case 256: 
            case 288: 
            case 384: 
            case 512: {
                this.initSponge(1600 - (bitLength << 1));
                break;
            }
            default: {
                throw new IllegalArgumentException("bitLength must be one of 128, 224, 256, 288, 384, or 512.");
            }
        }
    }

    private void initSponge(int rate) {
        if (rate <= 0 || rate >= 1600 || rate % 64 != 0) {
            throw new IllegalStateException("invalid rate value");
        }
        this.rate = rate;
        for (int i = 0; i < this.state.length; ++i) {
            this.state[i] = 0L;
        }
        Arrays.fill(this.dataQueue, (byte)0);
        this.bitsInQueue = 0;
        this.squeezing = false;
        this.fixedOutputLength = (1600 - rate) / 2;
    }

    protected void absorb(byte data) {
        if (this.bitsInQueue % 8 != 0) {
            throw new IllegalStateException("attempt to absorb with odd length queue");
        }
        if (this.squeezing) {
            throw new IllegalStateException("attempt to absorb while squeezing");
        }
        this.dataQueue[this.bitsInQueue >>> 3] = data;
        if ((this.bitsInQueue += 8) == this.rate) {
            this.KeccakAbsorb(this.dataQueue, 0);
            this.bitsInQueue = 0;
        }
    }

    protected void absorb(byte[] data, int off, int len) {
        int remaining;
        if (this.bitsInQueue % 8 != 0) {
            throw new IllegalStateException("attempt to absorb with odd length queue");
        }
        if (this.squeezing) {
            throw new IllegalStateException("attempt to absorb while squeezing");
        }
        int rateBytes = this.rate >>> 3;
        int bytesInQueue = this.bitsInQueue >>> 3;
        int available = rateBytes - bytesInQueue;
        if (len < available) {
            System.arraycopy(data, off, this.dataQueue, bytesInQueue, len);
            this.bitsInQueue += len << 3;
            return;
        }
        int count = 0;
        if (bytesInQueue > 0) {
            System.arraycopy(data, off, this.dataQueue, bytesInQueue, available);
            count += available;
            this.KeccakAbsorb(this.dataQueue, 0);
        }
        while ((remaining = len - count) >= rateBytes) {
            this.KeccakAbsorb(data, off + count);
            count += rateBytes;
        }
        System.arraycopy(data, off + count, this.dataQueue, 0, remaining);
        this.bitsInQueue = remaining << 3;
    }

    protected void absorbBits(int data, int bits) {
        if (bits < 1 || bits > 7) {
            throw new IllegalArgumentException("'bits' must be in the range 1 to 7");
        }
        if (this.bitsInQueue % 8 != 0) {
            throw new IllegalStateException("attempt to absorb with odd length queue");
        }
        if (this.squeezing) {
            throw new IllegalStateException("attempt to absorb while squeezing");
        }
        int mask = (1 << bits) - 1;
        this.dataQueue[this.bitsInQueue >>> 3] = (byte)(data & mask);
        this.bitsInQueue += bits;
    }

    private void padAndSwitchToSqueezingPhase() {
        int n = this.bitsInQueue >>> 3;
        this.dataQueue[n] = (byte)(this.dataQueue[n] | (byte)(1 << (this.bitsInQueue & 7)));
        if (++this.bitsInQueue == this.rate) {
            this.KeccakAbsorb(this.dataQueue, 0);
        } else {
            int full = this.bitsInQueue >>> 6;
            int partial = this.bitsInQueue & 0x3F;
            int off = 0;
            int i = 0;
            while (i < full) {
                int n2 = i++;
                this.state[n2] = this.state[n2] ^ Pack.littleEndianToLong(this.dataQueue, off);
                off += 8;
            }
            if (partial > 0) {
                long mask = (1L << partial) - 1L;
                int n3 = full;
                this.state[n3] = this.state[n3] ^ Pack.littleEndianToLong(this.dataQueue, off) & mask;
            }
        }
        int n4 = this.rate - 1 >>> 6;
        this.state[n4] = this.state[n4] ^ Long.MIN_VALUE;
        this.bitsInQueue = 0;
        this.squeezing = true;
    }

    protected void squeeze(byte[] output, int offset, long outputLength) {
        int partialBlock;
        if (!this.squeezing) {
            this.padAndSwitchToSqueezingPhase();
        }
        if (outputLength % 8L != 0L) {
            throw new IllegalStateException("outputLength not a multiple of 8");
        }
        for (long i = 0L; i < outputLength; i += (long)partialBlock) {
            if (this.bitsInQueue == 0) {
                this.KeccakExtract();
            }
            partialBlock = (int)Math.min((long)this.bitsInQueue, outputLength - i);
            System.arraycopy(this.dataQueue, (this.rate - this.bitsInQueue) / 8, output, offset + (int)(i / 8L), partialBlock / 8);
            this.bitsInQueue -= partialBlock;
        }
    }

    private void KeccakAbsorb(byte[] data, int off) {
        int count = this.rate >>> 6;
        int i = 0;
        while (i < count) {
            int n = i++;
            this.state[n] = this.state[n] ^ Pack.littleEndianToLong(data, off);
            off += 8;
        }
        this.KeccakPermutation();
    }

    private void KeccakExtract() {
        this.KeccakPermutation();
        Pack.longToLittleEndian(this.state, 0, this.rate >>> 6, this.dataQueue, 0);
        this.bitsInQueue = this.rate;
    }

    private void KeccakPermutation() {
        long[] A = this.state;
        long a00 = A[0];
        long a01 = A[1];
        long a02 = A[2];
        long a03 = A[3];
        long a04 = A[4];
        long a05 = A[5];
        long a06 = A[6];
        long a07 = A[7];
        long a08 = A[8];
        long a09 = A[9];
        long a10 = A[10];
        long a11 = A[11];
        long a12 = A[12];
        long a13 = A[13];
        long a14 = A[14];
        long a15 = A[15];
        long a16 = A[16];
        long a17 = A[17];
        long a18 = A[18];
        long a19 = A[19];
        long a20 = A[20];
        long a21 = A[21];
        long a22 = A[22];
        long a23 = A[23];
        long a24 = A[24];
        for (int i = 0; i < 24; ++i) {
            long c0 = a00 ^ a05 ^ a10 ^ a15 ^ a20;
            long c1 = a01 ^ a06 ^ a11 ^ a16 ^ a21;
            long c2 = a02 ^ a07 ^ a12 ^ a17 ^ a22;
            long c3 = a03 ^ a08 ^ a13 ^ a18 ^ a23;
            long c4 = a04 ^ a09 ^ a14 ^ a19 ^ a24;
            long d1 = (c1 << 1 | c1 >>> -1) ^ c4;
            long d2 = (c2 << 1 | c2 >>> -1) ^ c0;
            long d3 = (c3 << 1 | c3 >>> -1) ^ c1;
            long d4 = (c4 << 1 | c4 >>> -1) ^ c2;
            long d0 = (c0 << 1 | c0 >>> -1) ^ c3;
            a00 ^= d1;
            a05 ^= d1;
            a10 ^= d1;
            a15 ^= d1;
            a20 ^= d1;
            a01 ^= d2;
            a06 ^= d2;
            a11 ^= d2;
            a16 ^= d2;
            a21 ^= d2;
            a02 ^= d3;
            a07 ^= d3;
            a12 ^= d3;
            a17 ^= d3;
            a22 ^= d3;
            a03 ^= d4;
            a08 ^= d4;
            a13 ^= d4;
            a18 ^= d4;
            a23 ^= d4;
            a04 ^= d0;
            a09 ^= d0;
            a14 ^= d0;
            a19 ^= d0;
            a24 ^= d0;
            c1 = a01 << 1 | a01 >>> 63;
            a01 = a06 << 44 | a06 >>> 20;
            a06 = a09 << 20 | a09 >>> 44;
            a09 = a22 << 61 | a22 >>> 3;
            a22 = a14 << 39 | a14 >>> 25;
            a14 = a20 << 18 | a20 >>> 46;
            a20 = a02 << 62 | a02 >>> 2;
            a02 = a12 << 43 | a12 >>> 21;
            a12 = a13 << 25 | a13 >>> 39;
            a13 = a19 << 8 | a19 >>> 56;
            a19 = a23 << 56 | a23 >>> 8;
            a23 = a15 << 41 | a15 >>> 23;
            a15 = a04 << 27 | a04 >>> 37;
            a04 = a24 << 14 | a24 >>> 50;
            a24 = a21 << 2 | a21 >>> 62;
            a21 = a08 << 55 | a08 >>> 9;
            a08 = a16 << 45 | a16 >>> 19;
            a16 = a05 << 36 | a05 >>> 28;
            a05 = a03 << 28 | a03 >>> 36;
            a03 = a18 << 21 | a18 >>> 43;
            a18 = a17 << 15 | a17 >>> 49;
            a17 = a11 << 10 | a11 >>> 54;
            a11 = a07 << 6 | a07 >>> 58;
            a07 = a10 << 3 | a10 >>> 61;
            a10 = c1;
            c0 = a00 ^ (a01 ^ 0xFFFFFFFFFFFFFFFFL) & a02;
            c1 = a01 ^ (a02 ^ 0xFFFFFFFFFFFFFFFFL) & a03;
            a02 ^= (a03 ^ 0xFFFFFFFFFFFFFFFFL) & a04;
            a03 ^= (a04 ^ 0xFFFFFFFFFFFFFFFFL) & a00;
            a04 ^= (a00 ^ 0xFFFFFFFFFFFFFFFFL) & a01;
            a00 = c0;
            a01 = c1;
            c0 = a05 ^ (a06 ^ 0xFFFFFFFFFFFFFFFFL) & a07;
            c1 = a06 ^ (a07 ^ 0xFFFFFFFFFFFFFFFFL) & a08;
            a07 ^= (a08 ^ 0xFFFFFFFFFFFFFFFFL) & a09;
            a08 ^= (a09 ^ 0xFFFFFFFFFFFFFFFFL) & a05;
            a09 ^= (a05 ^ 0xFFFFFFFFFFFFFFFFL) & a06;
            a05 = c0;
            a06 = c1;
            c0 = a10 ^ (a11 ^ 0xFFFFFFFFFFFFFFFFL) & a12;
            c1 = a11 ^ (a12 ^ 0xFFFFFFFFFFFFFFFFL) & a13;
            a12 ^= (a13 ^ 0xFFFFFFFFFFFFFFFFL) & a14;
            a13 ^= (a14 ^ 0xFFFFFFFFFFFFFFFFL) & a10;
            a14 ^= (a10 ^ 0xFFFFFFFFFFFFFFFFL) & a11;
            a10 = c0;
            a11 = c1;
            c0 = a15 ^ (a16 ^ 0xFFFFFFFFFFFFFFFFL) & a17;
            c1 = a16 ^ (a17 ^ 0xFFFFFFFFFFFFFFFFL) & a18;
            a17 ^= (a18 ^ 0xFFFFFFFFFFFFFFFFL) & a19;
            a18 ^= (a19 ^ 0xFFFFFFFFFFFFFFFFL) & a15;
            a19 ^= (a15 ^ 0xFFFFFFFFFFFFFFFFL) & a16;
            a15 = c0;
            a16 = c1;
            c0 = a20 ^ (a21 ^ 0xFFFFFFFFFFFFFFFFL) & a22;
            c1 = a21 ^ (a22 ^ 0xFFFFFFFFFFFFFFFFL) & a23;
            a22 ^= (a23 ^ 0xFFFFFFFFFFFFFFFFL) & a24;
            a23 ^= (a24 ^ 0xFFFFFFFFFFFFFFFFL) & a20;
            a24 ^= (a20 ^ 0xFFFFFFFFFFFFFFFFL) & a21;
            a20 = c0;
            a21 = c1;
            a00 ^= KeccakRoundConstants[i];
        }
        A[0] = a00;
        A[1] = a01;
        A[2] = a02;
        A[3] = a03;
        A[4] = a04;
        A[5] = a05;
        A[6] = a06;
        A[7] = a07;
        A[8] = a08;
        A[9] = a09;
        A[10] = a10;
        A[11] = a11;
        A[12] = a12;
        A[13] = a13;
        A[14] = a14;
        A[15] = a15;
        A[16] = a16;
        A[17] = a17;
        A[18] = a18;
        A[19] = a19;
        A[20] = a20;
        A[21] = a21;
        A[22] = a22;
        A[23] = a23;
        A[24] = a24;
    }

    protected CryptoServiceProperties cryptoServiceProperties() {
        return Utils.getDefaultProperties(this, this.getDigestSize() * 8, this.purpose);
    }
}

