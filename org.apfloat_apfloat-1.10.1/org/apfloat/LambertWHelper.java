/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat;

import org.apfloat.Apcomplex;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatHelper;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.Apint;
import org.apfloat.Aprational;
import org.apfloat.InfiniteExpansionException;
import org.apfloat.spi.RadixConstants;
import org.apfloat.spi.Util;

class LambertWHelper {
    private static final double BRANCH_POINT_BEYOND = -0.367879441171446;
    private Apfloat x;
    private Apcomplex z;
    private int radix;
    private long precision;
    private long targetPrecision;
    private long k;
    private boolean close;
    private Apint minusOne;
    private Apint one;
    private Apint two;
    private Apint three;
    private Apfloat twoPi;
    private Apfloat twoPiK;
    private Apfloat e;
    private Apfloat minusOnePerE;
    private Apcomplex p;

    private LambertWHelper(Apcomplex z, long k) {
        this.targetPrecision = z.precision();
        this.precision = ApfloatHelper.extendPrecision(this.targetPrecision);
        this.radix = z.radix();
        this.z = ApfloatHelper.ensurePrecision(z, this.precision);
        if (z.imag().signum() == 0) {
            this.x = z.real();
        }
        this.k = k;
        this.minusOne = new Apint(-1L, this.radix);
        this.one = new Apint(1L, this.radix);
        this.two = new Apint(2L, this.radix);
        this.three = new Apint(3L, this.radix);
        this.minusOnePerE = new Apfloat(-0.367879441171446, Long.MIN_VALUE, this.radix);
        Apfloat distSquare = new Apfloat(1.0E-8, Long.MIN_VALUE, this.radix);
        boolean bl = this.close = ApcomplexMath.norm(z.subtract(this.minusOnePerE)).compareTo(distSquare) <= 0;
        if (k != 0L) {
            double precisionIncrease = Math.log(Math.abs((double)k) * 2.0 * Math.PI) / Math.log(this.radix);
            this.precision = ApfloatHelper.extendPrecision(this.precision, (long)precisionIncrease);
            this.targetPrecision = ApfloatHelper.extendPrecision(this.targetPrecision, (long)precisionIncrease);
        }
        if ((this.z.real().signum() != 0 || this.z.imag().signum() != 0) && this.precision == Long.MAX_VALUE) {
            throw new InfiniteExpansionException("Cannot calculate W to infinite precision");
        }
    }

    public static Apfloat w(Apfloat x) throws ArithmeticException, ApfloatRuntimeException {
        return new LambertWHelper(x, 0L).real();
    }

    public static Apcomplex w(Apcomplex z) throws ArithmeticException, ApfloatRuntimeException {
        return LambertWHelper.w(z, 0L);
    }

    public static Apcomplex w(Apcomplex z, long k) throws ArithmeticException, ApfloatRuntimeException {
        return new LambertWHelper(z, k).complex();
    }

    public Apfloat real() throws ArithmeticException, ApfloatRuntimeException {
        try {
            return this.doReal();
        }
        catch (ComplexException ce) {
            throw ce.getCause();
        }
    }

    private Apfloat doReal() throws ComplexException, ApfloatRuntimeException {
        boolean done;
        Apfloat w;
        Apfloat logx;
        assert (this.k == 0L || this.x.signum() < 0);
        if (this.x.signum() == 0) {
            return this.x;
        }
        long initialPrecision = RadixConstants.DOUBLE_PRECISION[this.x.radix()];
        Apfloat initialX = this.x.precision(initialPrecision);
        long digits = 0L;
        long oldAccuracy = 0L;
        if (this.x.compareTo(this.minusOnePerE) < 0) {
            throw new ComplexException(new ArithmeticException("Result would be complex"));
        }
        if (this.k == -1L && !this.close) {
            logx = ApfloatMath.log(initialX.negate());
            w = logx.subtract(ApfloatMath.log(logx.negate()));
        } else if (this.k == -1L) {
            w = this.negativeRealSeries();
            digits = oldAccuracy = w.precision();
            w = w.precision(this.shiftLeftPrecision(digits, 2));
        } else if (this.x.scale() > 1L) {
            logx = ApfloatMath.log(initialX);
            w = logx.subtract(ApfloatMath.log(logx));
        } else if (this.x.signum() > 0 && this.x.scale() >= 0L) {
            w = ApfloatMath.log(initialX.add(this.one));
        } else if (!this.close) {
            w = initialX;
        } else {
            w = this.positiveRealSeries();
            digits = oldAccuracy = w.precision();
            w = w.precision(this.shiftLeftPrecision(digits, 2));
        }
        if (!this.close) {
            this.targetPrecision -= (this.x.equalDigits(this.minusOnePerE) + 1L) / 2L;
        }
        this.targetPrecision = Math.max(this.targetPrecision, 1L);
        boolean bl = done = digits >= this.targetPrecision;
        if (!done) {
            ApfloatMath.logRadix(this.targetPrecision, this.radix);
        }
        boolean converges = false;
        for (int i = 0; i < 50 && !done; ++i) {
            double rate;
            Apfloat oldW = w;
            Apfloat z = ApfloatMath.log(this.x.divide(w)).subtract(w);
            Apfloat w1 = this.one.add(w);
            Apfloat q = z.multiply(this.two).divide(this.three).add(w1).multiply(w1).multiply(this.two);
            Apfloat e = z.divide(w1).multiply(q.subtract(z)).divide(q.subtract(this.two.multiply(z)));
            long accuracy = converges ? -e.scale() : digits;
            if ((double)accuracy >= (double)this.targetPrecision / (rate = Math.min(Math.max((double)accuracy / Math.max(1.0, (double)oldAccuracy), 1.0), 4.0))) {
                done = true;
            }
            oldAccuracy = accuracy;
            w = w.multiply(this.one.add(e));
            if (this.k == 0L && w.compareTo(this.minusOne) < 0 || this.k == -1L && w.compareTo(this.minusOne) > 0) {
                throw new ComplexException(new ArithmeticException("Result would be complex"));
            }
            if (!converges) {
                accuracy = digits = w.equalDigits(oldW);
                boolean bl2 = converges = digits >= initialPrecision / 4L;
            }
            if (!converges) continue;
            w = w.precision(this.shiftLeftPrecision(accuracy, 4, 20L));
        }
        return w.precision(this.targetPrecision);
    }

