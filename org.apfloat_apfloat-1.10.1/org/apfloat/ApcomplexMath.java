/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat;

import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatHelper;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.Apint;
import org.apfloat.Aprational;
import org.apfloat.IncompleteGammaHelper;
import org.apfloat.InfiniteExpansionException;
import org.apfloat.LambertWHelper;
import org.apfloat.LossOfPrecisionException;
import org.apfloat.OverflowException;
import org.apfloat.ParallelHelper;
import org.apfloat.spi.Util;

public class ApcomplexMath {
    private ApcomplexMath() {
    }

    @Deprecated
    public static Apcomplex negate(Apcomplex z) throws ApfloatRuntimeException {
        return z.negate();
    }

    public static Apfloat abs(Apcomplex z) throws ApfloatRuntimeException {
        if (z.real().signum() == 0) {
            return ApfloatMath.abs(z.imag());
        }
        if (z.imag().signum() == 0) {
            return ApfloatMath.abs(z.real());
        }
        return ApfloatMath.sqrt(ApcomplexMath.norm(z));
    }

    public static Apfloat norm(Apcomplex z) throws ApfloatRuntimeException {
        return ApfloatMath.multiplyAdd(z.real(), z.real(), z.imag(), z.imag());
    }

    public static Apfloat arg(Apcomplex z) throws ArithmeticException, ApfloatRuntimeException {
        return ApfloatMath.atan2(z.imag(), z.real());
    }

    public static Apcomplex scale(Apcomplex z, long scale) throws ApfloatRuntimeException {
        return new Apcomplex(ApfloatMath.scale(z.real(), scale), ApfloatMath.scale(z.imag(), scale));
    }

    public static Apcomplex pow(Apcomplex z, long n) throws ArithmeticException, ApfloatRuntimeException {
        if (n == 0L) {
            if (z.real().signum() == 0 && z.imag().signum() == 0) {
                throw new ArithmeticException("Zero to power zero");
            }
            return new Apcomplex(new Apfloat(1L, Long.MAX_VALUE, z.radix()));
        }
        if (n < 0L) {
            z = Apcomplex.ONES[z.radix()].divide(z);
            n = -n;
        }
        return ApcomplexMath.powAbs(z, n);
    }

    private static Apcomplex powAbs(Apcomplex z, long n) throws ArithmeticException, ApfloatRuntimeException {
        long precision = z.precision();
        z = ApfloatHelper.extendPrecision(z);
        int b2pow = 0;
        while ((n & 1L) == 0L) {
            ++b2pow;
            n >>>= 1;
        }
        Apcomplex r = z;
        while ((n >>>= 1) > 0L) {
            z = z.multiply(z);
            if ((n & 1L) == 0L) continue;
            r = r.multiply(z);
        }
        while (b2pow-- > 0) {
            r = r.multiply(r);
        }
        return ApfloatHelper.setPrecision(r, precision);
    }

    public static Apcomplex sqrt(Apcomplex z) throws ApfloatRuntimeException {
        return ApcomplexMath.root(z, 2L);
    }

    public static Apcomplex cbrt(Apcomplex z) throws ApfloatRuntimeException {
        return ApcomplexMath.root(z, 3L);
    }

    public static Apcomplex root(Apcomplex z, long n) throws ArithmeticException, ApfloatRuntimeException {
        return ApcomplexMath.root(z, n, 0L);
    }

    public static Apcomplex root(Apcomplex z, long n, long k) throws ArithmeticException, ApfloatRuntimeException {
        if (n == 0L) {
            throw new ArithmeticException("Zeroth root");
        }
        if (z.real().signum() == 0 && z.imag().signum() == 0) {
            if (n < 0L) {
                throw new ArithmeticException("Inverse root of zero");
            }
            return Apcomplex.ZEROS[z.radix()];
        }
        if (n == 1L) {
            return z;
        }
        if (z.imag().signum() == 0 && z.real().signum() > 0 && (k %= n) == 0L) {
            return new Apcomplex(ApfloatMath.root(z.real(), n));
        }
        if (n < 0L) {
            return ApcomplexMath.inverseRootAbs(z, -n, k);
        }
        if (n == 2L) {
            return z.multiply(ApcomplexMath.inverseRootAbs(z, 2L, k));
        }
        if (n == 3L) {
            if (z.real().signum() < 0) {
                k = z.imag().signum() == 0 ? 1L - k : k - 1L;
                k %= n;
            } else {
                k = -k;
            }
            Apcomplex w = z.multiply(z);
            return z.multiply(ApcomplexMath.inverseRootAbs(w, 3L, k));
        }
        return ApcomplexMath.inverseRootAbs(ApcomplexMath.inverseRootAbs(z, n, k), 1L, 0L);
    }

    public static Apcomplex inverseRoot(Apcomplex z, long n) throws ArithmeticException, ApfloatRuntimeException {
        return ApcomplexMath.inverseRoot(z, n, 0L);
    }

    public static Apcomplex inverseRoot(Apcomplex z, long n, long k) throws ArithmeticException, ApfloatRuntimeException {
        if (z.real().signum() == 0 && z.imag().signum() == 0) {
            throw new ArithmeticException("Inverse root of zero");
        }
        if (n == 0L) {
            throw new ArithmeticException("Inverse zeroth root");
        }
        if (z.imag().signum() == 0 && z.real().signum() > 0 && (k %= n) == 0L) {
            return new Apcomplex(ApfloatMath.inverseRoot(z.real(), n));
        }
        if (n < 0L) {
            return ApcomplexMath.inverseRootAbs(ApcomplexMath.inverseRootAbs(z, -n, k), 1L, 0L);
        }
        return ApcomplexMath.inverseRootAbs(z, n, k);
    }

