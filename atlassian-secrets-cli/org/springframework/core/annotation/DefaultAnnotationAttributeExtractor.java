/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.springframework.core.annotation.AbstractAliasAwareAnnotationAttributeExtractor;
import org.springframework.lang.Nullable;
import org.springframework.util.ReflectionUtils;

class DefaultAnnotationAttributeExtractor
extends AbstractAliasAwareAnnotationAttributeExtractor<Annotation> {
    DefaultAnnotationAttributeExtractor(Annotation annotation, @Nullable Object annotatedElement) {
        super(annotation.annotationType(), annotatedElement, annotation);
    }

    @Override
    @Nullable
    protected Object getRawAttributeValue(Method attributeMethod) {
        ReflectionUtils.makeAccessible(attributeMethod);
        return ReflectionUtils.invokeMethod(attributeMethod, this.getSource());
    }

    @Override
    @Nullable
    protected Object getRawAttributeValue(String attributeName) {
        Method attributeMethod = ReflectionUtils.findMethod(this.getAnnotationType(), attributeName);
        return attributeMethod != null ? this.getRawAttributeValue(attributeMethod) : null;
    }
}

