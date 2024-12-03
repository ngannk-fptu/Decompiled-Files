/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintValidatorContext
 */
package org.hibernate.validator.internal.constraintvalidators.hv;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.validation.ConstraintValidatorContext;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

public abstract class ModCheckBase {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private static final Pattern NUMBERS_ONLY_REGEXP = Pattern.compile("[^0-9]");
    private static final int DEC_RADIX = 10;
    private int startIndex;
    private int endIndex;
    private int checkDigitIndex;
    private boolean ignoreNonDigitCharacters;

    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        List<Integer> digits;
        char checkDigit;
        String digitsAsString;
        if (value == null) {
            return true;
        }
        String valueAsString = value.toString();
        try {
            digitsAsString = this.extractVerificationString(valueAsString);
            checkDigit = this.extractCheckDigit(valueAsString);
        }
        catch (IndexOutOfBoundsException e) {
            return false;
        }
        digitsAsString = this.stripNonDigitsIfRequired(digitsAsString);
        try {
            digits = this.extractDigits(digitsAsString);
        }
        catch (NumberFormatException e) {
            return false;
        }
        return this.isCheckDigitValid(digits, checkDigit);
    }

    public abstract boolean isCheckDigitValid(List<Integer> var1, char var2);

    protected void initialize(int startIndex, int endIndex, int checkDigitIndex, boolean ignoreNonDigitCharacters) {
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.checkDigitIndex = checkDigitIndex;
        this.ignoreNonDigitCharacters = ignoreNonDigitCharacters;
        this.validateOptions();
    }

    protected int extractDigit(char value) throws NumberFormatException {
        if (Character.isDigit(value)) {
            return Character.digit(value, 10);
        }
        throw LOG.getCharacterIsNotADigitException(value);
    }

    private List<Integer> extractDigits(String value) throws NumberFormatException {
        char[] chars;
        ArrayList<Integer> digits = new ArrayList<Integer>(value.length());
        for (char c : chars = value.toCharArray()) {
            digits.add(this.extractDigit(c));
        }
        return digits;
    }

    private boolean validateOptions() {
        if (this.startIndex < 0) {
            throw LOG.getStartIndexCannotBeNegativeException(this.startIndex);
        }
        if (this.endIndex < 0) {
            throw LOG.getEndIndexCannotBeNegativeException(this.endIndex);
        }
        if (this.startIndex > this.endIndex) {
            throw LOG.getInvalidRangeException(this.startIndex, this.endIndex);
        }
        if (this.checkDigitIndex > 0 && this.startIndex <= this.checkDigitIndex && this.endIndex > this.checkDigitIndex) {
            throw LOG.getInvalidCheckDigitException(this.startIndex, this.endIndex);
        }
        return true;
    }

    private String stripNonDigitsIfRequired(String value) {
        if (this.ignoreNonDigitCharacters) {
            return NUMBERS_ONLY_REGEXP.matcher(value).replaceAll("");
        }
        return value;
    }

    private String extractVerificationString(String value) throws IndexOutOfBoundsException {
        if (this.endIndex == Integer.MAX_VALUE) {
            return value.substring(0, value.length() - 1);
        }
        if (this.checkDigitIndex == -1) {
            return value.substring(this.startIndex, this.endIndex);
        }
        return value.substring(this.startIndex, this.endIndex + 1);
    }

    private char extractCheckDigit(String value) throws IndexOutOfBoundsException {
        if (this.checkDigitIndex == -1) {
            if (this.endIndex == Integer.MAX_VALUE) {
                return value.charAt(value.length() - 1);
            }
            return value.charAt(this.endIndex);
        }
        return value.charAt(this.checkDigitIndex);
    }
}

