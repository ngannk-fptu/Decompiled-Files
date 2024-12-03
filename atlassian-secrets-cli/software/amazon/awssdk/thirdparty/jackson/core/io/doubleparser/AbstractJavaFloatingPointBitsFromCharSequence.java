/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.thirdparty.jackson.core.io.doubleparser;

import software.amazon.awssdk.thirdparty.jackson.core.io.doubleparser.AbstractFloatValueParser;
import software.amazon.awssdk.thirdparty.jackson.core.io.doubleparser.FastDoubleSwar;

abstract class AbstractJavaFloatingPointBitsFromCharSequence
extends AbstractFloatValueParser {
    AbstractJavaFloatingPointBitsFromCharSequence() {
    }

    private static int skipWhitespace(CharSequence str, int index, int endIndex) {
        while (index < endIndex && str.charAt(index) <= ' ') {
            ++index;
        }
        return index;
    }

    abstract long nan();

    abstract long negativeInfinity();

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
            if (FastDoubleSwar.isDigit(ch)) {
                significand = 10L * significand + (long)ch - 48L;
            } else {
                if (ch != '.') break;
                illegal |= virtualIndexOfPoint >= 0;
                virtualIndexOfPoint = index;
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
        if ((ch | 0x20) == 101) {
            boolean isExponentNegative;
            boolean bl = isExponentNegative = (ch = AbstractJavaFloatingPointBitsFromCharSequence.charAt(str, ++index, endIndex)) == '-';
            if (isExponentNegative || ch == '+') {
                ch = AbstractJavaFloatingPointBitsFromCharSequence.charAt(str, ++index, endIndex);
            }
            illegal |= !FastDoubleSwar.isDigit(ch);
            do {
                if (expNumber >= 1024) continue;
                expNumber = 10 * expNumber + ch - 48;
            } while (FastDoubleSwar.isDigit(ch = AbstractJavaFloatingPointBitsFromCharSequence.charAt(str, ++index, endIndex)));
            if (isExponentNegative) {
                expNumber = -expNumber;
            }
            exponent += expNumber;
        }
        if (ch == 'd' | ch == 'D' | ch == 'f' | ch == 'F') {
            ++index;
        }
        index = AbstractJavaFloatingPointBitsFromCharSequence.skipWhitespace(str, index, endIndex);
        if (illegal || index < endIndex || !hasLeadingZero && digitCount == 0) {
            throw new NumberFormatException("illegal syntax");
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

    public final long parseFloatingPointLiteral(CharSequence str, int offset, int length) {
        boolean hasLeadingZero;
        boolean isNegative;
        int endIndex = offset + length;
        if (offset < 0 || endIndex < offset || endIndex > str.length() || length > 0x7FFFFFFB) {
            throw new IllegalArgumentException("offset < 0 or length > str.length");
        }
        int index = AbstractJavaFloatingPointBitsFromCharSequence.skipWhitespace(str, offset, endIndex);
        if (index == endIndex) {
            throw new NumberFormatException("illegal syntax");
        }
        char ch = str.charAt(index);
        boolean bl = isNegative = ch == '-';
        if ((isNegative || ch == '+') && (ch = AbstractJavaFloatingPointBitsFromCharSequence.charAt(str, ++index, endIndex)) == '\u0000') {
            throw new NumberFormatException("illegal syntax");
        }
        if (ch >= 'I') {
            return this.parseNaNOrInfinity(str, index, endIndex, isNegative);
        }
        boolean bl2 = hasLeadingZero = ch == '0';
        if (hasLeadingZero && ((ch = AbstractJavaFloatingPointBitsFromCharSequence.charAt(str, ++index, endIndex)) == 'x' || ch == 'X')) {
            return this.parseHexFloatLiteral(str, index + 1, offset, endIndex, isNegative);
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
            ch = str.charAt(index);
            int hexValue = AbstractJavaFloatingPointBitsFromCharSequence.lookupHex(ch);
            if (hexValue >= 0) {
                significand = significand << 4 | (long)hexValue;
            } else {
                long parsed;
                if (hexValue != -4) break;
                illegal |= virtualIndexOfPoint >= 0;
                virtualIndexOfPoint = index;
                while (index < endIndex - 8 && (parsed = FastDoubleSwar.tryToParseEightHexDigits(str, index + 1)) >= 0L) {
                    significand = (significand << 32) + parsed;
                    index += 8;
                }
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
        boolean bl = hasExponent = (ch | 0x20) == 112;
        if (hasExponent) {
            boolean isExponentNegative;
            boolean bl2 = isExponentNegative = (ch = AbstractJavaFloatingPointBitsFromCharSequence.charAt(str, ++index, endIndex)) == '-';
            if (isExponentNegative || ch == '+') {
                ch = AbstractJavaFloatingPointBitsFromCharSequence.charAt(str, ++index, endIndex);
            }
            illegal |= !FastDoubleSwar.isDigit(ch);
            do {
                if (expNumber >= 1024) continue;
                expNumber = 10 * expNumber + ch - 48;
            } while (FastDoubleSwar.isDigit(ch = AbstractJavaFloatingPointBitsFromCharSequence.charAt(str, ++index, endIndex)));
            if (isExponentNegative) {
                expNumber = -expNumber;
            }
            exponent += expNumber;
        }
        if (ch == 'd' | ch == 'D' | ch == 'f' | ch == 'F') {
            ++index;
        }
        index = AbstractJavaFloatingPointBitsFromCharSequence.skipWhitespace(str, index, endIndex);
        if (illegal || index < endIndex || digitCount == 0 || !hasExponent) {
            throw new NumberFormatException("illegal syntax");
        }
        int skipCountInTruncatedDigits = 0;
        if (digitCount > 16) {
            significand = 0L;
            for (index = significandStartIndex; index < significandEndIndex; ++index) {
                ch = str.charAt(index);
                int hexValue = AbstractJavaFloatingPointBitsFromCharSequence.lookupHex(ch);
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
        return this.valueOfHexLiteral(str, startIndex, endIndex, isNegative, significand, exponent, isSignificandTruncated, (virtualIndexOfPoint - index + skipCountInTruncatedDigits) * 4 + expNumber);
    }

    private long parseNaNOrInfinity(CharSequence str, int index, int endIndex, boolean isNegative) {
        if (str.charAt(index) == 'N') {
            if (index + 2 < endIndex && str.charAt(index + 1) == 'a' && str.charAt(index + 2) == 'N' && (index = AbstractJavaFloatingPointBitsFromCharSequence.skipWhitespace(str, index + 3, endIndex)) == endIndex) {
                return this.nan();
            }
        } else if (index + 7 < endIndex && str.charAt(index) == 'I' && str.charAt(index + 1) == 'n' && str.charAt(index + 2) == 'f' && str.charAt(index + 3) == 'i' && str.charAt(index + 4) == 'n' && str.charAt(index + 5) == 'i' && str.charAt(index + 6) == 't' && str.charAt(index + 7) == 'y' && (index = AbstractJavaFloatingPointBitsFromCharSequence.skipWhitespace(str, index + 8, endIndex)) == endIndex) {
            return isNegative ? this.negativeInfinity() : this.positiveInfinity();
        }
        throw new NumberFormatException("illegal syntax");
    }

    abstract long positiveInfinity();

    abstract long valueOfFloatLiteral(CharSequence var1, int var2, int var3, boolean var4, long var5, int var7, boolean var8, int var9);

    abstract long valueOfHexLiteral(CharSequence var1, int var2, int var3, boolean var4, long var5, int var7, boolean var8, int var9);
}

