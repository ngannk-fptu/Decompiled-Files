/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.validators.FieldValidatorSupport;
import java.lang.reflect.Array;
import java.util.Collection;

public class RequiredFieldValidator
extends FieldValidatorSupport {
    @Override
    public void validate(Object object) throws ValidationException {
        String fieldName = this.getFieldName();
        Object value = this.getFieldValue(fieldName, object);
        if (value == null) {
            this.addFieldError(fieldName, object);
        } else if (value.getClass().isArray() && Array.getLength(value) == 0) {
            this.addFieldError(fieldName, object);
        } else if (Collection.class.isAssignableFrom(value.getClass()) && ((Collection)value).size() == 0) {
            this.addFieldError(fieldName, object);
        }
    }
}

