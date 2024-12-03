/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.rest.exception;

import com.atlassian.business.insights.core.rest.exception.ForbiddenException;
import com.atlassian.business.insights.core.rest.validation.ValidationResult;
import java.util.Objects;
import javax.annotation.Nonnull;

public class InsufficientPermissionsException
extends ForbiddenException {
    public InsufficientPermissionsException(@Nonnull ValidationResult validationResult) {
        super("Insufficient permissions", Objects.requireNonNull(validationResult));
    }
}

