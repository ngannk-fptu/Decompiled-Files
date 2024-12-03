/*
 * Decompiled with CFR 0.152.
 */
package org.jheaps.monotone;

import java.math.BigInteger;

class UnsignedUtils {
    private static final long UNSIGNED_MASK = Long.MAX_VALUE;

    UnsignedUtils() {
    }

    static double unsignedLongToDouble(long x) {
        double d = x & Long.MAX_VALUE;
        if (x < 0L) {
            d += 9.223372036854776E18;
        }
        return d;
    }

    static BigInteger unsignedLongToBigInt(long x) {
        BigInteger asBigInt = BigInteger.valueOf(x & Long.MAX_VALUE);
        if (x < 0L) {
            asBigInt = asBigInt.setBit(63);
        }
        return asBigInt;
    }
}

