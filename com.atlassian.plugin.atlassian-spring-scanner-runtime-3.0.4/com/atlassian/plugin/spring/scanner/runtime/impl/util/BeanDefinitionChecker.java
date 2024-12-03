/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 */
package com.atlassian.plugin.spring.scanner.runtime.impl.util;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

public class BeanDefinitionChecker {
    public static boolean needToRegister(String beanName, BeanDefinition beanDefinition, BeanDefinitionRegistry registry) throws IllegalStateException {
        if (!registry.containsBeanDefinition(beanName)) {
            return true;
        }
        BeanDefinition existingDef = registry.getBeanDefinition(beanName);
        BeanDefinition originatingDef = existingDef.getOriginatingBeanDefinition();
        if (originatingDef != null) {
            existingDef = originatingDef;
        }
        if (BeanDefinitionChecker.isCompatible(beanDefinition, existingDef)) {
            return false;
        }
        throw new IllegalStateException("Annotation-specified bean name '" + beanName + "' for bean class [" + beanDefinition.getBeanClassName() + "] conflicts with existing, non-compatible bean definition of same name and class [" + existingDef.getBeanClassName() + "]");
    }

    public static boolean isCompatible(BeanDefinition newDefinition, BeanDefinition existingDefinition) {
        return newDefinition.getBeanClassName().equals(existingDefinition.getBeanClassName()) || newDefinition.equals(existingDefinition);
    }
}

