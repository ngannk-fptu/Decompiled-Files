/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsBuilder
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.ThreadSafe
 */
package com.atlassian.confluence.extra.jira.cache;

import com.atlassian.annotations.Internal;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.confluence.extra.jira.api.services.ConfluenceJiraPluginSettingManager;
import com.atlassian.confluence.extra.jira.cache.CompressingStringCache;
import com.atlassian.confluence.extra.jira.cache.JIMCache;
import com.atlassian.confluence.extra.jira.request.JiraChannelResponseHandler;
import com.atlassian.confluence.extra.jira.request.JiraStringResponseHandler;
import java.io.Serializable;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

@Internal
@ThreadSafe
public class JIMCacheProvider {
    public static final String JIM_CACHE_NAME = "com.atlassian.confluence.extra.jira.JiraIssuesMacro";
    private static final String JIM_CHANNEL_RESPONSE_CACHE_NAME = "com.atlassian.confluence.extra.jira.JiraIssuesMacro.channel";
    private static final String JIM_STRING_RESPONSE_CACHE_NAME = "com.atlassian.confluence.extra.jira.JiraIssuesMacro.string";
    private static final Integer DEFAULT_JIM_CACHE_TIMEOUT = Integer.parseInt(System.getProperty("confluence.jim.cache.time", "5"));
    private final CacheFactory cacheFactory;
    private final ConfluenceJiraPluginSettingManager settingManager;

    public JIMCacheProvider(CacheFactory cacheFactory, ConfluenceJiraPluginSettingManager settingManager) {
        this.cacheFactory = Objects.requireNonNull(cacheFactory);
        this.settingManager = Objects.requireNonNull(settingManager);
    }

    public JIMCache<CompressingStringCache> getResponseCache() {
        return this.cache(JIM_CACHE_NAME, null);
    }

    public JIMCache<JiraChannelResponseHandler> getChannelResponseHandlersCache() {
        Integer finalCacheTimeOutInMinutes = this.settingManager.getCacheTimeoutInMinutes().orElse(DEFAULT_JIM_CACHE_TIMEOUT);
        if (finalCacheTimeOutInMinutes <= 0) {
            return this.cache(JIM_CHANNEL_RESPONSE_CACHE_NAME, Duration.ofSeconds(1L));
        }
        return this.cache(JIM_CHANNEL_RESPONSE_CACHE_NAME, Duration.ofMinutes(finalCacheTimeOutInMinutes.intValue()));
    }

    public JIMCache<JiraStringResponseHandler> getStringResponseHandlersCache() {
        Integer finalCacheTimeOutInMinutes = this.settingManager.getCacheTimeoutInMinutes().orElse(DEFAULT_JIM_CACHE_TIMEOUT);
        if (finalCacheTimeOutInMinutes <= 0) {
            return this.cache(JIM_STRING_RESPONSE_CACHE_NAME, Duration.ofSeconds(1L));
        }
        return this.cache(JIM_STRING_RESPONSE_CACHE_NAME, Duration.ofMinutes(finalCacheTimeOutInMinutes.intValue()));
    }

    private <V extends Serializable> JIMCache<V> cache(String cacheName, @Nullable Duration ttl) {
        return new JIMCache.AtlassianCacheImpl(this.cacheFactory.getCache(cacheName, null, JIMCacheProvider.cacheSettings(ttl)));
    }

    private static CacheSettings cacheSettings(@Nullable Duration ttl) {
        CacheSettingsBuilder builder = new CacheSettingsBuilder().remote().replicateViaCopy();
        if (ttl != null) {
            builder.expireAfterWrite(ttl.toMillis(), TimeUnit.MILLISECONDS);
        }
        return builder.build();
    }
}

