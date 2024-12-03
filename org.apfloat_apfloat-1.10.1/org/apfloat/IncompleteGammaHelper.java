/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat;

import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.LongFunction;
import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatHelper;
import org.apfloat.ApfloatMath;
import org.apfloat.Apint;
import org.apfloat.InfiniteExpansionException;
import org.apfloat.OverflowException;

class IncompleteGammaHelper {
    IncompleteGammaHelper() {
    }

    public static Apcomplex gamma(Apcomplex a, Apcomplex z) {
        if (z.real().signum() == 0 && z.imag().signum() == 0) {
            if (a.real().signum() <= 0) {
                throw new ArithmeticException("Upper gamma with first argument real part nonpositive and second argment zero");
            }
            return ApcomplexMath.gamma(a);
        }
        IncompleteGammaHelper.checkPrecision(a, z);
        return IncompleteGammaHelper.upperGamma(a, z).getValue();
    }

    public static Apcomplex gamma(Apcomplex a, Apcomplex z0, Apcomplex z1) {
        if (a.real().signum() == 0 && a.imag().signum() == 0 && z0.real().signum() == 0 && z0.imag().signum() == 0 && z1.real().signum() == 0 && z1.imag().signum() == 0) {
            throw new ArithmeticException("Gamma of zero");
        }
        if (z0.equals(z1)) {
            return Apcomplex.ZEROS[z0.radix()];
        }
        IncompleteGammaHelper.checkPrecision(a, z0, z1);
        if (z0.real().signum() == 0 && z0.imag().signum() == 0) {
            return IncompleteGammaHelper.lowerGamma(a, z1, null).getValue();
        }
        if (z1.real().signum() == 0 && z1.imag().signum() == 0) {
            return IncompleteGammaHelper.lowerGamma(a, z0, null).getValue().negate();
        }
        return IncompleteGammaHelper.upperGamma(a, z0).subtract(IncompleteGammaHelper.upperGamma(a, z1));
    }

    private static void checkPrecision(Apcomplex ... z) {
        long precision = Arrays.stream(z).mapToLong(Apcomplex::precision).min().getAsLong();
        if (precision == Long.MAX_VALUE) {
            throw new InfiniteExpansionException("Cannot calculate incomplete gamma function to infinite precision");
        }
    }

    private static GammaValue upperGamma(Apcomplex a, Apcomplex z) {
        ContinuedFraction[] algorithms = null;
        if (a.isInteger() && a.real().signum() <= 0) {
            if (IncompleteGammaHelper.isCloseToNegativeRealAxis(z)) {
                long n;
                try {
                    n = a.real().longValueExact();
                }
                catch (ArithmeticException ae) {
                    throw new OverflowException(ae.getMessage(), ae);
                }
                return new GammaValue(a, IncompleteGammaHelper.upperGamma(n, z), false);
            }
            algorithms = ContinuedFraction.upperValues();
        }
        if (algorithms == null) {
            if (IncompleteGammaHelper.useLowerGamma(a, z)) {
                return IncompleteGammaHelper.lowerGamma(a, z, ContinuedFraction.lowerValues()).invert();
            }
            algorithms = IncompleteGammaHelper.isMaybeUnstable(a, z) ? ContinuedFraction.bothValues() : ContinuedFraction.upperValues();
        }
        ContinuedFraction fastest = IncompleteGammaHelper.fastestG(a, z, algorithms);
        return IncompleteGammaHelper.gammaG(a, z, fastest, ContinuedFractionType.UPPER);
    }

    private static GammaValue lowerGamma(Apcomplex a, Apcomplex z, ContinuedFraction[] algorithms) {
        if (a.isInteger() && a.real().signum() <= 0) {
            throw new ArithmeticException("Lower gamma with first argument nonpositive integer");
        }
        if (IncompleteGammaHelper.useSum(z)) {
            return new GammaValue(a, IncompleteGammaHelper.sum(a, z), false);
        }
        if (algorithms == null) {
            algorithms = IncompleteGammaHelper.isMaybeUnstable(a, z) ? ContinuedFraction.bothValues() : (IncompleteGammaHelper.useUpperGamma(a, z) ? ContinuedFraction.upperValues() : ContinuedFraction.lowerValues());
        }
        ContinuedFraction fastest = IncompleteGammaHelper.fastestG(a, z, algorithms);
        return IncompleteGammaHelper.gammaG(a, z, fastest, ContinuedFractionType.LOWER);
    }

