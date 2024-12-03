/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.license.BaseLicenseDetails
 *  com.atlassian.sal.api.license.LicenseHandler
 */
package com.atlassian.audit.coverage;

import com.atlassian.sal.api.license.BaseLicenseDetails;
import com.atlassian.sal.api.license.LicenseHandler;

public class ProductLicenseChecker {
    private static final String CONFLUENCE_BYPASS_DATA_CENTER_CHECK_PROPERTY_KEY = "confluence.bypass.data.center.check";
    private final LicenseHandler licenseHandler;

    public ProductLicenseChecker(LicenseHandler licenseHandler) {
        this.licenseHandler = licenseHandler;
    }

    public boolean isNotDcLicense() {
        return !this.isDcLicenseOrExempt();
    }

    public boolean isDcLicenseOrExempt() {
        return this.licenseHandler.getAllProductLicenses().stream().allMatch(this::isDcLicenseOrExempt);
    }

    private boolean isDcLicenseOrExempt(BaseLicenseDetails license) {
        return "true".equals(license.getProperty(CONFLUENCE_BYPASS_DATA_CENTER_CHECK_PROPERTY_KEY)) || license.isDataCenter();
    }
}

