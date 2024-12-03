/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.utils;

import java.util.List;

public class StringUtils {
    public static final String EMPTY = "";
    public static final String SPACE = " ";
    static int PAD_LIMIT = 10000;

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isBlank(String s) {
        return s == null || s.trim().length() == 0;
    }

    public static String leftPad(String str, int size, String padStr) {
        if (str == null) {
            return null;
        }
        if (StringUtils.isEmpty(padStr)) {
            padStr = SPACE;
        }
        int padLen = padStr.length();
        int strLen = str.length();
        int pads = size - strLen;
        if (pads <= 0) {
            return str;
        }
        if (padLen == 1 && pads <= PAD_LIMIT) {
            return StringUtils.leftPad(str, size, padStr.charAt(0));
        }
        if (pads == padLen) {
            return padStr.concat(str);
        }
        if (pads < padLen) {
            return padStr.substring(0, pads).concat(str);
        }
        char[] padding = new char[pads];
        char[] padChars = padStr.toCharArray();
        for (int i = 0; i < pads; ++i) {
            padding[i] = padChars[i % padLen];
        }
        return new String(padding).concat(str);
    }

    public static String leftPad(String str, int size, char padChar) {
        if (str == null) {
            return null;
        }
        int pads = size - str.length();
        if (pads <= 0) {
            return str;
        }
        if (pads > PAD_LIMIT) {
            return StringUtils.leftPad(str, size, String.valueOf(padChar));
        }
        return StringUtils.repeat(padChar, pads).concat(str);
    }

    public static String repeat(char ch, int repeat) {
        if (repeat <= 0) {
            return EMPTY;
        }
        char[] buf = new char[repeat];
        for (int i = repeat - 1; i >= 0; --i) {
            buf[i] = ch;
        }
        return new String(buf);
    }

    public static String repeat(String str, int repeat) {
        if (str == null) {
            return null;
        }
        if (repeat <= 0) {
            return EMPTY;
        }
        int inputLength = str.length();
        if (repeat == 1 || inputLength == 0) {
            return str;
        }
        if (inputLength == 1 && repeat <= PAD_LIMIT) {
            return StringUtils.repeat(str.charAt(0), repeat);
        }
        int outputLength = inputLength * repeat;
        switch (inputLength) {
            case 1: {
                return StringUtils.repeat(str.charAt(0), repeat);
            }
            case 2: {
                char ch0 = str.charAt(0);
                char ch1 = str.charAt(1);
                char[] output2 = new char[outputLength];
                for (int i = repeat * 2 - 2; i >= 0; --i) {
                    output2[i] = ch0;
                    output2[i + 1] = ch1;
                    --i;
                }
                return new String(output2);
            }
        }
        StringBuilder buf = new StringBuilder(outputLength);
        for (int i = 0; i < repeat; ++i) {
            buf.append(str);
        }
        return buf.toString();
    }

    public static String joinWith(String delimiter, List<String> lines) {
        if (lines.size() == 0) {
            return EMPTY;
        }
        int i = 0;
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            if (i++ > 0) {
                sb.append(delimiter);
            }
            sb.append(line);
        }
        return sb.toString();
    }
}

