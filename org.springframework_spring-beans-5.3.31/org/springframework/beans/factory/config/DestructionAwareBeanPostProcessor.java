/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public interface DestructionAwareBeanPostProcessor
extends BeanPostProcessor {
    public void postProcessBeforeDestruction(Object var1, String var2) throws BeansException;

    default public boolean requiresDestruction(Object bean) {
        return true;
    }
}

