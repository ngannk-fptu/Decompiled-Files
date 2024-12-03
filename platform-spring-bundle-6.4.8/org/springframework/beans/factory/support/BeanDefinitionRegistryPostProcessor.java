/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

public interface BeanDefinitionRegistryPostProcessor
extends BeanFactoryPostProcessor {
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry var1) throws BeansException;
}

