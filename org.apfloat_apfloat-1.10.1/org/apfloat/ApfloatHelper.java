/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.apfloat.Apcomplex;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatContext;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.Apint;
import org.apfloat.ApintMath;
import org.apfloat.Aprational;
import org.apfloat.InfiniteExpansionException;
import org.apfloat.LossOfPrecisionException;
import org.apfloat.spi.ApfloatBuilder;
import org.apfloat.spi.ApfloatImpl;
import org.apfloat.spi.RadixConstants;
import org.apfloat.spi.Util;

class ApfloatHelper {
    private ApfloatHelper() {
    }

    public static ApfloatImpl createApfloat(String value, boolean isInteger) throws NumberFormatException, ApfloatRuntimeException {
        long precision = isInteger ? Long.MAX_VALUE : Long.MIN_VALUE;
        int radix = ApfloatHelper.getDefaultRadix();
        return ApfloatHelper.implCreateApfloat(value, precision, radix, isInteger);
    }

    public static ApfloatImpl createApfloat(String value, long precision, boolean isInteger) throws NumberFormatException, IllegalArgumentException, ApfloatRuntimeException {
        int radix = ApfloatHelper.getDefaultRadix();
        return ApfloatHelper.createApfloat(value, precision, radix, isInteger);
    }

    public static ApfloatImpl createApfloat(String value, long precision, int radix, boolean isInteger) throws NumberFormatException, IllegalArgumentException, ApfloatRuntimeException {
        if (precision != Long.MIN_VALUE) {
            ApfloatHelper.checkPrecision(precision);
        }
        return ApfloatHelper.implCreateApfloat(value, precision, radix, isInteger);
    }

    private static ApfloatImpl implCreateApfloat(String value, long precision, int radix, boolean isInteger) throws NumberFormatException, ApfloatRuntimeException {
        ApfloatBuilder factory = ApfloatHelper.getApfloatBuilder();
        return factory.createApfloat(value, precision, radix, isInteger);
    }

    public static ApfloatImpl createApfloat(long value) throws NumberFormatException, ApfloatRuntimeException {
        int radix = ApfloatHelper.getDefaultRadix();
        return ApfloatHelper.implCreateApfloat(value, Long.MAX_VALUE, radix);
    }

    public static ApfloatImpl createApfloat(long value, long precision) throws NumberFormatException, IllegalArgumentException, ApfloatRuntimeException {
        int radix = ApfloatHelper.getDefaultRadix();
        return ApfloatHelper.createApfloat(value, precision, radix);
    }

    public static ApfloatImpl createApfloat(long value, long precision, int radix) throws NumberFormatException, IllegalArgumentException, ApfloatRuntimeException {
        precision = precision == Long.MIN_VALUE ? Long.MAX_VALUE : precision;
        ApfloatHelper.checkPrecision(precision);
        return ApfloatHelper.implCreateApfloat(value, precision, radix);
    }

    private static ApfloatImpl implCreateApfloat(long value, long precision, int radix) throws NumberFormatException, ApfloatRuntimeException {
        ApfloatBuilder factory = ApfloatHelper.getApfloatBuilder();
        return factory.createApfloat(value, precision, radix);
    }

    public static ApfloatImpl createApfloat(float value) throws NumberFormatException, ApfloatRuntimeException {
        int radix = ApfloatHelper.getDefaultRadix();
        int precision = ApfloatHelper.getFloatPrecision(radix);
        return ApfloatHelper.implCreateApfloat(value, (long)precision, radix);
    }

    public static ApfloatImpl createApfloat(float value, long precision) throws NumberFormatException, IllegalArgumentException, ApfloatRuntimeException {
        int radix = ApfloatHelper.getDefaultRadix();
        return ApfloatHelper.createApfloat(value, precision, radix);
    }

    public static ApfloatImpl createApfloat(float value, long precision, int radix) throws NumberFormatException, IllegalArgumentException, ApfloatRuntimeException {
        precision = precision == Long.MIN_VALUE ? (long)ApfloatHelper.getFloatPrecision(radix) : precision;
        ApfloatHelper.checkPrecision(precision);
        return ApfloatHelper.implCreateApfloat(value, precision, radix);
    }

    public static ApfloatImpl createApfloat(double value) throws NumberFormatException, ApfloatRuntimeException {
        int radix = ApfloatHelper.getDefaultRadix();
        int precision = ApfloatHelper.getDoublePrecision(radix);
        return ApfloatHelper.implCreateApfloat(value, (long)precision, radix);
    }

