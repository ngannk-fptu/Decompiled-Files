/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator.routines.checkdigit;

import org.apache.commons.validator.routines.checkdigit.CheckDigit;
import org.apache.commons.validator.routines.checkdigit.CheckDigitException;
import org.apache.commons.validator.routines.checkdigit.ModulusCheckDigit;

public final class SedolCheckDigit
extends ModulusCheckDigit {
    private static final long serialVersionUID = -8976881621148878443L;
    private static final int MAX_ALPHANUMERIC_VALUE = 35;
    public static final CheckDigit SEDOL_CHECK_DIGIT = new SedolCheckDigit();
    private static final int[] POSITION_WEIGHT = new int[]{1, 3, 1, 7, 3, 9, 1};

    public SedolCheckDigit() {
        super(10);
    }

    @Override
    protected int calculateModulus(String code, boolean includesCheckDigit) throws CheckDigitException {
        if (code.length() > POSITION_WEIGHT.length) {
            throw new CheckDigitException("Invalid Code Length = " + code.length());
        }
        return super.calculateModulus(code, includesCheckDigit);
    }

    @Override
    protected int weightedValue(int charValue, int leftPos, int rightPos) {
        return charValue * POSITION_WEIGHT[leftPos - 1];
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
}

