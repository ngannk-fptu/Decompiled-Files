/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.sal.api.validate;

import com.atlassian.sal.api.validate.LicenseValidationResult;
import java.util.Collection;
import java.util.Collections;
import javax.annotation.Nonnull;

public class MultipleLicensesValidationResult {
    private final Collection<LicenseValidationResult> licenseValidationResults;

    public MultipleLicensesValidationResult(@Nonnull Collection<LicenseValidationResult> licenseValidationResults) {
        this.licenseValidationResults = Collections.unmodifiableCollection(licenseValidationResults);
    }

    public Collection<LicenseValidationResult> getLicenseValidationResults() {
        return Collections.unmodifiableCollection(this.licenseValidationResults);
    }

    public boolean isValid() {
        return this.licenseValidationResults.stream().allMatch(LicenseValidationResult::isValid);
    }

    public boolean hasErrors() {
        return this.licenseValidationResults.stream().anyMatch(res -> res.hasErrors());
    }

    public boolean hasWarnings() {
        return this.licenseValidationResults.stream().anyMatch(res -> res.hasWarnings());
    }
}

