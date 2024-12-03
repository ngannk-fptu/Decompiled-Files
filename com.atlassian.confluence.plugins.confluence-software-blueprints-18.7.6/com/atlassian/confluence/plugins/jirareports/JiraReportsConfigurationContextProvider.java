/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.settings.Settings
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.ContextProvider
 */
package com.atlassian.confluence.plugins.jirareports;

import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import java.util.Map;

public class JiraReportsConfigurationContextProvider
implements ContextProvider {
    private SettingsManager settingsManager;

    public void init(Map<String, String> paramMap) throws PluginParseException {
    }

    public Map<String, Object> getContextMap(Map<String, Object> contextMap) {
        int connectionTimeout = 10000;
        Settings globalSettings = this.settingsManager.getGlobalSettings();
        if (globalSettings != null && globalSettings.getConfluenceHttpParameters() != null) {
            connectionTimeout = globalSettings.getConfluenceHttpParameters().getConnectionTimeout();
        }
        contextMap.put("connectionTimeout", connectionTimeout);
        return contextMap;
    }

    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }
}

