/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.support;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

public interface BeanNameGenerator {
    public String generateBeanName(BeanDefinition var1, BeanDefinitionRegistry var2);
}

