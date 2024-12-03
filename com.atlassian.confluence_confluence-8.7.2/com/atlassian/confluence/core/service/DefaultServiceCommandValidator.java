/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.core.service;

import com.atlassian.confluence.core.service.FieldValidationError;
import com.atlassian.confluence.core.service.ServiceCommandValidator;
import com.atlassian.confluence.core.service.ValidationError;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DefaultServiceCommandValidator
implements ServiceCommandValidator {
    private List<ValidationError> validationErrors = new ArrayList<ValidationError>();

    @Override
    public final void addFieldValidationError(String fieldName, String messageKey) {
        this.validationErrors.add(new FieldValidationError(fieldName, messageKey, new Object[0]));
    }

    @Override
    public final void addValidationError(String messageKey, Object ... messageArguments) {
        this.validationErrors.add(new ValidationError(messageKey, messageArguments));
    }

    @Override
    public final void addFieldValidationError(String fieldName, String messageKey, Object ... messageArguments) {
        this.validationErrors.add(new FieldValidationError(fieldName, messageKey, messageArguments));
    }

    @Override
    public void addFieldValidationError(FieldValidationError fieldError) {
        this.validationErrors.add(fieldError);
    }

    @Override
    public Collection<ValidationError> getValidationErrors() {
        return Collections.unmodifiableCollection(this.validationErrors);
    }
}

