/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.core.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.service.FieldValidationError;
import com.atlassian.confluence.core.service.ServiceCommand;
import com.atlassian.confluence.core.service.ValidationError;

public class ServiceBackedActionHelper {
    private final ServiceCommand serviceCommand;

    public ServiceBackedActionHelper(ServiceCommand serviceCommand) {
        this.serviceCommand = serviceCommand;
    }

    public void addValidationErrors(ConfluenceActionSupport action) {
        if (this.serviceCommand.isValid()) {
            return;
        }
        for (ValidationError error : this.serviceCommand.getValidationErrors()) {
            if (error instanceof FieldValidationError) {
                FieldValidationError fieldValidationError = (FieldValidationError)error;
                action.addFieldError(fieldValidationError.getFieldName(), fieldValidationError.getMessageKey(), fieldValidationError.getArgs());
                continue;
            }
            action.addActionError(error.getMessageKey(), error.getArgs());
        }
    }
}

