/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.license.LicenseService
 *  com.atlassian.extras.api.ProductLicense
 */
package com.atlassian.analytics.client.license;

import com.atlassian.analytics.client.license.LicenseProvider;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.extras.api.ProductLicense;
import java.util.Date;

public class ConfluenceLicenseProvider
implements LicenseProvider {
    private final LicenseService licenseService;

    public ConfluenceLicenseProvider(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    @Override
    public Date getLicenseCreationDate() {
        return this.getProductLicense().getCreationDate();
    }

    private ProductLicense getProductLicense() {
        return this.licenseService.retrieve();
    }
}

