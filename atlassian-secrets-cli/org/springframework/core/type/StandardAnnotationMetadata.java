/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.type;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.StandardClassMetadata;
import org.springframework.core.type.StandardMethodMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

public class StandardAnnotationMetadata
extends StandardClassMetadata
implements AnnotationMetadata {
    private final Annotation[] annotations;
    private final boolean nestedAnnotationsAsMap;

    public StandardAnnotationMetadata(Class<?> introspectedClass) {
        this(introspectedClass, false);
    }

    public StandardAnnotationMetadata(Class<?> introspectedClass, boolean nestedAnnotationsAsMap) {
        super(introspectedClass);
        this.annotations = introspectedClass.getAnnotations();
        this.nestedAnnotationsAsMap = nestedAnnotationsAsMap;
    }

    @Override
    public Set<String> getAnnotationTypes() {
        LinkedHashSet<String> types = new LinkedHashSet<String>();
        for (Annotation ann : this.annotations) {
            types.add(ann.annotationType().getName());
        }
        return types;
    }

    @Override
    public Set<String> getMetaAnnotationTypes(String annotationName) {
        return this.annotations.length > 0 ? AnnotatedElementUtils.getMetaAnnotationTypes(this.getIntrospectedClass(), annotationName) : Collections.emptySet();
    }

    @Override
    public boolean hasAnnotation(String annotationName) {
        for (Annotation ann : this.annotations) {
            if (!ann.annotationType().getName().equals(annotationName)) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean hasMetaAnnotation(String annotationName) {
        return this.annotations.length > 0 && AnnotatedElementUtils.hasMetaAnnotationTypes(this.getIntrospectedClass(), annotationName);
    }

    @Override
    public boolean isAnnotated(String annotationName) {
        return this.annotations.length > 0 && AnnotatedElementUtils.isAnnotated(this.getIntrospectedClass(), annotationName);
    }

    @Override
    public Map<String, Object> getAnnotationAttributes(String annotationName) {
        return this.getAnnotationAttributes(annotationName, false);
    }

    @Override
    @Nullable
    public Map<String, Object> getAnnotationAttributes(String annotationName, boolean classValuesAsString) {
        return this.annotations.length > 0 ? AnnotatedElementUtils.getMergedAnnotationAttributes(this.getIntrospectedClass(), annotationName, classValuesAsString, this.nestedAnnotationsAsMap) : null;
    }

    @Override
    @Nullable
    public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName) {
        return this.getAllAnnotationAttributes(annotationName, false);
    }

    @Override
    @Nullable
    public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName, boolean classValuesAsString) {
        return this.annotations.length > 0 ? AnnotatedElementUtils.getAllAnnotationAttributes(this.getIntrospectedClass(), annotationName, classValuesAsString, this.nestedAnnotationsAsMap) : null;
    }

    @Override
    public boolean hasAnnotatedMethods(String annotationName) {
        try {
            Method[] methods;
            for (Method method : methods = this.getIntrospectedClass().getDeclaredMethods()) {
                if (method.isBridge() || method.getAnnotations().length <= 0 || !AnnotatedElementUtils.isAnnotated((AnnotatedElement)method, annotationName)) continue;
                return true;
            }
            return false;
        }
        catch (Throwable ex) {
            throw new IllegalStateException("Failed to introspect annotated methods on " + this.getIntrospectedClass(), ex);
        }
    }

    @Override
    public Set<MethodMetadata> getAnnotatedMethods(String annotationName) {
        try {
            Method[] methods = this.getIntrospectedClass().getDeclaredMethods();
            LinkedHashSet<MethodMetadata> annotatedMethods = new LinkedHashSet<MethodMetadata>(4);
            for (Method method : methods) {
                if (method.isBridge() || method.getAnnotations().length <= 0 || !AnnotatedElementUtils.isAnnotated((AnnotatedElement)method, annotationName)) continue;
                annotatedMethods.add(new StandardMethodMetadata(method, this.nestedAnnotationsAsMap));
            }
            return annotatedMethods;
        }
        catch (Throwable ex) {
            throw new IllegalStateException("Failed to introspect annotated methods on " + this.getIntrospectedClass(), ex);
        }
    }
}

