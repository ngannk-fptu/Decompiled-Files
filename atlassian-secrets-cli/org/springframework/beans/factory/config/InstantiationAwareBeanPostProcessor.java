/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.config;

import java.beans.PropertyDescriptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.lang.Nullable;

public interface InstantiationAwareBeanPostProcessor
extends BeanPostProcessor {
    @Nullable
    default public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        return null;
    }

    default public boolean postProcessAfterInstantiation(Object bean2, String beanName) throws BeansException {
        return true;
    }

    @Nullable
    default public PropertyValues postProcessProperties(PropertyValues pvs, Object bean2, String beanName) throws BeansException {
        return null;
    }

    @Deprecated
    @Nullable
    default public PropertyValues postProcessPropertyValues(PropertyValues pvs, PropertyDescriptor[] pds, Object bean2, String beanName) throws BeansException {
        return pvs;
    }
}

