/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.annotation;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.type.AnnotationMetadata;

public interface ImportBeanDefinitionRegistrar {
    public void registerBeanDefinitions(AnnotationMetadata var1, BeanDefinitionRegistry var2);
}

