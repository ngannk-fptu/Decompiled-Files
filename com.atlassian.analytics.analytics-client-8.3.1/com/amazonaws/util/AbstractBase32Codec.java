/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.util;

import com.amazonaws.util.Codec;
import com.amazonaws.util.CodecUtils;

abstract class AbstractBase32Codec
implements Codec {
    private static final int MASK_2BITS = 3;
    private static final int MASK_3BITS = 7;
    private static final int MASK_4BITS = 15;
    private static final int MASK_5BITS = 31;
    private static final byte PAD = 61;
    private final byte[] alphabets;

    protected AbstractBase32Codec(byte[] alphabets) {
        this.alphabets = alphabets;
    }

    @Override
    public final byte[] encode(byte[] src) {
        int num5bytes = src.length / 5;
        int remainder = src.length % 5;
        if (remainder == 0) {
            byte[] dest = new byte[num5bytes * 8];
            int s = 0;
            int d = 0;
            while (s < src.length) {
                this.encode5bytes(src, s, dest, d);
                s += 5;
                d += 8;
            }
            return dest;
        }
        byte[] dest = new byte[(num5bytes + 1) * 8];
        int s = 0;
        int d = 0;
        while (s < src.length - remainder) {
            this.encode5bytes(src, s, dest, d);
            s += 5;
            d += 8;
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
            case 3: {
                this.encode3bytes(src, s, dest, d);
                break;
            }
            case 4: {
                this.encode4bytes(src, s, dest, d);
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
        return dest;
    }

    private final void encode5bytes(byte[] src, int s, byte[] dest, int d) {
        int n = d++;
        byte p = src[s++];
        dest[n] = this.alphabets[p >>> 3 & 0x1F];
        int n2 = d++;
        int n3 = (p & 7) << 2;
        p = src[s++];
        dest[n2] = this.alphabets[n3 | p >>> 6 & 3];
        dest[d++] = this.alphabets[p >>> 1 & 0x1F];
        int n4 = d++;
        int n5 = (p & 1) << 4;
        p = src[s++];
        dest[n4] = this.alphabets[n5 | p >>> 4 & 0xF];
        int n6 = d++;
        int n7 = (p & 0xF) << 1;
        p = src[s++];
        dest[n6] = this.alphabets[n7 | p >>> 7 & 1];
        dest[d++] = this.alphabets[p >>> 2 & 0x1F];
        int n8 = d++;
        int n9 = (p & 3) << 3;
        p = src[s];
        dest[n8] = this.alphabets[n9 | p >>> 5 & 7];
        dest[d] = this.alphabets[p & 0x1F];
    }

    private final void encode4bytes(byte[] src, int s, byte[] dest, int d) {
        int n = d++;
        byte p = src[s++];
        dest[n] = this.alphabets[p >>> 3 & 0x1F];
        int n2 = d++;
        int n3 = (p & 7) << 2;
        p = src[s++];
        dest[n2] = this.alphabets[n3 | p >>> 6 & 3];
        dest[d++] = this.alphabets[p >>> 1 & 0x1F];
        int n4 = d++;
        int n5 = (p & 1) << 4;
        p = src[s++];
        dest[n4] = this.alphabets[n5 | p >>> 4 & 0xF];
        int n6 = d++;
        int n7 = (p & 0xF) << 1;
        p = src[s];
        dest[n6] = this.alphabets[n7 | p >>> 7 & 1];
        dest[d++] = this.alphabets[p >>> 2 & 0x1F];
        dest[d++] = this.alphabets[(p & 3) << 3];
        dest[d] = 61;
    }

    private final void encode3bytes(byte[] src, int s, byte[] dest, int d) {
        int n = d++;
        byte p = src[s++];
        dest[n] = this.alphabets[p >>> 3 & 0x1F];
        int n2 = d++;
        int n3 = (p & 7) << 2;
        p = src[s++];
        dest[n2] = this.alphabets[n3 | p >>> 6 & 3];
        dest[d++] = this.alphabets[p >>> 1 & 0x1F];
        int n4 = d++;
        int n5 = (p & 1) << 4;
        p = src[s];
        dest[n4] = this.alphabets[n5 | p >>> 4 & 0xF];
        dest[d++] = this.alphabets[(p & 0xF) << 1];
        for (int i = 0; i < 3; ++i) {
            dest[d++] = 61;
        }
    }

    private final void encode2bytes(byte[] src, int s, byte[] dest, int d) {
        int n = d++;
        byte p = src[s++];
        dest[n] = this.alphabets[p >>> 3 & 0x1F];
        int n2 = d++;
        int n3 = (p & 7) << 2;
        p = src[s];
        dest[n2] = this.alphabets[n3 | p >>> 6 & 3];
        dest[d++] = this.alphabets[p >>> 1 & 0x1F];
        dest[d++] = this.alphabets[(p & 1) << 4];
        for (int i = 0; i < 4; ++i) {
            dest[d++] = 61;
        }
    }

    private final void encode1byte(byte[] src, int s, byte[] dest, int d) {
        int n = d++;
        byte p = src[s];
        dest[n] = this.alphabets[p >>> 3 & 0x1F];
        dest[d++] = this.alphabets[(p & 7) << 2];
        for (int i = 0; i < 6; ++i) {
            dest[d++] = 61;
        }
    }

    private final void decode5bytes(byte[] src, int s, byte[] dest, int d) {
        int p = 0;
        int n = d++;
        int n2 = s++;
        p = this.pos(src[s++]);
        dest[n] = (byte)(this.pos(src[n2]) << 3 | p >>> 2 & 7);
        int n3 = d++;
        int n4 = (p & 3) << 6 | this.pos(src[s++]) << 1;
        p = this.pos(src[s++]);
        dest[n3] = (byte)(n4 | p >>> 4 & 1);
        int n5 = d++;
        int n6 = (p & 0xF) << 4;
        p = this.pos(src[s++]);
        dest[n5] = (byte)(n6 | p >>> 1 & 0xF);
        int n7 = d++;
        int n8 = (p & 1) << 7 | this.pos(src[s++]) << 2;
        p = this.pos(src[s++]);
        dest[n7] = (byte)(n8 | p >>> 3 & 3);
        dest[d] = (byte)((p & 7) << 5 | this.pos(src[s]));
    }

    private final void decode1to4bytes(int n, byte[] src, int s, byte[] dest, int d) {
        int p = 0;
        int n2 = d++;
        int n3 = s++;
        p = this.pos(src[s++]);
        dest[n2] = (byte)(this.pos(src[n3]) << 3 | p >>> 2 & 7);
        if (n == 1) {
            CodecUtils.sanityCheckLastPos(p, 3);
            return;
        }
        int n4 = d++;
        int n5 = (p & 3) << 6 | this.pos(src[s++]) << 1;
        p = this.pos(src[s++]);
        dest[n4] = (byte)(n5 | p >>> 4 & 1);
        if (n == 2) {
            CodecUtils.sanityCheckLastPos(p, 15);
            return;
        }
        int n6 = d++;
        int n7 = (p & 0xF) << 4;
        p = this.pos(src[s++]);
        dest[n6] = (byte)(n7 | p >>> 1 & 0xF);
        if (n == 3) {
            CodecUtils.sanityCheckLastPos(p, 1);
            return;
        }
        int n8 = (p & 1) << 7 | this.pos(src[s++]) << 2;
        p = this.pos(src[s]);
        dest[d] = (byte)(n8 | p >>> 3 & 3);
        CodecUtils.sanityCheckLastPos(p, 7);
    }

    @Override
    public final byte[] decode(byte[] src, int length) {
        int d;
        int fq;
        if (length % 8 != 0) {
            throw new IllegalArgumentException("Input is expected to be encoded in multiple of 8 bytes but found: " + length);
        }
        int pads = 0;
        for (int last = length - 1; pads < 6 && last > -1 && src[last] == 61; --last, ++pads) {
        }
        switch (pads) {
            case 0: {
                fq = 5;
                break;
            }
            case 1: {
                fq = 4;
                break;
            }
            case 3: {
                fq = 3;
                break;
            }
            case 4: {
                fq = 2;
                break;
            }
            case 6: {
                fq = 1;
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid number of paddings " + pads);
            }
        }
        byte[] dest = new byte[length / 8 * 5 - (5 - fq)];
        int s = 0;
        for (d = 0; d < dest.length - fq % 5; d += 5) {
            this.decode5bytes(src, s, dest, d);
            s += 8;
        }
        if (fq < 5) {
            this.decode1to4bytes(fq, src, s, dest, d);
        }
        return dest;
    }

    protected abstract int pos(byte var1);
}

