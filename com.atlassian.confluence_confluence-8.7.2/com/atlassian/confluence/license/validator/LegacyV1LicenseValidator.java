/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 */
package com.atlassian.confluence.license.validator;

import com.atlassian.confluence.license.exception.KnownConfluenceLicenseValidationException;
import com.atlassian.confluence.license.validator.LicenseValidator;
import com.atlassian.extras.api.confluence.ConfluenceLicense;

public class LegacyV1LicenseValidator
implements LicenseValidator {
    @Override
    public void validate(ConfluenceLicense license) {
        if (license.getLicenseVersion() == 1) {
            throw new KnownConfluenceLicenseValidationException(license, KnownConfluenceLicenseValidationException.Reason.LEGACY_VERSION_1);
        }
    }
}

