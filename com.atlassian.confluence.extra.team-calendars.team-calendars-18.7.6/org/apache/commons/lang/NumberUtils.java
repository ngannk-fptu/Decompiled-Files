/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.lang;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.apache.commons.lang.StringUtils;

public final class NumberUtils {
    public static int stringToInt(String str) {
        return NumberUtils.stringToInt(str, 0);
    }

    public static int stringToInt(String str, int defaultValue) {
        try {
            return Integer.parseInt(str);
        }
        catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }

    public static Number createNumber(String val) throws NumberFormatException {
        String mant;
        String dec;
        if (val == null) {
            return null;
        }
        if (val.length() == 0) {
            throw new NumberFormatException("\"\" is not a valid number.");
        }
        if (val.length() == 1 && !Character.isDigit(val.charAt(0))) {
            throw new NumberFormatException(val + " is not a valid number.");
        }
        if (val.startsWith("--")) {
            return null;
        }
        if (val.startsWith("0x") || val.startsWith("-0x")) {
            return NumberUtils.createInteger(val);
        }
        char lastChar = val.charAt(val.length() - 1);
        int decPos = val.indexOf(46);
        int expPos = val.indexOf(101) + val.indexOf(69) + 1;
        if (decPos > -1) {
            if (expPos > -1) {
                if (expPos < decPos) {
                    throw new NumberFormatException(val + " is not a valid number.");
                }
                dec = val.substring(decPos + 1, expPos);
            } else {
                dec = val.substring(decPos + 1);
            }
            mant = val.substring(0, decPos);
        } else {
            mant = expPos > -1 ? val.substring(0, expPos) : val;
            dec = null;
        }
        if (!Character.isDigit(lastChar)) {
            String exp = expPos > -1 && expPos < val.length() - 1 ? val.substring(expPos + 1, val.length() - 1) : null;
            String numeric = val.substring(0, val.length() - 1);
            boolean allZeros = NumberUtils.isAllZeros(mant) && NumberUtils.isAllZeros(exp);
            switch (lastChar) {
                case 'L': 
                case 'l': {
                    if (dec == null && exp == null && (numeric.charAt(0) == '-' && NumberUtils.isDigits(numeric.substring(1)) || NumberUtils.isDigits(numeric))) {
                        try {
                            return NumberUtils.createLong(numeric);
                        }
                        catch (NumberFormatException nfe) {
                            return NumberUtils.createBigInteger(numeric);
                        }
                    }
                    throw new NumberFormatException(val + " is not a valid number.");
                }
                case 'F': 
                case 'f': {
                    try {
                        Float f = NumberUtils.createFloat(numeric);
                        if (!f.isInfinite() && (f.floatValue() != 0.0f || allZeros)) {
                            return f;
                        }
                    }
                    catch (NumberFormatException e) {
                        // empty catch block
                    }
                }
                case 'D': 
                case 'd': {
                    try {
                        Double d = NumberUtils.createDouble(numeric);
                        if (!d.isInfinite() && ((double)d.floatValue() != 0.0 || allZeros)) {
                            return d;
                        }
                    }
                    catch (NumberFormatException nfe) {
                        // empty catch block
                    }
                    try {
                        return NumberUtils.createBigDecimal(numeric);
                    }
                    catch (NumberFormatException e) {
                        // empty catch block
                    }
                }
            }
            throw new NumberFormatException(val + " is not a valid number.");
        }
        String exp = expPos > -1 && expPos < val.length() - 1 ? val.substring(expPos + 1, val.length()) : null;
        if (dec == null && exp == null) {
            try {
                return NumberUtils.createInteger(val);
            }
            catch (NumberFormatException nfe) {
                try {
                    return NumberUtils.createLong(val);
                }
                catch (NumberFormatException nfe2) {
                    return NumberUtils.createBigInteger(val);
                }
            }
        }
        boolean allZeros = NumberUtils.isAllZeros(mant) && NumberUtils.isAllZeros(exp);
        try {
            Float f = NumberUtils.createFloat(val);
            if (!f.isInfinite() && (f.floatValue() != 0.0f || allZeros)) {
                return f;
            }
        }
        catch (NumberFormatException nfe) {
            // empty catch block
        }
        try {
            Double d = NumberUtils.createDouble(val);
            if (!d.isInfinite() && (d != 0.0 || allZeros)) {
                return d;
            }
        }
        catch (NumberFormatException nfe) {
            // empty catch block
        }
        return NumberUtils.createBigDecimal(val);
    }

