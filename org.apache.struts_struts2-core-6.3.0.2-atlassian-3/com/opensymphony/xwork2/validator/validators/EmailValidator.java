/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.validator.validators.RegexFieldValidator;

public class EmailValidator
extends RegexFieldValidator {
    public static final String EMAIL_ADDRESS_PATTERN = "\\b^['_a-z0-9-\\+]+(\\.['_a-z0-9-\\+]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*\\.([a-z]{2,6})$\\b";

    public EmailValidator() {
        this.setRegex(EMAIL_ADDRESS_PATTERN);
        this.setCaseSensitive(false);
    }
}

