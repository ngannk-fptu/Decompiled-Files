/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.core.io.doubleparser;

import com.fasterxml.jackson.core.io.doubleparser.AbstractFloatValueParser;
import com.fasterxml.jackson.core.io.doubleparser.AbstractNumberParser;
import com.fasterxml.jackson.core.io.doubleparser.FastDoubleSwar;
import com.fasterxml.jackson.core.io.doubleparser.FastIntegerMath;
import com.fasterxml.jackson.core.io.doubleparser.ParseDigitsTaskCharArray;
import java.math.BigInteger;
import java.util.NavigableMap;

class JavaBigIntegerFromCharArray
extends AbstractNumberParser {
    public static final int MAX_INPUT_LENGTH = 1292782622;
    private static final int MAX_DECIMAL_DIGITS = 646456993;
    private static final int MAX_HEX_DIGITS = 0x20000000;

    JavaBigIntegerFromCharArray() {
    }

    public BigInteger parseBigIntegerLiteral(char[] str, int offset, int length, int radix) throws NumberFormatException {
        try {
            boolean isNegative;
            int endIndex = offset + length;
            if (offset < 0 || endIndex < offset || endIndex > str.length || length > 1292782622) {
                throw new IllegalArgumentException("offset < 0 or length > str.length");
            }
            int index = offset;
            char ch = str[index];
            boolean bl = isNegative = ch == '-';
            if ((isNegative || ch == '+') && (ch = JavaBigIntegerFromCharArray.charAt(str, ++index, endIndex)) == '\u0000') {
                throw new NumberFormatException("illegal syntax");
            }
            switch (radix) {
                case 10: {
                    return this.parseDecDigits(str, index, endIndex, isNegative);
                }
                case 16: {
                    return this.parseHexDigits(str, index, endIndex, isNegative);
                }
            }
            return new BigInteger(new String(str, offset, length), radix);
        }
        catch (ArithmeticException e) {
            NumberFormatException nfe = new NumberFormatException("value exceeds limits");
            nfe.initCause(e);
            throw nfe;
        }
    }

    private BigInteger parseDecDigits(char[] str, int from, int to, boolean isNegative) {
        int numDigits = to - from;
        if (numDigits > 18) {
            return this.parseManyDecDigits(str, from, to, isNegative);
        }
        int preroll = from + (numDigits & 7);
        long significand = FastDoubleSwar.tryToParseUpTo7Digits(str, from, preroll);
        boolean success = significand >= 0L;
        for (from = preroll; from < to; from += 8) {
            int addend = FastDoubleSwar.tryToParseEightDigits(str, from);
            success &= addend >= 0;
            significand = significand * 100000000L + (long)addend;
        }
        if (!success) {
            throw new NumberFormatException("illegal syntax");
        }
        return BigInteger.valueOf(isNegative ? -significand : significand);
    }

    private BigInteger parseHexDigits(char[] str, int from, int to, boolean isNegative) {
        int numDigits = to - (from = this.skipZeroes(str, from, to));
        if (numDigits <= 0) {
            return BigInteger.ZERO;
        }
        if (numDigits > 0x20000000) {
            throw new NumberFormatException("value exceeds limits");
        }
        byte[] bytes = new byte[(numDigits + 1 >> 1) + 1];
        int index = 1;
        boolean illegalDigits = false;
        if ((numDigits & 1) != 0) {
            char chLow = str[from++];
            byte valueLow = AbstractFloatValueParser.CHAR_TO_HEX_MAP[chLow];
            bytes[index++] = valueLow;
            illegalDigits = valueLow < 0;
        }
        int prerollLimit = from + (to - from & 7);
        while (from < prerollLimit) {
            char chHigh = str[from];
            char chLow = str[from + 1];
            int valueHigh = chHigh >= '\u0080' ? -1 : AbstractFloatValueParser.CHAR_TO_HEX_MAP[chHigh];
            int valueLow = chLow >= '\u0080' ? -1 : AbstractFloatValueParser.CHAR_TO_HEX_MAP[chLow];
            bytes[index++] = (byte)(valueHigh << 4 | valueLow);
            illegalDigits |= valueHigh < 0 || valueLow < 0;
            from += 2;
        }
        while (from < to) {
            long value = FastDoubleSwar.tryToParseEightHexDigits(str, from);
            FastDoubleSwar.writeIntBE(bytes, index, (int)value);
            illegalDigits |= value < 0L;
            from += 8;
            index += 4;
        }
        if (illegalDigits) {
            throw new NumberFormatException("illegal syntax");
        }
        BigInteger result = new BigInteger(bytes);
        return isNegative ? result.negate() : result;
    }

    private BigInteger parseManyDecDigits(char[] str, int from, int to, boolean isNegative) {
        int numDigits = to - (from = this.skipZeroes(str, from, to));
        if (numDigits > 646456993) {
            throw new NumberFormatException("value exceeds limits");
        }
        NavigableMap<Integer, BigInteger> powersOfTen = FastIntegerMath.fillPowersOf10Floor16(from, to);
        BigInteger result = ParseDigitsTaskCharArray.parseDigitsRecursive(str, from, to, powersOfTen);
        return isNegative ? result.negate() : result;
    }

    private int skipZeroes(char[] str, int from, int to) {
        while (from < to - 8 && FastDoubleSwar.isEightZeroes(str, from)) {
            from += 8;
        }
        while (from < to && str[from] == '0') {
            ++from;
        }
        return from;
    }
}

