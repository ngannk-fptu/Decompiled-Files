/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.upm.impl;

import com.atlassian.marketplace.client.model.Addon;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.upm.PluginUpdateRequestStore;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.impl.NamespacedPluginSettings;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PluginSettingsPluginUpdateRequestStore
implements PluginUpdateRequestStore {
    static final String KEY_PREFIX = PluginSettingsPluginUpdateRequestStore.class.getName();
    static final String PLUGIN_UPDATE_REQUESTS = "plugin-update-requests";
    private static final Logger log = LoggerFactory.getLogger(PluginSettingsPluginUpdateRequestStore.class);
    private final PluginSettingsFactory pluginSettingsFactory;

    public PluginSettingsPluginUpdateRequestStore(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = Objects.requireNonNull(pluginSettingsFactory, "pluginSettingsFactory");
    }

    private List<String> getPluginUpdateRequests() {
        Object entries = this.getPluginSettings().get(PLUGIN_UPDATE_REQUESTS);
        if (entries == null) {
            return new ArrayList<String>();
        }
        if (!(entries instanceof List)) {
            log.error("Invalid plugin update request storage has been detected: " + entries);
            this.saveEntries(Collections.emptyList());
            return new ArrayList<String>();
        }
        return Collections.unmodifiableList((List)entries);
    }

    @Override
    public void requestPluginUpdate(Plugin installedPlugin) {
        this.requestPluginUpdate(installedPlugin.getKey());
    }

    @Override
    public void requestPluginUpdate(Addon availablePlugin) {
        this.requestPluginUpdate(availablePlugin.getKey());
    }

    private void requestPluginUpdate(String pluginKey) {
        ArrayList<String> pluginUpdateRequests = new ArrayList<String>(this.getPluginUpdateRequests());
        pluginUpdateRequests.add(pluginKey);
        this.saveEntries(pluginUpdateRequests);
    }

    @Override
    public void resetPluginUpdateRequest(Plugin plugin) {
        this.saveEntries(this.getPluginUpdateRequests().stream().filter(p -> !p.equals(plugin.getKey())).collect(Collectors.toList()));
    }

    @Override
    public boolean isPluginUpdateRequested(Plugin plugin) {
        return this.getPluginUpdateRequests().stream().anyMatch(e -> e.equals(plugin.getKey()));
    }

    private void saveEntries(Collection<String> pluginUpdateRequests) {
        this.getPluginSettings().put(PLUGIN_UPDATE_REQUESTS, new ArrayList<String>(pluginUpdateRequests));
    }

    private PluginSettings getPluginSettings() {
        return new NamespacedPluginSettings(this.pluginSettingsFactory.createGlobalSettings(), KEY_PREFIX);
    }
}

