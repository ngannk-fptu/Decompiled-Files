/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.type.AnnotationMetadata
 *  org.springframework.core.type.MethodMetadata
 *  org.springframework.core.type.StandardAnnotationMetadata
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.beans.factory.annotation;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class AnnotatedGenericBeanDefinition
extends GenericBeanDefinition
implements AnnotatedBeanDefinition {
    private final AnnotationMetadata metadata;
    @Nullable
    private MethodMetadata factoryMethodMetadata;

    public AnnotatedGenericBeanDefinition(Class<?> beanClass) {
        this.setBeanClass(beanClass);
        this.metadata = AnnotationMetadata.introspect(beanClass);
    }

    public AnnotatedGenericBeanDefinition(AnnotationMetadata metadata) {
        Assert.notNull((Object)metadata, (String)"AnnotationMetadata must not be null");
        if (metadata instanceof StandardAnnotationMetadata) {
            this.setBeanClass(((StandardAnnotationMetadata)metadata).getIntrospectedClass());
        } else {
            this.setBeanClassName(metadata.getClassName());
        }
        this.metadata = metadata;
    }

    public AnnotatedGenericBeanDefinition(AnnotationMetadata metadata, MethodMetadata factoryMethodMetadata) {
        this(metadata);
        Assert.notNull((Object)factoryMethodMetadata, (String)"MethodMetadata must not be null");
        this.setFactoryMethodName(factoryMethodMetadata.getMethodName());
        this.factoryMethodMetadata = factoryMethodMetadata;
    }

    @Override
    public final AnnotationMetadata getMetadata() {
        return this.metadata;
    }

    @Override
    @Nullable
    public final MethodMetadata getFactoryMethodMetadata() {
        return this.factoryMethodMetadata;
    }
}

