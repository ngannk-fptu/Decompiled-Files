/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.ListableBeanFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.dao.support;

import java.lang.reflect.Method;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.dao.support.ChainedPersistenceExceptionTranslator;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

public class PersistenceExceptionTranslationInterceptor
implements MethodInterceptor,
BeanFactoryAware,
InitializingBean {
    @Nullable
    private volatile PersistenceExceptionTranslator persistenceExceptionTranslator;
    private boolean alwaysTranslate = false;
    @Nullable
    private ListableBeanFactory beanFactory;

    public PersistenceExceptionTranslationInterceptor() {
    }

    public PersistenceExceptionTranslationInterceptor(PersistenceExceptionTranslator pet) {
        Assert.notNull((Object)pet, (String)"PersistenceExceptionTranslator must not be null");
        this.persistenceExceptionTranslator = pet;
    }

    public PersistenceExceptionTranslationInterceptor(ListableBeanFactory beanFactory) {
        Assert.notNull((Object)beanFactory, (String)"ListableBeanFactory must not be null");
        this.beanFactory = beanFactory;
    }

    public void setPersistenceExceptionTranslator(PersistenceExceptionTranslator pet) {
        this.persistenceExceptionTranslator = pet;
    }

    public void setAlwaysTranslate(boolean alwaysTranslate) {
        this.alwaysTranslate = alwaysTranslate;
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (this.persistenceExceptionTranslator == null) {
            if (!(beanFactory instanceof ListableBeanFactory)) {
                throw new IllegalArgumentException("Cannot use PersistenceExceptionTranslator autodetection without ListableBeanFactory");
            }
            this.beanFactory = (ListableBeanFactory)beanFactory;
        }
    }

    public void afterPropertiesSet() {
        if (this.persistenceExceptionTranslator == null && this.beanFactory == null) {
            throw new IllegalArgumentException("Property 'persistenceExceptionTranslator' is required");
        }
    }

    @Nullable
    public Object invoke(MethodInvocation mi) throws Throwable {
        try {
            return mi.proceed();
        }
        catch (RuntimeException ex) {
            if (!this.alwaysTranslate && ReflectionUtils.declaresException((Method)mi.getMethod(), ex.getClass())) {
                throw ex;
            }
            PersistenceExceptionTranslator translator = this.persistenceExceptionTranslator;
            if (translator == null) {
                Assert.state((this.beanFactory != null ? 1 : 0) != 0, (String)"Cannot use PersistenceExceptionTranslator autodetection without ListableBeanFactory");
                this.persistenceExceptionTranslator = translator = this.detectPersistenceExceptionTranslators(this.beanFactory);
            }
            throw DataAccessUtils.translateIfNecessary(ex, translator);
        }
    }

    protected PersistenceExceptionTranslator detectPersistenceExceptionTranslators(ListableBeanFactory bf) {
        ChainedPersistenceExceptionTranslator cpet = new ChainedPersistenceExceptionTranslator();
        bf.getBeanProvider(PersistenceExceptionTranslator.class, false).orderedStream().forEach(cpet::addDelegate);
        return cpet;
    }
}

