/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.service.exceptions;

import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.exceptions.ServiceException;

public class ReadOnlyException
extends ServiceException {
    public ReadOnlyException() {
    }

    public ReadOnlyException(String message) {
        super(message);
    }

    public ReadOnlyException(String message, Throwable e) {
        super(message, e);
    }

    public ReadOnlyException(String message, ValidationResult validationResult) {
        super(message, validationResult);
    }
}

