/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula.functions;

public class BaseNumberUtils {
    public static double convertToDecimal(String value, int base, int maxNumberOfPlaces) throws IllegalArgumentException {
        boolean isNegative;
        char[] characters;
        if (value == null || value.length() == 0) {
            return 0.0;
        }
        long stringLength = value.length();
        if (stringLength > (long)maxNumberOfPlaces) {
            throw new IllegalArgumentException();
        }
        double decimalValue = 0.0;
        long signedDigit = 0L;
        boolean hasSignedDigit = true;
        for (char character : characters = value.toCharArray()) {
            long digit = '0' <= character && character <= '9' ? (long)character - 48L : ('A' <= character && character <= 'Z' ? 10L + (long)(character - 65) : ('a' <= character && character <= 'z' ? 10L + (long)(character - 97) : (long)base));
            if (digit < (long)base) {
                if (hasSignedDigit) {
                    hasSignedDigit = false;
                    signedDigit = digit;
                }
            } else {
                throw new IllegalArgumentException("character not allowed");
            }
            decimalValue = decimalValue * (double)base + (double)digit;
        }
        boolean bl = isNegative = !hasSignedDigit && stringLength == (long)maxNumberOfPlaces && signedDigit >= (long)(base / 2);
        if (isNegative) {
            decimalValue = BaseNumberUtils.getTwoComplement(base, maxNumberOfPlaces, decimalValue);
            decimalValue *= -1.0;
        }
        return decimalValue;
    }

    private static double getTwoComplement(double base, double maxNumberOfPlaces, double decimalValue) {
        return Math.pow(base, maxNumberOfPlaces) - decimalValue;
    }
}

