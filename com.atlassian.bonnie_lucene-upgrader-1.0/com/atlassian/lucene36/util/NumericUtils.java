/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.util;

public final class NumericUtils {
    public static final int PRECISION_STEP_DEFAULT = 4;
    public static final char SHIFT_START_LONG = ' ';
    public static final int BUF_SIZE_LONG = 11;
    public static final char SHIFT_START_INT = '`';
    public static final int BUF_SIZE_INT = 6;

    private NumericUtils() {
    }

    public static int longToPrefixCoded(long val, int shift, char[] buffer) {
        if (shift > 63 || shift < 0) {
            throw new IllegalArgumentException("Illegal shift value, must be 0..63");
        }
        int nChars = (63 - shift) / 7 + 1;
        int len = nChars + 1;
        buffer[0] = (char)(32 + shift);
        long sortableBits = val ^ Long.MIN_VALUE;
        sortableBits >>>= shift;
        while (nChars >= 1) {
            buffer[nChars--] = (char)(sortableBits & 0x7FL);
            sortableBits >>>= 7;
        }
        return len;
    }

    public static String longToPrefixCoded(long val, int shift) {
        char[] buffer = new char[11];
        int len = NumericUtils.longToPrefixCoded(val, shift, buffer);
        return new String(buffer, 0, len);
    }

    public static String longToPrefixCoded(long val) {
        return NumericUtils.longToPrefixCoded(val, 0);
    }

    public static int intToPrefixCoded(int val, int shift, char[] buffer) {
        if (shift > 31 || shift < 0) {
            throw new IllegalArgumentException("Illegal shift value, must be 0..31");
        }
        int nChars = (31 - shift) / 7 + 1;
        int len = nChars + 1;
        buffer[0] = (char)(96 + shift);
        int sortableBits = val ^ Integer.MIN_VALUE;
        sortableBits >>>= shift;
        while (nChars >= 1) {
            buffer[nChars--] = (char)(sortableBits & 0x7F);
            sortableBits >>>= 7;
        }
        return len;
    }

    public static String intToPrefixCoded(int val, int shift) {
        char[] buffer = new char[6];
        int len = NumericUtils.intToPrefixCoded(val, shift, buffer);
        return new String(buffer, 0, len);
    }

    public static String intToPrefixCoded(int val) {
        return NumericUtils.intToPrefixCoded(val, 0);
    }

    public static long prefixCodedToLong(String prefixCoded) {
        int shift = prefixCoded.charAt(0) - 32;
        if (shift > 63 || shift < 0) {
            throw new NumberFormatException("Invalid shift value in prefixCoded string (is encoded value really a LONG?)");
        }
        long sortableBits = 0L;
        int len = prefixCoded.length();
        for (int i = 1; i < len; ++i) {
            sortableBits <<= 7;
            char ch = prefixCoded.charAt(i);
            if (ch > '\u007f') {
                throw new NumberFormatException("Invalid prefixCoded numerical value representation (char " + Integer.toHexString(ch) + " at position " + i + " is invalid)");
            }
            sortableBits |= (long)ch;
        }
        return sortableBits << shift ^ Long.MIN_VALUE;
    }

    public static int prefixCodedToInt(String prefixCoded) {
        int shift = prefixCoded.charAt(0) - 96;
        if (shift > 31 || shift < 0) {
            throw new NumberFormatException("Invalid shift value in prefixCoded string (is encoded value really an INT?)");
        }
        int sortableBits = 0;
        int len = prefixCoded.length();
        for (int i = 1; i < len; ++i) {
            sortableBits <<= 7;
            char ch = prefixCoded.charAt(i);
            if (ch > '\u007f') {
                throw new NumberFormatException("Invalid prefixCoded numerical value representation (char " + Integer.toHexString(ch) + " at position " + i + " is invalid)");
            }
            sortableBits |= ch;
        }
        return sortableBits << shift ^ Integer.MIN_VALUE;
    }

    public static long doubleToSortableLong(double val) {
        long f = Double.doubleToLongBits(val);
        if (f < 0L) {
            f ^= Long.MAX_VALUE;
        }
        return f;
    }

    public static String doubleToPrefixCoded(double val) {
        return NumericUtils.longToPrefixCoded(NumericUtils.doubleToSortableLong(val));
    }

