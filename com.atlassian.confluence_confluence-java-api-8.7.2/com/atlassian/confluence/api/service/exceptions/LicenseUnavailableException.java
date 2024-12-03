/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.service.exceptions;

import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.exceptions.ServiceException;

public class LicenseUnavailableException
extends ServiceException {
    public LicenseUnavailableException() {
    }

    public LicenseUnavailableException(String message) {
        super(message);
    }

    public LicenseUnavailableException(Throwable throwable) {
        super(throwable);
    }

    public LicenseUnavailableException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public LicenseUnavailableException(String message, ValidationResult validationResult) {
        super(message, validationResult);
    }
}

