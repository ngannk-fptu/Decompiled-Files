/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator.routines;

import java.io.Serializable;
import org.apache.commons.validator.routines.RegexValidator;
import org.apache.commons.validator.routines.checkdigit.CheckDigit;

public final class CodeValidator
implements Serializable {
    private static final long serialVersionUID = 446960910870938233L;
    private final RegexValidator regexValidator;
    private final int minLength;
    private final int maxLength;
    private final CheckDigit checkdigit;

    public CodeValidator(String regex, CheckDigit checkdigit) {
        this(regex, -1, -1, checkdigit);
    }

    public CodeValidator(String regex, int length, CheckDigit checkdigit) {
        this(regex, length, length, checkdigit);
    }

    public CodeValidator(String regex, int minLength, int maxLength, CheckDigit checkdigit) {
        this.regexValidator = regex != null && regex.length() > 0 ? new RegexValidator(regex) : null;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.checkdigit = checkdigit;
    }

    public CodeValidator(RegexValidator regexValidator, CheckDigit checkdigit) {
        this(regexValidator, -1, -1, checkdigit);
    }

    public CodeValidator(RegexValidator regexValidator, int length, CheckDigit checkdigit) {
        this(regexValidator, length, length, checkdigit);
    }

    public CodeValidator(RegexValidator regexValidator, int minLength, int maxLength, CheckDigit checkdigit) {
        this.regexValidator = regexValidator;
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.checkdigit = checkdigit;
    }

    public CheckDigit getCheckDigit() {
        return this.checkdigit;
    }

    public int getMinLength() {
        return this.minLength;
    }

    public int getMaxLength() {
        return this.maxLength;
    }

    public RegexValidator getRegexValidator() {
        return this.regexValidator;
    }

    public boolean isValid(String input) {
        return this.validate(input) != null;
    }

    public Object validate(String input) {
        if (input == null) {
            return null;
        }
        String code = input.trim();
        if (code.length() == 0) {
            return null;
        }
        if (this.regexValidator != null && (code = this.regexValidator.validate(code)) == null) {
            return null;
        }
        if (this.minLength >= 0 && code.length() < this.minLength || this.maxLength >= 0 && code.length() > this.maxLength) {
            return null;
        }
        if (this.checkdigit != null && !this.checkdigit.isValid(code)) {
            return null;
        }
        return code;
    }
}

