/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.core.rest.validation;

import com.atlassian.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;

public class ValidationError {
    private final String key;
    private final List<String> errorMessageParameters;

    @VisibleForTesting
    public ValidationError(@Nonnull String key) {
        this(key, new ArrayList<String>());
    }

    @VisibleForTesting
    public ValidationError(@Nonnull String key, @Nonnull List<String> errorMessageParameters) {
        this.key = key;
        this.errorMessageParameters = errorMessageParameters;
    }

    ValidationError(@Nonnull String key, @Nonnull String parameter) {
        this(key, Collections.singletonList(parameter));
    }

    public String getKey() {
        return this.key;
    }

    public List<String> getErrorMessageParameters() {
        return this.errorMessageParameters;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ValidationError that = (ValidationError)o;
        return Objects.equals(this.key, that.key) && Objects.equals(this.errorMessageParameters, that.errorMessageParameters);
    }

    public int hashCode() {
        return Objects.hash(this.key, this.errorMessageParameters);
    }
}

