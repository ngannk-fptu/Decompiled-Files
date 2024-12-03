/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.config;

import java.util.Iterator;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.lang.Nullable;

public interface ConfigurableListableBeanFactory
extends ListableBeanFactory,
AutowireCapableBeanFactory,
ConfigurableBeanFactory {
    public void ignoreDependencyType(Class<?> var1);

    public void ignoreDependencyInterface(Class<?> var1);

    public void registerResolvableDependency(Class<?> var1, @Nullable Object var2);

    public boolean isAutowireCandidate(String var1, DependencyDescriptor var2) throws NoSuchBeanDefinitionException;

    public BeanDefinition getBeanDefinition(String var1) throws NoSuchBeanDefinitionException;

    public Iterator<String> getBeanNamesIterator();

    public void clearMetadataCache();

    public void freezeConfiguration();

    public boolean isConfigurationFrozen();

    public void preInstantiateSingletons() throws BeansException;
}

