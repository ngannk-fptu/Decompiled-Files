/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.metadata.tiff;

public final class Rational
extends Number
implements Comparable<Rational> {
    static final Rational ZERO = new Rational(0L, 1L);
    static final Rational NaN = new Rational();
    private final long numerator;
    private final long denominator;

    private Rational() {
        this.numerator = 0L;
        this.denominator = 0L;
    }

    public Rational(long l) {
        this(l, 1L);
    }

    public Rational(long l, long l2) {
        if (l2 == 0L) {
            throw new IllegalArgumentException("denominator == 0");
        }
        if (l == Long.MIN_VALUE || l2 == Long.MIN_VALUE) {
            throw new IllegalArgumentException("value == Long.MIN_VALUE");
        }
        long l3 = Rational.gcd(l, l2);
        long l4 = l / l3;
        long l5 = l2 / l3;
        this.numerator = l2 >= 0L ? l4 : -l4;
        this.denominator = l2 >= 0L ? l5 : -l5;
    }

    private static long gcd(long l, long l2) {
        if (l < 0L) {
            return Rational.gcd(l2, -l);
        }
        return l2 == 0L ? l : Rational.gcd(l2, l % l2);
    }

    private static long lcm(long l, long l2) {
        if (l < 0L) {
            return Rational.lcm(l2, -l);
        }
        return l * (l2 / Rational.gcd(l, l2));
    }

    public long numerator() {
        return this.numerator;
    }

    public long denominator() {
        return this.denominator;
    }

    @Override
    public int intValue() {
        return (int)this.doubleValue();
    }

    @Override
    public long longValue() {
        return (long)this.doubleValue();
    }

    @Override
    public float floatValue() {
        return (float)this.doubleValue();
    }

    @Override
    public double doubleValue() {
        if (this == NaN) {
            return Double.NaN;
        }
        return (double)this.numerator / (double)this.denominator;
    }

    @Override
    public int compareTo(Rational rational) {
        double d = this.doubleValue();
        double d2 = rational.doubleValue();
        return Double.compare(d, d2);
    }

    public int hashCode() {
        return Float.floatToIntBits(this.floatValue());
    }

    public boolean equals(Object object) {
        return object == this || object instanceof Rational && this.compareTo((Rational)object) == 0;
    }

    public String toString() {
        if (this == NaN) {
            return "NaN";
        }
        return this.denominator == 1L ? Long.toString(this.numerator) : String.format("%s/%s", this.numerator, this.denominator);
    }

    public Rational times(Rational rational) {
        if (this.equals(ZERO) || rational.equals(ZERO)) {
            return ZERO;
        }
        Rational rational2 = new Rational(this.numerator, rational.denominator);
        Rational rational3 = new Rational(rational.numerator, this.denominator);
        return new Rational(rational2.numerator * rational3.numerator, rational2.denominator * rational3.denominator);
    }

    public Rational plus(Rational rational) {
        if (this.equals(ZERO)) {
            return rational;
        }
        if (rational.equals(ZERO)) {
            return this;
        }
        long l = Rational.gcd(this.numerator, rational.numerator);
        long l2 = Rational.gcd(this.denominator, rational.denominator);
        return new Rational((this.numerator / l * (rational.denominator / l2) + rational.numerator / l * (this.denominator / l2)) * l, Rational.lcm(this.denominator, rational.denominator));
    }

    public Rational negate() {
        return new Rational(-this.numerator, this.denominator);
    }

    public Rational minus(Rational rational) {
        return this.plus(rational.negate());
    }

    public Rational reciprocal() {
        return new Rational(this.denominator, this.numerator);
    }

    public Rational divides(Rational rational) {
        if (rational.equals(ZERO)) {
            throw new ArithmeticException("/ by zero");
        }
        return this.times(rational.reciprocal());
    }
}

