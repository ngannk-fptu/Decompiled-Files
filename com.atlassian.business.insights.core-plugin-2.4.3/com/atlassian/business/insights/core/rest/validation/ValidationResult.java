/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.business.insights.core.rest.validation;

import com.atlassian.business.insights.core.rest.validation.ValidationError;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ValidationResult
implements Serializable {
    private static final long serialVersionUID = 1L;
    private final List<ValidationError> errors = new ArrayList<ValidationError>();

    public ValidationResult() {
    }

    public ValidationResult(String key) {
        this.add(key);
    }

    public void add(String key) {
        this.errors.add(new ValidationError(key));
    }

    public void add(String key, String errorParameter) {
        this.errors.add(new ValidationError(key, errorParameter));
    }

    public void add(String key, String ... errorParameters) {
        this.errors.add(new ValidationError(key, Arrays.asList(errorParameters)));
    }

    public boolean hasErrors() {
        return !this.errors.isEmpty();
    }

    public List<ValidationError> getErrors() {
        return this.errors;
    }
}

