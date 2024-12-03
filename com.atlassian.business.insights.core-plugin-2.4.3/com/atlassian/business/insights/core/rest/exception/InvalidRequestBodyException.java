/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.rest.exception;

import com.atlassian.business.insights.core.rest.exception.BadRequestException;
import com.atlassian.business.insights.core.rest.validation.ValidationResult;
import java.util.Objects;
import javax.annotation.Nonnull;

public class InvalidRequestBodyException
extends BadRequestException {
    public static final String MESSAGE = "Invalid request body";

    public InvalidRequestBodyException(@Nonnull ValidationResult validationResult) {
        super(MESSAGE, Objects.requireNonNull(validationResult));
    }
}

