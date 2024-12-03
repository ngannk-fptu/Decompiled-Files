/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator.routines.checkdigit;

import org.apache.commons.validator.routines.checkdigit.CheckDigit;
import org.apache.commons.validator.routines.checkdigit.CheckDigitException;
import org.apache.commons.validator.routines.checkdigit.ModulusCheckDigit;

public final class ISINCheckDigit
extends ModulusCheckDigit {
    private static final long serialVersionUID = -1239211208101323599L;
    private static final int MAX_ALPHANUMERIC_VALUE = 35;
    public static final CheckDigit ISIN_CHECK_DIGIT = new ISINCheckDigit();
    private static final int[] POSITION_WEIGHT = new int[]{2, 1};

    public ISINCheckDigit() {
        super(10);
    }

    @Override
    protected int calculateModulus(String code, boolean includesCheckDigit) throws CheckDigitException {
        char checkDigit;
        StringBuilder transformed = new StringBuilder(code.length() * 2);
        if (includesCheckDigit && !Character.isDigit(checkDigit = code.charAt(code.length() - 1))) {
            throw new CheckDigitException("Invalid checkdigit[" + checkDigit + "] in " + code);
        }
        for (int i = 0; i < code.length(); ++i) {
            int charValue = Character.getNumericValue(code.charAt(i));
            if (charValue < 0 || charValue > 35) {
                throw new CheckDigitException("Invalid Character[" + (i + 1) + "] = '" + charValue + "'");
            }
            transformed.append(charValue);
        }
        return super.calculateModulus(transformed.toString(), includesCheckDigit);
    }

    @Override
    protected int weightedValue(int charValue, int leftPos, int rightPos) {
        int weight = POSITION_WEIGHT[rightPos % 2];
        int weightedValue = charValue * weight;
        return ModulusCheckDigit.sumDigits(weightedValue);
    }
}

