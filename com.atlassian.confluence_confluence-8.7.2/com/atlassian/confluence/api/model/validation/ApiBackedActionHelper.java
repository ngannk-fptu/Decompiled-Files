/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.messages.Message
 *  com.atlassian.confluence.api.model.validation.FieldValidationError
 *  com.atlassian.confluence.api.model.validation.ValidationError
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 */
package com.atlassian.confluence.api.model.validation;

import com.atlassian.confluence.api.model.messages.Message;
import com.atlassian.confluence.api.model.validation.FieldValidationError;
import com.atlassian.confluence.api.model.validation.ValidationError;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.core.ConfluenceActionSupport;

public class ApiBackedActionHelper {
    private final ValidationResult validationResult;

    public ApiBackedActionHelper(ValidationResult validationResult) {
        this.validationResult = validationResult;
    }

    public void addValidationErrors(ConfluenceActionSupport action) {
        if (this.validationResult.isValid()) {
            return;
        }
        Iterable errors = this.validationResult.getErrors();
        for (ValidationError error : errors) {
            Message message = error.getMessage();
            String messageKey = message.getKey();
            Object[] args = message.getArgs();
            if (error instanceof FieldValidationError) {
                FieldValidationError fieldValidationError = (FieldValidationError)error;
                action.addFieldError(fieldValidationError.getFieldName(), messageKey, args);
                continue;
            }
            action.addActionError(messageKey, args);
        }
    }
}

