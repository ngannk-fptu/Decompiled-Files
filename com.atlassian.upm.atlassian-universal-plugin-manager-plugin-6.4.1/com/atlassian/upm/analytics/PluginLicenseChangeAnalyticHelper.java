/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.analytics;

import com.atlassian.upm.UpmHostApplicationInformation;
import com.atlassian.upm.api.license.DataCenterCrossgradeablePlugins;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.analytics.event.DefaultAnalyticsEvent;
import com.atlassian.upm.core.analytics.impl.DefaultAnalyticsLogger;
import com.atlassian.upm.license.internal.PluginLicenseRepository;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PluginLicenseChangeAnalyticHelper {
    private final UpmHostApplicationInformation appInfo;
    private final DataCenterCrossgradeablePlugins dataCenterCrossgradeablePlugins;
    private final DefaultAnalyticsLogger analyticsLogger;
    private final PluginLicenseRepository licenseRepository;

    public PluginLicenseChangeAnalyticHelper(UpmHostApplicationInformation appInfo, DataCenterCrossgradeablePlugins dataCenterCrossgradeablePlugins, DefaultAnalyticsLogger analyticsLogger, PluginLicenseRepository licenseRepository) {
        this.appInfo = appInfo;
        this.dataCenterCrossgradeablePlugins = dataCenterCrossgradeablePlugins;
        this.analyticsLogger = analyticsLogger;
        this.licenseRepository = licenseRepository;
    }

    public void logPluginLicenseChanged(String pluginKey) {
        if (this.appInfo.isHostDataCenterEnabled()) {
            HashMap<String, String> tmp = new HashMap<String, String>();
            tmp.put("appCrossgradeCount", String.valueOf(this.getInvalidPluginCount()));
            tmp.put("pluginKey", pluginKey);
            Map<String, String> data = Collections.unmodifiableMap(tmp);
            this.analyticsLogger.log(new DefaultAnalyticsEvent("manage-apps-plugin-license-updated", data));
        }
    }

    private long getInvalidPluginCount() {
        return this.dataCenterCrossgradeablePlugins.getDataCenterLicenseCrossgradeablePlugins().stream().filter(this::isServerLicensed).count();
    }

    private boolean isServerLicensed(DataCenterCrossgradeablePlugins.CrossgradePluginData plugin) {
        Option<PluginLicense> license = this.licenseRepository.getPluginLicense(plugin.getKey());
        return license.isDefined() && !license.get().isDataCenter();
    }
}

