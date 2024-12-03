/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.Random;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatContext;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.Apint;
import org.apfloat.GCDHelper;
import org.apfloat.InfiniteExpansionException;

public class ApintMath {
    private static final Random RANDOM = new Random();

    private ApintMath() {
    }

    public static Apint pow(Apint x, long n) throws ArithmeticException, ApfloatRuntimeException {
        if (n == 0L) {
            if (x.signum() == 0) {
                throw new ArithmeticException("Zero to power zero");
            }
            return new Apint(1L, x.radix());
        }
        if (n < 0L) {
            return Apint.ZEROS[x.radix()];
        }
        int b2pow = 0;
        while ((n & 1L) == 0L) {
            ++b2pow;
            n >>= 1;
        }
        Apint r = x;
        while ((n >>= 1) > 0L) {
            x = x.multiply(x);
            if ((n & 1L) == 0L) continue;
            r = r.multiply(x);
        }
        while (b2pow-- > 0) {
            r = r.multiply(r);
        }
        return r;
    }

    public static Apint[] sqrt(Apint x) throws ArithmeticException, ApfloatRuntimeException {
        return ApintMath.root(x, 2L);
    }

    public static Apint[] cbrt(Apint x) throws ApfloatRuntimeException {
        return ApintMath.root(x, 3L);
    }

    public static Apint[] root(Apint x, long n) throws ArithmeticException, ApfloatRuntimeException {
        if (n == 0L) {
            if (x.signum() == 0) {
                throw new ArithmeticException("Zeroth root of zero");
            }
            Apint one = new Apint(1L, x.radix());
            return new Apint[]{one, x.subtract(one)};
        }
        if (x.signum() == 0) {
            return new Apint[]{x, x};
        }
        if (x.equals(Apint.ONE) || n == 1L) {
            return new Apint[]{x, Apint.ZEROS[x.radix()]};
        }
        if (n < 0L) {
            return new Apint[]{Apint.ZEROS[x.radix()], x};
        }
        long precision = x.scale() / n + 20L;
        Apfloat approxX = x.precision(precision);
        Apfloat approxRoot = ApfloatMath.root(approxX, n);
        Apint root = approxRoot.truncate();
        Apint pow = ApintMath.pow(root, n);
        if (ApintMath.abs(pow).compareTo(ApintMath.abs(x)) > 0) {
            pow = x.signum() >= 0 ? ApintMath.powXMinus1(pow, root, n) : ApintMath.powXPlus1(pow, root, n);
            root = root.subtract(new Apint((long)x.signum(), x.radix()));
        } else {
            Apint powPlus1;
            Apint apint = powPlus1 = x.signum() >= 0 ? ApintMath.powXPlus1(pow, root, n) : ApintMath.powXMinus1(pow, root, n);
            if (ApintMath.abs(powPlus1).compareTo(ApintMath.abs(x)) <= 0) {
                pow = powPlus1;
                root = root.add(new Apint((long)x.signum(), x.radix()));
            }
        }
        Apint remainder = x.subtract(pow);
        assert (remainder.signum() * x.signum() >= 0);
        return new Apint[]{root, remainder};
    }

    private static Apint powXMinus1(Apint pow, Apint x, long n) throws ApfloatRuntimeException {
        Apint one = new Apint(1L, x.radix());
        pow = n == 2L ? pow.subtract(x).subtract(x).add(one) : (n == 3L ? pow.subtract(new Apint(3L, x.radix()).multiply(x).multiply(x.subtract(one))).subtract(one) : ApintMath.pow(x.subtract(one), n));
        return pow;
    }

    private static Apint powXPlus1(Apint pow, Apint x, long n) throws ApfloatRuntimeException {
        Apint one = new Apint(1L, x.radix());
        pow = n == 2L ? pow.add(x).add(x).add(one) : (n == 3L ? pow.add(new Apint(3L, x.radix()).multiply(x).multiply(x.add(one))).add(one) : ApintMath.pow(x.add(one), n));
        return pow;
    }

