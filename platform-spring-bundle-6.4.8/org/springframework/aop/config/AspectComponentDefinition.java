/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanReference;
import org.springframework.beans.factory.parsing.CompositeComponentDefinition;
import org.springframework.lang.Nullable;

public class AspectComponentDefinition
extends CompositeComponentDefinition {
    private final BeanDefinition[] beanDefinitions;
    private final BeanReference[] beanReferences;

    public AspectComponentDefinition(String aspectName, @Nullable BeanDefinition[] beanDefinitions, @Nullable BeanReference[] beanReferences, @Nullable Object source) {
        super(aspectName, source);
        this.beanDefinitions = beanDefinitions != null ? beanDefinitions : new BeanDefinition[]{};
        this.beanReferences = beanReferences != null ? beanReferences : new BeanReference[]{};
    }

    @Override
    public BeanDefinition[] getBeanDefinitions() {
        return this.beanDefinitions;
    }

    @Override
    public BeanReference[] getBeanReferences() {
        return this.beanReferences;
    }
}

