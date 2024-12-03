/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.io;

public final class NumberInput {
    public static final String NASTY_SMALL_DOUBLE = "2.2250738585072012e-308";
    static final long L_BILLION = 1000000000L;
    static final String MIN_LONG_STR_NO_SIGN = String.valueOf(Long.MIN_VALUE).substring(1);
    static final String MAX_LONG_STR = String.valueOf(Long.MAX_VALUE);

    public static final int parseInt(char[] digitChars, int offset, int len) {
        int num = digitChars[offset] - 48;
        if (++offset < (len += offset)) {
            num = num * 10 + (digitChars[offset] - 48);
            if (++offset < len) {
                num = num * 10 + (digitChars[offset] - 48);
                if (++offset < len) {
                    num = num * 10 + (digitChars[offset] - 48);
                    if (++offset < len) {
                        num = num * 10 + (digitChars[offset] - 48);
                        if (++offset < len) {
                            num = num * 10 + (digitChars[offset] - 48);
                            if (++offset < len) {
                                num = num * 10 + (digitChars[offset] - 48);
                                if (++offset < len) {
                                    num = num * 10 + (digitChars[offset] - 48);
                                    if (++offset < len) {
                                        num = num * 10 + (digitChars[offset] - 48);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return num;
    }

    public static final int parseInt(String str) {
        char c = str.charAt(0);
        int length = str.length();
        boolean negative = c == '-';
        int offset = 1;
        if (negative) {
            if (length == 1 || length > 10) {
                return Integer.parseInt(str);
            }
            c = str.charAt(offset++);
        } else if (length > 9) {
            return Integer.parseInt(str);
        }
        if (c > '9' || c < '0') {
            return Integer.parseInt(str);
        }
        int num = c - 48;
        if (offset < length) {
            if ((c = str.charAt(offset++)) > '9' || c < '0') {
                return Integer.parseInt(str);
            }
            num = num * 10 + (c - 48);
            if (offset < length) {
                if ((c = str.charAt(offset++)) > '9' || c < '0') {
                    return Integer.parseInt(str);
                }
                num = num * 10 + (c - 48);
                if (offset < length) {
                    do {
                        if ((c = str.charAt(offset++)) > '9' || c < '0') {
                            return Integer.parseInt(str);
                        }
                        num = num * 10 + (c - 48);
                    } while (offset < length);
                }
            }
        }
        return negative ? -num : num;
    }

    public static final long parseLong(char[] digitChars, int offset, int len) {
        int len1 = len - 9;
        long val = (long)NumberInput.parseInt(digitChars, offset, len1) * 1000000000L;
        return val + (long)NumberInput.parseInt(digitChars, offset + len1, 9);
    }

    public static final long parseLong(String str) {
        int length = str.length();
        if (length <= 9) {
            return NumberInput.parseInt(str);
        }
        return Long.parseLong(str);
    }

    public static final boolean inLongRange(char[] digitChars, int offset, int len, boolean negative) {
        String cmpStr = negative ? MIN_LONG_STR_NO_SIGN : MAX_LONG_STR;
        int cmpLen = cmpStr.length();
        if (len < cmpLen) {
            return true;
        }
        if (len > cmpLen) {
            return false;
        }
        for (int i = 0; i < cmpLen; ++i) {
            int diff = digitChars[offset + i] - cmpStr.charAt(i);
            if (diff == 0) continue;
            return diff < 0;
        }
        return true;
    }

    public static final boolean inLongRange(String numberStr, boolean negative) {
        String cmpStr = negative ? MIN_LONG_STR_NO_SIGN : MAX_LONG_STR;
        int cmpLen = cmpStr.length();
        int actualLen = numberStr.length();
        if (actualLen < cmpLen) {
            return true;
        }
        if (actualLen > cmpLen) {
            return false;
        }
        for (int i = 0; i < cmpLen; ++i) {
            int diff = numberStr.charAt(i) - cmpStr.charAt(i);
            if (diff == 0) continue;
            return diff < 0;
        }
        return true;
    }

    public static int parseAsInt(String input, int defaultValue) {
        char c;
        if (input == null) {
            return defaultValue;
        }
        int len = (input = input.trim()).length();
        if (len == 0) {
            return defaultValue;
        }
        int i = 0;
        if (i < len) {
            c = input.charAt(0);
            if (c == '+') {
                input = input.substring(1);
                len = input.length();
            } else if (c == '-') {
                ++i;
            }
        }
        while (i < len) {
            c = input.charAt(i);
            if (c > '9' || c < '0') {
                try {
                    return (int)NumberInput.parseDouble(input);
                }
                catch (NumberFormatException e) {
                    return defaultValue;
                }
            }
            ++i;
        }
        try {
            return Integer.parseInt(input);
        }
        catch (NumberFormatException numberFormatException) {
            return defaultValue;
        }
    }

    public static long parseAsLong(String input, long defaultValue) {
        char c;
        if (input == null) {
            return defaultValue;
        }
        int len = (input = input.trim()).length();
        if (len == 0) {
            return defaultValue;
        }
        int i = 0;
        if (i < len) {
            c = input.charAt(0);
            if (c == '+') {
                input = input.substring(1);
                len = input.length();
            } else if (c == '-') {
                ++i;
            }
        }
        while (i < len) {
            c = input.charAt(i);
            if (c > '9' || c < '0') {
                try {
                    return (long)NumberInput.parseDouble(input);
                }
                catch (NumberFormatException e) {
                    return defaultValue;
                }
            }
            ++i;
        }
        try {
            return Long.parseLong(input);
        }
        catch (NumberFormatException numberFormatException) {
            return defaultValue;
        }
    }

    public static double parseAsDouble(String input, double defaultValue) {
        if (input == null) {
            return defaultValue;
        }
        int len = (input = input.trim()).length();
        if (len == 0) {
            return defaultValue;
        }
        try {
            return NumberInput.parseDouble(input);
        }
        catch (NumberFormatException numberFormatException) {
            return defaultValue;
        }
    }

    public static final double parseDouble(String numStr) throws NumberFormatException {
        if (NASTY_SMALL_DOUBLE.equals(numStr)) {
            return Double.MIN_NORMAL;
        }
        return Double.parseDouble(numStr);
    }
}

