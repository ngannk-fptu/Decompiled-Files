/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.imaging.common;

import java.text.NumberFormat;

public class RationalNumber
extends Number {
    private static final long serialVersionUID = -8412262656468158691L;
    private static final double TOLERANCE = 1.0E-8;
    public final int numerator;
    public final int divisor;

    public RationalNumber(int numerator, int divisor) {
        this.numerator = numerator;
        this.divisor = divisor;
    }

    static RationalNumber factoryMethod(long n, long d) {
        if (n > Integer.MAX_VALUE || n < Integer.MIN_VALUE || d > Integer.MAX_VALUE || d < Integer.MIN_VALUE) {
            while ((n > Integer.MAX_VALUE || n < Integer.MIN_VALUE || d > Integer.MAX_VALUE || d < Integer.MIN_VALUE) && Math.abs(n) > 1L && Math.abs(d) > 1L) {
                n >>= 1;
                d >>= 1;
            }
            if (d == 0L) {
                throw new NumberFormatException("Invalid value, numerator: " + n + ", divisor: " + d);
            }
        }
        long gcd = RationalNumber.gcd(n, d);
        return new RationalNumber((int)(n /= gcd), (int)(d /= gcd));
    }

    private static long gcd(long a, long b) {
        if (b == 0L) {
            return a;
        }
        return RationalNumber.gcd(b, a % b);
    }

    public RationalNumber negate() {
        return new RationalNumber(-this.numerator, this.divisor);
    }

    @Override
    public double doubleValue() {
        return (double)this.numerator / (double)this.divisor;
    }

    @Override
    public float floatValue() {
        return (float)this.numerator / (float)this.divisor;
    }

    @Override
    public int intValue() {
        return this.numerator / this.divisor;
    }

    @Override
    public long longValue() {
        return (long)this.numerator / (long)this.divisor;
    }

    public String toString() {
        if (this.divisor == 0) {
            return "Invalid rational (" + this.numerator + "/" + this.divisor + ")";
        }
        NumberFormat nf = NumberFormat.getInstance();
        if (this.numerator % this.divisor == 0) {
            return nf.format(this.numerator / this.divisor);
        }
        return this.numerator + "/" + this.divisor + " (" + nf.format((double)this.numerator / (double)this.divisor) + ")";
    }

    public String toDisplayString() {
        if (this.numerator % this.divisor == 0) {
            return Integer.toString(this.numerator / this.divisor);
        }
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(3);
        return nf.format((double)this.numerator / (double)this.divisor);
    }

    public static RationalNumber valueOf(double value) {
        RationalNumber h;
        RationalNumber l;
        int approx;
        if (value >= 2.147483647E9) {
            return new RationalNumber(Integer.MAX_VALUE, 1);
        }
        if (value <= -2.147483647E9) {
            return new RationalNumber(-2147483647, 1);
        }
        boolean negative = false;
        if (value < 0.0) {
            negative = true;
            value = Math.abs(value);
        }
        if (value == 0.0) {
            return new RationalNumber(0, 1);
        }
        if (value >= 1.0) {
            approx = (int)value;
            if ((double)approx < value) {
                l = new RationalNumber(approx, 1);
                h = new RationalNumber(approx + 1, 1);
            } else {
                l = new RationalNumber(approx - 1, 1);
                h = new RationalNumber(approx, 1);
            }
        } else {
            approx = (int)(1.0 / value);
            if (1.0 / (double)approx < value) {
                l = new RationalNumber(1, approx);
                h = new RationalNumber(1, approx - 1);
            } else {
                l = new RationalNumber(1, approx + 1);
                h = new RationalNumber(1, approx);
            }
        }
        Option low = Option.factory(l, value);
        Option high = Option.factory(h, value);
        Option bestOption = low.error < high.error ? low : high;
        int maxIterations = 100;
        for (int count = 0; bestOption.error > 1.0E-8 && count < 100; ++count) {
            RationalNumber mediant = RationalNumber.factoryMethod((long)low.rationalNumber.numerator + (long)high.rationalNumber.numerator, (long)low.rationalNumber.divisor + (long)high.rationalNumber.divisor);
            Option mediantOption = Option.factory(mediant, value);
            if (value < mediant.doubleValue()) {
                if (high.error <= mediantOption.error) break;
                high = mediantOption;
            } else {
                if (low.error <= mediantOption.error) break;
                low = mediantOption;
            }
            if (!(mediantOption.error < bestOption.error)) continue;
            bestOption = mediantOption;
        }
        return negative ? bestOption.rationalNumber.negate() : bestOption.rationalNumber;
    }

    private static final class Option {
        public final RationalNumber rationalNumber;
        public final double error;

        private Option(RationalNumber rationalNumber, double error) {
            this.rationalNumber = rationalNumber;
            this.error = error;
        }

        public static Option factory(RationalNumber rationalNumber, double value) {
            return new Option(rationalNumber, Math.abs(rationalNumber.doubleValue() - value));
        }

        public String toString() {
            return this.rationalNumber.toString();
        }
    }
}

