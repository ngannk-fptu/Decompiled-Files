/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 */
package com.atlassian.upm.core.impl;

import com.atlassian.plugin.Plugin;
import com.atlassian.upm.PluginInfoUtils;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.ApplicationPluginsManager;
import com.atlassian.upm.core.HostingType;
import com.atlassian.upm.core.LicensingUsageVerifier;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.core.VersionAwareHostApplicationInformation;
import com.atlassian.upm.license.LicensedPlugins;

public class LicensingUsageVerifierImpl
implements LicensingUsageVerifier {
    private final VersionAwareHostApplicationInformation versionAwareHostApplicationInformation;
    private final PluginRetriever pluginRetriever;
    private final ApplicationPluginsManager applicationPluginsManager;

    public LicensingUsageVerifierImpl(VersionAwareHostApplicationInformation versionAwareHostApplicationInformation, PluginRetriever pluginRetriever, ApplicationPluginsManager applicationPluginsManager) {
        this.versionAwareHostApplicationInformation = versionAwareHostApplicationInformation;
        this.pluginRetriever = pluginRetriever;
        this.applicationPluginsManager = applicationPluginsManager;
    }

    @Override
    public boolean usesLicensing(Plugin plugin) {
        if (this.applicationPluginsManager.getApplicationRelatedPluginKeys().contains(plugin.getKey())) {
            return false;
        }
        if (this.isTCOnDataCenter(plugin) && !LicensedPlugins.hasLicensingEnabledParam(plugin)) {
            return false;
        }
        return LicensedPlugins.isLegacyLicensePlugin(plugin.getKey()) || LicensedPlugins.hasLicensingEnabledParam(plugin) || this.isCarebearLicensedPlugin(plugin) || this.isParentPluginNotInstalled(plugin);
    }

    @Override
    public boolean isCarebearSpecificPlugin(Plugin plugin) {
        return PluginInfoUtils.getBooleanPluginInfoParam(plugin.getPluginInformation(), "server-licensing-enabled");
    }

    protected boolean isTCOnDataCenter(Plugin plugin) {
        return HostingType.DATA_CENTER.equals((Object)this.versionAwareHostApplicationInformation.getHostingType()) && "com.atlassian.confluence.extra.team-calendars".equals(plugin.getKey());
    }

    protected boolean isCarebearLicensedPlugin(Plugin plugin) {
        return HostingType.SERVER.equals((Object)this.versionAwareHostApplicationInformation.getHostingType()) && this.isCarebearSpecificPlugin(plugin);
    }

    protected boolean isParentPluginNotInstalled(Plugin plugin) {
        if (this.versionAwareHostApplicationInformation.isJiraPostCarebear() && HostingType.DATA_CENTER.equals((Object)this.versionAwareHostApplicationInformation.getHostingType())) {
            Option<String> licensedWith = PluginInfoUtils.getStringPluginInfoParam(plugin.getPluginInformation(), "carebear-licensed-with");
            return licensedWith.isDefined() && !this.pluginRetriever.getPlugin(licensedWith.get()).isDefined();
        }
        return false;
    }
}

