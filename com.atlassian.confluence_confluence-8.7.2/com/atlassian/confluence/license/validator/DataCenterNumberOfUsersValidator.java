/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 */
package com.atlassian.confluence.license.validator;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.license.exception.KnownConfluenceLicenseValidationException;
import com.atlassian.confluence.license.validator.LicenseValidator;
import com.atlassian.extras.api.confluence.ConfluenceLicense;

@Internal
public class DataCenterNumberOfUsersValidator
implements LicenseValidator {
    @Override
    public void validate(ConfluenceLicense license) {
        if (license.isClusteringEnabled() && !license.isEvaluation() && license.getMaximumNumberOfUsers() <= 0 && !license.isUnlimitedNumberOfUsers()) {
            throw new KnownConfluenceLicenseValidationException(license, KnownConfluenceLicenseValidationException.Reason.LICENSE_INVALID_NUMBER_OF_USERS);
        }
    }
}

