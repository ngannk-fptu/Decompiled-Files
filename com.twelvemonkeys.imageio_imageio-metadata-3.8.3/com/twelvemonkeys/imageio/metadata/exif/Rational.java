/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.imageio.metadata.exif;

@Deprecated
public final class Rational
extends Number
implements Comparable<Rational> {
    private final com.twelvemonkeys.imageio.metadata.tiff.Rational delegate;

    public Rational(long l) {
        this(new com.twelvemonkeys.imageio.metadata.tiff.Rational(l, 1L));
    }

    public Rational(long l, long l2) {
        this(new com.twelvemonkeys.imageio.metadata.tiff.Rational(l, l2));
    }

    private Rational(com.twelvemonkeys.imageio.metadata.tiff.Rational rational) {
        this.delegate = rational;
    }

    public long numerator() {
        return this.delegate.numerator();
    }

    public long denominator() {
        return this.delegate.denominator();
    }

    @Override
    public byte byteValue() {
        return this.delegate.byteValue();
    }

    @Override
    public short shortValue() {
        return this.delegate.shortValue();
    }

    @Override
    public int intValue() {
        return this.delegate.intValue();
    }

    @Override
    public long longValue() {
        return this.delegate.longValue();
    }

    @Override
    public float floatValue() {
        return this.delegate.floatValue();
    }

    @Override
    public double doubleValue() {
        return this.delegate.doubleValue();
    }

    @Override
    public int compareTo(Rational rational) {
        return this.delegate.compareTo(rational.delegate);
    }

    public int hashCode() {
        return this.delegate.hashCode();
    }

    public boolean equals(Object object) {
        return object == this || object instanceof Rational && this.delegate.equals(((Rational)object).delegate);
    }

    public String toString() {
        return this.delegate.toString();
    }
}

