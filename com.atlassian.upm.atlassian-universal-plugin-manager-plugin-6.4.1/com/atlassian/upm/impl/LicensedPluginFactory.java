/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.sal.api.message.I18nResolver
 */
package com.atlassian.upm.impl;

import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.upm.PluginControlHandlerRegistry;
import com.atlassian.upm.api.license.entity.PluginLicense;
import com.atlassian.upm.core.PluginMetadataAccessor;
import com.atlassian.upm.core.impl.DefaultPluginFactory;
import com.atlassian.upm.license.internal.PluginLicenseRepository;
import java.util.Iterator;
import java.util.Objects;

public class LicensedPluginFactory
extends DefaultPluginFactory {
    private final PluginLicenseRepository licenseRepository;

    public LicensedPluginFactory(I18nResolver i18nResolver, PluginAccessor accessor, PluginMetadataAccessor metadata, PluginLicenseRepository licenseRepository, PluginControlHandlerRegistry pluginControlHandlerRegistry) {
        super(i18nResolver, accessor, metadata, pluginControlHandlerRegistry);
        this.licenseRepository = Objects.requireNonNull(licenseRepository, "licenseRepository");
    }

    @Override
    protected boolean isUninstallPreventedByAdditionalCriteria(Plugin plugin, boolean isConnect) {
        Iterator<PluginLicense> iterator;
        if (isConnect && (iterator = this.licenseRepository.getPluginLicense(plugin.getKey()).iterator()).hasNext()) {
            PluginLicense license = iterator.next();
            return license.isAutoRenewal();
        }
        return false;
    }
}

