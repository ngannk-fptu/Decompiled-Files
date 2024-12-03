/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.util;

import com.amazonaws.util.Codec;
import com.amazonaws.util.CodecUtils;

class Base64Codec
implements Codec {
    private static final int OFFSET_OF_a = 71;
    private static final int OFFSET_OF_0 = -4;
    private static final int OFFSET_OF_PLUS = -19;
    private static final int OFFSET_OF_SLASH = -16;
    private static final int MASK_2BITS = 3;
    private static final int MASK_4BITS = 15;
    private static final int MASK_6BITS = 63;
    private static final byte PAD = 61;
    private final byte[] alphabets;

    Base64Codec() {
        this.alphabets = CodecUtils.toBytesDirect("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/");
    }

    protected Base64Codec(byte[] alphabets) {
        this.alphabets = alphabets;
    }

    @Override
    public byte[] encode(byte[] src) {
        int num3bytes = src.length / 3;
        int remainder = src.length % 3;
        if (remainder == 0) {
            byte[] dest = new byte[num3bytes * 4];
            int s = 0;
            int d = 0;
            while (s < src.length) {
                this.encode3bytes(src, s, dest, d);
                s += 3;
                d += 4;
            }
            return dest;
        }
        byte[] dest = new byte[(num3bytes + 1) * 4];
        int s = 0;
        int d = 0;
        while (s < src.length - remainder) {
            this.encode3bytes(src, s, dest, d);
            s += 3;
            d += 4;
        }
        switch (remainder) {
            case 1: {
                this.encode1byte(src, s, dest, d);
                break;
            }
            case 2: {
                this.encode2bytes(src, s, dest, d);
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
        return dest;
    }

    void encode3bytes(byte[] src, int s, byte[] dest, int d) {
        int n = d++;
        byte p = src[s++];
        dest[n] = this.alphabets[p >>> 2 & 0x3F];
        int n2 = d++;
        int n3 = (p & 3) << 4;
        p = src[s++];
        dest[n2] = this.alphabets[n3 | p >>> 4 & 0xF];
        int n4 = d++;
        int n5 = (p & 0xF) << 2;
        p = src[s];
        dest[n4] = this.alphabets[n5 | p >>> 6 & 3];
        dest[d] = this.alphabets[p & 0x3F];
    }

    void encode2bytes(byte[] src, int s, byte[] dest, int d) {
        int n = d++;
        byte p = src[s++];
        dest[n] = this.alphabets[p >>> 2 & 0x3F];
        int n2 = d++;
        int n3 = (p & 3) << 4;
        p = src[s];
        dest[n2] = this.alphabets[n3 | p >>> 4 & 0xF];
        dest[d++] = this.alphabets[(p & 0xF) << 2];
        dest[d] = 61;
    }

    void encode1byte(byte[] src, int s, byte[] dest, int d) {
        int n = d++;
        byte p = src[s];
        dest[n] = this.alphabets[p >>> 2 & 0x3F];
        dest[d++] = this.alphabets[(p & 3) << 4];
        dest[d++] = 61;
        dest[d] = 61;
    }

    void decode4bytes(byte[] src, int s, byte[] dest, int d) {
        int p = 0;
        int n = d++;
        int n2 = s++;
        p = this.pos(src[s++]);
        dest[n] = (byte)(this.pos(src[n2]) << 2 | p >>> 4 & 3);
        int n3 = d++;
        int n4 = (p & 0xF) << 4;
        p = this.pos(src[s++]);
        dest[n3] = (byte)(n4 | p >>> 2 & 0xF);
        dest[d] = (byte)((p & 3) << 6 | this.pos(src[s]));
    }

    void decode1to3bytes(int n, byte[] src, int s, byte[] dest, int d) {
        int p = 0;
        int n2 = d++;
        int n3 = s++;
        p = this.pos(src[s++]);
        dest[n2] = (byte)(this.pos(src[n3]) << 2 | p >>> 4 & 3);
        if (n == 1) {
            CodecUtils.sanityCheckLastPos(p, 15);
            return;
        }
        int n4 = d++;
        int n5 = (p & 0xF) << 4;
        p = this.pos(src[s++]);
        dest[n4] = (byte)(n5 | p >>> 2 & 0xF);
        if (n == 2) {
            CodecUtils.sanityCheckLastPos(p, 3);
            return;
        }
        dest[d] = (byte)((p & 3) << 6 | this.pos(src[s]));
    }

    @Override
    public byte[] decode(byte[] src, int length) {
        int d;
        int fq;
        if (length % 4 != 0) {
            throw new IllegalArgumentException("Input is expected to be encoded in multiple of 4 bytes but found: " + length);
        }
        int pads = 0;
        for (int last = length - 1; pads < 2 && last > -1 && src[last] == 61; --last, ++pads) {
        }
        switch (pads) {
            case 0: {
                fq = 3;
                break;
            }
            case 1: {
                fq = 2;
                break;
            }
            case 2: {
                fq = 1;
                break;
            }
            default: {
                throw new Error("Impossible");
            }
        }
        byte[] dest = new byte[length / 4 * 3 - (3 - fq)];
        int s = 0;
        for (d = 0; d < dest.length - fq % 3; d += 3) {
            this.decode4bytes(src, s, dest, d);
            s += 4;
        }
        if (fq < 3) {
            this.decode1to3bytes(fq, src, s, dest, d);
        }
        return dest;
    }

    protected int pos(byte in) {
        byte pos = LazyHolder.DECODED[in];
        if (pos > -1) {
            return pos;
        }
        throw new IllegalArgumentException("Invalid base 64 character: '" + (char)in + "'");
    }

    private static class LazyHolder {
        private static final byte[] DECODED = LazyHolder.decodeTable();

        private LazyHolder() {
        }

        private static byte[] decodeTable() {
            byte[] dest = new byte[123];
            for (int i = 0; i <= 122; ++i) {
                dest[i] = i >= 65 && i <= 90 ? (int)(i - 65) : (i >= 48 && i <= 57 ? (int)(i - -4) : (i == 43 ? (int)(i - -19) : (i == 47 ? (int)(i - -16) : (i >= 97 && i <= 122 ? (int)(i - 71) : -1))));
            }
            return dest;
        }
    }
}

