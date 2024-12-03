/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;
import org.springframework.lang.Nullable;

public interface BeanPostProcessor {
    @Nullable
    default public Object postProcessBeforeInitialization(Object bean2, String beanName) throws BeansException {
        return bean2;
    }

    @Nullable
    default public Object postProcessAfterInitialization(Object bean2, String beanName) throws BeansException {
        return bean2;
    }
}

