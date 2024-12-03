/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.service.exceptions;

import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.exceptions.ServiceException;

public class PermissionException
extends ServiceException {
    public PermissionException() {
    }

    public PermissionException(String message) {
        super(message);
    }

    public PermissionException(Throwable throwable) {
        super(throwable);
    }

    public PermissionException(String message, Throwable e) {
        super(message, e);
    }

    public PermissionException(String message, ValidationResult validationResult) {
        super(message, validationResult);
    }
}