    private static boolean isAllZeros(String s) {
        if (s == null) {
            return true;
        }
        for (int i = s.length() - 1; i >= 0; --i) {
            if (s.charAt(i) == '0') continue;
            return false;
        }
        return s.length() > 0;
    }

    public static Float createFloat(String val) {
        return Float.valueOf(val);
    }

    public static Double createDouble(String val) {
        return Double.valueOf(val);
    }

    public static Integer createInteger(String val) {
        return Integer.decode(val);
    }

    public static Long createLong(String val) {
        return Long.valueOf(val);
    }

    public static BigInteger createBigInteger(String val) {
        BigInteger bi = new BigInteger(val);
        return bi;
    }

    public static BigDecimal createBigDecimal(String val) {
        BigDecimal bd = new BigDecimal(val);
        return bd;
    }

    public static long minimum(long a, long b, long c) {
        if (b < a) {
            a = b;
        }
        if (c < a) {
            a = c;
        }
        return a;
    }

    public static int minimum(int a, int b, int c) {
        if (b < a) {
            a = b;
        }
        if (c < a) {
            a = c;
        }
        return a;
    }

    public static long maximum(long a, long b, long c) {
        if (b > a) {
            a = b;
        }
        if (c > a) {
            a = c;
        }
        return a;
    }

    public static int maximum(int a, int b, int c) {
        if (b > a) {
            a = b;
        }
        if (c > a) {
            a = c;
        }
        return a;
    }

    public static int compare(double lhs, double rhs) {
        long rhsBits;
        if (lhs < rhs) {
            return -1;
        }
        if (lhs > rhs) {
            return 1;
        }
        long lhsBits = Double.doubleToLongBits(lhs);
        if (lhsBits == (rhsBits = Double.doubleToLongBits(rhs))) {
            return 0;
        }
        if (lhsBits < rhsBits) {
            return -1;
        }
        return 1;
    }

    public static int compare(float lhs, float rhs) {
        int rhsBits;
        if (lhs < rhs) {
            return -1;
        }
        if (lhs > rhs) {
            return 1;
        }
        int lhsBits = Float.floatToIntBits(lhs);
        if (lhsBits == (rhsBits = Float.floatToIntBits(rhs))) {
            return 0;
        }
        if (lhsBits < rhsBits) {
            return -1;
        }
        return 1;
    }

    public static boolean isDigits(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        for (int i = 0; i < str.length(); ++i) {
            if (Character.isDigit(str.charAt(i))) continue;
            return false;
        }
        return true;
    }

    public static boolean isNumber(String str) {
        int i;
        int start;
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        char[] chars = str.toCharArray();
        int sz = chars.length;
        boolean hasExp = false;
        boolean hasDecPoint = false;
        boolean allowSigns = false;
        boolean foundDigit = false;
        int n = start = chars[0] == '-' ? 1 : 0;
        if (sz > start + 1 && chars[start] == '0' && chars[start + 1] == 'x') {
            int i2 = start + 2;
            if (i2 == sz) {
                return false;
            }
            while (i2 < chars.length) {
                if (!(chars[i2] >= '0' && chars[i2] <= '9' || chars[i2] >= 'a' && chars[i2] <= 'f' || chars[i2] >= 'A' && chars[i2] <= 'F')) {
                    return false;
                }
                ++i2;
            }
            return true;
        }
        --sz;
        for (i = start; i < sz || i < sz + 1 && allowSigns && !foundDigit; ++i) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                foundDigit = true;
                allowSigns = false;
                continue;
            }
            if (chars[i] == '.') {
                if (hasDecPoint || hasExp) {
                    return false;
                }
                hasDecPoint = true;
                continue;
            }
            if (chars[i] == 'e' || chars[i] == 'E') {
                if (hasExp) {
                    return false;
                }
                if (!foundDigit) {
                    return false;
                }
                hasExp = true;
                allowSigns = true;
                continue;
            }
            if (chars[i] == '+' || chars[i] == '-') {
                if (!allowSigns) {
                    return false;
                }
                allowSigns = false;
                foundDigit = false;
                continue;
            }
            return false;
        }
        if (i < chars.length) {
            if (chars[i] >= '0' && chars[i] <= '9') {
                return true;
            }
            if (chars[i] == 'e' || chars[i] == 'E') {
                return false;
            }
            if (!(allowSigns || chars[i] != 'd' && chars[i] != 'D' && chars[i] != 'f' && chars[i] != 'F')) {
                return foundDigit;
            }
            if (chars[i] == 'l' || chars[i] == 'L') {
                return foundDigit && !hasExp;
            }
            return false;
        }
        return !allowSigns && foundDigit;
    }
}

