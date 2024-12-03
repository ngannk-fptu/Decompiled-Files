/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.validator.validators.RegexFieldValidator;
import org.apache.commons.lang3.StringUtils;

public class CreditCardValidator
extends RegexFieldValidator {
    public static final String CREDIT_CARD_PATTERN = "^(?:4[0-9]{12}(?:[0-9]{3})?|(?:5[1-5][0-9]{2}|222[1-9]|22[3-9][0-9]|2[3-6][0-9]{2}|27[01][0-9]|2720)[0-9]{12}|3[47][0-9]{13}|3(?:0[0-5]|[68][0-9])[0-9]{11}|6(?:011|5[0-9]{2})[0-9]{12}|(?:2131|1800|35\\d{3})\\d{11})$";

    public CreditCardValidator() {
        this.setRegex(CREDIT_CARD_PATTERN);
        this.setCaseSensitive(false);
    }

    @Override
    protected void validateFieldValue(Object object, String value, String regexToUse) {
        super.validateFieldValue(object, StringUtils.deleteWhitespace((String)value), regexToUse);
    }
}

