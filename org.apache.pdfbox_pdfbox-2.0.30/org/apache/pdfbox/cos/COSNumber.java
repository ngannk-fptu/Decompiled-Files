/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.cos;

import java.io.IOException;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSInteger;

public abstract class COSNumber
extends COSBase {
    @Deprecated
    public static final COSInteger ZERO = COSInteger.ZERO;
    @Deprecated
    public static final COSInteger ONE = COSInteger.ONE;

    public abstract float floatValue();

    public abstract double doubleValue();

    public abstract int intValue();

    public abstract long longValue();

    public static COSNumber get(String number) throws IOException {
        if (number.length() == 1) {
            char digit = number.charAt(0);
            if ('0' <= digit && digit <= '9') {
                return COSInteger.get((long)digit - 48L);
            }
            if (digit == '-' || digit == '.') {
                return COSInteger.ZERO;
            }
            throw new IOException("Not a number: " + number);
        }
        if (COSNumber.isFloat(number)) {
            return new COSFloat(number);
        }
        try {
            if (number.charAt(0) == '+') {
                return COSInteger.get(Long.parseLong(number.substring(1)));
            }
            return COSInteger.get(Long.parseLong(number));
        }
        catch (NumberFormatException e) {
            String numberString;
            String string = numberString = number.startsWith("+") || number.startsWith("-") ? number.substring(1) : number;
            if (!numberString.matches("[0-9]*")) {
                throw new IOException("Not a number: " + number);
            }
            return number.startsWith("-") ? COSInteger.OUT_OF_RANGE_MIN : COSInteger.OUT_OF_RANGE_MAX;
        }
    }

    private static boolean isFloat(String number) {
        int length = number.length();
        for (int i = 0; i < length; ++i) {
            char digit = number.charAt(i);
            if (digit != '.' && digit != 'e') continue;
            return true;
        }
        return false;
    }
}

