/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Serializable;
import java.io.Writer;
import java.util.Formattable;
import java.util.Formatter;
import org.apfloat.ApcomplexMath;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatHelper;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.Apint;
import org.apfloat.FormattingHelper;

public class Apcomplex
extends Number
implements Formattable,
Serializable {
    public static final Apint ZERO = new Apint(0L);
    public static final Apint ONE = new Apint(1L);
    public static final Apcomplex I = new Apcomplex(ZERO, ONE);
    public static final long INFINITE = Long.MAX_VALUE;
    public static final long DEFAULT = Long.MIN_VALUE;
    static final int EXTRA_PRECISION = 20;
    static final Apint[] ZEROS = new Apint[37];
    static final Apint[] ONES = new Apint[37];
    private static final long serialVersionUID = 3642932980384250551L;
    private Apfloat real;
    private Apfloat imag;

    protected Apcomplex() {
    }

    public Apcomplex(Apfloat real) {
        this(real, ZEROS[real.radix()]);
    }

    public Apcomplex(Apfloat real, Apfloat imag) throws IllegalArgumentException {
        if (real.signum() != 0 && imag.signum() != 0 && real.radix() != imag.radix()) {
            throw new IllegalArgumentException("Real part and imaginary part must have the same radix");
        }
        this.real = real;
        this.imag = imag;
    }

    public Apcomplex(String value) throws NumberFormatException, ApfloatRuntimeException {
        if (!value.startsWith("(")) {
            this.real = new Apfloat(value);
            this.imag = ZEROS[this.real.radix()];
            return;
        }
        if (!value.endsWith(")")) {
            throw new NumberFormatException("Missing end parenthesis");
        }
        int index = value.indexOf(44);
        if (index < 0) {
            this.real = new Apfloat(value.substring(1, value.length() - 1).trim());
            this.imag = ZEROS[this.real.radix()];
            return;
        }
        this.real = new Apfloat(value.substring(1, index).trim());
        this.imag = new Apfloat(value.substring(index + 1, value.length() - 1).trim());
    }

    public Apcomplex(PushbackReader in) throws IOException, NumberFormatException, ApfloatRuntimeException {
        if (!ApfloatHelper.readMatch(in, 40)) {
            this.real = new Apfloat(in);
            this.imag = ZEROS[this.real.radix()];
            return;
        }
        ApfloatHelper.extractWhitespace(in);
        this.real = new Apfloat(in);
        ApfloatHelper.extractWhitespace(in);
        if (ApfloatHelper.readMatch(in, 44)) {
            ApfloatHelper.extractWhitespace(in);
            this.imag = new Apfloat(in);
        } else {
            this.imag = ZEROS[this.real.radix()];
        }
        ApfloatHelper.extractWhitespace(in);
        if (!ApfloatHelper.readMatch(in, 41)) {
            throw new NumberFormatException("Missing end parenthesis");
        }
    }

    public int radix() {
        return this.real().signum() == 0 ? (this.imag().signum() == 0 ? this.real().radix() : this.imag().radix()) : this.real().radix();
    }

    public Apfloat real() {
        return this.real;
    }

    public Apfloat imag() {
        return this.imag;
    }

    public Apcomplex conj() throws ApfloatRuntimeException {
        return new Apcomplex(this.real(), this.imag().negate());
    }

    public long precision() throws ApfloatRuntimeException {
        if (this.real().signum() == 0 || this.imag().signum() == 0) {
            return Math.min(this.real().precision(), this.imag().precision());
        }
        long[] precisions = ApfloatHelper.getMatchingPrecisions(this.real(), this.imag());
        return Math.max(precisions[0], precisions[1]);
    }

    public Apcomplex precision(long precision) throws IllegalArgumentException, ApfloatRuntimeException {
        ApfloatHelper.checkPrecision(precision);
        Apcomplex z = new Apcomplex(this.real().precision(precision), this.imag().precision(precision));
        if (this.real().signum() == 0 || this.imag().signum() == 0) {
            return z;
        }
        long[] precisions = ApfloatHelper.getMatchingPrecisions(z.real(), z.imag());
        long realPrecision = precisions[0];
        long imagPrecision = precisions[1];
        return new Apcomplex(realPrecision > 0L ? z.real().precision(realPrecision) : ZEROS[this.real().radix()], imagPrecision > 0L ? z.imag().precision(imagPrecision) : ZEROS[this.imag().radix()]);
    }

    public long scale() throws ApfloatRuntimeException {
        return Math.max(this.real().scale(), this.imag().scale());
    }

    public long size() throws ApfloatRuntimeException {
        return Math.max(this.real().size(), this.imag().size());
    }

    public boolean isInteger() throws ApfloatRuntimeException {
        return this.imag().signum() == 0 && this.real().isInteger();
    }

    public Apcomplex negate() throws ApfloatRuntimeException {
        return new Apcomplex(this.real().negate(), this.imag().negate());
    }

    public Apcomplex add(Apcomplex z) throws ApfloatRuntimeException {
        return new Apcomplex(this.real().add(z.real()), this.imag().add(z.imag()));
    }

    public Apcomplex subtract(Apcomplex z) throws ApfloatRuntimeException {
        return new Apcomplex(this.real().subtract(z.real()), this.imag().subtract(z.imag()));
    }

    public Apcomplex multiply(Apcomplex z) throws ApfloatRuntimeException {
        return new Apcomplex(ApfloatMath.multiplySubtract(this.real(), z.real(), this.imag(), z.imag()), ApfloatMath.multiplyAdd(this.real(), z.imag(), this.imag(), z.real()));
    }

    public Apcomplex divide(Apcomplex z) throws ArithmeticException, ApfloatRuntimeException {
        Apfloat tmpImag;
        Apfloat tmpReal;
        if (z.real().signum() == 0 && z.imag().signum() == 0) {
            throw new ArithmeticException(this.real().signum() == 0 && this.imag().signum() == 0 ? "Zero divided by zero" : "Division by zero");
        }
        if (z.real().signum() == 0) {
            z = new Apcomplex(z.imag(), z.real().negate());
            tmpReal = this.imag();
            tmpImag = this.real().negate();
        } else {
            tmpReal = this.real();
            tmpImag = this.imag();
        }
        if (tmpImag.signum() == 0) {
            if (tmpReal.signum() == 0) {
                return this;
            }
            if (z.imag().signum() == 0) {
                return tmpReal.divide(z.real());
            }
        } else if (z.imag().signum() == 0) {
            if (z.real().equals(ONE)) {
                return new Apcomplex(tmpReal.precision(Math.min(tmpReal.precision(), z.real().precision())), tmpImag.precision(Math.min(tmpImag.precision(), z.real().precision())));
            }
            if (z.real().isShort()) {
                return new Apcomplex(tmpReal.divide(z.real()), tmpImag.divide(z.real()));
            }
            long precision = Math.min(this.precision(), z.real().precision());
            Apfloat inverse = ApfloatMath.inverseRoot(z.real(), 1L, precision);
            return new Apcomplex(tmpReal.multiply(inverse), tmpImag.multiply(inverse));
        }
        long precision = Math.min(this.precision(), z.precision());
        Apcomplex zApprox = new Apcomplex(z.real().precision(Math.min(precision, z.real().precision())), z.imag().precision(Math.min(precision, z.imag().precision())));
        return this.multiply(z.conj()).divide(ApcomplexMath.norm(zApprox));
    }

    @Override
    public double doubleValue() {
        return this.real().doubleValue();
    }

    @Override
    public float floatValue() {
        return this.real().floatValue();
    }

    @Override
    public byte byteValue() {
        return this.real().byteValue();
    }

    @Override
    public short shortValue() {
        return this.real().shortValue();
    }

    @Override
    public int intValue() {
        return this.real().intValue();
    }

    @Override
    public long longValue() {
        return this.real().longValue();
    }

    public byte byteValueExact() throws ArithmeticException {
        long value = this.longValueExact();
        if (value > 127L || value < -128L) {
            throw new ArithmeticException("Out of range");
        }
        return (byte)value;
    }

    public short shortValueExact() throws ArithmeticException {
        long value = this.longValueExact();
        if (value > 32767L || value < -32768L) {
            throw new ArithmeticException("Out of range");
        }
        return (short)value;
    }

    public int intValueExact() throws ArithmeticException {
        long value = this.longValueExact();
        if (value > Integer.MAX_VALUE || value < Integer.MIN_VALUE) {
            throw new ArithmeticException("Out of range");
        }
        return (int)value;
    }

    public long longValueExact() throws ArithmeticException {
        if (this.imag().signum() != 0) {
            throw new ArithmeticException("Out of range");
        }
        return this.real().longValueExact();
    }

    public long equalDigits(Apcomplex z) throws ApfloatRuntimeException {
        if (this.real().signum() == 0 && this.imag().signum() == 0 && z.real().signum() == 0 && z.imag().signum() == 0) {
            return Long.MAX_VALUE;
        }
        long minScale = Math.min(this.scale(), z.scale());
        long maxScale = Math.max(this.scale(), z.scale());
        if (maxScale - 1L > minScale) {
            return 0L;
        }
        long realScale = Math.max(this.real().scale(), z.real().scale());
        long imagScale = Math.max(this.imag().scale(), z.imag().scale());
        long realScaleDiff = maxScale - realScale < 0L ? Long.MAX_VALUE : maxScale - realScale;
        long imagScaleDiff = maxScale - imagScale < 0L ? Long.MAX_VALUE : maxScale - imagScale;
        long realEquals = this.real().equalDigits(z.real());
        long imagEquals = this.imag().equalDigits(z.imag());
        return Math.min(realEquals + realScaleDiff < 0L ? Long.MAX_VALUE : realEquals + realScaleDiff, imagEquals + imagScaleDiff < 0L ? Long.MAX_VALUE : imagEquals + imagScaleDiff);
    }

    public Apcomplex toRadix(int radix) throws NumberFormatException, ApfloatRuntimeException {
        return new Apcomplex(this.real().toRadix(radix), this.imag().toRadix(radix));
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Apcomplex) {
            Apcomplex that = (Apcomplex)obj;
            return this.test(that) && this.real().equals(that.real()) && this.imag().equals(that.imag());
        }
        return false;
    }

    public boolean test(Apcomplex z) throws ApfloatRuntimeException {
        return this.real().test(z.real()) && this.imag().test(z.imag());
    }

    public int hashCode() {
        return this.real().hashCode() * 3 + this.imag().hashCode();
    }

    public String toString() {
        return this.toString(false);
    }

    public String toString(boolean pretty) throws ApfloatRuntimeException {
        if (this.imag().signum() == 0) {
            return this.real().toString(pretty);
        }
        return '(' + this.real().toString(pretty) + ", " + this.imag().toString(pretty) + ')';
    }

    public void writeTo(Writer out) throws IOException, ApfloatRuntimeException {
        this.writeTo(out, false);
    }

    public void writeTo(Writer out, boolean pretty) throws IOException, ApfloatRuntimeException {
        if (this.imag().signum() == 0) {
            this.real().writeTo(out, pretty);
        } else {
            out.write(40);
            this.real().writeTo(out, pretty);
            out.write(", ");
            this.imag().writeTo(out, pretty);
            out.write(41);
        }
    }

    @Override
    public void formatTo(Formatter formatter, int flags, int width, int precision) {
        if (this.imag().signum() == 0) {
            this.real().formatTo(formatter, flags, width, precision);
        } else if (width == -1) {
            formatter.format("(", new Object[0]);
            this.real().formatTo(formatter, flags, width, precision);
            formatter.format(", ", new Object[0]);
            this.imag().formatTo(formatter, flags, width, precision);
            formatter.format(")", new Object[0]);
        } else {
            try {
                Writer out = FormattingHelper.wrapAppendableWriter(formatter.out());
                out = FormattingHelper.wrapPadWriter(out, (flags & 1) == 1);
                formatter = new Formatter(out, formatter.locale());
                formatter.format("(", new Object[0]);
                this.real().formatTo(formatter, flags, -1, precision);
                formatter.format(", ", new Object[0]);
                this.imag().formatTo(formatter, flags, -1, precision);
                formatter.format(")", new Object[0]);
                FormattingHelper.finishPad(out, width);
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    static {
        for (int i = 2; i <= 36; ++i) {
            Apcomplex.ZEROS[i] = new Apint(0L, i);
            Apcomplex.ONES[i] = new Apint(1L, i);
        }
        Apcomplex.ZEROS[Apcomplex.ZERO.radix()] = ZERO;
        Apcomplex.ONES[Apcomplex.ONE.radix()] = ONE;
    }
}

