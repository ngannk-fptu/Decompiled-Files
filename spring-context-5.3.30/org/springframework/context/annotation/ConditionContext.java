/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 *  org.springframework.core.env.Environment
 *  org.springframework.core.io.ResourceLoader
 *  org.springframework.lang.Nullable
 */
package org.springframework.context.annotation;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;

public interface ConditionContext {
    public BeanDefinitionRegistry getRegistry();

    @Nullable
    public ConfigurableListableBeanFactory getBeanFactory();

    public Environment getEnvironment();

    public ResourceLoader getResourceLoader();

    @Nullable
    public ClassLoader getClassLoader();
}

