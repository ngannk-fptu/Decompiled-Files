/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.LicenseType
 *  com.atlassian.extras.api.ProductLicense
 */
package com.atlassian.troubleshooting.stp.salext.license;

import com.atlassian.extras.api.LicenseType;
import com.atlassian.extras.api.ProductLicense;
import com.atlassian.troubleshooting.stp.salext.license.ApplicationLicenseInfo;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

public class ProductLicenseInfo
extends ApplicationLicenseInfo {
    private final ProductLicense license;

    public ProductLicenseInfo(ProductLicense license) {
        this.license = license;
    }

    @Override
    public Date getMaintenanceExpiryDate() {
        return this.license == null ? null : this.license.getMaintenanceExpiryDate();
    }

    @Override
    public boolean isEntitledToSupport() {
        if (this.license == null) {
            return false;
        }
        LicenseType licenseType = this.license.getLicenseType();
        if (licenseType == LicenseType.DEMONSTRATION) {
            return false;
        }
        if (licenseType == LicenseType.TESTING) {
            return false;
        }
        if (licenseType == LicenseType.NON_PROFIT) {
            return false;
        }
        return licenseType != LicenseType.PERSONAL;
    }

    @Override
    public boolean isEvaluation() {
        return this.license != null && this.license.isEvaluation();
    }

    @Override
    public boolean isStarter() {
        return this.license != null && this.license.getLicenseType() == LicenseType.STARTER;
    }

    @Override
    public String getSEN() {
        return this.license == null ? null : this.license.getSupportEntitlementNumber();
    }

    @Override
    public Set<Integer> getUserLimits() {
        return this.license == null ? Collections.emptySet() : Collections.singleton(this.license.getMaximumNumberOfUsers());
    }
}