    public static ApfloatImpl createApfloat(double value, long precision) throws NumberFormatException, IllegalArgumentException, ApfloatRuntimeException {
        int radix = ApfloatHelper.getDefaultRadix();
        return ApfloatHelper.createApfloat(value, precision, radix);
    }

    public static ApfloatImpl createApfloat(double value, long precision, int radix) throws NumberFormatException, IllegalArgumentException, ApfloatRuntimeException {
        precision = precision == Long.MIN_VALUE ? (long)ApfloatHelper.getDoublePrecision(radix) : precision;
        ApfloatHelper.checkPrecision(precision);
        return ApfloatHelper.implCreateApfloat(value, precision, radix);
    }

    private static ApfloatImpl implCreateApfloat(double value, long precision, int radix) throws NumberFormatException, ApfloatRuntimeException {
        ApfloatBuilder factory = ApfloatHelper.getApfloatBuilder();
        return factory.createApfloat(value, precision, radix);
    }

    public static ApfloatImpl createApfloat(PushbackReader in, boolean isInteger) throws IOException, NumberFormatException, ApfloatRuntimeException {
        int radix = ApfloatHelper.getDefaultRadix();
        return ApfloatHelper.implCreateApfloat(in, Long.MIN_VALUE, radix, isInteger);
    }

    public static ApfloatImpl createApfloat(PushbackReader in, long precision, boolean isInteger) throws IOException, NumberFormatException, IllegalArgumentException, ApfloatRuntimeException {
        int radix = ApfloatHelper.getDefaultRadix();
        return ApfloatHelper.createApfloat(in, precision, radix, isInteger);
    }

    public static ApfloatImpl createApfloat(PushbackReader in, long precision, int radix, boolean isInteger) throws IOException, NumberFormatException, IllegalArgumentException, ApfloatRuntimeException {
        if (precision != Long.MIN_VALUE) {
            ApfloatHelper.checkPrecision(precision);
        }
        return ApfloatHelper.implCreateApfloat(in, precision, radix, isInteger);
    }

    private static ApfloatImpl implCreateApfloat(PushbackReader in, long precision, int radix, boolean isInteger) throws IOException, NumberFormatException, ApfloatRuntimeException {
        ApfloatBuilder factory = ApfloatHelper.getApfloatBuilder();
        return factory.createApfloat(in, precision, radix, isInteger);
    }

    public static ApfloatImpl createApfloat(BigInteger value) throws NumberFormatException, ApfloatRuntimeException {
        int radix = ApfloatHelper.getDefaultRadix();
        return ApfloatHelper.createApfloat(value, Long.MAX_VALUE, radix);
    }

    public static ApfloatImpl createApfloat(BigInteger value, long precision) throws NumberFormatException, IllegalArgumentException, ApfloatRuntimeException {
        int radix = ApfloatHelper.getDefaultRadix();
        return ApfloatHelper.createApfloat(value, precision, radix);
    }

    public static ApfloatImpl createApfloat(BigInteger value, long precision, int radix) throws NumberFormatException, IllegalArgumentException, ApfloatRuntimeException {
        Apfloat a;
        if (precision != Long.MIN_VALUE) {
            ApfloatHelper.checkPrecision(precision);
        }
        ApfloatHelper.checkRadix(radix);
        try {
            a = new Apfloat(ApfloatHelper.createApfloat(ApfloatHelper.toPushbackReader(value), Long.MAX_VALUE, 16, true));
        }
        catch (IOException ioe) {
            throw new ApfloatRuntimeException("Should not occur", ioe);
        }
        precision = precision == Long.MIN_VALUE ? Long.MAX_VALUE : precision;
        return a.toRadix(radix).getImpl(precision);
    }

    public static ApfloatImpl createApfloat(BigDecimal value) throws ApfloatRuntimeException {
        return ApfloatHelper.implCreateApfloat(value.toString(), Long.MIN_VALUE, 10, false);
    }

    public static ApfloatImpl createApfloat(BigDecimal value, long precision) throws IllegalArgumentException, ApfloatRuntimeException {
        return ApfloatHelper.createApfloat(value.toString(), precision, 10, false);
    }

    public static boolean readMatch(PushbackReader in, int c) throws IOException {
        int i = in.read();
        if (i != c) {
            if (i != -1) {
                in.unread(i);
            }
            return false;
        }
        return true;
    }

