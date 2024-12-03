/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.confluence.setup.xstream.ConfluenceXStreamManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.webdav;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheManager;
import com.atlassian.confluence.extra.webdav.BandanaWebdavSettingsManager;
import com.atlassian.confluence.extra.webdav.WebdavSettings;
import com.atlassian.confluence.setup.xstream.ConfluenceXStreamManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class DefaultWebdavSettingsManager
extends BandanaWebdavSettingsManager {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultWebdavSettingsManager.class);
    private static final String CACHE_KEY = "com.atlassian.confluence.extra.webdav.settings";
    private final CacheManager cacheManager;

    public DefaultWebdavSettingsManager(@ComponentImport BandanaManager bandanaManager, @ComponentImport CacheManager cacheManager, @ComponentImport ConfluenceXStreamManager xStreamManager) {
        super(bandanaManager, xStreamManager);
        this.cacheManager = cacheManager;
    }

    private String getCacheEntryKey() {
        return "com.atlassian.confluence.extra.webdav.settings.global";
    }

    private WebdavSettings getCachedSetings() {
        Cache webdavSettingsCache = this.cacheManager.getCache(CACHE_KEY);
        String cacheEntryKey = this.getCacheEntryKey();
        try {
            return (WebdavSettings)webdavSettingsCache.get((Object)cacheEntryKey);
        }
        catch (ClassCastException cce) {
            LOG.warn("Unable to cast the cached WebdavSettings retrieved with key " + cacheEntryKey + " to a WebdavSettings. It will be purged from the cache.", (Throwable)cce);
            webdavSettingsCache.remove((Object)cacheEntryKey);
            return null;
        }
    }

    private void cacheSettings(WebdavSettings webdavSettings) {
        Cache webdavSettingsCache = this.cacheManager.getCache(CACHE_KEY);
        webdavSettingsCache.put((Object)this.getCacheEntryKey(), (Object)webdavSettings);
    }

    @Override
    public void save(WebdavSettings webdavSettings) {
        super.save(webdavSettings);
        this.cacheSettings(new WebdavSettings(webdavSettings));
    }

    @Override
    public WebdavSettings getWebdavSettings() {
        WebdavSettings webdavSettings = this.getCachedSetings();
        if (null != webdavSettings) {
            return new WebdavSettings(webdavSettings);
        }
        webdavSettings = super.getWebdavSettings();
        this.cacheSettings(new WebdavSettings(webdavSettings));
        return webdavSettings;
    }
}

