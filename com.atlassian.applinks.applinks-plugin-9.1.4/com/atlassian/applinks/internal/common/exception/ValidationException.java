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
import com.atlassian.applinks.internal.common.exception.InvalidArgumentException;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ValidationException
extends InvalidArgumentException
implements DetailedErrors {
    private final Object origin;
    private final List<DetailedError> errors;

    public ValidationException(@Nonnull Object origin, @Nonnull Iterable<DetailedError> errors, @Nullable String message) {
        this(origin, errors, message, null);
    }

    public ValidationException(@Nonnull Object origin, @Nonnull Iterable<DetailedError> errors, @Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
        this.origin = Objects.requireNonNull(origin, "origin");
        this.errors = ImmutableList.copyOf(errors);
    }

    @Nonnull
    public Object getOrigin() {
        return this.origin;
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
}

