/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.validators.FieldValidatorSupport;
import java.util.Collection;

public class RequiredStringValidator
extends FieldValidatorSupport {
    private boolean trim = true;

    public void setTrim(boolean trim) {
        this.trim = trim;
    }

    public void setTrimExpression(String trimExpression) {
        this.trim = (Boolean)this.parse(trimExpression, Boolean.class);
    }

    public boolean isTrim() {
        return this.trim;
    }

    @Override
    public void validate(Object object) throws ValidationException {
        Object fieldValue = this.getFieldValue(this.getFieldName(), object);
        if (fieldValue == null) {
            this.addFieldError(this.getFieldName(), object);
            return;
        }
        if (fieldValue.getClass().isArray()) {
            Object[] values;
            for (Object value : values = (Object[])fieldValue) {
                this.validateValue(object, value);
            }
        } else if (Collection.class.isAssignableFrom(fieldValue.getClass())) {
            Collection values = (Collection)fieldValue;
            for (Object value : values) {
                this.validateValue(object, value);
            }
        } else {
            this.validateValue(object, fieldValue);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void validateValue(Object object, Object fieldValue) {
        try {
            this.setCurrentValue(fieldValue);
            if (fieldValue == null) {
                this.addFieldError(this.getFieldName(), object);
                return;
            }
            if (fieldValue instanceof String) {
                String stingValue = (String)fieldValue;
                if (this.trim) {
                    stingValue = stingValue.trim();
                }
                if (stingValue.length() == 0) {
                    this.addFieldError(this.getFieldName(), object);
                }
            } else {
                this.addFieldError(this.getFieldName(), object);
            }
        }
        finally {
            this.setCurrentValue(null);
        }
    }
}

