/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.service.exceptions;

import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.exceptions.ServiceException;

public class GoneException
extends ServiceException {
    public GoneException() {
    }

    public GoneException(String message) {
        super(message);
    }

    public GoneException(String message, Throwable e) {
        super(message, e);
    }

    public GoneException(String message, ValidationResult validationResult) {
        super(message, validationResult);
    }
}

