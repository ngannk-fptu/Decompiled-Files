/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.converter;

import java.util.Locale;

public final class NumberFormatter {
    private static final String[] ROMAN_LETTERS = new String[]{"m", "cm", "d", "cd", "c", "xc", "l", "xl", "x", "ix", "v", "iv", "i"};
    private static final int[] ROMAN_VALUES = new int[]{1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
    private static final int T_ARABIC = 0;
    private static final int T_LOWER_LETTER = 4;
    private static final int T_LOWER_ROMAN = 2;
    private static final int T_ORDINAL = 5;
    private static final int T_UPPER_LETTER = 3;
    private static final int T_UPPER_ROMAN = 1;

    public static String getNumber(int num, int style) {
        switch (style) {
            case 1: {
                return NumberFormatter.toRoman(num).toUpperCase(Locale.ROOT);
            }
            case 2: {
                return NumberFormatter.toRoman(num);
            }
            case 3: {
                return NumberFormatter.toLetters(num).toUpperCase(Locale.ROOT);
            }
            case 4: {
                return NumberFormatter.toLetters(num);
            }
        }
        return String.valueOf(num);
    }

    private static String toLetters(int number) {
        if (number <= 0) {
            throw new IllegalArgumentException("Unsupported number: " + number);
        }
        int num = number;
        int radix = 26;
        char[] buf = new char[33];
        int charPos = buf.length;
        while (num > 0) {
            int remainder = --num % 26;
            buf[--charPos] = (char)(97 + remainder);
            num = (num - remainder) / 26;
        }
        return new String(buf, charPos, buf.length - charPos);
    }

    private static String toRoman(int number) {
        if (number <= 0) {
            throw new IllegalArgumentException("Unsupported number: " + number);
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < ROMAN_LETTERS.length; ++i) {
            String letter = ROMAN_LETTERS[i];
            int value = ROMAN_VALUES[i];
            while (number >= value) {
                number -= value;
                result.append(letter);
            }
        }
        return result.toString();
    }
}

