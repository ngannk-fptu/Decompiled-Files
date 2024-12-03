/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.factories;

public class RomanNumberFactory {
    private static final RomanDigit[] roman = new RomanDigit[]{new RomanDigit('m', 1000, false), new RomanDigit('d', 500, false), new RomanDigit('c', 100, true), new RomanDigit('l', 50, false), new RomanDigit('x', 10, true), new RomanDigit('v', 5, false), new RomanDigit('i', 1, true)};

    public static String getString(int index) {
        StringBuilder buf = new StringBuilder();
        if (index < 0) {
            buf.append('-');
            index = -index;
        }
        if (index > 3000) {
            buf.append('|');
            buf.append(RomanNumberFactory.getString(index / 1000));
            buf.append('|');
            index -= index / 1000 * 1000;
        }
        int pos = 0;
        while (true) {
            RomanDigit dig = roman[pos];
            while (index >= dig.value) {
                buf.append(dig.digit);
                index -= dig.value;
            }
            if (index <= 0) break;
            int j = pos;
            while (!RomanNumberFactory.roman[++j].pre) {
            }
            if (index + RomanNumberFactory.roman[j].value >= dig.value) {
                buf.append(RomanNumberFactory.roman[j].digit).append(dig.digit);
                index -= dig.value - RomanNumberFactory.roman[j].value;
            }
            ++pos;
        }
        return buf.toString();
    }

    public static String getLowerCaseString(int index) {
        return RomanNumberFactory.getString(index);
    }

    public static String getUpperCaseString(int index) {
        return RomanNumberFactory.getString(index).toUpperCase();
    }

    public static String getString(int index, boolean lowercase) {
        if (lowercase) {
            return RomanNumberFactory.getLowerCaseString(index);
        }
        return RomanNumberFactory.getUpperCaseString(index);
    }

    private static class RomanDigit {
        public char digit;
        public int value;
        public boolean pre;

        RomanDigit(char digit, int value, boolean pre) {
            this.digit = digit;
            this.value = value;
            this.pre = pre;
        }
    }
}

