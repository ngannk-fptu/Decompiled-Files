/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.healthcheck.checks.datacenter;

import com.atlassian.troubleshooting.api.healthcheck.LicenseService;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheckCondition;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;

public class NonDCLicenseCondition
implements SupportHealthCheckCondition {
    private final LicenseService licenseService;

    @Autowired
    public NonDCLicenseCondition(LicenseService licenseService) {
        this.licenseService = Objects.requireNonNull(licenseService);
    }

    @Override
    public boolean shouldDisplay() {
        return !this.licenseService.isLicensedForDataCenter();
    }
}

