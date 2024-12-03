/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.aop.Advice
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Role
 */
package org.springframework.cache.jcache.config;

import org.aopalliance.aop.Advice;
import org.springframework.cache.jcache.config.AbstractJCacheConfiguration;
import org.springframework.cache.jcache.interceptor.BeanFactoryJCacheOperationSourceAdvisor;
import org.springframework.cache.jcache.interceptor.JCacheInterceptor;
import org.springframework.cache.jcache.interceptor.JCacheOperationSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;

@Configuration(proxyBeanMethods=false)
@Role(value=2)
public class ProxyJCacheConfiguration
extends AbstractJCacheConfiguration {
    @Bean(name={"org.springframework.cache.config.internalJCacheAdvisor"})
    @Role(value=2)
    public BeanFactoryJCacheOperationSourceAdvisor cacheAdvisor(JCacheOperationSource jCacheOperationSource, JCacheInterceptor jCacheInterceptor) {
        BeanFactoryJCacheOperationSourceAdvisor advisor = new BeanFactoryJCacheOperationSourceAdvisor();
        advisor.setCacheOperationSource(jCacheOperationSource);
        advisor.setAdvice((Advice)jCacheInterceptor);
        if (this.enableCaching != null) {
            advisor.setOrder((Integer)this.enableCaching.getNumber("order"));
        }
        return advisor;
    }

    @Bean(name={"jCacheInterceptor"})
    @Role(value=2)
    public JCacheInterceptor cacheInterceptor(JCacheOperationSource jCacheOperationSource) {
        JCacheInterceptor interceptor = new JCacheInterceptor(this.errorHandler);
        interceptor.setCacheOperationSource(jCacheOperationSource);
        return interceptor;
    }
}

