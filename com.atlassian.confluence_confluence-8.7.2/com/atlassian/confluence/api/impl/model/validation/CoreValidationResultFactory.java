/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.messages.Message
 *  com.atlassian.confluence.api.model.messages.SimpleMessage
 *  com.atlassian.confluence.api.model.validation.SimpleFieldValidationError
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult$Builder
 *  com.atlassian.confluence.api.model.validation.ValidationError
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 */
package com.atlassian.confluence.api.impl.model.validation;

import com.atlassian.confluence.api.impl.model.validation.DefaultValidationError;
import com.atlassian.confluence.api.model.messages.Message;
import com.atlassian.confluence.api.model.messages.SimpleMessage;
import com.atlassian.confluence.api.model.validation.SimpleFieldValidationError;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.core.service.FieldValidationError;
import com.atlassian.confluence.core.service.ValidationError;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.List;

public class CoreValidationResultFactory {
    public static ValidationResult create(boolean authorized, Collection<ValidationError> coreErrors) {
        if (!authorized) {
            return SimpleValidationResult.FORBIDDEN;
        }
        if (coreErrors.isEmpty()) {
            return SimpleValidationResult.VALID;
        }
        return new SimpleValidationResult.Builder().authorized(true).addErrors(CoreValidationResultFactory.convertCoreErrorsToApiErrors(coreErrors)).build();
    }

    public static List<com.atlassian.confluence.api.model.validation.ValidationError> convertCoreErrorsToApiErrors(Collection<ValidationError> coreErrors) {
        return ImmutableList.copyOf((Iterable)Iterables.transform(coreErrors, coreError -> {
            if (coreError instanceof FieldValidationError) {
                String fieldName = ((FieldValidationError)coreError).getFieldName();
                return new SimpleFieldValidationError(fieldName, coreError.getMessageKey(), coreError.getArgs());
            }
            return new DefaultValidationError((Message)SimpleMessage.withKeyAndArgs((String)coreError.getMessageKey(), (Object[])coreError.getArgs()));
        }));
    }
}

