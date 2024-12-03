/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.audit.rest.v1.validation;

import com.atlassian.audit.rest.model.ErrorDescription;
import java.util.ArrayList;
import java.util.List;

public class ValidationResult {
    private final List<ErrorDescription> errors = new ArrayList<ErrorDescription>();

    public ValidationResult() {
    }

    public ValidationResult(String code, String title) {
        this.add(code, title);
    }

    public void add(String code, String title) {
        this.errors.add(new ErrorDescription(code, title));
    }

    public void addAll(ValidationResult result) {
        result.getErrors().forEach(error -> this.add(error.getKey(), error.getMessage()));
    }

    public void addAll(List<ErrorDescription> results) {
        results.forEach(error -> this.add(error.getKey(), error.getMessage()));
    }

    public boolean hasErrors() {
        return !this.errors.isEmpty();
    }

    public List<ErrorDescription> getErrors() {
        return this.errors;
    }
}

