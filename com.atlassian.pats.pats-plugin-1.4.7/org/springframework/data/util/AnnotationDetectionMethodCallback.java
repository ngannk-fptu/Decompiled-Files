/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.annotation.AnnotatedElementUtils
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.util.ReflectionUtils$MethodCallback
 */
package org.springframework.data.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

public class AnnotationDetectionMethodCallback<A extends Annotation>
implements ReflectionUtils.MethodCallback {
    private static final String MULTIPLE_FOUND = "Found annotation %s both on %s and %s! Make sure only one of them is annotated with it!";
    private final boolean enforceUniqueness;
    private final Class<A> annotationType;
    @Nullable
    private Method foundMethod;
    @Nullable
    private A annotation;

    public AnnotationDetectionMethodCallback(Class<A> annotationType) {
        this(annotationType, false);
    }

    public AnnotationDetectionMethodCallback(Class<A> annotationType, boolean enforceUniqueness) {
        Assert.notNull(annotationType, (String)"Annotation type must not be null!");
        this.annotationType = annotationType;
        this.enforceUniqueness = enforceUniqueness;
    }

    @Nullable
    public Method getMethod() {
        return this.foundMethod;
    }

    public Method getRequiredMethod() {
        Method method = this.foundMethod;
        if (method == null) {
            throw new IllegalStateException(String.format("No method with annotation %s found!", this.annotationType));
        }
        return method;
    }

    @Nullable
    public A getAnnotation() {
        return this.annotation;
    }

    public boolean hasFoundAnnotation() {
        return this.annotation != null;
    }

    public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
        if (this.foundMethod != null && !this.enforceUniqueness) {
            return;
        }
        Annotation foundAnnotation = AnnotatedElementUtils.findMergedAnnotation((AnnotatedElement)method, this.annotationType);
        if (foundAnnotation != null) {
            if (this.foundMethod != null && this.enforceUniqueness) {
                throw new IllegalStateException(String.format(MULTIPLE_FOUND, foundAnnotation.getClass().getName(), this.foundMethod, method));
            }
            this.annotation = foundAnnotation;
            ReflectionUtils.makeAccessible((Method)method);
            this.foundMethod = method;
        }
    }
}

