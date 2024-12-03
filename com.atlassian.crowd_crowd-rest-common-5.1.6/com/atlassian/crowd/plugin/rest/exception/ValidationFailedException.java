/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.CrowdException
 *  com.atlassian.crowd.validator.ValidationError
 */
package com.atlassian.crowd.plugin.rest.exception;

import com.atlassian.crowd.exception.CrowdException;
import com.atlassian.crowd.validator.ValidationError;
import java.util.List;

public class ValidationFailedException
extends CrowdException {
    private List<ValidationError> errorMessages;

    public ValidationFailedException(List<ValidationError> errorMessages) {
        this.errorMessages = errorMessages;
    }

    public List<ValidationError> getErrorMessages() {
        return this.errorMessages;
    }
}

