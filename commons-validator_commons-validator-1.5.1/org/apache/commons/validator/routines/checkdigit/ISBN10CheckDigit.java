/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator.routines.checkdigit;

import org.apache.commons.validator.routines.checkdigit.CheckDigit;
import org.apache.commons.validator.routines.checkdigit.CheckDigitException;
import org.apache.commons.validator.routines.checkdigit.ModulusCheckDigit;

public final class ISBN10CheckDigit
extends ModulusCheckDigit {
    private static final long serialVersionUID = 8000855044504864964L;
    public static final CheckDigit ISBN10_CHECK_DIGIT = new ISBN10CheckDigit();

    public ISBN10CheckDigit() {
        super(11);
    }

    @Override
    protected int weightedValue(int charValue, int leftPos, int rightPos) {
        return charValue * rightPos;
    }

    @Override
    protected int toInt(char character, int leftPos, int rightPos) throws CheckDigitException {
        if (rightPos == 1 && character == 'X') {
            return 10;
        }
        return super.toInt(character, leftPos, rightPos);
    }

    @Override
    protected String toCheckDigit(int charValue) throws CheckDigitException {
        if (charValue == 10) {
            return "X";
        }
        return super.toCheckDigit(charValue);
    }
}

