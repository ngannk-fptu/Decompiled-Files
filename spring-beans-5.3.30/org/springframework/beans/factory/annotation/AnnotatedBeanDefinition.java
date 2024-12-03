/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.type.AnnotationMetadata
 *  org.springframework.core.type.MethodMetadata
 *  org.springframework.lang.Nullable
 */
package org.springframework.beans.factory.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.lang.Nullable;

public interface AnnotatedBeanDefinition
extends BeanDefinition {
    public AnnotationMetadata getMetadata();

    @Nullable
    public MethodMetadata getFactoryMethodMetadata();
}

