/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.upm.license.internal.impl;

import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.license.internal.PluginLicenseStore;
import com.atlassian.upm.license.internal.impl.LicensePluginSettings;
import java.util.List;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public class PluginSettingsPluginLicenseStore
implements PluginLicenseStore {
    private final PluginSettingsFactory pluginSettingsFactory;
    private final TransactionTemplate txTemplate;

    public PluginSettingsPluginLicenseStore(PluginSettingsFactory pluginSettingsFactory, TransactionTemplate txTemplate) {
        this.pluginSettingsFactory = Objects.requireNonNull(pluginSettingsFactory, "pluginSettingsFactory");
        this.txTemplate = Objects.requireNonNull(txTemplate, "txTemplate");
    }

    @Override
    public Option<String> getPluginLicense(String pluginKey) {
        return (Option)this.txTemplate.execute(() -> Option.option(this.getPluginSettings().get(pluginKey)));
    }

    @Override
    public List<String> getPluginLicenses() {
        return (List)this.txTemplate.execute(() -> this.getPluginSettings().getStoredLicenses());
    }

    @Override
    public Option<String> setPluginLicense(String pluginKey, String licenseString) {
        if (StringUtils.isBlank((CharSequence)licenseString)) {
            throw new IllegalArgumentException("Cannot set empty license. Try removing it instead.");
        }
        return (Option)this.txTemplate.execute(() -> {
            LicensePluginSettings pluginSettings = this.getPluginSettings();
            String previousLicense = pluginSettings.put(pluginKey, licenseString.trim());
            pluginSettings.addStoredLicense(pluginKey);
            return Option.option(previousLicense);
        });
    }

    @Override
    public Option<String> removePluginLicense(String pluginKey) {
        return (Option)this.txTemplate.execute(() -> {
            LicensePluginSettings pluginSettings = this.getPluginSettings();
            String previousLicense = pluginSettings.remove(pluginKey);
            pluginSettings.removeStoredLicense(pluginKey);
            return Option.option(previousLicense);
        });
    }

    private LicensePluginSettings getPluginSettings() {
        return new LicensePluginSettings(this.pluginSettingsFactory.createGlobalSettings());
    }
}

