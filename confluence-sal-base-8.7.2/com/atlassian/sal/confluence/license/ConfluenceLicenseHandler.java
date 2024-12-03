/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.config.ConfigurationException
 *  com.atlassian.confluence.core.ConfluenceSidManager
 *  com.atlassian.confluence.license.LicenseService
 *  com.atlassian.confluence.license.exception.LicenseException
 *  com.atlassian.sal.api.i18n.InvalidOperationException
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.atlassian.sal.api.license.MultiProductLicenseDetails
 *  com.atlassian.sal.api.license.RawProductLicense
 *  com.atlassian.sal.api.license.SingleProductLicenseDetailsView
 *  com.atlassian.sal.api.validate.LicenseErrorCode
 *  com.atlassian.sal.api.validate.LicenseValidationError
 *  com.atlassian.sal.api.validate.LicenseValidationResult
 *  com.atlassian.sal.api.validate.LicenseValidationWarning
 *  com.atlassian.sal.api.validate.LicenseWarningCode
 *  com.atlassian.sal.api.validate.MultipleLicensesValidationResult
 *  com.atlassian.sal.api.validate.ValidationResult
 *  com.google.common.collect.ImmutableSortedSet
 *  javax.annotation.Nonnull
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.sal.confluence.license;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.config.ConfigurationException;
import com.atlassian.confluence.core.ConfluenceSidManager;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.license.exception.LicenseException;
import com.atlassian.sal.api.i18n.InvalidOperationException;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.sal.api.license.MultiProductLicenseDetails;
import com.atlassian.sal.api.license.RawProductLicense;
import com.atlassian.sal.api.license.SingleProductLicenseDetailsView;
import com.atlassian.sal.api.validate.LicenseErrorCode;
import com.atlassian.sal.api.validate.LicenseValidationError;
import com.atlassian.sal.api.validate.LicenseValidationResult;
import com.atlassian.sal.api.validate.LicenseValidationWarning;
import com.atlassian.sal.api.validate.LicenseWarningCode;
import com.atlassian.sal.api.validate.MultipleLicensesValidationResult;
import com.atlassian.sal.api.validate.ValidationResult;
import com.atlassian.sal.confluence.license.MultiProductLicenseDetailsImpl;
import com.atlassian.sal.confluence.license.SingleProductDetailsViewImpl;
import com.google.common.collect.ImmutableSortedSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ConfluenceLicenseHandler
implements LicenseHandler {
    public static final String UNSUPPORTED_PRODUCT_KEY_MESSAGE = "Unsupported product key ";
    private final ApplicationConfiguration applicationConfiguration;
    private final ConfluenceSidManager sidManager;
    private final LicenseService licenseService;

    public ConfluenceLicenseHandler(ConfluenceSidManager sidManager, LicenseService licenseService, ApplicationConfiguration applicationConfiguration) {
        this.applicationConfiguration = applicationConfiguration;
        this.licenseService = licenseService;
        this.sidManager = sidManager;
    }

    @Deprecated
    public void setLicense(String licenseString) {
        try {
            this.licenseService.install(licenseString);
        }
        catch (LicenseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public String getServerId() {
        try {
            return this.sidManager.getSid();
        }
        catch (ConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    public String getSupportEntitlementNumber() {
        return this.licenseService.retrieve().getSupportEntitlementNumber();
    }

    public boolean hostAllowsMultipleLicenses() {
        return false;
    }

    public boolean hostAllowsCustomProducts() {
        return false;
    }

    public Set<String> getProductKeys() {
        return Collections.singleton("conf");
    }

    public void addProductLicense(@Nonnull String productKey, @Nonnull String license) throws InvalidOperationException {
        if (!"conf".equalsIgnoreCase(productKey)) {
            String message = UNSUPPORTED_PRODUCT_KEY_MESSAGE + productKey;
            throw new InvalidOperationException(message, message);
        }
        try {
            this.licenseService.install(license);
        }
        catch (LicenseException e) {
            throw new InvalidOperationException(e.getMessage(), e.getLocalizedMessage());
        }
    }

    public void addProductLicenses(@Nonnull Set<RawProductLicense> rawProductLicenses) throws InvalidOperationException {
        switch (rawProductLicenses.size()) {
            case 0: {
                return;
            }
            case 1: {
                this.addProductLicense(rawProductLicenses.iterator().next());
                break;
            }
            default: {
                throw new UnsupportedOperationException(String.format("Expected at most one license, but found %d", rawProductLicenses.size()));
            }
        }
    }

    private void addProductLicense(RawProductLicense newLicense) throws InvalidOperationException {
        String productKey = newLicense.getProductKey().orElse("");
        String licenseString = newLicense.getLicense().orElse("");
        this.addProductLicense(productKey, licenseString);
    }

    public void removeProductLicense(@Nonnull String productKey) throws InvalidOperationException {
        String message = "Cannot remove Confluence license";
        throw new InvalidOperationException("Cannot remove Confluence license", "Cannot remove Confluence license");
    }

    public @NonNull ValidationResult validateProductLicense(@Nonnull String productKey, @Nonnull String license, Locale locale) {
        if (!"conf".equalsIgnoreCase(productKey)) {
            String message = UNSUPPORTED_PRODUCT_KEY_MESSAGE + productKey;
            return ValidationResult.withErrorMessages(Collections.singleton(message));
        }
        try {
            this.licenseService.validate(license);
            return ValidationResult.valid();
        }
        catch (LicenseException e) {
            return ValidationResult.withErrorMessages(Collections.singleton("Failed to validate license: " + e.getMessage()));
        }
    }

    @Nonnull
    public MultipleLicensesValidationResult validateMultipleProductLicenses(@Nonnull Set<RawProductLicense> rawProductLicenses, @Nullable Locale userLocale) {
        switch (rawProductLicenses.size()) {
            case 0: {
                return new MultipleLicensesValidationResult(Collections.emptySet());
            }
            case 1: {
                return this.validateRawProductLicense(rawProductLicenses.iterator().next(), userLocale);
            }
        }
        throw new UnsupportedOperationException(String.format("Expected at most one license, but found %d", rawProductLicenses.size()));
    }

    private MultipleLicensesValidationResult validateRawProductLicense(RawProductLicense license, @Nullable Locale userLocale) {
        String productKey = license.getProductKey().orElse("");
        String licenseString = license.getLicense().orElse("");
        ValidationResult validationResult = this.validateProductLicense(productKey, licenseString, userLocale);
        LicenseValidationResult licenseValidationResult = ConfluenceLicenseHandler.toLicenseValidationResult(validationResult, license);
        return new MultipleLicensesValidationResult(Collections.singleton(licenseValidationResult));
    }

    private static LicenseValidationResult toLicenseValidationResult(ValidationResult validationResult, RawProductLicense license) {
        Optional productKey = license.getProductKey();
        Optional licenseString = license.getLicense();
        if (validationResult.isValid()) {
            return LicenseValidationResult.withValidResult((Optional)productKey, (Optional)licenseString);
        }
        if (validationResult.hasErrors() && validationResult.hasWarnings()) {
            return LicenseValidationResult.withErrorAndWarningMessages((Optional)productKey, (Optional)licenseString, ConfluenceLicenseHandler.toErrors(validationResult.getErrorMessages()), ConfluenceLicenseHandler.toWarnings(validationResult.getWarningMessages()));
        }
        if (validationResult.hasErrors()) {
            return LicenseValidationResult.withErrorMessages((Optional)productKey, (Optional)licenseString, ConfluenceLicenseHandler.toErrors(validationResult.getErrorMessages()));
        }
        if (validationResult.hasWarnings()) {
            return LicenseValidationResult.withWarningMessages((Optional)productKey, (Optional)licenseString, ConfluenceLicenseHandler.toWarnings(validationResult.getWarningMessages()));
        }
        throw new IllegalStateException("Validation result is not valid, but has no errors or warnings");
    }

    private static Iterable<LicenseValidationWarning> toWarnings(Iterable<String> warningMessages) {
        return StreamSupport.stream(warningMessages.spliterator(), false).map(message -> new LicenseValidationWarning(LicenseWarningCode.UNKNOWN, message)).collect(Collectors.toList());
    }

    private static Iterable<LicenseValidationError> toErrors(Iterable<String> errorMessages) {
        return StreamSupport.stream(errorMessages.spliterator(), false).map(message -> new LicenseValidationError(LicenseErrorCode.UNKNOWN, message)).collect(Collectors.toList());
    }

    public @NonNull SortedSet<String> getAllSupportEntitlementNumbers() {
        String sen = this.licenseService.retrieve().getSupportEntitlementNumber();
        return sen != null ? ImmutableSortedSet.of((Comparable)((Object)sen)) : ImmutableSortedSet.of();
    }

    public @Nullable String getRawProductLicense(String productKey) {
        return (String)this.applicationConfiguration.getProperty((Object)"atlassian.license.message");
    }

    public @Nullable SingleProductLicenseDetailsView getProductLicenseDetails(@Nonnull String productKey) {
        if (!"conf".equalsIgnoreCase(productKey)) {
            throw new IllegalArgumentException(UNSUPPORTED_PRODUCT_KEY_MESSAGE + productKey);
        }
        return new SingleProductDetailsViewImpl(this.licenseService.retrieve());
    }

    public @NonNull Collection<MultiProductLicenseDetails> getAllProductLicenses() {
        return Collections.singleton(new MultiProductLicenseDetailsImpl(this.licenseService.retrieve()));
    }

    public @NonNull MultiProductLicenseDetails decodeLicenseDetails(String license) {
        return new MultiProductLicenseDetailsImpl(this.licenseService.validate(license));
    }
}

