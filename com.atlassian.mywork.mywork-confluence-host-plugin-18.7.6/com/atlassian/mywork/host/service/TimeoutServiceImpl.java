/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mywork.service.TimeoutService
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.mywork.host.service;

import com.atlassian.mywork.service.TimeoutService;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.springframework.stereotype.Component;

@ExportAsService(value={TimeoutService.class})
@Component
public class TimeoutServiceImpl
implements TimeoutService {
    private static final String SETTING_TIMEOUT = "com.atlassian.mywork.timeout";
    private static final String SETTING_MAX_TIMEOUT = "com.atlassian.mywork.max_timeout";
    private final PluginSettings pluginSettings;
    private volatile int timeout = 30;
    private volatile int maxTimeout;

    public TimeoutServiceImpl(PluginSettingsFactory pluginSettings) {
        this.pluginSettings = pluginSettings.createGlobalSettings();
        this.timeout = Integer.getInteger("mywork.timeout", 30);
        this.maxTimeout = Integer.getInteger("mywork.max_timeout", 300);
    }

    public int getTimeout() {
        String t = (String)this.pluginSettings.get(SETTING_TIMEOUT);
        return t != null ? Integer.parseInt(t) : this.timeout;
    }

    public void setTimeout(int timeout) {
        this.pluginSettings.put(SETTING_TIMEOUT, (Object)Integer.toString(timeout));
        this.timeout = timeout;
    }

    public int getMaxTimeout() {
        String t = (String)this.pluginSettings.get(SETTING_MAX_TIMEOUT);
        return t != null ? Integer.parseInt(t) : this.maxTimeout;
    }

    public void setMaxTimeout(int maxTimeout) {
        this.pluginSettings.put(SETTING_MAX_TIMEOUT, (Object)Integer.toString(maxTimeout));
        this.maxTimeout = maxTimeout;
    }
}

