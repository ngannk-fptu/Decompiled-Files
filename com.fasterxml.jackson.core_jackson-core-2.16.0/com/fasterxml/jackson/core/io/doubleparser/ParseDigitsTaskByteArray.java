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

class ParseDigitsTaskByteArray {
    public static final int RECURSION_THRESHOLD = 400;

    private ParseDigitsTaskByteArray() {
    }

    static BigInteger parseDigitsIterative(byte[] str, int from, int to) {
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

    static BigInteger parseDigitsRecursive(byte[] str, int from, int to, Map<Integer, BigInteger> powersOfTen) {
        int numDigits = to - from;
        if (numDigits <= 400) {
            return ParseDigitsTaskByteArray.parseDigitsIterative(str, from, to);
        }
        int mid = FastIntegerMath.splitFloor16(from, to);
        BigInteger high = ParseDigitsTaskByteArray.parseDigitsRecursive(str, from, mid, powersOfTen);
        BigInteger low = ParseDigitsTaskByteArray.parseDigitsRecursive(str, mid, to, powersOfTen);
        high = FftMultiplier.multiply(high, powersOfTen.get(to - mid));
        return low.add(high);
    }
}

