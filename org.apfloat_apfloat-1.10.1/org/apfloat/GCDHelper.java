/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat;

import org.apfloat.ApfloatRuntimeException;
import org.apfloat.Apint;
import org.apfloat.ApintMath;
import org.apfloat.spi.Util;

class GCDHelper {
    private GCDHelper() {
    }

    public static Apint gcd(Apint a, Apint b) throws ApfloatRuntimeException {
        if (a.signum() == 0) {
            return ApintMath.abs(b);
        }
        if (b.signum() == 0) {
            return ApintMath.abs(a);
        }
        if (a.scale() > b.scale()) {
            a = a.mod(b);
        } else if (b.scale() > a.scale()) {
            b = b.mod(a);
        }
        Apint gcd = (double)Math.max(a.scale(), b.scale()) * Math.log(Math.max(a.radix(), b.radix())) < 80000.0 ? GCDHelper.elementaryGcd(a, b) : GCDHelper.recursiveGcd(a, b);
        return gcd;
    }

    private static Apint elementaryGcd(Apint a, Apint b) throws ApfloatRuntimeException {
        while (b.signum() != 0) {
            Apint r = a.mod(b);
            a = b;
            b = r;
        }
        return ApintMath.abs(a);
    }

    private static Apint recursiveGcd(Apint a, Apint b) throws ApfloatRuntimeException {
        if (a.radix() != 2 || b.radix() != 2) {
            return GCDHelper.recursiveGcd(a.toRadix(2), b.toRadix(2)).toRadix(a.radix());
        }
        long zeros = Math.min(GCDHelper.v(a), GCDHelper.v(b));
        a = ApintMath.scale(a, -GCDHelper.v(a));
        b = ApintMath.scale(b, 1L - GCDHelper.v(b));
        long k = Math.max(a.scale(), b.scale());
        HalfGcdType t = GCDHelper.halfBinaryGcd(a, b, k);
        long j = t.j;
        Matrix result = t.r;
        Apint c = ApintMath.scale(result.r11.multiply(a).add(result.r12.multiply(b)), -2L * j);
        Apint d = ApintMath.scale(result.r21.multiply(a).add(result.r22.multiply(b)), -2L * j);
        Apint gcd = d.signum() == 0 ? c : GCDHelper.elementaryGcd(c, d);
        return ApintMath.abs(ApintMath.scale(gcd, zeros));
    }

    private static HalfGcdType halfBinaryGcd(Apint a, Apint b, long k) throws ApfloatRuntimeException {
        assert (GCDHelper.v(a) < GCDHelper.v(b));
        Apint one = new Apint(1L, 2);
        if (GCDHelper.v(b) > k) {
            return new HalfGcdType(0L, new Matrix(one, Apint.ZERO, Apint.ZERO, one));
        }
        long k1 = k >> 1;
        Apint a1 = a.mod(GCDHelper.powerOfTwo(2L * k1 + 1L));
        Apint b1 = b.mod(GCDHelper.powerOfTwo(2L * k1 + 1L));
        HalfGcdType t1 = GCDHelper.halfBinaryGcd(a1, b1, k1);
        long j1 = t1.j;
        Apint ac = ApintMath.scale(t1.r.r11.multiply(a).add(t1.r.r12.multiply(b)), -2L * j1);
        Apint bc = ApintMath.scale(t1.r.r21.multiply(a).add(t1.r.r22.multiply(b)), -2L * j1);
        long j0 = GCDHelper.v(bc);
        if (Util.ifFinite(j0, j0 + j1) > k) {
            return t1;
        }
        Apint[] qr = GCDHelper.binaryDivide(ac, bc);
        Apint q = qr[0];
        Apint r = qr[1];
        long k2 = k - (j0 + j1);
        Apint a2 = ApintMath.scale(bc, -j0).mod(GCDHelper.powerOfTwo(2L * k2 + 1L));
        Apint b2 = ApintMath.scale(r, -j0).mod(GCDHelper.powerOfTwo(2L * k2 + 1L));
        HalfGcdType t2 = GCDHelper.halfBinaryGcd(a2, b2, k2);
        long j2 = t2.j;
        Matrix qm = new Matrix(Apint.ZERO, GCDHelper.powerOfTwo(j0), GCDHelper.powerOfTwo(j0), q);
        Matrix result = t2.r.multiply(qm).multiply(t1.r);
        long j = j1 + j0 + j2;
        return new HalfGcdType(j, result);
    }

    private static Apint[] binaryDivide(Apint a, Apint b) throws ApfloatRuntimeException {
        Apint one;
        assert (a.signum() != 0);
        assert (b.signum() != 0);
        assert (GCDHelper.v(a) < GCDHelper.v(b));
        Apint A = ApintMath.scale(a, -GCDHelper.v(a)).negate();
        Apint B = ApintMath.scale(b, -GCDHelper.v(b));
        Apint q = one = new Apint(1L, 2);
        long n = GCDHelper.v(b) - GCDHelper.v(a) + 1L;
        int maxN = Util.log2up(n);
        for (int i = 1; i <= maxN; ++i) {
            q = q.add(q.multiply(one.subtract(B.multiply(q)))).mod(GCDHelper.powerOfTwo(1L << i));
        }
        q = GCDHelper.cmod(A.multiply(q), GCDHelper.powerOfTwo(n));
        Apint r = q.multiply(b).divide(GCDHelper.powerOfTwo(n - 1L)).add(a);
        return new Apint[]{q, r};
    }

    private static long v(Apint a) throws ApfloatRuntimeException {
        if (a.signum() == 0) {
            return Long.MAX_VALUE;
        }
        return a.scale() - a.size();
    }

    private static Apint powerOfTwo(long n) throws ApfloatRuntimeException {
        assert (n >= 0L);
        return ApintMath.scale(new Apint(1L, 2), n);
    }

    private static Apint cmod(Apint a, Apint m) throws ApfloatRuntimeException {
        Apint halfM;
        a = (a = a.mod(m)).compareTo(halfM = ApintMath.scale(m, -1L)) > 0 ? a.subtract(m) : a;
        a = a.compareTo(halfM.negate()) <= 0 ? a.add(m) : a;
        return a;
    }

    private static class HalfGcdType {
        public final long j;
        public final Matrix r;

        public HalfGcdType(long j, Matrix r) {
            this.j = j;
            this.r = r;
        }
    }

    private static class Matrix {
        public final Apint r11;
        public final Apint r12;
        public final Apint r21;
        public final Apint r22;

        public Matrix(Apint r11, Apint r12, Apint r21, Apint r22) {
            this.r11 = r11;
            this.r12 = r12;
            this.r21 = r21;
            this.r22 = r22;
        }

        public Matrix multiply(Matrix a) throws ApfloatRuntimeException {
            return new Matrix(Matrix.multiplyAdd(this.r11, a.r11, this.r12, a.r21), Matrix.multiplyAdd(this.r11, a.r12, this.r12, a.r22), Matrix.multiplyAdd(this.r21, a.r11, this.r22, a.r21), Matrix.multiplyAdd(this.r21, a.r12, this.r22, a.r22));
        }

        private static Apint multiplyAdd(Apint a, Apint b, Apint c, Apint d) throws ApfloatRuntimeException {
            return a.multiply(b).add(c.multiply(d));
        }
    }
}

