/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat;

import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Comparator;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatHelper;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.Apint;
import org.apfloat.ApintMath;
import org.apfloat.Aprational;
import org.apfloat.RoundingHelper;

public class AprationalMath {
    private AprationalMath() {
    }

    public static Aprational pow(Aprational x, long n) throws ArithmeticException, ApfloatRuntimeException {
        if (n == 0L) {
            if (x.signum() == 0) {
                throw new ArithmeticException("Zero to power zero");
            }
            return new Apint(1L, x.radix());
        }
        if (n < 0L) {
            x = Aprational.ONE.divide(x);
            n = -n;
        }
        int b2pow = 0;
        while ((n & 1L) == 0L) {
            ++b2pow;
            n >>>= 1;
        }
        Aprational r = x;
        while ((n >>>= 1) > 0L) {
            x = x.multiply(x);
            if ((n & 1L) == 0L) continue;
            r = r.multiply(x);
        }
        while (b2pow-- > 0) {
            r = r.multiply(r);
        }
        return r;
    }

    @Deprecated
    public static Aprational negate(Aprational x) throws ApfloatRuntimeException {
        return x.negate();
    }

    public static Aprational abs(Aprational x) throws ApfloatRuntimeException {
        if (x.signum() >= 0) {
            return x;
        }
        return x.negate();
    }

    public static Aprational copySign(Aprational x, Aprational y) throws ApfloatRuntimeException {
        if (y.signum() == 0) {
            return y;
        }
        if (x.signum() != y.signum()) {
            return x.negate();
        }
        return x;
    }

    public static Aprational scale(Aprational x, long scale) throws ApfloatRuntimeException {
        if (scale >= 0L) {
            return new Aprational(ApintMath.scale(x.numerator(), scale), x.denominator());
        }
        if (scale == Long.MIN_VALUE) {
            Apint scaler = ApintMath.pow(new Apint((long)x.radix(), x.radix()), 0x4000000000000000L);
            return new Aprational(x.numerator(), x.denominator().multiply(scaler)).divide(scaler);
        }
        return new Aprational(x.numerator(), ApintMath.scale(x.denominator(), -scale));
    }

    public static Apfloat round(Aprational x, long precision, RoundingMode roundingMode) throws IllegalArgumentException, ArithmeticException, ApfloatRuntimeException {
        return RoundingHelper.round(x, precision, roundingMode);
    }

    public static Aprational product(Aprational ... x) throws ApfloatRuntimeException {
        if (x.length == 0) {
            return Aprational.ONE;
        }
        Apint[] n = new Apint[x.length];
        Apint[] m = new Apint[x.length];
        for (int i = 0; i < x.length; ++i) {
            if (x[i].signum() == 0) {
                return Aprational.ZEROS[x[i].radix()];
            }
            n[i] = x[i].numerator();
            m[i] = x[i].denominator();
        }
        return new Aprational(ApintMath.product(n), ApintMath.product(m));
    }

    public static Aprational sum(Aprational ... x) throws ApfloatRuntimeException {
        if (x.length == 0) {
            return Aprational.ZERO;
        }
        x = (Aprational[])x.clone();
        Arrays.sort(x, Comparator.comparing(ApfloatHelper::size));
        return AprationalMath.recursiveSum(x, 0, x.length - 1);
    }

    public static Aprational max(Aprational x, Aprational y) {
        return x.compareTo(y) > 0 ? x : y;
    }

    public static Aprational min(Aprational x, Aprational y) {
        return x.compareTo(y) < 0 ? x : y;
    }

    private static Aprational recursiveSum(Aprational[] x, int n, int m) throws ApfloatRuntimeException {
        if (n == m) {
            return x[n];
        }
        int k = n + m >>> 1;
        return AprationalMath.recursiveSum(x, n, k).add(AprationalMath.recursiveSum(x, k + 1, m));
    }
}

