/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator.routines.checkdigit;

import java.io.Serializable;
import org.apache.commons.validator.routines.checkdigit.CheckDigit;
import org.apache.commons.validator.routines.checkdigit.CheckDigitException;

public abstract class ModulusCheckDigit
implements CheckDigit,
Serializable {
    private static final long serialVersionUID = 2948962251251528941L;
    private final int modulus;

    public ModulusCheckDigit(int modulus) {
        this.modulus = modulus;
    }

    public int getModulus() {
        return this.modulus;
    }

    @Override
    public boolean isValid(String code) {
        if (code == null || code.length() == 0) {
            return false;
        }
        try {
            int modulusResult = this.calculateModulus(code, true);
            return modulusResult == 0;
        }
        catch (CheckDigitException ex) {
            return false;
        }
    }

    @Override
    public String calculate(String code) throws CheckDigitException {
        if (code == null || code.length() == 0) {
            throw new CheckDigitException("Code is missing");
        }
        int modulusResult = this.calculateModulus(code, false);
        int charValue = (this.modulus - modulusResult) % this.modulus;
        return this.toCheckDigit(charValue);
    }

    protected int calculateModulus(String code, boolean includesCheckDigit) throws CheckDigitException {
        int total = 0;
        for (int i = 0; i < code.length(); ++i) {
            int lth = code.length() + (includesCheckDigit ? 0 : 1);
            int leftPos = i + 1;
            int rightPos = lth - i;
            int charValue = this.toInt(code.charAt(i), leftPos, rightPos);
            total += this.weightedValue(charValue, leftPos, rightPos);
        }
        if (total == 0) {
            throw new CheckDigitException("Invalid code, sum is zero");
        }
        return total % this.modulus;
    }

    protected abstract int weightedValue(int var1, int var2, int var3) throws CheckDigitException;

    protected int toInt(char character, int leftPos, int rightPos) throws CheckDigitException {
        if (Character.isDigit(character)) {
            return Character.getNumericValue(character);
        }
        throw new CheckDigitException("Invalid Character[" + leftPos + "] = '" + character + "'");
    }

    protected String toCheckDigit(int charValue) throws CheckDigitException {
        if (charValue >= 0 && charValue <= 9) {
            return Integer.toString(charValue);
        }
        throw new CheckDigitException("Invalid Check Digit Value =" + charValue);
    }

    public static int sumDigits(int number) {
        int total = 0;
        for (int todo = number; todo > 0; todo /= 10) {
            total += todo % 10;
        }
        return total;
    }
}

