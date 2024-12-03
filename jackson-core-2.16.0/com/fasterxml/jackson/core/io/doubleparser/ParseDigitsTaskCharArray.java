/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.core.io.doubleparser;

import com.fasterxml.jackson.core.io.doubleparser.BigSignificand;
import com.fasterxml.jackson.core.io.doubleparser.FastDoubleSwar;
import com.fasterxml.jackson.core.io.doubleparser.FastIntegerMath;
import com.fasterxml.jackson.core.io.doubleparser.FftMultiplier;
import java.math.BigInteger;
import java.util.Map;

class ParseDigitsTaskCharArray {
    static final int RECURSION_THRESHOLD = 400;

    private ParseDigitsTaskCharArray() {
    }

    static BigInteger parseDigitsIterative(char[] str, int from, int to) {
        int numDigits = to - from;
        BigSignificand bigSignificand = new BigSignificand(FastIntegerMath.estimateNumBits(numDigits));
        int preroll = from + (numDigits & 7);
        int value = FastDoubleSwar.tryToParseUpTo7Digits(str, from, preroll);
        boolean success = value >= 0;
        bigSignificand.add(value);
        for (from = preroll; from < to; from += 8) {
            int addend = FastDoubleSwar.tryToParseEightDigits(str, from);
            success &= addend >= 0;
            bigSignificand.fma(100000000, addend);
        }
        if (!success) {
            throw new NumberFormatException("illegal syntax");
        }
        return bigSignificand.toBigInteger();
    }

    static BigInteger parseDigitsRecursive(char[] str, int from, int to, Map<Integer, BigInteger> powersOfTen) {
        int numDigits = to - from;
        if (numDigits <= 400) {
            return ParseDigitsTaskCharArray.parseDigitsIterative(str, from, to);
        }
        int mid = FastIntegerMath.splitFloor16(from, to);
        BigInteger high = ParseDigitsTaskCharArray.parseDigitsRecursive(str, from, mid, powersOfTen);
        BigInteger low = ParseDigitsTaskCharArray.parseDigitsRecursive(str, mid, to, powersOfTen);
        high = FftMultiplier.multiply(high, powersOfTen.get(to - mid));
        return low.add(high);
    }
}

