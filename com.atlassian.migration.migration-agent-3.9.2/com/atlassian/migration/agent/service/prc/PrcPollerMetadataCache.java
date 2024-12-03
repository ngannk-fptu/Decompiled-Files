/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettings
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.google.common.cache.Cache
 *  com.google.common.cache.CacheBuilder
 *  javax.annotation.PreDestroy
 */
package com.atlassian.migration.agent.service.prc;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettings;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.migration.agent.entity.CloudSite;
import com.atlassian.migration.agent.mapi.external.model.PublicApiException;
import com.atlassian.migration.agent.service.cloud.CloudSiteService;
import com.google.common.cache.CacheBuilder;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;

public class PrcPollerMetadataCache {
    private static final String USER_CONTEXT_CACHE_NAME = "com.atlassian.migration.agent.service.prc.prcPollerMetadataCache";
    private static final Integer USER_CONTEXT_CACHE_EXPIRY_IN_DAYS = 19;
    private static Integer maximumContainerTokenCacheSize = 100;
    private static Integer containerTokenTtlInHours = 1;
    private Cache<String, ConfluenceUser> prcPollerUserContext;
    private com.google.common.cache.Cache<String, String> containerTokenLocalCache;
    private CloudSiteService cloudSiteService;

    public PrcPollerMetadataCache(CacheManager cacheManager, CloudSiteService cloudSiteService) {
        CacheSettings cacheSettings = new CacheSettingsBuilder().expireAfterWrite((long)USER_CONTEXT_CACHE_EXPIRY_IN_DAYS.intValue(), TimeUnit.DAYS).remote().replicateViaCopy().build();
        this.prcPollerUserContext = cacheManager.getCache(USER_CONTEXT_CACHE_NAME, null, cacheSettings);
        this.containerTokenLocalCache = CacheBuilder.newBuilder().maximumSize((long)maximumContainerTokenCacheSize.intValue()).expireAfterWrite((long)containerTokenTtlInHours.intValue(), TimeUnit.HOURS).build();
        this.cloudSiteService = cloudSiteService;
    }

    public String getContainerTokenForCloudId(String cloudId) {
        String containerToken = (String)this.containerTokenLocalCache.getIfPresent((Object)cloudId);
        if (containerToken == null) {
            Optional<CloudSite> cloudSiteOptional = this.cloudSiteService.getByCloudId(cloudId);
            if (cloudSiteOptional.isPresent()) {
                containerToken = cloudSiteOptional.get().getContainerToken();
                this.containerTokenLocalCache.put((Object)cloudId, (Object)containerToken);
            } else {
                throw new PublicApiException.ResourceNotFound(String.format("Can not find containerToken for cloudId = %s", cloudId));
            }
        }
        return containerToken;
    }

    public void removeContainerTokenInCacheForCloudId(String cloudId) {
        this.containerTokenLocalCache.invalidate((Object)cloudId);
    }

    public ConfluenceUser getPrcPollerUserContext(String cloudUrl) {
        return (ConfluenceUser)this.prcPollerUserContext.get((Object)cloudUrl);
    }

    public void setPrcPollerUserContext(String cloudUrl, ConfluenceUser pollerContext) {
        this.prcPollerUserContext.put((Object)cloudUrl, (Object)pollerContext);
    }

    @PreDestroy
    public void destroy() {
        this.prcPollerUserContext.removeAll();
        this.containerTokenLocalCache.invalidateAll();
    }
}

