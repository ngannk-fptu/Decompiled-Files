/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Writer;
import java.math.BigInteger;
import java.util.FormatFlagsConversionMismatchException;
import java.util.Formatter;
import java.util.IllegalFormatPrecisionException;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatHelper;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.ApintMath;
import org.apfloat.Aprational;
import org.apfloat.spi.ApfloatImpl;

public class Apint
extends Aprational {
    private static final long serialVersionUID = 5409721945040465491L;
    private Apfloat value;

    protected Apint() {
    }

    Apint(Apfloat value) {
        this.value = value;
    }

    public Apint(String value) throws NumberFormatException, ApfloatRuntimeException {
        this.value = new Apfloat(ApfloatHelper.createApfloat(value, true));
    }

    public Apint(String value, int radix) throws NumberFormatException, ApfloatRuntimeException {
        this.value = new Apfloat(ApfloatHelper.createApfloat(value, Long.MAX_VALUE, radix, true));
    }

    public Apint(long value) throws NumberFormatException, ApfloatRuntimeException {
        this.value = new Apfloat(ApfloatHelper.createApfloat(value));
    }

    public Apint(long value, int radix) throws NumberFormatException, ApfloatRuntimeException {
        this.value = new Apfloat(ApfloatHelper.createApfloat(value, Long.MAX_VALUE, radix));
    }

    public Apint(PushbackReader in) throws IOException, NumberFormatException, ApfloatRuntimeException {
        this.value = new Apfloat(ApfloatHelper.createApfloat(in, Long.MAX_VALUE, true));
    }

    public Apint(PushbackReader in, int radix) throws IOException, NumberFormatException, ApfloatRuntimeException {
        this.value = new Apfloat(ApfloatHelper.createApfloat(in, Long.MAX_VALUE, radix, true));
    }

    public Apint(BigInteger value) throws NumberFormatException, ApfloatRuntimeException {
        this.value = new Apfloat(value);
    }

    public Apint(BigInteger value, int radix) throws NumberFormatException, ApfloatRuntimeException {
        this.value = new Apfloat(value, Long.MAX_VALUE, radix);
    }

    @Override
    public Apint numerator() {
        return this;
    }

    @Override
    public Apint denominator() {
        return ONES[this.radix()];
    }

    @Override
    public int radix() {
        return this.value.radix();
    }

    @Override
    public long scale() throws ApfloatRuntimeException {
        return this.value.scale();
    }

    @Override
    public long size() throws ApfloatRuntimeException {
        return this.value.size();
    }

    @Override
    public int signum() {
        return this.value.signum();
    }

    @Override
    public boolean isShort() throws ApfloatRuntimeException {
        return this.value.isShort();
    }

    @Override
    public boolean isInteger() throws ApfloatRuntimeException {
        return true;
    }

    @Override
    public Apint negate() throws ApfloatRuntimeException {
        return new Apint(this.value.negate());
    }

    public Apint add(Apint x) throws ApfloatRuntimeException {
        return new Apint(this.value.add(x.value));
    }

    public Apint subtract(Apint x) throws ApfloatRuntimeException {
        return new Apint(this.value.subtract(x.value));
    }

    public Apint multiply(Apint x) throws ApfloatRuntimeException {
        return new Apint(this.value.multiply(x.value));
    }

    public Apint divide(Apint x) throws ArithmeticException, ApfloatRuntimeException {
        Apfloat ty;
        Apfloat b;
        if (x.signum() == 0) {
            throw new ArithmeticException(this.signum() == 0 ? "Zero divided by zero" : "Division by zero");
        }
        if (this.signum() == 0) {
            return this;
        }
        if (x.equals(ONE)) {
            return this;
        }
        Apfloat a = ApfloatMath.abs(this.value);
        if (a.compareTo(b = ApfloatMath.abs(x.value)) < 0) {
            return ZEROS[a.radix()];
        }
        long precision = this.scale() - x.scale() + 20L;
        Apfloat tx = this.value.precision(precision);
        Apint t = tx.divide(ty = x.value.precision(precision)).truncate();
        if ((a = a.subtract(ApfloatMath.abs(t.multiply(x.value)))).compareTo(b) >= 0) {
            t = t.add(new Apint((long)(this.signum() * x.signum()), x.radix()));
        } else if (a.signum() < 0) {
            t = t.subtract(new Apint((long)(this.signum() * x.signum()), x.radix()));
        }
        return t;
    }

    public Apint mod(Apint x) throws ApfloatRuntimeException {
        return new Apint(this.value.mod(x.value));
    }

    @Override
    public Apint floor() {
        return this;
    }

    @Override
    public Apint ceil() {
        return this;
    }

    @Override
    public Apint truncate() {
        return this;
    }

    @Override
    public Apint frac() throws ApfloatRuntimeException {
        return ZEROS[this.radix()];
    }

    public BigInteger toBigInteger() throws IllegalArgumentException {
        if (this.signum() == 0) {
            return BigInteger.ZERO;
        }
        return ApfloatHelper.toBigInteger(this);
    }

    @Override
    public Apint toRadix(int radix) throws NumberFormatException, ApfloatRuntimeException {
        return new Apint(this.value.toRadix(radix));
    }

    @Override
    public int compareTo(Apint x) {
        return this.value.compareTo(x.value);
    }

    @Override
    public int compareTo(Aprational x) {
        if (x instanceof Apint) {
            return this.compareTo((Apint)x);
        }
        return super.compareTo(x);
    }

    @Override
    public int compareTo(Apfloat x) {
        if (x instanceof Aprational) {
            return this.compareTo((Aprational)x);
        }
        return this.value.compareTo(x);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Apint) {
            Apint that = (Apint)obj;
            return this.value.equals(that.value);
        }
        if (obj instanceof Apfloat && !(obj instanceof Aprational)) {
            Apfloat that = (Apfloat)obj;
            return this.value.equals(that);
        }
        return super.equals(obj);
    }

    public boolean test(Apint x) {
        return this.value.test(x.value);
    }

    @Override
    public boolean test(Aprational x) {
        if (x instanceof Apint) {
            return this.test((Apint)x);
        }
        return super.test(x);
    }

    @Override
    public boolean test(Apfloat x) {
        if (x instanceof Aprational) {
            return this.test((Aprational)x);
        }
        return this.value.test(x);
    }

    @Override
    public int hashCode() {
        return this.value.hashCode();
    }

    @Override
    public String toString(boolean pretty) throws ApfloatRuntimeException {
        return this.value.toString(pretty);
    }

    @Override
    public void writeTo(Writer out, boolean pretty) throws IOException, ApfloatRuntimeException {
        this.value.writeTo(out, pretty);
    }

    @Override
    public void formatTo(Formatter formatter, int flags, int width, int precision) {
        if ((flags & 4) == 4) {
            throw new FormatFlagsConversionMismatchException("#", 's');
        }
        if (precision != -1) {
            throw new IllegalFormatPrecisionException(precision);
        }
        this.value.formatTo(formatter, flags | 4, width, precision);
    }

    @Override
    protected ApfloatImpl getImpl(long precision) throws ApfloatRuntimeException {
        return this.value.getImpl(precision);
    }

    @Override
    Apint roundAway() {
        return this;
    }

    @Override
    Apint abs() {
        return ApintMath.abs(this);
    }
}

