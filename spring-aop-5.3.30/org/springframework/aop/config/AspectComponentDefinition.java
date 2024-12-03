/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.config.BeanDefinition
 *  org.springframework.beans.factory.config.BeanReference
 *  org.springframework.beans.factory.parsing.CompositeComponentDefinition
 *  org.springframework.lang.Nullable
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

    public BeanDefinition[] getBeanDefinitions() {
        return this.beanDefinitions;
    }

    public BeanReference[] getBeanReferences() {
        return this.beanReferences;
    }
}