    public static void extractWhitespace(PushbackReader in) throws IOException {
        int c;
        while (Character.isWhitespace((char)(c = in.read()))) {
        }
        if (c != -1) {
            in.unread(c);
        }
    }

    public static long[] getMatchingPrecisions(Apfloat x, Apfloat y) throws ApfloatRuntimeException {
        long yScale;
        if (x.signum() == 0 || y.signum() == 0) {
            return new long[]{0L, 0L};
        }
        long xPrec = x.precision();
        long yPrec = y.precision();
        long xScale = x.scale();
        long maxScale = Math.max(xScale, yScale = y.scale());
        long xScaleDiff = maxScale - xScale < 0L ? Long.MAX_VALUE : maxScale - xScale;
        long yScaleDiff = maxScale - yScale < 0L ? Long.MAX_VALUE : maxScale - yScale;
        long maxPrec = Math.min(Util.ifFinite(xPrec, xPrec + xScaleDiff), Util.ifFinite(yPrec, yPrec + yScaleDiff));
        long destXPrec = maxPrec - xScaleDiff <= 0L ? 0L : Util.ifFinite(maxPrec, maxPrec - xScaleDiff);
        long destYPrec = maxPrec - yScaleDiff <= 0L ? 0L : Util.ifFinite(maxPrec, maxPrec - yScaleDiff);
        return new long[]{destXPrec, destYPrec};
    }

    public static long[] getMatchingPrecisions(Apfloat a, Apfloat b, Apfloat c, Apfloat d) throws ApfloatRuntimeException {
        long cdScale;
        long cdPrec;
        long abPrec = a.signum() == 0 || b.signum() == 0 ? 0L : Math.min(a.precision(), b.precision());
        long l = cdPrec = c.signum() == 0 || d.signum() == 0 ? 0L : Math.min(c.precision(), d.precision());
        if (abPrec == 0L || cdPrec == 0L) {
            return new long[]{abPrec, cdPrec, Math.max(abPrec, cdPrec)};
        }
        long abScale = a.scale() + b.scale();
        long maxScale = Math.max(abScale, cdScale = c.scale() + d.scale());
        long abScaleDiff = maxScale - abScale < 0L ? Long.MAX_VALUE : maxScale - abScale;
        long cdScaleDiff = maxScale - cdScale < 0L ? Long.MAX_VALUE : maxScale - cdScale;
        long maxPrec = Math.min(Util.ifFinite(abPrec, abPrec + abScaleDiff), Util.ifFinite(cdPrec, cdPrec + cdScaleDiff));
        long destAbPrec = maxPrec - abScaleDiff <= 0L ? 0L : Util.ifFinite(maxPrec, maxPrec - abScaleDiff + 1L);
        long destCdPrec = maxPrec - cdScaleDiff <= 0L ? 0L : Util.ifFinite(maxPrec, maxPrec - cdScaleDiff + 1L);
        return new long[]{destAbPrec, destCdPrec, maxPrec};
    }

    public static void checkPrecision(long precision) throws IllegalArgumentException {
        if (precision <= 0L) {
            throw new IllegalArgumentException("Precision " + precision + " is not positive");
        }
    }

    public static void checkRadix(int radix) throws NumberFormatException {
        if (radix < 2 || radix > 36) {
            throw new NumberFormatException("Invalid radix " + radix + "; radix must be between " + 2 + " and " + 36);
        }
    }

    private static void checkPowPrecision(long targetPrecision) throws InfiniteExpansionException {
        if (targetPrecision == Long.MAX_VALUE) {
            throw new InfiniteExpansionException("Cannot calculate power to infinite precision");
        }
    }

    private static Apcomplex checkPowBasic(Apcomplex z, Apcomplex w, long targetPrecision) throws ArithmeticException, ApfloatRuntimeException {
        if (w.real().signum() == 0 && w.imag().signum() == 0) {
            if (z.real().signum() == 0 && z.imag().signum() == 0) {
                throw new ArithmeticException("Zero to power zero");
            }
            return new Apcomplex(new Apfloat(1L, Long.MAX_VALUE, z.radix()));
        }
        if (z.real().signum() == 0 && z.imag().signum() == 0 || z.equals(Apcomplex.ONE) || w.equals(Apcomplex.ONE)) {
            return z.precision(targetPrecision);
        }
        return null;
    }

