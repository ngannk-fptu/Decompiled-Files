/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.raw;

import org.bouncycastle.math.raw.Bits;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class Interleave {
    private static final long M32 = 0x55555555L;
    private static final long M64 = 0x5555555555555555L;
    private static final long M64R = -6148914691236517206L;

    public static int expand8to16(int x) {
        x &= 0xFF;
        x = (x | x << 4) & 0xF0F;
        x = (x | x << 2) & 0x3333;
        x = (x | x << 1) & 0x5555;
        return x;
    }

    public static int expand16to32(int x) {
        x &= 0xFFFF;
        x = (x | x << 8) & 0xFF00FF;
        x = (x | x << 4) & 0xF0F0F0F;
        x = (x | x << 2) & 0x33333333;
        x = (x | x << 1) & 0x55555555;
        return x;
    }

    public static long expand32to64(int x) {
        x = Bits.bitPermuteStep(x, 65280, 8);
        x = Bits.bitPermuteStep(x, 0xF000F0, 4);
        x = Bits.bitPermuteStep(x, 0xC0C0C0C, 2);
        x = Bits.bitPermuteStep(x, 0x22222222, 1);
        return ((long)(x >>> 1) & 0x55555555L) << 32 | (long)x & 0x55555555L;
    }

    public static void expand64To128(long x, long[] z, int zOff) {
        x = Bits.bitPermuteStep(x, 0xFFFF0000L, 16);
        x = Bits.bitPermuteStep(x, 0xFF000000FF00L, 8);
        x = Bits.bitPermuteStep(x, 0xF000F000F000F0L, 4);
        x = Bits.bitPermuteStep(x, 0xC0C0C0C0C0C0C0CL, 2);
        x = Bits.bitPermuteStep(x, 0x2222222222222222L, 1);
        z[zOff] = x & 0x5555555555555555L;
        z[zOff + 1] = x >>> 1 & 0x5555555555555555L;
    }

    public static void expand64To128(long[] xs, int xsOff, int xsLen, long[] zs, int zsOff) {
        for (int i = 0; i < xsLen; ++i) {
            Interleave.expand64To128(xs[xsOff + i], zs, zsOff);
            zsOff += 2;
        }
    }

    public static void expand64To128Rev(long x, long[] z, int zOff) {
        x = Bits.bitPermuteStep(x, 0xFFFF0000L, 16);
        x = Bits.bitPermuteStep(x, 0xFF000000FF00L, 8);
        x = Bits.bitPermuteStep(x, 0xF000F000F000F0L, 4);
        x = Bits.bitPermuteStep(x, 0xC0C0C0C0C0C0C0CL, 2);
        x = Bits.bitPermuteStep(x, 0x2222222222222222L, 1);
        z[zOff] = x & 0xAAAAAAAAAAAAAAAAL;
        z[zOff + 1] = x << 1 & 0xAAAAAAAAAAAAAAAAL;
    }

    public static int shuffle(int x) {
        x = Bits.bitPermuteStep(x, 65280, 8);
        x = Bits.bitPermuteStep(x, 0xF000F0, 4);
        x = Bits.bitPermuteStep(x, 0xC0C0C0C, 2);
        x = Bits.bitPermuteStep(x, 0x22222222, 1);
        return x;
    }

    public static long shuffle(long x) {
        x = Bits.bitPermuteStep(x, 0xFFFF0000L, 16);
        x = Bits.bitPermuteStep(x, 0xFF000000FF00L, 8);
        x = Bits.bitPermuteStep(x, 0xF000F000F000F0L, 4);
        x = Bits.bitPermuteStep(x, 0xC0C0C0C0C0C0C0CL, 2);
        x = Bits.bitPermuteStep(x, 0x2222222222222222L, 1);
        return x;
    }

    public static int shuffle2(int x) {
        x = Bits.bitPermuteStep(x, 0xAA00AA, 7);
        x = Bits.bitPermuteStep(x, 52428, 14);
        x = Bits.bitPermuteStep(x, 0xF000F0, 4);
        x = Bits.bitPermuteStep(x, 65280, 8);
        return x;
    }

    public static long shuffle2(long x) {
        x = Bits.bitPermuteStep(x, 0xFF00FF00L, 24);
        x = Bits.bitPermuteStep(x, 0xCC00CC00CC00CCL, 6);
        x = Bits.bitPermuteStep(x, 0xF0F00000F0F0L, 12);
        x = Bits.bitPermuteStep(x, 0xA0A0A0A0A0A0A0AL, 3);
        return x;
    }

    public static long shuffle3(long x) {
        x = Bits.bitPermuteStep(x, 0xAA00AA00AA00AAL, 7);
        x = Bits.bitPermuteStep(x, 0xCCCC0000CCCCL, 14);
        x = Bits.bitPermuteStep(x, 0xF0F0F0F0L, 28);
        return x;
    }

    public static int unshuffle(int x) {
        x = Bits.bitPermuteStep(x, 0x22222222, 1);
        x = Bits.bitPermuteStep(x, 0xC0C0C0C, 2);
        x = Bits.bitPermuteStep(x, 0xF000F0, 4);
        x = Bits.bitPermuteStep(x, 65280, 8);
        return x;
    }

    public static long unshuffle(long x) {
        x = Bits.bitPermuteStep(x, 0x2222222222222222L, 1);
        x = Bits.bitPermuteStep(x, 0xC0C0C0C0C0C0C0CL, 2);
        x = Bits.bitPermuteStep(x, 0xF000F000F000F0L, 4);
        x = Bits.bitPermuteStep(x, 0xFF000000FF00L, 8);
        x = Bits.bitPermuteStep(x, 0xFFFF0000L, 16);
        return x;
    }

    public static int unshuffle2(int x) {
        x = Bits.bitPermuteStep(x, 65280, 8);
        x = Bits.bitPermuteStep(x, 0xF000F0, 4);
        x = Bits.bitPermuteStep(x, 52428, 14);
        x = Bits.bitPermuteStep(x, 0xAA00AA, 7);
        return x;
    }

    public static long unshuffle2(long x) {
        x = Bits.bitPermuteStep(x, 0xA0A0A0A0A0A0A0AL, 3);
        x = Bits.bitPermuteStep(x, 0xF0F00000F0F0L, 12);
        x = Bits.bitPermuteStep(x, 0xCC00CC00CC00CCL, 6);
        x = Bits.bitPermuteStep(x, 0xFF00FF00L, 24);
        return x;
    }

    public static long unshuffle3(long x) {
        return Interleave.shuffle3(x);
    }
}

