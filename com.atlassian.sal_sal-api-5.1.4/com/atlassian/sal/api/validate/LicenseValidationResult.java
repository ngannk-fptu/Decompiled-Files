/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.google.common.collect.ImmutableSet
 *  javax.annotation.Nonnull
 */
package com.atlassian.sal.api.validate;

import com.atlassian.annotations.PublicApi;
import com.atlassian.sal.api.validate.LicenseValidationError;
import com.atlassian.sal.api.validate.LicenseValidationWarning;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;

@PublicApi
public class LicenseValidationResult {
    private final Optional<String> productKey;
    private final Optional<String> license;
    private final ImmutableSet<LicenseValidationError> errorMessages;
    private final ImmutableSet<LicenseValidationWarning> warningMessages;

    private LicenseValidationResult(@Nonnull Optional<String> productKey, @Nonnull Optional<String> license, @Nonnull Iterable<LicenseValidationError> errorMessages, @Nonnull Iterable<LicenseValidationWarning> warningMessages) {
        this.productKey = Objects.requireNonNull(productKey);
        this.license = Objects.requireNonNull(license);
        this.errorMessages = ImmutableSet.copyOf(Objects.requireNonNull(errorMessages));
        this.warningMessages = ImmutableSet.copyOf(Objects.requireNonNull(warningMessages));
    }

    public static LicenseValidationResult withErrorMessages(@Nonnull Optional<String> productKey, @Nonnull Optional<String> license, @Nonnull Iterable<LicenseValidationError> errorMessages) {
        return new LicenseValidationResult(productKey, license, errorMessages, Collections.emptySet());
    }

    public static LicenseValidationResult withWarningMessages(@Nonnull Optional<String> productKey, @Nonnull Optional<String> license, @Nonnull Iterable<LicenseValidationWarning> warningMessages) {
        return new LicenseValidationResult(productKey, license, Collections.emptySet(), warningMessages);
    }

    public static LicenseValidationResult withErrorAndWarningMessages(@Nonnull Optional<String> productKey, @Nonnull Optional<String> license, @Nonnull Iterable<LicenseValidationError> errorMessages, @Nonnull Iterable<LicenseValidationWarning> warningMessages) {
        return new LicenseValidationResult(productKey, license, errorMessages, warningMessages);
    }

    public static LicenseValidationResult withValidResult(@Nonnull Optional<String> productKey, @Nonnull Optional<String> license) {
        return new LicenseValidationResult(productKey, license, Collections.emptySet(), Collections.emptySet());
    }

    public Optional<String> getProductKey() {
        return this.productKey;
    }

    public Optional<String> getLicense() {
        return this.license;
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
    public Collection<LicenseValidationError> getErrorMessages() {
        return Collections.unmodifiableCollection(this.errorMessages);
    }

    @Nonnull
    public Collection<LicenseValidationWarning> getWarningMessages() {
        return Collections.unmodifiableCollection(this.warningMessages);
    }
}

