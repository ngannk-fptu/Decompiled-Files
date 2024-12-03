/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.core.io.doubleparser;

import com.fasterxml.jackson.core.io.doubleparser.AbstractFloatValueParser;
import com.fasterxml.jackson.core.io.doubleparser.FastDoubleSwar;

abstract class AbstractFloatingPointBitsFromCharSequence
extends AbstractFloatValueParser {
    AbstractFloatingPointBitsFromCharSequence() {
    }

    private boolean isDigit(char c) {
        return '0' <= c && c <= '9';
    }

    private long parseDecFloatLiteral(CharSequence str, int index, int startIndex, int endIndex, boolean isNegative, boolean hasLeadingZero) {
        int exponentOfTruncatedSignificand;
        boolean isSignificandTruncated;
        int exponent;
        int digitCount;
        long significand = 0L;
        int significandStartIndex = index;
        int virtualIndexOfPoint = -1;
        boolean illegal = false;
        char ch = '\u0000';
        while (index < endIndex) {
            ch = str.charAt(index);
            if (this.isDigit(ch)) {
                significand = 10L * significand + (long)ch - 48L;
            } else {
                int eightDigits;
                if (ch != '.') break;
                illegal |= virtualIndexOfPoint >= 0;
                virtualIndexOfPoint = index;
                while (index < endIndex - 8 && (eightDigits = this.tryToParseEightDigits(str, index + 1)) >= 0) {
                    significand = 100000000L * significand + (long)eightDigits;
                    index += 8;
                }
            }
            ++index;
        }
        int significandEndIndex = index;
        if (virtualIndexOfPoint < 0) {
            digitCount = significandEndIndex - significandStartIndex;
            virtualIndexOfPoint = significandEndIndex;
            exponent = 0;
        } else {
            digitCount = significandEndIndex - significandStartIndex - 1;
            exponent = virtualIndexOfPoint - significandEndIndex + 1;
        }
        int expNumber = 0;
        if (ch == 'e' || ch == 'E') {
            boolean neg_exp;
            ch = ++index < endIndex ? str.charAt(index) : (char)'\u0000';
            boolean bl = neg_exp = ch == '-';
            if (neg_exp || ch == '+') {
                ch = ++index < endIndex ? str.charAt(index) : (char)'\u0000';
            }
            illegal |= !this.isDigit(ch);
            do {
                if (expNumber >= 1024) continue;
                expNumber = 10 * expNumber + ch - 48;
            } while (this.isDigit(ch = ++index < endIndex ? str.charAt(index) : (char)'\u0000'));
            if (neg_exp) {
                expNumber = -expNumber;
            }
            exponent += expNumber;
        }
        if (index < endIndex && (ch == 'd' || ch == 'D' || ch == 'f' || ch == 'F')) {
            ++index;
        }
        index = this.skipWhitespace(str, index, endIndex);
        if (illegal || index < endIndex || !hasLeadingZero && digitCount == 0) {
            return -1L;
        }
        int skipCountInTruncatedDigits = 0;
        if (digitCount > 19) {
            significand = 0L;
            for (index = significandStartIndex; index < significandEndIndex; ++index) {
                ch = str.charAt(index);
                if (ch == '.') {
                    ++skipCountInTruncatedDigits;
                    continue;
                }
                if (Long.compareUnsigned(significand, 1000000000000000000L) >= 0) break;
                significand = 10L * significand + (long)ch - 48L;
            }
            isSignificandTruncated = index < significandEndIndex;
            exponentOfTruncatedSignificand = virtualIndexOfPoint - index + skipCountInTruncatedDigits + expNumber;
        } else {
            isSignificandTruncated = false;
            exponentOfTruncatedSignificand = 0;
        }
        return this.valueOfFloatLiteral(str, startIndex, endIndex, isNegative, significand, exponent, isSignificandTruncated, exponentOfTruncatedSignificand);
    }

    public long parseFloatingPointLiteral(CharSequence str, int offset, int length) {
        boolean hasLeadingZero;
        boolean isNegative;
        int endIndex = offset + length;
        if (offset < 0 || endIndex > str.length()) {
            return -1L;
        }
        int index = this.skipWhitespace(str, offset, endIndex);
        if (index == endIndex) {
            return -1L;
        }
        char ch = str.charAt(index);
        boolean bl = isNegative = ch == '-';
        if (isNegative || ch == '+') {
            char c = ch = ++index < endIndex ? str.charAt(index) : (char)'\u0000';
            if (ch == '\u0000') {
                return -1L;
            }
        }
        if (ch >= 'I') {
            return ch == 'N' ? this.parseNaN(str, index, endIndex) : this.parseInfinity(str, index, endIndex, isNegative);
        }
        boolean bl2 = hasLeadingZero = ch == '0';
        if (hasLeadingZero) {
            char c = ch = ++index < endIndex ? str.charAt(index) : (char)'\u0000';
            if (ch == 'x' || ch == 'X') {
                return this.parseHexFloatLiteral(str, index + 1, offset, endIndex, isNegative);
            }
        }
        return this.parseDecFloatLiteral(str, index, offset, endIndex, isNegative, hasLeadingZero);
    }

    private long parseHexFloatLiteral(CharSequence str, int index, int startIndex, int endIndex, boolean isNegative) {
        boolean isSignificandTruncated;
        boolean hasExponent;
        int digitCount;
        long significand = 0L;
        int exponent = 0;
        int significandStartIndex = index;
        int virtualIndexOfPoint = -1;
        boolean illegal = false;
        char ch = '\u0000';
        while (index < endIndex) {
            int hexValue;
            ch = str.charAt(index);
            int n = hexValue = ch > '\u007f' ? -1 : AbstractFloatValueParser.CHAR_TO_HEX_MAP[ch];
            if (hexValue >= 0) {
                significand = significand << 4 | (long)hexValue;
            } else {
                if (hexValue != -4) break;
                illegal |= virtualIndexOfPoint >= 0;
                virtualIndexOfPoint = index;
            }
            ++index;
        }
        int significandEndIndex = index;
        if (virtualIndexOfPoint < 0) {
            digitCount = significandEndIndex - significandStartIndex;
            virtualIndexOfPoint = significandEndIndex;
        } else {
            digitCount = significandEndIndex - significandStartIndex - 1;
            exponent = Math.min(virtualIndexOfPoint - index + 1, 1024) * 4;
        }
        int expNumber = 0;
        boolean bl = hasExponent = ch == 'p' || ch == 'P';
        if (hasExponent) {
            boolean neg_exp;
            ch = ++index < endIndex ? str.charAt(index) : (char)'\u0000';
            boolean bl2 = neg_exp = ch == '-';
            if (neg_exp || ch == '+') {
                ch = ++index < endIndex ? str.charAt(index) : (char)'\u0000';
            }
            illegal |= !this.isDigit(ch);
            do {
                if (expNumber >= 1024) continue;
                expNumber = 10 * expNumber + ch - 48;
            } while (this.isDigit(ch = ++index < endIndex ? str.charAt(index) : (char)'\u0000'));
            if (neg_exp) {
                expNumber = -expNumber;
            }
            exponent += expNumber;
        }
        if (index < endIndex && (ch == 'd' || ch == 'D' || ch == 'f' || ch == 'F')) {
            ++index;
        }
        index = this.skipWhitespace(str, index, endIndex);
        if (illegal || index < endIndex || digitCount == 0 || !hasExponent) {
            return -1L;
        }
        int skipCountInTruncatedDigits = 0;
        if (digitCount > 16) {
            significand = 0L;
            for (index = significandStartIndex; index < significandEndIndex; ++index) {
                int hexValue;
                ch = str.charAt(index);
                int n = hexValue = ch > '\u007f' ? -1 : AbstractFloatValueParser.CHAR_TO_HEX_MAP[ch];
                if (hexValue >= 0) {
                    if (Long.compareUnsigned(significand, 1000000000000000000L) >= 0) break;
                    significand = significand << 4 | (long)hexValue;
                    continue;
                }
                ++skipCountInTruncatedDigits;
            }
            isSignificandTruncated = index < significandEndIndex;
        } else {
            isSignificandTruncated = false;
        }
        return this.valueOfHexLiteral(str, startIndex, endIndex, isNegative, significand, exponent, isSignificandTruncated, virtualIndexOfPoint - index + skipCountInTruncatedDigits + expNumber);
    }

    private long parseInfinity(CharSequence str, int index, int endIndex, boolean negative) {
        if (index + 7 < endIndex && str.charAt(index) == 'I' && str.charAt(index + 1) == 'n' && str.charAt(index + 2) == 'f' && str.charAt(index + 3) == 'i' && str.charAt(index + 4) == 'n' && str.charAt(index + 5) == 'i' && str.charAt(index + 6) == 't' && str.charAt(index + 7) == 'y' && (index = this.skipWhitespace(str, index + 8, endIndex)) == endIndex) {
            return negative ? this.negativeInfinity() : this.positiveInfinity();
        }
        return -1L;
    }

    private long parseNaN(CharSequence str, int index, int endIndex) {
        if (index + 2 < endIndex && str.charAt(index + 1) == 'a' && str.charAt(index + 2) == 'N' && (index = this.skipWhitespace(str, index + 3, endIndex)) == endIndex) {
            return this.nan();
        }
        return -1L;
    }

    private int skipWhitespace(CharSequence str, int index, int endIndex) {
        while (index < endIndex && str.charAt(index) <= ' ') {
            ++index;
        }
        return index;
    }

    private int tryToParseEightDigits(CharSequence str, int offset) {
        long first = (long)str.charAt(offset) | (long)str.charAt(offset + 1) << 16 | (long)str.charAt(offset + 2) << 32 | (long)str.charAt(offset + 3) << 48;
        long second = (long)str.charAt(offset + 4) | (long)str.charAt(offset + 5) << 16 | (long)str.charAt(offset + 6) << 32 | (long)str.charAt(offset + 7) << 48;
        return FastDoubleSwar.tryToParseEightDigitsUtf16(first, second);
    }

    abstract long nan();

    abstract long negativeInfinity();

    abstract long positiveInfinity();

    abstract long valueOfFloatLiteral(CharSequence var1, int var2, int var3, boolean var4, long var5, int var7, boolean var8, int var9);

    abstract long valueOfHexLiteral(CharSequence var1, int var2, int var3, boolean var4, long var5, int var7, boolean var8, int var9);
}

