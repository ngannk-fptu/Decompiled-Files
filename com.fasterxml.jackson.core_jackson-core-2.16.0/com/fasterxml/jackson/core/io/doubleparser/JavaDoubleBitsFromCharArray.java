/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.core.io.doubleparser;

import com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromCharArray;
import com.fasterxml.jackson.core.io.doubleparser.FastDoubleMath;

final class JavaDoubleBitsFromCharArray
extends AbstractJavaFloatingPointBitsFromCharArray {
    @Override
    long nan() {
        return Double.doubleToRawLongBits(Double.NaN);
    }

    @Override
    long negativeInfinity() {
        return Double.doubleToRawLongBits(Double.NEGATIVE_INFINITY);
    }

    @Override
    long positiveInfinity() {
        return Double.doubleToRawLongBits(Double.POSITIVE_INFINITY);
    }

    @Override
    long valueOfFloatLiteral(char[] str, int startIndex, int endIndex, boolean isNegative, long significand, int exponent, boolean isSignificandTruncated, int exponentOfTruncatedSignificand) {
        double d = FastDoubleMath.tryDecFloatToDoubleTruncated(isNegative, significand, exponent, isSignificandTruncated, exponentOfTruncatedSignificand);
        return Double.doubleToRawLongBits(Double.isNaN(d) ? Double.parseDouble(new String(str, startIndex, endIndex - startIndex)) : d);
    }

    @Override
    long valueOfHexLiteral(char[] str, int startIndex, int endIndex, boolean isNegative, long significand, int exponent, boolean isSignificandTruncated, int exponentOfTruncatedSignificand) {
        double d = FastDoubleMath.tryHexFloatToDoubleTruncated(isNegative, significand, exponent, isSignificandTruncated, exponentOfTruncatedSignificand);
        return Double.doubleToRawLongBits(Double.isNaN(d) ? Double.parseDouble(new String(str, startIndex, endIndex - startIndex)) : d);
    }
}

