/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Function2
 */
package com.atlassian.confluence.api.model.validation;

import com.atlassian.confluence.api.model.messages.Message;
import com.atlassian.confluence.api.model.validation.SimpleFieldValidationError;
import com.atlassian.confluence.api.model.validation.SimpleValidationError;
import com.atlassian.confluence.api.model.validation.ValidationError;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.api.service.exceptions.ReadOnlyException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.fugue.Function2;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

public class SimpleValidationResult
implements ValidationResult {
    private static final BiFunction<String, ValidationResult, ServiceException> defaultSupplier = (message, validationResult) -> {
        if (!validationResult.isAllowedInReadOnlyMode()) {
            return new ReadOnlyException((String)message, (ValidationResult)validationResult);
        }
        if (!validationResult.isAuthorized()) {
            return new PermissionException((String)message, (ValidationResult)validationResult);
        }
        return new BadRequestException((String)message, (ValidationResult)validationResult);
    };
    public static final ValidationResult VALID = new Builder().authorized(true).build();
    public static final ValidationResult FORBIDDEN = new Builder().authorized(false).build();
    public static final ValidationResult NOT_ALLOWED_IN_READ_ONLY_MODE = new Builder().allowedInReadOnlyMode(false).build();
    private final boolean authorized;
    private final boolean allowedInReadOnlyMode;
    private final List<ValidationError> errors;
    private final BiFunction<String, ValidationResult, ? extends ServiceException> exceptionSupplier;

    private SimpleValidationResult(Builder builder) {
        this.authorized = builder.authorized;
        this.errors = Collections.unmodifiableList(builder.errors);
        this.allowedInReadOnlyMode = builder.allowedInReadOnlyMode;
        this.exceptionSupplier = builder.exceptionSupplier != null ? builder.exceptionSupplier : defaultSupplier;
    }

    @Override
    public boolean isAuthorized() {
        return this.authorized;
    }

    @Override
    public boolean isAllowedInReadOnlyMode() {
        return this.allowedInReadOnlyMode;
    }

    public Collection<ValidationError> getErrors() {
        return this.errors;
    }

    @Override
    public ServiceException convertToServiceException(String errorMsg) {
        throw this.exceptionSupplier.apply(errorMsg, this);
    }

    public String toString() {
        return "SimpleValidationResult{authorized=" + this.authorized + ", allowedInReadOnlyMode=" + this.allowedInReadOnlyMode + ", errors=" + this.errors + '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean authorized = false;
        private boolean allowedInReadOnlyMode = true;
        private List<ValidationError> errors = new ArrayList<ValidationError>();
        private BiFunction<String, ValidationResult, ? extends ServiceException> exceptionSupplier;

        public Builder authorized(boolean value) {
            this.authorized = value;
            return this;
        }

        public Builder allowedInReadOnlyMode(boolean value) {
            this.allowedInReadOnlyMode = value;
            return this;
        }

        public Builder addFieldError(String fieldName, String key, Object ... args) {
            return this.addError(new SimpleFieldValidationError(fieldName, key, args));
        }

        public Builder addMessage(Message message) {
            return this.addError(new SimpleValidationError(message));
        }

        public Builder addError(String key, Object ... args) {
            return this.addError(new SimpleValidationError(key, args));
        }

        public Builder addError(ValidationError error) {
            this.errors.add(error);
            return this;
        }

        public Builder addErrors(List<ValidationError> errors) {
            this.errors.addAll(errors);
            return this;
        }

        @Deprecated
        public Builder addExceptionSupplier(Function2<String, ValidationResult, ? extends ServiceException> exceptionSupplier) {
            this.exceptionSupplier = (arg_0, arg_1) -> exceptionSupplier.apply(arg_0, arg_1);
            return this;
        }

        public Builder withExceptionSupplier(BiFunction<String, ValidationResult, ? extends ServiceException> exceptionSupplier) {
            this.exceptionSupplier = exceptionSupplier;
            return this;
        }

        public ValidationResult build() {
            return new SimpleValidationResult(this);
        }

        public boolean hasErrors() {
            return !this.errors.isEmpty();
        }
    }
}