    private static boolean useSum(Apcomplex z) {
        return z.scale() <= 0L;
    }

    private static boolean useLowerGamma(Apcomplex a, Apcomplex z) {
        return z.scale() < a.scale() || IncompleteGammaHelper.isCloseToNegativeRealAxis(z) || IncompleteGammaHelper.useSum(z);
    }

    private static boolean isCloseToNegativeRealAxis(Apcomplex z) {
        return (z.real().signum() <= 0 || z.real().scale() < 0L) && z.imag().scale() < 0L;
    }

    private static boolean useUpperGamma(Apcomplex a, Apcomplex z) {
        return a.scale() < z.scale();
    }

    private static boolean isMaybeUnstable(Apcomplex a, Apcomplex z) {
        if (a.scale() > 0L && z.scale() > 0L) {
            double ratio = IncompleteGammaHelper.norm(a).divide(IncompleteGammaHelper.norm(z)).doubleValue();
            return 0.01 <= ratio && ratio <= 100.0;
        }
        return false;
    }

    private static Apfloat norm(Apcomplex z) {
        return ApcomplexMath.norm(z.precision(ApfloatHelper.getDoublePrecision(z.radix())));
    }

    private static GammaValue gammaG(Apcomplex a, Apcomplex z, ContinuedFraction algorithm, ContinuedFractionType type) {
        Apcomplex g = IncompleteGammaHelper.g(algorithm.getSequence(), a, z, algorithm.getMinIterations(a, z));
        return new GammaValue(a, g, algorithm.getType() != type);
    }

    private static Sequence upperGammaSequence(Apcomplex a, Apcomplex z) {
        int radix = z.radix();
        Apint one = new Apint(1L, radix);
        Apcomplex za = z.subtract(a);
        Sequence s = new Sequence(n -> {
            if (n == 1L) {
                return one;
            }
            Apint n1 = new Apint(n - 1L, radix);
            return n1.multiply(a.subtract(n1));
        }, n -> new Apint(2L * n - 1L, radix).add(za));
        return s;
    }

    private static Sequence lowerGammaSequence(Apcomplex a, Apcomplex z) {
        int radix = z.radix();
        Apint one = new Apint(1L, radix);
        Sequence s = new Sequence(n -> {
            if (n == 1L) {
                return one;
            }
            if (n % 2L == 0L) {
                return new Apint(1L - n / 2L, radix).subtract(a).multiply(z);
            }
            return new Apint(n / 2L, radix).multiply(z);
        }, n -> new Apint(n - 1L, radix).add(a));
        return s;
    }

    private static Sequence upperGammaSequenceAlternative(Apcomplex a, Apcomplex z) {
        int radix = z.radix();
        Apint one = new Apint(1L, radix);
        Sequence s = new Sequence(n -> {
            if (n == 1L) {
                return one;
            }
            if (n % 2L == 0L) {
                return new Apint(n / 2L, radix).subtract(a);
            }
            return new Apint(n / 2L, radix);
        }, n -> n % 2L == 0L ? one : z);
        return s;
    }

    private static Sequence lowerGammaSequenceAlternative(Apcomplex a, Apcomplex z) {
        int radix = z.radix();
        Apint one = new Apint(1L, radix);
        Apcomplex az = a.add(z);
        Sequence s = new Sequence(n -> {
            if (n == 1L) {
                return one;
            }
            return new Apint(2L - n, radix).subtract(a).multiply(z);
        }, n -> {
            if (n == 1L) {
                return a;
            }
            return az.add(new Apint(n - 1L, radix));
        });
        return s;
    }

