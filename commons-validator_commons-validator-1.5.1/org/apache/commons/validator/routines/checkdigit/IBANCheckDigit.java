/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator.routines.checkdigit;

import java.io.Serializable;
import org.apache.commons.validator.routines.checkdigit.CheckDigit;
import org.apache.commons.validator.routines.checkdigit.CheckDigitException;

public final class IBANCheckDigit
implements CheckDigit,
Serializable {
    private static final int MIN_CODE_LEN = 5;
    private static final long serialVersionUID = -3600191725934382801L;
    private static final int MAX_ALPHANUMERIC_VALUE = 35;
    public static final CheckDigit IBAN_CHECK_DIGIT = new IBANCheckDigit();
    private static final long MAX = 999999999L;
    private static final long MODULUS = 97L;

    @Override
    public boolean isValid(String code) {
        if (code == null || code.length() < 5) {
            return false;
        }
        String check = code.substring(2, 4);
        if ("00".equals(check) || "01".equals(check) || "99".equals(check)) {
            return false;
        }
        try {
            int modulusResult = this.calculateModulus(code);
            return modulusResult == 1;
        }
        catch (CheckDigitException ex) {
            return false;
        }
    }

    @Override
    public String calculate(String code) throws CheckDigitException {
        if (code == null || code.length() < 5) {
            throw new CheckDigitException("Invalid Code length=" + (code == null ? 0 : code.length()));
        }
        code = code.substring(0, 2) + "00" + code.substring(4);
        int modulusResult = this.calculateModulus(code);
        int charValue = 98 - modulusResult;
        String checkDigit = Integer.toString(charValue);
        return charValue > 9 ? checkDigit : "0" + checkDigit;
    }

    private int calculateModulus(String code) throws CheckDigitException {
        String reformattedCode = code.substring(4) + code.substring(0, 4);
        long total = 0L;
        for (int i = 0; i < reformattedCode.length(); ++i) {
            int charValue = Character.getNumericValue(reformattedCode.charAt(i));
            if (charValue < 0 || charValue > 35) {
                throw new CheckDigitException("Invalid Character[" + i + "] = '" + charValue + "'");
            }
            total = (charValue > 9 ? total * 100L : total * 10L) + (long)charValue;
            if (total <= 999999999L) continue;
            total %= 97L;
        }
        return (int)(total % 97L);
    }
}

