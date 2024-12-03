/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.google.common.base.Strings
 *  javax.annotation.PostConstruct
 */
package com.atlassian.migration.agent.service;

import com.atlassian.migration.agent.json.Jsons;
import com.atlassian.migration.agent.service.impl.MigrationSettingsType;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.google.common.base.Strings;
import java.util.function.Supplier;
import javax.annotation.PostConstruct;

public abstract class DefaultTypeSettings {
    private static final String PLUGIN_KEY = "com.atlassian.migration.agent";
    private final Supplier<PluginSettings> pluginSettingsSupplier = () -> ((PluginSettingsFactory)pluginSettingsFactory).createGlobalSettings();
    private final MigrationSettingsType migrationSettingsType;
    private String settingsTypeKey;

    protected DefaultTypeSettings(PluginSettingsFactory pluginSettingsFactory, MigrationSettingsType migrationSettingsType) {
        this.migrationSettingsType = migrationSettingsType;
    }

    @PostConstruct
    public void initialize() {
        this.settingsTypeKey = String.format("%s:%s", new Object[]{PLUGIN_KEY, this.migrationSettingsType});
        this.putSettings(this.getSettings());
    }

    public Object getSettings() {
        String settingsTypeValue = (String)this.pluginSettingsSupplier.get().get(this.settingsTypeKey);
        if (Strings.isNullOrEmpty((String)settingsTypeValue)) {
            return this.getDefaultPluginSettings();
        }
        return this.mapStringToObject(settingsTypeValue);
    }

    public boolean putSettings(Object settingsTypeValue) {
        if (this.isValidSettings(settingsTypeValue)) {
            if (this.settingsUnchanged(settingsTypeValue)) {
                return false;
            }
            this.pluginSettingsSupplier.get().put(this.settingsTypeKey, (Object)Jsons.valueAsString(settingsTypeValue));
            return true;
        }
        throw new IllegalArgumentException("Invalid settings for " + (Object)((Object)this.migrationSettingsType));
    }

    private boolean settingsUnchanged(Object settingsTypeValue) {
        return this.pluginSettingsSupplier.get().equals(settingsTypeValue);
    }

    protected abstract boolean isValidSettings(Object var1);

    public Object mapStringToObject(String settingsTypeValue) {
        return settingsTypeValue;
    }

    protected abstract Object getDefaultPluginSettings();
}

