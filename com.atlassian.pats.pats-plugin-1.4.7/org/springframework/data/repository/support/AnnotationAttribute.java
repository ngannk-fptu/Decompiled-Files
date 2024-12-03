/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.MethodParameter
 *  org.springframework.core.annotation.AnnotationUtils
 *  org.springframework.util.Assert
 */
package org.springframework.data.repository.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Optional;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

class AnnotationAttribute {
    private final Class<? extends Annotation> annotationType;
    private final Optional<String> attributeName;

    public AnnotationAttribute(Class<? extends Annotation> annotationType) {
        this(annotationType, Optional.empty());
    }

    public AnnotationAttribute(Class<? extends Annotation> annotationType, Optional<String> attributeName) {
        Assert.notNull(annotationType, (String)"Annotation type must not be null");
        Assert.notNull(attributeName, (String)"Attribute name must not be null");
        this.annotationType = annotationType;
        this.attributeName = attributeName;
    }

    public Class<? extends Annotation> getAnnotationType() {
        return this.annotationType;
    }

    public Optional<Object> getValueFrom(MethodParameter parameter) {
        Assert.notNull((Object)parameter, (String)"MethodParameter must not be null!");
        Annotation annotation = parameter.getParameterAnnotation(this.annotationType);
        return Optional.ofNullable(annotation).map(this::getValueFrom);
    }

    public Optional<Object> getValueFrom(AnnotatedElement annotatedElement) {
        Assert.notNull((Object)annotatedElement, (String)"Annotated element must not be null!");
        Annotation annotation = annotatedElement.getAnnotation(this.annotationType);
        return Optional.ofNullable(annotation).map(it -> this.getValueFrom(annotation));
    }

    public Object getValueFrom(Annotation annotation) {
        Assert.notNull((Object)annotation, (String)"Annotation must not be null!");
        return this.attributeName.map(it -> AnnotationUtils.getValue((Annotation)annotation, (String)it)).orElseGet(() -> AnnotationUtils.getValue((Annotation)annotation));
    }
}

