/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cache.annotation;

import org.springframework.cache.annotation.AbstractCachingConfiguration;
import org.springframework.cache.annotation.AnnotationCacheOperationSource;
import org.springframework.cache.interceptor.BeanFactoryCacheOperationSourceAdvisor;
import org.springframework.cache.interceptor.CacheInterceptor;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

@Configuration
@Role(value=2)
public class ProxyCachingConfiguration
extends AbstractCachingConfiguration {
    @Bean(name={"org.springframework.cache.config.internalCacheAdvisor"})
    @Role(value=2)
    public BeanFactoryCacheOperationSourceAdvisor cacheAdvisor() {
        BeanFactoryCacheOperationSourceAdvisor advisor = new BeanFactoryCacheOperationSourceAdvisor();
        advisor.setCacheOperationSource(this.cacheOperationSource());
        advisor.setAdvice(this.cacheInterceptor());
        if (this.enableCaching != null) {
            advisor.setOrder((Integer)this.enableCaching.getNumber("order"));
        }
        return advisor;
    }

    @Bean
    @Role(value=2)
    public CacheOperationSource cacheOperationSource() {
        return new AnnotationCacheOperationSource();
    }

    @Bean
    @Role(value=2)
    public CacheInterceptor cacheInterceptor() {
        CacheInterceptor interceptor = new CacheInterceptor();
        interceptor.setCacheOperationSources(this.cacheOperationSource());
        if (this.cacheResolver != null) {
            interceptor.setCacheResolver(this.cacheResolver);
        } else if (this.cacheManager != null) {
            interceptor.setCacheManager(this.cacheManager);
        }
        if (this.keyGenerator != null) {
            interceptor.setKeyGenerator(this.keyGenerator);
        }
        if (this.errorHandler != null) {
            interceptor.setErrorHandler(this.errorHandler);
        }
        return interceptor;
    }
}

