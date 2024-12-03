/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.validator.routines;

import java.io.Serializable;
import org.apache.commons.validator.routines.CodeValidator;
import org.apache.commons.validator.routines.checkdigit.CheckDigitException;
import org.apache.commons.validator.routines.checkdigit.EAN13CheckDigit;
import org.apache.commons.validator.routines.checkdigit.ISBN10CheckDigit;

public class ISBNValidator
implements Serializable {
    private static final int ISBN_10_LEN = 10;
    private static final long serialVersionUID = 4319515687976420405L;
    private static final String SEP = "(?:\\-|\\s)";
    private static final String GROUP = "(\\d{1,5})";
    private static final String PUBLISHER = "(\\d{1,7})";
    private static final String TITLE = "(\\d{1,6})";
    static final String ISBN10_REGEX = "^(?:(\\d{9}[0-9X])|(?:(\\d{1,5})(?:\\-|\\s)(\\d{1,7})(?:\\-|\\s)(\\d{1,6})(?:\\-|\\s)([0-9X])))$";
    static final String ISBN13_REGEX = "^(978|979)(?:(\\d{10})|(?:(?:\\-|\\s)(\\d{1,5})(?:\\-|\\s)(\\d{1,7})(?:\\-|\\s)(\\d{1,6})(?:\\-|\\s)([0-9])))$";
    private static final ISBNValidator ISBN_VALIDATOR = new ISBNValidator();
    private static final ISBNValidator ISBN_VALIDATOR_NO_CONVERT = new ISBNValidator(false);
    private final CodeValidator isbn10Validator = new CodeValidator("^(?:(\\d{9}[0-9X])|(?:(\\d{1,5})(?:\\-|\\s)(\\d{1,7})(?:\\-|\\s)(\\d{1,6})(?:\\-|\\s)([0-9X])))$", 10, ISBN10CheckDigit.ISBN10_CHECK_DIGIT);
    private final CodeValidator isbn13Validator = new CodeValidator("^(978|979)(?:(\\d{10})|(?:(?:\\-|\\s)(\\d{1,5})(?:\\-|\\s)(\\d{1,7})(?:\\-|\\s)(\\d{1,6})(?:\\-|\\s)([0-9])))$", 13, EAN13CheckDigit.EAN13_CHECK_DIGIT);
    private final boolean convert;

    public static ISBNValidator getInstance() {
        return ISBN_VALIDATOR;
    }

    public static ISBNValidator getInstance(boolean convert) {
        return convert ? ISBN_VALIDATOR : ISBN_VALIDATOR_NO_CONVERT;
    }

    public ISBNValidator() {
        this(true);
    }

    public ISBNValidator(boolean convert) {
        this.convert = convert;
    }

    public boolean isValid(String code) {
        return this.isValidISBN13(code) || this.isValidISBN10(code);
    }

    public boolean isValidISBN10(String code) {
        return this.isbn10Validator.isValid(code);
    }

    public boolean isValidISBN13(String code) {
        return this.isbn13Validator.isValid(code);
    }

    public String validate(String code) {
        String result = this.validateISBN13(code);
        if (result == null && (result = this.validateISBN10(code)) != null && this.convert) {
            result = this.convertToISBN13(result);
        }
        return result;
    }

    public String validateISBN10(String code) {
        Object result = this.isbn10Validator.validate(code);
        return result == null ? null : result.toString();
    }

    public String validateISBN13(String code) {
        Object result = this.isbn13Validator.validate(code);
        return result == null ? null : result.toString();
    }

    public String convertToISBN13(String isbn10) {
        if (isbn10 == null) {
            return null;
        }
        String input = isbn10.trim();
        if (input.length() != 10) {
            throw new IllegalArgumentException("Invalid length " + input.length() + " for '" + input + "'");
        }
        String isbn13 = "978" + input.substring(0, 9);
        try {
            String checkDigit = this.isbn13Validator.getCheckDigit().calculate(isbn13);
            isbn13 = isbn13 + checkDigit;
            return isbn13;
        }
        catch (CheckDigitException e) {
            throw new IllegalArgumentException("Check digit error for '" + input + "' - " + e.getMessage());
        }
    }
}

