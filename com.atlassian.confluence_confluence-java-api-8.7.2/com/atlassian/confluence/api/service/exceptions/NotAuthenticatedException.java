/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.service.exceptions;

import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.exceptions.ServiceException;

public class NotAuthenticatedException
extends ServiceException {
    public NotAuthenticatedException() {
    }

    public NotAuthenticatedException(String message) {
        super(message);
    }

    public NotAuthenticatedException(Throwable throwable) {
        super(throwable);
    }

    public NotAuthenticatedException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public NotAuthenticatedException(String message, ValidationResult validationResult) {
        super(message, validationResult);
    }
}

