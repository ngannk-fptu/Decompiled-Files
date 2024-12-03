/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.BeanDefinitionStoreException
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.NoSuchBeanDefinitionException
 *  org.springframework.beans.factory.ObjectFactory
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.support.DefaultListableBeanFactory
 */
package com.atlassian.confluence.setup;

import com.atlassian.confluence.cache.spring.BeanNameCacheResult;
import com.atlassian.confluence.cache.spring.BeanNameTypeCache;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

public class ConfluenceListableBeanFactory
extends DefaultListableBeanFactory
implements Serializable {
    private static final long serialVersionUID = 1L;
    private transient BeanNameTypeCache beanNameTypeCache = new BeanNameTypeCache();

    public ConfluenceListableBeanFactory() {
        this.setAllowBeanDefinitionOverriding(false);
    }

    public ConfluenceListableBeanFactory(BeanFactory parentBeanFactory) {
        super(parentBeanFactory);
        this.setAllowBeanDefinitionOverriding(false);
    }

    public String toString() {
        return "toString() in ConfluenceListableBeanFactory overridden for performance reasons";
    }

    public void unregisterBeanDefinition(String beanAlias) {
        this.removeBeanDefinition(beanAlias);
        this.removeSingleton(beanAlias);
        this.flushBeanNamesForTypeCache();
    }

    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException {
        super.registerBeanDefinition(beanName, beanDefinition);
        this.flushBeanNamesForTypeCache();
    }

    public void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        super.removeBeanDefinition(beanName);
        this.flushBeanNamesForTypeCache();
    }

    protected void resetBeanDefinition(String beanName) {
        super.resetBeanDefinition(beanName);
        this.flushBeanNamesForTypeCache();
    }

    protected boolean removeSingletonIfCreatedForTypeCheckOnly(String beanName) {
        boolean result = super.removeSingletonIfCreatedForTypeCheckOnly(beanName);
        if (result) {
            this.flushBeanNamesForTypeCache();
        }
        return result;
    }

    public void registerSingleton(String beanName, Object singletonObject) throws IllegalStateException {
        super.registerSingleton(beanName, singletonObject);
        this.flushBeanNamesForTypeCache();
    }

    public void removeAlias(String alias) {
        super.removeAlias(alias);
        this.flushBeanNamesForTypeCache();
    }

    public void flushBeanNamesForTypeCache() {
        this.beanNameTypeCache.clearCache();
    }

    protected void removeSingleton(String beanName) {
        super.removeSingleton(beanName);
        this.flushBeanNamesForTypeCache();
    }

    protected void addSingletonFactory(String beanName, ObjectFactory singletonFactory) {
        super.addSingletonFactory(beanName, singletonFactory);
        this.flushBeanNamesForTypeCache();
    }

    protected void addSingleton(String beanName, Object singletonObject) {
        super.addSingleton(beanName, singletonObject);
        this.flushBeanNamesForTypeCache();
    }

    public String[] getBeanNamesForType(Class type, boolean includeNonSingletons, boolean allowEagerInit) {
        String[] value;
        if (Boolean.getBoolean("confluence.disableBeanTypeCache")) {
            return super.getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
        }
        BeanNameCacheResult result = this.beanNameTypeCache.get(type, includeNonSingletons, allowEagerInit);
        if (result == null) {
            value = super.getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
            this.beanNameTypeCache.put(type, includeNonSingletons, allowEagerInit, value);
        } else {
            value = result.getResult();
        }
        return value;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.beanNameTypeCache = new BeanNameTypeCache();
    }

    private void readObjectNoData() throws ObjectStreamException {
        this.beanNameTypeCache = new BeanNameTypeCache();
    }
}