    private static Apcomplex inverseRootAbs(Apcomplex z, long n, long k) throws ArithmeticException, ApfloatRuntimeException {
        Apcomplex result;
        double angle;
        double doubleImag;
        double doubleReal;
        double magnitude;
        Apcomplex tweak;
        Apfloat tmpImag;
        Apfloat tmpReal;
        long doublePrecision;
        if (z.equals(Apcomplex.ONE) && k == 0L) {
            return z;
        }
        if (n == 2L && z.imag().signum() == 0 && z.real().signum() < 0) {
            Apfloat y = ApfloatMath.inverseRoot(z.real().negate(), n);
            return new Apcomplex(Apfloat.ZEROS[z.radix()], k == 0L ? y.negate() : y);
        }
        long targetPrecision = z.precision();
        if (targetPrecision == Long.MAX_VALUE) {
            throw new InfiniteExpansionException("Cannot calculate inverse root to infinite precision");
        }
        Apfloat one = new Apfloat(1L, Long.MAX_VALUE, z.radix());
        Apfloat divisor = ApfloatMath.abs(new Apfloat(n, Long.MAX_VALUE, z.radix()));
        double doubleN = Math.abs((double)n);
        long realScale = z.real().scale();
        long imagScale = z.imag().scale();
        long scale = Math.max(realScale, imagScale);
        long scaleDiff = scale - Math.min(realScale, imagScale);
        long precision = doublePrecision = (long)ApfloatHelper.getDoublePrecision(z.radix());
        long scaleQuot = scale / n;
        long scaleRem = scale - scaleQuot * n;
        double scaleRemFactor = Math.pow(z.radix(), (double)(-scaleRem) / doubleN);
        if (z.imag().signum() == 0 || (scaleDiff > doublePrecision / 2L || scaleDiff < 0L) && realScale > imagScale) {
            double d;
            tmpReal = z.real().precision(doublePrecision);
            tmpImag = z.imag().precision(doublePrecision);
            tweak = new Apcomplex(Apfloat.ZERO, tmpImag.divide(divisor.multiply(tmpReal)));
            tmpReal = ApfloatMath.scale(tmpReal, -tmpReal.scale());
            magnitude = tmpReal.doubleValue();
            if (d >= 0.0) {
                doubleReal = Math.pow(magnitude, -1.0 / doubleN) * scaleRemFactor;
                doubleImag = 0.0;
            } else {
                magnitude = Math.pow(-magnitude, -1.0 / doubleN) * scaleRemFactor;
                angle = (tmpImag.signum() >= 0 ? -Math.PI : Math.PI) / doubleN;
                doubleReal = magnitude * Math.cos(angle);
                doubleImag = magnitude * Math.sin(angle);
            }
            tmpReal = ApfloatMath.scale(new Apfloat(doubleReal, doublePrecision, z.radix()), -scaleQuot);
            tmpImag = ApfloatMath.scale(new Apfloat(doubleImag, doublePrecision, z.radix()), -scaleQuot);
            result = new Apcomplex(tmpReal, tmpImag);
            result = result.subtract(result.multiply(tweak));
        } else if (z.real().signum() == 0 || (scaleDiff > doublePrecision / 2L || scaleDiff < 0L) && imagScale > realScale) {
            double d;
            tmpReal = z.real().precision(doublePrecision);
            tmpImag = z.imag().precision(doublePrecision);
            tweak = new Apcomplex(Apfloat.ZERO, tmpReal.divide(divisor.multiply(tmpImag)));
            tmpImag = ApfloatMath.scale(tmpImag, -tmpImag.scale());
            magnitude = tmpImag.doubleValue();
            if (d >= 0.0) {
                magnitude = Math.pow(magnitude, -1.0 / doubleN) * scaleRemFactor;
                angle = -Math.PI / (2.0 * doubleN);
            } else {
                magnitude = Math.pow(-magnitude, -1.0 / doubleN) * scaleRemFactor;
                angle = Math.PI / (2.0 * doubleN);
            }
            doubleReal = magnitude * Math.cos(angle);
            doubleImag = magnitude * Math.sin(angle);
            tmpReal = ApfloatMath.scale(new Apfloat(doubleReal, doublePrecision, z.radix()), -scaleQuot);
            tmpImag = ApfloatMath.scale(new Apfloat(doubleImag, doublePrecision, z.radix()), -scaleQuot);
            result = new Apcomplex(tmpReal, tmpImag);
            result = result.add(result.multiply(tweak));
        } else {
            tmpReal = z.real().precision(doublePrecision);
            tmpImag = z.imag().precision(doublePrecision);
            tmpReal = ApfloatMath.scale(tmpReal, -scale);
            tmpImag = ApfloatMath.scale(tmpImag, -scale);
            doubleReal = tmpReal.doubleValue();
            doubleImag = tmpImag.doubleValue();
            magnitude = Math.pow(doubleReal * doubleReal + doubleImag * doubleImag, -1.0 / (2.0 * doubleN)) * scaleRemFactor;
            angle = -Math.atan2(doubleImag, doubleReal) / doubleN;
            doubleReal = magnitude * Math.cos(angle);
            doubleImag = magnitude * Math.sin(angle);
            tmpReal = ApfloatMath.scale(new Apfloat(doubleReal, doublePrecision, z.radix()), -scaleQuot);
            tmpImag = ApfloatMath.scale(new Apfloat(doubleImag, doublePrecision, z.radix()), -scaleQuot);
            result = new Apcomplex(tmpReal, tmpImag);
        }
        if (k != 0L) {
            Apcomplex branch;
            long l = k = k < 0L ? k + n : k;
            if (n % 4L == 0L && n >>> 2 == k) {
                branch = new Apcomplex(Apfloat.ZERO, one);
            } else if (n % 4L == 0L && (n >>> 2) * 3L == k) {
                branch = new Apcomplex(Apfloat.ZERO, one.negate());
            } else if (n % 2L == 0L && n >>> 1 == k) {
                branch = one.negate();
            } else {
                angle = Math.PI * 2 * (double)k / doubleN;
                doubleReal = Math.cos(angle);
                doubleImag = Math.sin(angle);
                Apfloat tmpReal2 = new Apfloat(doubleReal, doublePrecision, z.radix());
                Apfloat tmpImag2 = new Apfloat(doubleImag, doublePrecision, z.radix());
                branch = new Apcomplex(tmpReal2, tmpImag2);
            }
            result = result.multiply(z.imag().signum() >= 0 ? branch.conj() : branch);
        }
        int iterations = 0;
        for (long maxPrec = precision; maxPrec < targetPrecision; maxPrec <<= 1) {
            ++iterations;
        }
        int precisingIteration = iterations;
        long minPrec = precision;
        while (precisingIteration > 0 && minPrec - 20L << precisingIteration < targetPrecision) {
            --precisingIteration;
            minPrec <<= 1;
        }
        z = ApfloatHelper.extendPrecision(z);
        while (iterations-- > 0) {
            result = ApfloatHelper.setPrecision(result, Math.min(precision *= 2L, targetPrecision));
            Apcomplex t = ApcomplexMath.powAbs(result, n);
            t = ApcomplexMath.lastIterationExtendPrecision(iterations, precisingIteration, t);
            t = one.subtract(z.multiply(t));
            if (iterations < precisingIteration) {
                t = new Apcomplex(t.real().precision(precision / 2L), t.imag().precision(precision / 2L));
            }
            result = ApcomplexMath.lastIterationExtendPrecision(iterations, precisingIteration, result);
            result = result.add(result.multiply(t).divide(divisor));
            if (iterations != precisingIteration) continue;
            t = ApcomplexMath.powAbs(result, n);
            t = ApcomplexMath.lastIterationExtendPrecision(iterations, -1, t);
            result = ApcomplexMath.lastIterationExtendPrecision(iterations, -1, result);
            result = result.add(result.multiply(one.subtract(z.multiply(t))).divide(divisor));
        }
        return ApfloatHelper.setPrecision(result, targetPrecision);
    }

