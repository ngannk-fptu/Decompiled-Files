/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator.routines.checkdigit;

import org.apache.commons.validator.routines.checkdigit.CheckDigit;
import org.apache.commons.validator.routines.checkdigit.CheckDigitException;
import org.apache.commons.validator.routines.checkdigit.ModulusCheckDigit;

public final class ISSNCheckDigit
extends ModulusCheckDigit {
    private static final long serialVersionUID = 1L;
    public static final CheckDigit ISSN_CHECK_DIGIT = new ISSNCheckDigit();

    public ISSNCheckDigit() {
        super(11);
    }

    @Override
    protected int weightedValue(int charValue, int leftPos, int rightPos) throws CheckDigitException {
        return charValue * (9 - leftPos);
    }

    @Override
    protected String toCheckDigit(int charValue) throws CheckDigitException {
        if (charValue == 10) {
            return "X";
        }
        return super.toCheckDigit(charValue);
    }

    @Override
    protected int toInt(char character, int leftPos, int rightPos) throws CheckDigitException {
        if (rightPos == 1 && character == 'X') {
            return 10;
        }
        return super.toInt(character, leftPos, rightPos);
    }
}

