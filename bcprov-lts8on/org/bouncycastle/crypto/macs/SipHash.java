/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.macs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Pack;

public class SipHash
implements Mac {
    protected final int c;
    protected final int d;
    protected long k0;
    protected long k1;
    protected long v0;
    protected long v1;
    protected long v2;
    protected long v3;
    protected long m = 0L;
    protected int wordPos = 0;
    protected int wordCount = 0;

    public SipHash() {
        this.c = 2;
        this.d = 4;
    }

    public SipHash(int c, int d) {
        this.c = c;
        this.d = d;
    }

    @Override
    public String getAlgorithmName() {
        return "SipHash-" + this.c + "-" + this.d;
    }

    @Override
    public int getMacSize() {
        return 8;
    }

    @Override
    public void init(CipherParameters params) throws IllegalArgumentException {
        if (!(params instanceof KeyParameter)) {
            throw new IllegalArgumentException("'params' must be an instance of KeyParameter");
        }
        KeyParameter keyParameter = (KeyParameter)params;
        byte[] key = keyParameter.getKey();
        if (key.length != 16) {
            throw new IllegalArgumentException("'params' must be a 128-bit key");
        }
        this.k0 = Pack.littleEndianToLong(key, 0);
        this.k1 = Pack.littleEndianToLong(key, 8);
        this.reset();
    }

    @Override
    public void update(byte input) throws IllegalStateException {
        this.m >>>= 8;
        this.m |= ((long)input & 0xFFL) << 56;
        if (++this.wordPos == 8) {
            this.processMessageWord();
            this.wordPos = 0;
        }
    }

    @Override
    public void update(byte[] input, int offset, int length) throws DataLengthException, IllegalStateException {
        int i;
        int fullWords = length & 0xFFFFFFF8;
        if (this.wordPos == 0) {
            for (i = 0; i < fullWords; i += 8) {
                this.m = Pack.littleEndianToLong(input, offset + i);
                this.processMessageWord();
            }
            while (i < length) {
                this.m >>>= 8;
                this.m |= ((long)input[offset + i] & 0xFFL) << 56;
                ++i;
            }
            this.wordPos = length - fullWords;
        } else {
            int bits = this.wordPos << 3;
            while (i < fullWords) {
                long n = Pack.littleEndianToLong(input, offset + i);
                this.m = n << bits | this.m >>> -bits;
                this.processMessageWord();
                this.m = n;
                i += 8;
            }
            while (i < length) {
                this.m >>>= 8;
                this.m |= ((long)input[offset + i] & 0xFFL) << 56;
                if (++this.wordPos == 8) {
                    this.processMessageWord();
                    this.wordPos = 0;
                }
                ++i;
            }
        }
    }

    public long doFinal() throws DataLengthException, IllegalStateException {
        this.m >>>= 7 - this.wordPos << 3;
        this.m >>>= 8;
        this.m |= ((long)((this.wordCount << 3) + this.wordPos) & 0xFFL) << 56;
        this.processMessageWord();
        this.v2 ^= 0xFFL;
        this.applySipRounds(this.d);
        long result = this.v0 ^ this.v1 ^ this.v2 ^ this.v3;
        this.reset();
        return result;
    }

    @Override
    public int doFinal(byte[] out, int outOff) throws DataLengthException, IllegalStateException {
        long result = this.doFinal();
        Pack.longToLittleEndian(result, out, outOff);
        return 8;
    }

    @Override
    public void reset() {
        this.v0 = this.k0 ^ 0x736F6D6570736575L;
        this.v1 = this.k1 ^ 0x646F72616E646F6DL;
        this.v2 = this.k0 ^ 0x6C7967656E657261L;
        this.v3 = this.k1 ^ 0x7465646279746573L;
        this.m = 0L;
        this.wordPos = 0;
        this.wordCount = 0;
    }

    protected void processMessageWord() {
        ++this.wordCount;
        this.v3 ^= this.m;
        this.applySipRounds(this.c);
        this.v0 ^= this.m;
    }

    protected void applySipRounds(int n) {
        long r0 = this.v0;
        long r1 = this.v1;
        long r2 = this.v2;
        long r3 = this.v3;
        for (int r = 0; r < n; ++r) {
            r0 += r1;
            r2 += r3;
            r1 = SipHash.rotateLeft(r1, 13);
            r3 = SipHash.rotateLeft(r3, 16);
            r1 ^= r0;
            r3 ^= r2;
            r0 = SipHash.rotateLeft(r0, 32);
            r2 += r1;
            r0 += r3;
            r1 = SipHash.rotateLeft(r1, 17);
            r3 = SipHash.rotateLeft(r3, 21);
            r1 ^= r2;
            r3 ^= r0;
            r2 = SipHash.rotateLeft(r2, 32);
        }
        this.v0 = r0;
        this.v1 = r1;
        this.v2 = r2;
        this.v3 = r3;
    }

    protected static long rotateLeft(long x, int n) {
        return x << n | x >>> -n;
    }
}