    public static Apcomplex[] allRoots(Apcomplex z, int n) throws ArithmeticException, ApfloatRuntimeException {
        if (n == 0) {
            throw new ArithmeticException("Zeroth root");
        }
        if (n == 1) {
            return new Apcomplex[]{z};
        }
        if (n == Integer.MIN_VALUE) {
            throw new ApfloatRuntimeException("Maximum array size exceeded");
        }
        if (z.real().signum() == 0 && z.imag().signum() == 0) {
            if (n < 0) {
                throw new ArithmeticException("Inverse root of zero");
            }
            Object[] allRoots = new Apcomplex[n];
            Arrays.fill(allRoots, Apcomplex.ZEROS[z.radix()]);
            return allRoots;
        }
        boolean inverse = n < 0;
        n = Math.abs(n);
        long precision = z.precision();
        z = ApfloatHelper.extendPrecision(z);
        Apcomplex w = ApcomplexMath.inverseRootAbs(new Apfloat(1L, precision, z.radix()), n, 1L);
        w = z.imag().signum() >= 0 ^ inverse ? w.conj() : w;
        Apcomplex[] allRoots = new Apcomplex[n];
        Apcomplex root = inverse ? ApcomplexMath.inverseRootAbs(z, n, 0L) : ApcomplexMath.root(z, n);
        allRoots[0] = ApfloatHelper.setPrecision(root, precision);
        for (int i = 1; i < n; ++i) {
            root = root.multiply(w);
            allRoots[i] = ApfloatHelper.setPrecision(root, precision);
        }
        return allRoots;
    }

    public static Apcomplex agm(Apcomplex a, Apcomplex b) throws ApfloatRuntimeException {
        Apcomplex t;
        long workingPrecision;
        if (a.real().signum() == 0 && a.imag().signum() == 0 || b.real().signum() == 0 && b.imag().signum() == 0) {
            return Apcomplex.ZEROS[a.radix()];
        }
        if (a.real().signum() == b.real().signum() && a.imag().signum() == 0 && b.imag().signum() == 0) {
            return ApfloatMath.agm(a.real(), b.real());
        }
        if (a.equals(b)) {
            return a.precision(Math.min(a.precision(), b.precision()));
        }
        if (a.equals(b.negate())) {
            return Apcomplex.ZEROS[a.radix()];
        }
        long targetPrecision = workingPrecision = Math.min(a.precision(), b.precision());
        if (workingPrecision == Long.MAX_VALUE) {
            throw new InfiniteExpansionException("Cannot calculate agm to infinite precision");
        }
        workingPrecision = ApfloatHelper.extendPrecision(workingPrecision);
        a = ApfloatHelper.ensurePrecision(a, workingPrecision);
        b = ApfloatHelper.ensurePrecision(b, workingPrecision);
        long precision = 0L;
        long halfWorkingPrecision = (workingPrecision + 1L) / 2L;
        long CONVERGING = 1000L;
        Apfloat two = new Apfloat(2L, Long.MAX_VALUE, a.radix());
        while (precision < 1000L && precision < halfWorkingPrecision) {
            t = a.add(b).divide(two);
            b = ApcomplexMath.rightSqrt(a.multiply(b), t);
            a = t;
            a = ApfloatHelper.ensurePrecision(a, workingPrecision);
            b = ApfloatHelper.ensurePrecision(b, workingPrecision);
            precision = a.equalDigits(b);
        }
        while (precision <= halfWorkingPrecision) {
            t = a.add(b).divide(two);
            b = ApcomplexMath.rightSqrt(a.multiply(b), t);
            a = t;
            a = ApfloatHelper.ensurePrecision(a, workingPrecision);
            b = ApfloatHelper.ensurePrecision(b, workingPrecision);
            precision *= 2L;
        }
        return ApfloatHelper.setPrecision(a.add(b).divide(two), targetPrecision);
    }

