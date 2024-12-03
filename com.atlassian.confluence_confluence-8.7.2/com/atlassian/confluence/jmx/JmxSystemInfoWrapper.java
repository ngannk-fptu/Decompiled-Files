/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.Plugin
 */
package com.atlassian.confluence.jmx;

import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.confluence.status.service.systeminfo.ConfluenceInfo;
import com.atlassian.confluence.status.service.systeminfo.DatabaseInfo;
import com.atlassian.plugin.Plugin;
import java.util.ArrayList;
import java.util.List;

public class JmxSystemInfoWrapper {
    private SystemInformationService systemInformationService;

    public JmxSystemInfoWrapper(SystemInformationService systemInformationService) {
        this.systemInformationService = systemInformationService;
    }

    public String getDatabaseUrl() {
        return this.getDatabaseInfo().getUrl();
    }

    public String getDatabaseDialect() {
        return this.getDatabaseInfo().getDialect();
    }

    public String getDatabaseIsolationLevel() {
        return this.getDatabaseInfo().getIsolationLevel();
    }

    public String getDatabaseDriverName() {
        return this.getDatabaseInfo().getDriverName();
    }

    public String getDatabaseDriverVersion() {
        return this.getDatabaseInfo().getDriverVersion();
    }

    public String getDatabaseVersion() {
        return this.getDatabaseInfo().getVersion();
    }

    public String getDatabaseName() {
        return this.getDatabaseInfo().getName();
    }

    public Long getDatabaseExampleLatency() {
        return this.getDatabaseInfo().getExampleLatency();
    }

    private DatabaseInfo getDatabaseInfo() {
        return this.systemInformationService.getDatabaseInfo();
    }

    public String getConfluenceHome() {
        return this.getConfluenceInfo().getHome();
    }

    public String getConfluenceBuildNumber() {
        return this.getConfluenceInfo().getBuildNumber();
    }

    public String getConfluenceVersion() {
        return this.getConfluenceInfo().getVersion();
    }

    public long getStartTime() {
        return this.getConfluenceInfo().getStartTime();
    }

    public String getConfluenceUpTime() {
        return this.getConfluenceInfo().getUpTime();
    }

    public List getConfluenceEnabledPluginsNames() {
        List<Plugin> plugins = this.getConfluenceInfo().getEnabledPlugins();
        ArrayList<String> pluginNames = new ArrayList<String>();
        for (Plugin plugin : plugins) {
            pluginNames.add(plugin.getName());
        }
        return pluginNames;
    }

    private ConfluenceInfo getConfluenceInfo() {
        return this.systemInformationService.getConfluenceInfo();
    }
}

