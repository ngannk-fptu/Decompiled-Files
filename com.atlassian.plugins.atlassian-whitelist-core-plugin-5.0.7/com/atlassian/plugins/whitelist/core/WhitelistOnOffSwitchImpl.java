/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugins.whitelist.WhitelistOnOffSwitch
 *  com.atlassian.plugins.whitelist.events.ClearWhitelistCacheEvent
 *  com.atlassian.plugins.whitelist.events.WhitelistDisabledEvent
 *  com.atlassian.plugins.whitelist.events.WhitelistEnabledEvent
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.BooleanUtils
 *  org.apache.commons.lang3.ObjectUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.plugins.whitelist.core;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugins.whitelist.WhitelistOnOffSwitch;
import com.atlassian.plugins.whitelist.events.ClearWhitelistCacheEvent;
import com.atlassian.plugins.whitelist.events.WhitelistDisabledEvent;
import com.atlassian.plugins.whitelist.events.WhitelistEnabledEvent;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class WhitelistOnOffSwitchImpl
implements WhitelistOnOffSwitch,
InitializingBean,
DisposableBean {
    private static final String CACHE_NAME = WhitelistOnOffSwitchImpl.class.getName() + ".enabled";
    private static final String CACHE_KEY = "5";
    private static final String WHITELIST_ENABLED_KEY = "com.atlassian.plugins.atlassian-whitelist-api-plugin:whitelist.enabled";
    private static final boolean ENABLED_BY_DEFAULT = true;
    private static final Logger logger = LoggerFactory.getLogger(WhitelistOnOffSwitchImpl.class);
    private final Cache<String, Boolean> cache;
    private final PluginSettingsFactory pluginSettingsFactory;
    private final EventPublisher eventPublisher;

    public WhitelistOnOffSwitchImpl(PluginSettingsFactory pluginSettingsFactory, EventPublisher eventPublisher, CacheManager cacheFactory) {
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.eventPublisher = eventPublisher;
        this.cache = cacheFactory.getCache(CACHE_NAME, null, new CacheSettingsBuilder().remote().expireAfterWrite(1L, TimeUnit.HOURS).build());
    }

    public void afterPropertiesSet() throws Exception {
        this.eventPublisher.register((Object)this);
    }

    public void destroy() throws Exception {
        this.eventPublisher.unregister((Object)this);
    }

    public void enable() {
        this.storeValue(true);
        this.clearCache();
        logger.debug("Whitelist has been enabled.");
        this.eventPublisher.publish((Object)WhitelistEnabledEvent.INSTANCE);
    }

    public void disable() {
        this.storeValue(false);
        this.clearCache();
        logger.debug("Whitelist has been disabled.");
        this.eventPublisher.publish((Object)WhitelistDisabledEvent.INSTANCE);
    }

    public boolean isEnabled() {
        try {
            return (Boolean)this.cache.get((Object)CACHE_KEY, this::loadValue);
        }
        catch (RuntimeException th) {
            logger.warn("Failed to read entry from cache '" + CACHE_NAME + "': {}", (Object)th.getMessage());
            return this.loadValue();
        }
    }

    @EventListener
    public void onClearWhitelistCacheEvent(ClearWhitelistCacheEvent event) {
        this.clearCache();
    }

    private void clearCache() {
        try {
            this.cache.removeAll();
        }
        catch (RuntimeException th) {
            logger.error("Failed to remove all entries from cache '" + CACHE_NAME + "': {}", (Object)th.getMessage());
        }
    }

    @Nonnull
    private Boolean loadValue() {
        Object o = this.settings().get(WHITELIST_ENABLED_KEY);
        Boolean storedValue = BooleanUtils.toBooleanObject((String)ObjectUtils.toString((Object)o, null));
        return BooleanUtils.toBooleanDefaultIfNull((Boolean)storedValue, (boolean)true);
    }

    private void storeValue(boolean enabled) {
        this.settings().put(WHITELIST_ENABLED_KEY, (Object)Boolean.toString(enabled));
    }

    private PluginSettings settings() {
        return this.pluginSettingsFactory.createGlobalSettings();
    }
}

