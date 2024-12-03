/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.spi.DcLicenseChecker
 *  com.atlassian.crowd.service.license.LicenseService
 */
package com.atlassian.crowd.license;

import com.atlassian.crowd.embedded.spi.DcLicenseChecker;
import com.atlassian.crowd.service.license.LicenseService;

public class DcLicenseCheckerImpl
implements DcLicenseChecker {
    private final LicenseService licenseService;

    public DcLicenseCheckerImpl(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    public boolean isDcLicense() {
        return this.licenseService.getLicense().isClusteringEnabled();
    }
}

