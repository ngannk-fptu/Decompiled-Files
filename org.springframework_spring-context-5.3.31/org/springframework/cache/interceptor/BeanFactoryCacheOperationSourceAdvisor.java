/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.aop.ClassFilter
 *  org.springframework.aop.Pointcut
 *  org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor
 *  org.springframework.lang.Nullable
 */
package org.springframework.cache.interceptor;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.cache.interceptor.CacheOperationSource;
import org.springframework.cache.interceptor.CacheOperationSourcePointcut;
import org.springframework.lang.Nullable;

public class BeanFactoryCacheOperationSourceAdvisor
extends AbstractBeanFactoryPointcutAdvisor {
    @Nullable
    private CacheOperationSource cacheOperationSource;
    private final CacheOperationSourcePointcut pointcut = new CacheOperationSourcePointcut(){

        @Override
        @Nullable
        protected CacheOperationSource getCacheOperationSource() {
            return BeanFactoryCacheOperationSourceAdvisor.this.cacheOperationSource;
        }
    };

    public void setCacheOperationSource(CacheOperationSource cacheOperationSource) {
        this.cacheOperationSource = cacheOperationSource;
    }

    public void setClassFilter(ClassFilter classFilter) {
        this.pointcut.setClassFilter(classFilter);
    }

    public Pointcut getPointcut() {
        return this.pointcut;
    }
}

