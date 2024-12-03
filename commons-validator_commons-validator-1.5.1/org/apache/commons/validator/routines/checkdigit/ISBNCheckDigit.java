/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator.routines.checkdigit;

import java.io.Serializable;
import org.apache.commons.validator.routines.checkdigit.CheckDigit;
import org.apache.commons.validator.routines.checkdigit.CheckDigitException;
import org.apache.commons.validator.routines.checkdigit.EAN13CheckDigit;
import org.apache.commons.validator.routines.checkdigit.ISBN10CheckDigit;

public final class ISBNCheckDigit
implements CheckDigit,
Serializable {
    private static final long serialVersionUID = 1391849166205184558L;
    public static final CheckDigit ISBN10_CHECK_DIGIT = ISBN10CheckDigit.ISBN10_CHECK_DIGIT;
    public static final CheckDigit ISBN13_CHECK_DIGIT = EAN13CheckDigit.EAN13_CHECK_DIGIT;
    public static final CheckDigit ISBN_CHECK_DIGIT = new ISBNCheckDigit();

    @Override
    public String calculate(String code) throws CheckDigitException {
        if (code == null || code.length() == 0) {
            throw new CheckDigitException("ISBN Code is missing");
        }
        if (code.length() == 9) {
            return ISBN10_CHECK_DIGIT.calculate(code);
        }
        if (code.length() == 12) {
            return ISBN13_CHECK_DIGIT.calculate(code);
        }
        throw new CheckDigitException("Invalid ISBN Length = " + code.length());
    }

    @Override
    public boolean isValid(String code) {
        if (code == null) {
            return false;
        }
        if (code.length() == 10) {
            return ISBN10_CHECK_DIGIT.isValid(code);
        }
        if (code.length() == 13) {
            return ISBN13_CHECK_DIGIT.isValid(code);
        }
        return false;
    }
}

