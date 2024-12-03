/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.spi.feature.FileAuditingFeature
 */
package com.atlassian.confluence.api.impl.service.audit.adapter;

import com.atlassian.audit.spi.feature.FileAuditingFeature;
import com.atlassian.confluence.license.LicenseService;

public class DcLicenseFileAuditingFeature
implements FileAuditingFeature {
    private final LicenseService licenseService;

    public DcLicenseFileAuditingFeature(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    public boolean isEnabled() {
        return this.licenseService.isLicensedForDataCenterOrExempt();
    }
}

