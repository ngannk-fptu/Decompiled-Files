/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatContext;
import org.apfloat.ApfloatHelper;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.Apint;
import org.apfloat.ApintMath;
import org.apfloat.ConcurrentSoftHashMap;
import org.apfloat.InfiniteExpansionException;
import org.apfloat.LambertWHelper;
import org.apfloat.LossOfPrecisionException;
import org.apfloat.OverflowException;
import org.apfloat.ParallelHelper;
import org.apfloat.RoundingHelper;
import org.apfloat.ShutdownMap;
import org.apfloat.spi.Util;

public class ApfloatMath {
    private static final Map<Integer, Apfloat> SHUTDOWN_MAP = new ShutdownMap<Integer, Apfloat>();
    private static ConcurrentMap<Integer, Integer> radixPiKeys = new ConcurrentHashMap<Integer, Integer>();
    private static Map<Integer, Apfloat> radixPi = new ConcurrentSoftHashMap<Integer, Apfloat>();
    private static Map<Integer, PiCalculator> radixPiCalculator = new Hashtable<Integer, PiCalculator>();
    private static Map<Integer, Apfloat> radixPiT = new ConcurrentSoftHashMap<Integer, Apfloat>();
    private static Map<Integer, Apfloat> radixPiQ = new ConcurrentSoftHashMap<Integer, Apfloat>();
    private static Map<Integer, Apfloat> radixPiP = new ConcurrentSoftHashMap<Integer, Apfloat>();
    private static Map<Integer, Apfloat> radixPiInverseRoot = new ConcurrentSoftHashMap<Integer, Apfloat>();
    private static Map<Integer, Long> radixPiTerms = new Hashtable<Integer, Long>();
    private static ConcurrentMap<Integer, Integer> radixLogKeys = new ConcurrentHashMap<Integer, Integer>();
    private static Map<Integer, Apfloat> radixLog = new ConcurrentHashMap<Integer, Apfloat>();
    private static Map<Integer, Apfloat> radixLogPi = new ConcurrentHashMap<Integer, Apfloat>();
    private static ConcurrentMap<Integer, Integer> radixGaussianKeys = new ConcurrentHashMap<Integer, Integer>();
    private static Map<Integer, Apfloat> nextGaussian = new ConcurrentHashMap<Integer, Apfloat>();
    private static Map<Integer, Long> nextGaussianPrecision = new ConcurrentHashMap<Integer, Long>();

    private ApfloatMath() {
    }

    public static Apfloat pow(Apfloat x, long n) throws ArithmeticException, ApfloatRuntimeException {
        if (n == 0L) {
            if (x.signum() == 0) {
                throw new ArithmeticException("Zero to power zero");
            }
            return new Apfloat(1L, Long.MAX_VALUE, x.radix());
        }
        if (n < 0L) {
            x = ApfloatMath.inverseRoot(x, 1L);
            n = -n;
        }
        long precision = x.precision();
        x = ApfloatHelper.extendPrecision(x);
        int b2pow = 0;
        while ((n & 1L) == 0L) {
            ++b2pow;
            n >>>= 1;
        }
        Apfloat r = x;
        while ((n >>>= 1) > 0L) {
            x = x.multiply(x);
            if ((n & 1L) == 0L) continue;
            r = r.multiply(x);
        }
        while (b2pow-- > 0) {
            r = r.multiply(r);
        }
        return r.precision(precision);
    }

    public static Apfloat sqrt(Apfloat x) throws ArithmeticException, ApfloatRuntimeException {
        return ApfloatMath.root(x, 2L);
    }

    public static Apfloat cbrt(Apfloat x) throws ApfloatRuntimeException {
        return ApfloatMath.root(x, 3L);
    }

    public static Apfloat root(Apfloat x, long n) throws ArithmeticException, ApfloatRuntimeException {
        if (n == 0L) {
            throw new ArithmeticException("Zeroth root");
        }
        if (x.signum() == 0) {
            return Apfloat.ZEROS[x.radix()];
        }
        if (n == 1L) {
            return x;
        }
        if (n == Long.MIN_VALUE) {
            return ApfloatMath.sqrt(ApfloatMath.inverseRoot(x, n / -2L));
        }
        if (n < 0L) {
            return ApfloatMath.inverseRoot(x, -n);
        }
        if (n == 2L) {
            return x.multiply(ApfloatMath.inverseRoot(x, 2L));
        }
        if (n == 3L) {
            Apfloat y = x.multiply(x);
            return x.multiply(ApfloatMath.inverseRoot(y, 3L));
        }
        Apfloat y = ApfloatMath.inverseRoot(x, n);
        return ApfloatMath.inverseRoot(y, 1L);
    }

    public static Apfloat inverseRoot(Apfloat x, long n) throws ArithmeticException, ApfloatRuntimeException {
        return ApfloatMath.inverseRoot(x, n, x.precision());
    }

    public static Apfloat inverseRoot(Apfloat x, long n, long targetPrecision) throws IllegalArgumentException, ArithmeticException, ApfloatRuntimeException {
        return ApfloatMath.inverseRoot(x, n, targetPrecision, null);
    }

    public static Apfloat inverseRoot(Apfloat x, long n, long targetPrecision, Apfloat initialGuess) throws IllegalArgumentException, ArithmeticException, ApfloatRuntimeException {
        return ApfloatMath.inverseRoot(x, n, targetPrecision, initialGuess, initialGuess == null ? 0L : initialGuess.precision());
    }

