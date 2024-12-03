/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.stax2.ri.typed;

public final class NumberUtil {
    public static final int MAX_INT_CLEN = 11;
    public static final int MAX_LONG_CLEN = 21;
    public static final int MAX_DOUBLE_CLEN = 32;
    public static final int MAX_FLOAT_CLEN = 32;
    private static final char NULL_CHAR = '\u0000';
    private static final int MILLION = 1000000;
    private static final int BILLION = 1000000000;
    private static final long TEN_BILLION_L = 10000000000L;
    private static final long THOUSAND_L = 1000L;
    private static final byte BYTE_HYPHEN = 45;
    private static final byte BYTE_1 = 49;
    private static final byte BYTE_2 = 50;
    private static long MIN_INT_AS_LONG = -2147483647L;
    private static long MAX_INT_AS_LONG = Integer.MAX_VALUE;
    static final char[] LEADING_TRIPLETS = new char[4000];
    static final char[] FULL_TRIPLETS = new char[4000];

    public static int writeInt(int n, char[] cArray, int n2) {
        boolean bl;
        if (n < 0) {
            if (n == Integer.MIN_VALUE) {
                return NumberUtil.writeLong((long)n, cArray, n2);
            }
            cArray[n2++] = 45;
            n = -n;
        }
        if (n < 1000000) {
            if (n < 1000) {
                if (n < 10) {
                    cArray[n2++] = (char)(48 + n);
                } else {
                    n2 = NumberUtil.writeLeadingTriplet(n, cArray, n2);
                }
            } else {
                int n3 = n / 1000;
                n2 = NumberUtil.writeLeadingTriplet(n3, cArray, n2);
                n2 = NumberUtil.writeFullTriplet(n -= n3 * 1000, cArray, n2);
            }
            return n2;
        }
        boolean bl2 = bl = n >= 1000000000;
        if (bl) {
            if ((n -= 1000000000) >= 1000000000) {
                n -= 1000000000;
                cArray[n2++] = 50;
            } else {
                cArray[n2++] = 49;
            }
        }
        int n4 = n / 1000;
        int n5 = n - n4 * 1000;
        n = n4;
        int n6 = n - (n4 /= 1000) * 1000;
        n2 = bl ? NumberUtil.writeFullTriplet(n4, cArray, n2) : NumberUtil.writeLeadingTriplet(n4, cArray, n2);
        n2 = NumberUtil.writeFullTriplet(n6, cArray, n2);
        n2 = NumberUtil.writeFullTriplet(n5, cArray, n2);
        return n2;
    }

    public static int writeInt(int n, byte[] byArray, int n2) {
        boolean bl;
        if (n < 0) {
            if (n == Integer.MIN_VALUE) {
                return NumberUtil.writeLong((long)n, byArray, n2);
            }
            byArray[n2++] = 45;
            n = -n;
        }
        if (n < 1000000) {
            if (n < 1000) {
                if (n < 10) {
                    byArray[n2++] = (byte)(48 + n);
                } else {
                    n2 = NumberUtil.writeLeadingTriplet(n, byArray, n2);
                }
            } else {
                int n3 = n / 1000;
                n2 = NumberUtil.writeLeadingTriplet(n3, byArray, n2);
                n2 = NumberUtil.writeFullTriplet(n -= n3 * 1000, byArray, n2);
            }
            return n2;
        }
        boolean bl2 = bl = n >= 1000000000;
        if (bl) {
            if ((n -= 1000000000) >= 1000000000) {
                n -= 1000000000;
                byArray[n2++] = 50;
            } else {
                byArray[n2++] = 49;
            }
        }
        int n4 = n / 1000;
        int n5 = n - n4 * 1000;
        n = n4;
        int n6 = n - (n4 /= 1000) * 1000;
        n2 = bl ? NumberUtil.writeFullTriplet(n4, byArray, n2) : NumberUtil.writeLeadingTriplet(n4, byArray, n2);
        n2 = NumberUtil.writeFullTriplet(n6, byArray, n2);
        n2 = NumberUtil.writeFullTriplet(n5, byArray, n2);
        return n2;
    }

    public static int writeLong(long l, char[] cArray, int n) {
        int n2;
        if (l < 0L) {
            if (l >= MIN_INT_AS_LONG) {
                return NumberUtil.writeInt((int)l, cArray, n);
            }
            if (l == Long.MIN_VALUE) {
                return NumberUtil.getChars(String.valueOf(l), cArray, n);
            }
            cArray[n++] = 45;
            l = -l;
        } else if (l <= MAX_INT_AS_LONG) {
            return NumberUtil.writeInt((int)l, cArray, n);
        }
        int n3 = n;
        int n4 = n += NumberUtil.calcLongStrLength(l);
        while (l > MAX_INT_AS_LONG) {
            long l2 = l / 1000L;
            n2 = (int)(l - l2 * 1000L);
            NumberUtil.writeFullTriplet(n2, cArray, n4 -= 3);
            l = l2;
        }
        int n5 = (int)l;
        while (n5 >= 1000) {
            int n6 = n5 / 1000;
            n2 = n5 - n6 * 1000;
            NumberUtil.writeFullTriplet(n2, cArray, n4 -= 3);
            n5 = n6;
        }
        NumberUtil.writeLeadingTriplet(n5, cArray, n3);
        return n;
    }

