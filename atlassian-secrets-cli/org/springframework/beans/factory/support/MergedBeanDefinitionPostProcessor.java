/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.support;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;

public interface MergedBeanDefinitionPostProcessor
extends BeanPostProcessor {
    public void postProcessMergedBeanDefinition(RootBeanDefinition var1, Class<?> var2, String var3);

    default public void resetBeanDefinition(String beanName) {
    }
}