    public static Apfloat inverseRoot(Apfloat x, long n, long targetPrecision, Apfloat initialGuess, long initialPrecision) throws IllegalArgumentException, ArithmeticException, ApfloatRuntimeException {
        long precision;
        Apfloat result;
        if (x.signum() == 0) {
            throw new ArithmeticException("Inverse root of zero");
        }
        if (n == 0L) {
            throw new ArithmeticException("Inverse zeroth root");
        }
        if ((n & 1L) == 0L && x.signum() < 0) {
            throw new ArithmeticException("Even root of negative number; result would be complex");
        }
        if (targetPrecision <= 0L) {
            throw new IllegalArgumentException("Target precision " + targetPrecision + " is not positive");
        }
        if (x.equals(Apfloat.ONE)) {
            return x.precision(targetPrecision);
        }
        if (targetPrecision == Long.MAX_VALUE) {
            throw new InfiniteExpansionException("Cannot calculate inverse root to infinite precision");
        }
        if (n == Long.MIN_VALUE) {
            Apfloat y = ApfloatMath.inverseRoot(x, n / -2L);
            return ApfloatMath.inverseRoot(y, 2L);
        }
        if (n < 0L) {
            Apfloat y = ApfloatMath.inverseRoot(x, -n);
            return ApfloatMath.inverseRoot(y, 1L);
        }
        long doublePrecision = ApfloatHelper.getDoublePrecision(x.radix());
        Apfloat one = new Apfloat(1L, Long.MAX_VALUE, x.radix());
        Apfloat divisor = new Apfloat(n, Long.MAX_VALUE, x.radix());
        if (initialGuess == null || initialPrecision < doublePrecision) {
            long scaleQuot = x.scale() / n;
            long scaleRem = x.scale() - scaleQuot * n;
            result = x.precision(doublePrecision);
            result = ApfloatMath.scale(result, -result.scale());
            precision = doublePrecision;
            result = new Apfloat((double)result.signum() * Math.pow(Math.abs(result.doubleValue()), -1.0 / (double)n) * Math.pow(x.radix(), (double)(-scaleRem) / (double)n), precision, x.radix());
            result = ApfloatMath.scale(result, -scaleQuot);
        } else {
            result = initialGuess;
            precision = initialPrecision;
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
        x = ApfloatHelper.extendPrecision(x);
        while (iterations-- > 0) {
            result = result.precision(Math.min(precision *= 2L, targetPrecision));
            Apfloat t = ApfloatMath.pow(result, n);
            t = ApfloatMath.lastIterationExtendPrecision(iterations, precisingIteration, t);
            t = one.subtract(x.multiply(t));
            if (iterations < precisingIteration) {
                t = t.precision(precision / 2L);
            }
            result = ApfloatMath.lastIterationExtendPrecision(iterations, precisingIteration, result);
            result = result.add(result.multiply(t).divide(divisor));
            if (iterations != precisingIteration) continue;
            t = ApfloatMath.pow(result, n);
            t = ApfloatMath.lastIterationExtendPrecision(iterations, -1, t);
            result = ApfloatMath.lastIterationExtendPrecision(iterations, -1, result);
            result = result.add(result.multiply(one.subtract(x.multiply(t))).divide(divisor));
        }
        return result.precision(targetPrecision);
    }

    public static Apint floor(Apfloat x) throws ApfloatRuntimeException {
        return x.floor();
    }

    public static Apint ceil(Apfloat x) throws ApfloatRuntimeException {
        return x.ceil();
    }

    public static Apint truncate(Apfloat x) throws ApfloatRuntimeException {
        return x.truncate();
    }

    public static Apfloat frac(Apfloat x) throws ApfloatRuntimeException {
        return x.frac();
    }

    public static Apfloat round(Apfloat x, long precision, RoundingMode roundingMode) throws IllegalArgumentException, ArithmeticException, ApfloatRuntimeException {
        return RoundingHelper.round(x, precision, roundingMode);
    }

    @Deprecated
    public static Apfloat negate(Apfloat x) throws ApfloatRuntimeException {
        return x.negate();
    }

    public static Apfloat abs(Apfloat x) throws ApfloatRuntimeException {
        if (x.signum() >= 0) {
            return x;
        }
        return x.negate();
    }

    public static Apfloat copySign(Apfloat x, Apfloat y) throws ApfloatRuntimeException {
        if (y.signum() == 0) {
            return y;
        }
        if (x.signum() != y.signum()) {
            return x.negate();
        }
        return x;
    }

    public static Apfloat scale(Apfloat x, long scale) throws ApfloatRuntimeException {
        Apfloat result;
        if (scale == 0L || x.signum() == 0) {
            return x;
        }
        Apfloat radix = new Apfloat(x.radix(), Long.MAX_VALUE, x.radix());
        if ((Math.abs(scale) & 0xC000000000000000L) != 0L) {
            Apfloat scaler1 = ApfloatMath.pow(radix, Math.abs(scale) >>> 1);
            Apfloat scaler2 = (scale & 1L) == 0L ? scaler1 : scaler1.multiply(radix);
            result = scale >= 0L ? x.multiply(scaler1).multiply(scaler2) : x.divide(scaler1).divide(scaler2);
        } else if (x.radix() <= 14) {
            Apfloat scaler = new Apfloat("1e" + scale, Long.MAX_VALUE, x.radix());
            result = x.multiply(scaler);
        } else {
            Apfloat scaler = ApfloatMath.pow(radix, Math.abs(scale));
            result = scale >= 0L ? x.multiply(scaler) : x.divide(scaler);
        }
        return result;
    }

    public static Apfloat[] modf(Apfloat x) throws ApfloatRuntimeException {
        Apfloat[] result;
        result = new Apfloat[]{x.floor(), x.signum() >= 0 ? x.frac() : x.subtract(result[0])};
        return result;
    }

    public static Apfloat fmod(Apfloat x, Apfloat y) throws ApfloatRuntimeException {
        Apfloat b;
        if (y.signum() == 0) {
            return y;
        }
        if (x.signum() == 0) {
            return x;
        }
        Apfloat a = ApfloatMath.abs(x);
        if (a.compareTo(b = ApfloatMath.abs(y)) < 0) {
            return x;
        }
        if (x.precision() <= x.scale() - y.scale()) {
            return Apfloat.ZEROS[x.radix()];
        }
        long precision = x.scale() - y.scale() + 20L;
        Apfloat tx = x.precision(precision);
        Apfloat ty = y.precision(precision);
        Apfloat t = tx.divide(ty).truncate();
        precision = Math.min(Util.ifFinite(y.precision(), y.precision() + x.scale() - y.scale()), x.precision());
        tx = x.precision(precision);
        ty = y.precision(precision);
        a = ApfloatMath.abs(tx).subtract(ApfloatMath.abs(t.multiply(ty)));
        if (a.compareTo(b = ApfloatMath.abs(ty)) >= 0) {
            a = a.subtract(b);
        } else if (a.signum() < 0) {
            a = a.add(b);
        }
        t = ApfloatMath.copySign(a, x);
        return t;
    }

    public static Apfloat multiplyAdd(Apfloat a, Apfloat b, Apfloat c, Apfloat d) throws ApfloatRuntimeException {
        return ApfloatMath.multiplyAddOrSubtract(a, b, c, d, false);
    }

    public static Apfloat multiplySubtract(Apfloat a, Apfloat b, Apfloat c, Apfloat d) throws ApfloatRuntimeException {
        return ApfloatMath.multiplyAddOrSubtract(a, b, c, d, true);
    }

    private static Apfloat multiplyAddOrSubtract(Apfloat a, Apfloat b, Apfloat c, Apfloat d, boolean subtract) throws ApfloatRuntimeException {
        Apfloat cd;
        Apfloat ab;
        long[] precisions = ApfloatHelper.getMatchingPrecisions(a, b, c, d);
        if (precisions[0] == 0L) {
            ab = Apfloat.ZEROS[a.radix()];
        } else {
            a = a.precision(precisions[0]);
            b = b.precision(precisions[0]);
            ab = a.multiply(b);
        }
        if (precisions[1] == 0L) {
            cd = Apfloat.ZEROS[c.radix()];
        } else {
            c = c.precision(precisions[1]);
            d = d.precision(precisions[1]);
            cd = c.multiply(d);
        }
        Apfloat result = subtract ? ab.subtract(cd) : ab.add(cd);
        return result.signum() == 0 ? result : result.precision(precisions[2]);
    }

    public static Apfloat agm(Apfloat a, Apfloat b) throws ApfloatRuntimeException {
        Apfloat t;
        long workingPrecision;
        boolean negate;
        if (a.signum() == 0 || b.signum() == 0) {
            return Apfloat.ZEROS[a.radix()];
        }
        if (ApfloatMath.abs(a).equals(ApfloatMath.abs(b))) {
            return a.signum() == b.signum() ? a.precision(Math.min(a.precision(), b.precision())) : Apfloat.ZEROS[a.radix()];
        }
        if (a.signum() != b.signum()) {
            throw new ArithmeticException("Non-real result");
        }
        boolean bl = negate = a.signum() < 0;
        if (negate) {
            a = a.negate();
            b = b.negate();
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
            b = ApfloatMath.sqrt(a.multiply(b));
            a = t;
            a = ApfloatHelper.ensurePrecision(a, workingPrecision);
            b = ApfloatHelper.ensurePrecision(b, workingPrecision);
            precision = a.equalDigits(b);
        }
        while (precision <= halfWorkingPrecision) {
            t = a.add(b).divide(two);
            b = ApfloatMath.sqrt(a.multiply(b));
            a = t;
            a = ApfloatHelper.ensurePrecision(a, workingPrecision);
            b = ApfloatHelper.ensurePrecision(b, workingPrecision);
            precision *= 2L;
        }
        Apfloat result = a.add(b).divide(two).precision(targetPrecision);
        return negate ? result.negate() : result;
    }

    public static Apfloat pi(long precision) throws IllegalArgumentException, NumberFormatException, ApfloatRuntimeException {
        ApfloatContext ctx = ApfloatContext.getContext();
        int radix = ctx.getDefaultRadix();
        return ApfloatMath.pi(precision, radix);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Apfloat pi(long precision, int radix) throws IllegalArgumentException, NumberFormatException, ApfloatRuntimeException {
        Apfloat pi;
        Integer radixKey;
        if (precision <= 0L) {
            throw new IllegalArgumentException("Precision " + precision + " is not positive");
        }
        if (precision == Long.MAX_VALUE) {
            throw new InfiniteExpansionException("Cannot calculate pi to infinite precision");
        }
        Integer n = radixKey = ApfloatMath.getRadixKey(radixPiKeys, radix);
        synchronized (n) {
            pi = radixPi.get(radixKey);
            pi = pi == null || pi.precision() < precision ? ApfloatMath.calculatePi(precision, radixKey) : pi.precision(precision);
        }
        return pi;
    }

    private static Apfloat calculatePi(long precision, Integer radixKey) throws ApfloatRuntimeException {
        int radix = radixKey;
        PiCalculator piCalculator = radixPiCalculator.get(radixKey);
        if (piCalculator == null) {
            piCalculator = new PiCalculator(radix);
            radixPiCalculator.put(radixKey, piCalculator);
        }
        ApfloatHolder RT = new ApfloatHolder();
        ApfloatHolder RQ = new ApfloatHolder();
        ApfloatHolder RP = new ApfloatHolder();
        long neededTerms = (long)((double)precision * Math.log(radix) / 32.65445004177);
        long workingPrecision = ApfloatHelper.extendPrecision(precision);
        Long terms = radixPiTerms.get(radixKey);
        Apfloat LT = radixPiT.get(radixKey);
        Apfloat LQ = radixPiQ.get(radixKey);
        Apfloat LP = radixPiP.get(radixKey);
        Apfloat inverseRoot = radixPiInverseRoot.get(radixKey);
        if (terms != null && LT != null && LQ != null && LP != null && inverseRoot != null) {
            long currentTerms = terms;
            if (currentTerms != neededTerms + 1L) {
                piCalculator.r(currentTerms, neededTerms + 1L, RT, RQ, RP);
                LT = RQ.getApfloat().multiply(LT).add(LP.multiply(RT.getApfloat()));
                LQ = LQ.multiply(RQ.getApfloat());
                LP = LP.multiply(RP.getApfloat());
            }
            inverseRoot = ApfloatMath.inverseRoot(new Apfloat(1823176476672000L, workingPrecision, radix), 2L, workingPrecision, inverseRoot);
        } else {
            piCalculator.r(0L, neededTerms + 1L, RT, RQ, RP);
            LT = RT.getApfloat();
            LQ = RQ.getApfloat();
            LP = RP.getApfloat();
            inverseRoot = ApfloatMath.inverseRoot(new Apfloat(1823176476672000L, workingPrecision, radix), 2L);
        }
        Apfloat pi = ApfloatMath.inverseRoot(inverseRoot.multiply(LT), 1L).multiply(LQ);
        inverseRoot = inverseRoot.precision(precision);
        pi = pi.precision(precision);
        radixPiT.put(radixKey, LT);
        radixPiQ.put(radixKey, LQ);
        radixPiP.put(radixKey, LP);
        radixPiInverseRoot.put(radixKey, inverseRoot);
        radixPiTerms.put(radixKey, neededTerms + 1L);
        radixPi.put(radixKey, pi);
        return pi;
    }

    public static Apfloat log(Apfloat x) throws ArithmeticException, ApfloatRuntimeException {
        return ApfloatMath.log(x, true);
    }

    public static Apfloat log(Apfloat x, Apfloat b) throws ArithmeticException, ApfloatRuntimeException {
        long targetPrecision = Math.min(x.precision(), b.precision());
        Apfloat one = new Apfloat(1L, Long.MAX_VALUE, x.radix());
        long xPrecision = Util.ifFinite(targetPrecision, targetPrecision + one.equalDigits(x));
        x = x.precision(Math.min(x.precision(), xPrecision));
        long bPrecision = Util.ifFinite(targetPrecision, targetPrecision + one.equalDigits(b));
        b = b.precision(Math.min(b.precision(), bPrecision));
        return ApfloatMath.log(x, false).divide(ApfloatMath.log(b, false));
    }

    private static Apfloat log(Apfloat x, boolean multiplyByPi) throws ArithmeticException, ApfloatRuntimeException {
        Apfloat radixPower;
        if (x.signum() <= 0) {
            throw new ArithmeticException("Logarithm of " + (x.signum() == 0 ? "zero" : "negative number; result would be complex"));
        }
        if (x.equals(Apfloat.ONE)) {
            return Apfloat.ZEROS[x.radix()];
        }
        long targetPrecision = x.precision();
        Apfloat one = new Apfloat(1L, Long.MAX_VALUE, x.radix());
        long finalPrecision = Util.ifFinite(targetPrecision, targetPrecision - one.equalDigits(x));
        if (x.scale() > 1L) {
            double logScale = Math.log((double)x.scale() - 1.0) / Math.log(x.radix());
            logScale += Math.ulp(logScale);
            finalPrecision = Util.ifFinite(finalPrecision, finalPrecision + (long)logScale);
        }
        long originalScale = x.scale();
        x = ApfloatMath.scale(x, -originalScale);
        if (originalScale == 0L) {
            radixPower = Apfloat.ZERO;
        } else {
            Apfloat logRadix = ApfloatHelper.extendPrecision(ApfloatMath.logRadix(targetPrecision, x.radix(), multiplyByPi));
            radixPower = new Apfloat(originalScale, Long.MAX_VALUE, x.radix()).multiply(logRadix);
        }
        return ApfloatHelper.extendPrecision(ApfloatMath.rawLog(x, multiplyByPi)).add(radixPower).precision(finalPrecision);
    }

    private static Apfloat rawLog(Apfloat x, boolean multiplyByPi) throws ApfloatRuntimeException {
        assert (x.signum() > 0);
        long targetPrecision = x.precision();
        if (targetPrecision == Long.MAX_VALUE) {
            throw new InfiniteExpansionException("Cannot calculate logarithm to infinite precision");
        }
        Apfloat one = new Apfloat(1L, Long.MAX_VALUE, x.radix());
        int EXTRA_PRECISION = 25;
        long workingPrecision = ApfloatHelper.extendPrecision(targetPrecision);
        long n = targetPrecision / 2L + 25L;
        x = ApfloatHelper.extendPrecision(x, 25L);
        Apfloat e = one.precision(workingPrecision);
        e = ApfloatMath.scale(e, -n);
        x = ApfloatMath.scale(x, -n);
        Apfloat agme = ApfloatHelper.extendPrecision(ApfloatMath.agm(one, e));
        Apfloat agmex = ApfloatHelper.extendPrecision(ApfloatMath.agm(one, x));
        Apfloat log = agmex.subtract(agme).precision(workingPrecision);
        if (multiplyByPi) {
            Apfloat pi = ApfloatHelper.extendPrecision(ApfloatMath.pi(targetPrecision, x.radix()));
            log = pi.multiply(log);
        }
        log = log.divide(new Apfloat(2L, Long.MAX_VALUE, x.radix()).multiply(agme).multiply(agmex));
        return log.precision(targetPrecision);
    }

    public static Apfloat logRadix(long precision, int radix) throws ApfloatRuntimeException {
        return ApfloatMath.logRadix(precision, radix, true);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Apfloat logRadix(long precision, int radix, boolean multiplyByPi) throws ApfloatRuntimeException {
        Apfloat logRadix;
        Integer radixKey;
        Integer n = radixKey = ApfloatMath.getRadixKey(radixLogKeys, radix);
        synchronized (n) {
            Map<Integer, Apfloat> cache = multiplyByPi ? radixLogPi : radixLog;
            logRadix = cache.get(radixKey);
            if (logRadix == null || logRadix.precision() < precision) {
                if (multiplyByPi) {
                    logRadix = ApfloatHelper.extendPrecision(ApfloatMath.logRadix(precision, radix, false));
                    Apfloat pi = ApfloatHelper.extendPrecision(ApfloatMath.pi(precision, radix));
                    logRadix = logRadix.multiply(pi).precision(precision);
                } else {
                    Apfloat f = new Apfloat("0.1", precision, radix);
                    logRadix = ApfloatMath.rawLog(f, multiplyByPi).negate();
                }
                cache.put(radixKey, logRadix);
            } else {
                logRadix = logRadix.precision(precision);
            }
        }
        return logRadix;
    }

    public static Apfloat exp(Apfloat x) throws ApfloatRuntimeException {
        Apfloat result;
        long precision;
        int radix = x.radix();
        if (x.signum() == 0) {
            return new Apfloat(1L, Long.MAX_VALUE, radix);
        }
        long targetPrecision = x.precision();
        long doublePrecision = ApfloatHelper.getDoublePrecision(radix);
        if (x.scale() < 1L) {
            targetPrecision = Util.ifFinite(targetPrecision, targetPrecision + 1L - x.scale());
        }
        long finalPrecision = targetPrecision;
        if (x.scale() > 1L) {
            if (x.scale() - 1L >= targetPrecision) {
                throw new LossOfPrecisionException("Complete loss of accurate digits");
            }
            finalPrecision = Util.ifFinite(targetPrecision, targetPrecision - (x.scale() - 1L));
        }
        if (targetPrecision == Long.MAX_VALUE) {
            throw new InfiniteExpansionException("Cannot calculate exponent to infinite precision");
        }
        if (x.compareTo(new Apfloat(9.223372036854776E18 * Math.log(radix), doublePrecision, radix)) >= 0) {
            throw new OverflowException("Overflow");
        }
        if (x.compareTo(new Apfloat(-9.223372036854776E18 * Math.log(radix), doublePrecision, radix)) <= 0) {
            return Apfloat.ZEROS[radix];
        }
        if (x.scale() <= -4611686018427387884L) {
            return new Apfloat(1L, Long.MAX_VALUE, radix).add(x).precision(Long.MAX_VALUE);
        }
        if (x.scale() < -doublePrecision / 2L) {
            precision = -2L * x.scale();
            result = new Apfloat(1L, precision, radix).add(x);
        } else {
            long scaledXPrecision = Math.max(0L, x.scale()) + doublePrecision;
            Apfloat logRadix = ApfloatMath.log(new Apfloat((double)radix, scaledXPrecision, radix));
            Apfloat scaledX = x.precision(scaledXPrecision).divide(logRadix);
            Apint integerPart = scaledX.truncate();
            Apfloat fractionalPart = scaledX.frac();
            result = new Apfloat(Math.pow(radix, fractionalPart.doubleValue()), doublePrecision, radix);
            if ((result = ApfloatMath.scale(result, integerPart.longValue())).signum() == 0) {
                return Apfloat.ZEROS[radix];
            }
            precision = doublePrecision;
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
        if (iterations > 0) {
            ApfloatMath.logRadix(targetPrecision, radix);
        }
        x = ApfloatHelper.extendPrecision(x);
        while (iterations-- > 0) {
            result = result.precision(Math.min(precision *= 2L, targetPrecision));
            Apfloat t = ApfloatMath.log(result);
            t = ApfloatMath.lastIterationExtendPrecision(iterations, precisingIteration, t);
            t = x.subtract(t);
            if (iterations < precisingIteration) {
                t = t.precision(precision / 2L);
            }
            result = ApfloatMath.lastIterationExtendPrecision(iterations, precisingIteration, result);
            result = result.add(result.multiply(t));
            if (iterations != precisingIteration) continue;
            t = ApfloatMath.log(result);
            t = ApfloatMath.lastIterationExtendPrecision(iterations, -1, t);
            result = ApfloatMath.lastIterationExtendPrecision(iterations, -1, result);
            result = result.add(result.multiply(x.subtract(t)));
        }
        return result.precision(finalPrecision);
    }

    public static Apfloat pow(Apfloat x, Apfloat y) throws ArithmeticException, ApfloatRuntimeException {
        long targetPrecision = Math.min(x.precision(), y.precision());
        Apfloat result = ApfloatHelper.checkPow(x, y, targetPrecision);
        if (result != null) {
            return result;
        }
        ApfloatMath.logRadix(targetPrecision, x.radix());
        Apfloat one = new Apfloat(1L, Long.MAX_VALUE, x.radix());
        targetPrecision = Util.ifFinite(targetPrecision, targetPrecision + one.equalDigits(x));
        x = x.precision(Math.min(x.precision(), targetPrecision));
        result = ApfloatMath.log(x);
        long intermediatePrecision = Math.min(y.precision(), result.precision());
        result = ApfloatHelper.extendPrecision(result);
        result = ApfloatHelper.extendPrecision(y).multiply(result);
        result = ApfloatMath.exp(result.precision(intermediatePrecision));
        return result;
    }

    public static Apfloat acosh(Apfloat x) throws ArithmeticException, ApfloatRuntimeException {
        Apfloat one = new Apfloat(1L, Long.MAX_VALUE, x.radix());
        return ApfloatMath.log(x.add(ApfloatMath.sqrt(x.multiply(x).subtract(one))));
    }

    public static Apfloat asinh(Apfloat x) throws ApfloatRuntimeException {
        Apfloat one = new Apfloat(1L, Long.MAX_VALUE, x.radix());
        if (x.signum() >= 0) {
            return ApfloatMath.log(ApfloatMath.sqrt(x.multiply(x).add(one)).add(x));
        }
        return ApfloatMath.log(ApfloatMath.sqrt(x.multiply(x).add(one)).subtract(x)).negate();
    }

    public static Apfloat atanh(Apfloat x) throws ArithmeticException, ApfloatRuntimeException {
        Apfloat one = new Apfloat(1L, Long.MAX_VALUE, x.radix());
        Apfloat two = new Apfloat(2L, Long.MAX_VALUE, x.radix());
        return ApfloatMath.log(one.add(x).divide(one.subtract(x))).divide(two);
    }

    public static Apfloat cosh(Apfloat x) throws ApfloatRuntimeException {
        Apfloat y = ApfloatMath.exp(x);
        Apfloat one = new Apfloat(1L, Long.MAX_VALUE, x.radix());
        Apfloat two = new Apfloat(2L, Long.MAX_VALUE, x.radix());
        return y.add(one.divide(y)).divide(two);
    }

    public static Apfloat sinh(Apfloat x) throws ApfloatRuntimeException {
        Apfloat y = ApfloatMath.exp(x);
        Apfloat one = new Apfloat(1L, Long.MAX_VALUE, x.radix());
        Apfloat two = new Apfloat(2L, Long.MAX_VALUE, x.radix());
        return y.subtract(one.divide(y)).divide(two);
    }

    public static Apfloat tanh(Apfloat x) throws ApfloatRuntimeException {
        return ApfloatMath.tanh(x, x.signum() > 0);
    }

    static Apfloat tanhFixedPrecision(Apfloat x) throws ApfloatRuntimeException {
        return ApfloatMath.tanh(x, x.signum() < 0);
    }

    private static Apfloat tanh(Apfloat x, boolean negate) throws ApfloatRuntimeException {
        x = negate ? x.negate() : x;
        Apfloat one = new Apfloat(1L, Long.MAX_VALUE, x.radix());
        Apfloat two = new Apfloat(2L, Long.MAX_VALUE, x.radix());
        Apfloat y = ApfloatMath.exp(two.multiply(x));
        y = y.subtract(one).divide(y.add(one));
        return negate ? y.negate() : y;
    }

    public static Apfloat acos(Apfloat x) throws ArithmeticException, ApfloatRuntimeException {
        Apfloat one = new Apfloat(1L, Long.MAX_VALUE, x.radix());
        Apcomplex i = new Apcomplex(Apfloat.ZERO, one);
        return ApcomplexMath.log(x.add(i.multiply(ApfloatMath.sqrt(one.subtract(x.multiply(x)))))).imag();
    }

    public static Apfloat asin(Apfloat x) throws ArithmeticException, ApfloatRuntimeException {
        Apfloat one = new Apfloat(1L, Long.MAX_VALUE, x.radix());
        Apcomplex i = new Apcomplex(Apfloat.ZERO, one);
        return ApcomplexMath.log(ApfloatMath.sqrt(one.subtract(x.multiply(x))).subtract(i.multiply(x))).imag().negate();
    }

    public static Apfloat atan(Apfloat x) throws ApfloatRuntimeException {
        Apfloat one = new Apfloat(1L, Long.MAX_VALUE, x.radix());
        Apfloat two = new Apfloat(2L, Long.MAX_VALUE, x.radix());
        Apcomplex i = new Apcomplex(Apfloat.ZERO, one);
        return ApcomplexMath.log(i.subtract(x).divide(i.add(x))).imag().divide(two);
    }

    public static Apfloat atan2(Apfloat y, Apfloat x) throws ArithmeticException, ApfloatRuntimeException {
        if (x.signum() == 0) {
            if (y.signum() == 0) {
                throw new ArithmeticException("Angle of (0, 0)");
            }
            Apfloat pi = ApfloatMath.pi(y.precision(), y.radix());
            Apfloat two = new Apfloat(2L, Long.MAX_VALUE, y.radix());
            return new Apfloat(y.signum(), Long.MAX_VALUE, y.radix()).multiply(pi).divide(two);
        }
        if (y.signum() == 0) {
            if (x.signum() > 0) {
                return Apfloat.ZEROS[x.radix()];
            }
            return ApfloatMath.pi(x.precision(), x.radix());
        }
        if (Math.min(y.precision(), x.precision()) == Long.MAX_VALUE) {
            throw new InfiniteExpansionException("Cannot calculate atan2 to infinite precision");
        }
        if (x.signum() > 0 && y.scale() < x.scale()) {
            return ApfloatMath.atan(y.divide(x));
        }
        long maxScale = Math.max(y.scale(), x.scale());
        y = ApfloatMath.scale(y, -maxScale);
        x = ApfloatMath.scale(x, -maxScale);
        return ApcomplexMath.log(new Apcomplex(x, y)).imag();
    }

    public static Apfloat cos(Apfloat x) throws ApfloatRuntimeException {
        return ApcomplexMath.exp(new Apcomplex(Apfloat.ZERO, x)).real();
    }

    public static Apfloat sin(Apfloat x) throws ApfloatRuntimeException {
        return ApcomplexMath.exp(new Apcomplex(Apfloat.ZERO, x)).imag();
    }

    public static Apfloat tan(Apfloat x) throws ArithmeticException, ApfloatRuntimeException {
        Apcomplex w = ApcomplexMath.exp(new Apcomplex(Apfloat.ZERO, x));
        return w.imag().divide(w.real());
    }

    public static Apfloat w(Apfloat x) throws ArithmeticException, ApfloatRuntimeException {
        return LambertWHelper.w(x);
    }

    public static Apfloat toDegrees(Apfloat x) throws ApfloatRuntimeException {
        return x.signum() == 0 ? x : x.multiply(new Apfloat(180L, Long.MAX_VALUE, x.radix())).divide(ApfloatMath.pi(x.precision(), x.radix()));
    }

    public static Apfloat toRadians(Apfloat x) throws ApfloatRuntimeException {
        return x.signum() == 0 ? x : x.divide(new Apfloat(180L, Long.MAX_VALUE, x.radix())).multiply(ApfloatMath.pi(x.precision(), x.radix()));
    }

    public static Apfloat product(Apfloat ... x) throws ApfloatRuntimeException {
        if (x.length == 0) {
            return Apfloat.ONE;
        }
        long maxPrec = Long.MAX_VALUE;
        for (int i = 0; i < x.length; ++i) {
            if (x[i].signum() == 0) {
                return Apfloat.ZEROS[x[i].radix()];
            }
            maxPrec = Math.min(maxPrec, x[i].precision());
        }
        Apfloat[] tmp = new Apfloat[x.length];
        long extraPrec = (long)Math.sqrt(x.length);
        long destPrec = ApfloatHelper.extendPrecision(maxPrec, extraPrec);
        for (int i = 0; i < x.length; ++i) {
            tmp[i] = x[i].precision(destPrec);
        }
        x = tmp;
        PriorityQueue<Apfloat> heap = new PriorityQueue<Apfloat>(x.length, Comparator.comparing(Apfloat::size));
        ParallelHelper.ProductKernel kernel = h -> {
            Apfloat a = (Apfloat)h.remove();
            Apfloat b = (Apfloat)h.remove();
            Apfloat c = a.multiply(b);
            h.add(c);
        };
        ParallelHelper.parallelProduct((Apcomplex[])x, heap, kernel);
        return ((Apfloat)heap.remove()).precision(maxPrec);
    }

    public static Apfloat sum(Apfloat ... x) throws ApfloatRuntimeException {
        List<Apfloat> list;
        if (x.length == 0) {
            return Apfloat.ZERO;
        }
        long maxScale = -9223372036854775807L;
        long maxPrec = Long.MAX_VALUE;
        for (int i = 0; i < x.length; ++i) {
            long oldScale = maxScale;
            long oldPrec = maxPrec;
            long newScale = x[i].scale();
            long newPrec = x[i].precision();
            maxScale = Math.max(oldScale, newScale);
            long oldScaleDiff = maxScale - oldScale < 0L ? Long.MAX_VALUE : maxScale - oldScale;
            long newScaleDiff = maxScale - newScale < 0L ? Long.MAX_VALUE : maxScale - newScale;
            maxPrec = Math.min(Util.ifFinite(oldPrec, oldPrec + oldScaleDiff), Util.ifFinite(newPrec, newPrec + newScaleDiff));
        }
        Apfloat[] tmp = new Apfloat[x.length];
        for (int i = 0; i < x.length; ++i) {
            long scale = x[i].scale();
            long scaleDiff = maxScale - scale < 0L ? Long.MAX_VALUE : maxScale - scale;
            long destPrec = maxPrec - scaleDiff <= 0L ? 0L : Util.ifFinite(maxPrec, maxPrec - scaleDiff);
            tmp[i] = destPrec > 0L ? x[i].precision(destPrec) : Apfloat.ZERO;
        }
        x = tmp;
        Comparator<Apfloat> comparator = Comparator.comparing(Apfloat::scale);
        Arrays.sort(x, comparator);
        if (x.length >= 1000) {
            ApfloatContext ctx = ApfloatContext.getContext();
            long maxSize = (long)((double)ctx.getMemoryThreshold() * 5.0 / Math.log(ctx.getDefaultRadix()));
            ConcurrentLinkedQueue queue = new ConcurrentLinkedQueue();
            list = new ArrayList<Apfloat>();
            for (Apfloat a : x) {
                (a.size() <= maxSize ? queue : list).add(a);
            }
            Runnable runnable = () -> {
                Apfloat a;
                Apfloat s = Apfloat.ZERO;
                while ((a = (Apfloat)queue.poll()) != null) {
                    s = s.add(a);
                }
                queue.add(s);
            };
            ParallelHelper.runParallel(runnable);
            list.addAll(queue);
            Collections.sort(list, comparator);
        } else {
            list = Arrays.asList(x);
        }
        Apfloat s = Apfloat.ZERO;
        for (Apfloat a : list) {
            s = s.add(a);
        }
        return s;
    }

    public static Apfloat euler(long precision) {
        ApfloatContext ctx = ApfloatContext.getContext();
        int radix = ctx.getDefaultRadix();
        return ApfloatMath.euler(precision, radix);
    }

    public static Apfloat euler(long precision, int radix) throws IllegalArgumentException, NumberFormatException, ApfloatRuntimeException {
        Apfloat ov;
        Apfloat ou;
        Apint one;
        long n = (long)(20.0 + 0.25 * Math.log(radix) * (double)precision);
        long workingPrecision = ApfloatHelper.extendPrecision(precision);
        Apfloat a = ApfloatMath.log(new Apfloat(n, workingPrecision, radix)).negate();
        Apfloat b = new Apfloat(1L, workingPrecision, radix);
        Apfloat u = a;
        Apfloat v = b;
        Apint k = one = new Apint(1L, radix);
        Apint n2 = ApintMath.pow(new Apint(n, radix), 2L);
        do {
            ou = u;
            ov = v;
            b = b.multiply(n2).divide(ApfloatMath.pow((Apfloat)k, 2L));
            a = a.multiply(n2).divide(k).add(b).divide(k);
            u = u.add(a);
            v = v.add(b);
            k = k.add(one);
        } while (u.equalDigits(ou) < precision || v.equalDigits(ov) < precision);
        return u.divide(v).precision(precision);
    }

    public static Apfloat gamma(Apfloat x) {
        return ApcomplexMath.gamma(x).real();
    }

    public static Apfloat gamma(Apfloat a, Apfloat x) {
        if (!(x.signum() >= 0 || a.signum() > 0 && a.isInteger())) {
            throw new ArithmeticException("Non-real result");
        }
        return ApcomplexMath.gamma(a, x).real();
    }

    public static Apfloat gamma(Apfloat a, Apfloat x0, Apfloat x1) {
        if (!(x0.signum() >= 0 && x1.signum() >= 0 || a.signum() > 0 && a.isInteger())) {
            throw new ArithmeticException("Non-real result");
        }
        return ApcomplexMath.gamma(a, x0, x1).real();
    }

    public static Apfloat random(long digits) {
        ApfloatContext ctx = ApfloatContext.getContext();
        int radix = ctx.getDefaultRadix();
        return ApfloatMath.random(digits, radix);
    }

    public static Apfloat random(long digits, int radix) {
        Apfloat random = ApintMath.random(digits, radix);
        if (((Apfloat)random).signum() != 0) {
            random = random.precision(((Apfloat)random).scale());
        }
        return ApfloatMath.scale(random, -digits);
    }

    public static Apfloat randomGaussian(long digits) {
        ApfloatContext ctx = ApfloatContext.getContext();
        int radix = ctx.getDefaultRadix();
        return ApfloatMath.randomGaussian(digits, radix);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Apfloat randomGaussian(long digits, int radix) {
        Integer radixKey;
        Integer n = radixKey = ApfloatMath.getRadixKey(radixGaussianKeys, radix);
        synchronized (n) {
            Apfloat v2;
            Apfloat v1;
            Apfloat s;
            Apfloat nextGaussian = ApfloatMath.nextGaussian.remove(radixKey);
            Long nextGaussianPrecision = ApfloatMath.nextGaussianPrecision.remove(radixKey);
            if (nextGaussian != null && nextGaussianPrecision == digits) {
                return nextGaussian;
            }
            Apint one = new Apint(1L, radix);
            Apint two = new Apint(2L, radix);
            while ((s = ApfloatMath.multiplyAdd(v1 = two.multiply(ApfloatMath.random(digits, radix)).subtract(one).precision(digits), v1, v2 = two.multiply(ApfloatMath.random(digits, radix)).subtract(one).precision(digits), v2)).compareTo(one) >= 1 || s.signum() == 0) {
            }
            Apfloat multiplier = ApfloatMath.sqrt(two.negate().multiply(ApfloatMath.log(s)).divide(s));
            nextGaussian = v2.multiply(multiplier);
            ApfloatMath.nextGaussian.put(radixKey, nextGaussian);
            ApfloatMath.nextGaussianPrecision.put(radixKey, digits);
            return v1.multiply(multiplier);
        }
    }

    public static Apfloat max(Apfloat x, Apfloat y) {
        return x.compareTo(y) > 0 ? x : y;
    }

    public static Apfloat min(Apfloat x, Apfloat y) {
        return x.compareTo(y) < 0 ? x : y;
    }

    public static Apfloat nextAfter(Apfloat start, Apfloat direction) {
        return ApfloatMath.nextInDirection(start, direction.compareTo(start));
    }

    public static Apfloat nextUp(Apfloat x) {
        return ApfloatMath.nextInDirection(x, 1);
    }

    public static Apfloat nextDown(Apfloat x) {
        return ApfloatMath.nextInDirection(x, -1);
    }

    public static Apfloat ulp(Apfloat x) {
        return ApfloatMath.ulp(x, 1);
    }

    private static Apfloat nextInDirection(Apfloat x, int direction) {
        return x.add(ApfloatMath.ulp(x, direction));
    }

    private static Apfloat ulp(Apfloat x, int direction) {
        long scale = x.scale() - x.precision();
        if (x.precision() == Long.MAX_VALUE || x.scale() < 0L && scale >= 0L) {
            return Apfloat.ZEROS[x.radix()];
        }
        return ApfloatMath.scale(new Apfloat(direction, 1L, x.radix()), scale);
    }

    private static Apfloat lastIterationExtendPrecision(int iterations, int precisingIteration, Apfloat x) throws ApfloatRuntimeException {
        return iterations == 0 && precisingIteration != 0 ? ApfloatHelper.extendPrecision(x) : x;
    }

    private static Integer getRadixKey(Map<Integer, Integer> radixKeys, int radix) {
        Integer value = new Integer(radix);
        Integer radixKey = radixKeys.putIfAbsent(value, value);
        if (radixKey == null) {
            radixKey = value;
        }
        return radixKey;
    }

    static Apfloat factorial(long n, long precision) throws ArithmeticException, NumberFormatException, ApfloatRuntimeException {
        ApfloatContext ctx = ApfloatContext.getContext();
        int radix = ctx.getDefaultRadix();
        return ApfloatMath.factorial(n, precision, radix);
    }

    static Apfloat factorial(long n, long precision, int radix) throws ArithmeticException, NumberFormatException, ApfloatRuntimeException {
        Apfloat oddProduct;
        if (n < 0L) {
            throw new ArithmeticException("Factorial of negative number");
        }
        if (n < 2L) {
            return new Apfloat(1L, precision, radix);
        }
        long targetPrecision = precision;
        precision = ApfloatHelper.extendPrecision(precision);
        Apfloat factorialProduct = oddProduct = new Apfloat(1L, precision, radix);
        long exponentOfTwo = 0L;
        for (int i = 62 - Long.numberOfLeadingZeros(n); i >= 0; --i) {
            long m = n >>> i;
            long k = m >>> 1;
            exponentOfTwo += k;
            oddProduct = oddProduct.multiply(ApfloatMath.oddProduct(k + 1L, m, precision, radix));
            factorialProduct = factorialProduct.multiply(oddProduct);
        }
        return factorialProduct.multiply(ApfloatMath.pow(new Apfloat(2L, precision, radix), exponentOfTwo)).precision(targetPrecision);
    }

    private static Apfloat oddProduct(long n, long m, long precision, int radix) throws ApfloatRuntimeException {
        if ((n |= 1L) > (m = m - 1L | 1L)) {
            return new Apfloat(1L, precision, radix);
        }
        if (n == m) {
            return new Apfloat(n, precision, radix);
        }
        long k = n + m >>> 1;
        return ApfloatMath.oddProduct(n, k, precision, radix).multiply(ApfloatMath.oddProduct(k + 1L, m, precision, radix));
    }

    static void cleanUp() {
        radixPi = SHUTDOWN_MAP;
        radixPiT = SHUTDOWN_MAP;
        radixPiQ = SHUTDOWN_MAP;
        radixPiP = SHUTDOWN_MAP;
        radixPiInverseRoot = SHUTDOWN_MAP;
        radixLog = SHUTDOWN_MAP;
        radixLogPi = SHUTDOWN_MAP;
        nextGaussian = SHUTDOWN_MAP;
    }

    private static class PiCalculator {
        private final Apfloat A;
        private final Apfloat B;
        private final Apfloat J;
        private final Apfloat ONE;
        private final Apfloat TWO;
        private final Apfloat FIVE;
        private final Apfloat SIX;
        private int radix;

        public PiCalculator(int radix) throws ApfloatRuntimeException {
            this.A = new Apfloat(13591409L, Long.MAX_VALUE, radix);
            this.B = new Apfloat(545140134L, Long.MAX_VALUE, radix);
            this.J = new Apfloat(10939058860032000L, Long.MAX_VALUE, radix);
            this.ONE = new Apfloat(1L, Long.MAX_VALUE, radix);
            this.TWO = new Apfloat(2L, Long.MAX_VALUE, radix);
            this.FIVE = new Apfloat(5L, Long.MAX_VALUE, radix);
            this.SIX = new Apfloat(6L, Long.MAX_VALUE, radix);
            this.radix = radix;
        }

        private Apfloat a(long n) throws ApfloatRuntimeException {
            Apfloat s = new Apfloat(n, Long.MAX_VALUE, this.radix);
            Apfloat v = this.A.add(this.B.multiply(s));
            v = (n & 1L) == 0L ? v : v.negate();
            return v;
        }

        private Apfloat p(long n) throws ApfloatRuntimeException {
            Apfloat v;
            if (n == 0L) {
                v = this.ONE;
            } else {
                Apfloat f = new Apfloat(n, Long.MAX_VALUE, this.radix);
                Apfloat sixf = this.SIX.multiply(f);
                v = sixf.subtract(this.ONE).multiply(this.TWO.multiply(f).subtract(this.ONE)).multiply(sixf.subtract(this.FIVE));
            }
            return v;
        }

        private Apfloat q(long n) throws ApfloatRuntimeException {
            Apfloat v;
            if (n == 0L) {
                v = this.ONE;
            } else {
                Apfloat f = new Apfloat(n, Long.MAX_VALUE, this.radix);
                v = this.J.multiply(f).multiply(f).multiply(f);
            }
            return v;
        }

        public void r(long n1, long n2, ApfloatHolder T, ApfloatHolder Q, ApfloatHolder P) throws ApfloatRuntimeException {
            assert (n1 != n2);
            long length = n2 - n1;
            if (length == 1L) {
                Apfloat p0 = this.p(n1);
                T.setApfloat(this.a(n1).multiply(p0));
                Q.setApfloat(this.q(n1));
                P.setApfloat(p0);
            } else {
                long nMiddle = (n1 + n2) / 2L;
                ApfloatHolder LT = new ApfloatHolder();
                ApfloatHolder LQ = new ApfloatHolder();
                ApfloatHolder LP = new ApfloatHolder();
                this.r(n1, nMiddle, LT, LQ, LP);
                this.r(nMiddle, n2, T, Q, P);
                T.setApfloat(Q.getApfloat().multiply(LT.getApfloat()).add(LP.getApfloat().multiply(T.getApfloat())));
                Q.setApfloat(LQ.getApfloat().multiply(Q.getApfloat()));
                P.setApfloat(LP.getApfloat().multiply(P.getApfloat()));
            }
        }
    }

    private static class ApfloatHolder {
        private Apfloat apfloat;

        public ApfloatHolder() {
            this(null);
        }

        public ApfloatHolder(Apfloat apfloat) {
            this.apfloat = apfloat;
        }

        public Apfloat getApfloat() {
            return this.apfloat;
        }

        public void setApfloat(Apfloat apfloat) {
            this.apfloat = apfloat;
        }
    }
}

