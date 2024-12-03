/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.extras.api.ProductLicense
 *  com.google.common.base.Preconditions
 */
package com.atlassian.upm.license.compatibility;

import com.atlassian.extras.api.ProductLicense;
import com.atlassian.upm.SysCommon;
import com.atlassian.upm.license.compatibility.CompatibleLicenseStatus;
import com.atlassian.upm.license.compatibility.CompatiblePluginLicenseManager;
import com.atlassian.upm.license.compatibility.LegacyCompatiblePluginLicenseManager;
import com.google.common.base.Preconditions;
import java.net.URI;

public class OnDemandCompatiblePluginLicenseManager
implements CompatiblePluginLicenseManager {
    private final LegacyCompatiblePluginLicenseManager legacyPluginLicenseManager;
    private final CompatiblePluginLicenseManager defaultPluginLicenseManager;

    public OnDemandCompatiblePluginLicenseManager(LegacyCompatiblePluginLicenseManager legacyPluginLicenseManager, CompatiblePluginLicenseManager defaultPluginLicenseManager) {
        this.legacyPluginLicenseManager = (LegacyCompatiblePluginLicenseManager)Preconditions.checkNotNull((Object)legacyPluginLicenseManager, (Object)"legacyPluginLicenseManager");
        this.defaultPluginLicenseManager = (CompatiblePluginLicenseManager)Preconditions.checkNotNull((Object)defaultPluginLicenseManager, (Object)"defaultPluginLicenseManager");
    }

    @Override
    public ProductLicense setLicense(String rawLicense) {
        return this.defaultPluginLicenseManager.setLicense(rawLicense);
    }

    @Override
    public ProductLicense getCurrentLicense() {
        return this.defaultPluginLicenseManager.getCurrentLicense();
    }

    @Override
    public ProductLicense getLicense(String rawLicense) {
        return this.defaultPluginLicenseManager.getLicense(rawLicense);
    }

    @Override
    public CompatibleLicenseStatus getCurrentLicenseStatus() {
        return this.defaultPluginLicenseManager.getCurrentLicenseStatus();
    }

    @Override
    public CompatibleLicenseStatus getLicenseStatus(String rawLicense) {
        return this.defaultPluginLicenseManager.getLicenseStatus(rawLicense);
    }

    @Override
    public URI getPluginLicenseAdministrationUri() {
        if (SysCommon.isOnDemand()) {
            return this.legacyPluginLicenseManager.getPluginLicenseAdministrationUri();
        }
        return this.defaultPluginLicenseManager.getPluginLicenseAdministrationUri();
    }
}

