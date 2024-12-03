/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.sal.api.validate;

import com.atlassian.sal.api.validate.LicenseErrorCode;
import java.util.Objects;
import javax.annotation.Nonnull;

public class LicenseValidationError {
    private final LicenseErrorCode licenseErrorCode;
    private final String errorMessage;

    public LicenseValidationError(@Nonnull LicenseErrorCode licenseErrorCode, @Nonnull String errorMessage) {
        this.licenseErrorCode = Objects.requireNonNull(licenseErrorCode);
        this.errorMessage = Objects.requireNonNull(errorMessage);
    }

    @Nonnull
    public LicenseErrorCode getLicenseErrorCode() {
        return this.licenseErrorCode;
    }

    @Nonnull
    public String getErrorMessage() {
        return this.errorMessage;
    }
}

