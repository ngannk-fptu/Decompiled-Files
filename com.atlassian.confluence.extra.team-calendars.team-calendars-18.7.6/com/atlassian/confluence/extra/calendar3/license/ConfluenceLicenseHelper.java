/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.fugue.Maybe
 *  com.atlassian.fugue.Option
 *  com.atlassian.license.LicenseException
 *  com.atlassian.license.LicensePair
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.license;

import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.fugue.Maybe;
import com.atlassian.fugue.Option;
import com.atlassian.license.LicenseException;
import com.atlassian.license.LicensePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfluenceLicenseHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfluenceLicenseHelper.class);
    private static final String ATLASSIAN_LICENSE_KEY = "atlassian.license.message";
    private final ApplicationConfiguration applicationConfig;

    public ConfluenceLicenseHelper(ApplicationConfiguration applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    public String getProductLicenseString() throws LicenseException {
        LicensePair licensePair = (LicensePair)this.retrieveLicense().getOrNull();
        if (licensePair == null) {
            throw new LicenseException("Could not retrieve Confluence License");
        }
        return licensePair.getOriginalLicenseString();
    }

    protected Maybe<LicensePair> retrieveLicense() {
        LicensePair returnLicense = null;
        try {
            String licenseString = (String)this.applicationConfig.getProperty((Object)ATLASSIAN_LICENSE_KEY);
            if (licenseString != null) {
                returnLicense = new LicensePair(licenseString);
            } else {
                String licenseMessage = (String)this.applicationConfig.getProperty((Object)"confluence.license.message");
                String licenseHash = (String)this.applicationConfig.getProperty((Object)"confluence.license.hash");
                if (licenseMessage != null && licenseHash != null) {
                    returnLicense = new LicensePair(licenseMessage, licenseHash);
                }
            }
        }
        catch (LicenseException e) {
            LOGGER.warn("Unable to parse license string", (Throwable)e);
        }
        return Option.option(returnLicense);
    }
}

