/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import java.math.BigInteger;
import org.apfloat.internal.LongModConstants;

/*
 * Multiple versions of this class in jar - see https://www.benf.org/other/cfr/multi-version-jar.html
 */
public class LongElementaryModMath {
    private static final long[] INVERSE_MODULUS = new long[]{BigInteger.ONE.shiftLeft(119).divide(BigInteger.valueOf(LongModConstants.MODULUS[0])).longValueExact(), BigInteger.ONE.shiftLeft(119).divide(BigInteger.valueOf(LongModConstants.MODULUS[1])).longValueExact(), BigInteger.ONE.shiftLeft(119).divide(BigInteger.valueOf(LongModConstants.MODULUS[2])).longValueExact()};
    private long modulus;
    private long inverseModulus;

    public final long modMultiply(long l, long l2) {
        long l3 = l * l2;
        long l4 = Math.multiplyHigh(l, l2) << 9 | l3 >>> 55;
        long l5 = l3 - Math.multiplyHigh(l4, this.inverseModulus) * this.modulus;
        long l6 = l5 - this.modulus;
        return l6 < 0L ? l5 : l6;
    }

    public final long modAdd(long l, long l2) {
        long l3 = l + l2;
        return l3 >= this.modulus ? l3 - this.modulus : l3;
    }

    public final long modSubtract(long l, long l2) {
        long l3 = l - l2;
        return l3 < 0L ? l3 + this.modulus : l3;
    }

    public final long getModulus() {
        return this.modulus;
    }

    public final void setModulus(long l) {
        if (l == LongModConstants.MODULUS[0]) {
            this.inverseModulus = INVERSE_MODULUS[0];
        } else if (l == LongModConstants.MODULUS[1]) {
            this.inverseModulus = INVERSE_MODULUS[1];
        } else if (l == LongModConstants.MODULUS[2]) {
            this.inverseModulus = INVERSE_MODULUS[2];
        } else assert (false);
        this.modulus = l;
    }
}