    private static ContinuedFraction fastestG(Apcomplex a, Apcomplex z, ContinuedFraction[] algorithms) {
        int radix = z.radix();
        long precision = (long)(50.0 / Math.log10(radix));
        a = a.precision(precision);
        z = z.precision(precision);
        ContinuedFraction fastest = null;
        ContinuedFractionResult fastestResult = null;
        for (ContinuedFraction continuedFraction : algorithms) {
            long fastestPrecision;
            long fastestIterations;
            ContinuedFractionResult result = IncompleteGammaHelper.continuedFraction((Sequence)continuedFraction.sequence.apply(a, z), radix, precision, 0L, 50L);
            if (fastest == null) {
                fastest = continuedFraction;
                fastestResult = result;
                continue;
            }
            long resultIterations = result.getIterations();
            if (resultIterations < (fastestIterations = fastestResult.getIterations())) {
                fastest = continuedFraction;
                fastestResult = result;
                continue;
            }
            if (resultIterations != fastestIterations) continue;
            Apint one = new Apint(1L, radix);
            long resultPrecision = result.getDelta().equalDigits(one);
            if (resultPrecision <= (fastestPrecision = fastestResult.getDelta().equalDigits(one))) continue;
            fastest = continuedFraction;
            fastestResult = result;
        }
        return fastest;
    }

    private static Apcomplex g(BiFunction<Apcomplex, Apcomplex, Sequence> s, Apcomplex a, Apcomplex z, long minIterations) {
        int radix = z.radix();
        long extraPrecision = IncompleteGammaHelper.extraPrecision(radix);
        a = ApfloatHelper.extendPrecision(a, extraPrecision);
        z = ApfloatHelper.extendPrecision(z, extraPrecision);
        long reducePrecision = extraPrecision;
        Apcomplex f = null;
        do {
            try {
                f = IncompleteGammaHelper.continuedFraction(s.apply(a, z), radix, Math.min(a.precision(), z.precision()), minIterations, Long.MAX_VALUE).getResult();
            }
            catch (RetryException re) {
                a = ApfloatHelper.extendPrecision(a, extraPrecision);
                z = ApfloatHelper.extendPrecision(z, extraPrecision);
                reducePrecision += extraPrecision;
                extraPrecision += extraPrecision;
            }
        } while (f == null);
        Apcomplex g = f.multiply(ApcomplexMath.exp(a.multiply(ApcomplexMath.log(z)).subtract(z)));
        return ApfloatHelper.reducePrecision(g, reducePrecision);
    }

    private static long extraPrecision(int radix) {
        return (long)(40.0 / Math.log10(radix));
    }

    private static ContinuedFractionResult continuedFraction(Sequence s, int radix, long workingPrecision, long minIterations, long maxIterations) {
        Apcomplex delta;
        long precision;
        Apcomplex f;
        Apint one = new Apint(1L, radix);
        long n = 0L;
        Apcomplex c = f = IncompleteGammaHelper.tiny(new Apint(0L, radix), workingPrecision);
        Apcomplex d = Apcomplex.ZERO;
        long precisionLoss = IncompleteGammaHelper.extraPrecision(radix) / 4L;
        long targetPrecision = workingPrecision - precisionLoss;
        long maxPrecision = 0L;
        do {
            n = Math.addExact(n, 1L);
            Apcomplex an = s.a(n).precision(workingPrecision);
            Apcomplex bn = s.b(n).precision(workingPrecision);
            d = d.multiply(an).add(bn);
            if ((d = ApfloatHelper.ensurePrecision(d, workingPrecision)).real().signum() == 0 && d.imag().signum() == 0) {
                d = IncompleteGammaHelper.tiny(bn, workingPrecision);
            }
            c = bn.add(an.divide(c));
            if ((c = ApfloatHelper.ensurePrecision(c, workingPrecision)).real().signum() == 0 && c.imag().signum() == 0) {
                c = IncompleteGammaHelper.tiny(bn, workingPrecision);
            }
            d = one.divide(d);
            delta = c.multiply(d);
            f = f.multiply(delta);
            precision = delta.equalDigits(one);
            maxPrecision = Math.max(maxPrecision, precision);
            if (precision >= precisionLoss || maxPrecision < targetPrecision - precisionLoss) continue;
            throw new RetryException();
        } while (n < minIterations || n <= maxIterations && precision < targetPrecision);
        return new ContinuedFractionResult(f, delta, n);
    }

    private static Apcomplex tiny(Apcomplex z, long workingPrecision) {
        if (z.real().signum() == 0 && z.imag().signum() == 0) {
            z = new Apfloat(1L, workingPrecision, z.radix());
        }
        return ApcomplexMath.scale(ApcomplexMath.ulp(z), -workingPrecision).precision(workingPrecision);
    }

