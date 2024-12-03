/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.thirdparty.jackson.core.io.schubfach;

import software.amazon.awssdk.thirdparty.jackson.core.io.schubfach.MathUtils;

public final class DoubleToDecimal {
    static final int P = 53;
    private static final int W = 11;
    static final int Q_MIN = -1074;
    static final int Q_MAX = 971;
    static final int E_MIN = -323;
    static final int E_MAX = 309;
    static final long C_TINY = 3L;
    static final int K_MIN = -324;
    static final int K_MAX = 292;
    static final int H = 17;
    private static final long C_MIN = 0x10000000000000L;
    private static final int BQ_MASK = 2047;
    private static final long T_MASK = 0xFFFFFFFFFFFFFL;
    private static final long MASK_63 = Long.MAX_VALUE;
    private static final int MASK_28 = 0xFFFFFFF;
    private static final int NON_SPECIAL = 0;
    private static final int PLUS_ZERO = 1;
    private static final int MINUS_ZERO = 2;
    private static final int PLUS_INF = 3;
    private static final int MINUS_INF = 4;
    private static final int NAN = 5;
    public final int MAX_CHARS = 24;
    private final byte[] bytes = new byte[24];
    private int index;

    private DoubleToDecimal() {
    }

    public static String toString(double v) {
        return new DoubleToDecimal().toDecimalString(v);
    }

    private String toDecimalString(double v) {
        switch (this.toDecimal(v)) {
            case 0: {
                return this.charsToString();
            }
            case 1: {
                return "0.0";
            }
            case 2: {
                return "-0.0";
            }
            case 3: {
                return "Infinity";
            }
            case 4: {
                return "-Infinity";
            }
        }
        return "NaN";
    }

    private int toDecimal(double v) {
        long bits = Double.doubleToRawLongBits(v);
        long t = bits & 0xFFFFFFFFFFFFFL;
        int bq = (int)(bits >>> 52) & 0x7FF;
        if (bq < 2047) {
            this.index = -1;
            if (bits < 0L) {
                this.append(45);
            }
            if (bq != 0) {
                long f;
                int mq = 1075 - bq;
                long c = 0x10000000000000L | t;
                if (0 < mq & mq < 53 && (f = c >> mq) << mq == c) {
                    return this.toChars(f, 0);
                }
                return this.toDecimal(-mq, c, 0);
            }
            if (t != 0L) {
                return t < 3L ? this.toDecimal(-1074, 10L * t, -1) : this.toDecimal(-1074, t, 0);
            }
            return bits == 0L ? 1 : 2;
        }
        if (t != 0L) {
            return 5;
        }
        return bits > 0L ? 3 : 4;
    }

    private int toDecimal(int q, long c, int dk) {
        boolean win;
        int k;
        long cbl;
        int out = (int)c & 1;
        long cb = c << 2;
        long cbr = cb + 2L;
        if (c != 0x10000000000000L | q == -1074) {
            cbl = cb - 2L;
            k = MathUtils.flog10pow2(q);
        } else {
            cbl = cb - 1L;
            k = MathUtils.flog10threeQuartersPow2(q);
        }
        int h = q + MathUtils.flog2pow10(-k) + 2;
        long g1 = MathUtils.g1(k);
        long g0 = MathUtils.g0(k);
        long vb = DoubleToDecimal.rop(g1, g0, cb << h);
        long vbl = DoubleToDecimal.rop(g1, g0, cbl << h);
        long vbr = DoubleToDecimal.rop(g1, g0, cbr << h);
        long s = vb >> 2;
        if (s >= 100L) {
            boolean wpin;
            long sp10 = 10L * MathUtils.multiplyHigh(s, 1844674407370955168L);
            long tp10 = sp10 + 10L;
            boolean upin = vbl + (long)out <= sp10 << 2;
            boolean bl = wpin = (tp10 << 2) + (long)out <= vbr;
            if (upin != wpin) {
                return this.toChars(upin ? sp10 : tp10, k);
            }
        }
        long t = s + 1L;
        boolean uin = vbl + (long)out <= s << 2;
        boolean bl = win = (t << 2) + (long)out <= vbr;
        if (uin != win) {
            return this.toChars(uin ? s : t, k + dk);
        }
        long cmp = vb - (s + t << 1);
        return this.toChars(cmp < 0L || cmp == 0L && (s & 1L) == 0L ? s : t, k + dk);
    }

