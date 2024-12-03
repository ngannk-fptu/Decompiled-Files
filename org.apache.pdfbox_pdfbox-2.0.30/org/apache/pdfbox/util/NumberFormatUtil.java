/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.util;

public class NumberFormatUtil {
    private static final int MAX_FRACTION_DIGITS = 5;
    private static final long[] POWER_OF_TENS;
    private static final int[] POWER_OF_TENS_INT;

    private NumberFormatUtil() {
    }

    public static int formatFloatFast(float value, int maxFractionDigits, byte[] asciiBuffer) {
        long fractionPart;
        if (Float.isNaN(value) || Float.isInfinite(value) || value > 9.223372E18f || value <= -9.223372E18f || maxFractionDigits > 5) {
            return -1;
        }
        int offset = 0;
        long integerPart = (long)value;
        if (value < 0.0f) {
            asciiBuffer[offset++] = 45;
            integerPart = -integerPart;
        }
        if ((fractionPart = (long)((Math.abs((double)value) - (double)integerPart) * (double)POWER_OF_TENS[maxFractionDigits] + 0.5)) >= POWER_OF_TENS[maxFractionDigits]) {
            ++integerPart;
            fractionPart -= POWER_OF_TENS[maxFractionDigits];
        }
        offset = NumberFormatUtil.formatPositiveNumber(integerPart, NumberFormatUtil.getExponent(integerPart), false, asciiBuffer, offset);
        if (fractionPart > 0L && maxFractionDigits > 0) {
            asciiBuffer[offset++] = 46;
            offset = NumberFormatUtil.formatPositiveNumber(fractionPart, maxFractionDigits - 1, true, asciiBuffer, offset);
        }
        return offset;
    }

    private static int formatPositiveNumber(long number, int exp, boolean omitTrailingZeros, byte[] asciiBuffer, int startOffset) {
        int digit;
        int offset = startOffset;
        long remaining = number;
        while (!(remaining <= Integer.MAX_VALUE || omitTrailingZeros && remaining <= 0L)) {
            long digit2 = remaining / POWER_OF_TENS[exp];
            remaining -= digit2 * POWER_OF_TENS[exp];
            asciiBuffer[offset++] = (byte)(48L + digit2);
            --exp;
        }
        for (int remainingInt = (int)remaining; !(exp < 0 || omitTrailingZeros && remainingInt <= 0); remainingInt -= digit * POWER_OF_TENS_INT[exp], --exp) {
            digit = remainingInt / POWER_OF_TENS_INT[exp];
            asciiBuffer[offset++] = (byte)(48 + digit);
        }
        return offset;
    }

    private static int getExponent(long number) {
        for (int exp = 0; exp < POWER_OF_TENS.length - 1; ++exp) {
            if (number >= POWER_OF_TENS[exp + 1]) continue;
            return exp;
        }
        return POWER_OF_TENS.length - 1;
    }

    static {
        int exp;
        POWER_OF_TENS = new long[19];
        NumberFormatUtil.POWER_OF_TENS[0] = 1L;
        for (exp = 1; exp < POWER_OF_TENS.length; ++exp) {
            NumberFormatUtil.POWER_OF_TENS[exp] = POWER_OF_TENS[exp - 1] * 10L;
        }
        POWER_OF_TENS_INT = new int[10];
        NumberFormatUtil.POWER_OF_TENS_INT[0] = 1;
        for (exp = 1; exp < POWER_OF_TENS_INT.length; ++exp) {
            NumberFormatUtil.POWER_OF_TENS_INT[exp] = POWER_OF_TENS_INT[exp - 1] * 10;
        }
    }
}

