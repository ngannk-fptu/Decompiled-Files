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
import org.apfloat.spi.Util;

public class FixedPrecisionApcomplexHelper {
    private long precision;

    public FixedPrecisionApcomplexHelper(long precision) throws IllegalArgumentException {
        ApfloatHelper.checkPrecision(precision);
        this.precision = precision;
    }

    public Apcomplex valueOf(Apcomplex z) throws ApfloatRuntimeException {
        return z.precision(this.precision());
    }

    public Apcomplex negate(Apcomplex z) throws ApfloatRuntimeException {
        return this.valueOf(z).negate();
    }

    public Apcomplex conj(Apcomplex z) throws ApfloatRuntimeException {
        return this.valueOf(z).conj();
    }

    public Apcomplex add(Apcomplex z, Apcomplex w) throws ApfloatRuntimeException {
        return this.valueOf(this.setPrecision(z).add(this.setPrecision(w)));
    }

    public Apcomplex subtract(Apcomplex z, Apcomplex w) throws ApfloatRuntimeException {
        return this.valueOf(this.setPrecision(z).subtract(this.setPrecision(w)));
    }

    public Apcomplex multiply(Apcomplex z, Apcomplex w) throws ApfloatRuntimeException {
        return this.valueOf(this.setPrecision(z).multiply(this.setPrecision(w)));
    }

    public Apcomplex divide(Apcomplex z, Apcomplex w) throws ArithmeticException, ApfloatRuntimeException {
        return this.valueOf(this.setPrecision(z).divide(this.setPrecision(w)));
    }

    public Apcomplex pow(Apcomplex z, Apcomplex w) throws ArithmeticException, ApfloatRuntimeException {
        Apcomplex result = ApfloatHelper.checkPow(z, w, this.precision());
        if (result != null) {
            return this.valueOf(result);
        }
        return this.exp(this.multiply(this.log(z), w));
    }

    public Apcomplex pow(Apcomplex z, long n) throws ArithmeticException, ApfloatRuntimeException {
        return this.valueOf(ApcomplexMath.pow(this.setPrecision(z), n));
    }

    public Apfloat arg(Apcomplex z) throws ArithmeticException, ApfloatRuntimeException {
        return this.valueOf(ApcomplexMath.arg(this.setPrecision(z)));
    }

    public Apfloat imag(Apcomplex z) {
        return this.valueOf(z.imag());
    }

    public Apfloat real(Apcomplex z) {
        return this.valueOf(z.real());
    }

    public Apfloat abs(Apcomplex z) throws ApfloatRuntimeException {
        return this.valueOf(ApcomplexMath.abs(this.setPrecision(z)));
    }

    public Apfloat norm(Apcomplex z) throws ApfloatRuntimeException {
        return this.valueOf(ApcomplexMath.norm(this.setPrecision(z)));
    }

    public Apcomplex acos(Apcomplex z) throws ApfloatRuntimeException {
        if (z.real().signum() == 0 && z.imag().signum() == 0) {
            return this.divide(this.pi(z.radix()), new Apfloat(2L, this.precision(), z.radix()));
        }
        return this.valueOf(ApcomplexMath.acos(this.setPrecision(z)));
    }

    public Apcomplex acosh(Apcomplex z) throws ApfloatRuntimeException {
        if (z.real().signum() == 0 && z.imag().signum() == 0) {
            return this.valueOf(new Apcomplex(Apfloat.ZEROS[z.radix()], this.pi(z.radix()).divide(new Apfloat(2L, this.precision(), z.radix()))));
        }
        return this.valueOf(ApcomplexMath.acosh(this.setPrecision(z)));
    }

    public Apcomplex asin(Apcomplex z) throws ApfloatRuntimeException {
        return this.valueOf(ApcomplexMath.asin(this.setPrecision(z)));
    }

    public Apcomplex asinh(Apcomplex z) throws ApfloatRuntimeException {
        return this.valueOf(ApcomplexMath.asinh(this.setPrecision(z)));
    }

    public Apcomplex atan(Apcomplex z) throws ApfloatRuntimeException {
        return this.valueOf(ApcomplexMath.atan(this.setPrecision(z)));
    }

    public Apcomplex atanh(Apcomplex z) throws ArithmeticException, ApfloatRuntimeException {
        return this.valueOf(ApcomplexMath.atanh(this.setPrecision(z)));
    }