    public Apcomplex complex() throws ArithmeticException, ApfloatRuntimeException {
        boolean done;
        Apcomplex w;
        if (this.z.real().signum() == 0 && this.z.imag().signum() == 0) {
            if (this.k == 0L) {
                return this.z;
            }
            throw new ArithmeticException("W_" + this.k + " of zero");
        }
        long initialPrecision = RadixConstants.DOUBLE_PRECISION[this.radix];
        Apcomplex initialZ = ApfloatHelper.limitPrecision(this.z, initialPrecision);
        long digits = 0L;
        long oldAccuracy = 0L;
        try {
            if (this.k == 0L && this.z.imag().signum() == 0 && this.z.real().compareTo(this.minusOnePerE) > 0) {
                return this.doReal();
            }
            if (this.k == -1L && this.z.imag().signum() == 0 && this.z.real().signum() < 0 && this.z.real().compareTo(this.minusOnePerE) > 0) {
                return this.doReal();
            }
        }
        catch (ComplexException complexException) {
            // empty catch block
        }
        if (this.k == 0L && this.z.scale() < 0L) {
            w = initialZ;
        } else if (this.k == 0L && this.z.scale() < 1L && !this.close) {
            w = ApcomplexMath.log(initialZ.add(this.one));
        } else if (this.k > 1L || this.k < -1L || !this.close) {
            w = this.logApprox(initialZ);
        } else if (this.k == 0L) {
            w = this.positiveComplexSeries();
            digits = oldAccuracy = w.precision();
            w = w.precision(this.shiftLeftPrecision(digits, 2));
        } else if (this.k == -1L && this.z.imag().signum() >= 0 || this.k == 1L && this.z.imag().signum() < 0) {
            w = this.negativeComplexSeries();
            digits = oldAccuracy = w.precision();
            w = w.precision(this.shiftLeftPrecision(digits, 2));
        } else {
            w = this.logApprox(initialZ);
        }
        if (!this.close && (this.k == 0L || this.k == -1L && this.z.imag().signum() >= 0 || this.k == 1L && this.z.imag().signum() < 0)) {
            this.targetPrecision -= (this.z.equalDigits(this.minusOnePerE) + 1L) / 2L;
        }
        this.targetPrecision = Math.max(this.targetPrecision, 1L);
        boolean bl = done = digits >= this.targetPrecision;
        if (!done) {
            ApfloatMath.logRadix(this.targetPrecision, this.radix);
        }
        boolean converges = false;
        for (int j = 0; j < 50 && !done; ++j) {
            double rate;
            Apcomplex oldW = w;
            Apcomplex z = this.fixLogBranch(this.log(this.z.divide(w)), w).subtract(w);
            Apcomplex w1 = this.one.add(w);
            Apcomplex q = z.multiply(this.two).divide(this.three).add(w1).multiply(w1).multiply(this.two);
            Apcomplex e = z.divide(w1).multiply(q.subtract(z)).divide(q.subtract(this.two.multiply(z)));
            long accuracy = converges ? -e.scale() : digits;
            if ((double)accuracy >= (double)this.targetPrecision / (rate = Math.min(Math.max((double)accuracy / Math.max(1.0, (double)oldAccuracy), 1.0), 4.0))) {
                done = true;
            }
            oldAccuracy = accuracy;
            w = w.multiply(this.one.add(e));
            if (!converges) {
                accuracy = digits = w.equalDigits(oldW);
                boolean bl2 = converges = digits >= initialPrecision / 4L;
            }
            if (!converges) continue;
            w = w.precision(this.shiftLeftPrecision(accuracy, 4, 20L));
        }
        return w.precision(this.targetPrecision);
    }

