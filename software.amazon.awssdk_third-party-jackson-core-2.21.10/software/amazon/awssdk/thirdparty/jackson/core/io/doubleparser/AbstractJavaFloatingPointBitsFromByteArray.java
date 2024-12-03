/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.thirdparty.jackson.core.io.doubleparser;

import software.amazon.awssdk.thirdparty.jackson.core.io.doubleparser.AbstractFloatValueParser;
import software.amazon.awssdk.thirdparty.jackson.core.io.doubleparser.FastDoubleSwar;

abstract class AbstractJavaFloatingPointBitsFromByteArray
extends AbstractFloatValueParser {
    AbstractJavaFloatingPointBitsFromByteArray() {
    }

    private static int skipWhitespace(byte[] str, int index, int endIndex) {
        while (index < endIndex && (str[index] & 0xFF) <= 32) {
            ++index;
        }
        return index;
    }

    abstract long nan();

    abstract long negativeInfinity();

    private long parseDecFloatLiteral(byte[] str, int index, int startIndex, int endIndex, boolean isNegative, boolean hasLeadingZero) {
        int exponentOfTruncatedSignificand;
        boolean isSignificandTruncated;
        int exponent;
        int digitCount;
        long significand = 0L;
        int significandStartIndex = index;
        int virtualIndexOfPoint = -1;
        boolean illegal = false;
        byte ch = 0;
        while (index < endIndex) {
            ch = str[index];
            if (FastDoubleSwar.isDigit(ch)) {
                significand = 10L * significand + (long)ch - 48L;
            } else {
                int digits;
                if (ch != 46) break;
                illegal |= virtualIndexOfPoint >= 0;
                virtualIndexOfPoint = index;
                while (index < endIndex - 4 && (digits = FastDoubleSwar.tryToParseFourDigits(str, index + 1)) >= 0) {
                    significand = 10000L * significand + (long)digits;
                    index += 4;
                }
            }
            ++index;
        }
        int significandEndIndex = index;
        if (virtualIndexOfPoint < 0) {
            digitCount = index - significandStartIndex;
            virtualIndexOfPoint = index;
            exponent = 0;
        } else {
            digitCount = index - significandStartIndex - 1;
            exponent = virtualIndexOfPoint - index + 1;
        }
        int expNumber = 0;
        if ((ch | 0x20) == 101) {
            boolean isExponentNegative;
            boolean bl = isExponentNegative = (ch = AbstractJavaFloatingPointBitsFromByteArray.charAt(str, ++index, endIndex)) == 45;
            if (isExponentNegative || ch == 43) {
                ch = AbstractJavaFloatingPointBitsFromByteArray.charAt(str, ++index, endIndex);
            }
            illegal |= !FastDoubleSwar.isDigit(ch);
            do {
                if (expNumber >= 1024) continue;
                expNumber = 10 * expNumber + ch - 48;
            } while (FastDoubleSwar.isDigit(ch = AbstractJavaFloatingPointBitsFromByteArray.charAt(str, ++index, endIndex)));
            if (isExponentNegative) {
                expNumber = -expNumber;
            }
            exponent += expNumber;
        }
        if (ch == 100 | ch == 68 | ch == 102 | ch == 70) {
            ++index;
        }
        index = AbstractJavaFloatingPointBitsFromByteArray.skipWhitespace(str, index, endIndex);
        if (illegal || index < endIndex || !hasLeadingZero && digitCount == 0) {
            throw new NumberFormatException("illegal syntax");
        }
        int skipCountInTruncatedDigits = 0;
        if (digitCount > 19) {
            significand = 0L;
            for (index = significandStartIndex; index < significandEndIndex; ++index) {
                ch = str[index];
                if (ch == 46) {
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

    public long parseFloatingPointLiteral(byte[] str, int offset, int length) {
        boolean hasLeadingZero;
        boolean isNegative;
        int endIndex = offset + length;
        if (offset < 0 || endIndex < offset || endIndex > str.length || length > 0x7FFFFFFB) {
            throw new IllegalArgumentException("offset < 0 or length > str.length");
        }
        int index = AbstractJavaFloatingPointBitsFromByteArray.skipWhitespace(str, offset, endIndex);
        if (index == endIndex) {
            throw new NumberFormatException("illegal syntax");
        }
        byte ch = str[index];
        boolean bl = isNegative = ch == 45;
        if ((isNegative || ch == 43) && (ch = AbstractJavaFloatingPointBitsFromByteArray.charAt(str, ++index, endIndex)) == 0) {
            throw new NumberFormatException("illegal syntax");
        }
        if (ch >= 73) {
            return this.parseNaNOrInfinity(str, index, endIndex, isNegative);
        }
        boolean bl2 = hasLeadingZero = ch == 48;
        if (hasLeadingZero && ((ch = AbstractJavaFloatingPointBitsFromByteArray.charAt(str, ++index, endIndex)) == 120 || ch == 88)) {
            return this.parseHexFloatingPointLiteral(str, index + 1, offset, endIndex, isNegative);
        }
        return this.parseDecFloatLiteral(str, index, offset, endIndex, isNegative, hasLeadingZero);
    }

    private long parseHexFloatingPointLiteral(byte[] str, int index, int startIndex, int endIndex, boolean isNegative) {
        boolean isSignificandTruncated;
        boolean hasExponent;
        int digitCount;
        long significand = 0L;
        int exponent = 0;
        int significandStartIndex = index;
        int virtualIndexOfPoint = -1;
        boolean illegal = false;
        byte ch = 0;
        while (index < endIndex) {
            ch = str[index];
            int hexValue = AbstractJavaFloatingPointBitsFromByteArray.lookupHex(ch);
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
        boolean bl = hasExponent = (ch | 0x20) == 112;
        if (hasExponent) {
            boolean isExponentNegative;
            boolean bl2 = isExponentNegative = (ch = AbstractJavaFloatingPointBitsFromByteArray.charAt(str, ++index, endIndex)) == 45;
            if (isExponentNegative || ch == 43) {
                ch = AbstractJavaFloatingPointBitsFromByteArray.charAt(str, ++index, endIndex);
            }
            illegal |= !FastDoubleSwar.isDigit(ch);
            do {
                if (expNumber >= 1024) continue;
                expNumber = 10 * expNumber + ch - 48;
            } while (FastDoubleSwar.isDigit(ch = AbstractJavaFloatingPointBitsFromByteArray.charAt(str, ++index, endIndex)));
            if (isExponentNegative) {
                expNumber = -expNumber;
            }
            exponent += expNumber;
        }
        if (ch == 100 | ch == 68 | ch == 102 | ch == 70) {
            ++index;
        }
        index = AbstractJavaFloatingPointBitsFromByteArray.skipWhitespace(str, index, endIndex);
        if (illegal || index < endIndex || digitCount == 0 || !hasExponent) {
            throw new NumberFormatException("illegal syntax");
        }
        int skipCountInTruncatedDigits = 0;
        if (digitCount > 16) {
            significand = 0L;
            for (index = significandStartIndex; index < significandEndIndex; ++index) {
                ch = str[index];
                int hexValue = AbstractJavaFloatingPointBitsFromByteArray.lookupHex(ch);
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

    private long parseNaNOrInfinity(byte[] str, int index, int endIndex, boolean isNegative) {
        if (str[index] == 78) {
            if (index + 2 < endIndex && str[index + 1] == 97 && str[index + 2] == 78 && (index = AbstractJavaFloatingPointBitsFromByteArray.skipWhitespace(str, index + 3, endIndex)) == endIndex) {
                return this.nan();
            }
        } else if (index + 7 < endIndex && FastDoubleSwar.readLongLE(str, index) == 8751735898823355977L && (index = AbstractJavaFloatingPointBitsFromByteArray.skipWhitespace(str, index + 8, endIndex)) == endIndex) {
            return isNegative ? this.negativeInfinity() : this.positiveInfinity();
        }
        throw new NumberFormatException("illegal syntax");
    }

    abstract long positiveInfinity();

    abstract long valueOfFloatLiteral(byte[] var1, int var2, int var3, boolean var4, long var5, int var7, boolean var8, int var9);

    abstract long valueOfHexLiteral(byte[] var1, int var2, int var3, boolean var4, long var5, int var7, boolean var8, int var9);
}

