/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.license.LicenseHandler
 */
package com.atlassian.upm.impl;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.upm.UpmHostApplicationInformation;
import com.atlassian.upm.core.impl.VersionAwareHostApplicationInformationImpl;
import com.atlassian.upm.license.internal.HostLicenseProvider;
import com.atlassian.upm.osgi.PackageAccessor;
import com.atlassian.upm.osgi.Version;
import com.atlassian.upm.osgi.impl.Versions;
import com.atlassian.upm.test.rest.resources.UpmSysResource;
import java.util.Iterator;
import java.util.Objects;

public class UpmHostApplicationInformationImpl
extends VersionAwareHostApplicationInformationImpl
implements UpmHostApplicationInformation {
    private static final String AUI_PLUGIN_KEY = "com.atlassian.auiplugin";
    private static final Version AUI_DIALOG2_MIN_VERSION = Versions.fromString("5.3");
    private final HostLicenseProvider hostLicenseProvider;
    private final Version auiVersion;

    public UpmHostApplicationInformationImpl(HostLicenseProvider hostLicenseProvider, ApplicationProperties applicationProperties, LicenseHandler licenseHandler, PackageAccessor packageAccessor, PluginAccessor pluginAccessor) {
        super(applicationProperties, licenseHandler, packageAccessor);
        this.hostLicenseProvider = Objects.requireNonNull(hostLicenseProvider, "hostLicenseProvider");
        Plugin auiPlugin = pluginAccessor.getPlugin(AUI_PLUGIN_KEY);
        this.auiVersion = auiPlugin == null ? Versions.fromString("0") : Versions.fromPlugin(auiPlugin);
    }

    @Override
    public boolean isHostDataCenterEnabled() {
        Iterator<Boolean> iterator = UpmSysResource.isDataCenterEnabled().iterator();
        if (iterator.hasNext()) {
            Boolean dcEnabled = iterator.next();
            return dcEnabled;
        }
        return this.hostLicenseProvider.getHostApplicationLicenseAttributes().isDataCenter();
    }

    @Override
    public UpmHostApplicationInformation.AuiCapabilities getAuiCapabilities() {
        boolean dialog2 = this.auiVersion.compareTo(AUI_DIALOG2_MIN_VERSION) >= 0;
        return new UpmHostApplicationInformation.AuiCapabilities(dialog2);
    }
}

