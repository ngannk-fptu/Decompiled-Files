/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.sal.api.validate;

import com.atlassian.sal.api.validate.LicenseWarningCode;
import java.util.Objects;
import javax.annotation.Nonnull;

public class LicenseValidationWarning {
    private final LicenseWarningCode licenseWarningCode;
    private final String warningMessage;

    public LicenseValidationWarning(@Nonnull LicenseWarningCode licenseWarningCode, @Nonnull String warningMessage) {
        this.licenseWarningCode = Objects.requireNonNull(licenseWarningCode);
        this.warningMessage = Objects.requireNonNull(warningMessage);
    }

    @Nonnull
    public LicenseWarningCode getLicenseWarningCode() {
        return this.licenseWarningCode;
    }

    @Nonnull
    public String getWarningMessage() {
        return this.warningMessage;
    }
}

