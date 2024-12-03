/*
 * Decompiled with CFR 0.152.
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

