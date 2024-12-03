/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cache.CacheConfigManager
 *  com.google.common.collect.ImmutableMap
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.cache.ehcache;

import com.atlassian.confluence.cache.CacheConfigManager;
import com.atlassian.confluence.cache.ehcache.ConfluenceEhCache;
import com.atlassian.confluence.cache.ehcache.EhCacheConfigStore;
import com.atlassian.confluence.cache.ehcache.EhCacheManager;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class EhCacheConfigManager
implements CacheConfigManager,
InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(EhCacheConfigManager.class);
    public static final String CACHE_MAX_ENTRIES_LOCAL_HEAP_CONFIG_KEY_PREFIX = "cache.maxEntriesLocalHeap.";
    private final Map<String, CacheReconfiguration> reconfigurationFunctions = ImmutableMap.of((Object)"cache.maxEntriesLocalHeap.", (Object)this.reconfigureMaxEntriesLocalHeap());
    private final EhCacheConfigStore ehCacheConfigStore;
    private final EhCacheManager ehCacheManager;

    public EhCacheConfigManager(EhCacheConfigStore ehCacheConfigStore, EhCacheManager ehCacheManager) {
        this.ehCacheConfigStore = ehCacheConfigStore;
        this.ehCacheManager = ehCacheManager;
    }

    public void afterPropertiesSet() throws Exception {
        this.reconfigureCaches();
    }

    void reconfigureCaches() {
        try {
            log.info("Reconfiguring caches from settings overrides");
            Properties cacheConfig = this.ehCacheConfigStore.readStoredConfig();
            for (String configKey : cacheConfig.stringPropertyNames()) {
                String configValue = cacheConfig.getProperty(configKey);
                if (!StringUtils.isNotBlank((CharSequence)configValue)) continue;
                this.reconfigureCacheFromConfig(configKey, configValue);
            }
        }
        catch (IOException ex) {
            log.error("Unable to read cache config", (Throwable)ex);
        }
    }

    private void reconfigureCacheFromConfig(String configKey, String configValue) {
        for (Map.Entry<String, CacheReconfiguration> reconfigurationEntry : this.reconfigurationFunctions.entrySet()) {
            String propertyPrefix = reconfigurationEntry.getKey();
            if (!configKey.startsWith(propertyPrefix)) continue;
            String cacheName = StringUtils.removeStart((String)configKey, (String)propertyPrefix);
            try {
                int configIntValue = Integer.parseInt(configValue);
                ConfluenceEhCache cache = this.ehCacheManager.getCache(cacheName);
                reconfigurationEntry.getValue().reconfigure(cache, configIntValue);
            }
            catch (NumberFormatException ex) {
                log.warn("Config value for property '{}' is not a number: '{}'", (Object)configKey, (Object)configValue);
            }
        }
    }

    public void changeMaxCacheSize(String cacheName, int newMaxCacheSize) {
        log.info("Updating max size of cache [{}] to [{}]", (Object)cacheName, (Object)newMaxCacheSize);
        ConfluenceEhCache ehCache = this.ehCacheManager.getCache(cacheName);
        boolean updated = ehCache.updateMaxEntriesLocalHeap(newMaxCacheSize);
        if (!updated) {
            log.warn("Unable to update max entries for local heap for cache {} to {}.", (Object)cacheName, (Object)newMaxCacheSize);
        }
        this.updateStoredMaxEntriesLocalHeap(cacheName, newMaxCacheSize);
    }

    private void updateStoredMaxEntriesLocalHeap(String cacheName, int newMaxCacheSize) {
        try {
            Properties configProperties = this.ehCacheConfigStore.readStoredConfig();
            String configKey = CACHE_MAX_ENTRIES_LOCAL_HEAP_CONFIG_KEY_PREFIX + cacheName;
            configProperties.setProperty(configKey, String.valueOf(newMaxCacheSize));
            this.ehCacheConfigStore.updateStoredConfig(configProperties);
        }
        catch (IOException ex) {
            log.error("Failed to store new ehcache config properties", (Throwable)ex);
        }
    }

    private CacheReconfiguration reconfigureMaxEntriesLocalHeap() {
        return (cache, configValue) -> {
            log.info("Reconfiguring cache '{}' with maxEntriesLocalHeap={}", (Object)cache.getName(), (Object)configValue);
            boolean updated = cache.updateMaxEntriesLocalHeap(configValue);
            if (!updated) {
                log.warn("Unable to update max entries for local heap for cache {} to {}.", (Object)cache.getName(), (Object)configValue);
            }
        };
    }

    private static interface CacheReconfiguration {
        public void reconfigure(ConfluenceEhCache var1, int var2);
    }
}

