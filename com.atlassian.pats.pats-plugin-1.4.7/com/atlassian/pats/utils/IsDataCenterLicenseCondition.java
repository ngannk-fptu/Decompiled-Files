/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 */
package com.atlassian.pats.utils;

import com.atlassian.pats.utils.LicenseChecker;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import java.util.Map;

public class IsDataCenterLicenseCondition
implements Condition {
    private final LicenseChecker licenseChecker;

    public IsDataCenterLicenseCondition(LicenseChecker licenseChecker) {
        this.licenseChecker = licenseChecker;
    }

    public void init(Map<String, String> map) throws PluginParseException {
    }

    public boolean shouldDisplay(Map<String, Object> map) {
        return this.licenseChecker.isDataCenterProduct();
    }
}