    public static Apcomplex checkPow(Apcomplex z, Apcomplex w, long targetPrecision) throws ArithmeticException, ApfloatRuntimeException {
        Apcomplex result = ApfloatHelper.checkPowBasic(z, w, targetPrecision);
        if (result != null) {
            return result;
        }
        ApfloatHelper.checkPowPrecision(targetPrecision);
        return null;
    }

    public static Apfloat checkPow(Apfloat x, Apfloat y, long targetPrecision) throws ArithmeticException, ApfloatRuntimeException {
        Apcomplex result = ApfloatHelper.checkPowBasic(x, y, targetPrecision);
        if (result != null) {
            return result.real();
        }
        if (x.signum() < 0) {
            throw new ArithmeticException("Power of negative number; result would be complex");
        }
        ApfloatHelper.checkPowPrecision(targetPrecision);
        return null;
    }

    public static int getFloatPrecision(int radix) {
        assert (radix > 0);
        return RadixConstants.FLOAT_PRECISION[radix];
    }

    public static int getDoublePrecision(int radix) {
        assert (radix > 0);
        return RadixConstants.DOUBLE_PRECISION[radix];
    }

    public static int getLongPrecision(int radix) {
        assert (radix > 0);
        return RadixConstants.LONG_PRECISION[radix];
    }

    public static Apfloat limitPrecision(Apfloat x, long precision) throws ApfloatRuntimeException {
        return x.precision(Math.min(x.precision(), precision));
    }

    public static Apfloat ensurePrecision(Apfloat x, long precision) throws ApfloatRuntimeException {
        return x.precision(Math.max(x.precision(), precision));
    }

    public static long extendPrecision(long precision, long extraPrecision) {
        return Util.ifFinite(precision, precision + extraPrecision);
    }

    public static long extendPrecision(long precision) {
        return ApfloatHelper.extendPrecision(precision, 20L);
    }

    public static long reducePrecision(long precision, long extraPrecision) throws ApfloatRuntimeException {
        if ((precision -= extraPrecision) <= 0L) {
            throw new LossOfPrecisionException("Complete loss of precision");
        }
        return precision;
    }

    public static long reducePrecision(long precision) throws ApfloatRuntimeException {
        return ApfloatHelper.reducePrecision(precision, 20L);
    }

    public static Apfloat extendPrecision(Apfloat x) throws ApfloatRuntimeException {
        return x.precision(ApfloatHelper.extendPrecision(x.precision()));
    }

    public static Apfloat extendPrecision(Apfloat x, long extraPrecision) throws ApfloatRuntimeException {
        return x.precision(ApfloatHelper.extendPrecision(x.precision(), extraPrecision));
    }

    public static Apfloat reducePrecision(Apfloat x) throws ApfloatRuntimeException {
        return x.precision(ApfloatHelper.reducePrecision(x.precision()));
    }

    public static Apfloat reducePrecision(Apfloat x, long extraPrecision) throws ApfloatRuntimeException {
        return x.precision(ApfloatHelper.reducePrecision(x.precision(), extraPrecision));
    }

    public static Apcomplex setPrecision(Apcomplex z, long precision) throws ApfloatRuntimeException {
        if (z.real().signum() == 0) {
            return new Apcomplex(z.real(), z.imag().precision(precision));
        }
        if (z.imag().signum() == 0) {
            return new Apcomplex(z.real().precision(precision), z.imag());
        }
        long precisionChange = precision - z.precision();
        long realPrecision = z.real().precision();
        long imagPrecision = z.imag().precision();
        long newRealPrecision = Util.ifFinite(realPrecision, realPrecision + precisionChange);
        long newImagPrecision = Util.ifFinite(imagPrecision, imagPrecision + precisionChange);
        if (precisionChange < 0L) {
            if (realPrecision + precisionChange <= 0L) {
                return new Apcomplex(Apfloat.ZEROS[z.radix()], z.imag().precision(precision));
            }
            if (imagPrecision + precisionChange <= 0L) {
                return new Apcomplex(z.real().precision(precision), Apfloat.ZEROS[z.radix()]);
            }
        }
        return new Apcomplex(z.real().precision(newRealPrecision), z.imag().precision(newImagPrecision));
    }

    public static Apcomplex limitPrecision(Apcomplex z, long precision) throws ApfloatRuntimeException {
        return new Apcomplex(ApfloatHelper.limitPrecision(z.real(), precision), ApfloatHelper.limitPrecision(z.imag(), precision));
    }

