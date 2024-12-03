/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.Condition
 *  com.atlassian.sal.api.license.BaseLicenseDetails
 *  com.atlassian.sal.api.license.LicenseHandler
 */
package com.atlassian.plugins.cleanuphub;

import com.atlassian.plugin.web.Condition;
import com.atlassian.sal.api.license.BaseLicenseDetails;
import com.atlassian.sal.api.license.LicenseHandler;
import java.util.Collection;
import java.util.Map;

public class CleanupHubEnabledCondition
implements Condition {
    private final LicenseHandler licenseHandler;

    public CleanupHubEnabledCondition(LicenseHandler licenseHandler) {
        this.licenseHandler = licenseHandler;
    }

    public void init(Map<String, String> map) {
    }

    private boolean licensesAreDataCenter() {
        try {
            Collection licenseDetails = this.licenseHandler.getAllProductLicenses();
            return !licenseDetails.isEmpty() && licenseDetails.stream().allMatch(BaseLicenseDetails::isDataCenter);
        }
        catch (Exception e) {
            return false;
        }
    }

    public boolean shouldDisplay(Map<String, Object> map) {
        return this.licensesAreDataCenter();
    }
}

