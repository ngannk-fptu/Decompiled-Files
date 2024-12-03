/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.license;

import com.atlassian.upm.PluginInfoUtils;
import com.atlassian.upm.api.license.DataCenterCrossgradeablePlugins;
import com.atlassian.upm.core.LicensingUsageVerifier;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginRetriever;
import com.atlassian.upm.license.LicensedPlugins;
import com.atlassian.upm.license.internal.PluginLicenseRepository;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class DataCenterCrossgradeablePluginsImpl
implements DataCenterCrossgradeablePlugins {
    private final PluginRetriever pluginRetriever;
    private final PluginLicenseRepository pluginLicenseRepository;
    private final LicensingUsageVerifier licensingUsageVerifier;

    public DataCenterCrossgradeablePluginsImpl(PluginRetriever pluginRetriever, PluginLicenseRepository pluginLicenseRepository, LicensingUsageVerifier licensingUsageVerifier) {
        this.pluginRetriever = Objects.requireNonNull(pluginRetriever, "pluginAccessor");
        this.licensingUsageVerifier = Objects.requireNonNull(licensingUsageVerifier, "licensingUsageVerifier");
        this.pluginLicenseRepository = pluginLicenseRepository;
    }

    @Override
    public List<DataCenterCrossgradeablePlugins.CrossgradePluginData> getDataCenterLicenseCrossgradeablePlugins() {
        this.pluginLicenseRepository.invalidateCache();
        return StreamSupport.stream(this.pluginRetriever.getPlugins().spliterator(), false).filter(this::crossgradeNeeded).map(p -> new DataCenterCrossgradeablePlugins.CrossgradePluginData(p.getKey(), p.getName())).collect(Collectors.toList());
    }

    private boolean crossgradeNeeded(Plugin plugin) {
        return PluginInfoUtils.isStatusDataCenterCompatibleAccordingToPluginDescriptor(plugin.getPluginInformation()) && plugin.isUserInstalled() && LicensedPlugins.usesLicensing(plugin.getPlugin(), this.licensingUsageVerifier) && !this.licensingUsageVerifier.isCarebearSpecificPlugin(plugin.getPlugin()) && plugin.isEnabled() && (Boolean)this.pluginLicenseRepository.getPluginLicense(plugin.getKey()).map(l -> !l.isDataCenter()).getOrElse(true) != false;
    }
}

