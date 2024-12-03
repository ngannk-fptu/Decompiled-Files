/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.license.BaseLicenseDetails
 *  com.atlassian.sal.api.license.LicenseHandler
 */
package com.atlassian.pats.utils;

import com.atlassian.sal.api.license.BaseLicenseDetails;
import com.atlassian.sal.api.license.LicenseHandler;

public class LicenseChecker {
    private final LicenseHandler licenseHandler;

    public LicenseChecker(LicenseHandler licenseHandler) {
        this.licenseHandler = licenseHandler;
    }

    public boolean isDataCenterProduct() {
        return this.licenseHandler.getAllProductLicenses().stream().allMatch(BaseLicenseDetails::isDataCenter);
    }
}

