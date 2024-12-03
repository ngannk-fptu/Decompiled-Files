/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.license.LicenseService
 *  com.atlassian.extras.api.confluence.ConfluenceLicense
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.confluence.healthcheck.license;

import com.atlassian.confluence.license.LicenseService;
import com.atlassian.extras.api.confluence.ConfluenceLicense;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthCheck;
import com.atlassian.troubleshooting.api.healthcheck.SupportHealthStatus;
import com.atlassian.troubleshooting.healthcheck.SupportHealthStatusBuilder;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.springframework.beans.factory.annotation.Autowired;

public final class LicenseHealthCheck
implements SupportHealthCheck {
    private final DateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy");
    private final LicenseService licenseService;
    private final SupportHealthStatusBuilder healthStatusBuilder;

    @Autowired
    public LicenseHealthCheck(LicenseService licenseService, SupportHealthStatusBuilder healthStatusBuilder) {
        this.licenseService = licenseService;
        this.healthStatusBuilder = healthStatusBuilder;
    }

    @Override
    public boolean isNodeSpecific() {
        return false;
    }

    @Override
    public SupportHealthStatus check() {
        ConfluenceLicense license = this.licenseService.retrieve();
        String expiryDateString = this.dateFormatter.format(license.getMaintenanceExpiryDate());
        int daysToExpiry = license.getNumberOfDaysBeforeMaintenanceExpiry();
        if (daysToExpiry < 0) {
            return this.healthStatusBuilder.major(this, this.getLicenseExpiredPlaceholder(license), new Serializable[]{expiryDateString});
        }
        int warningTimePeriod = this.getWarningPeriod(license);
        if (daysToExpiry <= warningTimePeriod) {
            return this.healthStatusBuilder.warning(this, this.getLicenseExpiringPlaceholder(license), new Serializable[]{Integer.valueOf(warningTimePeriod), expiryDateString});
        }
        return this.healthStatusBuilder.ok(this, this.getLicenseValidPlaceholder(license), new Serializable[]{expiryDateString});
    }

    private String getLicenseValidPlaceholder(ConfluenceLicense license) {
        return license.isSubscription() ? "confluence.healthcheck.license.valid.datacenter" : "confluence.healthcheck.license.valid";
    }

    private String getLicenseExpiringPlaceholder(ConfluenceLicense license) {
        return license.isSubscription() ? "confluence.healthcheck.license.expiring.datacenter" : "confluence.healthcheck.license.expiring";
    }

    private String getLicenseExpiredPlaceholder(ConfluenceLicense license) {
        return license.isSubscription() ? "confluence.healthcheck.license.expired.datacenter" : "confluence.healthcheck.license.expired";
    }

    private int getWarningPeriod(ConfluenceLicense license) {
        if (license.isEvaluation()) {
            return 7;
        }
        return 30;
    }
}

