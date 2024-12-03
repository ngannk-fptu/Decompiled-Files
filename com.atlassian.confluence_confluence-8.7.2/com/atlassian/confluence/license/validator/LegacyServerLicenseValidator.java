/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.license.validator;

import com.atlassian.confluence.license.exception.KnownConfluenceLicenseValidationException;
import com.atlassian.confluence.license.validator.LicenseValidator;
import com.atlassian.extras.api.confluence.ConfluenceLicense;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LegacyServerLicenseValidator
implements LicenseValidator {
    public static final Logger log = LoggerFactory.getLogger(LegacyServerLicenseValidator.class);

    @Override
    public void validate(ConfluenceLicense license) {
        if (!license.isClusteringEnabled()) {
            throw new KnownConfluenceLicenseValidationException(license, KnownConfluenceLicenseValidationException.Reason.LEGACY_SERVER_LICENSE);
        }
    }
}

