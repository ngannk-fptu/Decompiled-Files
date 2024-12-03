/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

@FunctionalInterface
public interface BeanFactoryPostProcessor {
    public void postProcessBeanFactory(ConfigurableListableBeanFactory var1) throws BeansException;
}

