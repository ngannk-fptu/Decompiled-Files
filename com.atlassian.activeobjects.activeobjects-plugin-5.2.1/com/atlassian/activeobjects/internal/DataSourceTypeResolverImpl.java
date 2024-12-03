/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.activeobjects.internal;

import com.atlassian.activeobjects.internal.ActiveObjectsSettingKeys;
import com.atlassian.activeobjects.internal.DataSourceType;
import com.atlassian.activeobjects.internal.DataSourceTypeResolver;
import com.atlassian.activeobjects.internal.Prefix;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DataSourceTypeResolverImpl
implements DataSourceTypeResolver {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final PluginSettings pluginSettings;
    private final DataSourceType defaultDataSourceType;
    private final ActiveObjectsSettingKeys settingKeys;

    public DataSourceTypeResolverImpl(PluginSettingsFactory pluginSettingsFactory, ActiveObjectsSettingKeys settingKeys, DataSourceType defaultDataSourceType) {
        Preconditions.checkNotNull((Object)pluginSettingsFactory);
        this.pluginSettings = (PluginSettings)Preconditions.checkNotNull((Object)pluginSettingsFactory.createGlobalSettings());
        this.settingKeys = (ActiveObjectsSettingKeys)Preconditions.checkNotNull((Object)settingKeys);
        this.defaultDataSourceType = (DataSourceType)((Object)Preconditions.checkNotNull((Object)((Object)defaultDataSourceType)));
    }

    @Override
    public DataSourceType getDataSourceType(Prefix prefix) {
        String setting = this.getSetting(prefix);
        if (setting != null) {
            try {
                return DataSourceType.valueOf(setting);
            }
            catch (IllegalArgumentException e) {
                this.logger.warn("Active objects data source type setting <" + setting + "> for key <" + this.getSettingKey(prefix) + "> could not be resolved to a valid " + DataSourceType.class.getName() + ". Using default value <" + (Object)((Object)this.defaultDataSourceType) + ">.");
                return this.defaultDataSourceType;
            }
        }
        return this.defaultDataSourceType;
    }

    private String getSetting(Prefix prefix) {
        return (String)this.pluginSettings.get(this.getSettingKey(prefix));
    }

    private String getSettingKey(Prefix prefix) {
        return this.settingKeys.getDataSourceTypeKey(prefix);
    }
}

