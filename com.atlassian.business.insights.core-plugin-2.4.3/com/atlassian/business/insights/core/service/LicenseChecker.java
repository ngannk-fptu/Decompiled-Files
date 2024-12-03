/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.sal.api.license.BaseLicenseDetails
 *  com.atlassian.sal.api.license.LicenseHandler
 */
package com.atlassian.business.insights.core.service;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.sal.api.license.BaseLicenseDetails;
import com.atlassian.sal.api.license.LicenseHandler;

public class LicenseChecker {
    @VisibleForTesting
    public static final String CONFLUENCE_BYPASS_DATA_CENTER_CHECK_PROPERTY_KEY = "confluence.bypass.data.center.check";
    private final LicenseHandler licenseHandler;

    public LicenseChecker(LicenseHandler licenseHandler) {
        this.licenseHandler = licenseHandler;
    }

    public boolean isDcLicense() {
        return this.licenseHandler.getAllProductLicenses().stream().allMatch(this::isDcLicenseOrExempt);
    }

    private boolean isDcLicenseOrExempt(BaseLicenseDetails license) {
        return "true".equals(license.getProperty(CONFLUENCE_BYPASS_DATA_CENTER_CHECK_PROPERTY_KEY)) || license.isDataCenter();
    }
}

