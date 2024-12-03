/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.fugue.Option
 */
package com.atlassian.confluence.api.service.exceptions;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.validation.ValidationError;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.util.FugueConversionUtil;
import com.atlassian.fugue.Option;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@ExperimentalApi
public class ServiceException
extends RuntimeException {
    private final ValidationResult validationResult;

    public ServiceException() {
        this.validationResult = null;
    }

    public ServiceException(String message) {
        super(message);
        this.validationResult = null;
    }

    public ServiceException(Throwable throwable) {
        super(throwable);
        this.validationResult = null;
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
        this.validationResult = null;
    }

    public ServiceException(String message, ValidationResult validationResult) {
        super(message);
        this.validationResult = validationResult;
    }

    @Deprecated
    public Option<ValidationResult> getOptionalValidationResult() {
        return FugueConversionUtil.toComOption(this.optionalValidationResult());
    }

    public Optional<ValidationResult> optionalValidationResult() {
        return Optional.ofNullable(this.validationResult);
    }

    @Override
    public String toString() {
        Iterable<ValidationError> errors;
        Optional<ValidationResult> option;
        StringBuilder b = new StringBuilder();
        b.append(this.getClass().getName());
        String message = this.getMessage();
        if (message != null) {
            b.append(": ").append(message);
        }
        if ((option = this.optionalValidationResult()).isPresent() && (errors = option.get().getErrors()).iterator().hasNext()) {
            b.append(": ");
            String formattedErrorMessage = StreamSupport.stream(errors.spliterator(), false).map(input -> input.getMessage().toString()).collect(Collectors.joining(", ", "[", "]"));
            b.append(formattedErrorMessage);
        }
        return b.toString();
    }
}

