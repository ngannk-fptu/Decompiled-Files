/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.math.raw;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public abstract class Bits {
    public static int bitPermuteStep(int x, int m, int s) {
        int t = (x ^ x >>> s) & m;
        return t ^ t << s ^ x;
    }

    public static long bitPermuteStep(long x, long m, int s) {
        long t = (x ^ x >>> s) & m;
        return t ^ t << s ^ x;
    }

    public static int bitPermuteStepSimple(int x, int m, int s) {
        return (x & m) << s | x >>> s & m;
    }

    public static long bitPermuteStepSimple(long x, long m, int s) {
        return (x & m) << s | x >>> s & m;
    }
}