    public static Apcomplex ensurePrecision(Apcomplex z, long precision) throws ApfloatRuntimeException {
        return new Apcomplex(ApfloatHelper.ensurePrecision(z.real(), precision), ApfloatHelper.ensurePrecision(z.imag(), precision));
    }

    public static Apcomplex extendPrecision(Apcomplex z) throws ApfloatRuntimeException {
        return new Apcomplex(ApfloatHelper.extendPrecision(z.real()), ApfloatHelper.extendPrecision(z.imag()));
    }

    public static Apcomplex extendPrecision(Apcomplex z, long extraPrecision) throws ApfloatRuntimeException {
        return new Apcomplex(ApfloatHelper.extendPrecision(z.real(), extraPrecision), ApfloatHelper.extendPrecision(z.imag(), extraPrecision));
    }

    public static Apcomplex reducePrecision(Apcomplex z) throws ApfloatRuntimeException {
        return new Apcomplex(ApfloatHelper.reducePrecision(z.real()), ApfloatHelper.reducePrecision(z.imag()));
    }

    public static Apcomplex reducePrecision(Apcomplex z, long extraPrecision) throws ApfloatRuntimeException {
        return new Apcomplex(ApfloatHelper.reducePrecision(z.real(), extraPrecision), ApfloatHelper.reducePrecision(z.imag(), extraPrecision));
    }

    public static long size(Aprational x) throws ApfloatRuntimeException {
        return Math.max(x.numerator().size(), x.denominator().size());
    }

    public static BigInteger toBigInteger(Apint x) {
        assert (x.signum() != 0);
        Apint a = ApintMath.abs(x.toRadix(16));
        long scale = a.scale();
        long byteCount = scale + 1L >> 1;
        if (byteCount > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Maximum array size exceeded");
        }
        final byte[] bytes = new byte[(int)byteCount];
        final boolean startHi = (scale & 1L) == 0L;
        try {
            a.writeTo(new Writer(){
                private int b;
                private int bytePosition;
                private boolean hi;
                {
                    this.hi = startHi;
                }

                @Override
                public void write(int c) {
                    c = Character.digit(c, 16);
                    if (this.hi) {
                        this.b = c << 4;
                    } else {
                        this.b += c & 0xF;
                        bytes[this.bytePosition] = (byte)this.b;
                        ++this.bytePosition;
                    }
                    this.hi = !this.hi;
                }

                @Override
                public void write(char[] cbuf, int off, int len) {
                    for (int i = 0; i < len; ++i) {
                        this.write(cbuf[off + i]);
                    }
                }

                @Override
                public void close() {
                }

                @Override
                public void flush() {
                }
            });
        }
        catch (IOException ioe) {
            throw new ApfloatRuntimeException("Should not occur", ioe);
        }
        BigInteger b = new BigInteger(x.signum(), bytes);
        return b;
    }

    public static PushbackReader toPushbackReader(BigInteger x) throws IOException {
        byte[] bytes = x.abs().toByteArray();
        final int startB = x.signum() < 0 ? 45 : -1;
        ByteArrayInputStream in = new ByteArrayInputStream(bytes){
            private int b;
            {
                super(arg0);
                this.b = startB;
            }

            @Override
            public int read() {
                int c;
                if (this.b == -1) {
                    this.b = super.read();
                    if (this.b == -1) {
                        c = -1;
                    } else {
                        c = Character.forDigit(this.b >> 4, 16);
                        this.b = Character.forDigit(this.b & 0xF, 16);
                    }
                } else {
                    c = this.b;
                    this.b = -1;
                }
                return c;
            }

            @Override
            public int read(byte[] b, int off, int len) {
                int i;
                for (i = 0; i < len; ++i) {
                    int c = this.read();
                    if (c == -1) {
                        i = i == 0 ? -1 : i;
                        break;
                    }
                    b[i + off] = (byte)c;
                }
                return i;
            }
        };
        return new PushbackReader(new InputStreamReader((InputStream)in, "ISO-8859-1"));
    }

    private static int getDefaultRadix() throws NumberFormatException {
        ApfloatContext ctx = ApfloatContext.getContext();
        return ctx.getDefaultRadix();
    }

    private static ApfloatBuilder getApfloatBuilder() {
        ApfloatContext ctx = ApfloatContext.getContext();
        return ctx.getBuilderFactory().getApfloatBuilder();
    }
}

