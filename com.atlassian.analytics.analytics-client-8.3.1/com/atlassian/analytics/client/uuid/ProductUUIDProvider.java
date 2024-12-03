/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 */
package com.atlassian.analytics.client.uuid;

import com.atlassian.analytics.client.ServerIdProvider;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import java.util.UUID;

public class ProductUUIDProvider {
    static final String ANALYTICS_UUID_KEY = "com.atlassian.analytics.client.configuration.uuid";
    static final String ANALYTICS_SERVERID_KEY = "com.atlassian.analytics.client.configuration.serverid";
    private final PluginSettingsFactory pluginSettingsFactory;
    private final ServerIdProvider serverIdProvider;

    public ProductUUIDProvider(PluginSettingsFactory pluginSettingsFactory, ServerIdProvider serverIdProvider) {
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.serverIdProvider = serverIdProvider;
    }

    public String createUUID() {
        String actualServerId = this.getActualServerId();
        if (!this.serverIdMatches(actualServerId)) {
            return this.generateUUIDForServerId(actualServerId);
        }
        String savedUUID = this.getSavedUUID();
        if (null != savedUUID) {
            return savedUUID;
        }
        return this.generateUUIDForServerId(actualServerId);
    }

    public String getUUID() {
        String uuid = this.getSavedUUID();
        if (null != uuid) {
            return uuid;
        }
        return this.createUUID();
    }

    private String generateUUIDForServerId(String actualServerId) {
        String newUuid = UUID.randomUUID().toString();
        this.pluginSettingsFactory.createGlobalSettings().put(ANALYTICS_UUID_KEY, (Object)newUuid);
        this.pluginSettingsFactory.createGlobalSettings().put(ANALYTICS_SERVERID_KEY, (Object)actualServerId);
        return newUuid;
    }

    private String getActualServerId() {
        return this.serverIdProvider.getServerId();
    }

    private String getSavedServerId() {
        return (String)this.pluginSettingsFactory.createGlobalSettings().get(ANALYTICS_SERVERID_KEY);
    }

    private String getSavedUUID() {
        return (String)this.pluginSettingsFactory.createGlobalSettings().get(ANALYTICS_UUID_KEY);
    }

    private boolean serverIdMatches(String actualServerId) {
        String lastServerId = this.getSavedServerId();
        return null == actualServerId ? null == lastServerId : actualServerId.equals(lastServerId);
    }
}

