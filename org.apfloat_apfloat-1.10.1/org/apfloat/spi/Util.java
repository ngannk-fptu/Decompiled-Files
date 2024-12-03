/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.spi;

public class Util {
    private Util() {
    }

    public static int round2down(int x) {
        assert (x >= 0);
        return Integer.highestOneBit(x);
    }

    public static long round2down(long x) {
        assert (x >= 0L);
        return Long.highestOneBit(x);
    }

    public static int round2up(int x) {
        int r;
        assert (x >= 0);
        assert (x <= 0x40000000);
        if (x == 0) {
            return 0;
        }
        for (r = 1; r < x; r += r) {
        }
        return r;
    }

    public static long round2up(long x) {
        long r;
        assert (x >= 0L);
        assert (x <= 0x4000000000000000L);
        if (x == 0L) {
            return 0L;
        }
        for (r = 1L; r < x; r += r) {
        }
        return r;
    }

    public static int round23down(int x) {
        assert (x >= 0);
        if (x == 0) {
            return 0;
        }
        int r = 1;
        int p = 0;
        while (r <= x && r > 0) {
            p = r;
            if (r == 1) {
                r = 2;
                continue;
            }
            if (r == (r & -r)) {
                r = r / 2 * 3;
                continue;
            }
            r = r / 3 * 4;
        }
        return p;
    }

    public static long round23down(long x) {
        assert (x >= 0L);
        if (x == 0L) {
            return 0L;
        }
        long r = 1L;
        long p = 0L;
        while (r <= x && r > 0L) {
            p = r;
            if (r == 1L) {
                r = 2L;
                continue;
            }
            if (r == (r & -r)) {
                r = r / 2L * 3L;
                continue;
            }
            r = r / 3L * 4L;
        }
        return p;
    }

    public static int round23up(int x) {
        assert (x >= 0);
        assert (x <= 0x60000000);
        if (x == 0) {
            return 0;
        }
        int r = 1;
        while (r < x) {
            if (r == 1) {
                r = 2;
                continue;
            }
            if (r == (r & -r)) {
                r = r / 2 * 3;
                continue;
            }
            r = r / 3 * 4;
        }
        return r;
    }

    public static long round23up(long x) {
        assert (x >= 0L);
        assert (x <= 0x6000000000000000L);
        if (x == 0L) {
            return 0L;
        }
        long r = 1L;
        while (r < x) {
            if (r == 1L) {
                r = 2L;
                continue;
            }
            if (r == (r & -r)) {
                r = r / 2L * 3L;
                continue;
            }
            r = r / 3L * 4L;
        }
        return r;
    }

    public static int sqrt4down(int x) {
        assert (x >= 0);
        if (x == 0) {
            return 0;
        }
        int r = 1;
        while ((x >>= 2) > 0) {
            r <<= 1;
        }
        return r;
    }

    public static long sqrt4down(long x) {
        assert (x >= 0L);
        if (x == 0L) {
            return 0L;
        }
        long r = 1L;
        while ((x >>= 2) > 0L) {
            r <<= 1;
        }
        return r;
    }

    public static int sqrt4up(int x) {
        assert (x >= 0);
        if (x == 0) {
            return 0;
        }
        int r = 1;
        int p = 1;
        while (p < x && p > 0) {
            p <<= 2;
            r <<= 1;
        }
        return r;
    }

    public static long sqrt4up(long x) {
        assert (x >= 0L);
        if (x == 0L) {
            return 0L;
        }
        long r = 1L;
        long p = 1L;
        while (p < x && p > 0L) {
            p <<= 2;
            r <<= 1;
        }
        return r;
    }

    public static int log2down(int x) {
        assert (x > 0);
        return 31 - Integer.numberOfLeadingZeros(x);
    }

    public static int log2down(long x) {
        assert (x > 0L);
        return 63 - Long.numberOfLeadingZeros(x);
    }

    public static int log2up(int x) {
        assert (x > 0);
        return Util.log2down(x) + (x == (x & -x) ? 0 : 1);
    }

    public static int log2up(long x) {
        assert (x > 0L);
        return Util.log2down(x) + (x == (x & -x) ? 0 : 1);
    }

    public static long ifFinite(long x, long y) {
        return x == Long.MAX_VALUE || y <= 0L ? Long.MAX_VALUE : y;
    }
}

