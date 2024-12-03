/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheLoader
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsBuilder
 *  javax.annotation.Nonnull
 *  javax.annotation.PreDestroy
 *  org.jetbrains.annotations.NotNull
 */
package com.atlassian.migration.agent.media.impl;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheLoader;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.migration.agent.json.Jsons;
import com.atlassian.migration.agent.media.MediaClientToken;
import com.atlassian.migration.agent.media.MediaClientTokenSupplier;
import com.atlassian.migration.agent.service.confluence.ConfluenceCloudService;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import javax.annotation.PreDestroy;
import org.jetbrains.annotations.NotNull;

public class CachedMediaClientTokenSupplier
implements MediaClientTokenSupplier {
    private static final String CACHE_NAME = "com.atlassian.migration.agent.mediaClientTokenCache";
    private static final Duration DEFAULT_MEDIA_TOKEN_CACHE_EXPIRY_TTL = Duration.ofMinutes(8L);
    @VisibleForTesting
    static final Duration DEFAULT_MEDIA_TOKEN_TTL = Duration.ofMinutes(15L);
    private final Cache<String, String> clientTokens;

    public CachedMediaClientTokenSupplier(final ConfluenceCloudService confluenceCloudService, CacheManager cacheManager) {
        CacheSettings cacheSettings = new CacheSettingsBuilder().remote().replicateViaCopy().expireAfterWrite(DEFAULT_MEDIA_TOKEN_CACHE_EXPIRY_TTL.toMillis(), TimeUnit.MILLISECONDS).build();
        CacheLoader<String, String> cacheLoader = new CacheLoader<String, String>(){

            @NotNull
            public String load(@NotNull String containerToken) {
                return Jsons.valueAsString(confluenceCloudService.getMediaClientToken(containerToken, DEFAULT_MEDIA_TOKEN_TTL));
            }
        };
        this.clientTokens = cacheManager.getCache(CACHE_NAME, (CacheLoader)cacheLoader, cacheSettings);
        this.clientTokens.removeAll();
    }

    @Override
    @Nonnull
    public MediaClientToken getToken(String containerToken) {
        try {
            return Objects.requireNonNull(Jsons.readValue((String)this.clientTokens.get((Object)containerToken), MediaClientToken.class));
        }
        catch (Exception e) {
            throw new IllegalStateException("Failed to load media token from container token", e);
        }
    }

    @Override
    @Nonnull
    public MediaClientToken getRefreshedToken(String containerToken) {
        this.clientTokens.remove((Object)containerToken);
        return this.getToken(containerToken);
    }

    @PreDestroy
    public void destroy() {
        this.clientTokens.removeAll();
    }
}