    public static double sortableLongToDouble(long val) {
        if (val < 0L) {
            val ^= Long.MAX_VALUE;
        }
        return Double.longBitsToDouble(val);
    }

    public static double prefixCodedToDouble(String val) {
        return NumericUtils.sortableLongToDouble(NumericUtils.prefixCodedToLong(val));
    }

    public static int floatToSortableInt(float val) {
        int f = Float.floatToIntBits(val);
        if (f < 0) {
            f ^= Integer.MAX_VALUE;
        }
        return f;
    }

    public static String floatToPrefixCoded(float val) {
        return NumericUtils.intToPrefixCoded(NumericUtils.floatToSortableInt(val));
    }

    public static float sortableIntToFloat(int val) {
        if (val < 0) {
            val ^= Integer.MAX_VALUE;
        }
        return Float.intBitsToFloat(val);
    }

    public static float prefixCodedToFloat(String val) {
        return NumericUtils.sortableIntToFloat(NumericUtils.prefixCodedToInt(val));
    }

    public static void splitLongRange(LongRangeBuilder builder, int precisionStep, long minBound, long maxBound) {
        NumericUtils.splitRange(builder, 64, precisionStep, minBound, maxBound);
    }

    public static void splitIntRange(IntRangeBuilder builder, int precisionStep, int minBound, int maxBound) {
        NumericUtils.splitRange(builder, 32, precisionStep, minBound, maxBound);
    }

    private static void splitRange(Object builder, int valSize, int precisionStep, long minBound, long maxBound) {
        if (precisionStep < 1) {
            throw new IllegalArgumentException("precisionStep must be >=1");
        }
        if (minBound > maxBound) {
            return;
        }
        int shift = 0;
        while (true) {
            boolean upperWrapped;
            long diff = 1L << shift + precisionStep;
            long mask = (1L << precisionStep) - 1L << shift;
            boolean hasLower = (minBound & mask) != 0L;
            boolean hasUpper = (maxBound & mask) != mask;
            long nextMinBound = (hasLower ? minBound + diff : minBound) & (mask ^ 0xFFFFFFFFFFFFFFFFL);
            long nextMaxBound = (hasUpper ? maxBound - diff : maxBound) & (mask ^ 0xFFFFFFFFFFFFFFFFL);
            boolean lowerWrapped = nextMinBound < minBound;
            boolean bl = upperWrapped = nextMaxBound > maxBound;
            if (shift + precisionStep >= valSize || nextMinBound > nextMaxBound || lowerWrapped || upperWrapped) break;
            if (hasLower) {
                NumericUtils.addRange(builder, valSize, minBound, minBound | mask, shift);
            }
            if (hasUpper) {
                NumericUtils.addRange(builder, valSize, maxBound & (mask ^ 0xFFFFFFFFFFFFFFFFL), maxBound, shift);
            }
            minBound = nextMinBound;
            maxBound = nextMaxBound;
            shift += precisionStep;
        }
        NumericUtils.addRange(builder, valSize, minBound, maxBound, shift);
    }

    private static void addRange(Object builder, int valSize, long minBound, long maxBound, int shift) {
        maxBound |= (1L << shift) - 1L;
        switch (valSize) {
            case 64: {
                ((LongRangeBuilder)builder).addRange(minBound, maxBound, shift);
                break;
            }
            case 32: {
                ((IntRangeBuilder)builder).addRange((int)minBound, (int)maxBound, shift);
                break;
            }
            default: {
                throw new IllegalArgumentException("valSize must be 32 or 64.");
            }
        }
    }

    public static abstract class IntRangeBuilder {
        public void addRange(String minPrefixCoded, String maxPrefixCoded) {
            throw new UnsupportedOperationException();
        }

        public void addRange(int min, int max, int shift) {
            this.addRange(NumericUtils.intToPrefixCoded(min, shift), NumericUtils.intToPrefixCoded(max, shift));
        }
    }

    public static abstract class LongRangeBuilder {
        public void addRange(String minPrefixCoded, String maxPrefixCoded) {
            throw new UnsupportedOperationException();
        }

        public void addRange(long min, long max, int shift) {
            this.addRange(NumericUtils.longToPrefixCoded(min, shift), NumericUtils.longToPrefixCoded(max, shift));
        }
    }
}

