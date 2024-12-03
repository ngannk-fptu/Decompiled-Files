/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.cache.jcache.interceptor;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;
import org.springframework.cache.jcache.interceptor.JCacheOperationSource;
import org.springframework.cache.jcache.interceptor.JCacheOperationSourcePointcut;
import org.springframework.lang.Nullable;

public class BeanFactoryJCacheOperationSourceAdvisor
extends AbstractBeanFactoryPointcutAdvisor {
    @Nullable
    private JCacheOperationSource cacheOperationSource;
    private final JCacheOperationSourcePointcut pointcut = new JCacheOperationSourcePointcut(){

        @Override
        protected JCacheOperationSource getCacheOperationSource() {
            return BeanFactoryJCacheOperationSourceAdvisor.this.cacheOperationSource;
        }
    };

    public void setCacheOperationSource(JCacheOperationSource cacheOperationSource) {
        this.cacheOperationSource = cacheOperationSource;
    }

    public void setClassFilter(ClassFilter classFilter) {
        this.pointcut.setClassFilter(classFilter);
    }

    @Override
    public Pointcut getPointcut() {
        return this.pointcut;
    }
}

