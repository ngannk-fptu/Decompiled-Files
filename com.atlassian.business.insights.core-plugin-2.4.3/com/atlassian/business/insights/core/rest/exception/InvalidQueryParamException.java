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

public class InvalidQueryParamException
extends BadRequestException {
    public InvalidQueryParamException(@Nonnull ValidationResult validationResult) {
        super("Invalid query parameters", Objects.requireNonNull(validationResult));
    }
}

