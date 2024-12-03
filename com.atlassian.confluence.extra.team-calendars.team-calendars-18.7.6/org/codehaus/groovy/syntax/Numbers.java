/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.syntax;

import java.math.BigDecimal;
import java.math.BigInteger;

public class Numbers {
    private static final BigInteger MAX_LONG = BigInteger.valueOf(Long.MAX_VALUE);
    private static final BigInteger MIN_LONG = BigInteger.valueOf(Long.MIN_VALUE);
    private static final BigInteger MAX_INTEGER = BigInteger.valueOf(Integer.MAX_VALUE);
    private static final BigInteger MIN_INTEGER = BigInteger.valueOf(Integer.MIN_VALUE);
    private static final BigDecimal MAX_DOUBLE = new BigDecimal(String.valueOf(Double.MAX_VALUE));
    private static final BigDecimal MIN_DOUBLE = MAX_DOUBLE.negate();
    private static final BigDecimal MAX_FLOAT = new BigDecimal(String.valueOf(Float.MAX_VALUE));
    private static final BigDecimal MIN_FLOAT = MAX_FLOAT.negate();

    public static boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    public static boolean isOctalDigit(char c) {
        return c >= '0' && c <= '7';
    }

    public static boolean isHexDigit(char c) {
        return Numbers.isDigit(c) || c >= 'A' && c <= 'F' || c >= 'a' && c <= 'f';
    }

    public static boolean isNumericTypeSpecifier(char c, boolean isDecimal) {
        if (isDecimal) {
            switch (c) {
                case 'D': 
                case 'F': 
                case 'G': 
                case 'd': 
                case 'f': 
                case 'g': {
                    return true;
                }
            }
        } else {
            switch (c) {
                case 'G': 
                case 'I': 
                case 'L': 
                case 'g': 
                case 'i': 
                case 'l': {
                    return true;
                }
            }
        }
        return false;
    }

    public static Number parseInteger(String text) {
        text = text.replace("_", "");
        int c = 32;
        int length = text.length();
        boolean negative = false;
        char c2 = text.charAt(0);
        c = c2;
        if (c2 == '-' || c == 43) {
            negative = c == 45;
            text = text.substring(1, length);
            --length;
        }
        int radix = 10;
        if (text.charAt(0) == '0' && length > 1) {
            c = text.charAt(1);
            if (c == 88 || c == 120) {
                radix = 16;
                text = text.substring(2, length);
                length -= 2;
            } else if (c == 66 || c == 98) {
                radix = 2;
                text = text.substring(2, length);
                length -= 2;
            } else {
                radix = 8;
            }
        }
        int type = 120;
        if (Numbers.isNumericTypeSpecifier(text.charAt(length - 1), false)) {
            type = Character.toLowerCase(text.charAt(length - 1));
            text = text.substring(0, length - 1);
            --length;
        }
        if (negative) {
            text = "-" + text;
        }
        BigInteger value = new BigInteger(text, radix);
        switch (type) {
            case 105: {
                return value.intValue();
            }
            case 108: {
                return new Long(value.longValue());
            }
            case 103: {
                return value;
            }
        }
        if (value.compareTo(MAX_INTEGER) <= 0 && value.compareTo(MIN_INTEGER) >= 0) {
            return value.intValue();
        }
        if (value.compareTo(MAX_LONG) <= 0 && value.compareTo(MIN_LONG) >= 0) {
            return value.longValue();
        }
        return value;
    }

    public static Number parseDecimal(String text) {
        text = text.replace("_", "");
        int length = text.length();
        int type = 120;
        if (Numbers.isNumericTypeSpecifier(text.charAt(length - 1), true)) {
            type = Character.toLowerCase(text.charAt(length - 1));
            text = text.substring(0, length - 1);
            --length;
        }
        BigDecimal value = new BigDecimal(text);
        switch (type) {
            case 102: {
                if (value.compareTo(MAX_FLOAT) <= 0 && value.compareTo(MIN_FLOAT) >= 0) {
                    return new Float(text);
                }
                throw new NumberFormatException("out of range");
            }
            case 100: {
                if (value.compareTo(MAX_DOUBLE) <= 0 && value.compareTo(MIN_DOUBLE) >= 0) {
                    return new Double(text);
                }
                throw new NumberFormatException("out of range");
            }
        }
        return value;
    }
}