    private static Apcomplex upperGamma(long mn, Apcomplex z) {
        Apcomplex result = IncompleteGammaHelper.e1(z);
        assert (mn <= 0L);
        long n = -mn;
        if (n > 0L) {
            Apcomplex s;
            long workingPrecision = ApfloatHelper.extendPrecision(z.precision());
            int radix = z.radix();
            result = result.divide(ApfloatMath.factorial(n, workingPrecision, radix));
            if ((n & 1L) == 1L) {
                result = result.negate();
            }
            z = ApfloatHelper.extendPrecision(z);
            Apcomplex ez = ApcomplexMath.exp(z.negate());
            Apcomplex sum = s = ApcomplexMath.pow(z, mn).divide(new Apint(mn, radix));
            for (long k = 2L; k <= n; ++k) {
                s = s.multiply(z).divide(new Apint(++mn, radix));
                sum = sum.add(s);
            }
            result = result.subtract(ez.multiply(sum));
        }
        return result;
    }

    private static Apcomplex sum(Apcomplex a, Apcomplex z) {
        Apcomplex t;
        a = ApfloatHelper.extendPrecision(a);
        boolean useAlternatingSum = (z = ApfloatHelper.extendPrecision(z)).real().signum() >= 0;
        Apcomplex za = ApcomplexMath.pow(z, a);
        if (!useAlternatingSum) {
            za = za.multiply(ApcomplexMath.exp(z.negate()));
        }
        long targetPrecision = Math.min(a.precision(), z.precision());
        int radix = z.radix();
        Apcomplex sum = Apcomplex.ZERO;
        Apint one = new Apint(1L, radix);
        Apcomplex f = useAlternatingSum ? one.precision(targetPrecision) : a;
        long n = 0L;
        do {
            if (useAlternatingSum) {
                Apint nn = new Apint(n, radix);
                Apcomplex an = a.add(nn);
                if (n > 0L) {
                    za = za.multiply(z);
                    f = f.multiply(nn);
                }
                t = za.divide(f.multiply(an));
                sum = (n & 1L) == 0L ? sum.add(t) : sum.subtract(t);
            } else {
                if (n > 0L) {
                    a = a.add(one);
                    za = za.multiply(z);
                    f = f.multiply(a);
                }
                t = za.divide(f);
                sum = sum.add(t);
            }
            ++n;
        } while (sum.scale() - t.scale() < targetPrecision && !t.equals(Apcomplex.ZERO));
        return ApfloatHelper.reducePrecision(sum);
    }

    private static Apcomplex e1(Apcomplex z) {
        Apint kk;
        Apcomplex t;
        Apcomplex mz;
        Apcomplex s;
        assert (IncompleteGammaHelper.isCloseToNegativeRealAxis(z));
        int radix = z.radix();
        long targetPrecision = z.precision();
        Apcomplex sum = s = (mz = ApfloatHelper.extendPrecision(z).negate());
        long k = 1L;
        do {
            kk = new Apint(++k, radix);
        } while ((sum = sum.add(t = (s = s.multiply(mz).divide(kk)).divide(kk))).scale() - t.scale() < targetPrecision && !t.equals(Apcomplex.ZERO));
        Apcomplex result = ApfloatMath.euler(targetPrecision, radix).negate().subtract(ApcomplexMath.log(z)).subtract(ApfloatHelper.reducePrecision(sum));
        return result;
    }

    private static class Sequence {
        private LongFunction<Apcomplex> a;
        private LongFunction<Apcomplex> b;

        public Sequence(LongFunction<Apcomplex> a, LongFunction<Apcomplex> b) {
            this.a = a;
            this.b = b;
        }

        public Apcomplex a(long n) {
            return this.a.apply(n);
        }

        public Apcomplex b(long n) {
            return this.b.apply(n);
        }
    }

    private static class GammaValue {
        private Apcomplex a;
        private Apcomplex result;
        private boolean inverted;

        public GammaValue(Apcomplex a, Apcomplex result, boolean inverted) {
            this.a = a;
            this.result = result;
            this.inverted = inverted;
        }

        public GammaValue invert() {
            return new GammaValue(this.a, this.result, !this.inverted);
        }

