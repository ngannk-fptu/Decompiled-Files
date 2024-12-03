/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core.analytics;

import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.analytics.SenFinder;
import com.atlassian.upm.license.internal.PluginLicenseRepository;

public class SenFinderImpl
implements SenFinder {
    private final PluginLicenseRepository pluginLicenseRepository;

    public SenFinderImpl(PluginLicenseRepository pluginLicenseRepository) {
        this.pluginLicenseRepository = pluginLicenseRepository;
    }

    @Override
    public Option<String> findSen(Plugin plugin) {
        return this.findSen(plugin.getKey());
    }

    @Override
    public Option<String> findSen(String key) {
        return this.pluginLicenseRepository.getPluginLicense(key).flatMap(PluginLicense::getSupportEntitlementNumber).filter(s -> !s.isEmpty());
    }
}

