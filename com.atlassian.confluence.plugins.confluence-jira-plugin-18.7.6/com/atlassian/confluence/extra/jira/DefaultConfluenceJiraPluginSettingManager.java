/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  javax.annotation.Nonnull
 */
package com.atlassian.confluence.extra.jira;

import com.atlassian.confluence.extra.jira.api.services.ConfluenceJiraPluginSettingManager;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import java.util.Optional;
import javax.annotation.Nonnull;

public final class DefaultConfluenceJiraPluginSettingManager
implements ConfluenceJiraPluginSettingManager {
    private static final String TIME_OF_CACHE_SETTING_IN_MINUTES = "com.atlassian.confluence.extra.jira.admin.cachesetting";
    private PluginSettings settings;
    private final PluginSettingsFactory pluginSettingsFactory;

    public DefaultConfluenceJiraPluginSettingManager(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    @Override
    public void setCacheTimeoutInMinutes(@Nonnull Optional<Integer> minutes) {
        if (minutes.isPresent()) {
            this.getSettings().put(TIME_OF_CACHE_SETTING_IN_MINUTES, (Object)minutes.get().toString());
        } else {
            this.getSettings().put(TIME_OF_CACHE_SETTING_IN_MINUTES, null);
        }
    }

    @Override
    @Nonnull
    public Optional<Integer> getCacheTimeoutInMinutes() {
        String minutesString = (String)this.getSettings().get(TIME_OF_CACHE_SETTING_IN_MINUTES);
        if (minutesString == null) {
            return Optional.empty();
        }
        return Optional.of(Integer.valueOf(minutesString));
    }

    private PluginSettings getSettings() {
        if (this.settings == null) {
            this.settings = this.pluginSettingsFactory.createGlobalSettings();
        }
        return this.settings;
    }
}

