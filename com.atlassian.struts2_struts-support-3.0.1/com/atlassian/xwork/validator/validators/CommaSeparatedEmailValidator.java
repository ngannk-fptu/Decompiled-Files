/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.validator.ValidationException
 *  com.opensymphony.xwork2.validator.validators.RegexFieldValidator
 */
package com.atlassian.xwork.validator.validators;

import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.validators.RegexFieldValidator;
import java.util.Arrays;

public class CommaSeparatedEmailValidator
extends RegexFieldValidator {
    private static final String COMMA_SEPARATED_EMAIL_PATTERN = "\\b^(['_a-z0-9-\\+]+(\\.['_a-z0-9-\\+]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*\\.([a-z]{2,6}))(,\\s*['_a-z0-9-\\+]+(\\.['_a-z0-9-\\+]+)*@[a-z0-9-]+(\\.[a-z0-9-]+)*\\.([a-z]{2,6}))*$\\b";

    public CommaSeparatedEmailValidator() {
        this.setRegex(COMMA_SEPARATED_EMAIL_PATTERN);
        this.setCaseSensitive(false);
    }

    public void validate1(Object object) throws ValidationException {
        String fieldName = this.getFieldName();
        String value = (String)this.getFieldValue(fieldName, object);
        if (value == null) {
            return;
        }
        if ((value = value.trim()).length() == 0) {
            return;
        }
        String[] emails = value.split("\\s*,\\s*");
        Arrays.stream(emails).forEach(email -> this.validateFieldValue(object, (String)email, this.getRegex()));
    }
}

