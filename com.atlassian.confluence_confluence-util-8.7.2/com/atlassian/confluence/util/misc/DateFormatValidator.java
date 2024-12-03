/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.validator.ValidationException
 *  com.opensymphony.xwork2.validator.validators.FieldValidatorSupport
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.util.misc;

import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.validators.FieldValidatorSupport;
import java.text.SimpleDateFormat;
import org.apache.commons.lang3.StringUtils;

public class DateFormatValidator
extends FieldValidatorSupport {
    public void validate(Object object) throws ValidationException {
        String fieldValue = (String)this.getFieldValue(this.getFieldName(), object);
        if (StringUtils.isNotEmpty((CharSequence)fieldValue)) {
            try {
                new SimpleDateFormat(fieldValue);
            }
            catch (IllegalArgumentException e) {
                this.addFieldError(this.getFieldName(), object);
            }
        }
    }
}

