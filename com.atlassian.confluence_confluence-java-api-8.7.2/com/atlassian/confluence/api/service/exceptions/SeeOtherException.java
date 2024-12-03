/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.service.exceptions;

import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.exceptions.ServiceException;

public class SeeOtherException
extends ServiceException {
    public SeeOtherException() {
    }

    public SeeOtherException(String message) {
        super(message);
    }

    public SeeOtherException(Throwable throwable) {
        super(throwable);
    }

    public SeeOtherException(String message, Throwable e) {
        super(message, e);
    }

    public SeeOtherException(String message, ValidationResult validationResult) {
        super(message, validationResult);
    }
}

