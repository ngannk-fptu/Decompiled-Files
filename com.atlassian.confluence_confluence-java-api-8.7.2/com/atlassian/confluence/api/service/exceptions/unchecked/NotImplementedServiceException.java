/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.service.exceptions.unchecked;

import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.exceptions.ServiceException;

public class NotImplementedServiceException
extends ServiceException {
    public NotImplementedServiceException(String message) {
        super(message);
    }

    public NotImplementedServiceException(String message, Exception e) {
        super(message, e);
    }

    public NotImplementedServiceException(String message, ValidationResult validationResult) {
        super(message, validationResult);
    }
}

