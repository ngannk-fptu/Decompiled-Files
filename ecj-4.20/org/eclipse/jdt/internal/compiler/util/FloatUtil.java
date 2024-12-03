/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jdt.internal.compiler.util;

public class FloatUtil {
    private static final int DOUBLE_FRACTION_WIDTH = 52;
    private static final int DOUBLE_PRECISION = 53;
    private static final int MAX_DOUBLE_EXPONENT = 1023;
    private static final int MIN_NORMALIZED_DOUBLE_EXPONENT = -1022;
    private static final int MIN_UNNORMALIZED_DOUBLE_EXPONENT = -1075;
    private static final int DOUBLE_EXPONENT_BIAS = 1023;
    private static final int DOUBLE_EXPONENT_SHIFT = 52;
    private static final int SINGLE_FRACTION_WIDTH = 23;
    private static final int SINGLE_PRECISION = 24;
    private static final int MAX_SINGLE_EXPONENT = 127;
    private static final int MIN_NORMALIZED_SINGLE_EXPONENT = -126;
    private static final int MIN_UNNORMALIZED_SINGLE_EXPONENT = -150;
    private static final int SINGLE_EXPONENT_BIAS = 127;
    private static final int SINGLE_EXPONENT_SHIFT = 23;

    public static float valueOfHexFloatLiteral(char[] source) {
        long bits = FloatUtil.convertHexFloatingPointLiteralToBits(source);
        return Float.intBitsToFloat((int)bits);
    }

    public static double valueOfHexDoubleLiteral(char[] source) {
        long bits = FloatUtil.convertHexFloatingPointLiteralToBits(source);
        return Double.longBitsToDouble(bits);
    }