    private Apcomplex log(Apcomplex z) throws ArithmeticException, ApfloatRuntimeException {
        Apcomplex logz = ApcomplexMath.log(z);
        logz = ApfloatHelper.ensurePrecision(logz, z.precision());
        if (this.k != 0L) {
            Apcomplex offset = new Apcomplex(Apfloat.ZERO, this.twoPiK());
            logz = logz.add(offset);
        }
        return logz;
    }

    private Apcomplex logApprox(Apcomplex z) throws ArithmeticException, ApfloatRuntimeException {
        Apcomplex logz = this.log(z);
        Apcomplex w = logz.subtract(ApcomplexMath.log(logz));
        return w;
    }

    private Apcomplex p() throws ApfloatRuntimeException {
        if (this.p == null) {
            Apfloat e = new Apfloat(Math.E, Long.MIN_VALUE, this.radix);
            this.p = ApcomplexMath.sqrt(this.two.multiply(e.multiply(this.z).add(this.one)));
            long precision = e.precision();
            while (this.p.precision() <= -this.p.scale() && precision < this.precision) {
                precision = this.shiftLeftPrecision(precision, 1);
                this.p = ApcomplexMath.sqrt(this.two.multiply(this.e(precision).multiply(this.z).add(this.one)));
            }
            this.targetPrecision = this.p.real().signum() == 0 && this.p.imag().signum() == 0 ? (this.targetPrecision /= 2L) : (this.targetPrecision += this.p.scale() - 1L);
        }
        return this.p;
    }

    private Apfloat positiveRealSeries() throws ComplexException, ApfloatRuntimeException {
        return this.realSeries(this.p());
    }

    private Apfloat negativeRealSeries() throws ComplexException, ApfloatRuntimeException {
        return this.realSeries(this.p().negate());
    }

    private Apcomplex positiveComplexSeries() throws ApfloatRuntimeException {
        return this.complexSeries(this.p());
    }

    private Apcomplex negativeComplexSeries() throws ApfloatRuntimeException {
        return this.complexSeries(this.p().negate());
    }

    private Apfloat realSeries(Apcomplex p) throws ComplexException, ApfloatRuntimeException {
        if (p.imag().signum() != 0) {
            throw new ComplexException(new ArithmeticException("Result would be complex"));
        }
        return this.complexSeries(p).real();
    }

    private Apcomplex complexSeries(Apcomplex p) throws ApfloatRuntimeException {
        Aprational factor2 = new Aprational(this.one, this.three);
        Aprational factor3 = new Aprational(new Apint(11L, this.radix), new Apint(72L, this.radix));
        Apcomplex p2 = p.multiply(p);
        Apcomplex p3 = p2.multiply(p);
        Apcomplex w = this.minusOne.add(p).subtract(factor2.multiply(p2)).add(factor3.multiply(p3));
        long seriesPrecision = Util.ifFinite(-p3.scale(), -p3.scale() - p.scale());
        w = w.precision(Math.min(w.precision(), seriesPrecision));
        return w;
    }

    private long shiftLeftPrecision(long precision, int i) {
        return this.shiftLeftPrecision(precision, i, 0L);
    }

    private long shiftLeftPrecision(long precision, int i, long add) {
        while (--i >= 0) {
            precision = ApfloatHelper.extendPrecision(precision, precision);
        }
        return ApfloatHelper.extendPrecision(precision, add);
    }

    private Apcomplex fixLogBranch(Apcomplex next, Apcomplex previous) throws ApfloatRuntimeException {
        if (this.k != 0L) {
            int comparePrecision = RadixConstants.DOUBLE_PRECISION[next.radix()];
            double diff = next.imag().precision(comparePrecision).subtract(previous.imag()).doubleValue();
            if (diff < -Math.PI) {
                Apcomplex twoPiI = new Apcomplex(Apfloat.ZERO, this.twoPi());
                next = next.add(twoPiI);
            } else if (diff > Math.PI) {
                Apcomplex twoPiI = new Apcomplex(Apfloat.ZERO, this.twoPi());
                next = next.subtract(twoPiI);
            }
        }
        return next;
    }

    private Apfloat twoPi() throws ApfloatRuntimeException {
        if (this.twoPi == null) {
            this.twoPi = this.two.multiply(ApfloatMath.pi(this.precision, this.radix));
        }
        return this.twoPi;
    }

    private Apfloat twoPiK() throws ApfloatRuntimeException {
        if (this.twoPiK == null) {
            this.twoPiK = this.twoPi().multiply(new Apint(this.k, this.radix));
        }
        return this.twoPiK;
    }

    private Apfloat e(long precision) throws ApfloatRuntimeException {
        if (this.e == null || this.e.precision() < precision) {
            this.e = ApfloatMath.exp(new Apfloat(1L, precision, this.radix));
        }
        return this.e;
    }

    private static class ComplexException
    extends Exception {
        private static final long serialVersionUID = 1L;

        public ComplexException(ArithmeticException cause) {
            super(cause);
        }

        @Override
        public ArithmeticException getCause() {
            return (ArithmeticException)super.getCause();
        }
    }
}

