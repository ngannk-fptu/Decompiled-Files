/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Formatter;
import org.apfloat.Apcomplex;
import org.apfloat.ApfloatHelper;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.Apint;
import org.apfloat.FormattingHelper;
import org.apfloat.RadixConversionHelper;
import org.apfloat.RoundingHelper;
import org.apfloat.spi.ApfloatImpl;

public class Apfloat
extends Apcomplex
implements Comparable<Apfloat> {
    private static final long serialVersionUID = -36707433458144439L;
    private ApfloatImpl impl;

    protected Apfloat() {
    }

    protected Apfloat(ApfloatImpl impl) {
        assert (impl.precision() > 0L);
        this.impl = impl;
    }

    public Apfloat(String value) throws NumberFormatException, ApfloatRuntimeException {
        this(ApfloatHelper.createApfloat(value, false));
    }

    public Apfloat(String value, long precision) throws NumberFormatException, IllegalArgumentException, ApfloatRuntimeException {
        this(ApfloatHelper.createApfloat(value, precision, false));
    }

    public Apfloat(String value, long precision, int radix) throws NumberFormatException, IllegalArgumentException, ApfloatRuntimeException {
        this(ApfloatHelper.createApfloat(value, precision, radix, false));
    }

    public Apfloat(long value) throws NumberFormatException, ApfloatRuntimeException {
        this(ApfloatHelper.createApfloat(value));
    }

    public Apfloat(long value, long precision) throws NumberFormatException, IllegalArgumentException, ApfloatRuntimeException {
        this(ApfloatHelper.createApfloat(value, precision));
    }

    public Apfloat(long value, long precision, int radix) throws NumberFormatException, IllegalArgumentException, ApfloatRuntimeException {
        this(ApfloatHelper.createApfloat(value, precision, radix));
    }

    public Apfloat(float value) throws NumberFormatException, ApfloatRuntimeException {
        this(ApfloatHelper.createApfloat(value));
    }

    public Apfloat(float value, long precision) throws NumberFormatException, IllegalArgumentException, ApfloatRuntimeException {
        this(ApfloatHelper.createApfloat(value, precision));
    }

    public Apfloat(float value, long precision, int radix) throws NumberFormatException, IllegalArgumentException, ApfloatRuntimeException {
        this(ApfloatHelper.createApfloat(value, precision, radix));
    }

    public Apfloat(double value) throws NumberFormatException, ApfloatRuntimeException {
        this(ApfloatHelper.createApfloat(value));
    }

    public Apfloat(double value, long precision) throws NumberFormatException, IllegalArgumentException, ApfloatRuntimeException {
        this(ApfloatHelper.createApfloat(value, precision));
    }

    public Apfloat(double value, long precision, int radix) throws NumberFormatException, IllegalArgumentException, ApfloatRuntimeException {
        this(ApfloatHelper.createApfloat(value, precision, radix));
    }

    public Apfloat(PushbackReader in) throws IOException, NumberFormatException, ApfloatRuntimeException {
        this(ApfloatHelper.createApfloat(in, false));
    }

    public Apfloat(PushbackReader in, long precision) throws IOException, NumberFormatException, IllegalArgumentException, ApfloatRuntimeException {
        this(ApfloatHelper.createApfloat(in, precision, false));
    }

    public Apfloat(PushbackReader in, long precision, int radix) throws IOException, NumberFormatException, IllegalArgumentException, ApfloatRuntimeException {
        this(ApfloatHelper.createApfloat(in, precision, radix, false));
    }

    public Apfloat(BigInteger value) throws NumberFormatException, ApfloatRuntimeException {
        this(ApfloatHelper.createApfloat(value));
    }

    public Apfloat(BigInteger value, long precision) throws NumberFormatException, IllegalArgumentException, ApfloatRuntimeException {
        this(ApfloatHelper.createApfloat(value, precision));
    }

    public Apfloat(BigInteger value, long precision, int radix) throws NumberFormatException, IllegalArgumentException, ApfloatRuntimeException {
        this(ApfloatHelper.createApfloat(value, precision, radix));
    }

    public Apfloat(BigDecimal value) throws ApfloatRuntimeException {
        this(ApfloatHelper.createApfloat(value));
    }

    public Apfloat(BigDecimal value, long precision) throws IllegalArgumentException, ApfloatRuntimeException {
        this(ApfloatHelper.createApfloat(value, precision));
    }

    @Override
    public int radix() {
        return this.impl.radix();
    }

    @Override
    public Apfloat real() {
        return this;
    }

    @Override
    public Apfloat imag() {
        return ZEROS[this.radix()];
    }

    @Override
    public long precision() throws ApfloatRuntimeException {
        return this.impl.precision();
    }

    @Override
    public Apfloat precision(long precision) throws IllegalArgumentException, ApfloatRuntimeException {
        ApfloatHelper.checkPrecision(precision);
        return new Apfloat(this.getImpl(precision));
    }

    @Override
    public long scale() throws ApfloatRuntimeException {
        if (this.signum() == 0) {
            return -9223372036854775807L;
        }
        return this.impl.scale();
    }

    @Override
    public long size() throws ApfloatRuntimeException {
        if (this.signum() == 0) {
            return 0L;
        }
        return this.impl.size();
    }

    public int signum() {
        return this.impl.signum();
    }

    @Override
    public boolean isInteger() throws ApfloatRuntimeException {
        return this.signum() == 0 || this.size() <= this.scale();
    }

    public boolean isShort() throws ApfloatRuntimeException {
        return this.impl.isShort();
    }

    @Override
    public Apfloat negate() throws ApfloatRuntimeException {
        return new Apfloat(this.impl.negate());
    }

    public Apfloat add(Apfloat x) throws ApfloatRuntimeException {
        if (x.signum() == 0) {
            return this;
        }
        if (this.signum() == 0) {
            return x;
        }
        return this.addOrSubtract(x, false);
    }

    public Apfloat subtract(Apfloat x) throws ApfloatRuntimeException {
        if (x.signum() == 0) {
            return this;
        }
        if (this.signum() == 0) {
            ApfloatImpl impl = x.getImpl();
            impl = impl.negate();
            return new Apfloat(impl);
        }
        return this.addOrSubtract(x, true);
    }

    private Apfloat addOrSubtract(Apfloat x, boolean subtract) throws ApfloatRuntimeException {
        ApfloatImpl impl;
        long[] precisions = ApfloatHelper.getMatchingPrecisions(this, x);
        if (precisions[0] == 0L) {
            impl = x.getImpl(precisions[1]);
            if (subtract) {
                impl = impl.negate();
            }
        } else if (precisions[1] == 0L) {
            impl = this.getImpl(precisions[0]);
        } else {
            impl = this.getImpl(precisions[0]);
            ApfloatImpl xImpl = x.getImpl(precisions[1]);
            impl = impl.addOrSubtract(xImpl, subtract);
        }
        return new Apfloat(impl);
    }

    public Apfloat multiply(Apfloat x) throws ApfloatRuntimeException {
        if (this.signum() == 0) {
            return this;
        }
        if (x.signum() == 0) {
            return x;
        }
        if (this.equals(ONE)) {
            return x.precision(Math.min(this.precision(), x.precision()));
        }
        if (x.equals(ONE)) {
            return this.precision(Math.min(this.precision(), x.precision()));
        }
        long targetPrecision = Math.min(this.precision(), x.precision());
        ApfloatImpl thisImpl = this.getImpl(targetPrecision);
        ApfloatImpl xImpl = x.getImpl(targetPrecision);
        ApfloatImpl impl = thisImpl.multiply(xImpl);
        return new Apfloat(impl);
    }

    public Apfloat divide(Apfloat x) throws ArithmeticException, ApfloatRuntimeException {
        if (x.signum() == 0) {
            throw new ArithmeticException(this.signum() == 0 ? "Zero divided by zero" : "Division by zero");
        }
        if (this.signum() == 0) {
            return this;
        }
        if (x.equals(ONE)) {
            return this.precision(Math.min(this.precision(), x.precision()));
        }
        long targetPrecision = Math.min(this.precision(), x.precision());
        if (x.isShort()) {
            ApfloatImpl thisImpl = this.getImpl(targetPrecision);
            ApfloatImpl xImpl = x.getImpl(targetPrecision);
            ApfloatImpl impl = thisImpl.divideShort(xImpl);
            return new Apfloat(impl);
        }
        Apfloat inverse = ApfloatMath.inverseRoot(x, 1L, targetPrecision);
        return this.multiply(inverse);
    }

    public Apfloat mod(Apfloat x) throws ApfloatRuntimeException {
        return ApfloatMath.fmod(this, x);
    }

    public Apint floor() throws ApfloatRuntimeException {
        if (this.signum() >= 0) {
            return new Apint(new Apfloat(this.impl.absFloor()));
        }
        return new Apint(new Apfloat(this.impl.absCeil()));
    }

    public Apint ceil() throws ApfloatRuntimeException {
        if (this.signum() >= 0) {
            return new Apint(new Apfloat(this.impl.absCeil()));
        }
        return new Apint(new Apfloat(this.impl.absFloor()));
    }

    public Apint truncate() throws ApfloatRuntimeException {
        return new Apint(new Apfloat(this.impl.absFloor()));
    }

    public Apfloat frac() throws ApfloatRuntimeException {
        return new Apfloat(this.impl.frac());
    }

    @Override
    public double doubleValue() {
        int targetPrecision = ApfloatHelper.getDoublePrecision(this.radix());
        ApfloatImpl impl = this.getImpl(targetPrecision);
        return impl.doubleValue();
    }

    @Override
    public float floatValue() {
        return (float)this.doubleValue();
    }

    @Override
    public byte byteValue() {
        long longValue = this.longValue();
        return (byte)Math.min(Math.max(longValue, -128L), 127L);
    }

    @Override
    public short shortValue() {
        long longValue = this.longValue();
        return (short)Math.min(Math.max(longValue, -32768L), 32767L);
    }

    @Override
    public int intValue() {
        long longValue = this.longValue();
        return (int)Math.min(Math.max(longValue, Integer.MIN_VALUE), Integer.MAX_VALUE);
    }

    @Override
    public long longValue() {
        int targetPrecision = ApfloatHelper.getLongPrecision(this.radix());
        ApfloatImpl impl = this.getImpl(targetPrecision);
        return impl.longValue();
    }

    @Override
    public long longValueExact() throws ArithmeticException {
        long value = this.longValue();
        if (!new Apint(value, this.radix()).equals(this.truncate())) {
            throw new ArithmeticException("Out of range");
        }
        return value;
    }

    public long equalDigits(Apfloat x) throws ApfloatRuntimeException {
        long targetPrecision = Math.min(this.precision(), x.precision());
        ApfloatImpl thisImpl = this.getImpl(targetPrecision);
        ApfloatImpl xImpl = x.getImpl(targetPrecision);
        return thisImpl.equalDigits(xImpl);
    }

    @Override
    public Apfloat toRadix(int radix) throws NumberFormatException, ApfloatRuntimeException {
        return RadixConversionHelper.toRadix(this, radix);
    }

    @Override
    public int compareTo(Apfloat x) {
        if (x.preferCompare(this)) {
            return -x.compareTo(this);
        }
        return this.getImpl().compareTo(x.getImpl());
    }

    public boolean preferCompare(Apfloat x) {
        return false;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Apfloat) {
            Apfloat x = (Apfloat)obj;
            if (x.preferCompare(this)) {
                return x.equals(this);
            }
            return this.getImpl().equals(x.getImpl());
        }
        return super.equals(obj);
    }

    public boolean test(Apfloat x) throws ApfloatRuntimeException {
        if (x.preferCompare(this)) {
            return x.test(this);
        }
        return this.signum() == x.signum() && this.scale() == x.scale() && this.size() == x.size();
    }

    @Override
    public int hashCode() {
        return this.impl.hashCode();
    }

    @Override
    public String toString(boolean pretty) throws ApfloatRuntimeException {
        return this.impl.toString(pretty);
    }

    @Override
    public void writeTo(Writer out, boolean pretty) throws IOException, ApfloatRuntimeException {
        this.impl.writeTo(out, pretty);
    }

    @Override
    public void formatTo(Formatter formatter, int flags, int width, int precision) {
        Apfloat x = precision == -1 ? this : ApfloatHelper.limitPrecision(this, (long)precision);
        try {
            Writer out = FormattingHelper.wrapAppendableWriter(formatter.out());
            out = FormattingHelper.wrapLocalizeWriter(out, formatter, this.radix(), (flags & 2) == 2);
            if (width == -1) {
                x.writeTo(out, (flags & 4) == 4);
            } else {
                out = FormattingHelper.wrapPadWriter(out, (flags & 1) == 1);
                x.writeTo(out, (flags & 4) == 4);
                FormattingHelper.finishPad(out, width);
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    protected ApfloatImpl getImpl(long precision) throws ApfloatRuntimeException {
        if (precision == this.precision()) {
            return this.impl;
        }
        return this.impl.precision(precision);
    }

    Apint roundAway() throws ApfloatRuntimeException {
        return new Apint(new Apfloat(this.impl.absCeil()));
    }

    Apfloat scale(long scale) {
        return ApfloatMath.scale(this, scale);
    }

    Apfloat abs() {
        return ApfloatMath.abs(this);
    }

    int compareToHalf() {
        return RoundingHelper.compareToHalf(this);
    }

    private ApfloatImpl getImpl() throws ApfloatRuntimeException {
        long precision = this.precision();
        return this.getImpl(precision);
    }
}