    private static Apcomplex rightSqrt(Apcomplex z, Apcomplex reference) {
        Apcomplex result = ApcomplexMath.sqrt(z);
        int doublePrecision = ApfloatHelper.getDoublePrecision(z.radix());
        Apcomplex approxResult = result.precision(doublePrecision);
        Apcomplex approxReference = reference.precision(doublePrecision);
        int comparison = ApcomplexMath.norm(approxReference.subtract(approxResult)).compareTo(ApcomplexMath.norm(approxReference.add(approxResult)));
        if (comparison == 0) {
            comparison = ApcomplexMath.norm(reference.subtract(result)).compareTo(ApcomplexMath.norm(reference.add(result)));
        }
        if (comparison > 0 || comparison == 0 && result.divide(reference).imag().signum() <= 0) {
            result = result.negate();
        }
        return result;
    }

    public static Apcomplex log(Apcomplex z) throws ArithmeticException, ApfloatRuntimeException {
        Apfloat radixPower;
        Apfloat imagBias;
        if (z.real().signum() >= 0 && z.imag().signum() == 0) {
            return ApfloatMath.log(z.real());
        }
        long targetPrecision = z.precision();
        if (targetPrecision == Long.MAX_VALUE) {
            throw new InfiniteExpansionException("Cannot calculate logarithm to infinite precision");
        }
        Apfloat x = ApcomplexMath.abs(z);
        if (x.scale() > 1L) {
            double logScale = Math.log((double)x.scale() - 1.0) / Math.log(x.radix());
            logScale += Math.ulp(logScale);
            targetPrecision = Util.ifFinite(targetPrecision, targetPrecision + (long)logScale);
        }
        if (z.real().signum() < 0) {
            Apfloat pi = ApfloatHelper.extendPrecision(ApfloatMath.pi(targetPrecision, z.radix()), z.radix() <= 3 ? 1L : 0L);
            imagBias = z.imag().signum() >= 0 ? pi : pi.negate();
            z = z.negate();
        } else {
            imagBias = Apfloat.ZERO;
        }
        Apfloat one = new Apfloat(1L, Long.MAX_VALUE, z.radix());
        long originalScale = z.scale();
        z = ApcomplexMath.scale(z, -originalScale);
        if (originalScale == 0L) {
            radixPower = Apfloat.ZERO;
        } else {
            Apfloat logRadix = ApfloatHelper.extendPrecision(ApfloatMath.logRadix(targetPrecision, z.radix()));
            radixPower = new Apfloat(originalScale, Long.MAX_VALUE, z.radix()).multiply(logRadix);
        }
        Apcomplex result = ApfloatHelper.extendPrecision(ApcomplexMath.rawLog(z)).add(radixPower);
        long finalRealPrecision = Math.max(targetPrecision - one.equalDigits(x), 1L);
        long finalImagPrecision = Math.max(targetPrecision - 1L + result.imag().scale(), 1L);
        return new Apcomplex(result.real().precision(finalRealPrecision), result.imag().precision(finalImagPrecision).add(imagBias));
    }

    public static Apcomplex log(Apcomplex z, Apcomplex w) throws ArithmeticException, ApfloatRuntimeException {
        if (z.real().signum() >= 0 && z.imag().signum() == 0 && w.real().signum() >= 0 && w.imag().signum() == 0) {
            return ApfloatMath.log(z.real(), w.real());
        }
        long targetPrecision = Math.min(z.precision(), w.precision());
        if (z.real().signum() >= 0 && z.imag().signum() == 0) {
            Apfloat x = z.real();
            Apfloat one = new Apfloat(1L, Long.MAX_VALUE, x.radix());
            targetPrecision = Util.ifFinite(targetPrecision, targetPrecision + one.equalDigits(x));
            x = x.precision(Math.min(x.precision(), targetPrecision));
            return ApfloatMath.log(x).divide(ApcomplexMath.log(w));
        }
        if (w.real().signum() >= 0 && w.imag().signum() == 0) {
            Apfloat y = w.real();
            Apfloat one = new Apfloat(1L, Long.MAX_VALUE, y.radix());
            targetPrecision = Util.ifFinite(targetPrecision, targetPrecision + one.equalDigits(y));
            y = y.precision(Math.min(y.precision(), targetPrecision));
            return ApcomplexMath.log(z).divide(ApfloatMath.log(y));
        }
        return ApcomplexMath.log(z).divide(ApcomplexMath.log(w));
    }