    public static int writeLong(long l, byte[] byArray, int n) {
        int n2;
        if (l < 0L) {
            if (l >= MIN_INT_AS_LONG) {
                return NumberUtil.writeInt((int)l, byArray, n);
            }
            if (l == Long.MIN_VALUE) {
                return NumberUtil.getAsciiBytes(String.valueOf(l), byArray, n);
            }
            byArray[n++] = 45;
            l = -l;
        } else if (l <= MAX_INT_AS_LONG) {
            return NumberUtil.writeInt((int)l, byArray, n);
        }
        int n3 = n;
        int n4 = n += NumberUtil.calcLongStrLength(l);
        while (l > MAX_INT_AS_LONG) {
            long l2 = l / 1000L;
            n2 = (int)(l - l2 * 1000L);
            NumberUtil.writeFullTriplet(n2, byArray, n4 -= 3);
            l = l2;
        }
        int n5 = (int)l;
        while (n5 >= 1000) {
            int n6 = n5 / 1000;
            n2 = n5 - n6 * 1000;
            NumberUtil.writeFullTriplet(n2, byArray, n4 -= 3);
            n5 = n6;
        }
        NumberUtil.writeLeadingTriplet(n5, byArray, n3);
        return n;
    }

    public static int writeFloat(float f, char[] cArray, int n) {
        return NumberUtil.getChars(String.valueOf(f), cArray, n);
    }

    public static int writeFloat(float f, byte[] byArray, int n) {
        return NumberUtil.getAsciiBytes(String.valueOf(f), byArray, n);
    }

    public static int writeDouble(double d, char[] cArray, int n) {
        return NumberUtil.getChars(String.valueOf(d), cArray, n);
    }

    public static int writeDouble(double d, byte[] byArray, int n) {
        return NumberUtil.getAsciiBytes(String.valueOf(d), byArray, n);
    }

    private static int writeLeadingTriplet(int n, char[] cArray, int n2) {
        char c;
        int n3 = n << 2;
        if ((c = LEADING_TRIPLETS[n3++]) != '\u0000') {
            cArray[n2++] = c;
        }
        if ((c = LEADING_TRIPLETS[n3++]) != '\u0000') {
            cArray[n2++] = c;
        }
        cArray[n2++] = LEADING_TRIPLETS[n3];
        return n2;
    }

    private static int writeLeadingTriplet(int n, byte[] byArray, int n2) {
        char c;
        int n3 = n << 2;
        if ((c = LEADING_TRIPLETS[n3++]) != '\u0000') {
            byArray[n2++] = (byte)c;
        }
        if ((c = LEADING_TRIPLETS[n3++]) != '\u0000') {
            byArray[n2++] = (byte)c;
        }
        byArray[n2++] = (byte)LEADING_TRIPLETS[n3];
        return n2;
    }

    private static int writeFullTriplet(int n, char[] cArray, int n2) {
        int n3 = n << 2;
        cArray[n2++] = FULL_TRIPLETS[n3++];
        cArray[n2++] = FULL_TRIPLETS[n3++];
        cArray[n2++] = FULL_TRIPLETS[n3];
        return n2;
    }

    private static int writeFullTriplet(int n, byte[] byArray, int n2) {
        int n3 = n << 2;
        byArray[n2++] = (byte)FULL_TRIPLETS[n3++];
        byArray[n2++] = (byte)FULL_TRIPLETS[n3++];
        byArray[n2++] = (byte)FULL_TRIPLETS[n3];
        return n2;
    }

    private static int calcLongStrLength(long l) {
        int n;
        long l2 = 10000000000L;
        for (n = 10; l >= l2 && n != 19; ++n) {
            l2 = (l2 << 3) + (l2 << 1);
        }
        return n;
    }

    private static int getChars(String string, char[] cArray, int n) {
        int n2 = string.length();
        string.getChars(0, n2, cArray, n);
        return n + n2;
    }

    private static int getAsciiBytes(String string, byte[] byArray, int n) {
        int n2 = string.length();
        for (int i = 0; i < n2; ++i) {
            byArray[n++] = (byte)string.charAt(i);
        }
        return n;
    }

    static {
        int n = 0;
        for (int i = 0; i < 10; ++i) {
            char c = (char)(48 + i);
            char c2 = i == 0 ? (char)'\u0000' : c;
            for (int j = 0; j < 10; ++j) {
                char c3 = (char)(48 + j);
                char c4 = i == 0 && j == 0 ? (char)'\u0000' : c3;
                for (int k = 0; k < 10; ++k) {
                    char c5 = (char)(48 + k);
                    NumberUtil.LEADING_TRIPLETS[n] = c2;
                    NumberUtil.LEADING_TRIPLETS[n + 1] = c4;
                    NumberUtil.LEADING_TRIPLETS[n + 2] = c5;
                    NumberUtil.FULL_TRIPLETS[n] = c;
                    NumberUtil.FULL_TRIPLETS[n + 1] = c3;
                    NumberUtil.FULL_TRIPLETS[n + 2] = c5;
                    n += 4;
                }
            }
        }
    }
}

