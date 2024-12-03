/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.audit.rest.v1.validation.exception;

import com.atlassian.audit.rest.v1.validation.ValidationResult;

public class InvalidQueryException
extends RuntimeException {
    private final ValidationResult validationResult;

    public InvalidQueryException(ValidationResult validationResult) {
        super("Invalid Query");
        this.validationResult = validationResult;
    }

    public ValidationResult getValidationResult() {
        return this.validationResult;
    }
}

