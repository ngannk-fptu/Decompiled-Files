/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheManager
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.migration.agent;

import com.atlassian.cache.CacheManager;
import com.atlassian.migration.agent.CommonBeanConfiguration;
import com.atlassian.migration.agent.ImportedOsgiServiceBeans;
import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.media.MediaClientTokenSupplier;
import com.atlassian.migration.agent.media.impl.CachedMediaClientTokenSupplier;
import com.atlassian.migration.agent.media.impl.DefaultMediaFileUploaderFactory;
import com.atlassian.migration.agent.okhttp.OKHttpProxyBuilder;
import com.atlassian.migration.agent.service.confluence.ConfluenceCloudService;
import com.atlassian.migration.agent.service.impl.MigrationPlatformService;
import com.atlassian.migration.agent.service.impl.UserAgentInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(value={ImportedOsgiServiceBeans.class, CommonBeanConfiguration.class})
@Configuration
public class MediaBeanConfiguration {
    @Bean
    public MigrationPlatformService migrationPlatformService(MigrationAgentConfiguration configuration, UserAgentInterceptor userAgentInterceptor, OKHttpProxyBuilder okHttpProxyBuilder) {
        return new MigrationPlatformService(configuration, userAgentInterceptor, okHttpProxyBuilder);
    }

    @Bean
    public CachedMediaClientTokenSupplier cachedMediaClientTokenSupplier(ConfluenceCloudService confluenceCloudService, CacheManager cacheManager) {
        return new CachedMediaClientTokenSupplier(confluenceCloudService, cacheManager);
    }

    @Bean
    public DefaultMediaFileUploaderFactory defaultMediaFileUploaderFactory(MediaClientTokenSupplier mediaClientTokenSupplier, MigrationAgentConfiguration configuration, UserAgentInterceptor userAgentInterceptor, OKHttpProxyBuilder okHttpProxyBuilder) {
        return new DefaultMediaFileUploaderFactory(mediaClientTokenSupplier, configuration, userAgentInterceptor, okHttpProxyBuilder);
    }
}

