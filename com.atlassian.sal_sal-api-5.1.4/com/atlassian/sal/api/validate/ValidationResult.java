/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nonnull
 */
package com.atlassian.sal.api.validate;

import com.atlassian.annotations.PublicApi;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import javax.annotation.Nonnull;

@PublicApi
public class ValidationResult {
    private static final ValidationResult VALID = new ValidationResult(Collections.emptySet());
    private final ImmutableSet<String> errorMessages;
    private final ImmutableSet<String> warningMessages;

    private ValidationResult(@Nonnull Iterable<String> errorMessages) {
        this(errorMessages, Collections.emptySet());
    }

    private ValidationResult(@Nonnull Iterable<String> errorMessages, @Nonnull Iterable<String> warningMessages) {
        Preconditions.checkNotNull(errorMessages, (Object)"errorMessages");
        Preconditions.checkNotNull(warningMessages, (Object)"warningMessages");
        this.errorMessages = ImmutableSet.copyOf(errorMessages);
        this.warningMessages = ImmutableSet.copyOf(warningMessages);
    }

    public static ValidationResult valid() {
        return VALID;
    }

    public static ValidationResult withErrorMessages(@Nonnull Iterable<String> errorMessages) {
        return new ValidationResult(errorMessages);
    }

    public static ValidationResult withWarningMessages(@Nonnull Iterable<String> warningMessages) {
        return new ValidationResult(Collections.emptySet(), warningMessages);
    }

    public static ValidationResult withErrorAndWarningMessages(@Nonnull Iterable<String> errorMessages, @Nonnull Iterable<String> warningMessages) {
        return new ValidationResult(errorMessages, warningMessages);
    }

    public boolean isValid() {
        return this.errorMessages.isEmpty();
    }

    public boolean hasErrors() {
        return !this.errorMessages.isEmpty();
    }

    public boolean hasWarnings() {
        return !this.warningMessages.isEmpty();
    }

    @Nonnull
    public Iterable<String> getErrorMessages() {
        return this.errorMessages;
    }

    @Nonnull
    public Iterable<String> getWarningMessages() {
        return this.warningMessages;
    }
}

