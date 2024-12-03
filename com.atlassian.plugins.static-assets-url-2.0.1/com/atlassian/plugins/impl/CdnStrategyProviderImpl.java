/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.cache.CachedReference
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.gadgets.event.ClearSpecCacheEvent
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.webresource.WebResourceIntegration
 *  com.atlassian.plugin.webresource.cdn.CDNStrategy
 *  com.atlassian.plugin.webresource.cdn.CdnStrategyProvider
 *  com.atlassian.plugin.webresource.prebake.PrebakeConfig
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  javax.inject.Inject
 *  org.apache.log4j.Logger
 *  org.springframework.stereotype.Component
 */
package com.atlassian.plugins.impl;

import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.cache.CachedReference;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.gadgets.event.ClearSpecCacheEvent;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.webresource.WebResourceIntegration;
import com.atlassian.plugin.webresource.cdn.CDNStrategy;
import com.atlassian.plugin.webresource.cdn.CdnStrategyProvider;
import com.atlassian.plugin.webresource.prebake.PrebakeConfig;
import com.atlassian.plugins.impl.CdnConfigurationChangedEvent;
import com.atlassian.plugins.impl.PrefixCDNStrategy;
import com.atlassian.plugins.impl.rest.CdnConfigurationEntity;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import java.util.Optional;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
@ExportAsService(value={CdnStrategyProvider.class})
public class CdnStrategyProviderImpl
implements CdnStrategyProvider {
    private static final Logger log = Logger.getLogger(CdnStrategyProviderImpl.class);
    public static final String CDN_ENABLED_FEATURE_KEY = "atlassian.cdn.enabled";
    public static final String CDN_URL_FEATURE_KEY = "atlassian.prefix.cdn.url";
    private static final String PRE_BAKED_MAPPING_FILE = "jira.cdn.prebaked.mapping.file";
    private final String CT_CDN_MAPPING_FILE = System.getProperty("jira.cdn.prebaked.mapping.file");
    private final PluginSettingsFactory pluginSettingsFactory;
    private final TransactionTemplate transactionTemplate;
    private final EventPublisher eventPublisher;
    private final WebResourceIntegration webResourceIntegration;
    private final CachedReference<Optional<CDNStrategy>> cachedStrategyReference;

    @Inject
    public CdnStrategyProviderImpl(PluginSettingsFactory pluginSettingsFactory, TransactionTemplate transactionTemplate, CacheManager cacheManager, EventPublisher eventPublisher, WebResourceIntegration webResourceIntegration) {
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.transactionTemplate = transactionTemplate;
        this.eventPublisher = eventPublisher;
        this.webResourceIntegration = webResourceIntegration;
        this.cachedStrategyReference = cacheManager.getCachedReference(CdnStrategyProviderImpl.class.getName(), this::createCdnStrategy, new CacheSettingsBuilder().remote().replicateViaInvalidation().replicateAsynchronously().build());
    }

    @PostConstruct
    @PreDestroy
    private void reset() {
        this.cachedStrategyReference.reset();
    }

    public Optional<CDNStrategy> getCdnStrategy() {
        return (Optional)this.cachedStrategyReference.get();
    }

    private Optional<CDNStrategy> createCdnStrategy() {
        return this.isEnabled() ? Optional.of(new PrefixCDNStrategy(this.getUrl(), this.getPrebakeConfig())) : Optional.empty();
    }

    private Optional<PrebakeConfig> getPrebakeConfig() {
        return this.CT_CDN_MAPPING_FILE != null ? Optional.of(PrebakeConfig.forPattern((String)this.CT_CDN_MAPPING_FILE)) : Optional.empty();
    }

    public boolean isEnabled() {
        return Boolean.valueOf((String)this.pluginSettingsFactory.createGlobalSettings().get(CDN_ENABLED_FEATURE_KEY));
    }

    public String getUrl() {
        return (String)this.pluginSettingsFactory.createGlobalSettings().get(CDN_URL_FEATURE_KEY);
    }

    public CdnConfigurationEntity getConfiguration() {
        return new CdnConfigurationEntity(this.isEnabled(), this.getUrl());
    }

    public void setConfiguration(CdnConfigurationEntity configuration) {
        this.transactionTemplate.execute(() -> {
            PluginSettings pluginSettings = this.pluginSettingsFactory.createGlobalSettings();
            pluginSettings.put(CDN_ENABLED_FEATURE_KEY, (Object)String.valueOf(configuration.isEnabled()));
            pluginSettings.put(CDN_URL_FEATURE_KEY, (Object)configuration.getUrl());
            return null;
        });
        this.reset();
        this.clearGadgetSpecCache();
        this.eventPublisher.publish((Object)new CdnConfigurationChangedEvent(configuration.isEnabled(), configuration.getUrl()));
        this.webResourceIntegration.rebuildResourceUrlPrefix();
    }

    private void clearGadgetSpecCache() {
        try {
            this.eventPublisher.publish((Object)new ClearSpecCacheEvent());
        }
        catch (NoClassDefFoundError e) {
            log.info((Object)("Gadgets API not present in product, skipping cache invalidation: " + e));
        }
    }
}

