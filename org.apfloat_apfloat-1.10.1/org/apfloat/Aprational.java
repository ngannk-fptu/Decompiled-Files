/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Writer;
import java.lang.ref.SoftReference;
import java.math.BigInteger;
import java.util.Formatter;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatContext;
import org.apfloat.ApfloatHelper;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.Apint;
import org.apfloat.ApintMath;
import org.apfloat.AprationalMath;
import org.apfloat.FormattingHelper;
import org.apfloat.RoundingHelper;
import org.apfloat.spi.ApfloatImpl;
import org.apfloat.spi.RadixConstants;

public class Aprational
extends Apfloat {
    private static final long serialVersionUID = -224128535732558313L;
    private static final long UNDEFINED = Long.MIN_VALUE;
    private Apint numerator;
    private Apint denominator;
    private volatile long scale = Long.MIN_VALUE;
    private volatile long size = 0L;
    private transient SoftReference<Apfloat> inverseDen = null;
    private transient SoftReference<Apfloat> approx = null;

    protected Aprational() {
    }

    public Aprational(Apint value) throws ApfloatRuntimeException {
        this(value, ONES[value.radix()]);
    }

    public Aprational(Apint numerator, Apint denominator) throws IllegalArgumentException, ApfloatRuntimeException {
        this.numerator = numerator;
        this.denominator = denominator;
        this.checkDenominator();
        this.reduce();
    }

    public Aprational(String value) throws NumberFormatException, IllegalArgumentException, ApfloatRuntimeException {
        this(value, ApfloatContext.getContext().getDefaultRadix());
    }

    public Aprational(String value, int radix) throws NumberFormatException, IllegalArgumentException, ApfloatRuntimeException {
        int index = value.indexOf(47);
        if (index < 0) {
            this.numerator = new Apint(value, radix);
            this.denominator = ONES[radix];
            return;
        }
        this.numerator = new Apint(value.substring(0, index).trim(), radix);
        this.denominator = new Apint(value.substring(index + 1).trim(), radix);
        this.checkDenominator();
        this.reduce();
    }

    public Aprational(PushbackReader in) throws IOException, NumberFormatException, IllegalArgumentException, ApfloatRuntimeException {
        this(in, ApfloatContext.getContext().getDefaultRadix());
    }

    public Aprational(PushbackReader in, int radix) throws IOException, NumberFormatException, IllegalArgumentException, ApfloatRuntimeException {
        this.numerator = new Apint(in, radix);
        ApfloatHelper.extractWhitespace(in);
        if (!ApfloatHelper.readMatch(in, 47)) {
            this.denominator = ONES[radix];
            return;
        }
        ApfloatHelper.extractWhitespace(in);
        this.denominator = new Apint(in, radix);
        this.checkDenominator();
        this.reduce();
    }

    public Aprational(BigInteger value) throws ApfloatRuntimeException {
        this.numerator = new Apint(value);
        this.denominator = ONE;
    }

    public Aprational(BigInteger value, int radix) throws ApfloatRuntimeException {
        this.numerator = new Apint(value, radix);
        this.denominator = ONES[radix];
    }

    public Aprational(double value) throws ApfloatRuntimeException {
        this(value, ApfloatContext.getContext().getDefaultRadix());
    }

    public Aprational(double value, int radix) throws ApfloatRuntimeException {
        if (Double.isInfinite(value) || Double.isNaN(value)) {
            throw new NumberFormatException(value + " is not a valid number");
        }
        long bits = Double.doubleToLongBits(value);
        long sign = bits >> 63 == 0L ? 1 : -1;
        long exponent = bits >> 52 & 0x7FFL;
        long significand = exponent == 0L ? (bits & 0xFFFFFFFFFFFFFL) << 1 : bits & 0xFFFFFFFFFFFFFL | 0x10000000000000L;
        exponent -= 1075L;
        if (significand == 0L) {
            this.numerator = new Apint(0L, radix);
            this.denominator = ONES[radix];
            return;
        }
        while ((significand & 1L) == 0L) {
            significand >>= 1;
            ++exponent;
        }
        this.numerator = new Apint(sign * significand, radix);
        Apint powerOfTwo = ApintMath.pow(new Apint(2L, radix), Math.abs(exponent));
        if (exponent >= 0L) {
            this.numerator = this.numerator.multiply(powerOfTwo);
            this.denominator = ONES[radix];
        } else {
            this.denominator = powerOfTwo;
        }
    }

    public Apint numerator() {
        return this.numerator;
    }

    public Apint denominator() {
        return this.denominator;
    }

    @Override
    public int radix() {
        return this.numerator() == ONE ? this.denominator().radix() : this.numerator().radix();
    }

    @Override
    public long precision() throws ApfloatRuntimeException {
        return Long.MAX_VALUE;
    }

    @Override
    public long scale() throws ApfloatRuntimeException {
        if (this.signum() == 0) {
            return -9223372036854775807L;
        }
        if (this.scale == Long.MIN_VALUE) {
            long scale = this.numerator().scale() - this.denominator().scale();
            scale = scale > 0L ? this.truncate().scale() : AprationalMath.scale(this, 1L - scale).truncate().scale() + scale - 1L;
            this.scale = scale;
        }
        return this.scale;
    }

    @Override
    public long size() throws ApfloatRuntimeException {
        if (this.signum() == 0) {
            return 0L;
        }
        if (this.denominator().equals(ONE)) {
            return this.numerator().size();
        }
        if (this.size == 0L) {
            Apint dividend = this.denominator();
            for (int i = 0; i < RadixConstants.RADIX_FACTORS[this.radix()].length; ++i) {
                Apint[] quotientAndRemainder;
                Apint factor = new Apint((long)RadixConstants.RADIX_FACTORS[this.radix()][i], this.radix());
                while ((quotientAndRemainder = ApintMath.div(dividend, factor))[1].signum() == 0) {
                    dividend = quotientAndRemainder[0];
                }
            }
            long size = !dividend.equals(ONE) ? Long.MAX_VALUE : ApintMath.scale(this.numerator(), this.denominator().scale() * 5L).divide(this.denominator()).size();
            this.size = size;
        }
        return this.size;
    }

    @Override
    public int signum() {
        return this.numerator().signum();
    }

    @Override
    public boolean isShort() throws ApfloatRuntimeException {
        return this.numerator().isShort() && this.denominator().equals(ONE);
    }

    @Override
    public boolean isInteger() throws ApfloatRuntimeException {
        return this.denominator().equals(ONE);
    }

    @Override
    public Aprational negate() throws ApfloatRuntimeException {
        return new Aprational(this.numerator().negate(), this.denominator());
    }

    public Aprational add(Aprational x) throws ApfloatRuntimeException {
        return new Aprational(this.numerator().multiply(x.denominator()).add(this.denominator().multiply(x.numerator())), this.denominator().multiply(x.denominator())).reduce();
    }

    public Aprational subtract(Aprational x) throws ApfloatRuntimeException {
        return new Aprational(this.numerator().multiply(x.denominator()).subtract(this.denominator().multiply(x.numerator())), this.denominator().multiply(x.denominator())).reduce();
    }

    public Aprational multiply(Aprational x) throws ApfloatRuntimeException {
        Aprational result = new Aprational(this.numerator().multiply(x.numerator()), this.denominator().multiply(x.denominator()));
        if (this == x) {
            return result;
        }
        return result.reduce();
    }

    public Aprational divide(Aprational x) throws ArithmeticException, ApfloatRuntimeException {
        if (x.signum() == 0) {
            throw new ArithmeticException(this.signum() == 0 ? "Zero divided by zero" : "Division by zero");
        }
        if (this.signum() == 0) {
            return this;
        }
        return new Aprational(this.numerator().multiply(x.denominator()), this.denominator().multiply(x.numerator())).reduce();
    }

    public Aprational mod(Aprational x) throws ApfloatRuntimeException {
        if (x.signum() == 0) {
            return x;
        }
        if (this.signum() == 0) {
            return this;
        }
        return this.subtract(this.divide(x).truncate().multiply(x));
    }

    @Override
    public Apint floor() throws ApfloatRuntimeException {
        if (this.signum() >= 0) {
            return this.truncate();
        }
        return this.roundAway();
    }

    @Override
    public Apint ceil() throws ApfloatRuntimeException {
        if (this.signum() <= 0) {
            return this.truncate();
        }
        return this.roundAway();
    }

    @Override
    public Apint truncate() throws ApfloatRuntimeException {
        return this.numerator().divide(this.denominator());
    }

    @Override
    public Aprational frac() throws ApfloatRuntimeException {
        return new Aprational(this.numerator().mod(this.denominator()), this.denominator());
    }

    @Override
    public Aprational toRadix(int radix) throws NumberFormatException, ApfloatRuntimeException {
        return new Aprational(this.numerator().toRadix(radix), this.denominator().toRadix(radix));
    }

    @Override
    public int compareTo(Aprational x) {
        Apint a = this.numerator().multiply(x.denominator());
        Apint b = x.numerator().multiply(this.denominator());
        return a.compareTo(b);
    }

    @Override
    public int compareTo(Apfloat x) {
        if (x instanceof Aprational) {
            return this.compareTo((Aprational)x);
        }
        Apfloat a = this.numerator().precision(Long.MAX_VALUE);
        Apfloat b = x.multiply(this.denominator()).precision(Long.MAX_VALUE);
        return a.compareTo(b);
    }

    @Override
    public boolean preferCompare(Apfloat x) {
        return !(x instanceof Aprational);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Aprational) {
            Aprational that = (Aprational)obj;
            return this.numerator().equals(that.numerator()) && this.denominator().equals(that.denominator());
        }
        if (obj instanceof Apfloat) {
            Apfloat that = (Apfloat)obj;
            Apfloat a = this.numerator().precision(Long.MAX_VALUE);
            Apfloat b = that.multiply(this.denominator()).precision(Long.MAX_VALUE);
            return a.equals(b);
        }
        return super.equals(obj);
    }

    public boolean test(Aprational x) {
        return this.numerator().test(x.numerator()) && this.denominator().test(x.denominator());
    }

    @Override
    public boolean test(Apfloat x) throws ApfloatRuntimeException {
        if (x instanceof Aprational) {
            return this.test((Aprational)x);
        }
        return !this.isInteger() || this.numerator().test(x);
    }

    @Override
    public int hashCode() {
        return this.numerator().hashCode() * 3 + this.denominator().hashCode();
    }

    @Override
    public String toString() {
        return this.toString(true);
    }

    @Override
    public String toString(boolean pretty) throws ApfloatRuntimeException {
        return this.numerator().toString(pretty) + (this.denominator().equals(ONE) ? "" : '/' + this.denominator().toString(pretty));
    }

    @Override
    public void writeTo(Writer out) throws IOException, ApfloatRuntimeException {
        this.writeTo(out, true);
    }

    @Override
    public void writeTo(Writer out, boolean pretty) throws IOException, ApfloatRuntimeException {
        this.numerator().writeTo(out, pretty);
        if (!this.denominator().equals(ONE)) {
            out.write(47);
            this.denominator().writeTo(out, pretty);
        }
    }

    @Override
    public void formatTo(Formatter formatter, int flags, int width, int precision) {
        if (this.denominator().equals(ONE)) {
            this.numerator().formatTo(formatter, flags, width, precision);
        } else if (width == -1) {
            this.numerator().formatTo(formatter, flags, width, precision);
            formatter.format("/", new Object[0]);
            this.denominator().formatTo(formatter, flags, width, precision);
        } else {
            try {
                Writer out = FormattingHelper.wrapAppendableWriter(formatter.out());
                out = FormattingHelper.wrapPadWriter(out, (flags & 1) == 1);
                formatter = new Formatter(out, formatter.locale());
                this.numerator().formatTo(formatter, flags, -1, precision);
                formatter.format("/", new Object[0]);
                this.denominator().formatTo(formatter, flags, -1, precision);
                FormattingHelper.finishPad(out, width);
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    @Override
    protected ApfloatImpl getImpl(long precision) throws ApfloatRuntimeException {
        return this.ensureApprox(precision).getImpl(precision);
    }

    @Override
    Apint roundAway() throws ApfloatRuntimeException {
        Apint[] div = ApintMath.div(this.numerator(), this.denominator());
        if (div[1].signum() == 0) {
            return div[0];
        }
        return div[0].add(new Apint((long)this.signum(), div[0].radix()));
    }

    @Override
    Aprational scale(long scale) {
        return AprationalMath.scale(this, scale);
    }

    @Override
    Aprational abs() {
        return AprationalMath.abs(this);
    }

    @Override
    int compareToHalf() {
        return RoundingHelper.compareToHalf(this);
    }

    private void checkDenominator() throws IllegalArgumentException {
        if (this.denominator.signum() == 0) {
            throw new IllegalArgumentException("Denominator is zero");
        }
    }

    private Aprational reduce() throws IllegalArgumentException, ApfloatRuntimeException {
        if (this.numerator.signum() == 0) {
            this.denominator = ONES[this.denominator.radix()];
        } else {
            if (!this.numerator.equals(ONE) && !this.denominator.equals(ONE)) {
                if (this.numerator.radix() != this.denominator.radix()) {
                    throw new IllegalArgumentException("Numerator and denominator must have the same radix");
                }
                Apint gcd = ApintMath.gcd(this.numerator, this.denominator);
                this.numerator = this.numerator.divide(gcd);
                this.denominator = this.denominator.divide(gcd);
            }
            int sign = this.numerator.signum() * this.denominator.signum();
            this.denominator = ApintMath.abs(this.denominator);
            if (sign != this.numerator.signum()) {
                this.numerator = this.numerator.negate();
            }
        }
        return this;
    }

    private synchronized Apfloat ensureApprox(long precision) throws ApfloatRuntimeException {
        Apfloat approx = this.getApprox(precision);
        if (approx == null || approx.precision() < precision) {
            if (this.denominator().equals(ONE)) {
                approx = this.numerator();
            } else {
                precision = Math.max(precision, 1L);
                if (this.denominator().isShort()) {
                    approx = this.numerator().precision(precision).divide(this.denominator());
                    this.setApprox(approx);
                } else {
                    Apfloat inverseDen = this.getInverseDen();
                    inverseDen = ApfloatMath.inverseRoot(this.denominator(), 1L, precision, inverseDen);
                    approx = this.numerator().multiply(inverseDen);
                    this.setApprox(approx);
                    this.setInverseDen(inverseDen);
                }
            }
        }
        return approx;
    }

    private Apfloat getApprox(long precision) {
        return this.approx == null ? null : this.approx.get();
    }

    private void setApprox(Apfloat approx) {
        this.approx = new SoftReference<Apfloat>(approx);
    }

    private Apfloat getInverseDen() {
        return this.inverseDen == null ? null : this.inverseDen.get();
    }

    private void setInverseDen(Apfloat inverseDen) {
        this.inverseDen = new SoftReference<Apfloat>(inverseDen);
    }
}

