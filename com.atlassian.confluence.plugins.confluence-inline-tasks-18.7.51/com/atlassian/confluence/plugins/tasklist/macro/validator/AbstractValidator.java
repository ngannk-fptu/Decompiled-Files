/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.tasklist.macro.validator;

import com.atlassian.confluence.plugins.tasklist.macro.validator.ValidatedErrorType;

public abstract class AbstractValidator {
    protected String fieldNameCode;
    protected String input;
    protected ValidatedErrorType error;

    protected AbstractValidator(String fieldNameCode) {
        this.fieldNameCode = fieldNameCode;
    }

    public abstract boolean validate();

    public ValidatedErrorType getError() {
        return this.error;
    }
}

