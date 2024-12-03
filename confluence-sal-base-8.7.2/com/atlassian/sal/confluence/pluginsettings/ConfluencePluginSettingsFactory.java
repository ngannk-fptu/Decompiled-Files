/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext
 *  com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  io.atlassian.fugue.Option
 *  javax.annotation.Nullable
 */
package com.atlassian.sal.confluence.pluginsettings;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.spring.transaction.interceptor.TransactionalHostContextAccessor;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.confluence.pluginsettings.CachingPluginSettings;
import com.atlassian.sal.confluence.pluginsettings.ConfluencePluginSettings;
import com.atlassian.sal.confluence.pluginsettings.TransactionalPluginSettings;
import io.atlassian.fugue.Option;
import java.io.Serializable;
import javax.annotation.Nullable;

public class ConfluencePluginSettingsFactory
implements PluginSettingsFactory {
    private static final String CACHE_NAME = ConfluencePluginSettings.class.getName();
    private final BandanaManager bandanaManager;
    private final TransactionalHostContextAccessor hostContextAccessor;
    private final CacheFactory cacheFactory;

    public ConfluencePluginSettingsFactory(BandanaManager bandanaManager, TransactionalHostContextAccessor hostContextAccessor, CacheFactory cacheFactory) {
        this.bandanaManager = bandanaManager;
        this.hostContextAccessor = hostContextAccessor;
        this.cacheFactory = cacheFactory;
    }

    public PluginSettings createSettingsForKey(@Nullable String key) {
        ConfluenceBandanaContext bandanaContext = new ConfluenceBandanaContext(key);
        ConfluencePluginSettings bandanaPluginSettings = new ConfluencePluginSettings(this.bandanaManager, bandanaContext);
        CachingPluginSettings cachingPluginSettings = new CachingPluginSettings((Cache<CachingPluginSettings.CacheKey, Option<Object>>)this.cacheFactory.getCache(CACHE_NAME), bandanaPluginSettings, (Serializable)((Object)bandanaContext.getContextKey()));
        return new TransactionalPluginSettings(cachingPluginSettings, this.hostContextAccessor);
    }

    public PluginSettings createGlobalSettings() {
        return this.createSettingsForKey(null);
    }
}