    @Deprecated
    public static Apint negate(Apint x) throws ApfloatRuntimeException {
        return x.negate();
    }

    public static Apint abs(Apint x) throws ApfloatRuntimeException {
        if (x.signum() >= 0) {
            return x;
        }
        return x.negate();
    }

    public static Apint copySign(Apint x, Apint y) throws ApfloatRuntimeException {
        if (y.signum() == 0) {
            return y;
        }
        if (x.signum() != y.signum()) {
            return x.negate();
        }
        return x;
    }

    public static Apint scale(Apint x, long scale) throws ApfloatRuntimeException {
        return ApfloatMath.scale(x, scale).truncate();
    }

    public static Apint[] div(Apint x, Apint y) throws ArithmeticException, ApfloatRuntimeException {
        Apfloat ty;
        Apint b;
        if (y.signum() == 0) {
            throw new ArithmeticException("Division by zero");
        }
        if (x.signum() == 0) {
            return new Apint[]{x, x};
        }
        if (y.equals(Apint.ONE)) {
            return new Apint[]{x, Apint.ZEROS[x.radix()]};
        }
        Apint a = ApintMath.abs(x);
        if (a.compareTo(b = ApintMath.abs(y)) < 0) {
            return new Apint[]{Apint.ZEROS[x.radix()], x};
        }
        long precision = x.scale() - y.scale() + 20L;
        Apfloat tx = x.precision(precision);
        Apint q = tx.divide(ty = y.precision(precision)).truncate();
        if ((a = a.subtract(ApintMath.abs(q.multiply(y)))).compareTo(b) >= 0) {
            q = q.add(new Apint((long)(x.signum() * y.signum()), x.radix()));
            a = a.subtract(b);
        } else if (a.signum() < 0) {
            q = q.subtract(new Apint((long)(x.signum() * y.signum()), x.radix()));
            a = a.add(b);
        }
        Apint r = ApintMath.copySign(a, x);
        return new Apint[]{q, r};
    }

    public static Apint gcd(Apint a, Apint b) throws ApfloatRuntimeException {
        return GCDHelper.gcd(a, b);
    }

    public static Apint lcm(Apint a, Apint b) throws ApfloatRuntimeException {
        if (a.signum() == 0 && b.signum() == 0) {
            return Apint.ZEROS[a.radix()];
        }
        return ApintMath.abs(a.multiply(b)).divide(ApintMath.gcd(a, b));
    }

    public static Apint modMultiply(Apint a, Apint b, Apint m) throws ApfloatRuntimeException {
        return a.multiply(b).mod(m);
    }

    private static Apint modMultiply(Apint x1, Apint x2, Apint y, Apfloat inverseY) throws ApfloatRuntimeException {
        Apint b;
        Apint x = x1.multiply(x2);
        if (x.signum() == 0) {
            return x;
        }
        long precision = x.scale() - y.scale() + 20L;
        Apint a = ApintMath.abs(x);
        if (a.compareTo(b = ApintMath.abs(y)) < 0) {
            return x;
        }
        Apint t = x.multiply(inverseY.precision(precision)).truncate();
        if ((a = a.subtract(ApintMath.abs(t.multiply(y)))).compareTo(b) >= 0) {
            a = a.subtract(b);
        } else if (a.signum() < 0) {
            a = a.add(b);
        }
        t = ApintMath.copySign(a, x);
        return t;
    }

