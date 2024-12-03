/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.Condition
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions;

import com.atlassian.confluence.license.LicenseService;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import java.util.Map;

public class IsConfluenceSpacesLicensedCondition
implements Condition {
    private LicenseService licenseService;
    private PluginAccessor pluginAccessor;

    public void init(Map<String, String> stringStringMap) throws PluginParseException {
    }

    public boolean shouldDisplay(Map<String, Object> stringObjectMap) {
        return this.pluginAccessor.isPluginEnabled("com.atlassian.confluence.plugins.confluence-spaces") && !this.licenseService.retrieve().isExpired();
    }

    public void setLicenseService(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    public void setPluginAccessor(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }
}

