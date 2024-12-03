/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.cache.annotation.AbstractCachingConfiguration
 *  org.springframework.cache.annotation.AbstractCachingConfiguration$CachingConfigurerSupplier
 *  org.springframework.cache.interceptor.CacheResolver
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Role
 *  org.springframework.lang.Nullable
 */
package org.springframework.cache.jcache.config;

import java.util.function.Supplier;
import org.springframework.cache.annotation.AbstractCachingConfiguration;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.jcache.config.JCacheConfigurer;
import org.springframework.cache.jcache.interceptor.DefaultJCacheOperationSource;
import org.springframework.cache.jcache.interceptor.JCacheOperationSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.lang.Nullable;

@Configuration(proxyBeanMethods=false)
public abstract class AbstractJCacheConfiguration
extends AbstractCachingConfiguration {
    @Nullable
    protected Supplier<CacheResolver> exceptionCacheResolver;

    protected void useCachingConfigurer(AbstractCachingConfiguration.CachingConfigurerSupplier cachingConfigurerSupplier) {
        super.useCachingConfigurer(cachingConfigurerSupplier);
        this.exceptionCacheResolver = cachingConfigurerSupplier.adapt(config -> {
            if (config instanceof JCacheConfigurer) {
                return ((JCacheConfigurer)config).exceptionCacheResolver();
            }
            return null;
        });
    }

    @Bean(name={"jCacheOperationSource"})
    @Role(value=2)
    public JCacheOperationSource cacheOperationSource() {
        return new DefaultJCacheOperationSource(this.cacheManager, this.cacheResolver, this.exceptionCacheResolver, this.keyGenerator);
    }
}