    public static Apint modPow(Apint a, Apint b, Apint m) throws ArithmeticException, ApfloatRuntimeException {
        Apint[] qr;
        if (b.signum() == 0) {
            if (a.signum() == 0) {
                throw new ArithmeticException("Zero to power zero");
            }
            return new Apint(1L, a.radix());
        }
        if (m.signum() == 0) {
            return m;
        }
        m = ApintMath.abs(m);
        Apfloat inverseModulus = ApfloatMath.inverseRoot(m, 1L, m.scale() + 20L);
        a = a.mod(m);
        if (b.signum() < 0) {
            a = ApintMath.modInverse(a, m);
            b = b.negate();
        }
        Apint two = new Apint(2L, b.radix());
        while ((qr = ApintMath.div(b, two))[1].signum() == 0) {
            a = ApintMath.modMultiply(a, a, m, inverseModulus);
            b = qr[0];
        }
        Apint r = a;
        qr = ApintMath.div(b, two);
        while ((b = qr[0]).signum() > 0) {
            a = ApintMath.modMultiply(a, a, m, inverseModulus);
            qr = ApintMath.div(b, two);
            if (qr[1].signum() == 0) continue;
            r = ApintMath.modMultiply(r, a, m, inverseModulus);
        }
        return r;
    }

    private static Apint modInverse(Apint a, Apint m) throws ArithmeticException, ApfloatRuntimeException {
        Apint one = new Apint(1L, m.radix());
        Apint x = Apint.ZERO;
        Apint y = one;
        Apint oldX = one;
        Apint oldY = Apint.ZERO;
        Apint oldA = a;
        Apint b = m;
        while (b.signum() != 0) {
            Apint q = a.divide(b);
            Apint tmp = b;
            b = a.mod(b);
            a = tmp;
            tmp = x;
            x = oldX.subtract(q.multiply(x));
            oldX = tmp;
            tmp = y;
            y = oldY.subtract(q.multiply(y));
            oldY = tmp;
        }
        if (!ApintMath.abs(a).equals(one)) {
            throw new ArithmeticException("Modular inverse does not exist");
        }
        if (oldX.signum() != oldA.signum()) {
            oldX = oldX.add(ApintMath.copySign(m, oldA));
        }
        return oldX;
    }

    public static Apint factorial(long n) throws ArithmeticException, NumberFormatException, ApfloatRuntimeException {
        return new Apint(ApfloatMath.factorial(n, Long.MAX_VALUE));
    }

    public static Apint factorial(long n, int radix) throws ArithmeticException, NumberFormatException, ApfloatRuntimeException {
        return new Apint(ApfloatMath.factorial(n, Long.MAX_VALUE, radix));
    }

    public static Apint product(Apint ... x) throws ApfloatRuntimeException {
        return new Apint(ApfloatMath.product(x));
    }

    public static Apint sum(Apint ... x) throws ApfloatRuntimeException {
        return new Apint(ApfloatMath.sum(x));
    }

    public static Apint random(long digits) {
        ApfloatContext ctx = ApfloatContext.getContext();
        int radix = ctx.getDefaultRadix();
        return ApintMath.random(digits, radix);
    }

    public static Apint random(final long digits, final int radix) {
        if (digits <= 0L) {
            throw new IllegalArgumentException(digits + " is not positive");
        }
        if (digits == Long.MAX_VALUE) {
            throw new InfiniteExpansionException("Cannot generate an infinite number of random digits");
        }
        PushbackReader reader = new PushbackReader(new Reader(){
            private long remaining;
            {
                this.remaining = digits;
            }

            @Override
            public int read(char[] buffer, int offset, int length) {
                if (this.remaining == 0L) {
                    return -1;
                }
                length = (int)Math.min((long)length, this.remaining);
                for (int i = 0; i < length; ++i) {
                    buffer[i + offset] = Character.forDigit(RANDOM.nextInt(radix), radix);
                }
                this.remaining -= (long)length;
                return length;
            }

            @Override
            public void close() {
            }
        });
        try {
            return new Apint(reader, radix);
        }
        catch (IOException ioe) {
            throw new ApfloatRuntimeException("Error generating random number");
        }
    }

    public static Apint max(Apint x, Apint y) {
        return x.compareTo(y) > 0 ? x : y;
    }

    public static Apint min(Apint x, Apint y) {
        return x.compareTo(y) < 0 ? x : y;
    }
}