    public Apcomplex cbrt(Apcomplex z) throws ApfloatRuntimeException {
        return this.valueOf(ApcomplexMath.cbrt(this.setPrecision(z)));
    }

    public Apcomplex cos(Apcomplex z) throws ApfloatRuntimeException {
        return this.valueOf(ApcomplexMath.cos(this.setTrigExpPrecision(z)));
    }

    public Apcomplex cosh(Apcomplex z) throws ApfloatRuntimeException {
        return this.valueOf(ApcomplexMath.cosh(this.setExpTrigPrecision(z)));
    }

    public Apcomplex exp(Apcomplex z) throws ApfloatRuntimeException {
        return this.valueOf(ApcomplexMath.exp(this.setExpTrigPrecision(z)));
    }

    public Apcomplex log(Apcomplex z) throws ArithmeticException, ApfloatRuntimeException {
        return this.valueOf(ApcomplexMath.log(this.setLogarithmicPrecision(z)));
    }

    public Apcomplex log(Apcomplex z, Apcomplex w) throws ArithmeticException, ApfloatRuntimeException {
        return this.valueOf(ApcomplexMath.log(this.setPrecision(z), this.setPrecision(w)));
    }

    public Apcomplex sin(Apcomplex z) throws ApfloatRuntimeException {
        return this.valueOf(ApcomplexMath.sin(this.setTrigExpPrecision(z)));
    }

    public Apcomplex sinh(Apcomplex z) throws ApfloatRuntimeException {
        return this.valueOf(ApcomplexMath.sinh(this.setExpTrigPrecision(z)));
    }

    public Apcomplex sqrt(Apcomplex z) throws ApfloatRuntimeException {
        return this.valueOf(ApcomplexMath.sqrt(this.setPrecision(z)));
    }

    public Apcomplex tan(Apcomplex z) throws ArithmeticException, ApfloatRuntimeException {
        return this.valueOf(ApcomplexMath.tan(this.setTrigExpPrecision(z)));
    }

    public Apcomplex tanh(Apcomplex z) throws ArithmeticException, ApfloatRuntimeException {
        return this.valueOf(ApcomplexMath.tanhFixedPrecision(this.setExpTrigPrecision(z)));
    }

    public Apcomplex agm(Apcomplex a, Apcomplex b) throws ApfloatRuntimeException {
        return this.valueOf(ApcomplexMath.agm(this.setPrecision(a), this.setPrecision(b)));
    }

    public Apcomplex inverseRoot(Apcomplex z, long n) throws ArithmeticException, ApfloatRuntimeException {
        return this.valueOf(ApcomplexMath.inverseRoot(this.setPrecision(z), n));
    }

    public Apcomplex inverseRoot(Apcomplex z, long n, long k) throws ArithmeticException, ApfloatRuntimeException {
        return this.valueOf(ApcomplexMath.inverseRoot(this.setPrecision(z), n, k));
    }

    public Apcomplex root(Apcomplex z, long n) throws ArithmeticException, ApfloatRuntimeException {
        return this.valueOf(ApcomplexMath.root(this.setPrecision(z), n));
    }

    public Apcomplex root(Apcomplex z, long n, long k) throws ArithmeticException, ApfloatRuntimeException {
        return this.valueOf(ApcomplexMath.root(this.setPrecision(z), n, k));
    }

    public Apcomplex[] allRoots(Apcomplex z, int n) throws ArithmeticException, ApfloatRuntimeException {
        Apcomplex[] allRoots = ApcomplexMath.allRoots(this.setPrecision(z), n);
        for (int i = 0; i < allRoots.length; ++i) {
            allRoots[i] = this.valueOf(allRoots[i]);
        }
        return allRoots;
    }

    public Apcomplex scale(Apcomplex z, long scale) throws ApfloatRuntimeException {
        return ApcomplexMath.scale(this.valueOf(z), scale);
    }

    public Apcomplex w(Apcomplex z) throws ApfloatRuntimeException {
        return this.valueOf(ApcomplexMath.w(this.setPrecision(z)));
    }

    public Apcomplex w(Apcomplex z, long k) throws ApfloatRuntimeException {
        return this.valueOf(ApcomplexMath.w(this.setPrecision(z), k));
    }

