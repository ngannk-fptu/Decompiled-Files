/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.AvailableToPlugins
 *  javax.annotation.Resource
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.confluence.impl.cache;

import com.atlassian.cache.CacheManager;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.impl.cache.ConfluenceCacheManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.AvailableToPlugins;
import javax.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class CacheManagerContextConfig {
    @Resource
    private ClusterManager clusterManager;
    @Resource
    private CacheManager confluenceHazelcastCacheManager;
    @Resource
    private CacheManager ehCacheManager;
    @Resource
    private EventPublisher eventPublisher;

    CacheManagerContextConfig() {
    }

    @Bean
    @AvailableToPlugins(interfaces={CacheManager.class})
    CacheManager cacheManager() {
        CacheManager delegate = this.clusterManager.isClustered() ? this.confluenceHazelcastCacheManager : this.ehCacheManager;
        return new ConfluenceCacheManager(delegate, this.eventPublisher);
    }
}

