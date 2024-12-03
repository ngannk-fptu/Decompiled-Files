/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.config;

import org.springframework.beans.factory.config.BeanDefinition;

@FunctionalInterface
public interface BeanDefinitionCustomizer {
    public void customize(BeanDefinition var1);
}

