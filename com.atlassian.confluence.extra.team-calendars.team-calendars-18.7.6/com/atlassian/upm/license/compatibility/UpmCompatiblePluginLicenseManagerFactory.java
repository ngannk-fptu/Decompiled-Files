/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.upm.api.license.PluginLicenseManager
 *  com.google.common.base.Preconditions
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 */
package com.atlassian.upm.license.compatibility;

import com.atlassian.upm.api.license.PluginLicenseManager;
import com.atlassian.upm.license.compatibility.CompatiblePluginLicenseManager;
import com.atlassian.upm.license.compatibility.OptionalService;
import com.atlassian.upm.license.compatibility.UpmCompatiblePluginLicenseManager;
import com.google.common.base.Preconditions;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class UpmCompatiblePluginLicenseManagerFactory
extends OptionalService<PluginLicenseManager> {
    private final CompatiblePluginLicenseManager defaultPluginLicenseManager;

    public UpmCompatiblePluginLicenseManagerFactory(CompatiblePluginLicenseManager defaultPluginLicenseManager, BundleContext bundleContext) {
        super(PluginLicenseManager.class, bundleContext);
        this.defaultPluginLicenseManager = (CompatiblePluginLicenseManager)Preconditions.checkNotNull((Object)defaultPluginLicenseManager, (Object)"defaultPluginLicenseManager");
    }

    public UpmCompatiblePluginLicenseManager get() {
        PluginLicenseManager licenseManager = (PluginLicenseManager)this.getService().get();
        String pluginKey = this.getPluginKey(this.getBundleContext().getBundle());
        return new UpmCompatiblePluginLicenseManager(this.defaultPluginLicenseManager, licenseManager, pluginKey);
    }

    private String getPluginKey(Bundle bundle) {
        String pluginKey = (String)bundle.getHeaders().get("Atlassian-Plugin-Key");
        Preconditions.checkState((pluginKey != null ? 1 : 0) != 0);
        return pluginKey;
    }
}

