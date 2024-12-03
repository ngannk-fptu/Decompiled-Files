/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.core.io.doubleparser;

import com.fasterxml.jackson.core.io.doubleparser.FastDoubleMath;

class FastFloatMath {
    private static final int FLOAT_EXPONENT_BIAS = 127;
    private static final int FLOAT_SIGNIFICAND_WIDTH = 24;
    private static final int FLOAT_MIN_EXPONENT_POWER_OF_TEN = -45;
    private static final int FLOAT_MAX_EXPONENT_POWER_OF_TEN = 38;
    private static final int FLOAT_MIN_EXPONENT_POWER_OF_TWO = -126;
    private static final int FLOAT_MAX_EXPONENT_POWER_OF_TWO = 127;
    private static final float[] FLOAT_POWER_OF_TEN = new float[]{1.0f, 10.0f, 100.0f, 1000.0f, 10000.0f, 100000.0f, 1000000.0f, 1.0E7f, 1.0E8f, 1.0E9f, 1.0E10f};

    private FastFloatMath() {
    }

    static float decFloatLiteralToFloat(boolean isNegative, long significand, int exponent, boolean isSignificandTruncated, int exponentOfTruncatedSignificand) {
        float result;
        if (significand == 0L) {
            return isNegative ? -0.0f : 0.0f;
        }
        if (isSignificandTruncated) {
            if (-45 <= exponentOfTruncatedSignificand && exponentOfTruncatedSignificand <= 38) {
                float withoutRounding = FastFloatMath.tryDecToFloatWithFastAlgorithm(isNegative, significand, exponentOfTruncatedSignificand);
                float roundedUp = FastFloatMath.tryDecToFloatWithFastAlgorithm(isNegative, significand + 1L, exponentOfTruncatedSignificand);
                if (!Float.isNaN(withoutRounding) && roundedUp == withoutRounding) {
                    return withoutRounding;
                }
            }
            result = Float.NaN;
        } else {
            result = -45 <= exponent && exponent <= 38 ? FastFloatMath.tryDecToFloatWithFastAlgorithm(isNegative, significand, exponent) : Float.NaN;
        }
        return result;
    }

    static float hexFloatLiteralToFloat(boolean isNegative, long significand, int exponent, boolean isSignificandTruncated, int exponentOfTruncatedSignificand) {
        float result;
        if (significand == 0L) {
            return isNegative ? -0.0f : 0.0f;
        }
        if (isSignificandTruncated) {
            if (-126 <= exponentOfTruncatedSignificand && exponentOfTruncatedSignificand <= 127) {
                float withoutRounding = FastFloatMath.tryHexToFloatWithFastAlgorithm(isNegative, significand, exponentOfTruncatedSignificand);
                float roundedUp = FastFloatMath.tryHexToFloatWithFastAlgorithm(isNegative, significand + 1L, exponentOfTruncatedSignificand);
                if (!Double.isNaN(withoutRounding) && roundedUp == withoutRounding) {
                    return withoutRounding;
                }
            }
            result = Float.NaN;
        } else {
            result = -126 <= exponent && exponent <= 127 ? FastFloatMath.tryHexToFloatWithFastAlgorithm(isNegative, significand, exponent) : Float.NaN;
        }
        return result;
    }

    static float tryDecToFloatWithFastAlgorithm(boolean isNegative, long digits, int power) {
        if (-10 <= power && power <= 10 && Long.compareUnsigned(digits, 0xFFFFFFL) <= 0) {
            float d = digits;
            d = power < 0 ? (d /= FLOAT_POWER_OF_TEN[-power]) : (d *= FLOAT_POWER_OF_TEN[power]);
            return isNegative ? -d : d;
        }
        long factorMantissa = FastDoubleMath.MANTISSA_64[power - -325];
        long exponent = (217706L * (long)power >> 16) + 127L + 64L;
        int lz = Long.numberOfLeadingZeros(digits);
        FastDoubleMath.UInt128 product = FastDoubleMath.fullMultiplication(digits <<= lz, factorMantissa);
        long lower = product.low;
        long upper = product.high;
        if ((upper & 0x3FFFFFFFFFL) == 0x3FFFFFFFFFL && Long.compareUnsigned(lower + digits, lower) < 0) {
            long factor_mantissa_low = FastDoubleMath.MANTISSA_128[power - -325];
            product = FastDoubleMath.fullMultiplication(digits, factor_mantissa_low);
            long product_low = product.low;
            long product_middle2 = product.high;
            long product_middle1 = lower;
            long product_high = upper;
            long product_middle = product_middle1 + product_middle2;
            if (Long.compareUnsigned(product_middle, product_middle1) < 0) {
                ++product_high;
            }
            if (product_middle + 1L == 0L && (product_high & 0x7FFFFFFFFFL) == 0x7FFFFFFFFFL && product_low + (long)Long.compareUnsigned(digits, product_low) < 0L) {
                return Float.NaN;
            }
            upper = product_high;
        }
        long upperbit = upper >>> 63;
        long mantissa = upper >>> (int)(upperbit + 38L);
        lz += (int)(1L ^ upperbit);
        if ((upper & 0x3FFFFFFFFFL) == 0x3FFFFFFFFFL || (upper & 0x3FFFFFFFFFL) == 0L && (mantissa & 3L) == 1L) {
            return Float.NaN;
        }
        ++mantissa;
        if ((mantissa >>>= 1) >= 0x1000000L) {
            mantissa = 0x800000L;
            --lz;
        }
        mantissa &= 0xFFFFFFFFFF7FFFFFL;
        long real_exponent = exponent - (long)lz;
        if (real_exponent < 1L || real_exponent > 254L) {
            return Float.NaN;
        }
        int bits = (int)(mantissa | real_exponent << 23 | (isNegative ? 0x80000000L : 0L));
        return Float.intBitsToFloat(bits);
    }

    static float tryHexToFloatWithFastAlgorithm(boolean isNegative, long digits, int power) {
        if (digits == 0L || power < -180) {
            return isNegative ? -0.0f : 0.0f;
        }
        if (power > 127) {
            return isNegative ? Float.NEGATIVE_INFINITY : Float.POSITIVE_INFINITY;
        }
        if (Long.compareUnsigned(digits, 0x1FFFFFFFFFFFFFL) <= 0) {
            float d = digits;
            d *= Math.scalb(1.0f, power);
            if (isNegative) {
                d = -d;
            }
            return d;
        }
        return Float.NaN;
    }
}

