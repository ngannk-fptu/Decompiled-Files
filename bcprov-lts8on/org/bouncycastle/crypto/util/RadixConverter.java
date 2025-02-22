/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.crypto.util;

import java.math.BigInteger;
import org.bouncycastle.util.BigIntegers;

public class RadixConverter {
    private static final double LOG_LONG_MAX_VALUE = Math.log(9.223372036854776E18);
    private static final int DEFAULT_POWERS_TO_CACHE = 10;
    private final int digitsGroupLength;
    private final BigInteger digitsGroupSpaceSize;
    private final int radix;
    private final BigInteger[] digitsGroupSpacePowers;

    public RadixConverter(int radix, int numberOfCachedPowers) {
        this.radix = radix;
        this.digitsGroupLength = (int)Math.floor(LOG_LONG_MAX_VALUE / Math.log(radix));
        this.digitsGroupSpaceSize = BigInteger.valueOf(radix).pow(this.digitsGroupLength);
        this.digitsGroupSpacePowers = this.precomputeDigitsGroupPowers(numberOfCachedPowers, this.digitsGroupSpaceSize);
    }

    public RadixConverter(int radix) {
        this(radix, 10);
    }

    public int getRadix() {
        return this.radix;
    }

    public void toEncoding(BigInteger number, int messageLength, short[] out) {
        if (number.signum() < 0) {
            throw new IllegalArgumentException();
        }
        int digitIndex = messageLength - 1;
        do {
            if (number.equals(BigInteger.ZERO)) {
                out[digitIndex--] = 0;
                continue;
            }
            BigInteger[] quotientAndRemainder = number.divideAndRemainder(this.digitsGroupSpaceSize);
            number = quotientAndRemainder[0];
            digitIndex = this.toEncoding(quotientAndRemainder[1].longValue(), digitIndex, out);
        } while (digitIndex >= 0);
        if (number.signum() != 0) {
            throw new IllegalArgumentException();
        }
    }

    private int toEncoding(long number, int digitIndex, short[] out) {
        for (int i = 0; i < this.digitsGroupLength && digitIndex >= 0; ++i) {
            if (number == 0L) {
                out[digitIndex--] = 0;
                continue;
            }
            out[digitIndex--] = (short)(number % (long)this.radix);
            number /= (long)this.radix;
        }
        if (number != 0L) {
            throw new IllegalStateException("Failed to convert decimal number");
        }
        return digitIndex;
    }

    public BigInteger fromEncoding(short[] digits) {
        BigInteger currentGroupCardinality = BigIntegers.ONE;
        BigInteger res = null;
        int indexGroup = 0;
        int numberOfDigits = digits.length;
        for (int groupStartDigitIndex = numberOfDigits - this.digitsGroupLength; groupStartDigitIndex > -this.digitsGroupLength; groupStartDigitIndex -= this.digitsGroupLength) {
            int actualDigitsInGroup = this.digitsGroupLength;
            if (groupStartDigitIndex < 0) {
                actualDigitsInGroup = this.digitsGroupLength + groupStartDigitIndex;
                groupStartDigitIndex = 0;
            }
            int groupEndDigitIndex = Math.min(groupStartDigitIndex + actualDigitsInGroup, numberOfDigits);
            long groupInBaseRadix = this.fromEncoding(groupStartDigitIndex, groupEndDigitIndex, digits);
            BigInteger bigInteger = BigInteger.valueOf(groupInBaseRadix);
            if (indexGroup == 0) {
                res = bigInteger;
            } else {
                currentGroupCardinality = indexGroup <= this.digitsGroupSpacePowers.length ? this.digitsGroupSpacePowers[indexGroup - 1] : currentGroupCardinality.multiply(this.digitsGroupSpaceSize);
                res = res.add(bigInteger.multiply(currentGroupCardinality));
            }
            ++indexGroup;
        }
        return res;
    }

    public int getDigitsGroupLength() {
        return this.digitsGroupLength;
    }

    private long fromEncoding(int groupStartDigitIndex, int groupEndDigitIndex, short[] digits) {
        long decimalNumber = 0L;
        for (int digitIndex = groupStartDigitIndex; digitIndex < groupEndDigitIndex; ++digitIndex) {
            decimalNumber = decimalNumber * (long)this.radix + (long)(digits[digitIndex] & 0xFFFF);
        }
        return decimalNumber;
    }

    private BigInteger[] precomputeDigitsGroupPowers(int numberOfCachedPowers, BigInteger digitsGroupSpaceSize) {
        BigInteger[] cachedPowers = new BigInteger[numberOfCachedPowers];
        BigInteger currentPower = digitsGroupSpaceSize;
        for (int i = 0; i < numberOfCachedPowers; ++i) {
            cachedPowers[i] = currentPower;
            currentPower = currentPower.multiply(digitsGroupSpaceSize);
        }
        return cachedPowers;
    }
}

