/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.aop.scope.ScopedProxyUtils
 *  org.springframework.beans.factory.config.BeanDefinitionHolder
 *  org.springframework.beans.factory.support.BeanDefinitionRegistry
 */
package org.springframework.context.annotation;

import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

final class ScopedProxyCreator {
    private ScopedProxyCreator() {
    }

    public static BeanDefinitionHolder createScopedProxy(BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry, boolean proxyTargetClass) {
        return ScopedProxyUtils.createScopedProxy((BeanDefinitionHolder)definitionHolder, (BeanDefinitionRegistry)registry, (boolean)proxyTargetClass);
    }

    public static String getTargetBeanName(String originalBeanName) {
        return ScopedProxyUtils.getTargetBeanName((String)originalBeanName);
    }
}

