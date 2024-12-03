/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.aop.Advice
 *  org.springframework.aop.framework.ProxyFactory
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.ListableBeanFactory
 *  org.springframework.dao.support.PersistenceExceptionTranslationInterceptor
 *  org.springframework.util.Assert
 */
package org.springframework.data.repository.core.support;

import org.aopalliance.aop.Advice;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.dao.support.PersistenceExceptionTranslationInterceptor;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.support.RepositoryProxyPostProcessor;
import org.springframework.util.Assert;

public class PersistenceExceptionTranslationRepositoryProxyPostProcessor
implements RepositoryProxyPostProcessor {
    private final PersistenceExceptionTranslationInterceptor interceptor;

    public PersistenceExceptionTranslationRepositoryProxyPostProcessor(ListableBeanFactory beanFactory) {
        Assert.notNull((Object)beanFactory, (String)"BeanFactory must not be null!");
        this.interceptor = new PersistenceExceptionTranslationInterceptor();
        this.interceptor.setBeanFactory((BeanFactory)beanFactory);
        this.interceptor.afterPropertiesSet();
    }

    @Override
    public void postProcess(ProxyFactory factory, RepositoryInformation repositoryInformation) {
        factory.addAdvice((Advice)this.interceptor);
    }
}

