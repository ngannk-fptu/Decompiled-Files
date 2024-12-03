/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.model.validation;

import com.atlassian.confluence.api.model.validation.ServiceExceptionSupplier;
import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.ValidationResult;

public class SimpleValidationResults {
    private SimpleValidationResults() {
    }

    public static ValidationResult notFoundResult(String message, Object ... args) {
        return SimpleValidationResult.builder().authorized(true).addError(message, args).withExceptionSupplier(ServiceExceptionSupplier.notFoundExceptionSupplier()).build();
    }

    public static ValidationResult conflictResult(String message, Object ... args) {
        return SimpleValidationResult.builder().authorized(true).addError(message, args).withExceptionSupplier(ServiceExceptionSupplier.conflictExceptionSupplier()).build();
    }

    public static ValidationResult notImplementedResult(String message, Object ... args) {
        return SimpleValidationResult.builder().authorized(true).addError(message, args).withExceptionSupplier(ServiceExceptionSupplier.notImplementedSupplier()).build();
    }

    public static ValidationResult forbiddenResult(String message, Object ... args) {
        return SimpleValidationResult.builder().authorized(false).addError(message, args).build();
    }

    public static ValidationResult invalidResult(String message, Object ... args) {
        return SimpleValidationResult.builder().authorized(true).addError(message, args).build();
    }

    public static ValidationResult paymentRequiredResult(String message, Object ... args) {
        return SimpleValidationResult.builder().authorized(true).addError(message, args).withExceptionSupplier(ServiceExceptionSupplier.licenseUnavailableExceptionSupplier()).build();
    }

    public static ValidationResult notAuthenticatedResult(String message, Object ... args) {
        return SimpleValidationResult.builder().authorized(false).addError(message, args).withExceptionSupplier(ServiceExceptionSupplier.notAuthenticatedExceptionSupplier()).build();
    }
}

