/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.codehaus.jackson.annotate.JsonIgnore
 */
package com.atlassian.confluence.api.model.validation;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.messages.Message;
import com.atlassian.confluence.api.model.validation.ValidationError;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.InternalServerException;
import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.api.service.exceptions.ReadOnlyException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.codehaus.jackson.annotate.JsonIgnore;

@ExperimentalApi
public interface ValidationResult {
    public boolean isAuthorized();

    public boolean isAllowedInReadOnlyMode();

    default public boolean isValid() {
        return this.getErrors() == null || !this.getErrors().iterator().hasNext();
    }

    public Iterable<ValidationError> getErrors();

    default public boolean isSuccessful() {
        return this.isValid() && this.isAuthorized() && this.isAllowedInReadOnlyMode();
    }

    @JsonIgnore
    default public boolean isNotSuccessful() {
        return !this.isSuccessful();
    }

    @Deprecated
    default public ServiceException throwIfInvalid(String msg) throws ServiceException {
        this.throwIfNotSuccessful(msg);
        return null;
    }

    @Deprecated
    default public void throwIfNotValid(String msg) throws ServiceException {
        this.throwIfNotSuccessful(msg);
    }

    @Deprecated
    default public ServiceException throwIfInvalid() throws ServiceException {
        this.throwIfNotSuccessful();
        return null;
    }

    default public ServiceException convertToServiceException(@Nullable String errorMsg) {
        if (!this.isAllowedInReadOnlyMode()) {
            return new ReadOnlyException(errorMsg);
        }
        if (!this.isAuthorized()) {
            return new PermissionException(errorMsg, this);
        }
        if (!this.isValid()) {
            return new BadRequestException(errorMsg, this);
        }
        throw new InternalServerException(new IllegalStateException("Attempt to convert the valid and authorized ValidationResult to exception"));
    }

    default public void throwIfNotSuccessful(String msg) throws ServiceException {
        if (this.isNotSuccessful()) {
            throw this.convertToServiceException(msg);
        }
    }

    default public void throwIfNotSuccessful() throws ServiceException {
        if (this.isNotSuccessful()) {
            Message message;
            Iterable<ValidationError> errors = this.getErrors();
            if (errors != null && errors.iterator().hasNext() && (message = errors.iterator().next().getMessage()) != null) {
                throw this.convertToServiceException(message.getTranslation());
            }
            throw this.convertToServiceException(null);
        }
    }
}

