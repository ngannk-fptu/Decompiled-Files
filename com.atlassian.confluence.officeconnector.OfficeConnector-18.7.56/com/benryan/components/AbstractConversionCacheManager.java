/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.plugin.Plugin
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.benryan.components.OcSettingsManager
 *  io.atlassian.util.concurrent.ResettableLazyReference
 *  javax.annotation.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.benryan.components;

import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.plugin.Plugin;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.benryan.components.OcSettingsManager;
import com.benryan.conversion.ConversionCache;
import com.benryan.conversion.FileBackedCache;
import io.atlassian.util.concurrent.ResettableLazyReference;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

class AbstractConversionCacheManager<K, V> {
    private static final String OC_KEY = "com.atlassian.confluence.extra.officeconnector";
    protected static final Logger log = LoggerFactory.getLogger(AbstractConversionCacheManager.class);
    protected final PageManager pageManager;
    protected final AttachmentManager fileManager;
    private final ResettableLazyReference<Optional<ConversionCache>> cacheRef;
    protected final Date lastUpgrade;
    protected final OcSettingsManager ocSettingsManager;

    @Autowired
    AbstractConversionCacheManager(@ComponentImport PageManager pageManager, @ComponentImport AttachmentManager fileManager, final OcSettingsManager ocSettingsManager, @ComponentImport PluginAccessor accessor) {
        this.pageManager = pageManager;
        this.fileManager = fileManager;
        this.ocSettingsManager = ocSettingsManager;
        Plugin plugin = accessor.getPlugin(OC_KEY);
        this.lastUpgrade = plugin.getDateLoaded();
        this.cacheRef = new ResettableLazyReference<Optional<ConversionCache>>(){

            protected Optional<ConversionCache> create() {
                return Optional.ofNullable(AbstractConversionCacheManager.createCache(ocSettingsManager));
            }
        };
    }

    protected String buildBaseKey(String attachmentId) {
        return attachmentId;
    }

    public void initCache() {
        this.cacheRef.reset();
    }

    @Nullable
    private static FileBackedCache createCache(OcSettingsManager ocSettingsManager) {
        int cacheType = ocSettingsManager.getCacheType();
        switch (cacheType) {
            case 0: 
            case 1: 
            case 2: {
                String path = ocSettingsManager.getCacheDir();
                try {
                    return new FileBackedCache(path);
                }
                catch (IOException e) {
                    log.error(e.getMessage(), (Throwable)e);
                    break;
                }
            }
            default: {
                log.error("Unhandled cache type {}", (Object)cacheType);
            }
        }
        return null;
    }

    public void putToCache(K key, V value) {
        ((Optional)this.cacheRef.get()).ifPresent(cache -> cache.put(key, value));
    }

    @Nullable
    public V getFromCache(K key) {
        return ((Optional)this.cacheRef.get()).map(cache -> cache.get(key)).orElse(null);
    }
}