    private static Apcomplex rawLog(Apcomplex z) throws ApfloatRuntimeException {
        assert (z.real().signum() != 0 || z.imag().signum() != 0);
        Apfloat one = new Apfloat(1L, Long.MAX_VALUE, z.radix());
        int EXTRA_PRECISION = 25;
        long targetPrecision = z.precision();
        long workingPrecision = ApfloatHelper.extendPrecision(targetPrecision);
        long n = targetPrecision / 2L + 25L;
        z = ApfloatHelper.extendPrecision(z, 25L);
        Apfloat e = one.precision(workingPrecision);
        e = ApfloatMath.scale(e, -n);
        z = ApcomplexMath.scale(z, -n);
        Apfloat agme = ApfloatHelper.extendPrecision(ApfloatMath.agm(one, e));
        Apcomplex agmez = ApfloatHelper.extendPrecision(ApcomplexMath.agm(one, z));
        Apfloat pi = ApfloatHelper.extendPrecision(ApfloatMath.pi(targetPrecision, z.radix()));
        Apcomplex log = pi.multiply(agmez.subtract(agme)).divide(new Apfloat(2L, Long.MAX_VALUE, z.radix()).multiply(agme).multiply(agmez));
        return ApfloatHelper.setPrecision(log, targetPrecision);
    }

    public static Apcomplex exp(Apcomplex z) throws ApfloatRuntimeException {
        Apcomplex resultImag;
        Apfloat resultReal;
        Apfloat zImag;
        long imagPrecision;
        if (z.imag().signum() == 0) {
            return ApfloatMath.exp(z.real());
        }
        int radix = z.radix();
        Apfloat one = new Apfloat(1L, Long.MAX_VALUE, radix);
        long doublePrecision = ApfloatHelper.getDoublePrecision(radix);
        if (z.real().precision() < z.real().scale() - 1L) {
            throw new LossOfPrecisionException("Complete loss of accurate digits in real part");
        }
        if (z.imag().precision() < z.imag().scale()) {
            throw new LossOfPrecisionException("Complete loss of accurate digits in imaginary part");
        }
        long realPrecision = Util.ifFinite(z.real().precision(), z.real().precision() + 1L - z.real().scale());
        long targetPrecision = Math.min(realPrecision, imagPrecision = Util.ifFinite(z.imag().precision(), 1L + z.imag().precision() - z.imag().scale()));
        if (targetPrecision == Long.MAX_VALUE) {
            throw new InfiniteExpansionException("Cannot calculate exponent to infinite precision");
        }
        if (z.real().compareTo(new Apfloat(9.223372036854776E18 * Math.log(radix), doublePrecision, radix)) >= 0) {
            throw new OverflowException("Overflow");
        }
        if (z.real().compareTo(new Apfloat(-9.223372036854776E18 * Math.log(radix), doublePrecision, radix)) <= 0) {
            return Apcomplex.ZEROS[z.radix()];
        }
        boolean negateResult = false;
        if (z.imag().scale() > 0L) {
            long piPrecision = Util.ifFinite(targetPrecision, targetPrecision + z.imag().scale());
            Apfloat pi = ApfloatMath.pi(piPrecision, radix);
            Apfloat twoPi = pi.add(pi);
            Apfloat halfPi = pi.divide(new Apfloat(2L, targetPrecision, radix));
            zImag = ApfloatMath.fmod(z.imag(), twoPi);
            if (zImag.compareTo(pi) > 0) {
                zImag = zImag.subtract(twoPi);
            } else if (zImag.compareTo(pi.negate()) <= 0) {
                zImag = zImag.add(twoPi);
            }
            if (zImag.compareTo(halfPi) > 0) {
                zImag = zImag.subtract(pi);
                negateResult = true;
            } else if (zImag.compareTo(halfPi.negate()) <= 0) {
                zImag = zImag.add(pi);
                negateResult = true;
            }
        } else {
            zImag = z.imag();
        }
        z = new Apcomplex(z.real(), zImag);
        if (z.real().signum() == 0) {
            resultReal = one;
        } else if (z.real().scale() < -doublePrecision / 2L) {
            long precision = Util.ifFinite(-z.real().scale(), -2L * z.real().scale());
            resultReal = one.precision(precision).add(z.real());
        } else {
            long scaledRealPrecision = Math.max(0L, z.real().scale()) + doublePrecision;
            Apfloat logRadix = ApfloatMath.log(new Apfloat((double)radix, scaledRealPrecision, radix));
            Apfloat scaledReal = z.real().precision(scaledRealPrecision).divide(logRadix);
            Apint integerPart = scaledReal.truncate();
            Apfloat fractionalPart = scaledReal.frac();
            resultReal = new Apfloat(Math.pow(radix, fractionalPart.doubleValue()), doublePrecision, radix);
            if ((resultReal = ApfloatMath.scale(resultReal, integerPart.longValue())).signum() == 0) {
                return Apcomplex.ZEROS[z.radix()];
            }
        }
        if (zImag.signum() == 0) {
            resultImag = one;
        } else if (zImag.scale() < -doublePrecision / 2L) {
            long precision = Util.ifFinite(-zImag.scale(), -2L * zImag.scale());
            resultImag = new Apcomplex(one.precision(precision), zImag.precision(-zImag.scale()));
        } else {
            double doubleImag = zImag.doubleValue();
            resultImag = new Apcomplex(new Apfloat(Math.cos(doubleImag), doublePrecision, radix), new Apfloat(Math.sin(doubleImag), doublePrecision, radix));
        }
        Apcomplex result = resultReal.multiply(resultImag);
        long precision = result.precision();
        int iterations = 0;
        for (long maxPrec = precision; maxPrec < targetPrecision; maxPrec <<= 1) {
            ++iterations;
        }
        int precisingIteration = iterations;
        long minPrec = precision;
        while (precisingIteration > 0 && minPrec - 20L << precisingIteration < targetPrecision) {
            --precisingIteration;
            minPrec <<= 1;
        }
        if (iterations > 0) {
            ApfloatMath.logRadix(targetPrecision, radix);
        }
        z = ApfloatHelper.extendPrecision(z);
        while (iterations-- > 0) {
            result = ApfloatHelper.setPrecision(result, Math.min(precision *= 2L, targetPrecision));
            Apcomplex t = ApcomplexMath.log(result);
            t = ApcomplexMath.lastIterationExtendPrecision(iterations, precisingIteration, t);
            t = z.subtract(t);
            if (iterations < precisingIteration) {
                t = new Apcomplex(t.real().precision(precision / 2L), t.imag().precision(precision / 2L));
            }
            result = ApcomplexMath.lastIterationExtendPrecision(iterations, precisingIteration, result);
            result = result.add(result.multiply(t));
            if (iterations != precisingIteration) continue;
            t = ApcomplexMath.log(result);
            t = ApcomplexMath.lastIterationExtendPrecision(iterations, -1, t);
            result = ApcomplexMath.lastIterationExtendPrecision(iterations, -1, result);
            result = result.add(result.multiply(z.subtract(t)));
        }
        return ApfloatHelper.setPrecision(negateResult ? result.negate() : result, targetPrecision);
    }

