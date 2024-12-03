/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.core.service;

import com.atlassian.confluence.core.service.ValidationError;

public class FieldValidationError
extends ValidationError {
    private final String fieldName;

    public FieldValidationError(String fieldName, String messageKey, Object ... args) {
        super(messageKey, args);
        if (fieldName == null) {
            throw new IllegalArgumentException("fieldName cannot be null. Use ValidationError instead.");
        }
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        FieldValidationError that = (FieldValidationError)o;
        return this.fieldName.equals(that.fieldName);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.fieldName.hashCode();
        return result;
    }
}

