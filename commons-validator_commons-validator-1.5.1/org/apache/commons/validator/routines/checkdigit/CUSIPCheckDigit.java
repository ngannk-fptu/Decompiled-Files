/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator.routines.checkdigit;

import org.apache.commons.validator.routines.checkdigit.CheckDigit;
import org.apache.commons.validator.routines.checkdigit.CheckDigitException;
import org.apache.commons.validator.routines.checkdigit.ModulusCheckDigit;

public final class CUSIPCheckDigit
extends ModulusCheckDigit {
    private static final long serialVersionUID = 666941918490152456L;
    public static final CheckDigit CUSIP_CHECK_DIGIT = new CUSIPCheckDigit();
    private static final int[] POSITION_WEIGHT = new int[]{2, 1};

    public CUSIPCheckDigit() {
        super(10);
    }

    @Override
    protected int toInt(char character, int leftPos, int rightPos) throws CheckDigitException {
        int charMax;
        int charValue = Character.getNumericValue(character);
        int n = charMax = rightPos == 1 ? 9 : 35;
        if (charValue < 0 || charValue > charMax) {
            throw new CheckDigitException("Invalid Character[" + leftPos + "," + rightPos + "] = '" + charValue + "' out of range 0 to " + charMax);
        }
        return charValue;
    }

    @Override
    protected int weightedValue(int charValue, int leftPos, int rightPos) {
        int weight = POSITION_WEIGHT[rightPos % 2];
        int weightedValue = charValue * weight;
        return ModulusCheckDigit.sumDigits(weightedValue);
    }
}

