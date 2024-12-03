/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.messages.Message
 *  com.atlassian.confluence.api.model.validation.ValidationError
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 */
package com.atlassian.confluence.validation;

import com.atlassian.confluence.api.model.messages.Message;
import com.atlassian.confluence.api.model.validation.ValidationError;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.validation.MessageHolder;

public class XWorkValidationResultSupport {
    public static MessageHolder addAnyMessages(MessageHolder holder, ServiceException e) {
        e.optionalValidationResult().ifPresent(result -> XWorkValidationResultSupport.addAllMessages(holder, result));
        return holder;
    }

    public static MessageHolder addAllMessages(MessageHolder holder, ValidationResult validationResult) {
        Iterable errors = validationResult.getErrors();
        for (ValidationError error : errors) {
            Message message = error.getMessage();
            holder.addActionError(message.getKey(), message.getArgs());
        }
        return holder;
    }
}