    public Apcomplex product(Apcomplex ... z) throws ApfloatRuntimeException {
        return this.valueOf(ApcomplexMath.product(this.setPrecision(z)));
    }

    public Apcomplex sum(Apcomplex ... z) throws ApfloatRuntimeException {
        return this.valueOf(ApcomplexMath.sum(this.setPrecision(z)));
    }

    public Apcomplex gamma(Apcomplex z) throws ArithmeticException, ApfloatRuntimeException {
        return this.valueOf(ApcomplexMath.gamma(this.setGammaPrecision(z)));
    }

    public Apcomplex gamma(Apcomplex a, Apcomplex z) throws ArithmeticException, ApfloatRuntimeException {
        return this.valueOf(ApcomplexMath.gamma(this.setGammaPrecision(a), this.setPrecision(z)));
    }

    public Apcomplex gamma(Apcomplex a, Apcomplex z0, Apcomplex z1) throws ArithmeticException, ApfloatRuntimeException {
        return this.valueOf(ApcomplexMath.gamma(this.setGammaPrecision(a), this.setPrecision(z0), this.setPrecision(z1)));
    }

    public Apfloat ulp(Apcomplex z) throws ApfloatRuntimeException {
        return this.valueOf(ApcomplexMath.ulp(this.setPrecision(z)));
    }

    public long precision() {
        return this.precision;
    }

    Apfloat valueOf(Apfloat x) throws ApfloatRuntimeException {
        return x.precision(this.precision());
    }

    Apfloat pi() throws ApfloatRuntimeException {
        return ApfloatMath.pi(this.precision());
    }

    Apfloat pi(int radix) throws NumberFormatException, ApfloatRuntimeException {
        return ApfloatMath.pi(this.precision(), radix);
    }

    Apfloat setTrigonometricPrecision(Apfloat x) throws ApfloatRuntimeException {
        long precision = ApfloatHelper.extendPrecision(this.precision(), Math.max(0L, x.scale()));
        return x.precision(precision);
    }

    Apfloat setExponentialPrecision(Apfloat x) throws ApfloatRuntimeException {
        if (x.scale() <= -this.precision()) {
            x = new Apfloat(0L, Long.MIN_VALUE, x.radix());
        } else if (x.scale() < 0L) {
            long precision = Util.ifFinite(this.precision(), this.precision() + x.scale());
            x = x.precision(precision);
        } else if (x.scale() > 1L) {
            long precision = Util.ifFinite(this.precision(), this.precision() + x.scale() - 1L);
            x = x.precision(precision);
        } else {
            x = x.precision(this.precision());
        }
        return x;
    }

    Apfloat setLogarithmicPrecision(Apfloat x) throws ApfloatRuntimeException {
        long precision = ApfloatHelper.extendPrecision(this.precision(), x.equalDigits(new Apfloat(1L, Long.MAX_VALUE, x.radix())));
        return x.precision(precision);
    }

    Apfloat setGammaPrecision(Apfloat x) {
        return this.setTrigonometricPrecision(x);
    }

    private Apcomplex setPrecision(Apcomplex z) throws ApfloatRuntimeException {
        return z.precision(this.precision());
    }

    private Apcomplex[] setPrecision(Apcomplex[] z) throws ApfloatRuntimeException {
        Apcomplex[] tmp = new Apcomplex[z.length];
        for (int i = 0; i < z.length; ++i) {
            tmp[i] = this.setPrecision(z[i]);
        }
        return tmp;
    }

    private Apcomplex setExpTrigPrecision(Apcomplex z) {
        return new Apcomplex(this.setExponentialPrecision(z.real()), this.setTrigonometricPrecision(z.imag()));
    }

    private Apcomplex setTrigExpPrecision(Apcomplex z) {
        return new Apcomplex(this.setTrigonometricPrecision(z.real()), this.setExponentialPrecision(z.imag()));
    }

    private Apcomplex setLogarithmicPrecision(Apcomplex z) {
        long precision = ApfloatHelper.extendPrecision(this.precision(), this.abs(z).equalDigits(new Apfloat(1L, Long.MAX_VALUE, z.radix())));
        return z.precision(precision);
    }

    private Apcomplex setGammaPrecision(Apcomplex z) {
        long precision = ApfloatHelper.extendPrecision(this.precision(), Math.max(0L, z.scale()));
        return z.precision(precision);
    }
}

