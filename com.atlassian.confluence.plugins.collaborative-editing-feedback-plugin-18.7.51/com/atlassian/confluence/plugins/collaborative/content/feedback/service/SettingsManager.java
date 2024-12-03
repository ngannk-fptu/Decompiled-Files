/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager
 *  com.atlassian.confluence.setup.BootstrapManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.collaborative.content.feedback.service;

import com.atlassian.confluence.plugins.synchrony.config.SynchronyConfigurationManager;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import java.io.File;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SettingsManager {
    private static final String PLUGIN_SETTINGS_KEY = "com.atlassian.confluence.plugins.collaborative-editing-feedback-plugin";
    private static final String EDITOR_REPORTS_ENABLED_SETTINGS_NAME = "confluence.plugins.collab.feedback.editor.reports.enabled";
    private static final int MAX_FILES = Integer.getInteger("confluence.plugins.collab.feedback.files.max", 200);
    private static final long OPERATION_TIMEOUT = Long.getLong("confluence.plugins.collab.feedback.operation.timeout.sec", 20L);
    private static final int MAX_CONCURRENT_REQUESTS = Integer.getInteger("confluence.plugins.collab.feedback.concurrent.max", 5);
    private static final String DESTINATION_FOLDER = System.getProperty("confluence.plugins.collab.feedback.destination.folder");
    private static final int CLEAN_UP_HOURS_THRESHOLD = Integer.getInteger("confluence.plugins.collab.feedback.cleanup.threshold.hours", 120);
    private final ApplicationProperties applicationProperties;
    private final BootstrapManager bootstrapManager;
    private final PluginSettings pluginSettings;
    private final SynchronyConfigurationManager synchronyConfigurationManager;

    @Autowired
    public SettingsManager(@ComponentImport ApplicationProperties applicationProperties, @ComponentImport BootstrapManager bootstrapManager, @ComponentImport PluginSettingsFactory pluginSettingsFactory, @ComponentImport SynchronyConfigurationManager synchronyConfigurationManager) {
        this.applicationProperties = applicationProperties;
        this.bootstrapManager = bootstrapManager;
        this.pluginSettings = pluginSettingsFactory.createSettingsForKey(PLUGIN_SETTINGS_KEY);
        this.synchronyConfigurationManager = synchronyConfigurationManager;
    }

    public File getDestinationFolder() {
        File destination = DESTINATION_FOLDER != null ? new File(DESTINATION_FOLDER) : new File(this.bootstrapManager.getSharedHome(), "collab-data");
        if (!destination.exists()) {
            destination.mkdirs();
        }
        return destination;
    }

    public Properties getAppProperties() {
        Properties properties = new Properties();
        properties.setProperty("app", this.applicationProperties.getDisplayName());
        properties.setProperty("version", this.applicationProperties.getVersion());
        properties.setProperty("build", this.applicationProperties.getBuildNumber());
        properties.setProperty("buildDate", this.applicationProperties.getBuildDate().toString());
        return properties;
    }

    public int getMaxFiles() {
        return MAX_FILES;
    }

    public long getOperationTimeout() {
        return OPERATION_TIMEOUT;
    }

    public int getMaxConcurrentRequests() {
        return MAX_CONCURRENT_REQUESTS;
    }

    public boolean isEditorReportsEnabled() {
        return Boolean.parseBoolean(String.valueOf(this.pluginSettings.get(EDITOR_REPORTS_ENABLED_SETTINGS_NAME)));
    }

    public void setEditorReportsEnabled(boolean isEnabled) {
        this.pluginSettings.put(EDITOR_REPORTS_ENABLED_SETTINGS_NAME, (Object)String.valueOf(isEnabled));
    }

    public boolean collaborativeEditingEnabled() {
        return this.synchronyConfigurationManager.isSharedDraftsEnabled();
    }

    public int dataRetention() {
        return CLEAN_UP_HOURS_THRESHOLD;
    }
}

