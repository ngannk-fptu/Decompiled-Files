/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.ConstraintValidator
 *  javax.validation.ConstraintValidatorContext
 */
package org.hibernate.validator.internal.constraintvalidators;

import java.lang.annotation.Annotation;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.hibernate.validator.internal.util.DomainNameUtil;

public class AbstractEmailValidator<A extends Annotation>
implements ConstraintValidator<A, CharSequence> {
    private static final int MAX_LOCAL_PART_LENGTH = 64;
    private static final String LOCAL_PART_ATOM = "[a-z0-9!#$%&'*+/=?^_`{|}~\u0080-\uffff-]";
    private static final String LOCAL_PART_INSIDE_QUOTES_ATOM = "(?:[a-z0-9!#$%&'*.(),<>\\[\\]:;  @+/=?^_`{|}~\u0080-\uffff-]|\\\\\\\\|\\\\\\\")";
    private static final Pattern LOCAL_PART_PATTERN = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~\u0080-\uffff-]+|\"(?:[a-z0-9!#$%&'*.(),<>\\[\\]:;  @+/=?^_`{|}~\u0080-\uffff-]|\\\\\\\\|\\\\\\\")+\")(?:\\.(?:[a-z0-9!#$%&'*+/=?^_`{|}~\u0080-\uffff-]+|\"(?:[a-z0-9!#$%&'*.(),<>\\[\\]:;  @+/=?^_`{|}~\u0080-\uffff-]|\\\\\\\\|\\\\\\\")+\"))*", 2);

    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null || value.length() == 0) {
            return true;
        }
        String stringValue = value.toString();
        int splitPosition = stringValue.lastIndexOf(64);
        if (splitPosition < 0) {
            return false;
        }
        String localPart = stringValue.substring(0, splitPosition);
        String domainPart = stringValue.substring(splitPosition + 1);
        if (!this.isValidEmailLocalPart(localPart)) {
            return false;
        }
        return DomainNameUtil.isValidEmailDomainAddress(domainPart);
    }

    private boolean isValidEmailLocalPart(String localPart) {
        if (localPart.length() > 64) {
            return false;
        }
        Matcher matcher = LOCAL_PART_PATTERN.matcher(localPart);
        return matcher.matches();
    }
}

