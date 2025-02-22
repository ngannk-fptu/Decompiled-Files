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
import org.bouncycastle.util.Memoable;
import org.bouncycastle.util.Pack;

public class DSTU7564Digest
implements ExtendedDigest,
Memoable {
    private static final int NB_512 = 8;
    private static final int NB_1024 = 16;
    private static final int NR_512 = 10;
    private static final int NR_1024 = 14;
    private final CryptoServicePurpose purpose;
    private int hashSize;
    private int blockSize;
    private int columns;
    private int rounds;
    private long[] state;
    private long[] tempState1;
    private long[] tempState2;
    private long inputBlocks;
    private int bufOff;
    private byte[] buf;
    private static final byte[] S0 = new byte[]{-88, 67, 95, 6, 107, 117, 108, 89, 113, -33, -121, -107, 23, -16, -40, 9, 109, -13, 29, -53, -55, 77, 44, -81, 121, -32, -105, -3, 111, 75, 69, 57, 62, -35, -93, 79, -76, -74, -102, 14, 31, -65, 21, -31, 73, -46, -109, -58, -110, 114, -98, 97, -47, 99, -6, -18, -12, 25, -43, -83, 88, -92, -69, -95, -36, -14, -125, 55, 66, -28, 122, 50, -100, -52, -85, 74, -113, 110, 4, 39, 46, -25, -30, 90, -106, 22, 35, 43, -62, 101, 102, 15, -68, -87, 71, 65, 52, 72, -4, -73, 106, -120, -91, 83, -122, -7, 91, -37, 56, 123, -61, 30, 34, 51, 36, 40, 54, -57, -78, 59, -114, 119, -70, -11, 20, -97, 8, 85, -101, 76, -2, 96, 92, -38, 24, 70, -51, 125, 33, -80, 63, 27, -119, -1, -21, -124, 105, 58, -99, -41, -45, 112, 103, 64, -75, -34, 93, 48, -111, -79, 120, 17, 1, -27, 0, 104, -104, -96, -59, 2, -90, 116, 45, 11, -94, 118, -77, -66, -50, -67, -82, -23, -118, 49, 28, -20, -15, -103, -108, -86, -10, 38, 47, -17, -24, -116, 53, 3, -44, 127, -5, 5, -63, 94, -112, 32, 61, -126, -9, -22, 10, 13, 126, -8, 80, 26, -60, 7, 87, -72, 60, 98, -29, -56, -84, 82, 100, 16, -48, -39, 19, 12, 18, 41, 81, -71, -49, -42, 115, -115, -127, 84, -64, -19, 78, 68, -89, 42, -123, 37, -26, -54, 124, -117, 86, -128};
    private static final byte[] S1 = new byte[]{-50, -69, -21, -110, -22, -53, 19, -63, -23, 58, -42, -78, -46, -112, 23, -8, 66, 21, 86, -76, 101, 28, -120, 67, -59, 92, 54, -70, -11, 87, 103, -115, 49, -10, 100, 88, -98, -12, 34, -86, 117, 15, 2, -79, -33, 109, 115, 77, 124, 38, 46, -9, 8, 93, 68, 62, -97, 20, -56, -82, 84, 16, -40, -68, 26, 107, 105, -13, -67, 51, -85, -6, -47, -101, 104, 78, 22, -107, -111, -18, 76, 99, -114, 91, -52, 60, 25, -95, -127, 73, 123, -39, 111, 55, 96, -54, -25, 43, 72, -3, -106, 69, -4, 65, 18, 13, 121, -27, -119, -116, -29, 32, 48, -36, -73, 108, 74, -75, 63, -105, -44, 98, 45, 6, -92, -91, -125, 95, 42, -38, -55, 0, 126, -94, 85, -65, 17, -43, -100, -49, 14, 10, 61, 81, 125, -109, 27, -2, -60, 71, 9, -122, 11, -113, -99, 106, 7, -71, -80, -104, 24, 50, 113, 75, -17, 59, 112, -96, -28, 64, -1, -61, -87, -26, 120, -7, -117, 70, -128, 30, 56, -31, -72, -88, -32, 12, 35, 118, 29, 37, 36, 5, -15, 110, -108, 40, -102, -124, -24, -93, 79, 119, -45, -123, -30, 82, -14, -126, 80, 122, 47, 116, 83, -77, 97, -81, 57, 53, -34, -51, 31, -103, -84, -83, 114, 44, -35, -48, -121, -66, 94, -90, -20, 4, -58, 3, 52, -5, -37, 89, -74, -62, 1, -16, 90, -19, -89, 102, 33, 127, -118, 39, -57, -64, 41, -41};
    private static final byte[] S2 = new byte[]{-109, -39, -102, -75, -104, 34, 69, -4, -70, 106, -33, 2, -97, -36, 81, 89, 74, 23, 43, -62, -108, -12, -69, -93, 98, -28, 113, -44, -51, 112, 22, -31, 73, 60, -64, -40, 92, -101, -83, -123, 83, -95, 122, -56, 45, -32, -47, 114, -90, 44, -60, -29, 118, 120, -73, -76, 9, 59, 14, 65, 76, -34, -78, -112, 37, -91, -41, 3, 17, 0, -61, 46, -110, -17, 78, 18, -99, 125, -53, 53, 16, -43, 79, -98, 77, -87, 85, -58, -48, 123, 24, -105, -45, 54, -26, 72, 86, -127, -113, 119, -52, -100, -71, -30, -84, -72, 47, 21, -92, 124, -38, 56, 30, 11, 5, -42, 20, 110, 108, 126, 102, -3, -79, -27, 96, -81, 94, 51, -121, -55, -16, 93, 109, 63, -120, -115, -57, -9, 29, -23, -20, -19, -128, 41, 39, -49, -103, -88, 80, 15, 55, 36, 40, 48, -107, -46, 62, 91, 64, -125, -77, 105, 87, 31, 7, 28, -118, -68, 32, -21, -50, -114, -85, -18, 49, -94, 115, -7, -54, 58, 26, -5, 13, -63, -2, -6, -14, 111, -67, -106, -35, 67, 82, -74, 8, -13, -82, -66, 25, -119, 50, 38, -80, -22, 75, 100, -124, -126, 107, -11, 121, -65, 1, 95, 117, 99, 27, 35, 61, 104, 42, 101, -24, -111, -10, -1, 19, 88, -15, 71, 10, 127, -59, -89, -25, 97, 90, 6, 70, 68, 66, 4, -96, -37, 57, -122, 84, -86, -116, 52, 33, -117, -8, 12, 116, 103};
    private static final byte[] S3 = new byte[]{104, -115, -54, 77, 115, 75, 78, 42, -44, 82, 38, -77, 84, 30, 25, 31, 34, 3, 70, 61, 45, 74, 83, -125, 19, -118, -73, -43, 37, 121, -11, -67, 88, 47, 13, 2, -19, 81, -98, 17, -14, 62, 85, 94, -47, 22, 60, 102, 112, 93, -13, 69, 64, -52, -24, -108, 86, 8, -50, 26, 58, -46, -31, -33, -75, 56, 110, 14, -27, -12, -7, -122, -23, 79, -42, -123, 35, -49, 50, -103, 49, 20, -82, -18, -56, 72, -45, 48, -95, -110, 65, -79, 24, -60, 44, 113, 114, 68, 21, -3, 55, -66, 95, -86, -101, -120, -40, -85, -119, -100, -6, 96, -22, -68, 98, 12, 36, -90, -88, -20, 103, 32, -37, 124, 40, -35, -84, 91, 52, 126, 16, -15, 123, -113, 99, -96, 5, -102, 67, 119, 33, -65, 39, 9, -61, -97, -74, -41, 41, -62, -21, -64, -92, -117, -116, 29, -5, -1, -63, -78, -105, 46, -8, 101, -10, 117, 7, 4, 73, 51, -28, -39, -71, -48, 66, -57, 108, -112, 0, -114, 111, 80, 1, -59, -38, 71, 63, -51, 105, -94, -30, 122, -89, -58, -109, 15, 10, 6, -26, 43, -106, -93, 28, -81, 106, 18, -124, 57, -25, -80, -126, -9, -2, -99, -121, 92, -127, 53, -34, -76, -91, -4, -128, -17, -53, -69, 107, 118, -70, 90, 125, 120, 11, -107, -29, -83, 116, -104, 59, 54, 100, 109, -36, -16, 89, -87, 76, 23, 127, -111, -72, -55, 87, 27, -32, 97};

    public DSTU7564Digest(DSTU7564Digest digest) {
        this.purpose = digest.purpose;
        this.copyIn(digest);
        CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
    }

    private void copyIn(DSTU7564Digest digest) {
        this.hashSize = digest.hashSize;
        this.blockSize = digest.blockSize;
        this.rounds = digest.rounds;
        if (this.columns > 0 && this.columns == digest.columns) {
            System.arraycopy(digest.state, 0, this.state, 0, this.columns);
            System.arraycopy(digest.buf, 0, this.buf, 0, this.blockSize);
        } else {
            this.columns = digest.columns;
            this.state = Arrays.clone(digest.state);
            this.tempState1 = new long[this.columns];
            this.tempState2 = new long[this.columns];
            this.buf = Arrays.clone(digest.buf);
        }
        this.inputBlocks = digest.inputBlocks;
        this.bufOff = digest.bufOff;
    }

    public DSTU7564Digest(int hashSizeBits) {
        this(hashSizeBits, CryptoServicePurpose.ANY);
    }

    public DSTU7564Digest(int hashSizeBits, CryptoServicePurpose purpose) {
        this.purpose = purpose;
        if (hashSizeBits != 256 && hashSizeBits != 384 && hashSizeBits != 512) {
            throw new IllegalArgumentException("Hash size is not recommended. Use 256/384/512 instead");
        }
        this.hashSize = hashSizeBits >>> 3;
        if (hashSizeBits > 256) {
            this.columns = 16;
            this.rounds = 14;
        } else {
            this.columns = 8;
            this.rounds = 10;
        }
        this.blockSize = this.columns << 3;
        this.state = new long[this.columns];
        this.state[0] = this.blockSize;
        this.tempState1 = new long[this.columns];
        this.tempState2 = new long[this.columns];
        this.buf = new byte[this.blockSize];
        CryptoServicesRegistrar.checkConstraints(this.cryptoServiceProperties());
    }

    @Override
    public String getAlgorithmName() {
        return "DSTU7564";
    }

    @Override
    public int getDigestSize() {
        return this.hashSize;
    }

    @Override
    public int getByteLength() {
        return this.blockSize;
    }

    @Override
    public void update(byte in) {
        this.buf[this.bufOff++] = in;
        if (this.bufOff == this.blockSize) {
            this.processBlock(this.buf, 0);
            this.bufOff = 0;
            ++this.inputBlocks;
        }
    }

    @Override
    public void update(byte[] in, int inOff, int len) {
        while (this.bufOff != 0 && len > 0) {
            this.update(in[inOff++]);
            --len;
        }
        if (len > 0) {
            while (len >= this.blockSize) {
                this.processBlock(in, inOff);
                inOff += this.blockSize;
                len -= this.blockSize;
                ++this.inputBlocks;
            }
            while (len > 0) {
                this.update(in[inOff++]);
                --len;
            }
        }
    }

    @Override
    public int doFinal(byte[] out, int outOff) {
        int inputBytes = this.bufOff;
        this.buf[this.bufOff++] = -128;
        int lenPos = this.blockSize - 12;
        if (this.bufOff > lenPos) {
            while (this.bufOff < this.blockSize) {
                this.buf[this.bufOff++] = 0;
            }
            this.bufOff = 0;
            this.processBlock(this.buf, 0);
        }
        while (this.bufOff < lenPos) {
            this.buf[this.bufOff++] = 0;
        }
        long c = (this.inputBlocks & 0xFFFFFFFFL) * (long)this.blockSize + (long)inputBytes << 3;
        Pack.intToLittleEndian((int)c, this.buf, this.bufOff);
        this.bufOff += 4;
        c >>>= 32;
        Pack.longToLittleEndian(c += (this.inputBlocks >>> 32) * (long)this.blockSize << 3, this.buf, this.bufOff);
        this.processBlock(this.buf, 0);
        System.arraycopy(this.state, 0, this.tempState1, 0, this.columns);
        this.P(this.tempState1);
        for (int col = 0; col < this.columns; ++col) {
            int n = col;
            this.state[n] = this.state[n] ^ this.tempState1[col];
        }
        int neededColumns = this.hashSize >>> 3;
        for (int col = this.columns - neededColumns; col < this.columns; ++col) {
            Pack.longToLittleEndian(this.state[col], out, outOff);
            outOff += 8;
        }
        this.reset();
        return this.hashSize;
    }

    @Override
    public void reset() {
        Arrays.fill(this.state, 0L);
        this.state[0] = this.blockSize;
        this.inputBlocks = 0L;
        this.bufOff = 0;
    }

    private void processBlock(byte[] input, int inOff) {
        int col;
        int pos = inOff;
        for (col = 0; col < this.columns; ++col) {
            long word = Pack.littleEndianToLong(input, pos);
            pos += 8;
            this.tempState1[col] = this.state[col] ^ word;
            this.tempState2[col] = word;
        }
        this.P(this.tempState1);
        this.Q(this.tempState2);
        for (col = 0; col < this.columns; ++col) {
            int n = col;
            this.state[n] = this.state[n] ^ (this.tempState1[col] ^ this.tempState2[col]);
        }
    }

    private void P(long[] s) {
        for (int round = 0; round < this.rounds; ++round) {
            long rc = round;
            int col = 0;
            while (col < this.columns) {
                int n = col++;
                s[n] = s[n] ^ rc;
                rc += 16L;
            }
            this.shiftRows(s);
            this.subBytes(s);
            this.mixColumns(s);
        }
    }

    private void Q(long[] s) {
        for (int round = 0; round < this.rounds; ++round) {
            long rc = (long)(this.columns - 1 << 4 ^ round) << 56 | 0xF0F0F0F0F0F0F3L;
            int col = 0;
            while (col < this.columns) {
                int n = col++;
                s[n] = s[n] + rc;
                rc -= 0x1000000000000000L;
            }
            this.shiftRows(s);
            this.subBytes(s);
            this.mixColumns(s);
        }
    }

    private static long mixColumn(long c) {
        long x1 = (c & 0x7F7F7F7F7F7F7F7FL) << 1 ^ ((c & 0x8080808080808080L) >>> 7) * 29L;
        long u = DSTU7564Digest.rotate(8, c) ^ c;
        u ^= DSTU7564Digest.rotate(16, u);
        long v = (u ^= DSTU7564Digest.rotate(48, c)) ^ c ^ x1;
        v = (v & 0x3F3F3F3F3F3F3F3FL) << 2 ^ ((v & 0x8080808080808080L) >>> 6) * 29L ^ ((v & 0x4040404040404040L) >>> 6) * 29L;
        return u ^ DSTU7564Digest.rotate(32, v) ^ DSTU7564Digest.rotate(40, x1) ^ DSTU7564Digest.rotate(48, x1);
    }

    private void mixColumns(long[] s) {
        for (int col = 0; col < this.columns; ++col) {
            s[col] = DSTU7564Digest.mixColumn(s[col]);
        }
    }

    private static long rotate(int n, long x) {
        return x >>> n | x << -n;
    }

    private void shiftRows(long[] s) {
        switch (this.columns) {
            case 8: {
                long c0 = s[0];
                long c1 = s[1];
                long c2 = s[2];
                long c3 = s[3];
                long c4 = s[4];
                long c5 = s[5];
                long c6 = s[6];
                long c7 = s[7];
                long d = (c0 ^ c4) & 0xFFFFFFFF00000000L;
                c0 ^= d;
                c4 ^= d;
                d = (c1 ^ c5) & 0xFFFFFFFF000000L;
                c1 ^= d;
                c5 ^= d;
                d = (c2 ^ c6) & 0xFFFFFFFF0000L;
                c2 ^= d;
                c6 ^= d;
                d = (c3 ^ c7) & 0xFFFFFFFF00L;
                c3 ^= d;
                c7 ^= d;
                d = (c0 ^ c2) & 0xFFFF0000FFFF0000L;
                c0 ^= d;
                c2 ^= d;
                d = (c1 ^ c3) & 0xFFFF0000FFFF00L;
                c1 ^= d;
                c3 ^= d;
                d = (c4 ^ c6) & 0xFFFF0000FFFF0000L;
                c4 ^= d;
                c6 ^= d;
                d = (c5 ^ c7) & 0xFFFF0000FFFF00L;
                c5 ^= d;
                c7 ^= d;
                d = (c0 ^ c1) & 0xFF00FF00FF00FF00L;
                c0 ^= d;
                c1 ^= d;
                d = (c2 ^ c3) & 0xFF00FF00FF00FF00L;
                c2 ^= d;
                c3 ^= d;
                d = (c4 ^ c5) & 0xFF00FF00FF00FF00L;
                c4 ^= d;
                c5 ^= d;
                d = (c6 ^ c7) & 0xFF00FF00FF00FF00L;
                c6 ^= d;
                c7 ^= d;
                s[0] = c0;
                s[1] = c1;
                s[2] = c2;
                s[3] = c3;
                s[4] = c4;
                s[5] = c5;
                s[6] = c6;
                s[7] = c7;
                break;
            }
            case 16: {
                long c00 = s[0];
                long c01 = s[1];
                long c02 = s[2];
                long c03 = s[3];
                long c04 = s[4];
                long c05 = s[5];
                long c06 = s[6];
                long c07 = s[7];
                long c08 = s[8];
                long c09 = s[9];
                long c10 = s[10];
                long c11 = s[11];
                long c12 = s[12];
                long c13 = s[13];
                long c14 = s[14];
                long c15 = s[15];
                long d = (c00 ^ c08) & 0xFF00000000000000L;
                c00 ^= d;
                c08 ^= d;
                d = (c01 ^ c09) & 0xFF00000000000000L;
                c01 ^= d;
                c09 ^= d;
                d = (c02 ^ c10) & 0xFFFF000000000000L;
                c02 ^= d;
                c10 ^= d;
                d = (c03 ^ c11) & 0xFFFFFF0000000000L;
                c03 ^= d;
                c11 ^= d;
                d = (c04 ^ c12) & 0xFFFFFFFF00000000L;
                c04 ^= d;
                c12 ^= d;
                d = (c05 ^ c13) & 0xFFFFFFFF000000L;
                c05 ^= d;
                c13 ^= d;
                d = (c06 ^ c14) & 0xFFFFFFFFFF0000L;
                c06 ^= d;
                c14 ^= d;
                d = (c07 ^ c15) & 0xFFFFFFFFFFFF00L;
                c07 ^= d;
                c15 ^= d;
                d = (c00 ^ c04) & 0xFFFFFF00000000L;
                c00 ^= d;
                c04 ^= d;
                d = (c01 ^ c05) & 0xFFFFFFFFFF000000L;
                c01 ^= d;
                c05 ^= d;
                d = (c02 ^ c06) & 0xFF00FFFFFFFF0000L;
                c02 ^= d;
                c06 ^= d;
                d = (c03 ^ c07) & 0xFF0000FFFFFFFF00L;
                c03 ^= d;
                c07 ^= d;
                d = (c08 ^ c12) & 0xFFFFFF00000000L;
                c08 ^= d;
                c12 ^= d;
                d = (c09 ^ c13) & 0xFFFFFFFFFF000000L;
                c09 ^= d;
                c13 ^= d;
                d = (c10 ^ c14) & 0xFF00FFFFFFFF0000L;
                c10 ^= d;
                c14 ^= d;
                d = (c11 ^ c15) & 0xFF0000FFFFFFFF00L;
                c11 ^= d;
                c15 ^= d;
                d = (c00 ^ c02) & 0xFFFF0000FFFF0000L;
                c00 ^= d;
                c02 ^= d;
                d = (c01 ^ c03) & 0xFFFF0000FFFF00L;
                c01 ^= d;
                c03 ^= d;
                d = (c04 ^ c06) & 0xFFFF0000FFFF0000L;
                c04 ^= d;
                c06 ^= d;
                d = (c05 ^ c07) & 0xFFFF0000FFFF00L;
                c05 ^= d;
                c07 ^= d;
                d = (c08 ^ c10) & 0xFFFF0000FFFF0000L;
                c08 ^= d;
                c10 ^= d;
                d = (c09 ^ c11) & 0xFFFF0000FFFF00L;
                c09 ^= d;
                c11 ^= d;
                d = (c12 ^ c14) & 0xFFFF0000FFFF0000L;
                c12 ^= d;
                c14 ^= d;
                d = (c13 ^ c15) & 0xFFFF0000FFFF00L;
                c13 ^= d;
                c15 ^= d;
                d = (c00 ^ c01) & 0xFF00FF00FF00FF00L;
                c00 ^= d;
                c01 ^= d;
                d = (c02 ^ c03) & 0xFF00FF00FF00FF00L;
                c02 ^= d;
                c03 ^= d;
                d = (c04 ^ c05) & 0xFF00FF00FF00FF00L;
                c04 ^= d;
                c05 ^= d;
                d = (c06 ^ c07) & 0xFF00FF00FF00FF00L;
                c06 ^= d;
                c07 ^= d;
                d = (c08 ^ c09) & 0xFF00FF00FF00FF00L;
                c08 ^= d;
                c09 ^= d;
                d = (c10 ^ c11) & 0xFF00FF00FF00FF00L;
                c10 ^= d;
                c11 ^= d;
                d = (c12 ^ c13) & 0xFF00FF00FF00FF00L;
                c12 ^= d;
                c13 ^= d;
                d = (c14 ^ c15) & 0xFF00FF00FF00FF00L;
                c14 ^= d;
                c15 ^= d;
                s[0] = c00;
                s[1] = c01;
                s[2] = c02;
                s[3] = c03;
                s[4] = c04;
                s[5] = c05;
                s[6] = c06;
                s[7] = c07;
                s[8] = c08;
                s[9] = c09;
                s[10] = c10;
                s[11] = c11;
                s[12] = c12;
                s[13] = c13;
                s[14] = c14;
                s[15] = c15;
                break;
            }
            default: {
                throw new IllegalStateException("unsupported state size: only 512/1024 are allowed");
            }
        }
    }

    private void subBytes(long[] s) {
        for (int i = 0; i < this.columns; ++i) {
            long u = s[i];
            int lo = (int)u;
            int hi = (int)(u >>> 32);
            byte t0 = S0[lo & 0xFF];
            byte t1 = S1[lo >>> 8 & 0xFF];
            byte t2 = S2[lo >>> 16 & 0xFF];
            byte t3 = S3[lo >>> 24];
            lo = t0 & 0xFF | (t1 & 0xFF) << 8 | (t2 & 0xFF) << 16 | t3 << 24;
            byte t4 = S0[hi & 0xFF];
            byte t5 = S1[hi >>> 8 & 0xFF];
            byte t6 = S2[hi >>> 16 & 0xFF];
            byte t7 = S3[hi >>> 24];
            hi = t4 & 0xFF | (t5 & 0xFF) << 8 | (t6 & 0xFF) << 16 | t7 << 24;
            s[i] = (long)lo & 0xFFFFFFFFL | (long)hi << 32;
        }
    }

    @Override
    public Memoable copy() {
        return new DSTU7564Digest(this);
    }

    @Override
    public void reset(Memoable other) {
        DSTU7564Digest d = (DSTU7564Digest)other;
        this.copyIn(d);
    }

    protected CryptoServiceProperties cryptoServiceProperties() {
        return Utils.getDefaultProperties(this, 256, this.purpose);
    }
}

