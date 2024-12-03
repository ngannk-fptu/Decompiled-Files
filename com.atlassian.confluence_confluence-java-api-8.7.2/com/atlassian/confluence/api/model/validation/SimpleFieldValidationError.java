/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.model.validation;

import com.atlassian.confluence.api.model.validation.FieldValidationError;
import com.atlassian.confluence.api.model.validation.SimpleValidationError;

public class SimpleFieldValidationError
extends SimpleValidationError
implements FieldValidationError {
    private final String fieldName;

    public SimpleFieldValidationError(String fieldName, String key, Object ... args) {
        super(key, args);
        this.fieldName = fieldName;
    }

    @Override
    public String getFieldName() {
        return this.fieldName;
    }
}

