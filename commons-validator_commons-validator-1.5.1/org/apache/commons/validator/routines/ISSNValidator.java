/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator.routines;

import java.io.Serializable;
import org.apache.commons.validator.routines.CodeValidator;
import org.apache.commons.validator.routines.checkdigit.CheckDigitException;
import org.apache.commons.validator.routines.checkdigit.EAN13CheckDigit;
import org.apache.commons.validator.routines.checkdigit.ISSNCheckDigit;

public class ISSNValidator
implements Serializable {
    private static final long serialVersionUID = 4319515687976420405L;
    private static final String ISSN_REGEX = "(?:ISSN )?(\\d{4})-(\\d{3}[0-9X])$";
    private static final CodeValidator VALIDATOR = new CodeValidator("(?:ISSN )?(\\d{4})-(\\d{3}[0-9X])$", 8, ISSNCheckDigit.ISSN_CHECK_DIGIT);
    private static final ISSNValidator ISSN_VALIDATOR = new ISSNValidator();

    public static ISSNValidator getInstance() {
        return ISSN_VALIDATOR;
    }

    public boolean isValid(String code) {
        return VALIDATOR.isValid(code);
    }

    public Object validate(String code) {
        return VALIDATOR.validate(code);
    }

    public String convertToEAN13(String issn, String suffix) {
        if (suffix == null || !suffix.matches("\\d\\d")) {
            throw new IllegalArgumentException("Suffix must be two digits: '" + suffix + "'");
        }
        Object result = this.validate(issn);
        if (result == null) {
            return null;
        }
        String input = result.toString();
        String ean13 = "977" + input.substring(0, input.length() - 1) + suffix;
        try {
            String checkDigit = EAN13CheckDigit.EAN13_CHECK_DIGIT.calculate(ean13);
            ean13 = ean13 + checkDigit;
            return ean13;
        }
        catch (CheckDigitException e) {
            throw new IllegalArgumentException("Check digit error for '" + ean13 + "' - " + e.getMessage());
        }
    }
}

