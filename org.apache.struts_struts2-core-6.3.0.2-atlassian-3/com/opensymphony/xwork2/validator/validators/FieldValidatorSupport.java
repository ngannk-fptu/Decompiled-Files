/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.validator.validators;

import com.opensymphony.xwork2.validator.FieldValidator;
import com.opensymphony.xwork2.validator.validators.ValidatorSupport;

public abstract class FieldValidatorSupport
extends ValidatorSupport
implements FieldValidator {
    protected String fieldName;
    protected String type;
    protected Object currentValue;

    @Override
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public String getFieldName() {
        return this.fieldName;
    }

    @Override
    public void setValidatorType(String type) {
        this.type = type;
    }

    @Override
    public String getValidatorType() {
        return this.type;
    }

    public Object getCurrentValue() {
        return this.currentValue;
    }

    void setCurrentValue(Object currentValue) {
        this.currentValue = currentValue;
    }
}

