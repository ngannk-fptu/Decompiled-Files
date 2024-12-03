/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintValidator
 *  javax.validation.ConstraintValidatorContext
 */
package org.hibernate.validator.internal.constraintvalidators.hv;

import java.util.function.Function;
import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraints.ISBN;

public class ISBNValidator
implements ConstraintValidator<ISBN, CharSequence> {
    private static Pattern NOT_DIGITS_OR_NOT_X = Pattern.compile("[^\\dX]");
    private int length;
    private Function<String, Boolean> checkChecksumFunction;

    public void initialize(ISBN constraintAnnotation) {
        switch (constraintAnnotation.type()) {
            case ISBN_10: {
                this.length = 10;
                this.checkChecksumFunction = this::checkChecksumISBN10;
                break;
            }
            case ISBN_13: {
                this.length = 13;
                this.checkChecksumFunction = this::checkChecksumISBN13;
            }
        }
    }

    public boolean isValid(CharSequence isbn, ConstraintValidatorContext context) {
        if (isbn == null) {
            return true;
        }
        String digits = NOT_DIGITS_OR_NOT_X.matcher(isbn).replaceAll("");
        if (digits.length() != this.length) {
            return false;
        }
        return this.checkChecksumFunction.apply(digits);
    }

    private boolean checkChecksumISBN10(String isbn) {
        int sum = 0;
        for (int i = 0; i < isbn.length() - 1; ++i) {
            sum += (isbn.charAt(i) - 48) * (10 - i);
        }
        return (sum += isbn.charAt(9) == 'X' ? 10 : isbn.charAt(9) - 48) % 11 == 0;
    }

    private boolean checkChecksumISBN13(String isbn) {
        int sum = 0;
        for (int i = 0; i < isbn.length(); ++i) {
            sum += (isbn.charAt(i) - 48) * (i % 2 == 0 ? 1 : 3);
        }
        return sum % 10 == 0;
    }
}

