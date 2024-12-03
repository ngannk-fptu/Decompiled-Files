/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.Condition
 *  com.atlassian.sal.api.license.BaseLicenseDetails
 *  com.atlassian.sal.api.license.LicenseHandler
 */
package com.atlassian.plugins.impl.conditions;

import com.atlassian.plugin.web.Condition;
import com.atlassian.sal.api.license.BaseLicenseDetails;
import com.atlassian.sal.api.license.LicenseHandler;
import java.util.Map;

public class IsDataCenterCondition
implements Condition {
    private final LicenseHandler licenseHandler;

    public IsDataCenterCondition(LicenseHandler licenseHandler) {
        this.licenseHandler = licenseHandler;
    }

    public void init(Map<String, String> map) {
    }

    public boolean shouldDisplay(Map<String, Object> map) {
        return this.licenseHandler.getAllProductLicenses().stream().allMatch(BaseLicenseDetails::isDataCenter);
    }
}

