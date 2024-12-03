/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.AnnotatedBeanDefinition
 *  org.springframework.beans.factory.support.GenericBeanDefinition
 *  org.springframework.core.type.AnnotationMetadata
 *  org.springframework.core.type.MethodMetadata
 *  org.springframework.core.type.classreading.MetadataReader
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.context.annotation;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class ScannedGenericBeanDefinition
extends GenericBeanDefinition
implements AnnotatedBeanDefinition {
    private final AnnotationMetadata metadata;

    public ScannedGenericBeanDefinition(MetadataReader metadataReader) {
        Assert.notNull((Object)metadataReader, (String)"MetadataReader must not be null");
        this.metadata = metadataReader.getAnnotationMetadata();
        this.setBeanClassName(this.metadata.getClassName());
        this.setResource(metadataReader.getResource());
    }

    public final AnnotationMetadata getMetadata() {
        return this.metadata;
    }

    @Nullable
    public MethodMetadata getFactoryMethodMetadata() {
        return null;
    }
}

