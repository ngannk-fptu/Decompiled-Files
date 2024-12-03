/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.events.AnalyticsConfigChangedEvent
 *  com.atlassian.analytics.api.events.AnalyticsConfigChangedEvent$Key
 *  com.atlassian.analytics.api.services.AnalyticsConfigService
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheLoader
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.google.common.base.Preconditions
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.analytics.client.configuration;

import com.atlassian.analytics.api.events.AnalyticsConfigChangedEvent;
import com.atlassian.analytics.api.services.AnalyticsConfigService;
import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheLoader;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.google.common.base.Preconditions;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AnalyticsConfig
implements AnalyticsConfigService {
    private static final String CACHE_KEY = "com.atlassian.analytics.client.configuration.settings-cache";
    private static final Logger LOG = LoggerFactory.getLogger(AnalyticsConfig.class);
    private final PluginSettingsFactory pluginSettingsFactory;
    private final EventPublisher eventPublisher;
    private final Cache<String, Boolean> settingsCache;

    public AnalyticsConfig(PluginSettingsFactory pluginSettingsFactory, EventPublisher eventPublisher, CacheManager cacheManager) {
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.eventPublisher = eventPublisher;
        this.settingsCache = this.getSettingsCache(cacheManager);
    }

    private Cache<String, Boolean> getSettingsCache(CacheManager cacheManager) {
        CacheLoader cacheLoader = key -> this.getBooleanSetting(AnalyticsConfigChangedEvent.Key.valueOf((String)key));
        CacheSettings cacheSettings = new CacheSettingsBuilder().remote().replicateAsynchronously().replicateViaInvalidation().build();
        return cacheManager.getCache(CACHE_KEY, cacheLoader, cacheSettings);
    }

    public String getDestination() {
        return this.getSetting(AnalyticsConfigChangedEvent.Key.DESTINATION);
    }

    public String getDestinationOrDefault(String defaultDestination) {
        String destination = this.getDestination();
        return StringUtils.isNotEmpty((CharSequence)destination) ? destination : defaultDestination;
    }

    public void setDestination(String destination) {
        Preconditions.checkNotNull((Object)destination);
        this.updateSetting(AnalyticsConfigChangedEvent.Key.DESTINATION, destination);
    }

    public boolean isPolicyUpdateAcknowledged() {
        return (Boolean)this.settingsCache.get((Object)AnalyticsConfigChangedEvent.Key.POLICY_ACKNOWLEDGED.name());
    }

    public void setPolicyUpdateAcknowledged(boolean acknowledged) {
        boolean updateSuccessful = this.updateSetting(AnalyticsConfigChangedEvent.Key.POLICY_ACKNOWLEDGED, acknowledged);
        if (updateSuccessful) {
            this.settingsCache.put((Object)AnalyticsConfigChangedEvent.Key.POLICY_ACKNOWLEDGED.name(), (Object)acknowledged);
        }
    }

    public void setDefaultAnalyticsEnabled() {
        String analyticsEnabled = this.getSetting(AnalyticsConfigChangedEvent.Key.ANALYTICS_ENABLED);
        if (StringUtils.isEmpty((CharSequence)analyticsEnabled)) {
            this.setAnalyticsEnabled(true, "");
        }
    }

    public boolean isAnalyticsEnabled() {
        return (Boolean)this.settingsCache.get((Object)AnalyticsConfigChangedEvent.Key.ANALYTICS_ENABLED.name());
    }

    public void setAnalyticsEnabled(boolean analyticsEnabled, String userName) {
        boolean updateSuccessful = this.updateSetting(AnalyticsConfigChangedEvent.Key.ANALYTICS_ENABLED, analyticsEnabled);
        if (updateSuccessful) {
            this.settingsCache.put((Object)AnalyticsConfigChangedEvent.Key.ANALYTICS_ENABLED.name(), (Object)analyticsEnabled);
        }
        if (!analyticsEnabled) {
            String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            this.updateSetting(AnalyticsConfigChangedEvent.Key.ANALYTICS_DISABLED_USERNAME, userName, false);
            this.updateSetting(AnalyticsConfigChangedEvent.Key.ANALYTICS_DISABLED_DATE, currentDateTime, false);
        }
    }

    private boolean updateSetting(AnalyticsConfigChangedEvent.Key key, boolean newValue) {
        return this.updateSetting(key, Boolean.toString(newValue), true);
    }

    private boolean updateSetting(AnalyticsConfigChangedEvent.Key key, String newValue) {
        return this.updateSetting(key, newValue, true);
    }

    private boolean updateSetting(AnalyticsConfigChangedEvent.Key key, String newValue, boolean fireEvent) {
        String oldValue = this.getSetting(key);
        boolean putSuccessful = this.putSetting(key, newValue);
        if (fireEvent && putSuccessful) {
            this.eventPublisher.publish((Object)new AnalyticsConfigChangedEvent(key, oldValue, newValue));
        }
        return putSuccessful;
    }

    private boolean putSetting(AnalyticsConfigChangedEvent.Key key, String newValue) {
        try {
            this.pluginSettingsFactory.createGlobalSettings().put(key.getKey(), (Object)newValue);
            return true;
        }
        catch (RuntimeException e) {
            LOG.warn("Couldn't change the analytics settings. This can safely be ignored during plugin shutdown. Detail: ", (Throwable)e);
            return false;
        }
    }

    private String getSetting(AnalyticsConfigChangedEvent.Key key) {
        try {
            String setting = (String)this.pluginSettingsFactory.createGlobalSettings().get(key.getKey());
            return setting == null ? "" : setting;
        }
        catch (RuntimeException e) {
            LOG.warn("Couldn't check the analytics settings. This can safely be ignored during plugin shutdown. Detail: ", (Throwable)e);
            return "";
        }
    }

    private Boolean getBooleanSetting(AnalyticsConfigChangedEvent.Key key) {
        String value = this.getSetting(key);
        return StringUtils.isNotEmpty((CharSequence)value) && Boolean.parseBoolean(value);
    }

    public boolean canCollectAnalytics() {
        return this.isAnalyticsEnabled() && this.isPolicyUpdateAcknowledged();
    }

    public boolean hasLoggedBaseData() {
        return this.getBooleanSetting(AnalyticsConfigChangedEvent.Key.LOGGED_BASE_DATA);
    }

    public void setLoggedBaseData(boolean loggedBaseData) {
        this.updateSetting(AnalyticsConfigChangedEvent.Key.LOGGED_BASE_DATA, loggedBaseData);
    }
}

