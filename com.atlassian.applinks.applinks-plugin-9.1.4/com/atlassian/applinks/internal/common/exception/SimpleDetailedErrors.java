/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.common.exception;

import com.atlassian.applinks.internal.common.exception.DetailedError;
import com.atlassian.applinks.internal.common.exception.DetailedErrors;
import com.atlassian.applinks.internal.common.exception.SimpleDetailedError;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SimpleDetailedErrors
implements DetailedErrors {
    private final List<DetailedError> errors;

    public SimpleDetailedErrors(@Nonnull Iterable<DetailedError> errors) {
        this.errors = ImmutableList.copyOf(errors);
    }

    @Override
    @Nonnull
    public Iterable<DetailedError> getErrors() {
        return this.errors;
    }

    @Override
    public boolean hasErrors() {
        return !this.errors.isEmpty();
    }

    public static final class Builder {
        private final List<DetailedError> errors = new ArrayList<DetailedError>();

        @Nonnull
        public Builder error(@Nullable String context, @Nonnull String summary, @Nullable String details) {
            this.errors.add(new SimpleDetailedError(context, summary, details));
            return this;
        }

        @Nonnull
        public Builder error(@Nonnull String summary) {
            this.errors.add(new SimpleDetailedError(summary));
            return this;
        }

        @Nonnull
        public SimpleDetailedErrors build() {
            return new SimpleDetailedErrors(this.errors);
        }

        public boolean hasErrors() {
            return !this.errors.isEmpty();
        }
    }
}

