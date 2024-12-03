/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bitbucket.server.ApplicationMode
 *  com.atlassian.bitbucket.server.ApplicationPropertiesService
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 */
package com.atlassian.analytics.client.configuration;

import com.atlassian.analytics.client.configuration.AnalyticsConfig;
import com.atlassian.analytics.client.configuration.BitbucketAnalyticsSettings;
import com.atlassian.bitbucket.server.ApplicationMode;
import com.atlassian.bitbucket.server.ApplicationPropertiesService;
import com.atlassian.cache.CacheManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

public class BitbucketAnalyticsConfig
extends AnalyticsConfig {
    private final ApplicationMode applicationMode;
    private final BitbucketAnalyticsSettings bitbucketAnalyticsSettings;

    public BitbucketAnalyticsConfig(PluginSettingsFactory pluginSettingsFactory, EventPublisher eventPublisher, CacheManager cacheManager, ApplicationPropertiesService applicationPropertiesService, BitbucketAnalyticsSettings bitbucketAnalyticsSettings) {
        super(pluginSettingsFactory, eventPublisher, cacheManager);
        this.bitbucketAnalyticsSettings = bitbucketAnalyticsSettings;
        this.applicationMode = applicationPropertiesService.getMode();
    }

    @Override
    public boolean canCollectAnalytics() {
        if (ApplicationMode.MIRROR.equals((Object)this.applicationMode)) {
            return this.bitbucketAnalyticsSettings.canCollectAnalytics();
        }
        return super.canCollectAnalytics();
    }
}

