/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Maps
 *  javax.annotation.Nonnull
 *  javax.annotation.concurrent.Immutable
 */
package com.atlassian.oauth2.provider.rest.exception;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class ErrorCollection {
    public static final ErrorCollection EMPTY = new ErrorCollection(Collections.emptyList(), Collections.emptyMap());
    private final List<String> errors;
    private final Map<String, List<String>> fieldErrors;

    public boolean hasAnyErrors() {
        return !this.errors.isEmpty() || !this.fieldErrors.isEmpty();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(@Nonnull ErrorCollection errorCollection) {
        return new Builder(errorCollection);
    }

    public static ErrorCollection forMessage(@Nonnull String errorMessage) {
        return ErrorCollection.builder().addErrors(errorMessage).build();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ErrorCollection that = (ErrorCollection)o;
        return Objects.equals(this.getErrors(), that.getErrors()) && Objects.equals(this.getFieldErrors(), that.getFieldErrors());
    }

    public int hashCode() {
        return Objects.hash(this.getErrors(), this.getFieldErrors());
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("errors", this.getErrors()).add("fieldErrors", this.getFieldErrors()).toString();
    }

    public List<String> getErrors() {
        return this.errors;
    }

    public Map<String, List<String>> getFieldErrors() {
        return this.fieldErrors;
    }

    public ErrorCollection(List<String> errors, Map<String, List<String>> fieldErrors) {
        this.errors = errors;
        this.fieldErrors = fieldErrors;
    }

    public static final class Builder {
        private final List<String> errors;
        private final Map<String, List<String>> fieldErrors;

        private Builder() {
            this.errors = new ArrayList<String>();
            this.fieldErrors = new HashMap<String, List<String>>();
        }

        private Builder(@Nonnull ErrorCollection errorCollection) {
            this.errors = new ArrayList<String>(errorCollection.getErrors());
            this.fieldErrors = new HashMap<String, List<String>>(Maps.transformValues(errorCollection.getFieldErrors(), ArrayList::new));
        }

        public Builder addErrors(@Nonnull Iterable<String> errors) {
            Iterables.addAll(this.errors, errors);
            return this;
        }

        public Builder addErrors(String ... errors) {
            return this.addErrors(Arrays.asList(errors));
        }

        public Builder setErrors(@Nonnull Iterable<String> errors) {
            return this.clearErrors().addErrors(errors);
        }

        public Builder setErrors(String ... errors) {
            return this.setErrors(Arrays.asList(errors));
        }

        public Builder clearErrors() {
            this.errors.clear();
            return this;
        }

        public Builder addFieldErrors(@Nonnull String fieldName, @Nonnull Iterable<String> errors) {
            Iterables.addAll((Collection)this.fieldErrors.computeIfAbsent(fieldName, any -> new ArrayList()), errors);
            return this;
        }

        public Builder addFieldErrors(@Nonnull String fieldName, String ... errors) {
            return this.addFieldErrors(fieldName, Arrays.asList(errors));
        }

        public Builder addFieldErrors(@Nonnull Map<String, ? extends Iterable<String>> fieldErrors) {
            fieldErrors.forEach(this::addFieldErrors);
            return this;
        }

        public Builder setFieldErrors(@Nonnull String fieldName, @Nonnull Iterable<String> errors) {
            return this.clearFieldErrors(fieldName).addFieldErrors(fieldName, errors);
        }

        public Builder setFieldErrors(@Nonnull String fieldName, String ... errors) {
            return this.setFieldErrors(fieldName, Arrays.asList(errors));
        }

        public Builder setFieldErrors(@Nonnull Map<String, ? extends Iterable<String>> fieldErrors) {
            return this.clearFieldErrors().addFieldErrors(fieldErrors);
        }

        public Builder clearFieldErrors(@Nonnull String fieldName) {
            this.fieldErrors.put(fieldName, new ArrayList());
            return this;
        }

        public Builder clearFieldErrors() {
            this.fieldErrors.clear();
            return this;
        }

        public boolean hasAnyErrors() {
            return this.build().hasAnyErrors();
        }

        public ErrorCollection build() {
            return new ErrorCollection(this.errors, this.fieldErrors);
        }
    }
}

