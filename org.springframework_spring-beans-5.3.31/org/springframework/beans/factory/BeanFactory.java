/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.ResolvableType
 *  org.springframework.lang.Nullable
 */
package org.springframework.beans.factory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

public interface BeanFactory {
    public static final String FACTORY_BEAN_PREFIX = "&";

    public Object getBean(String var1) throws BeansException;

    public <T> T getBean(String var1, Class<T> var2) throws BeansException;

    public Object getBean(String var1, Object ... var2) throws BeansException;

    public <T> T getBean(Class<T> var1) throws BeansException;

    public <T> T getBean(Class<T> var1, Object ... var2) throws BeansException;

    public <T> ObjectProvider<T> getBeanProvider(Class<T> var1);

    public <T> ObjectProvider<T> getBeanProvider(ResolvableType var1);

    public boolean containsBean(String var1);

    public boolean isSingleton(String var1) throws NoSuchBeanDefinitionException;

    public boolean isPrototype(String var1) throws NoSuchBeanDefinitionException;

    public boolean isTypeMatch(String var1, ResolvableType var2) throws NoSuchBeanDefinitionException;

    public boolean isTypeMatch(String var1, Class<?> var2) throws NoSuchBeanDefinitionException;

    @Nullable
    public Class<?> getType(String var1) throws NoSuchBeanDefinitionException;

    @Nullable
    public Class<?> getType(String var1, boolean var2) throws NoSuchBeanDefinitionException;

    public String[] getAliases(String var1);
}

