/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 */
package com.atlassian.upm.license.internal.impl;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.upm.impl.LongKeyHasher;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LicensePluginSettings
implements PluginSettings {
    private static final String KEY_PREFIX = "com.atlassian.upm.license.internal.impl.PluginSettingsPluginLicenseRepository:licenses:";
    private static final String STORED_LICENSES = "all_stored_licenses";
    private final PluginSettings pluginSettings;

    public LicensePluginSettings(PluginSettings pluginSettings) {
        this.pluginSettings = Objects.requireNonNull(pluginSettings, "pluginSettings");
    }

    public String get(String pluginKey) {
        return (String)this.pluginSettings.get(LongKeyHasher.hashKeyIfTooLong(KEY_PREFIX + pluginKey));
    }

    void addStoredLicense(String pluginKey) {
        List<String> storedLicenses = this.getStoredLicenses();
        if (!storedLicenses.contains(pluginKey)) {
            List updatedStoredLicenses = Stream.concat(this.getStoredLicenses().stream(), Stream.of(pluginKey)).collect(Collectors.toList());
            this.pluginSettings.put(LongKeyHasher.hashKeyIfTooLong("com.atlassian.upm.license.internal.impl.PluginSettingsPluginLicenseRepository:licenses:all_stored_licenses"), updatedStoredLicenses);
        }
    }

    void removeStoredLicense(String pluginKey) {
        ArrayList<String> storedLicenses = new ArrayList<String>(this.getStoredLicenses());
        if (storedLicenses.contains(pluginKey)) {
            storedLicenses.removeIf(storedLicense -> storedLicense.equals(pluginKey));
            this.pluginSettings.put(LongKeyHasher.hashKeyIfTooLong("com.atlassian.upm.license.internal.impl.PluginSettingsPluginLicenseRepository:licenses:all_stored_licenses"), storedLicenses);
        }
    }

    public List<String> getStoredLicenses() {
        Object licenses = this.pluginSettings.get(LongKeyHasher.hashKeyIfTooLong("com.atlassian.upm.license.internal.impl.PluginSettingsPluginLicenseRepository:licenses:all_stored_licenses"));
        return licenses == null ? Collections.emptyList() : (List)licenses;
    }

    public String put(String pluginKey, Object licenseString) {
        return (String)this.pluginSettings.put(LongKeyHasher.hashKeyIfTooLong(KEY_PREFIX + pluginKey), licenseString);
    }

    public String remove(String pluginKey) {
        return (String)this.pluginSettings.remove(LongKeyHasher.hashKeyIfTooLong(KEY_PREFIX + pluginKey));
    }
}

