/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginState
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 */
package com.atlassian.upm.impl;

import com.atlassian.plugin.PluginState;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.upm.SelfUpdatePluginAccessor;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.PluginRetriever;
import java.io.File;
import java.net.URI;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SelfUpdatePluginAccessorImpl
implements SelfUpdatePluginAccessor {
    private static final String SELFUPDATE_EXECUTE_UPDATE_RESOURCE_PATH = "/rest/plugins/self-update/1.0/";
    private static final String SELFUPDATE_INTERNAL_UPDATE_SUBPATH = "immediate";
    private static final String SELFUPDATE_SETTINGS_BASE = "com.atlassian.upm:selfupdate";
    private static final String SELFUPDATE_SETTINGS_JAR_PATH = "com.atlassian.upm:selfupdate.jar";
    private static final String SELFUPDATE_SETTINGS_UPM_KEY = "com.atlassian.upm:selfupdate.key";
    private static final String SELFUPDATE_SETTINGS_UPM_URI = "com.atlassian.upm:selfupdate.upm.uri";
    private static final String SELFUPDATE_SETTINGS_SELFUPDATE_PLUGIN_URI = "com.atlassian.upm:selfupdate.stub.uri";
    private static final String SELFUPDATE_SETTINGS_ENABLED_PLUGIN_LIST = "com.atlassian.upm:selfupdate.plugins.enabled";
    private static final String SELFUPDATE_SETTINGS_UPM_COMPLETION_URI = "com.atlassian.upm:selfupdate.upm.post.uri";
    private final ApplicationProperties applicationProperties;
    private final PluginRetriever pluginRetriever;
    private final PluginSettingsFactory pluginSettingsFactory;
    private final TransactionTemplate txTemplate;

    public SelfUpdatePluginAccessorImpl(ApplicationProperties applicationProperties, PluginRetriever pluginRetriever, PluginSettingsFactory pluginSettingsFactory, TransactionTemplate txTemplate) {
        this.applicationProperties = Objects.requireNonNull(applicationProperties, "applicationProperties");
        this.pluginRetriever = Objects.requireNonNull(pluginRetriever, "pluginRetriever");
        this.pluginSettingsFactory = Objects.requireNonNull(pluginSettingsFactory, "pluginSettingsFactory");
        this.txTemplate = Objects.requireNonNull(txTemplate, "txTemplate");
    }

    @Override
    public URI prepareUpdate(File jarToInstall, String expectedPluginKey, URI pluginUri, URI selfUpdatePluginUri, URI selfUpdateCompletionUri) {
        this.txTemplate.execute(() -> {
            PluginSettings settings = this.pluginSettingsFactory.createGlobalSettings();
            settings.put(SELFUPDATE_SETTINGS_JAR_PATH, (Object)jarToInstall.getAbsolutePath());
            settings.put(SELFUPDATE_SETTINGS_UPM_KEY, (Object)expectedPluginKey);
            settings.put(SELFUPDATE_SETTINGS_UPM_URI, (Object)pluginUri.toString());
            settings.put(SELFUPDATE_SETTINGS_SELFUPDATE_PLUGIN_URI, (Object)selfUpdatePluginUri.toString());
            settings.put(SELFUPDATE_SETTINGS_ENABLED_PLUGIN_LIST, (Object)String.join((CharSequence)",", this.getAllEnabledPluginKeys()));
            settings.put(SELFUPDATE_SETTINGS_UPM_COMPLETION_URI, (Object)selfUpdateCompletionUri.toString());
            return null;
        });
        return URI.create(this.applicationProperties.getBaseUrl(UrlMode.ABSOLUTE) + SELFUPDATE_EXECUTE_UPDATE_RESOURCE_PATH);
    }

    @Override
    public URI getInternalUpdateUri(URI baseUpdateUri) {
        return URI.create(baseUpdateUri.toString() + SELFUPDATE_INTERNAL_UPDATE_SUBPATH);
    }

    private Iterable<String> getAllEnabledPluginKeys() {
        return StreamSupport.stream(this.pluginRetriever.getPlugins().spliterator(), false).filter(plugin -> plugin.getPluginState() == PluginState.ENABLED).map(Plugin::getKey).collect(Collectors.toList());
    }
}