    public static Apcomplex pow(Apcomplex z, Apcomplex w) throws ApfloatRuntimeException {
        long targetPrecision = Math.min(z.precision(), w.precision());
        Apcomplex result = ApfloatHelper.checkPow(z, w, targetPrecision);
        if (result != null) {
            return result;
        }
        if (z.real().signum() >= 0 && z.imag().signum() == 0) {
            Apfloat x = z.real();
            Apfloat one = new Apfloat(1L, Long.MAX_VALUE, x.radix());
            targetPrecision = Util.ifFinite(targetPrecision, targetPrecision + one.equalDigits(x));
            x = x.precision(Math.min(x.precision(), targetPrecision));
            return ApcomplexMath.exp(w.multiply(ApfloatMath.log(x)));
        }
        return ApcomplexMath.exp(w.multiply(ApcomplexMath.log(z)));
    }

    public static Apcomplex acos(Apcomplex z) throws ApfloatRuntimeException {
        Apfloat one = new Apfloat(1L, Long.MAX_VALUE, z.radix());
        if (z.imag().signum() == 0 && ApfloatMath.abs(z.real()).compareTo(one) <= 0) {
            return ApfloatMath.acos(z.real());
        }
        Apcomplex i = new Apcomplex(Apfloat.ZERO, one);
        Apcomplex w = i.multiply(ApcomplexMath.log(z.add(ApcomplexMath.sqrt(z.multiply(z).subtract(one)))));
        if (z.real().signum() < 0 && z.imag().signum() == 0) {
            return new Apcomplex(w.real().negate(), w.imag());
        }
        if (z.real().signum() * z.imag().signum() > 0 || z.real().signum() == 0) {
            return w.negate();
        }
        return w;
    }

    public static Apcomplex acosh(Apcomplex z) throws ApfloatRuntimeException {
        Apfloat one = new Apfloat(1L, Long.MAX_VALUE, z.radix());
        if (z.real().signum() > 0 || z.real().signum() == 0 && z.imag().signum() >= 0) {
            return ApcomplexMath.log(z.add(ApcomplexMath.sqrt(z.multiply(z).subtract(one))));
        }
        return ApcomplexMath.log(z.subtract(ApcomplexMath.sqrt(z.multiply(z).subtract(one))));
    }

    public static Apcomplex asin(Apcomplex z) throws ApfloatRuntimeException {
        Apfloat one = new Apfloat(1L, Long.MAX_VALUE, z.radix());
        if (z.imag().signum() == 0 && ApfloatMath.abs(z.real()).compareTo(one) <= 0) {
            return ApfloatMath.asin(z.real());
        }
        Apcomplex i = new Apcomplex(Apfloat.ZERO, one);
        if (z.imag().signum() >= 0) {
            return i.multiply(ApcomplexMath.log(ApcomplexMath.sqrt(one.subtract(z.multiply(z))).subtract(i.multiply(z))));
        }
        return i.multiply(ApcomplexMath.log(i.multiply(z).add(ApcomplexMath.sqrt(one.subtract(z.multiply(z)))))).negate();
    }

    public static Apcomplex asinh(Apcomplex z) throws ApfloatRuntimeException {
        Apfloat one = new Apfloat(1L, Long.MAX_VALUE, z.radix());
        if (z.real().signum() >= 0) {
            return ApcomplexMath.log(ApcomplexMath.sqrt(z.multiply(z).add(one)).add(z));
        }
        return ApcomplexMath.log(ApcomplexMath.sqrt(z.multiply(z).add(one)).subtract(z)).negate();
    }

    public static Apcomplex atan(Apcomplex z) throws ArithmeticException, ApfloatRuntimeException {
        if (z.imag().signum() == 0) {
            return ApfloatMath.atan(z.real());
        }
        Apfloat one = new Apfloat(1L, Long.MAX_VALUE, z.radix());
        Apfloat two = new Apfloat(2L, Long.MAX_VALUE, z.radix());
        Apcomplex i = new Apcomplex(Apfloat.ZERO, one);
        Apcomplex w = ApcomplexMath.log(i.add(z).divide(i.subtract(z))).multiply(i).divide(two);
        if (z.real().signum() == 0 && z.imag().signum() > 0) {
            return new Apcomplex(w.real().negate(), w.imag());
        }
        return w;
    }

