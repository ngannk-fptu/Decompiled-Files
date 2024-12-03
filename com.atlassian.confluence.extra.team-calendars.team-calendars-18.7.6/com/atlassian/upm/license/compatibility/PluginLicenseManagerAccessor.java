/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.license.compatibility;

import com.atlassian.upm.license.compatibility.CompatiblePluginLicenseManager;
import com.atlassian.upm.license.compatibility.LegacyCompatiblePluginLicenseManager;

public interface PluginLicenseManagerAccessor {
    public CompatiblePluginLicenseManager getPluginLicenseManager();

    public LegacyCompatiblePluginLicenseManager getLegacyPluginLicenseManager();

    public boolean isOnDemand();

    public boolean isUpmPluginLicenseManagerResolved();
}