    private static long rop(long g1, long g0, long cp) {
        long x1 = MathUtils.multiplyHigh(g0, cp);
        long y0 = g1 * cp;
        long y1 = MathUtils.multiplyHigh(g1, cp);
        long z = (y0 >>> 1) + x1;
        long vbp = y1 + (z >>> 63);
        return vbp | (z & Long.MAX_VALUE) + Long.MAX_VALUE >>> 63;
    }

    private int toChars(long f, int e) {
        int len = MathUtils.flog10pow2(64 - Long.numberOfLeadingZeros(f));
        if (f >= MathUtils.pow10(len)) {
            ++len;
        }
        long hm = MathUtils.multiplyHigh(f *= MathUtils.pow10(17 - len), 193428131138340668L) >>> 20;
        int l = (int)(f - 100000000L * hm);
        int h = (int)(hm * 1441151881L >>> 57);
        int m = (int)(hm - (long)(100000000 * h));
        if (0 < (e += len) && e <= 7) {
            return this.toChars1(h, m, l, e);
        }
        if (-3 < e && e <= 0) {
            return this.toChars2(h, m, l, e);
        }
        return this.toChars3(h, m, l, e);
    }

    private int toChars1(int h, int m, int l, int e) {
        int t;
        int i;
        this.appendDigit(h);
        int y = this.y(m);
        for (i = 1; i < e; ++i) {
            t = 10 * y;
            this.appendDigit(t >>> 28);
            y = t & 0xFFFFFFF;
        }
        this.append(46);
        while (i <= 8) {
            t = 10 * y;
            this.appendDigit(t >>> 28);
            y = t & 0xFFFFFFF;
            ++i;
        }
        this.lowDigits(l);
        return 0;
    }

    private int toChars2(int h, int m, int l, int e) {
        this.appendDigit(0);
        this.append(46);
        while (e < 0) {
            this.appendDigit(0);
            ++e;
        }
        this.appendDigit(h);
        this.append8Digits(m);
        this.lowDigits(l);
        return 0;
    }

    private int toChars3(int h, int m, int l, int e) {
        this.appendDigit(h);
        this.append(46);
        this.append8Digits(m);
        this.lowDigits(l);
        this.exponent(e - 1);
        return 0;
    }

    private void lowDigits(int l) {
        if (l != 0) {
            this.append8Digits(l);
        }
        this.removeTrailingZeroes();
    }

    private void append8Digits(int m) {
        int y = this.y(m);
        for (int i = 0; i < 8; ++i) {
            int t = 10 * y;
            this.appendDigit(t >>> 28);
            y = t & 0xFFFFFFF;
        }
    }

    private void removeTrailingZeroes() {
        while (this.bytes[this.index] == 48) {
            --this.index;
        }
        if (this.bytes[this.index] == 46) {
            ++this.index;
        }
    }

    private int y(int a) {
        return (int)(MathUtils.multiplyHigh((long)(a + 1) << 28, 193428131138340668L) >>> 20) - 1;
    }

    private void exponent(int e) {
        int d;
        this.append(69);
        if (e < 0) {
            this.append(45);
            e = -e;
        }
        if (e < 10) {
            this.appendDigit(e);
            return;
        }
        if (e >= 100) {
            d = e * 1311 >>> 17;
            this.appendDigit(d);
            e -= 100 * d;
        }
        d = e * 103 >>> 10;
        this.appendDigit(d);
        this.appendDigit(e - 10 * d);
    }

    private void append(int c) {
        this.bytes[++this.index] = (byte)c;
    }

    private void appendDigit(int d) {
        this.bytes[++this.index] = (byte)(48 + d);
    }

    private String charsToString() {
        return new String(this.bytes, 0, 0, this.index + 1);
    }
}

