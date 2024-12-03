/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.databind.ObjectMapper
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  javax.inject.Singleton
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.sisyphus.marketplace;

import com.atlassian.sisyphus.marketplace.MarketPlaceData;
import com.atlassian.sisyphus.marketplace.MarketPlaceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class MarketPlaceServiceImpl
implements MarketPlaceService {
    private static final Logger log = LoggerFactory.getLogger(MarketPlaceServiceImpl.class);
    private static final String MARKETPLACE_BASE_URL = "https://marketplace.atlassian.com";
    private static final int EXPIRE_CACHE_DAYS = 1;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private LoadingCache<String, MarketPlaceData> cache = CacheBuilder.newBuilder().expireAfterWrite(1L, TimeUnit.DAYS).build((CacheLoader)new CacheLoader<String, MarketPlaceData>(){

        public MarketPlaceData load(String key) throws Exception {
            return (MarketPlaceData)MarketPlaceServiceImpl.this.objectMapper.readValue(new URL("https://marketplace.atlassian.com/rest/1.0/plugins/" + key), MarketPlaceData.class);
        }
    });

    @Override
    public MarketPlaceData getPluginData(String pluginKey) {
        log.debug("Getting marketplace data for - " + pluginKey);
        try {
            return (MarketPlaceData)this.cache.get((Object)pluginKey);
        }
        catch (ExecutionException e) {
            log.error("Unable to get MPAC data - " + e.getMessage());
            return null;
        }
    }
}

