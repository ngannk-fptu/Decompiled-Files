/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 */
package com.atlassian.zdu;

import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import com.atlassian.zdu.LicenseService;
import java.util.Map;

public class DCLicenseCondition
implements Condition {
    private final LicenseService licenseService;

    public DCLicenseCondition(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    public void init(Map<String, String> map) throws PluginParseException {
    }

    public boolean shouldDisplay(Map<String, Object> map) {
        return this.licenseService.isDataCenter();
    }
}

