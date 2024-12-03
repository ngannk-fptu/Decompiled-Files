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

@Configuration(proxyBeanMethods=false)
@Role(value=2)
public class ProxyCachingConfiguration
extends AbstractCachingConfiguration {
    @Bean(name={"org.springframework.cache.config.internalCacheAdvisor"})
    @Role(value=2)
    public BeanFactoryCacheOperationSourceAdvisor cacheAdvisor(CacheOperationSource cacheOperationSource, CacheInterceptor cacheInterceptor) {
        BeanFactoryCacheOperationSourceAdvisor advisor = new BeanFactoryCacheOperationSourceAdvisor();
        advisor.setCacheOperationSource(cacheOperationSource);
        advisor.setAdvice(cacheInterceptor);
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
    public CacheInterceptor cacheInterceptor(CacheOperationSource cacheOperationSource) {
        CacheInterceptor interceptor = new CacheInterceptor();
        interceptor.configure(this.errorHandler, this.keyGenerator, this.cacheResolver, this.cacheManager);
        interceptor.setCacheOperationSource(cacheOperationSource);
        return interceptor;
    }
}

