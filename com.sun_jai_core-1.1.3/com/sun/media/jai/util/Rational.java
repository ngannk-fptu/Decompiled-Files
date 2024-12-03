/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.util;

public class Rational {
    public long num;
    public long denom;
    private static final int MAX_TERMS = 20;

    public Rational(long num, long denom) {
        this.num = num;
        this.denom = denom;
    }

    public Rational(Rational r) {
        this.num = r.num;
        this.denom = r.denom;
    }

    public static Rational createFromFrac(long[] terms, int len) {
        Rational r = new Rational(0L, 1L);
        for (int i = len - 1; i >= 0; --i) {
            r.add(terms[i]);
            if (i == 0) continue;
            r.invert();
        }
        return r;
    }

    public static Rational approximate(float f, float tol) {
        float rem = f;
        long[] d = new long[20];
        int index = 0;
        for (int i = 0; i < 20; ++i) {
            int k = (int)Math.floor(rem);
            d[index++] = k;
            if ((rem -= (float)k) == 0.0f) break;
            rem = 1.0f / rem;
        }
        Rational r = null;
        for (int i = 1; i <= index; ++i) {
            r = Rational.createFromFrac(d, i);
            if (!(Math.abs(r.floatValue() - f) < tol)) continue;
            return r;
        }
        return r;
    }

    public static Rational approximate(double f, double tol) {
        double rem = f;
        long[] d = new long[20];
        int index = 0;
        for (int i = 0; i < 20; ++i) {
            long k = (long)Math.floor(rem);
            d[index++] = k;
            if ((rem -= (double)k) == 0.0) break;
            rem = 1.0 / rem;
        }
        Rational r = null;
        for (int i = 1; i <= index; ++i) {
            r = Rational.createFromFrac(d, i);
            if (!(Math.abs(r.doubleValue() - f) < tol)) continue;
            return r;
        }
        return r;
    }

    private static long gcd(long m, long n) {
        if (m < 0L) {
            m = -m;
        }
        if (n < 0L) {
            n = -n;
        }
        while (n > 0L) {
            long tmp = m % n;
            m = n;
            n = tmp;
        }
        return m;
    }

    private void normalize() {
        long gcd;
        if (this.denom < 0L) {
            this.num = -this.num;
            this.denom = -this.denom;
        }
        if ((gcd = Rational.gcd(this.num, this.denom)) > 1L) {
            this.num /= gcd;
            this.denom /= gcd;
        }
    }

    public void add(long i) {
        this.num += i * this.denom;
        this.normalize();
    }

    public void add(Rational r) {
        this.num = this.num * r.denom + r.num * this.denom;
        this.denom *= r.denom;
        this.normalize();
    }

    public void subtract(long i) {
        this.num -= i * this.denom;
        this.normalize();
    }

    public void subtract(Rational r) {
        this.num = this.num * r.denom - r.num * this.denom;
        this.denom *= r.denom;
        this.normalize();
    }

    public void multiply(long i) {
        this.num *= i;
        this.normalize();
    }

    public void multiply(Rational r) {
        this.num *= r.num;
        this.denom *= r.denom;
        this.normalize();
    }

    public void invert() {
        long tmp = this.num;
        this.num = this.denom;
        this.denom = tmp;
    }

    public float floatValue() {
        return (float)this.num / (float)this.denom;
    }

    public double doubleValue() {
        return (double)this.num / (double)this.denom;
    }

    public String toString() {
        return this.num + "/" + this.denom;
    }

    public static int ceil(long num, long denom) {
        int ret = (int)(num / denom);
        if (num > 0L && num % denom != 0L) {
            ++ret;
        }
        return ret;
    }

    public static int floor(long num, long denom) {
        int ret = (int)(num / denom);
        if (num < 0L && num % denom != 0L) {
            --ret;
        }
        return ret;
    }

    public static void main(String[] args) {
        float f = Float.parseFloat(args[0]);
        for (int i = 1; i < 15; ++i) {
            Rational r = Rational.approximate(f, (float)Math.pow(10.0, -i));
            System.out.println(r + " = " + r.floatValue());
        }
    }
}

