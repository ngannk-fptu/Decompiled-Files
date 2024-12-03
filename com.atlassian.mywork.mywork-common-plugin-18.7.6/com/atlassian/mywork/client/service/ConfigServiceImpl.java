/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.mywork.client.service;

import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.mywork.client.service.ConfigService;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigServiceImpl
implements ConfigService {
    private static final Logger log = LoggerFactory.getLogger(ConfigServiceImpl.class);
    private static final String SETTING_HOST = "com.atlassian.mywork.host";
    private final PluginSettings pluginSettings;
    private final ApplicationLinkService applicationLinkService;

    public ConfigServiceImpl(PluginSettingsFactory pluginSettings, ApplicationLinkService applicationLinkService) {
        this.applicationLinkService = applicationLinkService;
        this.pluginSettings = pluginSettings.createGlobalSettings();
    }

    public String get(String name, String default_) {
        String hostSetting = (String)this.pluginSettings.get(name);
        return hostSetting != null ? hostSetting : System.getProperty(name, default_);
    }

    public void set(String name, String value) {
        this.pluginSettings.put(name, (Object)value);
    }

    public void remove(String name) {
        this.pluginSettings.remove(name);
    }

    @Override
    public String getHost() {
        return this.get(SETTING_HOST, null);
    }

    @Override
    public void setHost(String host) {
        this.set(SETTING_HOST, host);
    }
}

