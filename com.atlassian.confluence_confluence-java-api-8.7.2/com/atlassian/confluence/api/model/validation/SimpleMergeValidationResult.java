/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.api.model.validation;

import com.atlassian.confluence.api.model.validation.MergeValidationResult;
import com.atlassian.confluence.api.model.validation.ValidationError;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import org.checkerframework.checker.nullness.qual.Nullable;

public class SimpleMergeValidationResult
implements MergeValidationResult {
    private final ValidationResult delegate;

    public SimpleMergeValidationResult(Builder builder) {
        this.delegate = builder.validationResult;
    }

    @Override
    public boolean isAuthorized() {
        return this.delegate.isAuthorized();
    }

    @Override
    public boolean isAllowedInReadOnlyMode() {
        return this.delegate.isAllowedInReadOnlyMode();
    }

    @Override
    public boolean isValid() {
        return this.delegate.isValid();
    }

    @Override
    public Iterable<ValidationError> getErrors() {
        return this.delegate.getErrors();
    }

    @Override
    @Deprecated
    public ServiceException throwIfInvalid(String msg) throws ServiceException {
        return this.delegate.throwIfInvalid(msg);
    }

    @Override
    @Deprecated
    public void throwIfNotValid(String msg) throws ServiceException {
        this.delegate.throwIfNotValid(msg);
    }

    @Override
    @Deprecated
    public ServiceException throwIfInvalid() throws ServiceException {
        return this.delegate.throwIfInvalid();
    }

    @Override
    public void throwIfNotSuccessful(String msg) throws ServiceException {
        this.delegate.throwIfNotSuccessful(msg);
    }

    @Override
    public void throwIfNotSuccessful() throws ServiceException {
        this.delegate.throwIfNotSuccessful();
    }

    @Override
    public ServiceException convertToServiceException(@Nullable String errorMsg) {
        return this.delegate.convertToServiceException(errorMsg);
    }

    public static Builder builder(ValidationResult result) {
        return new Builder(result);
    }

    public static class Builder {
        private final ValidationResult validationResult;

        public Builder(ValidationResult validationResult) {
            this.validationResult = validationResult;
        }

        public MergeValidationResult build() {
            return new SimpleMergeValidationResult(this);
        }
    }
}

