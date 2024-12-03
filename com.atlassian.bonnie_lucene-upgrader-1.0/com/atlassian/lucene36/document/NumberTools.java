/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.document;

@Deprecated
public class NumberTools {
    private static final int RADIX = 36;
    private static final char NEGATIVE_PREFIX = '-';
    private static final char POSITIVE_PREFIX = '0';
    public static final String MIN_STRING_VALUE = "-0000000000000";
    public static final String MAX_STRING_VALUE = "01y2p0ij32e8e7";
    public static final int STR_SIZE = "-0000000000000".length();

    public static String longToString(long l) {
        if (l == Long.MIN_VALUE) {
            return MIN_STRING_VALUE;
        }
        StringBuilder buf = new StringBuilder(STR_SIZE);
        if (l < 0L) {
            buf.append('-');
            l = Long.MAX_VALUE + l + 1L;
        } else {
            buf.append('0');
        }
        String num = Long.toString(l, 36);
        int padLen = STR_SIZE - num.length() - buf.length();
        while (padLen-- > 0) {
            buf.append('0');
        }
        buf.append(num);
        return buf.toString();
    }

    public static long stringToLong(String str) {
        if (str == null) {
            throw new NullPointerException("string cannot be null");
        }
        if (str.length() != STR_SIZE) {
            throw new NumberFormatException("string is the wrong size");
        }
        if (str.equals(MIN_STRING_VALUE)) {
            return Long.MIN_VALUE;
        }
        char prefix = str.charAt(0);
        long l = Long.parseLong(str.substring(1), 36);
        if (prefix != '0') {
            if (prefix == '-') {
                l = l - Long.MAX_VALUE - 1L;
            } else {
                throw new NumberFormatException("string does not begin with the correct prefix");
            }
        }
        return l;
    }
}