        public Apcomplex subtract(GammaValue that) {
            if (this.inverted == that.inverted) {
                Apcomplex result = this.result.subtract(that.result);
                return this.inverted ? result.negate() : result;
            }
            Apcomplex result = this.result.add(that.result).subtract(ApcomplexMath.gamma(this.a));
            return this.inverted ? result.negate() : result;
        }

        public Apcomplex getValue() {
            return this.inverted ? ApcomplexMath.gamma(this.a).subtract(this.result) : this.result;
        }
    }

    private static enum ContinuedFraction {
        LOWER1(ContinuedFractionType.LOWER, (x$0, x$1) -> IncompleteGammaHelper.access$400(x$0, x$1)){

            @Override
            protected long doGetMinIterations(Apcomplex a, Apcomplex z) {
                return a.real().signum() >= 0 ? 0L : Math.subtractExact(4L, Math.multiplyExact(2L, a.real().longValueExact()));
            }
        }
        ,
        LOWER2(ContinuedFractionType.LOWER, (x$0, x$1) -> IncompleteGammaHelper.access$300(x$0, x$1)){

            @Override
            protected long doGetMinIterations(Apcomplex a, Apcomplex z) {
                return Math.max(a.real().signum() >= 0 ? 0L : Math.subtractExact(3L, a.real().longValueExact()), Math.subtractExact(2L, Math.addExact(a.real().longValueExact(), z.real().longValueExact())));
            }
        }
        ,
        UPPER1(ContinuedFractionType.UPPER, (x$0, x$1) -> IncompleteGammaHelper.access$200(x$0, x$1)){

            @Override
            protected long doGetMinIterations(Apcomplex a, Apcomplex z) {
                return Math.max(a.real().signum() <= 0 ? 0L : Math.addExact(2L, a.real().longValueExact()), Math.addExact(1L, Math.subtractExact(a.real().longValueExact(), z.real().longValueExact()) / 2L));
            }
        }
        ,
        UPPER2(ContinuedFractionType.UPPER, (x$0, x$1) -> IncompleteGammaHelper.access$100(x$0, x$1)){

            @Override
            protected long doGetMinIterations(Apcomplex a, Apcomplex z) {
                return a.real().signum() <= 0 ? 0L : Math.addExact(Math.multiplyExact(2L, a.real().longValueExact()), 2L);
            }
        };

        private ContinuedFractionType type;
        private BiFunction<Apcomplex, Apcomplex, Sequence> sequence;

        private ContinuedFraction(ContinuedFractionType type, BiFunction<Apcomplex, Apcomplex, Sequence> sequence) {
            this.type = type;
            this.sequence = sequence;
        }

        public ContinuedFractionType getType() {
            return this.type;
        }

        public BiFunction<Apcomplex, Apcomplex, Sequence> getSequence() {
            return this.sequence;
        }

        public long getMinIterations(Apcomplex a, Apcomplex z) {
            try {
                return this.doGetMinIterations(a, z);
            }
            catch (ArithmeticException ae) {
                throw new OverflowException(ae.getMessage(), ae);
            }
        }

        protected abstract long doGetMinIterations(Apcomplex var1, Apcomplex var2);

        public static ContinuedFraction[] upperValues() {
            ContinuedFraction[] upperValues = new ContinuedFraction[]{UPPER1};
            return upperValues;
        }

        public static ContinuedFraction[] lowerValues() {
            ContinuedFraction[] lowerValues = new ContinuedFraction[]{LOWER1};
            return lowerValues;
        }

        public static ContinuedFraction[] bothValues() {
            ContinuedFraction[] bothValues = new ContinuedFraction[]{LOWER1, UPPER1};
            return bothValues;
        }
    }

    private static enum ContinuedFractionType {
        LOWER,
        UPPER;

    }

    private static class ContinuedFractionResult {
        private Apcomplex result;
        private Apcomplex delta;
        private long iterations;

        public ContinuedFractionResult(Apcomplex result, Apcomplex delta, long iterations) {
            this.result = result;
            this.delta = delta;
            this.iterations = iterations;
        }

        public Apcomplex getResult() {
            return this.result;
        }

        public Apcomplex getDelta() {
            return this.delta;
        }

        public long getIterations() {
            return this.iterations;
        }
    }

    private static class RetryException
    extends RuntimeException {
        private static final long serialVersionUID = 1L;

        private RetryException() {
        }
    }
}

