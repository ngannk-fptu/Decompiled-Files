/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.oauth2.provider.rest.exception;

import com.atlassian.oauth2.common.rest.validator.ErrorCollection;
import java.util.Objects;

public class ValidationException
extends RuntimeException {
    private final ErrorCollection errorCollection;

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ValidationException that = (ValidationException)o;
        return Objects.equals(this.errorCollection, that.errorCollection);
    }

    public int hashCode() {
        return Objects.hash(this.errorCollection);
    }

    public ErrorCollection getErrorCollection() {
        return this.errorCollection;
    }

    public ValidationException(ErrorCollection errorCollection) {
        this.errorCollection = errorCollection;
    }

    @Override
    public String toString() {
        return "ValidationException(errorCollection=" + this.getErrorCollection() + ")";
    }
}

