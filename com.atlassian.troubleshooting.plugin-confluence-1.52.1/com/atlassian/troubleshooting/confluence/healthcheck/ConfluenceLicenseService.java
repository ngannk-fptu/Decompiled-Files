/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.license.LicenseService
 *  com.atlassian.extras.api.Product
 *  com.atlassian.extras.api.ProductLicense
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.confluence.healthcheck;

import com.atlassian.confluence.license.LicenseService;
import com.atlassian.extras.api.Product;
import com.atlassian.extras.api.ProductLicense;
import com.atlassian.extras.api.confluence.ConfluenceLicense;
import com.atlassian.troubleshooting.preupgrade.checks.Expiry;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;

public class ConfluenceLicenseService
implements com.atlassian.troubleshooting.api.healthcheck.LicenseService {
    private final LicenseService confluenceLicenseService;

    @Autowired
    public ConfluenceLicenseService(LicenseService confluenceLicenseService) {
        this.confluenceLicenseService = confluenceLicenseService;
    }

    @Override
    public boolean isEvaluation() {
        return this.confluenceLicenseService.retrieve().isEvaluation();
    }

    @Override
    public boolean userCanRequestTechnicalSupport() {
        return !com.atlassian.troubleshooting.api.healthcheck.LicenseService.isStarterLicense((ProductLicense)this.confluenceLicenseService.retrieve(), Product.CONFLUENCE.getNamespace());
    }

    @Override
    public boolean isWithinMaintenanceFor(Date date) {
        ConfluenceLicense license = this.confluenceLicenseService.retrieve();
        return this.isEnterprise(license) || Expiry.fromDate(license.getMaintenanceExpiryDate()).isBeforeExpiry(date);
    }

    @Override
    public boolean isLicensedForDataCenter() {
        return this.confluenceLicenseService.isLicensedForDataCenter();
    }

    @Override
    public boolean isExpired() {
        return this.confluenceLicenseService.retrieve().isExpired();
    }

    private boolean isEnterprise(ConfluenceLicense license) {
        return "true".equals(license.getProperty("ELA"));
    }
}

