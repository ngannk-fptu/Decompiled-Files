/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.core.Conventions
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StringUtils
 */
package org.springframework.aop.framework.autoproxy;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Conventions;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

public abstract class AutoProxyUtils {
    public static final String PRESERVE_TARGET_CLASS_ATTRIBUTE = Conventions.getQualifiedAttributeName(AutoProxyUtils.class, (String)"preserveTargetClass");
    public static final String ORIGINAL_TARGET_CLASS_ATTRIBUTE = Conventions.getQualifiedAttributeName(AutoProxyUtils.class, (String)"originalTargetClass");

    public static boolean shouldProxyTargetClass(ConfigurableListableBeanFactory beanFactory, @Nullable String beanName) {
        if (beanName != null && beanFactory.containsBeanDefinition(beanName)) {
            BeanDefinition bd = beanFactory.getBeanDefinition(beanName);
            return Boolean.TRUE.equals(bd.getAttribute(PRESERVE_TARGET_CLASS_ATTRIBUTE));
        }
        return false;
    }

    @Nullable
    public static Class<?> determineTargetClass(ConfigurableListableBeanFactory beanFactory, @Nullable String beanName) {
        BeanDefinition bd;
        Class targetClass;
        if (beanName == null) {
            return null;
        }
        if (beanFactory.containsBeanDefinition(beanName) && (targetClass = (Class)(bd = beanFactory.getMergedBeanDefinition(beanName)).getAttribute(ORIGINAL_TARGET_CLASS_ATTRIBUTE)) != null) {
            return targetClass;
        }
        return beanFactory.getType(beanName);
    }

    static void exposeTargetClass(ConfigurableListableBeanFactory beanFactory, @Nullable String beanName, Class<?> targetClass) {
        if (beanName != null && beanFactory.containsBeanDefinition(beanName)) {
            beanFactory.getMergedBeanDefinition(beanName).setAttribute(ORIGINAL_TARGET_CLASS_ATTRIBUTE, targetClass);
        }
    }

    static boolean isOriginalInstance(String beanName, Class<?> beanClass) {
        if (!StringUtils.hasLength((String)beanName) || beanName.length() != beanClass.getName().length() + ".ORIGINAL".length()) {
            return false;
        }
        return beanName.startsWith(beanClass.getName()) && beanName.endsWith(".ORIGINAL");
    }
}

