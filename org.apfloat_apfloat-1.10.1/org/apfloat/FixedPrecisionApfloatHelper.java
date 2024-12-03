/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat;

import java.math.RoundingMode;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatHelper;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.FixedPrecisionApcomplexHelper;
import org.apfloat.spi.Util;

public class FixedPrecisionApfloatHelper
extends FixedPrecisionApcomplexHelper {
    public FixedPrecisionApfloatHelper(long precision) throws IllegalArgumentException {
        super(precision);
    }

    @Override
    public Apfloat valueOf(Apfloat x) {
        return super.valueOf(x);
    }

    public Apfloat negate(Apfloat x) throws ApfloatRuntimeException {
        return this.valueOf(x).negate();
    }

    public Apfloat add(Apfloat x, Apfloat y) throws ApfloatRuntimeException {
        return this.valueOf(this.setPrecision(x).add(this.valueOf(y)));
    }

    public Apfloat subtract(Apfloat x, Apfloat y) throws ApfloatRuntimeException {
        return this.valueOf(this.setPrecision(x).subtract(this.setPrecision(y)));
    }

    public Apfloat multiply(Apfloat x, Apfloat y) throws ApfloatRuntimeException {
        return this.valueOf(this.setPrecision(x).multiply(this.setPrecision(y)));
    }

    public Apfloat divide(Apfloat x, Apfloat y) throws ArithmeticException, ApfloatRuntimeException {
        return this.valueOf(this.setPrecision(x).divide(this.setPrecision(y)));
    }

    public Apfloat pow(Apfloat x, Apfloat y) throws ArithmeticException, ApfloatRuntimeException {
        Apfloat result = ApfloatHelper.checkPow(x, y, this.precision());
        if (result != null) {
            return this.valueOf(result);
        }
        return this.exp(this.multiply(this.log(x), y));
    }

    public Apfloat pow(Apfloat x, long n) throws ArithmeticException, ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.pow(this.setPrecision(x), n));
    }

    public Apfloat abs(Apfloat x) throws ApfloatRuntimeException {
        return ApfloatMath.abs(this.valueOf(x));
    }

    public Apfloat acos(Apfloat x) throws ArithmeticException, ApfloatRuntimeException {
        if (x.signum() == 0) {
            return this.divide(this.pi(x.radix()), new Apfloat(2L, this.precision(), x.radix()));
        }
        return this.valueOf(ApfloatMath.acos(this.setPrecision(x)));
    }

    public Apfloat acosh(Apfloat x) throws ArithmeticException, ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.acosh(this.setPrecision(x)));
    }

    public Apfloat asin(Apfloat x) throws ArithmeticException, ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.asin(this.setPrecision(x)));
    }

    public Apfloat asinh(Apfloat x) throws ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.asinh(this.setPrecision(x)));
    }

    public Apfloat atan(Apfloat x) throws ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.atan(this.setPrecision(x)));
    }

    public Apfloat atanh(Apfloat x) throws ArithmeticException, ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.atanh(this.setPrecision(x)));
    }

    public Apfloat cbrt(Apfloat x) throws ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.cbrt(this.setPrecision(x)));
    }

    public Apfloat cos(Apfloat x) throws ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.cos(this.setTrigonometricPrecision(x)));
    }

    public Apfloat cosh(Apfloat x) throws ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.cosh(this.setExponentialPrecision(x)));
    }

    public Apfloat exp(Apfloat x) throws ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.exp(this.setExponentialPrecision(x)));
    }

    public Apfloat log(Apfloat x) throws ArithmeticException, ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.log(this.setLogarithmicPrecision(x)));
    }

    public Apfloat log(Apfloat x, Apfloat b) throws ArithmeticException, ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.log(this.setPrecision(x), this.setPrecision(b)));
    }

    public Apfloat sin(Apfloat x) throws ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.sin(this.setTrigonometricPrecision(x)));
    }

    public Apfloat sinh(Apfloat x) throws ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.sinh(this.setExponentialPrecision(x)));
    }

    public Apfloat sqrt(Apfloat x) throws ArithmeticException, ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.sqrt(this.setPrecision(x)));
    }

    public Apfloat tan(Apfloat x) throws ArithmeticException, ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.tan(this.setTrigonometricPrecision(x)));
    }

    public Apfloat tanh(Apfloat x) throws ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.tanhFixedPrecision(this.setExponentialPrecision(x)));
    }

    public Apfloat agm(Apfloat a, Apfloat b) throws ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.agm(this.setPrecision(a), this.setPrecision(b)));
    }

    public Apfloat inverseRoot(Apfloat x, long n) throws ArithmeticException, ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.inverseRoot(this.setPrecision(x), n));
    }

    public Apfloat root(Apfloat x, long n) throws ArithmeticException, ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.root(this.setPrecision(x), n));
    }

    public Apfloat scale(Apfloat x, long scale) throws ApfloatRuntimeException {
        return ApfloatMath.scale(this.valueOf(x), scale);
    }

    public Apfloat mod(Apfloat x, Apfloat y) throws ApfloatRuntimeException {
        return this.fmod(x, y);
    }

    public Apfloat ceil(Apfloat x) throws ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.ceil(this.setPrecision(x)));
    }

    public Apfloat floor(Apfloat x) throws ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.floor(this.setPrecision(x)));
    }

    public Apfloat truncate(Apfloat x) throws ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.truncate(this.setPrecision(x)));
    }

    public Apfloat frac(Apfloat x) throws ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.frac(x));
    }

    public Apfloat round(Apfloat x, RoundingMode roundingMode) throws ApfloatRuntimeException {
        return ApfloatMath.round(x, this.precision(), roundingMode);
    }

    public Apfloat w(Apfloat x) throws ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.w(this.setPrecision(x)));
    }

    public Apfloat toDegrees(Apfloat x) throws ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.toDegrees(this.setPrecision(x)));
    }

    public Apfloat toRadians(Apfloat x) throws ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.toRadians(this.setPrecision(x)));
    }

    public Apfloat atan2(Apfloat x, Apfloat y) throws ArithmeticException, ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.atan2(this.setPrecision(x), this.setPrecision(y)));
    }

    public Apfloat copySign(Apfloat x, Apfloat y) throws ApfloatRuntimeException {
        return ApfloatMath.copySign(this.valueOf(x), y);
    }

    public Apfloat fmod(Apfloat x, Apfloat y) throws ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.fmod(x, this.setPrecision(y)));
    }

    public Apfloat[] modf(Apfloat x) throws ApfloatRuntimeException {
        if (x.scale() > 0L) {
            long precision = Util.ifFinite(this.precision(), this.precision() + x.scale());
            x = x.precision(precision);
        } else {
            x = this.setPrecision(x);
        }
        Apfloat[] modfs = ApfloatMath.modf(x);
        modfs[0] = this.valueOf(modfs[0]);
        modfs[1] = this.valueOf(modfs[1]);
        return modfs;
    }

    public Apfloat factorial(long n) throws ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.factorial(n, this.precision()));
    }

    public Apfloat factorial(long n, int radix) throws ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.factorial(n, this.precision(), radix));
    }

    @Override
    public Apfloat pi() throws ApfloatRuntimeException {
        return super.pi();
    }

    @Override
    public Apfloat pi(int radix) throws NumberFormatException, ApfloatRuntimeException {
        return super.pi(radix);
    }

    public Apfloat logRadix(int radix) throws NumberFormatException, ApfloatRuntimeException {
        return ApfloatMath.logRadix(this.precision(), radix);
    }

    public Apfloat multiplyAdd(Apfloat a, Apfloat b, Apfloat c, Apfloat d) throws ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.multiplyAdd(this.setPrecision(a), this.setPrecision(b), this.setPrecision(c), this.setPrecision(d)));
    }

    public Apfloat multiplySubtract(Apfloat a, Apfloat b, Apfloat c, Apfloat d) throws ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.multiplySubtract(this.setPrecision(a), this.setPrecision(b), this.setPrecision(c), this.setPrecision(d)));
    }

    public Apfloat product(Apfloat ... x) throws ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.product(this.setPrecision(x)));
    }

    public Apfloat sum(Apfloat ... x) throws ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.sum(this.setPrecision(x)));
    }

    public Apfloat euler() throws ApfloatRuntimeException {
        return ApfloatMath.euler(this.precision());
    }

    public Apfloat euler(int radix) throws NumberFormatException, ApfloatRuntimeException {
        return ApfloatMath.euler(this.precision(), radix);
    }

    public Apfloat gamma(Apfloat x) throws ArithmeticException, ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.gamma(this.setGammaPrecision(x)));
    }

    public Apfloat gamma(Apfloat a, Apfloat x) throws ArithmeticException, ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.gamma(this.setGammaPrecision(a), this.setPrecision(x)));
    }

    public Apfloat gamma(Apfloat a, Apfloat x0, Apfloat x1) throws ArithmeticException, ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.gamma(this.setGammaPrecision(a), this.setPrecision(x0), this.setPrecision(x1)));
    }

    public Apfloat random() throws ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.random(this.precision()));
    }

    public Apfloat random(int radix) throws NumberFormatException, ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.random(this.precision(), radix));
    }

    public Apfloat randomGaussian() throws ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.randomGaussian(this.precision()));
    }

    public Apfloat randomGaussian(int radix) throws NumberFormatException, ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.randomGaussian(this.precision(), radix));
    }

    public Apfloat max(Apfloat x, Apfloat y) throws ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.max(this.setPrecision(x), this.setPrecision(y)));
    }

    public Apfloat min(Apfloat x, Apfloat y) throws ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.min(this.setPrecision(x), this.setPrecision(y)));
    }

    public Apfloat nextAfter(Apfloat x, Apfloat y) throws ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.nextAfter(this.setPrecision(x), this.setPrecision(y)));
    }

    public Apfloat nextDown(Apfloat x) throws ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.nextDown(this.setPrecision(x)));
    }

    public Apfloat nextUp(Apfloat x) throws ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.nextUp(this.setPrecision(x)));
    }

    public Apfloat ulp(Apfloat x) throws ApfloatRuntimeException {
        return this.valueOf(ApfloatMath.ulp(this.setPrecision(x)));
    }

    private Apfloat setPrecision(Apfloat x) throws ApfloatRuntimeException {
        return x.precision(this.precision());
    }

    private Apfloat[] setPrecision(Apfloat[] x) throws ApfloatRuntimeException {
        Apfloat[] tmp = new Apfloat[x.length];
        for (int i = 0; i < x.length; ++i) {
            tmp[i] = this.setPrecision(x[i]);
        }
        return tmp;
    }
}

