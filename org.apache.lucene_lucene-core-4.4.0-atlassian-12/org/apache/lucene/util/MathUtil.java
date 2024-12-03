/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.util;

public final class MathUtil {
    private MathUtil() {
    }

    public static int log(long x, int base) {
        if (base <= 1) {
            throw new IllegalArgumentException("base must be > 1");
        }
        int ret = 0;
        while (x >= (long)base) {
            x /= (long)base;
            ++ret;
        }
        return ret;
    }

    public static long gcd(long a, long b) {
        a = Math.abs(a);
        b = Math.abs(b);
        if (a == 0L) {
            return b;
        }
        if (b == 0L) {
            return a;
        }
        int commonTrailingZeros = Long.numberOfTrailingZeros(a | b);
        a >>>= Long.numberOfTrailingZeros(a);
        while (a != (b >>>= Long.numberOfTrailingZeros(b))) {
            if (a > b || a == Long.MIN_VALUE) {
                long tmp = a;
                a = b;
                b = tmp;
            }
            if (a == 1L) break;
            b -= a;
        }
        return a << commonTrailingZeros;
    }
}

