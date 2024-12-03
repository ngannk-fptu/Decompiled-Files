/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.thirdparty.jackson.core.io.doubleparser;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.NavigableMap;
import software.amazon.awssdk.thirdparty.jackson.core.io.doubleparser.AbstractNumberParser;
import software.amazon.awssdk.thirdparty.jackson.core.io.doubleparser.FastDoubleSwar;
import software.amazon.awssdk.thirdparty.jackson.core.io.doubleparser.FastIntegerMath;
import software.amazon.awssdk.thirdparty.jackson.core.io.doubleparser.FftMultiplier;
import software.amazon.awssdk.thirdparty.jackson.core.io.doubleparser.ParseDigitsTaskByteArray;

final class JavaBigDecimalFromByteArray
extends AbstractNumberParser {
    public static final int MAX_INPUT_LENGTH = 1292782635;
    public static final int MANY_DIGITS_THRESHOLD = 32;
    private static final long MAX_EXPONENT_NUMBER = Integer.MAX_VALUE;
    private static final int MAX_DIGIT_COUNT = 1292782621;

    public BigDecimal parseBigDecimalString(byte[] str, int offset, int length) {
        try {
            int exponentIndicatorIndex;
            long exponent;
            int digitCount;
            boolean isNegative;
            int index;
            if (length >= 32) {
                return this.parseBigDecimalStringWithManyDigits(str, offset, length);
            }
            long significand = 0L;
            int decimalPointIndex = -1;
            int endIndex = offset + length;
            byte ch = JavaBigDecimalFromByteArray.charAt(str, index, endIndex);
            boolean illegal = false;
            boolean bl = isNegative = ch == 45;
            if ((isNegative || ch == 43) && (ch = JavaBigDecimalFromByteArray.charAt(str, ++index, endIndex)) == 0) {
                throw new NumberFormatException("illegal syntax");
            }
            int integerPartIndex = index;
            for (index = offset; index < endIndex; ++index) {
                int digits;
                ch = str[index];
                if (FastDoubleSwar.isDigit(ch)) {
                    significand = 10L * significand + (long)ch - 48L;
                    continue;
                }
                if (ch != 46) break;
                illegal |= decimalPointIndex >= 0;
                decimalPointIndex = index;
                while (index < endIndex - 4 && (digits = FastDoubleSwar.tryToParseFourDigits(str, index + 1)) >= 0) {
                    significand = 10000L * significand + (long)digits;
                    index += 4;
                }
            }
            int significandEndIndex = index;
            if (decimalPointIndex < 0) {
                digitCount = significandEndIndex - integerPartIndex;
                decimalPointIndex = significandEndIndex;
                exponent = 0L;
            } else {
                digitCount = significandEndIndex - integerPartIndex - 1;
                exponent = decimalPointIndex - significandEndIndex + 1;
            }
            long expNumber = 0L;
            if ((ch | 0x20) == 101) {
                boolean isExponentNegative;
                exponentIndicatorIndex = index++;
                ch = JavaBigDecimalFromByteArray.charAt(str, index, endIndex);
                boolean bl2 = isExponentNegative = ch == 45;
                if (isExponentNegative || ch == 43) {
                    ch = JavaBigDecimalFromByteArray.charAt(str, ++index, endIndex);
                }
                illegal |= !FastDoubleSwar.isDigit(ch);
                do {
                    if (expNumber >= Integer.MAX_VALUE) continue;
                    expNumber = 10L * expNumber + (long)ch - 48L;
                } while (FastDoubleSwar.isDigit(ch = JavaBigDecimalFromByteArray.charAt(str, ++index, endIndex)));
                if (isExponentNegative) {
                    expNumber = -expNumber;
                }
                exponent += expNumber;
            } else {
                exponentIndicatorIndex = endIndex;
            }
            if (illegal || index < endIndex || digitCount == 0 || digitCount > 1292782621) {
                throw new NumberFormatException("illegal syntax");
            }
            if (exponent <= Integer.MIN_VALUE || exponent > Integer.MAX_VALUE) {
                throw new NumberFormatException("value exceeds limits");
            }
            if (digitCount <= 18) {
                return new BigDecimal(isNegative ? -significand : significand).scaleByPowerOfTen((int)exponent);
            }
            return this.valueOfBigDecimalString(str, integerPartIndex, decimalPointIndex, decimalPointIndex + 1, exponentIndicatorIndex, isNegative, (int)exponent);
        }
        catch (ArithmeticException e) {
            NumberFormatException nfe = new NumberFormatException("value exceeds limits");
            nfe.initCause(e);
            throw nfe;
        }
    }

    BigDecimal parseBigDecimalStringWithManyDigits(byte[] str, int offset, int length) {
        int exponentIndicatorIndex;
        long exponent;
        int digitCount;
        boolean isNegative;
        int index;
        if (length > 1292782635) {
            throw new NumberFormatException("illegal syntax");
        }
        int decimalPointIndex = -1;
        int nonZeroFractionalPartIndex = -1;
        int endIndex = offset + length;
        byte ch = JavaBigDecimalFromByteArray.charAt(str, index, endIndex);
        boolean illegal = false;
        boolean bl = isNegative = ch == 45;
        if ((isNegative || ch == 43) && (ch = JavaBigDecimalFromByteArray.charAt(str, ++index, endIndex)) == 0) {
            throw new NumberFormatException("illegal syntax");
        }
        int integerPartIndex = index;
        for (index = offset; index < endIndex - 8 && FastDoubleSwar.isEightZeroes(str, index); index += 8) {
        }
        while (index < endIndex && str[index] == 48) {
            ++index;
        }
        int nonZeroIntegerPartIndex = index;
        while (index < endIndex - 8 && FastDoubleSwar.isEightDigits(str, index)) {
            index += 8;
        }
        while (index < endIndex && FastDoubleSwar.isDigit(ch = str[index])) {
            ++index;
        }
        if (ch == 46) {
            decimalPointIndex = index++;
            while (index < endIndex - 8 && FastDoubleSwar.isEightZeroes(str, index)) {
                index += 8;
            }
            while (index < endIndex && str[index] == 48) {
                ++index;
            }
            nonZeroFractionalPartIndex = index;
            while (index < endIndex - 8 && FastDoubleSwar.isEightDigits(str, index)) {
                index += 8;
            }
            while (index < endIndex && FastDoubleSwar.isDigit(ch = str[index])) {
                ++index;
            }
        }
        int significandEndIndex = index;
        if (decimalPointIndex < 0) {
            digitCount = significandEndIndex - nonZeroIntegerPartIndex;
            decimalPointIndex = significandEndIndex;
            nonZeroFractionalPartIndex = significandEndIndex;
            exponent = 0L;
        } else {
            digitCount = nonZeroIntegerPartIndex == decimalPointIndex ? significandEndIndex - nonZeroFractionalPartIndex : significandEndIndex - nonZeroIntegerPartIndex - 1;
            exponent = decimalPointIndex - significandEndIndex + 1;
        }
        long expNumber = 0L;
        if ((ch | 0x20) == 101) {
            boolean isExponentNegative;
            exponentIndicatorIndex = index++;
            ch = JavaBigDecimalFromByteArray.charAt(str, index, endIndex);
            boolean bl2 = isExponentNegative = ch == 45;
            if (isExponentNegative || ch == 43) {
                ch = JavaBigDecimalFromByteArray.charAt(str, ++index, endIndex);
            }
            boolean bl3 = illegal = !FastDoubleSwar.isDigit(ch);
            do {
                if (expNumber >= Integer.MAX_VALUE) continue;
                expNumber = 10L * expNumber + (long)ch - 48L;
            } while (FastDoubleSwar.isDigit(ch = JavaBigDecimalFromByteArray.charAt(str, ++index, endIndex)));
            if (isExponentNegative) {
                expNumber = -expNumber;
            }
            exponent += expNumber;
        } else {
            exponentIndicatorIndex = endIndex;
        }
        if (illegal || index < endIndex) {
            throw new NumberFormatException("illegal syntax");
        }
        if (exponentIndicatorIndex - integerPartIndex == 0) {
            throw new NumberFormatException("illegal syntax");
        }
        if (exponent < Integer.MIN_VALUE || exponent > Integer.MAX_VALUE || digitCount > 1292782621) {
            throw new NumberFormatException("value exceeds limits");
        }
        return this.valueOfBigDecimalString(str, nonZeroIntegerPartIndex, decimalPointIndex, nonZeroFractionalPartIndex, exponentIndicatorIndex, isNegative, (int)exponent);
    }

    private BigDecimal valueOfBigDecimalString(byte[] str, int integerPartIndex, int decimalPointIndex, int nonZeroFractionalPartIndex, int exponentIndicatorIndex, boolean isNegative, int exponent) {
        BigInteger significand;
        BigInteger integerPart;
        int fractionDigitsCount = exponentIndicatorIndex - decimalPointIndex - 1;
        int nonZeroFractionDigitsCount = exponentIndicatorIndex - nonZeroFractionalPartIndex;
        int integerDigitsCount = decimalPointIndex - integerPartIndex;
        NavigableMap<Integer, BigInteger> powersOfTen = null;
        if (integerDigitsCount > 0) {
            if (integerDigitsCount > 400) {
                powersOfTen = FastIntegerMath.createPowersOfTenFloor16Map();
                FastIntegerMath.fillPowersOfNFloor16Recursive(powersOfTen, integerPartIndex, decimalPointIndex);
                integerPart = ParseDigitsTaskByteArray.parseDigitsRecursive(str, integerPartIndex, decimalPointIndex, powersOfTen);
            } else {
                integerPart = ParseDigitsTaskByteArray.parseDigitsRecursive(str, integerPartIndex, decimalPointIndex, null);
            }
        } else {
            integerPart = BigInteger.ZERO;
        }
        if (fractionDigitsCount > 0) {
            BigInteger fractionalPart;
            if (nonZeroFractionDigitsCount > 400) {
                if (powersOfTen == null) {
                    powersOfTen = FastIntegerMath.createPowersOfTenFloor16Map();
                }
                FastIntegerMath.fillPowersOfNFloor16Recursive(powersOfTen, nonZeroFractionalPartIndex, exponentIndicatorIndex);
                fractionalPart = ParseDigitsTaskByteArray.parseDigitsRecursive(str, nonZeroFractionalPartIndex, exponentIndicatorIndex, powersOfTen);
            } else {
                fractionalPart = ParseDigitsTaskByteArray.parseDigitsRecursive(str, nonZeroFractionalPartIndex, exponentIndicatorIndex, null);
            }
            if (integerPart.signum() == 0) {
                significand = fractionalPart;
            } else {
                BigInteger integerFactor = FastIntegerMath.computePowerOfTen(powersOfTen, fractionDigitsCount);
                significand = FftMultiplier.multiply(integerPart, integerFactor).add(fractionalPart);
            }
        } else {
            significand = integerPart;
        }
        return new BigDecimal(isNegative ? significand.negate() : significand, -exponent);
    }
}

