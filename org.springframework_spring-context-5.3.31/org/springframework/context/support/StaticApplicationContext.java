/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.MutablePropertyValues
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.support.GenericBeanDefinition
 *  org.springframework.lang.Nullable
 */
package org.springframework.context.support;

import java.util.Locale;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.StaticMessageSource;
import org.springframework.lang.Nullable;

public class StaticApplicationContext
extends GenericApplicationContext {
    private final StaticMessageSource staticMessageSource = new StaticMessageSource();

    public StaticApplicationContext() throws BeansException {
        this((ApplicationContext)null);
    }

    public StaticApplicationContext(@Nullable ApplicationContext parent) throws BeansException {
        super(parent);
        this.getBeanFactory().registerSingleton("messageSource", (Object)this.staticMessageSource);
    }

    @Override
    protected void assertBeanFactoryActive() {
    }

    public final StaticMessageSource getStaticMessageSource() {
        return this.staticMessageSource;
    }

    public void registerSingleton(String name, Class<?> clazz) throws BeansException {
        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClass(clazz);
        this.getDefaultListableBeanFactory().registerBeanDefinition(name, (BeanDefinition)bd);
    }

    public void registerSingleton(String name, Class<?> clazz, MutablePropertyValues pvs) throws BeansException {
        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setBeanClass(clazz);
        bd.setPropertyValues(pvs);
        this.getDefaultListableBeanFactory().registerBeanDefinition(name, (BeanDefinition)bd);
    }

    public void registerPrototype(String name, Class<?> clazz) throws BeansException {
        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setScope("prototype");
        bd.setBeanClass(clazz);
        this.getDefaultListableBeanFactory().registerBeanDefinition(name, (BeanDefinition)bd);
    }

    public void registerPrototype(String name, Class<?> clazz, MutablePropertyValues pvs) throws BeansException {
        GenericBeanDefinition bd = new GenericBeanDefinition();
        bd.setScope("prototype");
        bd.setBeanClass(clazz);
        bd.setPropertyValues(pvs);
        this.getDefaultListableBeanFactory().registerBeanDefinition(name, (BeanDefinition)bd);
    }

    public void addMessage(String code, Locale locale, String defaultMessage) {
        this.getStaticMessageSource().addMessage(code, locale, defaultMessage);
    }
}

