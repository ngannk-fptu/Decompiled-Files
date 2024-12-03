/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.service.license.LicenseService
 */
package com.atlassian.analytics.client.license;

import com.atlassian.analytics.client.license.LicenseProvider;
import com.atlassian.crowd.service.license.LicenseService;
import java.util.Date;

public class CrowdLicenseProvider
implements LicenseProvider {
    private final LicenseService licenseService;

    public CrowdLicenseProvider(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    @Override
    public Date getLicenseCreationDate() {
        return this.licenseService.getLicense().getCreationDate();
    }
}

