/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.annotation.AnnotatedElementUtils
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.aop.support.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.support.StaticMethodMatcher;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class AnnotationMethodMatcher
extends StaticMethodMatcher {
    private final Class<? extends Annotation> annotationType;
    private final boolean checkInherited;

    public AnnotationMethodMatcher(Class<? extends Annotation> annotationType) {
        this(annotationType, false);
    }

    public AnnotationMethodMatcher(Class<? extends Annotation> annotationType, boolean checkInherited) {
        Assert.notNull(annotationType, (String)"Annotation type must not be null");
        this.annotationType = annotationType;
        this.checkInherited = checkInherited;
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        if (this.matchesMethod(method)) {
            return true;
        }
        if (Proxy.isProxyClass(targetClass)) {
            return false;
        }
        Method specificMethod = AopUtils.getMostSpecificMethod(method, targetClass);
        return specificMethod != method && this.matchesMethod(specificMethod);
    }

    private boolean matchesMethod(Method method) {
        return this.checkInherited ? AnnotatedElementUtils.hasAnnotation((AnnotatedElement)method, this.annotationType) : method.isAnnotationPresent(this.annotationType);
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AnnotationMethodMatcher)) {
            return false;
        }
        AnnotationMethodMatcher otherMm = (AnnotationMethodMatcher)other;
        return this.annotationType.equals(otherMm.annotationType) && this.checkInherited == otherMm.checkInherited;
    }

    public int hashCode() {
        return this.annotationType.hashCode();
    }

    public String toString() {
        return this.getClass().getName() + ": " + this.annotationType;
    }
}