    public static Apcomplex atanh(Apcomplex z) throws ArithmeticException, ApfloatRuntimeException {
        Apfloat one = new Apfloat(1L, Long.MAX_VALUE, z.radix());
        Apfloat two = new Apfloat(2L, Long.MAX_VALUE, z.radix());
        Apcomplex w = ApcomplexMath.log(one.add(z).divide(one.subtract(z))).divide(two);
        if (z.real().signum() > 0 && z.imag().signum() == 0) {
            return w.conj();
        }
        return w;
    }

    public static Apcomplex cos(Apcomplex z) throws ApfloatRuntimeException {
        if (z.imag().signum() == 0) {
            return ApfloatMath.cos(z.real());
        }
        Apfloat one = new Apfloat(1L, Long.MAX_VALUE, z.radix());
        Apfloat two = new Apfloat(2L, Long.MAX_VALUE, z.radix());
        Apcomplex i = new Apcomplex(Apfloat.ZERO, one);
        Apcomplex w = ApcomplexMath.exp(i.multiply(z));
        return w.add(one.divide(w)).divide(two);
    }

    public static Apcomplex cosh(Apcomplex z) throws ApfloatRuntimeException {
        if (z.imag().signum() == 0) {
            return ApfloatMath.cosh(z.real());
        }
        Apfloat one = new Apfloat(1L, Long.MAX_VALUE, z.radix());
        Apfloat two = new Apfloat(2L, Long.MAX_VALUE, z.radix());
        Apcomplex w = ApcomplexMath.exp(z);
        return w.add(one.divide(w)).divide(two);
    }

    public static Apcomplex sin(Apcomplex z) throws ApfloatRuntimeException {
        if (z.imag().signum() == 0) {
            return ApfloatMath.sin(z.real());
        }
        Apfloat one = new Apfloat(1L, Long.MAX_VALUE, z.radix());
        Apfloat two = new Apfloat(2L, Long.MAX_VALUE, z.radix());
        Apcomplex i = new Apcomplex(Apfloat.ZERO, one);
        Apcomplex w = ApcomplexMath.exp(i.multiply(z));
        return one.divide(w).subtract(w).multiply(i).divide(two);
    }

    public static Apcomplex sinh(Apcomplex z) throws ApfloatRuntimeException {
        if (z.imag().signum() == 0) {
            return ApfloatMath.sinh(z.real());
        }
        Apfloat one = new Apfloat(1L, Long.MAX_VALUE, z.radix());
        Apfloat two = new Apfloat(2L, Long.MAX_VALUE, z.radix());
        Apcomplex w = ApcomplexMath.exp(z);
        return w.subtract(one.divide(w)).divide(two);
    }

    public static Apcomplex tan(Apcomplex z) throws ArithmeticException, ApfloatRuntimeException {
        if (z.imag().signum() == 0) {
            return ApfloatMath.tan(z.real());
        }
        boolean negate = z.imag().signum() > 0;
        z = negate ? z.negate() : z;
        Apfloat one = new Apfloat(1L, Long.MAX_VALUE, z.radix());
        Apfloat two = new Apfloat(2L, Long.MAX_VALUE, z.radix());
        Apcomplex i = new Apcomplex(Apfloat.ZERO, one);
        Apcomplex w = ApcomplexMath.exp(two.multiply(i).multiply(z));
        w = i.multiply(one.subtract(w)).divide(one.add(w));
        return negate ? w.negate() : w;
    }

    public static Apcomplex tanh(Apcomplex z) throws ArithmeticException, ApfloatRuntimeException {
        return ApcomplexMath.tanh(z, z.real().signum() > 0);
    }

    static Apcomplex tanhFixedPrecision(Apcomplex z) throws ArithmeticException, ApfloatRuntimeException {
        return ApcomplexMath.tanh(z, z.real().signum() < 0);
    }

    private static Apcomplex tanh(Apcomplex z, boolean negate) throws ArithmeticException, ApfloatRuntimeException {
        if (z.imag().signum() == 0) {
            return ApfloatMath.tanh(z.real());
        }
        z = negate ? z.negate() : z;
        Apfloat one = new Apfloat(1L, Long.MAX_VALUE, z.radix());
        Apfloat two = new Apfloat(2L, Long.MAX_VALUE, z.radix());
        Apcomplex w = ApcomplexMath.exp(two.multiply(z));
        w = w.subtract(one).divide(w.add(one));
        return negate ? w.negate() : w;
    }

    public static Apcomplex w(Apcomplex z) throws ApfloatRuntimeException {
        return LambertWHelper.w(z);
    }

    public static Apcomplex w(Apcomplex z, long k) throws ArithmeticException, ApfloatRuntimeException {
        return LambertWHelper.w(z, k);
    }

