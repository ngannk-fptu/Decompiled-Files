/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 */
package com.atlassian.confluence.plugins.dailysummary.actions;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import java.util.Arrays;
import java.util.List;

public class SummaryEmailAdminAction
extends ConfluenceActionSupport {
    private String defaultSchedule;
    private boolean defaultEnabled;
    private PluginSettingsFactory pluginSettingsFactory;

    public void setPluginSettingsFactory(@ComponentImport PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    public String execute() throws Exception {
        PluginSettings settings = this.pluginSettingsFactory.createGlobalSettings();
        this.defaultSchedule = (String)settings.get("atl.confluence.plugins.confluence-daily-summary-email:admin.defaultSchedule");
        String enabledStr = (String)settings.get("atl.confluence.plugins.confluence-daily-summary-email:admin.defaultEnabled");
        this.defaultEnabled = enabledStr == null ? Boolean.TRUE : Boolean.parseBoolean(enabledStr);
        return super.execute();
    }

    public String getDefaultSchedule() {
        return this.defaultSchedule;
    }

    public List<String> getSchedules() {
        return Arrays.asList("weekly", "daily");
    }

    public boolean getDefaultEnabled() {
        return this.defaultEnabled;
    }
}