    private static long convertHexFloatingPointLiteralToBits(char[] source) {
        int e;
        long fraction;
        int length = source.length;
        long mantissa = 0L;
        int next = 0;
        char nextChar = source[next];
        nextChar = source[next];
        if (nextChar != '0') {
            throw new NumberFormatException();
        }
        nextChar = source[++next];
        if (nextChar == 'X' || nextChar == 'x') {
            ++next;
        } else {
            throw new NumberFormatException();
        }
        int binaryPointPosition = -1;
        block19: while (true) {
            nextChar = source[next];
            switch (nextChar) {
                case '0': {
                    ++next;
                    continue block19;
                }
                case '.': {
                    binaryPointPosition = next++;
                    continue block19;
                }
            }
            break;
        }
        int mantissaBits = 0;
        int leadingDigitPosition = -1;
        block20: while (true) {
            int hexdigit;
            nextChar = source[next];
            switch (nextChar) {
                case '0': 
                case '1': 
                case '2': 
                case '3': 
                case '4': 
                case '5': 
                case '6': 
                case '7': 
                case '8': 
                case '9': {
                    hexdigit = nextChar - 48;
                    break;
                }
                case 'a': 
                case 'b': 
                case 'c': 
                case 'd': 
                case 'e': 
                case 'f': {
                    hexdigit = nextChar - 97 + 10;
                    break;
                }
                case 'A': 
                case 'B': 
                case 'C': 
                case 'D': 
                case 'E': 
                case 'F': {
                    hexdigit = nextChar - 65 + 10;
                    break;
                }
                case '.': {
                    binaryPointPosition = next++;
                    continue block20;
                }
                default: {
                    if (binaryPointPosition >= 0) break block20;
                    binaryPointPosition = next;
                    break block20;
                }
            }
            if (mantissaBits == 0) {
                leadingDigitPosition = next;
                mantissa = hexdigit;
                mantissaBits = 4;
            } else if (mantissaBits < 60) {
                mantissa <<= 4;
                mantissa |= (long)hexdigit;
                mantissaBits += 4;
            }
            ++next;
        }
        if ((nextChar = source[next]) == 'P' || nextChar == 'p') {
            ++next;
        } else {
            throw new NumberFormatException();
        }
        int exponent = 0;
        int exponentSign = 1;
        block21: while (next < length) {
            nextChar = source[next];
            switch (nextChar) {
                case '+': {
                    exponentSign = 1;
                    ++next;
                    break;
                }
                case '-': {
                    exponentSign = -1;
                    ++next;
                    break;
                }
                case '0': 
                case '1': 
                case '2': 
                case '3': 
                case '4': 
                case '5': 
                case '6': 
                case '7': 
                case '8': 
                case '9': {
                    int digit = nextChar - 48;
                    exponent = exponent * 10 + digit;
                    ++next;
                    break;
                }
                default: {
                    break block21;
                }
            }
        }
        boolean doublePrecision = true;
        if (next < length) {
            nextChar = source[next];
            switch (nextChar) {
                case 'F': 
                case 'f': {
                    doublePrecision = false;
                    ++next;
                    break;
                }
                case 'D': 
                case 'd': {
                    doublePrecision = true;
                    ++next;
                    break;
                }
                default: {
                    throw new NumberFormatException();
                }
            }
        }
        if (mantissa == 0L) {
            return 0L;
        }
        int scaleFactorCompensation = 0;
        long top = mantissa >>> mantissaBits - 4;
        if ((top & 8L) == 0L) {
            --mantissaBits;
            ++scaleFactorCompensation;
            if ((top & 4L) == 0L) {
                --mantissaBits;
                ++scaleFactorCompensation;
                if ((top & 2L) == 0L) {
                    --mantissaBits;
                    ++scaleFactorCompensation;
                }
            }
        }
        long result = 0L;
        if (doublePrecision) {
            int e2;
            long fraction2;
            if (mantissaBits > 53) {
                int extraBits = mantissaBits - 53;
                fraction2 = mantissa >>> extraBits - 1;
                long lowBit = fraction2 & 1L;
                fraction2 += lowBit;
                if (((fraction2 >>>= 1) & 0x20000000000000L) != 0L) {
                    fraction2 >>>= 1;
                    --scaleFactorCompensation;
                }
            } else {
                fraction2 = mantissa << 53 - mantissaBits;
            }
            int scaleFactor = 0;
            if (mantissaBits > 0) {
                if (leadingDigitPosition < binaryPointPosition) {
                    scaleFactor = 4 * (binaryPointPosition - leadingDigitPosition);
                    scaleFactor -= scaleFactorCompensation;
                } else {
                    scaleFactor = -4 * (leadingDigitPosition - binaryPointPosition - 1);
                    scaleFactor -= scaleFactorCompensation;
                }
            }
            if ((e2 = exponentSign * exponent + scaleFactor) - 1 > 1023) {
                result = Double.doubleToLongBits(Double.POSITIVE_INFINITY);
            } else if (e2 - 1 >= -1022) {
                long biasedExponent = e2 - 1 + 1023;
                result = fraction2 & 0xFFEFFFFFFFFFFFFFL;
                result |= biasedExponent << 52;
            } else if (e2 - 1 > -1075) {
                long biasedExponent = 0L;
                result = fraction2 >>> -1022 - e2 + 1;
                result |= biasedExponent << 52;
            } else {
                result = Double.doubleToLongBits(Double.NaN);
            }
            return result;
        }
        if (mantissaBits > 24) {
            int extraBits = mantissaBits - 24;
            fraction = mantissa >>> extraBits - 1;
            long lowBit = fraction & 1L;
            fraction += lowBit;
            if (((fraction >>>= 1) & 0x1000000L) != 0L) {
                fraction >>>= 1;
                --scaleFactorCompensation;
            }
        } else {
            fraction = mantissa << 24 - mantissaBits;
        }
        int scaleFactor = 0;
        if (mantissaBits > 0) {
            if (leadingDigitPosition < binaryPointPosition) {
                scaleFactor = 4 * (binaryPointPosition - leadingDigitPosition);
                scaleFactor -= scaleFactorCompensation;
            } else {
                scaleFactor = -4 * (leadingDigitPosition - binaryPointPosition - 1);
                scaleFactor -= scaleFactorCompensation;
            }
        }
        if ((e = exponentSign * exponent + scaleFactor) - 1 > 127) {
            result = Float.floatToIntBits(Float.POSITIVE_INFINITY);
        } else if (e - 1 >= -126) {
            long biasedExponent = e - 1 + 127;
            result = fraction & 0xFFFFFFFFFF7FFFFFL;
            result |= biasedExponent << 23;
        } else if (e - 1 > -150) {
            long biasedExponent = 0L;
            result = fraction >>> -126 - e + 1;
            result |= biasedExponent << 23;
        } else {
            result = Float.floatToIntBits(Float.NaN);
        }
        return result;
    }
}

