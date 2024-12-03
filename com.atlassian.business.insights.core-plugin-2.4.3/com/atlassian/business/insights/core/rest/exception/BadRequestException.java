/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.rest.exception;

import com.atlassian.business.insights.core.rest.validation.ValidationResult;
import java.util.Objects;
import javax.annotation.Nonnull;

public class BadRequestException
extends RuntimeException {
    private final ValidationResult validationResult;

    public BadRequestException(@Nonnull String message, @Nonnull ValidationResult validationResult) {
        super(Objects.requireNonNull(message));
        this.validationResult = Objects.requireNonNull(validationResult);
    }

    public ValidationResult getValidationResult() {
        return this.validationResult;
    }
}