    public static Apcomplex product(Apcomplex ... z) throws ApfloatRuntimeException {
        if (z.length == 0) {
            return Apcomplex.ONE;
        }
        long maxPrec = Long.MAX_VALUE;
        for (int i = 0; i < z.length; ++i) {
            if (z[i].real().signum() == 0 && z[i].imag().signum() == 0) {
                return Apcomplex.ZEROS[z[i].radix()];
            }
            maxPrec = Math.min(maxPrec, z[i].precision());
        }
        Apcomplex[] tmp = new Apcomplex[z.length];
        long extraPrec = (long)Math.sqrt(z.length);
        long destPrec = ApfloatHelper.extendPrecision(maxPrec, extraPrec);
        for (int i = 0; i < z.length; ++i) {
            tmp[i] = z[i].precision(destPrec);
        }
        z = tmp;
        PriorityQueue<Apcomplex> heap = new PriorityQueue<Apcomplex>(z.length, Comparator.comparing(Apcomplex::size));
        ParallelHelper.ProductKernel kernel = h -> {
            Apcomplex a = (Apcomplex)h.remove();
            Apcomplex b = (Apcomplex)h.remove();
            Apcomplex c = a.multiply(b);
            h.add(c);
        };
        ParallelHelper.parallelProduct((Apcomplex[])z, heap, kernel);
        return ApfloatHelper.setPrecision((Apcomplex)heap.remove(), maxPrec);
    }

    public static Apcomplex sum(Apcomplex ... z) throws ApfloatRuntimeException {
        if (z.length == 0) {
            return Apcomplex.ZERO;
        }
        Apfloat[] x = new Apfloat[z.length];
        Apfloat[] y = new Apfloat[z.length];
        for (int i = 0; i < z.length; ++i) {
            x[i] = z[i].real();
            y[i] = z[i].imag();
        }
        return new Apcomplex(ApfloatMath.sum(x), ApfloatMath.sum(y));
    }

    public static Apcomplex gamma(Apcomplex z) throws ArithmeticException, ApfloatRuntimeException {
        Apfloat c0;
        if (z.equals(Apfloat.ONE)) {
            return z;
        }
        long precision = z.precision();
        int radix = z.radix();
        if (z.imag().signum() == 0) {
            if (z.real().signum() == 0) {
                throw new ArithmeticException("Gamma of zero");
            }
            if (z.real().isInteger()) {
                long n;
                if (z.real().signum() < 0) {
                    throw new ArithmeticException("Gamma of negative integer");
                }
                try {
                    n = z.real().longValueExact();
                }
                catch (ArithmeticException ae) {
                    throw new OverflowException("Overflow");
                }
                return ApfloatMath.factorial(n - 1L, precision, radix);
            }
        }
        if (precision == Long.MAX_VALUE) {
            throw new InfiniteExpansionException("Cannot calculate gamma function to infinite precision");
        }
        if (z.real().signum() < 0) {
            z = z.negate();
            Apfloat pi = ApfloatMath.pi(precision, radix);
            return pi.negate().divide(z.multiply(ApcomplexMath.sin(pi.multiply(z))).multiply(ApcomplexMath.gamma(z)));
        }
        Apint one = new Apint(1L, radix);
        long a1 = (long)((double)precision / Math.log(Math.PI * 2) * Math.log(radix));
        long workingPrecision = ApfloatHelper.extendPrecision(precision, (long)((double)precision * 0.5) + 20L);
        z = z.precision(workingPrecision).subtract(one);
        Apint a = new Apint(a1 + 1L, radix);
        Apint two = new Apint(2L, radix);
        Apcomplex sum = c0 = ApfloatMath.sqrt(ApfloatMath.pi(workingPrecision, radix).multiply(two));
        Apfloat e = ApfloatMath.exp(one.precision(workingPrecision));
        Apfloat divisor = ApfloatMath.exp(new Apfloat(-a1, workingPrecision, radix));
        for (long k = 1L; k <= a1; ++k) {
            Apint kk = new Apint(k, radix);
            Apfloat ak = a.subtract(kk).precision(workingPrecision);
            Apfloat ck = ApfloatMath.inverseRoot(ak, 2L).multiply(ApfloatMath.pow(ak, k)).divide(divisor);
            sum = sum.add(ck.divide(z.add(kk)));
            if (k >= a1) continue;
            divisor = divisor.multiply(e).multiply(kk).negate();
        }
        Aprational half = new Aprational(one, two);
        Apcomplex result = ApcomplexMath.pow(z.add(a), z.add(half)).multiply(ApcomplexMath.exp(z.negate().subtract(a))).multiply(sum);
        double normalizedScale = (double)result.scale() * Math.log(radix);
        if (normalizedScale > 0.0 && z.real().scale() > 0L ? (precision -= (long)(1.01 * Math.log(normalizedScale) / Math.log(radix))) <= 0L : normalizedScale < 0.0 && (precision -= (long)(1.148 * Math.log(-normalizedScale) / Math.log(radix))) <= 0L) {
            throw new LossOfPrecisionException("Complete loss of accurate digits");
        }
        return result.precision(precision);
    }

    public static Apcomplex gamma(Apcomplex a, Apcomplex z) throws ArithmeticException, ApfloatRuntimeException {
        return IncompleteGammaHelper.gamma(a, z);
    }

    public static Apcomplex gamma(Apcomplex a, Apcomplex z0, Apcomplex z1) throws ArithmeticException, ApfloatRuntimeException {
        return IncompleteGammaHelper.gamma(a, z0, z1);
    }

    public static Apfloat ulp(Apcomplex z) {
        return ApfloatMath.max(ApfloatMath.ulp(z.real()), ApfloatMath.ulp(z.imag()));
    }

    private static Apcomplex lastIterationExtendPrecision(int iterations, int precisingIteration, Apcomplex z) {
        return iterations == 0 && precisingIteration != 0 ? ApfloatHelper.extendPrecision(z) : z;
    }
}

