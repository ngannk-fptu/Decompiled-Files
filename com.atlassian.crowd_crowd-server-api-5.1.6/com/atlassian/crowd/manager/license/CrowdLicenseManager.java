/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.crowd.CrowdLicense
 */
package com.atlassian.crowd.manager.license;

import com.atlassian.crowd.manager.license.CrowdLicenseManagerException;
import com.atlassian.extras.api.crowd.CrowdLicense;

public interface CrowdLicenseManager {
    public CrowdLicense getLicense();

    public CrowdLicense getLicense(String var1);

    public String getRawLicense();

    public CrowdLicense storeLicense(String var1) throws CrowdLicenseManagerException;

    public boolean isLicenseValid();

    public boolean isLicenseValid(CrowdLicense var1);

    public boolean isLicenseKeyValid(String var1);

    public boolean isSetupLicenseKeyValid(String var1);

    public int getCurrentResourceUsageTotal();

    public int recalculateResourceUsageTotal() throws CrowdLicenseManagerException;

    public boolean isResourceTotalOverLimit(float var1, int var2);

    public boolean isBuildWithinMaintenancePeriod(CrowdLicense var1);

    public boolean isBuildWithinMaintenancePeriod(String var1);

    public boolean isResourceTotalUnderLimitInLicenseKey(String var1);
}

